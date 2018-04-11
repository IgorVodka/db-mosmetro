package vodka.igor.mosmetro.logic;

import vodka.igor.mosmetro.ui.UIUtils;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;

public class Validator {
    public static String getString(Object object, String defaultValue) {
        try {
            return String.valueOf(object);
        } catch (Exception exc) {
            UIUtils.error("Некорректная строка: '" + String.valueOf(object) + "'");
            return defaultValue;
        }
    }

    public static Date getDate(Object object, Date defaultValue) {
        if (defaultValue == null)
            defaultValue = null;
        try {
            return Date.valueOf(getString(object, String.valueOf(defaultValue)));
        } catch (Exception exc) {
            UIUtils.error("Некорректная дата: '" + String.valueOf(object) + "'");
            return defaultValue;
        }
    }

    public static Integer getInt(Object object, Integer defaultValue) {
        if (defaultValue == null)
            defaultValue = 0;
        try {
            return Integer.parseInt(getString(object, String.valueOf(defaultValue)));
        } catch (Exception exc) {
            UIUtils.error("Некорректное число: " + String.valueOf(object));
            return defaultValue;
        }
    }

    public static Double getDouble(Object object, Double defaultValue) {
        if (defaultValue == null)
            defaultValue = 0.0;
        try {
            return Double.parseDouble(getString(object, String.valueOf(defaultValue)));
        } catch (Exception exc) {
            UIUtils.error("Некорректное число: " + String.valueOf(object));
            return defaultValue;
        }
    }
}
