package vodka.igor.mosmetro.ui;

import vodka.igor.mosmetro.listener.DatabaseRowLoadListener;
import vodka.igor.mosmetro.listener.DatabaseRowSaveListener;
import vodka.igor.mosmetro.main.GenericTableForm;
import vodka.igor.mosmetro.models.Line;
import vodka.igor.mosmetro.models.Station;

import javax.persistence.Query;
import javax.swing.*;
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
        return new String[] {
                "ID",
                "Метка",
                "Цвет",
                "Название линии",
        };
    }

    @Override
    public DatabaseRowSaveListener<Line> getSaveListener() {
        return (entityBound, row) -> {
            entityBound.setLineName((String) row.get("Название линии"));
        };
    }

    @Override
    public DatabaseRowLoadListener<Line> getLoadListener() {
        return line -> {
            Color color = line.getColor();
            return new Object[] {
                    line.getId(),
                    line.getTag(),
                    String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()),
                    line.getLineName()
            };
        };
    }

    @Override
    public void customize(TableDatabaseBinding<Line> binding) {
        binding.getTableControl().getColumn("Цвет")
                .setCellRenderer(new LineColorCellRenderer() {
                    @Override
                    public Color getColorForCell(int row, int column) {
                        return binding.getEntityByRow(row).getColor();
                    }
                });

        getForm().addControlButton("Stations", actionEvent -> {
            Line selectedLine = binding.getSelectedEntity();

            StationsTableFormWrapper wrapper = new StationsTableFormWrapper() {
                @Override
                public Query getQuery() {
                    return getSession().createQuery(
                            "select s from stations s" +
                            " where s.line = :line_id"
                    ).setParameter("line_id", selectedLine);
                }

                @Override
                public String getName() {
                    return "Станции на линии " + selectedLine.getLineName();
                }
            };
            new GenericTableForm<>(wrapper).showForm();
        });
    }
}
