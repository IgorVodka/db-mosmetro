package vodka.igor.mosmetro.main;

import org.hibernate.Session;
import vodka.igor.mosmetro.logic.MetroManager;
import vodka.igor.mosmetro.logic.Validator;
import vodka.igor.mosmetro.models.tickets.*;
import vodka.igor.mosmetro.ui.ShowableForm;
import vodka.igor.mosmetro.ui.UIUtils;
import vodka.igor.mosmetro.ui.item.TicketTypeItem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;

public class RegisterTicketForm extends JFrame implements ShowableForm {
    private JPanel contentPane;
    private JComboBox ticketTypeComboBox;
    private JSlider balanceSlider;
    private JLabel ticketTypeLabel;
    private JLabel dateLabel;
    private JLabel balanceLabel;
    private JButton registerTicketButton;
    private JTextField dateField;
    private JLabel balanceResultLabel;

    public RegisterTicketForm() {
        setTitle("Зарегистрировать билет");
        setContentPane(contentPane);

        Session session = MetroManager.getInstance().getSession();
        JComboBox source = TicketTypeItem.createTicketTypesComboBox(session);
        for (int i = 0; i < source.getItemCount(); i++) {
            ticketTypeComboBox.addItem(source.getItemAt(i));
        }

        registerTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                TicketTypeItem type = ((TicketTypeItem) ticketTypeComboBox.getSelectedItem());
                if (type.getTicketClass().equals(TroikaTicket.class)) {
                    TroikaTicket ticket = new TroikaTicket();
                    setTicketBalance((BalanceTicket) ticket);
                    setTicketExpirationDate(((ExpirableTicket) ticket));
                    session.save(ticket);
                } else if (type.getTicketClass().equals(SocialTicket.class)) {
                    SocialTicket ticket = new SocialTicket();
                    setTicketExpirationDate(((ExpirableTicket) ticket));
                    session.save(ticket);
                } else {
                    DefaultTicket ticket = new DefaultTicket();
                    session.save(ticket);
                }
            }
        });
    }

    private void setTicketBalance(BalanceTicket bt) {
        bt.setBalance((double) balanceSlider.getValue());
    }

    private void setTicketExpirationDate(ExpirableTicket et) {
        Date date = Validator.getDate(dateField.getText(), Date.valueOf(LocalDate.now()));
        et.setExpirationDate(date);
    }

    public void showForm() {
        pack();
        setVisible(true);
    }
}
