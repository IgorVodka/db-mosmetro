package vodka.igor.mosmetro.ui.item;

import org.hibernate.Session;
import vodka.igor.mosmetro.models.Line;
import vodka.igor.mosmetro.models.Station;

import javax.swing.*;
import java.util.List;

public class StationItem implements Comparable<StationItem> {
    private Station station;

    public StationItem(Station station) {
        this.station = station;
    }

    public Station getStation() {
        return station;
    }

    @Override
    public String toString() {
        return station == null ? "" : station.getStationName() + " (" +
                (station.getLine() == null ? "" : station.getLine().getTag())
                + ")";
    }

    @Override
    public boolean equals(Object that) {
        if (this.getStation() == null)
            return ((StationItem) that).getStation() == null;

        if (that instanceof StationItem) {
            return this.getStation().equals(((StationItem) that).getStation());
        } else {
            return this == that;
        }
    }

    public static JComboBox createStationsComboBox(Session session) {
        JComboBox comboBox = new JComboBox();
        List stations = session.createQuery("select s from stations s").getResultList();
        for (Station station : (List<Station>) stations) {
            StationItem item = new StationItem(station);
            comboBox.addItem(item);
        }
        return comboBox;
    }

    @Override
    public int compareTo(StationItem that) {
        if (this.getStation() == null)
            return 1;
        return this.getStation().getStationName().compareTo(that.getStation().getStationName());
    }
}