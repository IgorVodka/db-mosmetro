package vodka.igor.mosmetro.main;

import vodka.igor.mosmetro.ui.*;
import vodka.igor.mosmetro.models.Line;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GenericTableForm<T> extends JFrame implements ShowableForm {
    private JPanel contentPane;
    private JTable linesTable;
    private JToolBar controls;
    private JButton saveButton;
    private JButton deleteButton;

    public GenericTableForm(TableFormWrapper<T> wrapper) {
        setContentPane(contentPane);
        setResizable(false);

        setTitle(wrapper.getName());

        wrapper.setForm(this);

        TableDatabaseBinding<T> binding = new TableDatabaseBinding<>(
                wrapper.getQuery(),
                linesTable
        );

        binding.setHeaders(wrapper.getHeaders());

        binding.setSaveListener(wrapper.getSaveListener());
        binding.setLoadListener(wrapper.getLoadListener());

        wrapper.customize(binding);

        saveButton.addActionListener(actionEvent -> {
            binding.saveAll();
        });

        deleteButton.addActionListener(actionEvent -> {
            binding.markRowAsDeleted(linesTable.getSelectedRow());
        });

        binding.loadAll();
    }

    public void addControlButton(String text, ActionListener onClick) {
        JButton btn = new JButton(text);
        controls.add(btn);
        btn.addActionListener(onClick);
    }

    public void showForm() {
        validate();
        pack();
        setVisible(true);
    }
}
