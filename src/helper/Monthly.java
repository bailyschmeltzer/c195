package helper;

public class Monthly {

    public String month;
    public int total;

    public Monthly(String month, int total) {
        this.month = month;
        this.total = total;
    }

    /**
     *
     * @return month
     */
    //may be able to be deleted
    public String getMonth() {
        return month;
    }

    /**
     *
     * @return total
     */
    //may be able to be deleted
    public int getTotal() {
        return total;
    }
}
