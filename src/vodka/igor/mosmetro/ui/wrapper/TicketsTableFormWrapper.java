package vodka.igor.mosmetro.ui.wrapper;

import vodka.igor.mosmetro.listener.DatabaseRowLoadListener;
import vodka.igor.mosmetro.listener.DatabaseRowSaveListener;
import vodka.igor.mosmetro.logic.Validator;
import vodka.igor.mosmetro.main.GenericTableForm;
import vodka.igor.mosmetro.models.Station;
import vodka.igor.mosmetro.models.Visit;
import vodka.igor.mosmetro.models.tickets.BalanceTicket;
import vodka.igor.mosmetro.models.tickets.ExpirableTicket;
import vodka.igor.mosmetro.models.tickets.Ticket;
import vodka.igor.mosmetro.ui.ColorCellRenderer;
import vodka.igor.mosmetro.ui.TableDatabaseBinding;
import vodka.igor.mosmetro.ui.UIUtils;
import vodka.igor.mosmetro.ui.item.IDItem;
import vodka.igor.mosmetro.ui.item.StationItem;
import vodka.igor.mosmetro.ui.item.TicketItem;
import vodka.igor.mosmetro.ui.item.TicketTypeItem;

import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.Instant;

public class TicketsTableFormWrapper extends TableFormWrapper<Ticket> {
    @Override
    public String getName() {
        return "Билеты";
    }

    @Override
    public Query getQuery() {
        return getSession().createQuery("select t from tickets t");
    }

    @Override
    public String[] getHeaders() {
        return new String[]{
                "ID",
                "Тип билета",
                "Дата",
                "Баланс",
        };
    }

    @Override
    public DatabaseRowSaveListener<Ticket> getSaveListener() {
        return (ticket, row) -> {
            Class selectedClass = ((TicketTypeItem) row.get("Тип билета")).getTicketClass();
            if (!ticket.getClass().equals(selectedClass)) {
                ticket = recreateTicketWithCorrectType(ticket, selectedClass);
            }
            if (ticket instanceof BalanceTicket) {
                BalanceTicket bt = (BalanceTicket) ticket;
                bt.setBalance(Validator.getDouble(row.get("Баланс"), bt.getBalance()));
            }
            if (ticket instanceof ExpirableTicket) {
                ExpirableTicket et = (ExpirableTicket) ticket;
                et.setExpirationDate(Validator.getDate(row.get("Дата"), et.getExpirationDate()));
            }
            return ticket;
        };
    }

    private Ticket recreateTicketWithCorrectType(Ticket ticket, Class selectedClass) {
        try {
            getSession().delete(ticket);
            Ticket newTicket = (Ticket) selectedClass.newInstance();
            getSession().persist(newTicket);
            ticket = newTicket;
        } catch (InstantiationException | IllegalAccessException e) {
            UIUtils.error("Не удалось создать новый билет.");
        }
        return ticket;
    }

    @Override
    public DatabaseRowLoadListener<Ticket> getLoadListener() {
        return ticket ->
                new Object[]{
                        new IDItem(ticket.getId()),
                        new TicketTypeItem(ticket.getClass()),
                        ticket instanceof ExpirableTicket && ((ExpirableTicket) ticket).getExpirationDate() != null
                                ? ((ExpirableTicket) ticket).getExpirationDate().toString()
                                : "",
                        ticket instanceof BalanceTicket
                                ? ((BalanceTicket) ticket).getBalance()
                                : null
                };
    }

    @Override
    public void customize(TableDatabaseBinding<Ticket> binding) {
        binding.getModel().overrideColumnClass("Тип билета", TicketTypeItem.class);
        binding.getModel().overrideColumnClass("Баланс", Double.class);

        JComboBox comboBox = TicketTypeItem.createTicketTypesComboBox(getSession());
        binding.getTableControl().getColumn("Тип билета")
                .setCellEditor(new DefaultCellEditor(comboBox));

        binding.getTableControl().getColumn("Дата").setCellRenderer(new ColorCellRenderer() {
            @Override
            public Color getColorForCell(int row, int column) {
                Ticket ticket = binding.getEntityByRow(row);
                if (ticket instanceof ExpirableTicket) {
                    if (((ExpirableTicket) ticket).getExpirationDate() == null)
                        return Color.WHITE;

                    return ((ExpirableTicket) ticket).getExpirationDate().before(Date.from(Instant.now()))
                            ? Color.RED
                            : Color.GREEN;
                }
                return Color.LIGHT_GRAY;
            }
        });

        binding.getTableControl().getColumn("Баланс").setCellRenderer(new ColorCellRenderer() {
            @Override
            public Color getColorForCell(int row, int column) {
                Ticket ticket = binding.getEntityByRow(row);
                if (ticket instanceof BalanceTicket) {
                    return ((BalanceTicket) ticket).getBalance() < 0.0
                            ? Color.RED
                            : Color.GREEN;
                }
                return Color.LIGHT_GRAY;
            }
        });

        getForm().addControlButtonIf("see.visits", "Посещения", actionEvent -> {
            Ticket selectedTicket = binding.getSelectedEntity();

            VisitsTableFormWrapper wrapper = new VisitsTableFormWrapper() {
                @Override
                public Query getQuery() {
                    return getSession().createQuery(
                            "select v from visits v" +
                                    " where v.ticket = :ticket"
                    ).setParameter("ticket", selectedTicket);
                }

                @Override
                public String getName() {
                    return "Посещения билета " + selectedTicket.getId();
                }

                @Override
                public void customize(TableDatabaseBinding<Visit> binding) {
                    super.customize(binding);
                    binding.addFixedColumn("Билет", new TicketItem(selectedTicket));
                }
            };
            new GenericTableForm<>(Visit.class, wrapper).showForm();
        });
    }
}
