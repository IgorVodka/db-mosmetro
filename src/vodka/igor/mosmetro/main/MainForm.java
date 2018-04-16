package vodka.igor.mosmetro.main;

import vodka.igor.mosmetro.logic.*;
import vodka.igor.mosmetro.models.*;
import vodka.igor.mosmetro.models.tickets.DefaultTicket;
import vodka.igor.mosmetro.ui.*;
import vodka.igor.mosmetro.ui.wrapper.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainForm extends JFrame implements ShowableForm {
    private JPanel contentPane;
    private JPanel menuPanel;
    private JLabel mainLabel;
    private JLabel authorizationInfo;

    MainForm() {
        setContentPane(contentPane);
        setResizable(false);
        menuPanel.setLayout(new GridLayout(0, 2));
        setTitle("Московский метрополитен");
    }

    private ShowableForm addFormButton(
            String buttonText, Class<? extends ShowableForm> formToOpen
    ) {
        JButton btn = new JButton(buttonText);
        menuPanel.add(btn);

        ShowableForm instance = null;
        try {
            instance = formToOpen.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ShowableForm finalInstance = instance;
        btn.addActionListener((ActionEvent e) -> {
            if(finalInstance == null) {
                UIUtils.error("Класс " + formToOpen.getName() + " не найден!", "Ошибка!");
                return;
            }
            finalInstance.showForm();
        });

        return finalInstance;
    }

    private ShowableForm addFormButton(
            String buttonText, Class<? extends ShowableForm> formToOpen, String permission
    ) {
        AccessGroup group = MetroManager.getInstance().getAccessGroup();
        if (group.can(permission)) {
            return addFormButton(buttonText, formToOpen);
        }
        return null;
    }

    private <T> void addGenericFormButton(
            String buttonText,
            Class<?> entityClass,
            Class<? extends TableFormWrapper<T>> wrapperClass,
            String permission
    ) {
        AccessGroup group = MetroManager.getInstance().getAccessGroup();

        String seePermission = String.format("see.%s", permission);
        String editPermission = String.format("edit.%s", permission);

        if (group.can(seePermission)) {
            JButton btn = new JButton(buttonText);
            menuPanel.add(btn);

            btn.addActionListener((ActionEvent e) -> {
                TableFormWrapper<T> instance;
                try {
                    instance = wrapperClass.newInstance();
                    GenericTableForm form = new GenericTableForm<>(entityClass, instance);
                    form.showForm();
                    if (!group.can(editPermission)) {
                        form.disableEditFeatures();
                    }
                } catch (Exception exc) {
                    UIUtils.error(exc.getMessage(), "Ошибка БД!");
                    exc.printStackTrace();
                }
            });
        }
    }

    public void showForm() {
        authorizationInfo.setText("Вы авторизованы как " + MetroManager.getInstance().getAccessGroup().getName());

        addGenericFormButton("Линии", Line.class, LinesTableFormWrapper.class, "lines");
        addGenericFormButton("Станции", Station.class, StationsTableFormWrapper.class, "stations");
        addGenericFormButton("Поезда", Train.class, TrainsTableFormWrapper.class, "trains");
        addGenericFormButton("Машинисты", Driver.class, DriversTableFormWrapper.class, "drivers");
        addGenericFormButton("Перегоны", Span.class, SpansTableFormWrapper.class, "spans");
        addGenericFormButton("Пересадки", Change.class, ChangesTableFormWrapper.class, "changes");
        addGenericFormButton("Посещения станций", Visit.class, VisitsTableFormWrapper.class, "visits");
        addGenericFormButton("Билеты", DefaultTicket.class, TicketsTableFormWrapper.class, "tickets");
        addFormButton("Добавить посещение", AddStationVisitForm.class, "misc.add-visit");
        addFormButton("Зарегистрировать билет", RegisterTicketForm.class, "misc.register-ticket");
        addFormButton("Статистика посещений", StatsForm.class, "misc.visits-stats");
        addFormButton("О программе", AboutDialog.class);

        validate();
        pack();
        setVisible(true);
    }
}
