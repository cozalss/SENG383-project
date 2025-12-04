import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public static List<Task> taskList = new ArrayList<>();
    public static List<Wish> wishList = new ArrayList<>();
    public static String currentRole = "";

    public static void loadData() {
        taskList.clear();
        wishList.clear();
        loadTasks();
        loadWishes();
    }

    private static void loadTasks() {
        File file = new File("tasks.csv");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 7) {
                    taskList.add(new Task(
                            data[0].replace("|", ","),
                            data[1].replace("|", ","),
                            Integer.parseInt(data[2]),
                            data[3],
                            Integer.parseInt(data[4]),
                            data[5],
                            data[6]
                    ));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void loadWishes() {
        File file = new File("wishes.csv");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    wishList.add(new Wish(
                            data[0].replace("|", ","),
                            Integer.parseInt(data[1]),
                            data[2],
                            Integer.parseInt(data[3])
                    ));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("tasks.csv"))) {
            for (Task t : taskList) bw.write(t.toCSV() + "\n");
        } catch (IOException e) { e.printStackTrace(); }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("wishes.csv"))) {
            for (Wish w : wishList) bw.write(w.toCSV() + "\n");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void removeTask(int index) {
        if (index >= 0 && index < taskList.size()) {
            taskList.remove(index);
            saveData();
        }
    }

    public static void removeWish(int index) {
        if (index >= 0 && index < wishList.size()) {
            wishList.remove(index);
            saveData();
        }
    }

    public static int calculateTotalPoints() {
        int total = 0;
        for (Task t : taskList) {
            if ("APPROVED".equals(t.getStatus())) {
                total += t.getPoints();
            }
        }
        return total;
    }

    public static double calculateAverageRating() {
        int count = 0;
        int sum = 0;
        for (Task t : taskList) {
            if ("APPROVED".equals(t.getStatus()) && t.getRating() > 0) {
                sum += t.getRating();
                count++;
            }
        }
        return count == 0 ? 0.0 : (double) sum / count;
    }

    public static int calculateLevel() {
        int baseLevel = (calculateTotalPoints() / 100) + 1;
        double avg = calculateAverageRating();
        if (avg >= 4.5) {
            return baseLevel + 1;
        }
        return baseLevel;
    }

    public static int calculateProgress() {
        return calculateTotalPoints() % 100;
    }
}