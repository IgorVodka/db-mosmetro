package vodka.igor.mosmetro.models;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="drivers")
public class Driver {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="train_id")
	private Train train;
	
	@Column(name="full_name", length=128, nullable=false)
	private String fullName;
	
	@Column(name="birth_date")
	private Date birthDate;
	
	private boolean working;
	
	public Driver(String fullName, Date birthDate, boolean working) {
		this.fullName = fullName;
		this.birthDate = birthDate;
		this.working = working;
	}

	public Driver() {}
	
	public Driver(String fullName, Date birthDate) {
		this(fullName, birthDate, false);
	}
	
	public Driver(String fullName, boolean working) {
		this(fullName, null, working);
	}
	
	public Driver(String fullName) {
		this(fullName, null, false);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Train getTrain() {
		return train;
	}

	public void setTrain(Train train) {
		this.train = train;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public boolean isWorking() {
		return working;
	}

	public void setWorking(boolean working) {
		this.working = working;
	}	
}
