package helper;


public class Customer {
    private String divName;
    private int custID;
    private String custName;
    private String custAddress;
    private String custZip;
    private String custPhone;
    private int divID;

    public Customer(int custID, String custName, String custAddress, String custZip,
                     String custPhone, int divID, String divName) {

        this.custID = custID;
        this.custName = custName;
        this.custAddress = custAddress;
        this.custZip = custZip;
        this.custPhone = custPhone;
        this.divID = divID;
        this.divName = divName;
    }

    /**
     *
     * @return custID
     */
    public Integer getCustID() { return custID; }

    /**
     * Sets the customer ID
     * @param custID the customer ID
     */
    public void setCustID(Integer custID) { this.custID = custID; }

    /**
     *
     * @return custName
     */
    public String getCustName() { return custName; }

    /**
     * Sets the customer name
     * @param custName the customer name
     */
    //may be able to be deleted
    public void setCustName(String custName) { this.custName = custName; }

    /**
     *
     * @return custAddress
     */
    public String getCustAddress() { return custAddress; }

    /**
     * Sets the customer address
     * @param custAddress the customer address
     */
    //may be able to be deleted
    public void setCustAddress(String custAddress) { this.custAddress = custAddress; }

    /**
     *
     * @return custZip
     */
    public String getCustZip() { return custZip; }

    /**
     * Sets the customer zip code
     * @param zip the customer zip code
     */
    //may be able to be deleted
    public void setCustZip(String zip) { this.custZip = zip; }

    /**
     *
     * @return custPhone
     */
    public String getCustPhone() { return custPhone; }

    /**
     * Sets the customer phone number
     * @param custPhone customer phone number
     */
    //may be able to be deleted
    public void setCustPhone(String custPhone) { this.custPhone = custPhone; }

    /**
     *
     * @return divID
     */
    public Integer getDivID() { return divID; }

    /**
     *
     * @return divName
     */
    //may be able to be deleted
    public String getDivName() { return divName; }

    /**
     * Sets the customer first level division ID
     * @param divID the customer first level division ID
     */
    //may be able to be deleted
    public void setDivID(Integer divID) { this.divID = divID; }

}
