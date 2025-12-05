# ==========================================
# MAIN APPLICATION
# ==========================================

import tkinter as tk
from tkinter import ttk
from schedule_model import ScheduleModel
from scheduler_controller import SchedulerController
from timetable_view import TimetableView
from persistence_manager import PersistenceManager


class MainApp(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("BeePlan - Intelligent Course Scheduler")
        self.geometry("1400x900")
        self.configure(bg="#f5f7fa")

        # Apply modern theme and styling
        self.setup_styles()

        # Initialize Model
        self.schedule_model = ScheduleModel()
        self.schedule_model.load_dummy_data()

        # Layout
        self.create_widgets()

        # Initialize Controller
        self.controller = SchedulerController(
            self.schedule_model,
            self.timetable_view,
            self.report_text,
            self.notebook,
            self.tab_schedule
        )

        # Initialize Persistence Manager
        self.persistence_manager = PersistenceManager(self.schedule_model)

        # Refresh tables with initial data
        self.controller.refresh_tables(self.course_tree, self.room_tree)

    def setup_styles(self):
        style = ttk.Style()
        style.theme_use('clam')
        
        # Color scheme
        self.colors = {
            'primary': '#4a90e2',
            'secondary': '#50c878',
            'accent': '#ff6b6b',
            'warning': '#ffa726',
            'background': '#f5f7fa',
            'surface': '#ffffff',
            'text': '#2c3e50',
            'border': '#e0e0e0'
        }
        
        # Configure styles
        style.configure('TNotebook', background=self.colors['background'], borderwidth=0)
        style.configure('TNotebook.Tab', padding=[20, 10], font=('Segoe UI', 10, 'bold'))
        style.map('TNotebook.Tab',
                 background=[('selected', self.colors['primary']), ('!selected', '#e0e0e0')],
                 foreground=[('selected', 'white'), ('!selected', self.colors['text'])])
        
        style.configure('TFrame', background=self.colors['background'])
        style.configure('TLabelFrame', background=self.colors['surface'], 
                       foreground=self.colors['text'], font=('Segoe UI', 11, 'bold'))
        style.configure('TLabelFrame.Label', background=self.colors['surface'], 
                       foreground=self.colors['primary'], font=('Segoe UI', 11, 'bold'))
        
        # Button styles
        style.configure('Primary.TButton', font=('Segoe UI', 10, 'bold'),
                       background=self.colors['primary'], foreground='white',
                       padding=[15, 10], borderwidth=0)
        style.map('Primary.TButton',
                 background=[('active', '#357abd'), ('!active', self.colors['primary'])])
        
        style.configure('Success.TButton', font=('Segoe UI', 10, 'bold'),
                       background=self.colors['secondary'], foreground='white',
                       padding=[15, 10], borderwidth=0)
        style.map('Success.TButton',
                 background=[('active', '#3fa05a'), ('!active', self.colors['secondary'])])
        
        style.configure('Danger.TButton', font=('Segoe UI', 10),
                       background=self.colors['accent'], foreground='white',
                       padding=[12, 8], borderwidth=0)
        style.map('Danger.TButton',
                 background=[('active', '#e55555'), ('!active', self.colors['accent'])])
        
        style.configure('Secondary.TButton', font=('Segoe UI', 10),
                       background='#6c757d', foreground='white',
                       padding=[12, 8], borderwidth=0)
        style.map('Secondary.TButton',
                 background=[('active', '#5a6268'), ('!active', '#6c757d')])
        
        # Treeview styles
        style.configure('Treeview', font=('Segoe UI', 9), rowheight=25,
                       background=self.colors['surface'], foreground=self.colors['text'],
                       fieldbackground=self.colors['surface'])
        style.configure('Treeview.Heading', font=('Segoe UI', 10, 'bold'),
                       background=self.colors['primary'], foreground='white',
                       padding=[10, 5])
        style.map('Treeview', background=[('selected', self.colors['primary'])],
                 foreground=[('selected', 'white')])

    def create_widgets(self):
        # Main Container with gradient-like background
        main_frame = ttk.Frame(self)
        main_frame.pack(fill=tk.BOTH, expand=True, padx=15, pady=15)

        # Header
        header_frame = tk.Frame(main_frame, bg=self.colors['primary'], height=60)
        header_frame.pack(fill=tk.X, pady=(0, 15))
        header_frame.pack_propagate(False)
        
        title_label = tk.Label(header_frame, text="🐝 BeePlan - Intelligent Course Scheduler",
                              font=('Segoe UI', 18, 'bold'), bg=self.colors['primary'],
                              fg='white', pady=15)
        title_label.pack()

        # Tab Control
        self.notebook = ttk.Notebook(main_frame)
        self.notebook.pack(fill=tk.BOTH, expand=True)

        # Tabs
        self.tab_inputs = ttk.Frame(self.notebook)
        self.tab_schedule = ttk.Frame(self.notebook)
        self.tab_report = ttk.Frame(self.notebook)

        self.notebook.add(self.tab_inputs, text="📝 Data Entry")
        self.notebook.add(self.tab_schedule, text="📅 TimetableView")
        self.notebook.add(self.tab_report, text="📊 Validation Report")

        self.build_input_tab()
        self.build_schedule_tab()
        self.build_report_tab()

    def build_input_tab(self):
        # Split into Courses and Rooms
        paned = ttk.PanedWindow(self.tab_inputs, orient=tk.HORIZONTAL)
        paned.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)

        # === Courses Section ===
        course_frame = ttk.LabelFrame(paned, text="📚 Courses")
        paned.add(course_frame, weight=1)

        # Course List with scrollbar
        course_scroll = ttk.Scrollbar(course_frame, orient="vertical")
        course_scroll.pack(side="right", fill="y")
        
        columns = ("Code", "Name", "Instructor", "Year", "Th", "Lab", "Elec")
        self.course_tree = ttk.Treeview(course_frame, columns=columns, show="headings", height=15,
                                        yscrollcommand=course_scroll.set)
        course_scroll.config(command=self.course_tree.yview)
        
        for col in columns:
            self.course_tree.heading(col, text=col)
            self.course_tree.column(col, width=80, anchor='center')
        self.course_tree.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)

        # Course Controls
        c_btn_frame = tk.Frame(course_frame, bg=self.colors['surface'])
        c_btn_frame.pack(fill=tk.X, padx=10, pady=10)
        
        def on_add_course_enter(e):
            add_course_btn.config(bg="#3fa05a", relief=tk.RAISED, bd=2)
        def on_add_course_leave(e):
            add_course_btn.config(bg=self.colors['secondary'], relief=tk.FLAT, bd=0)
        def on_delete_course_enter(e):
            delete_course_btn.config(bg="#e55555", relief=tk.RAISED, bd=2)
        def on_delete_course_leave(e):
            delete_course_btn.config(bg=self.colors['accent'], relief=tk.FLAT, bd=0)
        
        add_course_btn = tk.Button(c_btn_frame, text="➕ Add Course", 
                                   font=('Segoe UI', 10, 'bold'),
                                   bg=self.colors['secondary'], fg='white',
                                   relief=tk.FLAT, padx=15, pady=8,
                                   cursor='hand2', activebackground="#3fa05a", activeforeground="white",
                                   command=lambda: self.controller.add_course_dialog(self, self.course_tree))
        add_course_btn.pack(side=tk.LEFT, padx=5)
        add_course_btn.bind("<Enter>", on_add_course_enter)
        add_course_btn.bind("<Leave>", on_add_course_leave)
        
        delete_course_btn = tk.Button(c_btn_frame, text="🗑️ Delete Selected",
                                      font=('Segoe UI', 10),
                                      bg=self.colors['accent'], fg='white',
                                      relief=tk.FLAT, padx=15, pady=8,
                                      cursor='hand2', activebackground="#e55555", activeforeground="white",
                                      command=lambda: self.controller.delete_course(self.course_tree))
        delete_course_btn.pack(side=tk.LEFT, padx=5)
        delete_course_btn.bind("<Enter>", on_delete_course_enter)
        delete_course_btn.bind("<Leave>", on_delete_course_leave)

        # === Rooms Section ===
        room_frame = ttk.LabelFrame(paned, text="🏫 Classrooms & Labs")
        paned.add(room_frame, weight=1)

        # Room List with scrollbar
        room_scroll = ttk.Scrollbar(room_frame, orient="vertical")
        room_scroll.pack(side="right", fill="y")
        
        r_columns = ("Name", "Capacity", "Type")
        self.room_tree = ttk.Treeview(room_frame, columns=r_columns, show="headings", height=15,
                                      yscrollcommand=room_scroll.set)
        room_scroll.config(command=self.room_tree.yview)
        
        for col in r_columns:
            self.room_tree.heading(col, text=col)
            self.room_tree.column(col, width=120, anchor='center')
        self.room_tree.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)

        # Room Controls
        r_btn_frame = tk.Frame(room_frame, bg=self.colors['surface'])
        r_btn_frame.pack(fill=tk.X, padx=10, pady=10)
        
        def on_add_room_enter(e):
            add_room_btn.config(bg="#3fa05a", relief=tk.RAISED, bd=2)
        def on_add_room_leave(e):
            add_room_btn.config(bg=self.colors['secondary'], relief=tk.FLAT, bd=0)
        def on_delete_room_enter(e):
            delete_room_btn.config(bg="#e55555", relief=tk.RAISED, bd=2)
        def on_delete_room_leave(e):
            delete_room_btn.config(bg=self.colors['accent'], relief=tk.FLAT, bd=0)
        
        add_room_btn = tk.Button(r_btn_frame, text="➕ Add Room",
                                font=('Segoe UI', 10, 'bold'),
                                bg=self.colors['secondary'], fg='white',
                                relief=tk.FLAT, padx=15, pady=8,
                                cursor='hand2', activebackground="#3fa05a", activeforeground="white",
                                command=lambda: self.controller.add_room_dialog(self, self.room_tree))
        add_room_btn.pack(side=tk.LEFT, padx=5)
        add_room_btn.bind("<Enter>", on_add_room_enter)
        add_room_btn.bind("<Leave>", on_add_room_leave)
        
        delete_room_btn = tk.Button(r_btn_frame, text="🗑️ Delete Selected",
                                    font=('Segoe UI', 10),
                                    bg=self.colors['accent'], fg='white',
                                    relief=tk.FLAT, padx=15, pady=8,
                                    cursor='hand2', activebackground="#e55555", activeforeground="white",
                                    command=lambda: self.controller.delete_room(self.room_tree))
        delete_room_btn.pack(side=tk.LEFT, padx=5)
        delete_room_btn.bind("<Enter>", on_delete_room_enter)
        delete_room_btn.bind("<Leave>", on_delete_room_leave)

        # Bottom Action Bar with gradient-like background
        action_frame = tk.Frame(self.tab_inputs, bg='#e8f4f8', relief=tk.RAISED, bd=2)
        action_frame.pack(fill=tk.X, padx=10, pady=15)
        
        left_frame = tk.Frame(action_frame, bg='#e8f4f8')
        left_frame.pack(side=tk.LEFT, padx=15, pady=10)
        
        right_frame = tk.Frame(action_frame, bg='#e8f4f8')
        right_frame.pack(side=tk.RIGHT, padx=15, pady=10)

        def on_load_enter(e):
            load_btn.config(bg="#5a6268", relief=tk.RAISED, bd=2)
        def on_load_leave(e):
            load_btn.config(bg='#6c757d', relief=tk.FLAT, bd=0)
        def on_save_enter(e):
            save_btn.config(bg="#5a6268", relief=tk.RAISED, bd=2)
        def on_save_leave(e):
            save_btn.config(bg='#6c757d', relief=tk.FLAT, bd=0)
        def on_generate_enter(e):
            generate_btn.config(bg="#357abd", relief=tk.RAISED, bd=2)
        def on_generate_leave(e):
            generate_btn.config(bg=self.colors['primary'], relief=tk.FLAT, bd=0)
        
        load_btn = tk.Button(left_frame, text="📂 Load JSON",
                             font=('Segoe UI', 10),
                             bg='#6c757d', fg='white',
                             relief=tk.FLAT, padx=15, pady=8,
                             cursor='hand2', activebackground="#5a6268", activeforeground="white",
                             command=self.load_json)
        load_btn.pack(side=tk.LEFT, padx=5)
        load_btn.bind("<Enter>", on_load_enter)
        load_btn.bind("<Leave>", on_load_leave)
        
        save_btn = tk.Button(left_frame, text="💾 Save JSON",
                            font=('Segoe UI', 10),
                            bg='#6c757d', fg='white',
                            relief=tk.FLAT, padx=15, pady=8,
                            cursor='hand2', activebackground="#5a6268", activeforeground="white",
                            command=self.save_json)
        save_btn.pack(side=tk.LEFT, padx=5)
        save_btn.bind("<Enter>", on_save_enter)
        save_btn.bind("<Leave>", on_save_leave)
        
        generate_btn = tk.Button(right_frame, text="✨ Generate Schedule",
                                font=('Segoe UI', 11, 'bold'),
                                bg=self.colors['primary'], fg='white',
                                relief=tk.FLAT, padx=20, pady=10,
                                cursor='hand2', activebackground="#357abd", activeforeground="white",
                                command=lambda: self.controller.generate_schedule())
        generate_btn.pack(side=tk.LEFT, padx=5)
        generate_btn.bind("<Enter>", on_generate_enter)
        generate_btn.bind("<Leave>", on_generate_leave)

    def build_schedule_tab(self):
        # Initialize TimetableView
        self.timetable_view = TimetableView(self.tab_schedule)

    def build_report_tab(self):
        # Report text with styling
        report_frame = tk.Frame(self.tab_report, bg=self.colors['surface'], relief=tk.RAISED, bd=2)
        report_frame.pack(fill=tk.BOTH, expand=True, padx=15, pady=15)
        
        self.report_text = tk.Text(report_frame, padx=15, pady=15, wrap=tk.WORD,
                                   font=('Consolas', 10), bg='#ffffff', fg=self.colors['text'],
                                   relief=tk.FLAT, bd=0, selectbackground=self.colors['primary'])
        self.report_text.pack(fill=tk.BOTH, expand=True)
        
        # Scrollbar for report
        report_scroll = ttk.Scrollbar(report_frame, orient="vertical", command=self.report_text.yview)
        report_scroll.pack(side="right", fill="y")
        self.report_text.config(yscrollcommand=report_scroll.set)

    def save_json(self):
        self.persistence_manager.save_json()
        # Refresh tables after save (in case data changed)
        self.controller.refresh_tables(self.course_tree, self.room_tree)

    def load_json(self):
        if self.persistence_manager.load_json():
            self.controller.refresh_tables(self.course_tree, self.room_tree)


if __name__ == "__main__":
    app = MainApp()
    app.mainloop()
