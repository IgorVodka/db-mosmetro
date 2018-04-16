package vodka.igor.mosmetro.main;

import vodka.igor.mosmetro.logic.AccessGroup;
import vodka.igor.mosmetro.logic.GroupNotFoundException;
import vodka.igor.mosmetro.ui.UIUtils;

import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;

public class LoginForm extends JDialog {
    private JPanel contentPane;
    private JButton buttonLogin;
    private JComboBox usernameBox;
    private JPasswordField passwordField;
    private JLabel usernameLabel;
    private JLabel passwordLabel;

    private AccessGroup accessGroup = null;

    LoginForm() {
        setTitle("Авторизация");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonLogin);

        buttonLogin.addActionListener(e -> onLogin());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(
                e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );
    }

    private void onLogin() {
        try {
            accessGroup = new AccessGroup((String) usernameBox.getSelectedItem());
            char[] password = passwordField.getPassword();
            boolean passwordCorrect = accessGroup.isCorrectPassword(String.valueOf(password));
            if (passwordCorrect) {
                dispose();
            } else {
                UIUtils.error("Пароль введён неверно!","Ошибка авторизации");
            }
            Arrays.fill(password, '\0');
        } catch (GroupNotFoundException e) {
            UIUtils.error("Группа не найдена!", "Ошибка авторизации");
        } catch (Exception e) {
            UIUtils.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public AccessGroup showDialog() {
        pack();
        setVisible(true);
        return accessGroup;
    }
}
