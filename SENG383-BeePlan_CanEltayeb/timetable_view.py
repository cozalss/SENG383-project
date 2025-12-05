# ==========================================
# TIMETABLE VIEW
# ==========================================

import tkinter as tk
from tkinter import ttk
from schedule_model import DAYS, TIME_SLOTS, FRIDAY_BLOCKED_INDICES


class TimetableView:
    def __init__(self, parent_frame):
        self.parent_frame = parent_frame
        self.schedule_frame = None
        self.grid_inner = None
        self.build_schedule_tab()

    def build_schedule_tab(self):
        # Grid View with modern styling
        self.schedule_frame = tk.Frame(self.parent_frame, bg='#f5f7fa')
        self.schedule_frame.pack(fill=tk.BOTH, expand=True, padx=15, pady=15)

        # Scrollbars
        canvas = tk.Canvas(self.schedule_frame, bg="#ffffff", highlightthickness=0,
                         relief=tk.RAISED, bd=2)
        scroll_y = ttk.Scrollbar(self.schedule_frame, orient="vertical", command=canvas.yview)
        scroll_x = ttk.Scrollbar(self.schedule_frame, orient="horizontal", command=canvas.xview)

        self.grid_inner = tk.Frame(canvas, bg="#ffffff")
        self.grid_inner.bind("<Configure>", lambda e: canvas.configure(scrollregion=canvas.bbox("all")))

        canvas.create_window((0, 0), window=self.grid_inner, anchor="nw")
        canvas.configure(yscrollcommand=scroll_y.set, xscrollcommand=scroll_x.set)

        scroll_y.pack(side="right", fill="y")
        scroll_x.pack(side="bottom", fill="x")
        canvas.pack(side="left", fill="both", expand=True)

        self.draw_empty_grid()

    def draw_empty_grid(self):
        # Clear existing
        for widget in self.grid_inner.winfo_children():
            widget.destroy()

        # Color scheme
        header_bg = "#4a90e2"
        time_bg = "#e8f4f8"
        cell_bg = "#ffffff"
        exam_bg = "#ffebee"
        
        # Headers (Days) - Modern styled
        time_header = tk.Label(self.grid_inner, text="Time / Day", 
                              font=("Segoe UI", 11, "bold"), bg=header_bg, fg="white",
                              borderwidth=2, relief=tk.RAISED, padx=15, pady=10)
        time_header.grid(row=0, column=0, sticky="nsew")
        
        day_colors = ["#5dade2", "#58d68d", "#f7dc6f", "#ec7063", "#bb8fce"]
        for i, day in enumerate(DAYS):
            day_lbl = tk.Label(self.grid_inner, text=day, 
                              font=("Segoe UI", 11, "bold"), bg=day_colors[i], fg="white",
                              borderwidth=2, relief=tk.RAISED, padx=20, pady=10)
            day_lbl.grid(row=0, column=i + 1, sticky="nsew")

        # Rows (Times)
        for i, time in enumerate(TIME_SLOTS):
            time_lbl = tk.Label(self.grid_inner, text=time, 
                               font=("Segoe UI", 9, "bold"), bg=time_bg, fg="#2c3e50",
                               borderwidth=1, relief=tk.SOLID, padx=10, pady=8)
            time_lbl.grid(row=i + 1, column=0, sticky="nsew")
            
            for j in range(len(DAYS)):
                # Empty Cells with alternating colors
                if j == 4 and i in FRIDAY_BLOCKED_INDICES:
                    bg = exam_bg
                    lbl = tk.Label(self.grid_inner, text="🚫 EXAM\nBLOCK", 
                                  bg=bg, borderwidth=1, relief=tk.SOLID, 
                                  fg="#c62828", font=("Segoe UI", 8, "bold"),
                                  padx=5, pady=5)
                else:
                    # Alternating cell colors for better visibility
                    bg = "#f8f9fa" if (i + j) % 2 == 0 else cell_bg
                    lbl = tk.Label(self.grid_inner, text="", bg=bg, 
                                  borderwidth=1, relief=tk.SOLID,
                                  padx=5, pady=5)
                lbl.grid(row=i + 1, column=j + 1, sticky="nsew", ipadx=5, ipady=5)
        
        # Configure grid weights for responsive sizing
        self.grid_inner.grid_columnconfigure(0, weight=1, minsize=120)
        for i in range(len(DAYS)):
            self.grid_inner.grid_columnconfigure(i + 1, weight=1, minsize=150)

    def render_schedule(self, schedule):
        self.draw_empty_grid()

        # Enhanced color palette for years with better contrast
        colors = {
            1: "#e3f2fd",  # Light blue
            2: "#c8e6c9",  # Light green
            3: "#ffe0b2",  # Light orange
            4: "#e1bee7"   # Light purple
        }
        
        # Type icons and colors
        type_colors = {
            "Theory": "#2196F3",
            "Lab": "#4CAF50"
        }

        for entry in schedule:
            r = entry.time_idx + 1
            c = entry.day_idx + 1

            # Skip exam block cells
            if c == 5 and (r - 1) in FRIDAY_BLOCKED_INDICES:
                continue

            # Enhanced content with icons
            type_icon = "📖" if entry.type == "Theory" else "🔬"
            text = f"{type_icon} {entry.course.code}\n{entry.type}\n📍 {entry.room.name}\n👤 {entry.course.instructor}"
            color = colors.get(entry.course.year, "#ffffff")

            # Find label for this cell
            slaves = self.grid_inner.grid_slaves(row=r, column=c)
            existing_text = ""

            target_label = None
            for s in slaves:
                if isinstance(s, tk.Label):
                    target_label = s
                    existing_text = s.cget("text")
                    break

            if target_label:
                if existing_text and existing_text.strip() and "EXAM" not in existing_text:
                    new_text = existing_text + "\n" + "─" * 15 + "\n" + text
                else:
                    new_text = text
                
                # Enhanced styling
                target_label.configure(
                    text=new_text, 
                    bg=color, 
                    font=("Segoe UI", 8),
                    fg="#2c3e50",
                    relief=tk.RAISED,
                    bd=2,
                    justify=tk.CENTER,
                    wraplength=140
                )

