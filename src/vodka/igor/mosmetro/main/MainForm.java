package vodka.igor.mosmetro.main;

import vodka.igor.mosmetro.logic.*;
import vodka.igor.mosmetro.ui.*;
import vodka.igor.mosmetro.ui.wrapper.LinesTableFormWrapper;
import vodka.igor.mosmetro.ui.wrapper.StationsTableFormWrapper;
import vodka.igor.mosmetro.ui.wrapper.TableFormWrapper;

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
            String buttonText, Class<? extends TableFormWrapper<T>> wrapper, String permission
    ) {
        AccessGroup group = MetroManager.getInstance().getAccessGroup();
        if (group.can(permission)) {
            JButton btn = new JButton(buttonText);
            menuPanel.add(btn);

            btn.addActionListener((ActionEvent e) -> {
                TableFormWrapper<T> instance;
                try {
                    instance = wrapper.newInstance();
                    GenericTableForm form = new GenericTableForm<>(instance);
                    form.showForm();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            });
        }
    }

    public void showForm() {
        authorizationInfo.setText("Вы авторизованы как " + MetroManager.getInstance().getAccessGroup().getName());

        // надо, чтобы кроме showForm была showForm с дополнительным фильтром как аргумент (мб коллбэк просто)
        // чтобы можно было смотреть линии для станции например
        // addFormButton("Линии", GenericTableForm.class, "see.lines");
        addGenericFormButton("Линии", LinesTableFormWrapper.class, "see.lines");
        addGenericFormButton("Станции", StationsTableFormWrapper.class, "see.stations");
        addFormButton("Поезда", MainForm.class, "see.trains");
        addFormButton("Машинисты", MainForm.class, "see.drivers");
        addFormButton("Перегоны", MainForm.class,  "see.spans");
        addFormButton("Пересадки", MainForm.class, "see.changes");
        addFormButton("Посещения станций", MainForm.class, "misc.station-visits");
        addFormButton("Добавить посещение", MainForm.class, "misc.add-visit");
        addFormButton("Зарегистрировать билет", MainForm.class, "misc.register-ticket");
        addFormButton("Статистика посещений", MainForm.class, "misc.visits-stats");

        validate();
        pack();
        setVisible(true);
    }
}
