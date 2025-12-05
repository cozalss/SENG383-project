# ==========================================
# SCHEDULE MODEL
# ==========================================

DAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"]
# Standard academic slots. Note the Friday exam block logic will rely on indices or strings.
TIME_SLOTS = [
    "08:30-09:20", "09:30-10:20", "10:30-11:20", "11:30-12:20",
    "13:30-14:20", "14:30-15:20", "15:30-16:20", "16:30-17:20"
]

# Indices corresponding to Friday 13:20 - 15:10 constraint
# 13:30-14:20 is index 4, 14:30-15:20 is index 5.
FRIDAY_BLOCKED_INDICES = [4, 5]


class Course:
    def __init__(self, code, name, instructor, year, theory_hours, lab_hours, is_elective=False, dept="CENG"):
        self.code = code
        self.name = name
        self.instructor = instructor
        self.year = year  # 1, 2, 3, 4
        self.theory_hours = int(theory_hours)
        self.lab_hours = int(lab_hours)
        self.is_elective = is_elective
        self.dept = dept  # "CENG" or "SENG"

    def __repr__(self):
        return f"{self.code} ({self.dept})"


class Room:
    def __init__(self, name, capacity, type_):
        self.name = name
        self.capacity = int(capacity)
        self.type = type_  # "Classroom" or "Lab"

    def __repr__(self):
        return self.name


class ScheduleEntry:
    def __init__(self, course, type_, day_idx, time_idx, room):
        self.course = course
        self.type = type_  # "Theory" or "Lab"
        self.day_idx = day_idx
        self.time_idx = time_idx
        self.room = room


class ScheduleModel:
    def __init__(self):
        self.courses = []
        self.rooms = []
        self.schedule = []

    def load_dummy_data(self):
        self.courses = [
            Course("CS101", "Intro to CS", "Dr. A", 1, 3, 2),
            Course("CS201", "Data Structures", "Dr. B", 2, 3, 2),
            Course("CS301", "Algorithms", "Dr. C", 3, 3, 0),
            Course("SE301", "Software Eng", "Dr. D", 3, 3, 0),
            Course("CS401", "AI", "Dr. A", 4, 3, 0),
            Course("ELEC1", "Web Dev", "Dr. E", 4, 3, 0, is_elective=True, dept="CENG"),
            Course("ELEC2", "Mobile Dev", "Dr. F", 4, 3, 0, is_elective=True, dept="SENG"),
        ]
        self.rooms = [
            Room("R101", 50, "Classroom"),
            Room("R102", 50, "Classroom"),
            Room("LAB1", 30, "Lab"),
            Room("LAB2", 40, "Lab"),
        ]

    def add_course(self, course):
        self.courses.append(course)

    def remove_course(self, course_code):
        self.courses = [c for c in self.courses if c.code != course_code]

    def add_room(self, room):
        self.rooms.append(room)

    def remove_room(self, room_name):
        self.rooms = [r for r in self.rooms if r.name != room_name]

    def set_schedule(self, schedule):
        self.schedule = schedule

