package advising;

public class CatStats {

    private String categoryName = "";
    private double pastTotalPerPeriod = 0.0;
    private double pastPercentagePerPeriod = 0.0;
    private double lastTotalPerPeriod = 0.0;
    private double lastPercentagePerPeriod = 0.0;
    private double netPercentagePerPeriodChange = 0.0;
    private double m3TotalperPeriod = 0.0;
    private double m6TotalperPeriod = 0.0;
    private double y1TotalperPeriod = 0.0;
    private double y2TotalperPeriod = 0.0;
    private double y5TotalperPeriod = 0.0;
    private double y10TotalperPeriod = 0.0;

    // ADVISING BASED OFF OF THESE PERCENTAGE CHANGES:
    private double m3NetPercentageChange = 0.0;
    private double m6NetPercentageChange = 0.0;
    private double y1NetPercentageChange = 0.0;
    private double y2NetPercentageChange = 0.0;
    private double y5NetPercentageChange = 0.0;
    private double y10NetPercentageChange = 0.0;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getPastTotalPerPeriod() {
        return pastTotalPerPeriod;
    }

    public void setPastTotalPerPeriod(double pastTotalPerPeriod) {
        this.pastTotalPerPeriod = pastTotalPerPeriod;
    }

    public double getLastTotalPerPeriod() {
        return lastTotalPerPeriod;
    }

    public void setLastTotalPerPeriod(double lastTotalPerPeriod) {
        this.lastTotalPerPeriod = lastTotalPerPeriod;
    }

    public double getPastPercentagePerPeriod() {
        return pastPercentagePerPeriod;
    }

    public double getLastPercentagePerPeriod() {
        return lastPercentagePerPeriod;
    }

    public double getNetPercentagePerPeriodChange() {
        return netPercentagePerPeriodChange;
    }

    public double getM3TotalperPeriod() {
        return m3TotalperPeriod;
    }

    public double getM3NetPercentageChange() {
        return m3NetPercentageChange;
    }

    public double getM6TotalperPeriod() {
        return m6TotalperPeriod;
    }

    public double getM6NetPercentageChange() {
        return m6NetPercentageChange;
    }

    public double getY1TotalperPeriod() {
        return y1TotalperPeriod;
    }

    public double getY1NetPercentageChange() {
        return y1NetPercentageChange;
    }

    public double getY2TotalperPeriod() {
        return y2TotalperPeriod;
    }

    public double getY2NetPercentageChange() {
        return y2NetPercentageChange;
    }

    public double getY5TotalperPeriod() {
        return y5TotalperPeriod;
    }

    public double getY5NetPercentageChange() {
        return y5NetPercentageChange;
    }

    public double getY10TotalperPeriod() {
        return y10TotalperPeriod;
    }

    public double getY10NetPercentageChange() {
        return y10NetPercentageChange;
    }

    public void calculateCategoryStats(double pastTotalSpent, double lastTotalSpent, int numberOfDaysPassed) {
        // numberOfDaysPassed is NOT the difference between the beginning of past period and ending of last period!
        // IT IS THE DIFFERENCE BETWEEN ENDING OF PAST PERIOD AND ENDING OF LAST PERIOD!!!
        pastPercentagePerPeriod = pastTotalPerPeriod/pastTotalSpent*100;
        lastPercentagePerPeriod = lastTotalPerPeriod/lastTotalSpent*100;
        netPercentagePerPeriodChange = lastPercentagePerPeriod-pastPercentagePerPeriod;
        m3TotalperPeriod = lastTotalPerPeriod+((lastTotalPerPeriod-pastTotalPerPeriod)/numberOfDaysPassed*91);
        m3NetPercentageChange = netPercentagePerPeriodChange/numberOfDaysPassed*91;
        m6TotalperPeriod = lastTotalPerPeriod+((lastTotalPerPeriod-pastTotalPerPeriod)/numberOfDaysPassed*183);
        m6NetPercentageChange = netPercentagePerPeriodChange/numberOfDaysPassed*183;
        y1TotalperPeriod = lastTotalPerPeriod+((lastTotalPerPeriod-pastTotalPerPeriod)/numberOfDaysPassed*365);
        y1NetPercentageChange = netPercentagePerPeriodChange/numberOfDaysPassed*365;
        y2TotalperPeriod = lastTotalPerPeriod+((lastTotalPerPeriod-pastTotalPerPeriod)/numberOfDaysPassed*730);
        y2NetPercentageChange = netPercentagePerPeriodChange/numberOfDaysPassed*730;
        y5TotalperPeriod = lastTotalPerPeriod+((lastTotalPerPeriod-pastTotalPerPeriod)/numberOfDaysPassed*1826);
        y5NetPercentageChange = netPercentagePerPeriodChange/numberOfDaysPassed*1826;
        y10TotalperPeriod = lastTotalPerPeriod+((lastTotalPerPeriod-pastTotalPerPeriod)/numberOfDaysPassed*3653);
        y10NetPercentageChange = netPercentagePerPeriodChange/numberOfDaysPassed*3653;
        if (m3TotalperPeriod>0.0) { m3TotalperPeriod=0.0; m3NetPercentageChange=0.0; }
        if (m6TotalperPeriod>0.0) { m6TotalperPeriod=0.0; m6NetPercentageChange=0.0; }
        if (y1TotalperPeriod>0.0) { y1TotalperPeriod=0.0; y1NetPercentageChange=0.0; }
        if (y2TotalperPeriod>0.0) { y2TotalperPeriod=0.0; y2NetPercentageChange=0.0; }
        if (y5TotalperPeriod>0.0) { y5TotalperPeriod=0.0; y5NetPercentageChange=0.0; }
        if (y10TotalperPeriod>0.0) { y10TotalperPeriod=0.0; y10NetPercentageChange=0.0; }
    }
}
