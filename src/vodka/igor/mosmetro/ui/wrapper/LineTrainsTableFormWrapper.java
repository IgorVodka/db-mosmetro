package vodka.igor.mosmetro.ui.wrapper;

import vodka.igor.mosmetro.listener.DatabaseRowLoadListener;
import vodka.igor.mosmetro.listener.DatabaseRowSaveListener;
import vodka.igor.mosmetro.models.LineTrain;
import vodka.igor.mosmetro.models.Line;
import vodka.igor.mosmetro.ui.ColorCellRenderer;
import vodka.igor.mosmetro.ui.TableDatabaseBinding;
import vodka.igor.mosmetro.ui.item.IDItem;
import vodka.igor.mosmetro.ui.item.LineItem;
import vodka.igor.mosmetro.ui.item.TrainItem;

import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;

public class LineTrainsTableFormWrapper extends TableFormWrapper<LineTrain> {

    @Override
    public String getName() {
        return "Линии поезда";
    }

    @Override
    public Query getQuery() {
        return null;
    }

    @Override
    public String[] getHeaders() {
        return new String[]{
                "ID",
                "Линия",
                "Поезд"
        };
    }

    @Override
    public DatabaseRowSaveListener<LineTrain> getSaveListener() {
        return (lineTrain, row) -> {
            lineTrain.setLine(((LineItem) row.get("Линия")).getLine());
            lineTrain.setTrain(((TrainItem) row.get("Поезд")).getTrain());
            return lineTrain;
        };
    }

    @Override
    public DatabaseRowLoadListener<LineTrain> getLoadListener() {
        return lineTrain ->
                new Object[]{
                        new IDItem(lineTrain.getId()),
                        new LineItem(lineTrain.getLine()),
                        new TrainItem(lineTrain.getTrain())
                };
    }

    @Override
    public void customize(TableDatabaseBinding<LineTrain> binding) {
        binding.getModel().overrideColumnClass("Линия", LineItem.class);
        binding.getModel().overrideColumnClass("Поезд", TrainItem.class);

        JComboBox linesComboBox = LineItem.createLinesComboBox(getSession());
        binding.getTableControl().getColumn("Линия")
                .setCellEditor(new DefaultCellEditor(linesComboBox));

        binding.getTableControl().getColumn("Линия")
                .setCellRenderer(new ColorCellRenderer() {
                    @Override
                    public Color getColorForCell(int row, int column) {
                        Line line = binding.getEntityByRow(row).getLine();
                        return line == null ? Color.WHITE : line.getColor();
                    }
                });

        JComboBox trainsComboBox = TrainItem.createTrainsComboBox(getSession());
        binding.getTableControl().getColumn("Поезд")
                .setCellEditor(new DefaultCellEditor(trainsComboBox));
    }
}
