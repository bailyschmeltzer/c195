package helper;

public class Country {

    private final int countryID;
    private final String countryName;


    public Country(int countryID, String countryName) {
        this.countryID = countryID;
        this.countryName = countryName;
    }

    /**
     *
     * @return countryID
     */
    public int getCountryID() { return countryID; }

    /**
     *
     * @return countryName
     */
    public String getCountryName() { return countryName; }
}
