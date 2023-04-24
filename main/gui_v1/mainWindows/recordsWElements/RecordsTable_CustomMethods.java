package gui_v1.mainWindows.recordsWElements;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.Comparator;
import java.util.Date;
import java.lang.Integer;

/**
 * @author Andrey Y
 * @author  Pavel
 * @author Sam
 * @version 0.0.2
 * @serial 030320230735
 *  This class is place for accumulating all custom methods needed
 *  for SPAM Teams Expense Consultant App to Run, and showing
 *  correct results.
 *
 */
public class RecordsTable_CustomMethods {
    private static RecordsTable_CustomMethods instance=null;
    private RecordsTable_CustomMethods(){
    }
    public static RecordsTable_CustomMethods getInstance(){
        if(instance ==null){
            instance = new RecordsTable_CustomMethods();
        }
        return instance;
    }

    /**
     * @param m  -- table model of table to set custom row sorting
     * @column -- column to which apply this sorting
     * @return -- custom row sorting for specified column of tables
     *  To set custom sorting of rows by some column tablee need this satment
     *   table.setRowSorter(getCustomRowSorter(table.getModel()), culumnNum);
     *
     */
    public TableRowSorter getCustomRowSorter_v1(TableModel m, int column){
        var sorter = new TableRowSorter<TableModel>(m);
        sorter.setComparator(column, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
//                System.out.println(o1+" "+o2);
                if (Double.parseDouble(o1.substring(1)) <= Double.parseDouble(o2.substring(1))) {
                    return -1;
                } else if (Double.parseDouble(o1.substring(1)) > Double.parseDouble(o2.substring(1))) {
                    return 1;
                }
                return 0;
            }
        });
        return sorter;
    }
    /**
     * @param m -- table model of table to set custom row sorting
     * @column -- column to which apply this sorting
     * @return -- custom row sorting for specified column of tables
     *
     * This custom method is for performing Transaction Table Rows Sorting by
     * date column.
     *
     *
     */
    public TableRowSorter<TableModel> getCustomRowSorterByDate(TableModel m, int column){
        var sorter = new TableRowSorter<TableModel>(m);
        sorter.setComparator(column, new Comparator<String>() {
            @Override
            public int compare(String c1, String c2) {
                Date d1;
                Date d2;
                String[] date1arr = c1.split("/");
                String[] date2arr = c2.split("/");
                d1 = new Date(Integer.parseInt(date1arr[0]),Integer.parseInt(date1arr[1]),Integer.parseInt(date1arr[2]));
                d2 = new Date(Integer.parseInt(date2arr[0]),Integer.parseInt(date2arr[1]),Integer.parseInt(date2arr[2]));
                int answr = d1.compareTo(d2);
                if(answr==0){
                    return 0;
                }else if (answr<0){
                    return -1;
                }else {
                   return 1;
                }
            }
        });
        return sorter;
    }
    /**
     * @param m --  table model of table to set custom row sorting
     * @return -- custom row sorting for specified column of tables
     * @return -- table sorted object with specified sorting
     *
     * This  method is providing custom amount sorting of specified column by its number.
     */
    public TableRowSorter<TableModel> getCustomRowSorter(TableModel m, int column){
        var sorter = new TableRowSorter<TableModel>(m);
        sorter.setComparator(column, new Comparator<String>() {
            @Override
            public int compare(String c1, String c2) {
                Double n1 = null;
                Double n2 = null;
//                o(c1 + " c "+c2);
                if(c1.startsWith("$")){
                    n1 = Double.parseDouble((c1.substring(1)).replace(",",""));
                }else if(c1.startsWith("-")){
                    n1 = Double.parseDouble((c1.substring(2)).replace(",",""));
                    n1*= -1;
                }
                if(c2.startsWith("$")){
                    n2 = Double.parseDouble((c2.substring(1)).replace(",",""));
                }else if(c2.startsWith("-")){
                    n2 = Double.parseDouble((c2.substring(2)).replace(",",""));
                    n2*= -1;
                }
//                o(n1 + " "+ n2);
                if (n1==null) n1 = 0.0;
                if (n2==null) n2 = 0.0;
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
    public  TableRowSorter<TableModel> getCustomRowSorterByDate2(TableModel m, int column){
        var sorter = new TableRowSorter<TableModel>(m);
        sorter.setComparator(column, new Comparator<String>() {
            @Override
            public int compare(String c1, String c2) {
                Double n1 = null;
                Double n2 = null;
                String[] date1arr = c1.split("/");
                String[] date2arr = c2.split("/");
                int y1 = Integer.parseInt(date1arr[0]);
                int m1 = Integer.parseInt(date1arr[1]);
                int d1 = Integer.parseInt(date1arr[2]);
                int y2 = Integer.parseInt(date2arr[0]);
                int m2 = Integer.parseInt(date2arr[1]);
                int d2 = Integer.parseInt(date2arr[2]);
                if((y1 == y2) && (m1 == m2) && (d1 == d2)){
                    return 0;
                }else if((y1 == y2) && (m1 == m2) && (d1 < d2)) {
                    return -1;
                }else if((y1 == y2) && (m1 == m2) && (d1 > d2)){
                    return 1;
                }else if((y1 == y2) && (m1 < m2)){
                    return -1;
                }else if((y1 == y2) && (m1 > m2)){
                    return 1;
                }else if(y1 < y2){
                    return -1;
                }else{
                    return 1;
                }
            }
        });
        return sorter;
    }
//    private static void o(Object o){
//        System.out.println(o+"");
//    }
}
