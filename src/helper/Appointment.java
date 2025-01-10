package helper;

import java.time.LocalDateTime;


public class Appointment {
    private final int apptID;
    private final String apptTitle;
    private final String apptDesc;
    private final String apptLocation;
    private final String apptType;
    private final LocalDateTime start;
    private final LocalDateTime end;
    public int custID;
    public int userID;
    public int contactID;

    public Appointment(int apptID, String apptTitle, String apptDesc,
                        String apptLocation, String apptType, LocalDateTime start, LocalDateTime end, int custID,
                        int userID, int contactID) {
        this.apptID = apptID;
        this.apptTitle = apptTitle;
        this.apptDesc = apptDesc;
        this.apptLocation = apptLocation;
        this.apptType = apptType;
        this.start = start;
        this.end = end;
        this.custID = custID;
        this.userID = userID;
        this.contactID = contactID;
    }


    /**
     *
     * @return apptID
     */
    public int getApptID() { return apptID; }


    /**
     *
     * @return appTitle
     */
    public String getAppTitle() { return apptTitle; }

    /**
     *
     * @return apptDesc
     */
    public String getApptDesc() {

        return apptDesc;
    }

    /**
     *
     * @return apptLocation
     */
    public String getApptLocation() { return apptLocation; }

    /**
     *
     * @return apptType
     */
    public String getApptType() { return apptType; }


    /**
     *
     * @return start
     */
    public LocalDateTime getStart() { return start; }

    /**
     *
     * @return end
     */
    public LocalDateTime getEnd() { return end; }

    /**
     *
     * @return custID
     */
    public int getCustID () { return custID; }

    /**
     *
     * @return userID
     */
    public int getUserID() { return userID; }

    /**
     *
     * @return contactID
     */
    public int getContactID() { return contactID; }

}
