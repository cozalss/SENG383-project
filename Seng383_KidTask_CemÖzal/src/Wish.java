import java.io.Serializable;

public class Wish implements Serializable {
    private String name;
    private int cost;
    private String status; // REQUESTED, GRANTED, REJECTED

    public Wish(String name, int cost, String status) {
        this.name = name;
        this.cost = cost;
        this.status = status;
    }

    public String toCSV() {
        return name.replace(",", "|") + "," + cost + "," + status;
    }

    // --- GETTER METODLARI ---
    public String getName() { return name; }
    public int getCost() { return cost; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}