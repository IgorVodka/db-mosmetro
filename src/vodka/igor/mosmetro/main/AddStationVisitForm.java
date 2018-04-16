package vodka.igor.mosmetro.main;

import org.hibernate.Session;
import vodka.igor.mosmetro.logic.MetroManager;
import vodka.igor.mosmetro.models.Station;
import vodka.igor.mosmetro.models.Visit;
import vodka.igor.mosmetro.models.tickets.Ticket;
import vodka.igor.mosmetro.ui.ShowableForm;
import vodka.igor.mosmetro.ui.item.StationItem;
import vodka.igor.mosmetro.ui.item.TicketItem;

import javax.swing.*;
import java.sql.Date;

public class AddStationVisitForm extends JFrame implements ShowableForm {
    private JPanel contentPane;

    public AddStationVisitForm() {
        setTitle("Добавить посещение станции");

        setContentPane(contentPane);
        Session session = MetroManager.getInstance().getSession();
        JComboBox stationsComboBox = StationItem.createStationsComboBox(session);
        contentPane.add(stationsComboBox);
        JComboBox ticketsComboBox = TicketItem.createTicketsComboBox(session);
        contentPane.add(ticketsComboBox);
        JButton btn = new JButton("Зарегистрировать");
        contentPane.add(btn);
        btn.addActionListener(event -> {
            Station selectedStation = ((StationItem) stationsComboBox.getSelectedItem()).getStation();
            Ticket selectedTicket = ((TicketItem) ticketsComboBox.getSelectedItem()).getTicket();

            Visit v = new Visit();
            v.setStation(selectedStation);
            v.setDate(new Date(event.getWhen()));
            v.setTicket(selectedTicket);

            session.save(v);
        });
    }

    public void showForm() {
        pack();
        setVisible(true);
    }
}
