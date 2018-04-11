package vodka.igor.mosmetro.ui.wrapper;

import vodka.igor.mosmetro.listener.DatabaseRowLoadListener;
import vodka.igor.mosmetro.listener.DatabaseRowSaveListener;
import vodka.igor.mosmetro.logic.Validator;
import vodka.igor.mosmetro.models.Driver;
import vodka.igor.mosmetro.ui.TableDatabaseBinding;
import vodka.igor.mosmetro.ui.item.IDItem;
import vodka.igor.mosmetro.ui.item.TrainItem;

import javax.persistence.Query;
import javax.swing.*;
import java.sql.Date;

public class DriversTableFormWrapper extends TableFormWrapper<Driver> {
    @Override
    public String getName() {
        return "Машинисты";
    }

    @Override
    public Query getQuery() {
        return getSession().createQuery("select d from drivers d");
    }

    @Override
    public String[] getHeaders() {
        return new String[]{
                "ID",
                "ФИО",
                "Дата рождения",
                "Поезд"
        };
    }

    @Override
    public DatabaseRowSaveListener<Driver> getSaveListener() {
        return (driver, row) -> {
            driver.setFullName(Validator.getString(row.get("ФИО"), driver.getFullName()));
            driver.setBirthDate(Validator.getDate(row.get("Дата рождения"), driver.getBirthDate()));
            driver.setTrain(((TrainItem) row.get("Поезд")).getTrain());
            return driver;
        };
    }

    @Override
    public DatabaseRowLoadListener<Driver> getLoadListener() {
        return driver ->
                new Object[]{
                        new IDItem(driver.getId()),
                        driver.getFullName(),
                        driver.getBirthDate().toString(),
                        new TrainItem(driver.getTrain())
                };
    }

    @Override
    public void customize(TableDatabaseBinding<Driver> binding) {
        binding.getModel().overrideColumnClass("Поезд", TrainItem.class);

        JComboBox comboBox = TrainItem.createTrainsComboBox(getSession());
        binding.getTableControl().getColumn("Поезд")
                .setCellEditor(new DefaultCellEditor(comboBox));
    }
}
