package vodka.igor.mosmetro.ui.wrapper;

import vodka.igor.mosmetro.listener.DatabaseRowLoadListener;
import vodka.igor.mosmetro.listener.DatabaseRowSaveListener;
import vodka.igor.mosmetro.models.Station;
import vodka.igor.mosmetro.ui.LineColorCellRenderer;
import vodka.igor.mosmetro.ui.TableDatabaseBinding;

import vodka.igor.mosmetro.models.Line;
import vodka.igor.mosmetro.ui.UIUtils;

import javax.persistence.NoResultException;
import javax.persistence.Query;
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
                "Линия"
        };
    }

    @Override
    public DatabaseRowSaveListener<Station> getSaveListener() {
        return (station, row) -> {
            station.setStationName((String) row.get("Название станции"));

            String newLineName = row.get("Линия").toString();
            Query q = getSession().createQuery(
                    "select l from lines l" +
                            " where l.lineName = :new_line_name")
                    .setParameter("new_line_name", newLineName);
            try {
                Line line = (Line) q.getSingleResult();
                station.setLine(line);
            } catch (NoResultException exc) {
                UIUtils.error("Линии " + newLineName + " не существует!");
            }
        };
    }

    @Override
    public DatabaseRowLoadListener<Station> getLoadListener() {
        return station ->
                new Object[]{
                        station.getId(),
                        station.getStationName(),
                        station.getLine().getLineName()
                };
    }

    @Override
    public void customize(TableDatabaseBinding<Station> binding) {
        binding.getTableControl().getColumn("Линия")
                .setCellRenderer(new LineColorCellRenderer() {
                    @Override
                    public Color getColorForCell(int row, int column) {
                        return binding.getEntityByRow(row).getLine().getColor();
                    }
                });
    }
}
