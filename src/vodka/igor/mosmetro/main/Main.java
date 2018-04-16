package vodka.igor.mosmetro.main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.io.File;
import java.sql.Date;
import java.util.Random;

import javax.persistence.Query;
import javax.swing.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import vodka.igor.mosmetro.logic.AccessGroup;
import vodka.igor.mosmetro.logic.MetroManager;
import vodka.igor.mosmetro.models.*;
import vodka.igor.mosmetro.models.tickets.*;

public class Main {
	Session session;
    Random r;

    Integer trainCounter = 0;
	
	public Line createLine(String name, Color color, String tag) {
		Line line = new Line(name, color, tag);
		session.save(line);
		return line;
	}
	
	public Station createStation(String name) {
		Station station = new Station(name);
		session.save(station);
		return station;
	}
	
	public Train createTrain(Integer number, String model, Date productionDate) {
		Train train = new Train(number, model);
		train.setProductionDate(productionDate);
		train.refreshRepairDate();
		session.save(train);
		
		return train;
	}
	
	public Driver createDriver(String name, Date birthDate, boolean isWorking) {
		Driver driver = new Driver(name, birthDate, isWorking);
		session.save(driver);
		
		return driver;
	}

    public Span createSpanBetween(Station station1, Station station2, Integer length) {
        Span span = station1.spanTo(station2, length);
		session.save(span);
		return span;
	}
	
	public Change createChangeBetween(Station station1, Station station2) {
        Change change = station1.changeTo(station2, Math.abs(r.nextInt() % 3) + 2);
		session.save(change);
		return change;
	}
	
	public Visit createVisit(Ticket ticket) {
		Visit visit = new Visit(ticket);
		visit.setDate(new Date(117, 1, r.nextInt() % 15));
		session.save(visit);
		return visit;
	}
	
