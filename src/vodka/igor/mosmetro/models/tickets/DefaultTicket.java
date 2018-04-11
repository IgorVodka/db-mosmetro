package vodka.igor.mosmetro.models.tickets;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.sql.Date;

@Entity
@DiscriminatorValue(value = "3")
public class DefaultTicket
        extends Ticket {

    @Override
    public String toString() {
        return "Единый";
    }

    @Override
    public String getTicketTypeName() {
        return "Единый";
    }
}
