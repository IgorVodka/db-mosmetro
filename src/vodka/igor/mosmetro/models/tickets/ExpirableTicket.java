package vodka.igor.mosmetro.models.tickets;

import java.sql.Date;

public interface ExpirableTicket {
	public void setExpirationDate(Date expirationDate);
	public Date getExpirationDate();
}
