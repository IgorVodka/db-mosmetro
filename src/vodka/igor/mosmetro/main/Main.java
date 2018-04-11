package vodka.igor.mosmetro.main;

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
	
	public Span createSpanBetween(Station station1, Station station2) {
		Span span = station1.spanTo(station2, 5);
		session.save(span);
		return span;
	}
	
	public Change createChangeBetween(Station station1, Station station2) {
		Change change = station1.changeTo(station2, 5);
		session.save(change);
		return change;
	}
	
	public Visit createVisit(Ticket ticket) {
		Visit visit = new Visit(ticket);
		Random r = new Random();
		visit.setDate(new Date(117, 1, r.nextInt() % 15));
		session.save(visit);
		return visit;
	}
	
	protected void handle() {
		SessionFactory sessionFactory = new Configuration()
				.configure(new File("src/resources/hibernate.cfg.xml"))
				.buildSessionFactory();

		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName()
			);
		}
		catch (Exception e) {
			// handle exception
		}
		
		session = sessionFactory.openSession();
		session.beginTransaction();

		Line line = createLine("Сокольническая", Color.RED, "1");
		line.addStation(createStation("Бульварро"));
		line.addStation(createStation("Румянцево"));
		line.addStation(createStation("Измайлово"));

		Line line2 = createLine("Калининско-Солнцевская", Color.YELLOW, "8");
		line2.addStation(createStation("Авиамоторная"));
		line2.addStation(createStation("Перово"));

		Station stationToBeDeleted = createStation("ээ не надо тут этой станции");
		line.addStation(stationToBeDeleted);
		line.removeStation(stationToBeDeleted);
		session.delete(stationToBeDeleted);

		Train train = createTrain(123, "Ёж", new Date(1, 1, 1));
		line.addTrain(train);
		
		Driver driver = createDriver("Хапов", new Date(1, 1, 5), true);
		train.addDriver(driver);
		
		Driver driver2 = createDriver("Водка", new Date(2, 2, 6), false);
		train.addDriver(driver2);
		
		createSpanBetween(
			line.getStations().get(0), 
			line.getStations().get(1)
		);
		
		createSpanBetween(
			line.getStations().get(2), 
			line.getStations().get(1)
		);
		
		createChangeBetween(
			line.getStations().get(0), 
			line.getStations().get(2)
		);
		
		TroikaTicket ticket = new TroikaTicket();
	    ticket.setExpirationDate(new Date(5, 5, 5));
	    ticket.setBalance(35.0);
		session.save(ticket);
		
		SocialTicket socialTicket = new SocialTicket();
		socialTicket.setExpirationDate(new Date(6, 6, 6));
		session.save(socialTicket);	
		
		Query q = session.createQuery(
			"select t from tickets t"
		);
		for(Ticket t : (List<Ticket>) q.getResultList()) {
			System.out.println(t);
		}

		addRandomVisitsForLine(line, ticket);
		addRandomVisitsForLine(line2, ticket);

		session.getTransaction().commit();
		
		List<Span> wowSpans = line.getStations().get(1).getSpans();
		System.out.println("spans count: " + wowSpans.size());
		for(Span wowSpan : wowSpans) {
			System.out.println(
					wowSpan.getStation1().getStationName() + 
					" TO " + wowSpan.getStation2().getStationName());
		}
		
		List<Change> wowChanges = line.getStations().get(2).getChanges();
		System.out.println("changes count: " + wowChanges.size());
		for(Change wowChange : wowChanges) {
			System.out.println(
					wowChange.getStation1().getStationName() + 
					" TO " + wowChange.getStation2().getStationName());
		}
		
		Query query = session.createQuery(
			"select s from stations s "
			+ "join lines l "
			+ "on s.line = l "
			+ "where line_name = :lineName"
		);
		query.setParameter("lineName", "Сокольническая");
		for(Station s : (List<Station>) query.getResultList()) {
			System.out.println("На линии есть станция " + s.getStationName());
		}

		AccessGroup group = new LoginForm().showDialog();
		if(group == null)
            exit();
		MetroManager manager = MetroManager.getInstance();
		manager.setAccessGroup(group);
		manager.setSession(session);

		new MainForm().showForm();
	}

	private void addRandomVisitsForLine(Line line, Ticket ticket) {
		Random r = new Random();
		for (int i = 0; i < line.getStations().size(); i++) {
			int times = r.nextInt() % 250 + 100;
			for (int j = 0; j < times; j++) {
				line.getStations().get(i).addVisit(createVisit(ticket));
			}
		}
	}

	private void exit() {
        session.close();
        System.exit(0);
    }
	
	public static void main(String[] args) {
		new Main().handle();
	}
}
