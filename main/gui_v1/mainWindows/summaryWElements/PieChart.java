package gui_v1.mainWindows.summaryWElements;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class PieChart extends ApplicationFrame{

    public PieChart( String title ) {
        super( title );
        setContentPane(createDemoPanel( ));
    }

    private static PieDataset createDataset( ) {
        DefaultPieDataset dataset = new DefaultPieDataset( );
        dataset.setValue( "1" , new Double( 20 ) );
        dataset.setValue( "2" , new Double( 20 ) );
        dataset.setValue( "3" , new Double( 40 ) );
        dataset.setValue( "4" , new Double( 10 ) );
        dataset.setValue( "5" , new Double( 20 ) );
        dataset.setValue( "6" , new Double( 20 ) );
        dataset.setValue( "7" , new Double( 40 ) );
        dataset.setValue( "8" , new Double( 10 ) );
        dataset.setValue( "9" , new Double( 20 ) );
        dataset.setValue( "10" , new Double( 20 ) );
        dataset.setValue( "11" , new Double( 40 ) );
        dataset.setValue( "12" , new Double( 10 ) );
        dataset.setValue( "13" , new Double( 20 ) );
        dataset.setValue( "14" , new Double( 20 ) );
        dataset.setValue( "15" , new Double( 40 ) );
        dataset.setValue( "16" , new Double( 10 ) );
        dataset.setValue( "17" , new Double( 20 ) );
        dataset.setValue( "18" , new Double( 20 ) );
        dataset.setValue( "19" , new Double( 40 ) );
        dataset.setValue( "20" , new Double( 10 ) );
        dataset.setValue( "21" , new Double( 20 ) );
        dataset.setValue( "22" , new Double( 20 ) );
        dataset.setValue( "23" , new Double( 40 ) );
        dataset.setValue( "24" , new Double( 10 ) );
        return dataset;
    }

    private static JFreeChart createChart( PieDataset dataset ) {
        JFreeChart chart = ChartFactory.createPieChart(
                "Mobile Sales",   // chart title
                dataset,          // data
                false,             // include legend
                true,
                false);

        return chart;
    }

    public static JPanel createDemoPanel( ) {
        JFreeChart chart = createChart(createDataset( ) );
        return new ChartPanel( chart );
    }


    public static void main( String[ ] args ) {
        PieChart demo = new PieChart( "Mobile Sales" );
        demo.setSize( 400 , 367 );
        RefineryUtilities.centerFrameOnScreen( demo );
        demo.setVisible( true );
    }
}
