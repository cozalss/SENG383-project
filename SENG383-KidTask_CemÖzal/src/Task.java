import java.io.Serializable;

public class Task implements Serializable {
    private String title;
    private String description;
    private int points;
    private String status;
    private int rating;
    private String dueDate;
    private String frequency;

    public Task(String title, String description, int points, String status, int rating, String dueDate, String frequency) {
        this.title = title;
        this.description = description;
        this.points = points;
        this.status = status;
        this.rating = rating;
        this.dueDate = dueDate;
        this.frequency = frequency;
    }

    public String toCSV() {
        return title.replace(",", "|") + "," +
                description.replace(",", "|") + "," +
                points + "," +
                status + "," +
                rating + "," +
                (dueDate == null ? "" : dueDate) + "," +
                (frequency == null ? "ONCE" : frequency);
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getPoints() { return points; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getDueDate() { return dueDate; }
    public String getFrequency() { return frequency; }
}