package helper;

public class Reports {

    private final int count;
    private final String name;

    public Reports(String name, int count) {
        this.name = name;
        this.count = count;
    }

    /**
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return count
     */
    public int getCount() {
        return count;
    }
}
