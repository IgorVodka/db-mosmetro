package vodka.igor.mosmetro.models.tickets;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import vodka.igor.mosmetro.models.Visit;

@Entity(name="tickets")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
	name="ticket_type_id", 
	discriminatorType=DiscriminatorType.INTEGER
)
public abstract class Ticket {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="ticket", cascade=CascadeType.ALL)
	private List<Visit> visits = new ArrayList<>();

	protected Date expirationDate;
	protected Double balance;
	
	public abstract String toString();

    abstract String getTicketTypeName();

    public Integer getId() {
        return id;
    }

	public void addVisit(Visit visit) {
		visits.add(visit);
		visit.setTicket(this);
	}
}
