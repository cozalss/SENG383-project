# 🚀 KidTask - Smart Task Manager

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![UI](https://img.shields.io/badge/GUI-Swing%20%2F%20Material-7C3AED?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)

## 📖 Project Overview
**KidTask** is a gamified task management application built with **Java Swing**. It turns daily chores into a fun, RPG-like experience for children.

Unlike traditional Swing apps, KidTask features a **modern, material-design inspired UI** with animated gradients, particle effects (confetti), and smooth transitions. It allows parents/teachers to assign tasks and children to earn points, level up, and unlock wishes.

---

## 📸 Screenshots

| **Animated Login Screen** | **Dashboard & Gamification** |
|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/8023ee0b-d8c2-4529-b709-abf98d8b8fa9" width="400" /> <br> *Floating shapes animation & Role cards* | <img src="https://github.com/user-attachments/assets/149cda77-8805-4454-9d9d-294781421224" width="400" /> <br> *Circular progress bar & Task list* |

---

## ✨ Key Features

### 🎮 Gamification & Visual Effects
* **Level System:** Dynamic leveling logic based on total points and average task ratings.
* **Circular Progress Bar:** Animated circular bar showing progress towards the next level.
* **Particle Effects:** Confetti explosion animation triggers automatically upon leveling up! 🎊
* **Toast Notifications:** Modern, floating popup messages (Success/Error/Warning) instead of old `JOptionPane` popups.

### 👥 Role-Based Access
* **Child:** View tasks, mark them as "Pending", view wish list.
* **Parent:** Create tasks, approve/rate completed tasks (1-5 Stars), approve wishes.
* **Teacher:** Assign school-related tasks and goals.

### 📝 Advanced Task Management
* **Star Rating System:** Parents rate completed tasks (⭐ 1-5). High ratings help the child level up faster.
* **Status Workflow:** `TO DO` ➝ `PENDING` (Child marks done) ➝ `APPROVED` (Parent confirms).
* **Filtering:** Filter tasks by frequency (Daily, Weekly, Once).

### 🎨 Modern UI/UX
* **Custom Components:** Rounded panels, custom table renderers, and badges.
* **Ripple Effects:** Buttons have a modern "ripple" click animation.
* **Animated Header:** The dashboard header features a slowly shifting color gradient.

### 💾 Data Persistence
* **CSV Storage:** Uses a custom CSV parser to save and load data without needing an external database engine.
    * `tasks.csv`: Stores task details, status, and star ratings.
    * `wishes.csv`: Stores wishlist items and costs.

---

## 🛠️ Tech Stack & Architecture

* **Language:** Java (JDK 8+)
* **GUI Framework:** Java Swing (Custom Painted Components)
* **Design Pattern:** MVC-inspired (Model: `Task/Wish`, View: `Panels`, Controller: `Main/DataManager`)
* **Data Storage:** Local File I/O (CSV)
* **Theme Colors:**
    * 🟣 Primary: `#7C3AED` (Violet)
    * 🟢 Success: `#10B981` (Emerald)
    * 🟠 Accent: `#F59E0B` (Amber)

---

## 🚀 How to Run

1.  **Clone the Repository**
    ```bash
    git clone [https://github.com/cozalss/SENG383-project/tree/main/SENG383-KidTask_Cem%C3%96zal_Final]
    ```

2.  **Open in IDE**
    * Open the project in **IntelliJ IDEA** (Recommended) or Eclipse.

3.  **Run the Application**
    * Locate `src/KidTaskMain.java`.
    * Right-click and select **Run 'KidTaskMain'**.

4.  **First Use**
    * The app will automatically create `tasks.csv` and `wishes.csv` files in the root directory upon saving data.

---

## 📂 Project Structure

```text
src/
├── KidTaskMain.java    # Entry point & Main Controller
├── KidTaskLoginUI.java # Animated Login Screen
├── TaskPanel.java      # Task Management Dashboard
├── WishPanel.java      # Wish List Dashboard
├── EffectPanel.java    # Particle & Notification Effects
├── TaskDialog.java     # Popup for adding tasks
├── WishDialog.java     # Popup for adding wishes
├── DataManager.java    # CSV Read/Write Logic
├── StyleTheme.java     # UI Colors & Fonts
├── Task.java           # Model Class
└── Wish.java           # Model Class



