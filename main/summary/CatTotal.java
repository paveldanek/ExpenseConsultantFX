package summary;

/**
 * A class representing an object which contains a single Transaction Category,
 * a total amount of money spent in that Category in a specific period, an average
 * amount per month (per 30 days) calculated from the total and period length, and
 * the percentage per the specific period of the total spent/ made (depending on
 * whether the total per period is negative/ positive respectively.
 */
public class CatTotal {

    private String categoryName = "";
    private double totalPerPeriod = 0.0;
    private double percentagePerPeriod = 0.0;
    private double averagePerMonth = 0.0;

    public CatTotal(String name, double total) {
        categoryName = name;
        totalPerPeriod = total;
    }

    public CatTotal(String name, double total, double percentage, double average) {
        categoryName = name;
        totalPerPeriod = total;
        percentagePerPeriod = percentage;
        averagePerMonth = average;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getTotalPerPeriod() {
        return totalPerPeriod;
    }

    public void setTotalPerPeriod(double totalPerPeriod) {
        this.totalPerPeriod = totalPerPeriod;
    }

    public void incrementTotalPerPeriod(double totalPerPeriod) {
        this.totalPerPeriod += totalPerPeriod;
    }

    public double getPercentagePerPeriod() {
        return percentagePerPeriod;
    }

    public void setPercentagePerPeriod(double percentagePerPeriod) {
        this.percentagePerPeriod = percentagePerPeriod;
    }

    public double getAveragePerMonth() {
        return averagePerMonth;
    }

    public void setAveragePerMonth(double averagePerMonth) {
        this.averagePerMonth = averagePerMonth;
    }

    /**
     * Calculates the percentage per period and average per month (per 30 dys) from the native
     * total per period, with the added "ingredients" of total expense/ total income (across
     * Categories), and the length of the specific period in days (periodDayAmount). If the
     * length of period is shorter than 30 days, than the average per month will be higher than
     * total per period.
     * @param totalExpense grand total spent in the period for the sum of all Categories
     * @param totalIncome grand total made/ gained in the period for the sum of all Categories
     * @param periodDayAmount the length of the period in days (amount of days in the period)
     */
    public void calculatePercentageAndAverage(double totalExpense, double totalIncome, long periodDayAmount) {
        if (totalPerPeriod <= 0.0) percentagePerPeriod = totalPerPeriod / totalExpense * 100;
        else percentagePerPeriod = totalPerPeriod / totalIncome * 100;
        averagePerMonth = totalPerPeriod / periodDayAmount * 30;
    }
}
