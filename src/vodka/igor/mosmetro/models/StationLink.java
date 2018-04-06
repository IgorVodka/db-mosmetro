package vodka.igor.mosmetro.models;

import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class StationLink {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected Integer id;
	
	protected Integer length;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="station1")
	protected Station station1;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="station2")
	protected Station station2;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Station getStation1() {
		return station1;
	}

	public void setStation1(Station station1) {
		this.station1 = station1;
	}

	public Station getStation2() {
		return station2;
	}

	public void setStation2(Station station2) {
		this.station2 = station2;
	}
}
