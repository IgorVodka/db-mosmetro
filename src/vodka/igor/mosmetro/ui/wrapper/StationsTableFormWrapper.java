package vodka.igor.mosmetro.ui.wrapper;

import vodka.igor.mosmetro.listener.DatabaseRowLoadListener;
import vodka.igor.mosmetro.listener.DatabaseRowSaveListener;
import vodka.igor.mosmetro.logic.Validator;
import vodka.igor.mosmetro.main.GenericTableForm;
import vodka.igor.mosmetro.models.*;
import vodka.igor.mosmetro.ui.ColorCellRenderer;
import vodka.igor.mosmetro.ui.TableDatabaseBinding;

import vodka.igor.mosmetro.ui.item.IDItem;
import vodka.igor.mosmetro.ui.item.LineItem;
import vodka.igor.mosmetro.ui.item.StationItem;

import javax.persistence.Query;
import javax.persistence.Table;
import javax.swing.*;
import java.awt.*;

public class StationsTableFormWrapper extends TableFormWrapper<Station> {
    @Override
    public String getName() {
        return "Станции";
    }

    @Override
    public Query getQuery() {
        return getSession().createQuery("select s from stations s");
    }

    @Override
    public String[] getHeaders() {
        return new String[]{
                "ID",
                "Название станции",
                "Линия",
                "Конечная"
        };
    }

    @Override
    public DatabaseRowSaveListener<Station> getSaveListener() {
        return (station, row) -> {
            station.setStationName(Validator.getString(row.get("Название станции"), station.getStationName()));
            station.setLine(((LineItem) row.get("Линия")).getLine());
            return station;
        };
    }

    @Override
    public DatabaseRowLoadListener<Station> getLoadListener() {
        return station ->
                new Object[]{
                        new IDItem(station.getId()),
                        station.getStationName(),
                        new LineItem(station.getLine()),
                        station.getSpans().size() < 2
                };
    }

    @Override
    public void customize(TableDatabaseBinding<Station> binding) {
        binding.getModel().overrideColumnClass("Линия", LineItem.class);
        binding.getModel().overrideColumnClass("Конечная", Boolean.class);

        binding.addFixedColumn("Конечная", true);

        binding.getTableControl().getColumn("Название станции")
                .setCellRenderer(new ColorCellRenderer() {
                    @Override
                    public Color getColorForCell(int row, int column) {
                        Station station = binding.getEntityByRow(row);
                        Line line = station.getLine();
                        return line == null ? Color.WHITE : line.getColor();
                    }
                });

        JComboBox comboBox = LineItem.createLinesComboBox(getSession());
        binding.getTableControl().getColumn("Линия")
                .setCellEditor(new DefaultCellEditor(comboBox));

        getForm().addControlButtonIf("see.spans", "Перегоны", actionEvent -> {
            Station selectedStation = binding.getSelectedEntity();

            SpansTableFormWrapper wrapper = new SpansTableFormWrapper() {
                @Override
                public Query getQuery() {
                    return getSession().createQuery(
                            "select s from spans s" +
                                    " where s.station1 = :station or s.station2 = :station"
                    ).setParameter("station", selectedStation);
                }

                @Override
                public String getName() {
                    return "Перегоны для станции " + selectedStation.getStationName();
                }
            };
            new GenericTableForm<>(Span.class, wrapper).showForm();
        });
        getForm().addControlButtonIf("see.changes", "Пересадки", actionEvent -> {
            Station selectedStation = binding.getSelectedEntity();

            ChangesTableFormWrapper wrapper = new ChangesTableFormWrapper() {
                @Override
                public Query getQuery() {
                    return getSession().createQuery(
                            "select c from changes c" +
                                    " where c.station1 = :station or c.station2 = :station"
                    ).setParameter("station", selectedStation);
                }

                @Override
                public String getName() {
                    return "Пересадки для станции " + selectedStation.getStationName();
                }
            };
            new GenericTableForm<>(Change.class, wrapper).showForm();
        });
        getForm().addControlButtonIf("see.visits", "Посещения", actionEvent -> {
            Station selectedStation = binding.getSelectedEntity();

            VisitsTableFormWrapper wrapper = new VisitsTableFormWrapper() {
                @Override
                public Query getQuery() {
                    return getSession().createQuery(
                            "select v from visits v" +
                                    " where v.station = :station"
                    ).setParameter("station", selectedStation);
                }

                @Override
                public String getName() {
                    return "Посещения для станции " + selectedStation.getStationName();
                }

                @Override
                public void customize(TableDatabaseBinding<Visit> binding) {
                    super.customize(binding);
                    binding.addFixedColumn("Станция", new StationItem(selectedStation));
                }
            };
            new GenericTableForm<>(Visit.class, wrapper).showForm();
        });
    }
}
