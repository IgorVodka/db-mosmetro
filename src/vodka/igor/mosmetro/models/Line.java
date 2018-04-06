package vodka.igor.mosmetro.models;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity(name="lines")
public class Line {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	public Line(String lineName, Integer color, String tag) {
		this.lineName = lineName;
		this.color = color;
		this.tag = tag;
	}
	
	public Line(String lineName, Color color, String tag) {
		this(lineName, color.getRGB(), tag);
	}

	public Line() {}

	@Column(name="line_name", nullable=false, unique=true, length=32) 
	private String lineName;

	@Column(name="color", nullable=false)
	private Integer color;
	
	@Column(nullable=false, unique=true)
	private String tag;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "line", cascade=CascadeType.ALL)
	private List<Station> stations = new ArrayList<>();
	
	@ManyToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Train> trains = new ArrayList<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public Color getColor() {
		return new Color(color);
	}

	public void setColor(Color color) {
		this.color = color.getRGB();
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public List<Station> getStations() {
		return stations;
	}
	
	public void addStation(Station station) {
		stations.add(station);
		station.setLine(this);
	}
	
	public void removeStation(Station station) {
		stations.remove(station);
		station.setLine(null);
	}

	public List<Train> getTrains() {
		return trains;
	}
	
	public void addTrain(Train train) {
		trains.add(train);
		train.getLines().add(this);
	}
	
	public void removeTrain(Train train) {
		trains.remove(train);
		train.getLines().remove(this);
	}
}
