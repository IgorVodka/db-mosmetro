package vodka.igor.mosmetro.ui;

import vodka.igor.mosmetro.listener.DatabaseRowLoadListener;
import vodka.igor.mosmetro.listener.DatabaseRowSaveListener;
import vodka.igor.mosmetro.models.Station;

import javax.persistence.Query;
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
        return new String[] {
                "ID",
                "Название станции",
                "Линия"
        };
    }

    @Override
    public DatabaseRowSaveListener<Station> getSaveListener() {
        return (station, row) -> {
            station.setStationName((String) row.get("Название станции"));
            // setLine (use session to find most appropriate line)
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
