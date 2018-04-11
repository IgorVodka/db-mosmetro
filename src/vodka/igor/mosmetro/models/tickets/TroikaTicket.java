package vodka.igor.mosmetro.models.tickets;

import java.sql.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value="1")
public class TroikaTicket 
	extends Ticket 
	implements ExpirableTicket, BalanceTicket {

	@Override
	public void setBalance(Double balance) {
		this.balance = balance;
	}

	@Override
	public Double getBalance() {
		return balance;
	}

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
        return "Тройка (" + getExpirationDate() + ", " + getBalance() + ")";
    }

    @Override
    public String getTicketTypeName() {
        return "Тройка";
    }
}
