package vodka.igor.mosmetro.ui.item;

import org.hibernate.Session;
import vodka.igor.mosmetro.models.Train;

import javax.swing.*;
import java.util.List;

public class TrainItem implements Comparable<TrainItem> {
    private Train train;

    public TrainItem(Train train) {
        this.train = train;
    }

    public Train getTrain() {
        return train;
    }

    @Override
    public String toString() {
        return train == null ? "" : "#" + train.getNumber() + ": " + train.getModel();
    }

    @Override
    public boolean equals(Object that) {
        if (this.getTrain() == null)
            return ((TrainItem) that).getTrain() == null;

        if (that instanceof TrainItem) {
            return this.getTrain().equals(((TrainItem) that).getTrain());
        } else {
            return this == that;
        }
    }

    public static JComboBox createTrainsComboBox(Session session) {
        JComboBox comboBox = new JComboBox();
        List trains = session.createQuery("select t from trains t").getResultList();
        comboBox.addItem(new TrainItem(null));
        for (Train train : (List<Train>) trains) {
            TrainItem item = new TrainItem(train);
            comboBox.addItem(item);
        }
        return comboBox;
    }

    @Override
    public int compareTo(TrainItem that) {
        if (this.getTrain() == null)
            return 1;
        return this.getTrain().getModel().compareTo(that.getTrain().getModel());
    }
}