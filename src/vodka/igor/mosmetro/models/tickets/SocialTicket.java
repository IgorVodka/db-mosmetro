package vodka.igor.mosmetro.models.tickets;

import java.sql.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value="2")
public class SocialTicket 
	extends Ticket 
	implements ExpirableTicket {

	@Override
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Override
	public Date getExpirationDate() {
		return expirationDate;
	}
	
	@Override
	public String toString() {
        return "Социальный (" + getExpirationDate() + ")";
    }

    @Override
    public String getTicketTypeName() {
        return "Социальный";
	}
}
