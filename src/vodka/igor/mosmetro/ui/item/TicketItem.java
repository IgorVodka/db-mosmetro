package vodka.igor.mosmetro.ui.item;

import org.hibernate.Session;
import vodka.igor.mosmetro.models.Train;
import vodka.igor.mosmetro.models.tickets.Ticket;

import javax.swing.*;
import java.util.List;

public class TicketItem implements Comparable<TicketItem> {
    private Ticket ticket;

    public TicketItem(Ticket ticket) {
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return ticket;
    }

    @Override
    public String toString() {
        return ticket == null ? "" : ("#" + ticket.getId() + ": " + ticket.toString());
    }

    @Override
    public boolean equals(Object that) {
        if (this.getTicket() == null)
            return ((TicketItem) that).getTicket() == null;

        if (that instanceof TicketItem) {
            return this.getTicket().equals(((TicketItem) that).getTicket());
        } else {
            return this == that;
        }
    }

    public static JComboBox createTicketsComboBox(Session session) {
        JComboBox comboBox = new JComboBox();
        List tickets = session.createQuery("select t from tickets t").getResultList();
        comboBox.addItem(new TicketItem(null));
        for (Ticket ticket : (List<Ticket>) tickets) {
            TicketItem item = new TicketItem(ticket);
            comboBox.addItem(item);
        }
        return comboBox;
    }

    @Override
    public int compareTo(TicketItem that) {
        if (this.getTicket() == null)
            return 1;
        return this.getTicket().getId().compareTo(that.getTicket().getId());
    }
}
