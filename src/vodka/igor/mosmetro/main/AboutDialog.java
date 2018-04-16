package vodka.igor.mosmetro.main;

import vodka.igor.mosmetro.ui.ShowableForm;
import vodka.igor.mosmetro.ui.UIUtils;

import javax.swing.*;
import java.awt.event.*;

public class AboutDialog extends JDialog implements ShowableForm {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    public AboutDialog() {
        setTitle("О программе");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        UIUtils.error("sas");
        dispose();
    }

    public void showForm() {
        pack();
        setVisible(true);
    }
}
