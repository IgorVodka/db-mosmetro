package vodka.igor.mosmetro.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.swing.event.ListSelectionEvent;

@Entity(name="stations")
@Table(
	uniqueConstraints=
		@UniqueConstraint(
			name="unique_station_names", 
			columnNames={"line_id", "station_name"}
		)
)
public class Station {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="station_name", length=32)
	private String stationName;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="line_id")
	private Line line;
	
    @OneToMany(mappedBy="station1")
    protected List<Span> spans1 = new ArrayList<Span>();
    @OneToMany(mappedBy="station2")
    protected List<Span> spans2 = new ArrayList<Span>();
    @OneToMany(mappedBy="station1")
    protected List<Change> changes1 = new ArrayList<Change>();
    @OneToMany(mappedBy="station2")
    protected List<Change> changes2 = new ArrayList<Change>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "station", cascade=CascadeType.ALL)
    protected List<Visit> visits = new ArrayList<Visit>();

	public Station(String stationName) {
		this.stationName = stationName;
	}

	public Station() {}
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public Line getLine() {
		return line;
	}
	
	public void setLine(Line line) {
		this.line = line;
	}
	
	public List<Span> getSpans() {
		List<Span> result = new ArrayList<Span>();
		result.addAll(spans1);
		result.addAll(spans2);
				
		return result;
	}
	
	public Span spanTo(Station to, Integer length) {
		Span span = new Span();
		span.setLength(length);
		if(getId() <= to.getId()) {
			span.setStation1(this);
			span.setStation2(to);
			spans1.add(span);
			to.spans2.add(span);
		} else {
			span.setStation1(to);
			span.setStation2(this);
			to.spans1.add(span);
			spans2.add(span);
		}
		return span;
	}

	// TODO: remove span to
	// TODO: remove change to
	
	public List<Change> getChanges() {
		List<Change> result = new ArrayList<Change>();
		result.addAll(changes1);
		result.addAll(changes2);
		return result;
	}
	
	public Change changeTo(Station to, Integer length) {
		Change change = new Change();
		change.setLength(length);
		if(getId() <= to.getId()) {
			change.setStation1(this);
			change.setStation2(to);
			changes1.add(change);
			to.changes2.add(change);
		} else {
			change.setStation1(to);
			change.setStation2(this);
			to.changes1.add(change);
			changes2.add(change);
		}
		return change;
	}

	public List<Visit> getVisits() {
		return visits;
	}

	public void addVisit(Visit visit) {
		visits.add(visit);
		visit.setStation(this);
	}
	
	public void removeVisit(Visit visit) {
		visits.remove(visit);
		visit.setStation(null);
	}
}
