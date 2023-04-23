package gui_v1.mainWindows.advisingWElements;

import advising.Advising;
import entities.Transaction;
import gui_v1.automation.GUI_ElementCreator;
import gui_v1.settings.GUI_Settings_Variables;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Comparator;

import static gui_v1.automation.GUI_ElementCreator.newFont;

public class GUI_AdvisingInstanceP extends JPanel implements GUI_Settings_Variables {

    private boolean advisingEmpty = true;
    private Advising advice;
    private JPanel mainTablePanel, advisingPanel;
    private JTable mainTable, advisingTable;
    private TableItemArrayCreator tableArrays;
    private String[] mainColumnNames = {"Category", "Sums Per.1", "% of expense Per.1",
                                        "Sums Per.2", "% of expense Per.2", "% change"},
            m3ColumnNames = {"Sums (in 3m)", "% change (in 3m)"},
            m6ColumnNames = {"Sums (in 6m)", "% change (in 6m)"},
            y1ColumnNames = {"Sums (in 1y)", "% change (in 1y)"},
            y2ColumnNames = {"Sums (in 2y)", "% change (in 2y)"},
            y5ColumnNames = {"Sums (in 5y)", "% change (in 5y)"},
            y10ColumnNames = {"Sums (in 10y)", "% change (in 10y)"};

    // ------------------------------------------------------------------------------------------------
    private class TableItemArrayCreator {

        private String[][] mainItems, m3Items, m6Items, y1Items, y2Items, y5Items, y10Items, totals;

        public TableItemArrayCreator(Advising advice) {
            mainItems = new String[advice.getCatStats().size()][6];
            m3Items = new String[advice.getCatStats().size()][2];
            m6Items = new String[advice.getCatStats().size()][2];
            y1Items = new String[advice.getCatStats().size()][2];
            y2Items = new String[advice.getCatStats().size()][2];
            y5Items = new String[advice.getCatStats().size()][2];
            y10Items = new String[advice.getCatStats().size()][2];
            totals = new String[1][8];
            for (int i = 0; i < advice.getCatStats().size(); i++) {
                mainItems[i][0] = advice.getCatStats().get(i).getCategoryName();
                if (advice.getCatStats().get(i).getPastTotalPerPeriod()<0.0000) mainItems[i][1]="-"; else mainItems[i][1]="";
                mainItems[i][1] += String.format("$%.2f", Math.abs(advice.getCatStats().get(i).getPastTotalPerPeriod()), "");
                mainItems[i][2] = String.format("%.2f%%", advice.getCatStats().get(i).getPastPercentagePerPeriod(), "");
                if (advice.getCatStats().get(i).getLastTotalPerPeriod()<0.0000) mainItems[i][3]="-"; else mainItems[i][3]="";
                mainItems[i][3] += String.format("$%.2f", Math.abs(advice.getCatStats().get(i).getLastTotalPerPeriod()), "");
                mainItems[i][4] = String.format("%.2f%%", advice.getCatStats().get(i).getLastPercentagePerPeriod(), "");
                if (advice.getCatStats().get(i).getNetPercentagePerPeriodChange()<0.0000)
                    mainItems[i][5]="<html><font color='#4cbb17'>";
                else if (advice.getCatStats().get(i).getNetPercentagePerPeriodChange()>=5.0000)
                    mainItems[i][5]="<html><font color='#ff0000'>";
                else mainItems[i][5]="";
                mainItems[i][5] += String.format("%.2f%%", advice.getCatStats().get(i).getNetPercentagePerPeriodChange(), "");
                if (advice.getCatStats().get(i).getM3TotalperPeriod()<0.0000) m3Items[i][0]="-"; else m3Items[i][0]="";
                m3Items[i][0] += String.format("$%.2f", Math.abs(advice.getCatStats().get(i).getM3TotalperPeriod()), "");
                m3Items[i][1] = String.format("%.2f%%", advice.getCatStats().get(i).getM3NetPercentageChange(), "");
                if (advice.getCatStats().get(i).getM6TotalperPeriod()<0.0000) m6Items[i][0]="-"; else m6Items[i][0]="";
                m6Items[i][0] += String.format("$%.2f", Math.abs(advice.getCatStats().get(i).getM6TotalperPeriod()), "");
                m6Items[i][1] = String.format("%.2f%%", advice.getCatStats().get(i).getM6NetPercentageChange(), "");
                if (advice.getCatStats().get(i).getY1TotalperPeriod()<0.0000) y1Items[i][0]="-"; else y1Items[i][0]="";
                y1Items[i][0] += String.format("$%.2f", Math.abs(advice.getCatStats().get(i).getY1TotalperPeriod()), "");
                y1Items[i][1] = String.format("%.2f%%", advice.getCatStats().get(i).getY1NetPercentageChange(), "");
                if (advice.getCatStats().get(i).getY2TotalperPeriod()<0.0000) y2Items[i][0]="-"; else y2Items[i][0]="";
                y2Items[i][0] += String.format("$%.2f", Math.abs(advice.getCatStats().get(i).getY2TotalperPeriod()), "");
                y2Items[i][1] = String.format("%.2f%%", advice.getCatStats().get(i).getY2NetPercentageChange(), "");
                if (advice.getCatStats().get(i).getY5TotalperPeriod()<0.0000) y5Items[i][0]="-"; else y5Items[i][0]="";
                y5Items[i][0] += String.format("$%.2f", Math.abs(advice.getCatStats().get(i).getY5TotalperPeriod()), "");
                y5Items[i][1] = String.format("%.2f%%", advice.getCatStats().get(i).getY5NetPercentageChange(), "");
                if (advice.getCatStats().get(i).getY10TotalperPeriod()<0.0000) y10Items[i][0]="-"; else y10Items[i][0]="";
                y10Items[i][0] += String.format("$%.2f", Math.abs(advice.getCatStats().get(i).getY10TotalperPeriod()), "");
                y10Items[i][1] = String.format("%.2f%%", advice.getCatStats().get(i).getY10NetPercentageChange(), "");
            }
            totals[0][0] = "TOTAL";
            if (advice.getPastSumOfCatTotals()<0.0000) totals[0][1]="-";
            else totals[0][1]="";
            totals[0][1] += String.format("$%.2f", Math.abs(advice.getPastSumOfCatTotals()), "");
            totals[0][2] = String.format("%.2f%%", advice.getPastSumOfCatPercentages(), "");
            if (advice.getLastSumOfCatTotals()<0.0000) totals[0][3]="-";
            else totals[0][3]="";
            totals[0][3] += String.format("$%.2f", Math.abs(advice.getLastSumOfCatTotals()), "");
            totals[0][4] = String.format("%.2f%%", advice.getLastSumOfCatPercentages(), "");
            totals[0][5] = String.format("%.2f%%", advice.getNetSumOfCatPercentageChange(), "");
            totals[0][6] = "";
            totals[0][7] = "";
        }

