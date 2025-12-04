import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public static List<Task> taskList = new ArrayList<>();
    public static List<Wish> wishList = new ArrayList<>();

    // Tracks who is currently logged in
    public static String currentRole = "";

    // --- File Operations ---
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
                if (data.length >= 5) {
                    taskList.add(new Task(
                            data[0].replace("|", ","),
                            data[1].replace("|", ","),
                            Integer.parseInt(data[2]),
                            data[3],
                            Integer.parseInt(data[4])
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
                if (data.length >= 3) {
                    wishList.add(new Wish(
                            data[0].replace("|", ","),
                            Integer.parseInt(data[1]),
                            data[2]
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

    // YENİ EKLENEN METOD: Dilek silme işlemi
    public static void removeWish(int index) {
        if (index >= 0 && index < wishList.size()) {
            wishList.remove(index);
            saveData();
        }
    }

    // --- Business Logic ---
    public static int calculateTotalPoints() {
        int total = 0;
        for (Task t : taskList) {
            if ("APPROVED".equals(t.getStatus())) {
                total += t.getPoints();
            }
        }
        return total;
    }

    public static int calculateLevel() {
        return (calculateTotalPoints() / 100) + 1;
    }

    public static int calculateProgress() {
        return calculateTotalPoints() % 100;
    }
}