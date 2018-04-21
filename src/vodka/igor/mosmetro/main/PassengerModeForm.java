package vodka.igor.mosmetro.main;

import org.hibernate.Session;
import vodka.igor.mosmetro.logic.MetroManager;
import vodka.igor.mosmetro.models.Change;
import vodka.igor.mosmetro.models.Span;
import vodka.igor.mosmetro.models.Station;
import vodka.igor.mosmetro.ui.ShowableForm;
import vodka.igor.mosmetro.ui.item.StationItem;

import javax.persistence.NoResultException;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class PassengerModeForm extends JFrame implements ShowableForm {
    private JLabel lineName;
    private JLabel stationName;
    private JProgressBar goingProgressBar;
    private JPanel changePane;
    private JPanel contentPane;
    private JComboBox changeComboBox;
    private JButton changeButton;
    private JLabel nextStationName;
    private JButton revertDirectionButton;

    private enum Direction { LEFT, RIGHT };
    private Direction direction;
    private Station currentStation;
    private Station nextStation;
    private Session session;
    private Thread progressBarTickThread;
    private boolean stopped = false;

    public PassengerModeForm() {
        setContentPane(contentPane);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Closing...");
                progressBarTickThread.stop();
            }
        });
        changeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Object item = changeComboBox.getSelectedItem();
                if(item != null) {
                    Station station = ((StationItem)item).getStation();
                    changeFor(station);
                }
            }
        });
        revertDirectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                revertDirection();
                goToStation(currentStation);
            }
        });
    }

    public Station getNextStation(Station station) {
        Span span;
        Station nextStation;

        try {
            if (direction.equals(Direction.RIGHT)) {
                span = (Span) session.createQuery("select s from spans s where s.station2 = :station")
                        .setParameter("station", station)
                        .getSingleResult();
                nextStation = span == null ? null : span.getStation1();
            } else {
                span = (Span) session.createQuery("select s from spans s where s.station1 = :station")
                        .setParameter("station", station)
                        .getSingleResult();
                nextStation = span == null ? null : span.getStation2();
            }
        } catch(NoResultException e) {
            return null;
        }

        return nextStation;
    }

    public void updateChangeControls() {
        boolean changesNeeded = stopped && changeComboBox.getItemCount() > 0;

        changeButton.setEnabled(changesNeeded);
        changeComboBox.setEnabled(changesNeeded);
        revertDirectionButton.setEnabled(stopped);
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
        updateChangeControls();
    }

    public String getCustomSoundMessage() {
        if(nextStation.getStationName().equals("Партизанская")) {
            return getSpeakerFile("Три станционных пути.wav");
        }
        return null;
    }

    public void updateProgressBar(long timeNeededStationName) {
        System.out.println("update progress bar!");

        vodka.igor.mosmetro.models.Line line = currentStation.getLine();
        List<Station> stations = line.getStations();
        int totalStations = stations.size();

        // reverse if different direction

        int currentStationIndex = stations.indexOf(currentStation);
        if(direction.equals(Direction.RIGHT)) {
            currentStationIndex = totalStations - currentStationIndex - 1;
        }
        goingProgressBar.setMaximum(totalStations);
        goingProgressBar.setValue(currentStationIndex + 1);

        if(progressBarTickThread != null) {
            progressBarTickThread.interrupt();
        }

        final int localCurrentStationIndex = currentStationIndex;
        progressBarTickThread = new Thread(() -> {
            try {
                setStopped(true);
                setTitle("Остановка.");
                Thread.sleep(Math.max(timeNeededStationName, 4000)); // осторожно, двери закрываются
                setTitle("Осторожно, двери закрываются!");

                Long timeSpent;
                if(nextStation != null) {
                    List soundsList = new ArrayList<String>();
                    Collections.addAll(
                            soundsList,
                            getSpeakerFile("ОДЗ.wav"),
                           getSpeakerFile("stations", nextStation)
                    );

                    Random r = new Random();
                    int messageNumber = Math.abs(r.nextInt() % 5);

                    String customMessage = getCustomSoundMessage();
                    if(customMessage != null) {
                        soundsList.add(customMessage);
                    } else {
                        if (messageNumber == 0) {
                            soundsList.add(getSpeakerFile("Не забывайте свои вещи.wav"));
                        } else if (messageNumber == 1) {
                            soundsList.add(getSpeakerFile("О подозрительных предметах.wav"));
                        } else if (messageNumber == 2) {
                            soundsList.add(getSpeakerFile("Уступайте.wav"));
                        }
                    }

                    timeSpent = playMultipleSounds(soundsList);
                } else {
                    timeSpent = playSound(getSpeakerFile("Дальше не идет.wav"));
                }

                Thread.sleep(2000);
                setStopped(false);
                setTitle("Едем...");

                for (int i = 0; i < Math.max(4, (timeSpent / 2000)); i++) {
                    goingProgressBar.setValue(localCurrentStationIndex + 1);
                    Thread.sleep(1000);
                    goingProgressBar.setValue(localCurrentStationIndex + 2);
                    Thread.sleep(1000);
                }

                if (nextStation == null) {
                    revertDirection();
                    goToStation(currentStation);
                } else {
                    goToStation(nextStation);
                }
            } catch(InterruptedException e) {
                System.out.println("interrupted");
            }
        });
        progressBarTickThread.start();
    }

    public void changeFor(Station station) {
        // TODO: stop all sounds
        goToStation(station);
    }

    private String getSpeakerName() {
        return currentStation == null || currentStation.getLine().getLineName().equals("Сокольническая") ?
                "Андрей" : "Игорь";
    }

    private String getSpeakerFile(String fileName) {
        return getSpeakerName() + "/" + fileName;
    }

    private String getSpeakerFile(String type, Station station) {
        return getSpeakerFile(
            type + "/" + station.getLine().getLineName() + "/" + station.getStationName() + ".wav"
        );
    }

    private void revertDirection() {
        direction = direction.equals(Direction.LEFT) ? Direction.RIGHT : Direction.LEFT;
        nextStation = getNextStation(currentStation);
    }

    public void goToStation(Station station) {
        currentStation = station;

        List soundsList = new ArrayList<String>();
        Collections.addAll(
                soundsList,
                getSpeakerFile("Станция.wav"),
                getSpeakerFile("stations", station)
        );

        soundsList.add(getSpeakerFile("changes", station));
        soundsList.add(getSpeakerFile("railway", station));

        long minTimeNeeded = playMultipleSounds(soundsList);

        lineName.setText(station.getLine().getLineName());
        stationName.setText(station.getStationName());

        nextStation = getNextStation(currentStation);
        nextStationName.setText(nextStation == null ? "(нет)" : nextStation.getStationName());

        List<Change> changes = station.getChanges();
        changeComboBox.removeAllItems();
        for(Change change : changes) {
            changeComboBox.addItem(
                    new StationItem(
                            change.getStation1().getId().equals(currentStation.getId())
                                    ? change.getStation2()
                                    : change.getStation1()
                    )
            );
        }

        updateProgressBar(minTimeNeeded);
    }

    public long playMultipleSounds(List<String> fileNames) {
            ArrayList<Clip> clips = new ArrayList<>();
            ArrayList<Long> waitTimes = new ArrayList<>();
            for(String fileName : fileNames) {
                try {
                    Clip clip = loadSoundClip(fileName);
                    clips.add(clip);
                    waitTimes.add(clip.getMicrosecondLength() / 1000);
                } catch(Exception e) {
                }
            }
            new Thread(() -> {
                int i = 0;
                try {
                    for(i = 0; i < clips.size(); i++) {
                        clips.get(i).start();
                        if(i != clips.size() - 1)
                            Thread.sleep(waitTimes.get(i));
                    }
                } catch (InterruptedException e) {
                    System.out.println("No file: " + fileNames.get(i));
                }
            }).start();

            return waitTimes.stream().reduce((x, y) -> x + y).orElse(0l);
    }

    public long playSound(String fileName) {
        try {
            Clip clip = loadSoundClip(fileName);
            clip.start();
            return clip.getMicrosecondLength() / 1000;
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Clip loadSoundClip(String fileName) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File file = new File("resources/sounds/" + fileName);
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
        DataLine.Info info = new DataLine.Info(Clip.class, inputStream.getFormat());
        Clip clip = (Clip)AudioSystem.getLine(info);
        clip.open(inputStream);
        return clip;
    }

    public void showForm() {
        direction = Direction.LEFT;+++++++++++++++++++++++
        session = MetroManager.getInstance().getSession();
        Station station = session.get(Station.class, 9);
        goToStation(station);

        pack();
        setVisible(true);
    }
}
