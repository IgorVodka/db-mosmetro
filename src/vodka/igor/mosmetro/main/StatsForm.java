package vodka.igor.mosmetro.main;

import org.hibernate.Session;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import vodka.igor.mosmetro.logic.MetroManager;
import vodka.igor.mosmetro.models.Station;
import vodka.igor.mosmetro.ui.ShowableForm;

import vodka.igor.mosmetro.models.Line;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatsForm extends JFrame implements ShowableForm {
    private JPanel contentPane;
    private JPanel plotPane;
    private JPanel infoPane;

    public StatsForm() {
        setTitle("Статистика посещений");

        setContentPane(contentPane);
        setPreferredSize(new Dimension(900, 450));
        setMaximumSize(new Dimension(900, 450));
        contentPane.setLayout(new GridLayout(1, 2));
        plotPane.setLayout(new CardLayout());
        infoPane.setLayout(new CardLayout());
    }

    private Session getSession() {
        return MetroManager.getInstance().getSession();
    }

    private void createDataSet(XYPlot plot, JTextArea textArea) {
        TimeSeriesCollection ds = new TimeSeriesCollection();

        List<Line> allLines = (List<Line>) getSession().createQuery(
                "select l from lines l"
        ).getResultList();

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        int seriesId = 0;

        for (Line line : allLines) {
            textArea.append(line.getLineName() + ":\n");
            TimeSeries ts = new TimeSeries(line.getLineName());

            List<Object[]> visitsEveryDay = getSession().createQuery(
                    "select v.date, count(v.id) " +
                            "from visits v " +
                            "where v.station.line = :line " +
                            "group by v.date " +
                            "order by v.date"
            ).setParameter("line", line).getResultList();

            for (Object[] visitsForDate : visitsEveryDay) {
                Date date = ((Date) (visitsForDate[0]));
                Long count = (Long) (visitsForDate[1]);

                ts.add(
                        new Day(
                                date.toLocalDate().getDayOfMonth(),
                                date.toLocalDate().getMonthValue(),
                                date.toLocalDate().getYear()
                        ),
                        count
                );

                textArea.append("  - " + date.toString() + " | посещений: " + count + "\n");
            }

            if (visitsEveryDay.size() > 0) {
                renderer.setSeriesPaint(
                        seriesId, line.getColor()
                );
                seriesId++;
            }

            ds.addSeries(ts);

            textArea.append("\n");
        }

        plot.setDataset(ds);
    }

    public void showForm() {
        infoPane.removeAll();
        plotPane.removeAll();

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font(textArea.getFont().getName(), Font.PLAIN, 16));
        JScrollPane scroll = new JScrollPane(textArea);
        infoPane.add(scroll);

        XYDataset ds = new DefaultXYDataset();

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Посещения станций",
                "Дата",
                "Количество посещений",
                ds
        );

        XYPlot plot = chart.getXYPlot();
        createDataSet(plot, textArea);

        ChartPanel panel = new ChartPanel(chart);
        plotPane.add(panel);

        textArea.setCaretPosition(0);

        pack();
        setVisible(true);
    }
}
