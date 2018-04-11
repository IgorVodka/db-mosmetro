package vodka.igor.mosmetro.models;

import javax.persistence.*;

@Entity(name = "line_trains")
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"line", "train"})
)
public class LineTrain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line")
    protected Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train")
    protected Train train;

    public LineTrain() {
        this(null, null);
    }

    public LineTrain(Line line, Train train) {
        this.line = line;
        this.train = train;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }
}
