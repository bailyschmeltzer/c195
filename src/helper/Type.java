package helper;

public class Type {

    public String type;
    public int total;

    public Type(String type, int total) {
        this.type = type;
        this.total = total;
    }

    /**
     *
     * @return type
     */
    public String getType() {
        return type;
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