        public String[][] getMainItems() {return mainItems;}
        public String[][] getM3Items() {return m3Items;}
        public String[][] getM6Items() {return m6Items;}
        public String[][] getY1Items() {return y1Items;}
        public String[][] getY2Items() {return y2Items;}
        public String[][] getY5Items() {return y5Items;}
        public String[][] getY10Items() {return y10Items;}
        public String[][] getTotals() {return totals;}
    }
    // ------------------------------------------------------------------------------------------------

    public GUI_AdvisingInstanceP(String acctNick, String from, String to, int scopeNumber) {
        if (acctNick==null || from==null || to==null) {
            setLayout(new GridBagLayout());
            JLabel label = GUI_ElementCreator.newFieldNameLabel(
                    "Insufficient account data to generate analysis or advice.");
            add(label);
            advice = null;
            advisingEmpty = true;
            return;
        }
        advisingEmpty = false;
        advice = new Advising(acctNick, Transaction.returnCalendarFromYYYYMMDD(from),
                Transaction.returnCalendarFromYYYYMMDD(to));
        tableArrays = new TableItemArrayCreator(advice);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        mainTablePanel = mainTablePanelCreator(tableArrays);
        JPanel totalsTablePanel = totalsTablePanelCreator(tableArrays);
        if (scopeNumber>0) switchScope(scopeNumber);
        c.gridwidth = 1;
        c.ipady = 250;
        c.ipadx = 900;
        c.gridx = 0; c.gridy = 0;
        add(mainTablePanel, c);
        c.gridwidth = 1;
        c.ipady = 0;
        c.ipadx = 900;
        c.gridx = 0; c.gridy = 1;
        add(totalsTablePanel, c);
        advisingPanel = advisingTextCreator();
        c.gridwidth = 1;
        c.ipady = 0;
        c.ipadx = 0;
        c.gridx = 0; c.gridy = 2;
        add(advisingPanel, c);
        if (advice.getCatStats().size()>0 &&
                advice.getCatStats().get(advice.getCatStats().size()-1).getNetPercentagePerPeriodChange()>=5) {
            JPanel catTablePanel = catListTablePanelCreator();
            c.gridwidth = 1;
            c.ipady = 10;
            c.ipadx = 200;
            c.gridx = 0;
            c.gridy = 3;
            add(catTablePanel, c);
        }
    }

