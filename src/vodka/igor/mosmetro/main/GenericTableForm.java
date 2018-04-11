package vodka.igor.mosmetro.main;

import vodka.igor.mosmetro.logic.CustomTableModel;
import vodka.igor.mosmetro.logic.MetroManager;
import vodka.igor.mosmetro.ui.*;
import vodka.igor.mosmetro.ui.wrapper.TableFormWrapper;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import java.awt.*;
import java.awt.event.ActionListener;

public class GenericTableForm<T> extends JFrame implements ShowableForm {
    private JPanel contentPane;
    private JTable entitiesTable;
    private JToolBar controls;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton addButton;
    private JButton refreshButton;
    private JTextField searchField;
    private JLabel searchLabel;

    private List<JButton> controlButtons;

    public void disableEditFeatures() {
        saveButton.setVisible(false);
        addButton.setVisible(false);
        deleteButton.setVisible(false);
        ((CustomTableModel) entitiesTable.getModel()).disableEdit();
    }

    public GenericTableForm(Class<?> entityClass, TableFormWrapper<T> wrapper) {
        setContentPane(contentPane);
        setResizable(false);

        controlButtons = new ArrayList<>();

        setTitle(wrapper.getName());

        wrapper.setForm(this);

        TableDatabaseBinding<T> binding = new TableDatabaseBinding<>(
                entityClass,
                wrapper.getQuery(),
                entitiesTable
        );

        binding.setHeaders(wrapper.getHeaders());

        binding.setSaveListener(wrapper.getSaveListener());
        binding.setLoadListener(wrapper.getLoadListener());

        saveButton.addActionListener(actionEvent -> binding.saveAll());
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(actionEvent -> binding.markRowAsDeleted(entitiesTable.getSelectedRow()));
        addButton.addActionListener(actionEvent -> binding.addEmptyEntity(entitiesTable.getSelectedRow()));
        refreshButton.addActionListener(actionEvent -> {
            controlButtons.forEach(button -> controls.remove(button));
            controlButtons.clear();
            deleteButton.setEnabled(false);
            wrapper.customize(binding);
            binding.loadAll();
        });
        enableButtonsOnSelection();

        searchField.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        handle();
                    }

                    public void removeUpdate(DocumentEvent e) {
                        handle();
                    }

                    public void insertUpdate(DocumentEvent e) {
                        handle();
                    }

                    private void handle() {
                        binding.updateFilter(searchField.getText());
                    }
                }
        );

        wrapper.customize(binding);
        binding.loadAll();
    }

    private void enableButtonsOnSelection() {
        entitiesTable.getSelectionModel().addListSelectionListener(
                actionEvent -> {
                    if (entitiesTable.getSelectedRowCount() == 0)
                        return;

                    controlButtons.forEach(item -> item.setEnabled(true));
                    deleteButton.setEnabled(true);
                }
        );
    }

    public void addControlButtonIf(String permission, String text, ActionListener onClick) {
        if (MetroManager.getInstance().getAccessGroup().can(permission)) {
            addControlButton(text, onClick);
        }
    }

    public void addControlButton(String text, ActionListener onClick) {
        JButton btn = new JButton(text);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD));
        btn.setEnabled(false);
        btn.addActionListener(onClick);
        controls.add(btn);
        controlButtons.add(btn);
    }

    public void showForm() {
        validate();
        pack();
        setVisible(true);
    }
}
