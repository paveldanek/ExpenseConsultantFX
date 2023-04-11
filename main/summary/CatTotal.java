package summary;

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

    public void calculatePercentageAndAverage(double totalExpense, double totalIncome, long periodDayAmount) {
        if (totalPerPeriod <= 0.0) percentagePerPeriod = totalPerPeriod / totalExpense * 100;
        else percentagePerPeriod = totalPerPeriod / totalIncome * 100;
        averagePerMonth = totalPerPeriod / periodDayAmount * 30;
    }
}
