package gui_v1.mainWindows.summaryWElements;

import entities.Transaction;
import gui_v1.automation.GUI_ElementCreator;
import gui_v1.mainWindows.recordsWElements.RecordsTable_CustomMethods;
import gui_v1.settings.GUI_Settings_Variables;
import main_logic.PEC;
import org.jfree.ui.RefineryUtilities;
import summary.Summary;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Comparator;

public class GUI_SummaryP extends JPanel implements GUI_Settings_Variables {

    private JPanel expensePieChartPanel;
    private JPanel expenseTablePanel;
    private JPanel incomePieChartPanel;
    private JPanel incomeTablePanel;
    private double[] expenseTotals;
    private double[] incomeTotals;
    private JTable expenseTable;
    private JTable incomeTable;

    public GUI_SummaryP(String acctNick, String from, String to) {
        // ----------- refreshing transaction and summary tables in db -------
        PEC.instance().uploadCurrentList();
        // ----------------------- downloading Summary ---------------------
        Summary summary = Summary.downloadSummary(acctNick, Transaction.returnCalendarFromYYYYMMDD(from),
                Transaction.returnCalendarFromYYYYMMDD(to));
        // ------------- creating arrays for pie charts and tables ----------
        int expenseCount = 0, incomeCount = 0;
        for (int i = 0; i < summary.getCatTotals().size(); i++) {
            if (summary.getCatTotals().get(i).getTotalPerPeriod()<0.0) expenseCount++;
            else incomeCount++;
        }
        expenseTotals = new double[expenseCount];
        incomeTotals = new double[incomeCount];
        String[][] expenseTableItems = new String[expenseCount][5];
        String[][] incomeTableItems = new String[incomeCount][5];
        String[] columnNames = {"No.", "Category", "Total per period", "Average per month", "% of sum of totals"};
        int eCounter = 0, iCounter = 0;
        for (int i = 0; i < summary.getCatTotals().size(); i++) {
            if (summary.getCatTotals().get(i).getTotalPerPeriod()<0.0) {
                expenseTableItems[eCounter][0] = String.valueOf(eCounter+1);
                expenseTableItems[eCounter][1] = summary.getCatTotals().get(i).getCategoryName();
                expenseTableItems[eCounter][2] = String.format("-$%.2f", Math.abs(summary.getCatTotals().get(i).getTotalPerPeriod()));
                expenseTableItems[eCounter][3] = String.format("-$%.2f", Math.abs(summary.getCatTotals().get(i).getAveragePerMonth()));
                expenseTableItems[eCounter][4] = String.format("%.2f%%", summary.getCatTotals().get(i).getPercentagePerPeriod());
                expenseTotals[eCounter] = Math.abs(summary.getCatTotals().get(i).getTotalPerPeriod());
                eCounter++;
            }
            else {
                incomeTableItems[iCounter][0] = String.valueOf(iCounter+1);
                incomeTableItems[iCounter][1] = summary.getCatTotals().get(i).getCategoryName();
                incomeTableItems[iCounter][2] = String.format("$%.2f", summary.getCatTotals().get(i).getTotalPerPeriod());
                incomeTableItems[iCounter][3] = String.format("$%.2f", summary.getCatTotals().get(i).getAveragePerMonth());
                incomeTableItems[iCounter][4] = String.format("%.2f%%", summary.getCatTotals().get(i).getPercentagePerPeriod());
                incomeTotals[iCounter] = summary.getCatTotals().get(i).getTotalPerPeriod();
                iCounter++;
            }
        }
        // ------------------------ populating the main JPanel with 5 elements ----
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        // --------------- title ----------------
        JPanel title = new JPanel();
        title.setLayout(new GridLayout(2,1));
        JLabel titleText = GUI_ElementCreator.newFieldNameLabel(
                "Summary for "+summary.getAccountNick()+" from period starting "+
                        Transaction.returnYYYYMMDDFromCalendar(summary.getPeriodBegin())+
                        " and ending "+Transaction.returnYYYYMMDDFromCalendar(summary.getPeriodEnd())+".");
        JLabel timeStamp = GUI_ElementCreator.newFieldNameLabel("(created on "+summary.getTimeStampString()+")");
        title.add(titleText);
        title.add(timeStamp);
        c.gridwidth = 2;
        c.gridx = 0; c.gridy = 0;
        add(title, c);
        // -------------- expense pie chart ------
        expensePieChartPanel = PieChart.createChartPanel("EXPENSES", expenseTotals);
        c.gridwidth = 1;
        c.ipady = 250;
        c.ipadx = 300;
        c.gridx = 0; c.gridy = 1;
        add(expensePieChartPanel, c);
        // ---------- expense table ----------------
        expenseTablePanel = tablePanelCreator(expenseTableItems, columnNames);
        c.gridwidth = 1;
        c.ipady = 250;
        c.ipadx = 650;
        c.gridx = 1; c.gridy = 1;
        add(expenseTablePanel, c);
        // ---------------- income pie chart -------
        incomePieChartPanel = PieChart.createChartPanel("INCOMES", incomeTotals);
        c.gridwidth = 1;
        c.ipady = 250;
        c.ipadx = 300;
        c.gridx = 0; c.gridy = 2;
        add(incomePieChartPanel, c);
        // ---------------- income table ------------
        incomeTablePanel = tablePanelCreator(incomeTableItems, columnNames);
        c.gridwidth = 1;
        c.ipady = 250;
        c.ipadx = 650;
        c.gridx = 1; c.gridy = 2;
        add(incomeTablePanel, c);
    }

    private JPanel tablePanelCreator(String[][] items, String[] columnNames) {
        JPanel output = new JPanel();
        if (items==null) items = new String[0][0];
        if (columnNames==null) columnNames = new String[0];
        output.setLayout(new BorderLayout());
        JTable table = new JTable(new DefaultTableModel(items, columnNames)) {
            public boolean isCellEditable(int row, int column) { return false; } };
        table.setAutoCreateRowSorter(true);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
        table.setRowSorter(getCustomRowSorter(sorter, table.getModel(), 0));
        table.setRowSorter(getCustomRowSorter(sorter, table.getModel(), 2));
        table.setRowSorter(getCustomRowSorter(sorter, table.getModel(), 3));
        table.setRowSorter(getCustomRowSorter(sorter, table.getModel(), 4));
        table.getColumnModel().getColumn(0).setPreferredWidth(5);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(r);
        table.getColumnModel().getColumn(3).setCellRenderer(r);
        table.getColumnModel().getColumn(4).setCellRenderer(r);
        output.add(table.getTableHeader(), BorderLayout.PAGE_START);
        output.add(new JScrollPane(table), BorderLayout.CENTER);
        return output;
    }

    private TableRowSorter<TableModel> getCustomRowSorter(TableRowSorter<TableModel> sorter, TableModel m, int column){
        sorter.setComparator(column, new Comparator<String>() {
            @Override
            public int compare(String c1, String c2) {
                Double n1 = Double.parseDouble(c1.replaceAll("[, $ %]",""));
                Double n2 = Double.parseDouble(c2.replaceAll("[, $ %]",""));
                if (n1 < n2) {
                    return -1;
                } else if (n1 > n2) {
                    return 1;
                }
                return 0;
            }
        });
        return sorter;
    }

    @Override
    public Component getComponent() {
        return null;
    }
}
