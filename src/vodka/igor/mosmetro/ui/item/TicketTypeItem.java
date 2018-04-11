package vodka.igor.mosmetro.ui.item;

import org.hibernate.Session;
import vodka.igor.mosmetro.models.tickets.DefaultTicket;
import vodka.igor.mosmetro.models.tickets.SocialTicket;
import vodka.igor.mosmetro.models.tickets.TroikaTicket;

import javax.swing.*;

public class TicketTypeItem implements Comparable<TicketTypeItem> {
    private Class ticketType;

    public TicketTypeItem(Class ticketType) {
        this.ticketType = ticketType;
    }

    public Class getTicketClass() {
        return ticketType;
    }

    @Override
    public String toString() {
        try {
            Object ticketTypeName = ticketType.getDeclaredMethod("getTicketTypeName")
                    .invoke(ticketType.newInstance());
            return ticketType == null
                    ? ""
                    : String.valueOf(ticketTypeName);
        } catch (Exception e) {
            return "error";
        }
    }

    @Override
    public boolean equals(Object that) {
        if (this.getTicketClass() == null)
            return ((TicketTypeItem) that).getTicketClass() == null;

        if (that instanceof TicketTypeItem) {
            return this.getTicketClass().equals(((TicketTypeItem) that).getTicketClass());
        } else {
            return this == that;
        }
    }

    public static JComboBox createTicketTypesComboBox(Session session) {
        JComboBox comboBox = new JComboBox();
        comboBox.addItem(new TicketTypeItem(DefaultTicket.class));
        comboBox.addItem(new TicketTypeItem(TroikaTicket.class));
        comboBox.addItem(new TicketTypeItem(SocialTicket.class));
        return comboBox;
    }

    @Override
    public int compareTo(TicketTypeItem that) {
        if (this.getTicketClass() == null)
            return 1;
        return this.getTicketClass().getSimpleName().compareTo(that.getTicketClass().getSimpleName());
    }
}