	protected void handle() {
		SessionFactory sessionFactory = new Configuration()
                .configure(new File("resources/hibernate.cfg.xml"))
				.buildSessionFactory();

		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName()
			);
		}
		catch (Exception e) {
			// handle exception
		}

        r = new Random();
		
		session = sessionFactory.openSession();
		session.beginTransaction();

        Line line1 = createLine("Сокольническая", Color.RED, "1");
        line1.addStation(createStation("Бульвар Рокоссовского")); // 0
        line1.addStation(createStation("Черкизовская")); // 1
        line1.addStation(createStation("Преображенская площадь")); // 2
        line1.addStation(createStation("Сокольники")); // 3
        line1.addStation(createStation("Красносельская")); // 4
        line1.addStation(createStation("Комсомольская")); // 5
        line1.addStation(createStation("Красные ворота")); // 6
        line1.addStation(createStation("Чистые пруды")); // 7

        Line line2 = createLine("Арбатско-Покровская", Color.BLUE, "3");
        line2.addStation(createStation("Щёлковская")); // 0
        line2.addStation(createStation("Первомайская")); // 1
        line2.addStation(createStation("Измайловская")); // 2
        line2.addStation(createStation("Партизанская")); // 3
        line2.addStation(createStation("Семёновская")); // 4
        line2.addStation(createStation("Электрозаводская")); // 5
        line2.addStation(createStation("Бауманская")); // 6
        line2.addStation(createStation("Курская")); // 7

        Line line3 = createLine("Калининско-Солнцевская", Color.YELLOW, "8");
        line3.addStation(createStation("Новокосино")); // 0
        line3.addStation(createStation("Новогиреево")); // 1
        line3.addStation(createStation("Перово")); // 2
        line3.addStation(createStation("Шоссе Энтузиастов")); // 3
        line3.addStation(createStation("Авиамоторная")); // 4
        line3.addStation(createStation("Площадь Ильича")); // 5
        line3.addStation(createStation("Марксистская")); // 6

        Line line4 = createLine("Люблинско-Дмитровская", Color.GREEN, "10");
        line4.addStation(createStation("Трубная")); // 0
        line4.addStation(createStation("Сретенский бульвар")); // 1
        line4.addStation(createStation("Чкаловская")); // 2
        line4.addStation(createStation("Римская")); // 3
        line4.addStation(createStation("Крестьянская застава")); // 4
        line4.addStation(createStation("Дубровка")); // 5

        Line line5 = createLine("Кольцевая", new Color(165, 42, 42), "5");
        line5.addStation(createStation("Павелецкая")); // 0
        line5.addStation(createStation("Таганская")); // 1
        line5.addStation(createStation("Курская")); // 2
        line5.addStation(createStation("Комсомольская")); // 3
        line5.addStation(createStation("Проспект Мира")); // 4

        addSequentialSpans(line1);
        addSequentialSpans(line2);
        addSequentialSpans(line3);
        addSequentialSpans(line4);
        addSequentialSpans(line5);

        addSomeTrains(line1, "Номерной", 15, 40);
        addSomeTrains(line2, "Русич", 10, 10);
        addSomeTrains(line2, "Ока", 5, 5);
        addSomeTrains(line3, "Ока", 10, 5);
        addSomeTrains(line4, "Номерной", 12, 40);
        addSomeTrains(line4, "Яуза", 3, 12);
        addSomeTrains(line5, "Русич", 7, 10);
        addSomeTrains(line5, "Ока", 7, 5);

        createChangeBetween(line1.getStations().get(7), line4.getStations().get(1));
        // Чистые пруды (1) - Сретенский бульвар (10)

        createChangeBetween(line2.getStations().get(7), line4.getStations().get(2));
        // Курская (3) - Чкаловская (10)

        createChangeBetween(line3.getStations().get(5), line4.getStations().get(3));
        // Площадь Ильича (8) - Римская (10)

        createChangeBetween(line5.getStations().get(1), line3.getStations().get(6));
        // Таганская (5) - Марксистская (8)

        createChangeBetween(line5.getStations().get(2), line4.getStations().get(2));
        // Курская (5) - Чкаловская (10)

        createChangeBetween(line5.getStations().get(2), line2.getStations().get(7));
        // Курская (5) - Курская (10)

        createChangeBetween(line5.getStations().get(3), line1.getStations().get(5));
        // Комсомольская (5) - Комсомольская (1)

        addSomeTrainsToAnotherLine(line1, line4);
        addSomeTrainsToAnotherLine(line2, line3);
        addSomeTrainsToAnotherLine(line1, line5);

        List<Ticket> tickets = createRandomTickets();

        addRandomVisitsForLine(line1, tickets);
        addRandomVisitsForLine(line2, tickets);
        addRandomVisitsForLine(line3, tickets);
        addRandomVisitsForLine(line4, tickets);
        addRandomVisitsForLine(line5, tickets);

		session.getTransaction().commit();

		AccessGroup group = new LoginForm().showDialog();
		if(group == null)
            exit();
		MetroManager manager = MetroManager.getInstance();
		manager.setAccessGroup(group);
		manager.setSession(session);

		new MainForm().showForm();
	}

    private void addRandomVisitsForLine(Line line, List<Ticket> tickets) {
		for (int i = 0; i < line.getStations().size(); i++) {
            int times = (Math.abs(r.nextInt()) % 50) + 250;
			for (int j = 0; j < times; j++) {
                line.getStations().get(i).addVisit(
                        createVisit(tickets.get(Math.abs(r.nextInt() % tickets.size())))
                );
            }
        }
    }

    private void addSequentialSpans(Line line) {
        for (int i = 0; i < line.getStations().size() - 1; i++) {
            createSpanBetween(
                    line.getStations().get(i),
                    line.getStations().get(i + 1),
                    (Math.abs(r.nextInt()) % 3) + 2
            );
        }
    }

    private void addSomeTrains(Line line, String trainName, Integer count, Integer age) {
        // TODO: line trains is M:M so add some same trains for both lines!

        for (int i = 0; i < count; i++) {
            Train train = new Train();
            train.setModel(trainName);
            train.setNumber(trainCounter++);
            train.setProductionDate(Date.valueOf(LocalDate.now().minusDays(Math.abs(r.nextInt() % (age * 365)))));
            train.setRepairDate(Date.valueOf(LocalDate.now().minusDays(Math.abs(r.nextInt() % 500))));
            train.setOnRoute(r.nextBoolean());

            int driversForTrain = Math.abs(r.nextInt() % 2) + 1;
            for (int j = 0; j < driversForTrain; j++) {
                addRandomDriver(train);
            }

            session.save(train);
            line.addTrain(train);
        }
    }

    private void addRandomDriver(Train train) {
        List<String> names = new ArrayList<String>();
        List<String> surnames = new ArrayList<String>();

        names.add("Андрей");
        names.add("Игорь");
        names.add("Борис");
        names.add("Коцта");
        names.add("Макс");
        names.add("Серёга");
        names.add("Владимир");
        names.add("Родион");
        names.add("Артемс");
        names.add("Фьодор");

        surnames.add("Хапов");
        surnames.add("Водка");
        surnames.add("Ботов");
        surnames.add("Маслеников");
        surnames.add("Олюш");
        surnames.add("Чернобровкин");
        surnames.add("Афанасьев");
        surnames.add("Болгар");
        surnames.add("Афанасьев");
        surnames.add("Кареникс");
        surnames.add("Скрипник");

        String name = names.get(Math.abs(r.nextInt() % names.size()))
                + " " + surnames.get(Math.abs(r.nextInt() % surnames.size()));
        Date birthDate = Date.valueOf(LocalDate.now().minusDays(Math.abs(r.nextInt() % 60 * 365)));

        Driver driver = new Driver();
        driver.setFullName(name);
        driver.setBirthDate(birthDate);
        driver.setTrain(train);
        session.save(driver);
    }

    private void addSomeTrainsToAnotherLine(Line from, Line to) {
        for (int i = 0; i < from.getLineTrains().size(); i++) {
            LineTrain lt = from.getLineTrains().get(i);
            Train train = lt.getTrain();
            if (r.nextBoolean() && r.nextBoolean()) {
                to.addTrain(train);
            }
        }
    }

    private List<Ticket> createRandomTickets() {
        List<Ticket> tickets = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            int type = Math.abs(r.nextInt() % 3);
            if (type == 0) {
                TroikaTicket tt = new TroikaTicket();
                tt.setBalance((double) r.nextInt() % 1000);
                tt.setExpirationDate(Date.valueOf(LocalDate.now().plusDays(r.nextInt() % 50)));
                session.save(tt);
                tickets.add(tt);
            } else if (type == 1) {
                SocialTicket st = new SocialTicket();
                st.setExpirationDate(Date.valueOf(LocalDate.now().plusDays(r.nextInt() % 50)));
                session.save(st);
                tickets.add(st);
            } else {
                DefaultTicket dt = new DefaultTicket();
                session.save(dt);
                tickets.add(dt);
            }
        }

        return tickets;
	}

	private void exit() {
        session.close();
        System.exit(0);
    }
	
	public static void main(String[] args) {
		new Main().handle();
	}
}
