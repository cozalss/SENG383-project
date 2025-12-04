import java.io.Serializable;

public class Wish implements Serializable {
    private String name;
    private int cost;
    private String status;
    private int requiredLevel;

    public Wish(String name, int cost, String status, int requiredLevel) {
        this.name = name;
        this.cost = cost;
        this.status = status;
        this.requiredLevel = requiredLevel;
    }

    public String toCSV() {
        return name.replace(",", "|") + "," + cost + "," + status + "," + requiredLevel;
    }

    public String getName() { return name; }
    public int getCost() { return cost; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getRequiredLevel() { return requiredLevel; }
}