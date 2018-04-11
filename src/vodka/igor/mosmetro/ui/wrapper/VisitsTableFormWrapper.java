package vodka.igor.mosmetro.ui.wrapper;

import vodka.igor.mosmetro.listener.DatabaseRowLoadListener;
import vodka.igor.mosmetro.listener.DatabaseRowSaveListener;
import vodka.igor.mosmetro.logic.Validator;
import vodka.igor.mosmetro.models.*;
import vodka.igor.mosmetro.ui.ColorCellRenderer;
import vodka.igor.mosmetro.ui.TableDatabaseBinding;
import vodka.igor.mosmetro.ui.item.IDItem;
import vodka.igor.mosmetro.ui.item.StationItem;
import vodka.igor.mosmetro.ui.item.TicketItem;

import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class VisitsTableFormWrapper extends TableFormWrapper<Visit> {
    @Override
    public String getName() {
        return "Посещения станций";
    }

    @Override
    public Query getQuery() {
        return getSession().createQuery("select v from visits v");
    }

    @Override
    public String[] getHeaders() {
        return new String[]{
                "ID",
                "Станция",
                "Билет",
                "Дата"
        };
    }

    @Override
    public DatabaseRowSaveListener<Visit> getSaveListener() {
        return (visit, row) -> {
            visit.setStation(((StationItem) row.get("Станция")).getStation());
            visit.setTicket(((TicketItem) row.get("Билет")).getTicket());
            visit.setDate(Validator.getDate(row.get("Дата"), visit.getDate()));
            return visit;
        };
    }

    @Override
    public DatabaseRowLoadListener<Visit> getLoadListener() {
        return visit ->
                new Object[]{
                        new IDItem(visit.getId()),
                        new StationItem(visit.getStation()),
                        new TicketItem(visit.getTicket()),
                        visit.getDate() == null ? "" : visit.getDate().toString()
                };
    }

    @Override
    public void customize(TableDatabaseBinding<Visit> binding) {
        binding.getModel().overrideColumnClass("Станция", StationItem.class);
        binding.getModel().overrideColumnClass("Билет", TicketItem.class);

        JComboBox stationsComboBox = StationItem.createStationsComboBox(getSession());
        binding.getTableControl().getColumn("Станция")
                .setCellEditor(new DefaultCellEditor(stationsComboBox));

        JComboBox ticketsComboBox = TicketItem.createTicketsComboBox(getSession());
        binding.getTableControl().getColumn("Билет")
                .setCellEditor(new DefaultCellEditor(ticketsComboBox));


        binding.getTableControl().getColumn("Станция")
                .setCellRenderer(new ColorCellRenderer() {
                    @Override
                    public Color getColorForCell(int row, int column) {
                        Station station = binding.getEntityByRow(row).getStation();
                        if (station == null)
                            return Color.WHITE;
                        Line line = station.getLine();
                        return line == null ? Color.WHITE : line.getColor();
                    }
                });
    }
}
