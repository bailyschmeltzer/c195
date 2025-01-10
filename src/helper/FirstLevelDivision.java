package helper;

public class FirstLevelDivision {

    private final int divID;
    private final String divName;
    public int countryID;

    public FirstLevelDivision(int divID, String divName, int countryID) {
        this.divID = divID;
        this.divName = divName;
        this.countryID = countryID;
    }

    /**
     *
     * @return divID
     */
    public int getDivID() {
        return divID;
    }

    /**
     *
     * @return divName
     */
    public String getDivName() {
        return divName;
    }

    /**
     *
     * @return countryID
     */
    public int getCountryID() {
        return countryID;
    }
}