    private JPanel mainTablePanelCreator(TableItemArrayCreator t) {
        JPanel output = new JPanel();
        output.setLayout(new BorderLayout());
        String[][] items = new String[t.getMainItems().length][8];
        for (int i = 0; i < items.length; i++) {
            System.arraycopy(t.getMainItems()[i], 0, items[i], 0, t.getMainItems()[i].length);
            System.arraycopy(t.getM3Items()[i], 0, items[i], t.getMainItems()[i].length, t.getM3Items()[i].length);
        }
        String[] columnNames = new String[mainColumnNames.length+ m3ColumnNames.length];
        System.arraycopy(mainColumnNames, 0, columnNames, 0, mainColumnNames.length);
        System.arraycopy(m3ColumnNames, 0, columnNames, mainColumnNames.length, m3ColumnNames.length);
        // ----------- set up main table ----------------------
        mainTable = new JTable(new DefaultTableModel(items, columnNames)) {
        public boolean isCellEditable(int row, int column) { return false; } };
        mainTable.setAutoCreateRowSorter(true);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(mainTable.getModel());
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.RIGHT);
        for (int i = 1; i < 8; i++) {
            mainTable.setRowSorter(getCustomRowSorter(sorter, mainTable.getModel(), i));
            mainTable.getColumnModel().getColumn(i).setCellRenderer(r);
        }
        mainTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        // ----------- put table together -----------------------
        output.add(mainTable.getTableHeader(), BorderLayout.PAGE_START);
        output.add(new JScrollPane(mainTable), BorderLayout.CENTER);
        return output;
}

    private JPanel totalsTablePanelCreator(TableItemArrayCreator t) {
        JPanel output = new JPanel();
        output.setLayout(new BorderLayout());
        String[] columnNames = new String[mainColumnNames.length+2];
        System.arraycopy(mainColumnNames, 0, columnNames, 0, mainColumnNames.length);
        columnNames[columnNames.length-1] = " ";
        columnNames[columnNames.length-2] = "";
        JTable totalsTable = new JTable(new DefaultTableModel(t.getTotals(), columnNames)) {
            public boolean isCellEditable(int row, int column) { return false; } };
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.RIGHT);
        for (int i = 1; i < 6; i++) {
            totalsTable.getColumnModel().getColumn(i).setCellRenderer(r);
        }
        totalsTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        // ----------- put table together -----------------------
        output.add(totalsTable.getTableHeader(), BorderLayout.PAGE_START);
        output.add(totalsTable, BorderLayout.CENTER);
        return output;
    }

    private TableRowSorter<TableModel> getCustomRowSorter(TableRowSorter<TableModel> sorter, TableModel m, int column){
        sorter.setComparator(column, new Comparator<String>() {
            @Override
            public int compare(String c1, String c2) {
                if (c1.startsWith("<")) {
                    String[] temp = c1.split(">");
                    c1 = temp[2];
                }
                if (c2.startsWith("<")) {
                    String[] temp = c2.split(">");
                    c2 = temp[2];
                }
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

    private JPanel advisingTextCreator() {
        JPanel output = new JPanel();
        output.setLayout(new GridBagLayout());
        String amount1, amount2, percentage;
        String a1Color, a2Color;
        if (advice.getPastNetIncome()<0.0) {
            amount1 = "-";
            a1Color = "#ff0000"; // red
        } else {
            amount1 = "";
            a1Color = "#4cbb17"; // green
        }
        amount1 += String.format("$%.2f", Math.abs(advice.getPastNetIncome()), "");
        if (advice.getLastNetIncome()<0.0) {
            amount2 = "-";
            a2Color = "#ff0000";
        } else {
            amount2 = "";
            a2Color = "#4cbb17";
        }
        amount2 += String.format("$%.2f", Math.abs(advice.getLastNetIncome()), "");
        percentage = String.format("%.2f%%", advice.getNetIncomeChangeInPercent(), "");
        String line1 = "<html>Your net income in the Period 1 was ";
        line1 += "<font color='"+a1Color+"'>"+amount1+"<font color='#ff'> and in the Period 2 was ";
        line1 += "<font color='"+a2Color+"'>"+amount2+"<font color='#ff'>,</html>";
        String line2 = "<html>which is ";
        String line3;
        if (advice.getNetIncomeChangeInPercent()<0.0) {
            line2 += "a <font color='#ff0000'>decrease of "+ percentage+"<font color='#ff'>. "+
                    "<u>WATCH OUT!!! LOWER YOUR SPENDINGS!!!</u></html>";
            line3 = "You NEED to DECREASE your spending mainly in categories:";
        }
        else {
            line2 += "an <font color='#4cbb17'>increase of "+percentage+"<font color='#ff'>.";
            if (advice.getNetIncomeChangeInPercent()>0.0 || advice.getLastNetIncome()>0.0) {
                line2 += " <u>GOOD JOB!!!</u>";
                if (advice.getLastNetIncome()>0.0) line2 += " YOU ARE ABLE TO SAVE UP!!!</u></html>";
                else line2 +="</html>";
            }
            line3 = "You could consider lowering your spending in categories like:";
        }
        JLabel label1 = new JLabel(line1, SwingConstants.CENTER);
        label1.setFont(newFont(label1.getFont(), txtSize_Regular));
        label1.setForeground(clrF_InfoMsgs);
        JLabel label2 = new JLabel(line2, SwingConstants.CENTER);
        label2.setFont(newFont(label2.getFont(), txtSize_Regular));
        label2.setForeground(clrF_InfoMsgs);
        JLabel label3 = new JLabel(line3, SwingConstants.CENTER);
        label3.setFont(newFont(label3.getFont(), txtSize_Regular));
        label3.setForeground(clrF_InfoMsgs);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 1;
        c.gridx = 0; c.gridy = 0;
        output.add(label1, c);
        c.gridwidth = 1;
        c.gridx = 0; c.gridy = 1;
        output.add(label2, c);
        if (advice.getCatStats().size()>0 &&
                advice.getCatStats().get(advice.getCatStats().size()-1).getNetPercentagePerPeriodChange()>=5) {
            c.gridwidth = 1;
            c.gridx = 0; c.gridy = 2;
            output.add(label3, c);
        }
        return output;
    }

    private JPanel catListTablePanelCreator() {
        JPanel output = new JPanel();
        int counter = 0;
        for (int i = 0; i < advice.getCatStats().size(); i++) {
            if (advice.getCatStats().get(i).getNetPercentagePerPeriodChange()>=5) counter++;
        }
        String[][] resultList = new String[counter][1];
        counter = 0;
        for (int i = advice.getCatStats().size()-1; i >= 0; i--) {
            if (advice.getCatStats().get(i).getNetPercentagePerPeriodChange()>=5) {
                resultList[counter][0] = "- "+advice.getCatStats().get(i).getCategoryName();
                counter++;
            }
        }
        output.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        String[] colNames = {""};
        JTable catTable = new JTable(new DefaultTableModel(resultList, colNames));
        c.gridwidth = 1;
        c.ipady = 50;
        c.ipadx = 200;
        c.gridx = 0; c.gridy = 0;
        output.add(new JScrollPane(catTable), c);
        return output;
    }

    public boolean isEmpty() {
        return advisingEmpty;
    }

    public void switchScope(int scopeNumber) {
        String[] columnNames;
        String[][] futureItems;
        switch (scopeNumber) {
            case 1:
                columnNames = m6ColumnNames;
                futureItems = tableArrays.getM6Items();
                break;
            case 2:
                columnNames = y1ColumnNames;
                futureItems = tableArrays.getY1Items();
                break;
            case 3:
                columnNames = y2ColumnNames;
                futureItems = tableArrays.getY2Items();
                break;
            case 4:
                columnNames = y5ColumnNames;
                futureItems = tableArrays.getY5Items();
                break;
            case 5:
                columnNames = y10ColumnNames;
                futureItems = tableArrays.getY10Items();
                break;
            default:
                columnNames = m3ColumnNames;
                futureItems = tableArrays.getM3Items();
        }
        //JPanel output = new JPanel();
        //output.setLayout(new BorderLayout());
        // ----- table header update ----------------
        JTableHeader th = mainTable.getTableHeader();
        TableColumnModel tcm = th.getColumnModel();
        TableColumn tc0 = tcm.getColumn(6);
        tc0.setHeaderValue(columnNames[0]);
        TableColumn tc1 = tcm.getColumn(7);
        tc1.setHeaderValue(columnNames[1]);
        th.repaint();
        // ------- table update ---------------------
        for (int i = 0; i < futureItems.length; i++) {
            mainTable.setValueAt(futureItems[i][0], i, 6);
            mainTable.setValueAt(futureItems[i][1], i, 7);
        }
    }

    @Override
    public Component getComponent() {
        return null;
    }
}
