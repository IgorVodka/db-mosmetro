package vodka.igor.mosmetro.ui.wrapper;

import vodka.igor.mosmetro.listener.DatabaseRowLoadListener;
import vodka.igor.mosmetro.listener.DatabaseRowSaveListener;
import vodka.igor.mosmetro.logic.Validator;
import vodka.igor.mosmetro.main.GenericTableForm;
import vodka.igor.mosmetro.models.Line;
import vodka.igor.mosmetro.models.LineTrain;
import vodka.igor.mosmetro.models.Station;
import vodka.igor.mosmetro.ui.ColorEditor;
import vodka.igor.mosmetro.ui.ColorCellRenderer;
import vodka.igor.mosmetro.ui.TableDatabaseBinding;
import vodka.igor.mosmetro.ui.UIUtils;
import vodka.igor.mosmetro.ui.item.IDItem;
import vodka.igor.mosmetro.ui.item.LineItem;

import javax.persistence.Query;
import java.awt.*;

public class LinesTableFormWrapper extends TableFormWrapper<Line> {
    @Override
    public String getName() {
        return "Линии";
    }

    @Override
    public Query getQuery() {
        return getSession().createQuery("select l from lines l");
    }

    @Override
    public String[] getHeaders() {
        return new String[]{
                "ID",
                "Метка",
                "Цвет",
                "Название линии",
        };
    }

    @Override
    public DatabaseRowSaveListener<Line> getSaveListener() {
        return (line, row) -> {
            line.setTag(Validator.getString(row.get("Метка"), line.getTag()));
            line.setColor(UIUtils.hex2Color((String) row.get("Цвет")));
            line.setLineName(Validator.getString(row.get("Название линии"), line.getLineName()));
            return line;
        };
    }

    @Override
    public DatabaseRowLoadListener<Line> getLoadListener() {
        return line -> {
            Color color = line.getColor();
            return new Object[]{
                    new IDItem(line.getId()),
                    line.getTag(),
                    UIUtils.color2Hex(line.getColor()),
                    line.getLineName()
            };
        };
    }

    @Override
    public void customize(TableDatabaseBinding<Line> binding) {
        binding.getTableControl().getColumn("Цвет")
                .setCellRenderer(new ColorCellRenderer() {
                    @Override
                    public Color getColorForCell(int row, int column) {
                        return binding.getEntityByRow(row).getColor();
                    }
                });

        binding.getTableControl().getColumn("Цвет")
                .setCellEditor(new ColorEditor());

        getForm().addControlButtonIf("see.stations", "Станции", actionEvent -> {
            Line selectedLine = binding.getSelectedEntity();

            StationsTableFormWrapper wrapper = new StationsTableFormWrapper() {
                @Override
                public Query getQuery() {
                    return getSession().createQuery(
                            "select s from stations s" +
                                    " where s.line = :line"
                    ).setParameter("line", selectedLine);
                }

                @Override
                public String getName() {
                    return "Станции на линии " + selectedLine.getLineName();
                }

                @Override
                public void customize(TableDatabaseBinding<Station> binding) {
                    super.customize(binding);
                    binding.addFixedColumn("Линия", new LineItem(selectedLine));
                }
            };
            new GenericTableForm<>(Station.class, wrapper).showForm();
        });

        getForm().addControlButtonIf("see.trains", "Поезда", actionEvent -> {
            Line selectedLine = binding.getSelectedEntity();

            LineTrainsTableFormWrapper wrapper = new LineTrainsTableFormWrapper() {
                @Override
                public Query getQuery() {
                    return getSession().createQuery(
                            "select lt from line_trains lt" +
                                    " where lt.line = :line"
                    ).setParameter("line", selectedLine);
                }

                @Override
                public String getName() {
                    return "Поезда линии " + selectedLine.getLineName();
                }

                @Override
                public void customize(TableDatabaseBinding<LineTrain> binding) {
                    super.customize(binding);
                    binding.addFixedColumn("Линия", new LineItem(selectedLine));
                }
            };
            new GenericTableForm<>(LineTrain.class, wrapper).showForm();
        });
    }
}
