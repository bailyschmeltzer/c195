package helper;

public class Contact {

    private String contName;
    public int contID;
    public String contEmail;

    public Contact(int contID, String contName, String contEmail) {
        this.contID = contID;
        this.contName = contName;
        this.contEmail = contEmail;
    }

    /**
     *
     * @return contID
     */
    public int getId() { return contID; }

    /**
     *
     * @return contName
     */
    public  String getName() { return contName; }

    /**
     *
     * @return contEmail
     */
    //may be able to be deleted
    public String getEmail() { return contEmail; }
}

