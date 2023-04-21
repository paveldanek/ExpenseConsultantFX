package gui_v1.mainWindows.summaryWElements;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;

public class PieChart {

    private static PieDataset createDataset(double[] data) {
        DefaultPieDataset dataset = new DefaultPieDataset( );
        for (int i = 0; i < data.length; i++) {
            dataset.setValue(String.valueOf(i+1), new Double(data[i]));
        }
        return dataset;
    }

    private static JFreeChart createChart(String title, PieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
                title, dataset, false, true, false);
        return chart;
    }

    public static JPanel createChartPanel(String title, double[] data) {
        JFreeChart chart = createChart(title, createDataset(data));
        ChartPanel cp = new ChartPanel(chart);
        //cp.setMaximumSize(new Dimension(600, 600));
        cp.setVisible(true);
        return cp;
    }

    /*
    public static void main( String[ ] args ) {
        PieChart demo = new PieChart( "Mobile Sales" );
        demo.setSize( 400 , 367 );
        RefineryUtilities.centerFrameOnScreen( demo );
        demo.setVisible( true );
    }
    */
}
