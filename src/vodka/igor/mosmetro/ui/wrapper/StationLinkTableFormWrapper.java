package vodka.igor.mosmetro.ui.wrapper;

import vodka.igor.mosmetro.listener.DatabaseRowLoadListener;
import vodka.igor.mosmetro.listener.DatabaseRowSaveListener;
import vodka.igor.mosmetro.logic.Validator;
import vodka.igor.mosmetro.models.Line;
import vodka.igor.mosmetro.models.StationLink;
import vodka.igor.mosmetro.ui.ColorCellRenderer;
import vodka.igor.mosmetro.ui.TableDatabaseBinding;
import vodka.igor.mosmetro.ui.item.IDItem;
import vodka.igor.mosmetro.ui.item.StationItem;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public abstract class StationLinkTableFormWrapper extends TableFormWrapper<StationLink> {
    @Override
    public String[] getHeaders() {
        return new String[]{
                "ID",
                "Станция 1",
                "Станция 2",
                "Длина"
        };
    }

    @Override
    public DatabaseRowSaveListener<StationLink> getSaveListener() {
        return (span, row) -> {
            span.setStation1(((StationItem) row.get("Станция 1")).getStation());
            span.setStation2(((StationItem) row.get("Станция 2")).getStation());
            span.setLength(Validator.getInt(row.get("Длина"), span.getLength()));
            return span;
        };
    }

    @Override
    public DatabaseRowLoadListener<StationLink> getLoadListener() {
        return span ->
                new Object[]{
                        new IDItem(span.getId()),
                        new StationItem(span.getStation1()),
                        new StationItem(span.getStation2()),
                        span.getLength()
                };
    }

    protected void customizeStationColumn(
            TableDatabaseBinding<StationLink> binding,
            String columnName,
            Function<StationLink, Line> lineGetter
    ) {
        JComboBox stationsComboBox = StationItem.createStationsComboBox(getSession());
        binding.getTableControl().getColumn(columnName)
                .setCellEditor(new DefaultCellEditor(stationsComboBox));

        binding.getTableControl().getColumn(columnName)
                .setCellRenderer(new ColorCellRenderer() {
                    @Override
                    public Color getColorForCell(int row, int column) {
                        Line line = lineGetter.apply(binding.getEntityByRow(row));
                        return line == null ? Color.WHITE : line.getColor();
                    }
                });
    }

    @Override
    public void customize(TableDatabaseBinding<StationLink> binding) {
        binding.getModel().overrideColumnClass("Длина", Integer.class);
        binding.getModel().overrideColumnClass("Станция 1", StationItem.class);
        binding.getModel().overrideColumnClass("Станция 2", StationItem.class);

        customizeStationColumn(
                binding,
                "Станция 1",
                span -> span.getStation1() == null ? null : span.getStation1().getLine()
        );
        customizeStationColumn(
                binding,
                "Станция 2",
                span -> span.getStation2() == null ? null : span.getStation2().getLine()
        );
    }
}
