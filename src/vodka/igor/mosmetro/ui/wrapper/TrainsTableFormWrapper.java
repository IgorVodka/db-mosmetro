package vodka.igor.mosmetro.ui.wrapper;

import vodka.igor.mosmetro.listener.DatabaseRowLoadListener;
import vodka.igor.mosmetro.listener.DatabaseRowSaveListener;
import vodka.igor.mosmetro.logic.Validator;
import vodka.igor.mosmetro.main.GenericTableForm;
import vodka.igor.mosmetro.models.*;
import vodka.igor.mosmetro.ui.ColorCellRenderer;
import vodka.igor.mosmetro.ui.TableDatabaseBinding;
import vodka.igor.mosmetro.ui.item.IDItem;
import vodka.igor.mosmetro.ui.item.LineItem;
import vodka.igor.mosmetro.ui.item.StationItem;
import vodka.igor.mosmetro.ui.item.TrainItem;

import javax.persistence.Query;
import java.awt.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDate;

public class TrainsTableFormWrapper extends TableFormWrapper<Train> {
    @Override
    public String getName() {
        return "Поезда";
    }

    @Override
    public Query getQuery() {
        return getSession().createQuery("select t from trains t");
    }

    @Override
    public String[] getHeaders() {
        return new String[]{
                "ID",
                "Модель",
                "Номер",
                "На линии",
                "Произведён",
                "Ремонт"
        };
    }

    @Override
    public DatabaseRowSaveListener<Train> getSaveListener() {
        return (train, row) -> {
            train.setModel(Validator.getString(row.get("Модель"), train.getModel()));
            train.setNumber(Validator.getInt(row.get("Номер"), train.getNumber()));
            train.setProductionDate(Validator.getDate(row.get("Произведён"), train.getProductionDate()));
            train.setRepairDate(Validator.getDate(row.get("Ремонт"), train.getRepairDate()));
            train.setOnRoute(row.get("На линии").equals(true));
            return train;
        };
    }

    @Override
    public DatabaseRowLoadListener<Train> getLoadListener() {
        return train ->
                new Object[]{
                        new IDItem(train.getId()),
                        train.getModel(),
                        train.getNumber(),
                        train.isOnRoute(),
                        train.getProductionDate() == null ? "" : train.getProductionDate().toString(),
                        train.getRepairDate() == null ? "" : train.getRepairDate().toString(),
                };
    }

    @Override
    public void customize(TableDatabaseBinding<Train> binding) {
        binding.getModel().overrideColumnClass("На линии", Boolean.class);

        binding.getTableControl().getColumn("Ремонт")
                .setCellRenderer(new ColorCellRenderer() {
                    @Override
                    public Color getColorForCell(int row, int column) {
                        Train train = binding.getEntityByRow(row);
                        Date oneYearAgo = Date.valueOf(LocalDate.now().minusYears(1));
                        if(train.getRepairDate() == null)
                            return Color.WHITE;
                        return train.getRepairDate().before(oneYearAgo) ? Color.RED : Color.GREEN;
                    }
                });

        getForm().addControlButtonIf("see.drivers", "Машинисты", actionEvent -> {
            Train selectedTrain = binding.getSelectedEntity();

            DriversTableFormWrapper wrapper = new DriversTableFormWrapper() {
                @Override
                public Query getQuery() {
                    return getSession().createQuery(
                            "select d from drivers d where d.train = :train"
                    ).setParameter("train", selectedTrain);
                }

                @Override
                public String getName() {
                    return "Машинисты поезда " + selectedTrain.getNumber();
                }

                @Override
                public void customize(TableDatabaseBinding<Driver> binding) {
                    super.customize(binding);
                    binding.addFixedColumn("Поезд", new TrainItem(selectedTrain));
                }
            };
            new GenericTableForm<>(LineTrain.class, wrapper).showForm();
        });

        getForm().addControlButtonIf("see.lines", "Линии", actionEvent -> {
            Train selectedTrain = binding.getSelectedEntity();

            LineTrainsTableFormWrapper wrapper = new LineTrainsTableFormWrapper() {
                @Override
                public Query getQuery() {
                    return getSession().createQuery(
                            "select lt from line_trains lt" +
                                    " where lt.train = :train"
                    ).setParameter("train", selectedTrain);
                }

                @Override
                public String getName() {
                    return "Линии поезда " + selectedTrain.getNumber();
                }

                @Override
                public void customize(TableDatabaseBinding<LineTrain> binding) {
                    super.customize(binding);
                    binding.addFixedColumn("Поезд", new TrainItem
                            (selectedTrain));
                }
            };
            new GenericTableForm<>(LineTrain.class, wrapper).showForm();
        });
    }
}
