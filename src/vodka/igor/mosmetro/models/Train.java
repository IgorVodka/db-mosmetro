package vodka.igor.mosmetro.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.NotFound;

@Entity(name="trains")
public class Train {
	final static int YEARS_TILL_REPAIR = 1;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(unique=true, nullable=false)
	private Integer number;
	
	@Column(nullable=false, length=32)
	private String model;
	
	@Column(name="production_date")
	private Date productionDate;
	
	@Column(name="repair_date")
	private Date repairDate;
	
	private boolean onRoute = false;

    //@ManyToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="trains")
    //private List<Line> lines = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "train", cascade = CascadeType.ALL)
    private List<LineTrain> trainLines = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "train", cascade=CascadeType.ALL)
	private List<Driver> drivers = new ArrayList<>();

	public Train(Integer number, String model, boolean onRoute) {
		this.number = number;
		this.model = model;
		this.onRoute = onRoute;
		this.productionDate = yearOffset(0);
		this.repairDate = yearOffset(YEARS_TILL_REPAIR);
	}

	public Train(Integer number, String model) {
		this(number, model, false);
	}

	public Train() {}
	
	private Date yearOffset(int years, long millis) {
		return new Date(millis + 86400*365*years);
	}
	
	private Date yearOffset(int years) {
		return yearOffset(years, System.currentTimeMillis());
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Date getProductionDate() {
		return productionDate;
	}

	public void setProductionDate(Date productionDate) {
		this.productionDate = productionDate;
	}

	public Date getRepairDate() {
		return repairDate;
	}

	public void setRepairDate(Date repairDate) {
		this.repairDate = repairDate;
	}
	
	public void setDates(Date productionDate, int yearsTillRepair) {
		setProductionDate(productionDate);
		setRepairDate(yearOffset(yearsTillRepair, productionDate.getTime()));
	}
	
	public void setDates(Date productionDate) {
		setDates(productionDate, YEARS_TILL_REPAIR);
	}
	
	public void refreshRepairDate(int yearsTillRepair) {
		setRepairDate(yearOffset(yearsTillRepair));
	}
	
	public void refreshRepairDate() {
		setRepairDate(yearOffset(YEARS_TILL_REPAIR));
	}

	public boolean isOnRoute() {
		return onRoute;
	}

	public void setOnRoute(boolean onRoute) {
		this.onRoute = onRoute;
	}

	public void addDriver(Driver driver) {
		drivers.add(driver);
		driver.setTrain(this);
	}
	
	public void removeDriver(Driver driver) {
		drivers.remove(driver);
		driver.setTrain(null);
	}

    public List<LineTrain> getTrainLines() {
        return trainLines;
    }

	public void addLine(Line line) {
        trainLines.add(new LineTrain(line, this));
        //train.getTrainLines()...
    }

	public void removeLine(Line line) {
        trainLines.removeIf(lineTrain -> lineTrain.getLine().equals(line));
	}
}