# ==========================================
# SCHEDULER CONTROLLER
# ==========================================

import tkinter as tk
from tkinter import ttk, messagebox
from schedule_model import Course, Room
from scheduler_algorithm import SchedulerAlgorithm


class SchedulerController:
    def __init__(self, schedule_model, timetable_view, report_text_widget, notebook, tab_schedule):
        self.schedule_model = schedule_model
        self.timetable_view = timetable_view
        self.report_text = report_text_widget
        self.notebook = notebook
        self.tab_schedule = tab_schedule
        self.scheduler = None

    # Helper validation to keep dialogs resilient
    def _validate_int(self, value, field_name, min_val=None, max_val=None):
        try:
            ivalue = int(value)
        except (TypeError, ValueError):
            messagebox.showerror("Invalid Input", f"{field_name} must be a number.")
            return None
        if min_val is not None and ivalue < min_val:
            messagebox.showerror("Invalid Input", f"{field_name} must be >= {min_val}.")
            return None
        if max_val is not None and ivalue > max_val:
            messagebox.showerror("Invalid Input", f"{field_name} must be <= {max_val}.")
            return None
        return ivalue

    def refresh_tables(self, course_tree=None, room_tree=None):
        # Clear and populate course tree
        if course_tree:
            for i in course_tree.get_children():
                course_tree.delete(i)
            for c in self.schedule_model.courses:
                elec = "Yes" if c.is_elective else "No"
                course_tree.insert("", "end",
                                  values=(c.code, c.name, c.instructor, c.year, c.theory_hours, c.lab_hours, elec))

        # Clear and populate room tree
        if room_tree:
            for i in room_tree.get_children():
                room_tree.delete(i)
            for r in self.schedule_model.rooms:
                room_tree.insert("", "end", values=(r.name, r.capacity, r.type))

    def add_course_dialog(self, parent_window, course_tree):
        top = tk.Toplevel(parent_window)
        top.title("➕ Add New Course")
        top.geometry("400x500")
        top.configure(bg="#f5f7fa")
        top.resizable(False, False)

        # Header
        header = tk.Frame(top, bg="#4a90e2", height=50)
        header.pack(fill=tk.X)
        tk.Label(header, text="➕ Add New Course", font=("Segoe UI", 14, "bold"),
                bg="#4a90e2", fg="white", pady=15).pack()

        # Main container to hold form and buttons
        main_container = tk.Frame(top, bg="#f5f7fa")
        main_container.pack(fill=tk.BOTH, expand=True)

        # Form frame
        form_frame = tk.Frame(main_container, bg="#f5f7fa", padx=20, pady=20)
        form_frame.pack(fill=tk.BOTH, expand=True)

        entries = {}
        fields = ["Code", "Name", "Instructor", "Year (1-4)", "Theory Hours", "Lab Hours"]

        for i, field in enumerate(fields):
            label = tk.Label(form_frame, text=field + ":", font=("Segoe UI", 10, "bold"),
                           bg="#f5f7fa", fg="#2c3e50", anchor="w")
            label.grid(row=i, column=0, padx=10, pady=8, sticky="ew")
            
            e = tk.Entry(form_frame, font=("Segoe UI", 10), relief=tk.SOLID, bd=1,
                        bg="white", fg="#2c3e50", insertbackground="#2c3e50")
            e.grid(row=i, column=1, padx=10, pady=8, ipadx=5, ipady=5, sticky="ew")
            entries[field] = e

        form_frame.grid_columnconfigure(1, weight=1)

        is_elec = tk.BooleanVar()
        check_frame = tk.Frame(form_frame, bg="#f5f7fa")
        check_frame.grid(row=len(fields), column=0, columnspan=2, pady=10)
        tk.Checkbutton(check_frame, text="Is Elective?", variable=is_elec,
                      font=("Segoe UI", 10), bg="#f5f7fa", fg="#2c3e50",
                      selectcolor="white", activebackground="#f5f7fa").pack()

        # Button frame - pack at bottom
        btn_frame = tk.Frame(main_container, bg="#f5f7fa", pady=15)
        btn_frame.pack(fill=tk.X, side=tk.BOTTOM)

        def save():
            code = entries["Code"].get().strip()
            name = entries["Name"].get().strip()
            instructor = entries["Instructor"].get().strip()
            year = self._validate_int(entries["Year (1-4)"].get(), "Year", 1, 4)
            th = self._validate_int(entries["Theory Hours"].get(), "Theory Hours", 0, 12)
            lab = self._validate_int(entries["Lab Hours"].get(), "Lab Hours", 0, 12)

            # Stop early if any field failed validation
            if not all([code, name, instructor]) or None in (year, th, lab):
                if not code or not name or not instructor:
                    messagebox.showerror("Invalid Input", "Code, Name, and Instructor are required.")
                return

            c = Course(code, name, instructor, year, th, lab, is_elective=is_elec.get())
            self.schedule_model.add_course(c)
            self.refresh_tables(course_tree=course_tree)
            top.destroy()
            messagebox.showinfo("Success", "Course added successfully!")

        def on_save_enter(e):
            save_btn.config(bg="#3fa05a")
        def on_save_leave(e):
            save_btn.config(bg="#50c878")
        def on_cancel_enter(e):
            cancel_btn.config(bg="#5a6268")
        def on_cancel_leave(e):
            cancel_btn.config(bg="#6c757d")
        
        save_btn = tk.Button(btn_frame, text="💾 Save Course", font=("Segoe UI", 11, "bold"),
                           bg="#50c878", fg="white", relief=tk.RAISED, bd=2, padx=20, pady=8,
                           command=save, cursor="hand2", activebackground="#3fa05a", activeforeground="white")
        save_btn.pack(side=tk.LEFT, padx=10)
        save_btn.bind("<Enter>", on_save_enter)
        save_btn.bind("<Leave>", on_save_leave)
        
        cancel_btn = tk.Button(btn_frame, text="Cancel", font=("Segoe UI", 10),
                             bg="#6c757d", fg="white", relief=tk.RAISED, bd=2, padx=20, pady=8,
                             command=top.destroy, cursor="hand2", activebackground="#5a6268", activeforeground="white")
        cancel_btn.pack(side=tk.LEFT, padx=10)
        cancel_btn.bind("<Enter>", on_cancel_enter)
        cancel_btn.bind("<Leave>", on_cancel_leave)

    def delete_course(self, course_tree):
        selected = course_tree.selection()
        if not selected:
            messagebox.showwarning("No Selection", "Please select a course to delete.")
            return
        
        for item in selected:
            vals = course_tree.item(item)['values']
            if vals and len(vals) > 0:
                # Find and remove
                self.schedule_model.remove_course(vals[0])
        self.refresh_tables(course_tree=course_tree)

    def add_room_dialog(self, parent_window, room_tree):
        top = tk.Toplevel(parent_window)
        top.title("➕ Add New Room")
        top.geometry("400x400")
        top.configure(bg="#f5f7fa")
        top.resizable(False, False)

        # Header
        header = tk.Frame(top, bg="#50c878", height=50)
        header.pack(fill=tk.X)
        tk.Label(header, text="➕ Add New Room", font=("Segoe UI", 14, "bold"),
                bg="#50c878", fg="white", pady=15).pack()

        # Main container to hold form and buttons
        main_container = tk.Frame(top, bg="#f5f7fa")
        main_container.pack(fill=tk.BOTH, expand=True)

        # Form frame
        form_frame = tk.Frame(main_container, bg="#f5f7fa", padx=20, pady=20)
        form_frame.pack(fill=tk.BOTH, expand=True)

        tk.Label(form_frame, text="Name:", font=("Segoe UI", 10, "bold"),
                bg="#f5f7fa", fg="#2c3e50", anchor="w").grid(row=0, column=0, padx=10, pady=15, sticky="ew")
        name_e = tk.Entry(form_frame, font=("Segoe UI", 10), relief=tk.SOLID, bd=1,
                         bg="white", fg="#2c3e50", insertbackground="#2c3e50")
        name_e.grid(row=0, column=1, padx=10, pady=15, ipadx=5, ipady=5, sticky="ew")

        tk.Label(form_frame, text="Capacity:", font=("Segoe UI", 10, "bold"),
                bg="#f5f7fa", fg="#2c3e50", anchor="w").grid(row=1, column=0, padx=10, pady=15, sticky="ew")
        cap_e = tk.Entry(form_frame, font=("Segoe UI", 10), relief=tk.SOLID, bd=1,
                        bg="white", fg="#2c3e50", insertbackground="#2c3e50")
        cap_e.grid(row=1, column=1, padx=10, pady=15, ipadx=5, ipady=5, sticky="ew")

        tk.Label(form_frame, text="Type:", font=("Segoe UI", 10, "bold"),
                bg="#f5f7fa", fg="#2c3e50", anchor="w").grid(row=2, column=0, padx=10, pady=15, sticky="ew")
        type_combo = ttk.Combobox(form_frame, values=["Classroom", "Lab"], 
                                 font=("Segoe UI", 10), state="readonly")
        type_combo.grid(row=2, column=1, padx=10, pady=15, ipadx=5, ipady=5, sticky="ew")
        type_combo.current(0)

        form_frame.grid_columnconfigure(1, weight=1)

        # Button frame - pack at bottom
        btn_frame = tk.Frame(main_container, bg="#f5f7fa", pady=15)
        btn_frame.pack(fill=tk.X, side=tk.BOTTOM)

        def save():
            name = name_e.get().strip()
            cap = self._validate_int(cap_e.get(), "Capacity", 1, 5000)
            type_val = type_combo.get()

            if not name or cap is None or not type_val:
                if not name:
                    messagebox.showerror("Invalid Input", "Room name is required.")
                return

            r = Room(name, cap, type_val)
            self.schedule_model.add_room(r)
            self.refresh_tables(room_tree=room_tree)
            top.destroy()
            messagebox.showinfo("Success", "Room added successfully!")

        def on_save_enter(e):
            save_btn.config(bg="#3fa05a")
        def on_save_leave(e):
            save_btn.config(bg="#50c878")
        def on_cancel_enter(e):
            cancel_btn.config(bg="#5a6268")
        def on_cancel_leave(e):
            cancel_btn.config(bg="#6c757d")
        
        save_btn = tk.Button(btn_frame, text="💾 Save Room", font=("Segoe UI", 11, "bold"),
                           bg="#50c878", fg="white", relief=tk.RAISED, bd=2, padx=20, pady=8,
                           command=save, cursor="hand2", activebackground="#3fa05a", activeforeground="white")
        save_btn.pack(side=tk.LEFT, padx=10)
        save_btn.bind("<Enter>", on_save_enter)
        save_btn.bind("<Leave>", on_save_leave)
        
        cancel_btn = tk.Button(btn_frame, text="Cancel", font=("Segoe UI", 10),
                             bg="#6c757d", fg="white", relief=tk.RAISED, bd=2, padx=20, pady=8,
                             command=top.destroy, cursor="hand2", activebackground="#5a6268", activeforeground="white")
        cancel_btn.pack(side=tk.LEFT, padx=10)
        cancel_btn.bind("<Enter>", on_cancel_enter)
        cancel_btn.bind("<Leave>", on_cancel_leave)

    def delete_room(self, room_tree):
        selected = room_tree.selection()
        if not selected:
            messagebox.showwarning("No Selection", "Please select a room to delete.")
            return
        
        for item in selected:
            vals = room_tree.item(item)['values']
            if vals and len(vals) > 0:
                self.schedule_model.remove_room(vals[0])
        self.refresh_tables(room_tree=room_tree)

    def generate_schedule(self):
        self.scheduler = SchedulerAlgorithm(self.schedule_model.courses, self.schedule_model.rooms, None)
        success, msg = self.scheduler.generate()

        if success:
            messagebox.showinfo("Success", msg)
            self.schedule_model.set_schedule(self.scheduler.schedule)
            self.timetable_view.render_schedule(self.scheduler.schedule)
            self.generate_report()
            self.notebook.select(self.tab_schedule)
        else:
            messagebox.showerror("Failed", msg)

    def generate_report(self):
        report_lines = self.scheduler.validate_full_schedule()
        self.report_text.delete(1.0, tk.END)
        
        # Header with styling
        self.report_text.insert(tk.END, "📊 VALIDATION REPORT\n", "header")
        self.report_text.insert(tk.END, "=" * 50 + "\n\n", "header")
        
        # Configure text tags for styling
        self.report_text.tag_configure("header", font=("Segoe UI", 14, "bold"), foreground="#4a90e2")
        self.report_text.tag_configure("success", font=("Segoe UI", 10), foreground="#50c878")
        self.report_text.tag_configure("warning", font=("Segoe UI", 10), foreground="#ffa726")
        self.report_text.tag_configure("summary", font=("Segoe UI", 11, "bold"), foreground="#2c3e50")
        self.report_text.tag_configure("entry", font=("Consolas", 9), foreground="#2c3e50")
        
        if not report_lines:
            self.report_text.insert(tk.END, "✅ No violations detected. Schedule is valid!\n\n", "success")
        else:
            self.report_text.insert(tk.END, "⚠️  Violations Found:\n\n", "warning")
            for line in report_lines:
                self.report_text.insert(tk.END, f"  ⚠ {line}\n", "warning")

        from schedule_model import DAYS, TIME_SLOTS
        self.report_text.insert(tk.END, "\n" + "=" * 50 + "\n", "header")
        self.report_text.insert(tk.END, "\n📅 SCHEDULE SUMMARY\n\n", "summary")
        for entry in self.scheduler.schedule:
            day = DAYS[entry.day_idx]
            time = TIME_SLOTS[entry.time_idx]
            type_icon = "📖" if entry.type == "Theory" else "🔬"
            self.report_text.insert(tk.END, 
                f"  {type_icon} {day} {time}: {entry.course.code} ({entry.type}) in {entry.room.name}\n", 
                "entry")

