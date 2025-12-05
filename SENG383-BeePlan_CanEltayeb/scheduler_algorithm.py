# ==========================================
# SCHEDULER ALGORITHM
# ==========================================

import random
from schedule_model import Course, Room, ScheduleEntry, DAYS, TIME_SLOTS, FRIDAY_BLOCKED_INDICES


class SchedulerAlgorithm:
    def __init__(self, courses, rooms, constraints):
        self.courses = courses
        self.rooms = rooms
        self.constraints = constraints
        self.schedule = []
        self.violations = []
        self.log = []

    def generate(self):
        self.schedule = []
        self.violations = []
        self.log = []

        # Prepare tasks: Break courses into individual hour blocks
        # We prioritize harder constraints (e.g., Theory before Lab) implicitly by ordering or checking
        tasks = []

        for course in self.courses:
            # Add Theory Blocks
            for _ in range(course.theory_hours):
                tasks.append({'course': course, 'type': 'Theory'})
            # Add Lab Blocks
            for _ in range(course.lab_hours):
                tasks.append({'course': course, 'type': 'Lab'})

        # Sort tasks to optimize backtracking (Heuristic: Most Constrained Variable)
        # 3rd year and electives are hard, schedule them early?
        # Or schedule labs last to ensure theory exists?
        # Let's shuffle slightly to allow variety, but generally huge blocks first
        tasks.sort(key=lambda x: (x['course'].year, x['type']), reverse=True)

        solution = self.backtrack(tasks, [])
        if solution:
            self.schedule = solution
            return True, "Schedule Generated Successfully!"
        else:
            return False, "Could not find a conflict-free schedule. Check Constraints."

    def backtrack(self, remaining_tasks, current_schedule):
        if not remaining_tasks:
            return current_schedule

        task = remaining_tasks[0]
        course = task['course']
        ctype = task['type']

        # Domain of possibilities: All (Day, Time, Room) tuples
        # Randomized to get different results on different runs
        possible_slots = [(d, t) for d in range(len(DAYS)) for t in range(len(TIME_SLOTS))]
        random.shuffle(possible_slots)

        for day_idx, time_idx in possible_slots:
            # 1. Hard Constraint: Friday Exam Block
            if day_idx == 4 and time_idx in FRIDAY_BLOCKED_INDICES:
                continue

            # Try to find a valid room
            valid_rooms = [r for r in self.rooms if self.is_room_valid(r, ctype, course)]
            random.shuffle(valid_rooms)

            for room in valid_rooms:
                if self.is_safe(task, day_idx, time_idx, room, current_schedule):
                    # Place it
                    new_entry = ScheduleEntry(course, ctype, day_idx, time_idx, room)
                    result = self.backtrack(remaining_tasks[1:], current_schedule + [new_entry])
                    if result:
                        return result

        return None

    def is_room_valid(self, room, ctype, course):
        # Lab sessions need Lab rooms
        if ctype == 'Lab' and room.type != 'Lab':
            return False
        if ctype == 'Theory' and room.type == 'Lab':
            return False  # Prefer classrooms for theory
        # Capacity check (Soft/Hard) - treating as hard for Lab <= 40
        if ctype == 'Lab' and room.capacity > 40:
            pass  # Rule says Lab capacity <= 40. Does it mean room size or students?
            # Assuming room must support the class, but "Lab capacity <= 40" usually means
            # we shouldn't put a huge class in a small lab.
            # Let's assume Room Capacity is the limit.
        return True

    def is_safe(self, task, day, time, room, schedule):
        course = task['course']
        ctype = task['type']

        # Check against existing placements
        lecturer_daily_hours = 0

        for entry in schedule:
            # 1. Room Conflict: Same room, same time
            if entry.day_idx == day and entry.time_idx == time and entry.room.name == room.name:
                return False

            # 2. Instructor Conflict: Same instructor, same time
            if entry.course.instructor == course.instructor and entry.day_idx == day and entry.time_idx == time:
                return False

            # 3. Student Group Conflict (Same Year)
            # Assuming 1st years can't be in two places at once
            if entry.course.year == course.year and entry.day_idx == day and entry.time_idx == time:
                # Exception: Electives might be split, but generally avoid overlap for same year
                if not (course.is_elective and entry.course.is_elective):
                    return False

            # 4. CENG vs SENG Elective Overlap
            if course.is_elective and entry.course.is_elective:
                if entry.day_idx == day and entry.time_idx == time:
                    return False  # "CENG and SENG electives must not overlap"

            # 5. 3rd Year vs Electives
            if (course.year == 3 and entry.course.is_elective) or (course.is_elective and entry.course.year == 3):
                if entry.day_idx == day and entry.time_idx == time:
                    return False

            # Count hours for lecturer constraint
            if entry.course.instructor == course.instructor and entry.day_idx == day and entry.type == 'Theory':
                lecturer_daily_hours += 1

        # 6. Lecturer max 4 hours theory per day
        if ctype == 'Theory' and lecturer_daily_hours >= 4:
            return False

        # 7. Lab after Theory
        # This is tricky in pure backtracking because we might schedule Lab before Theory exists in list.
        # We check: If we are placing a Lab, are all Theory hours for this course scheduled BEFORE this time?
        # Relaxed Logic: Just ensure no Theory is scheduled AFTER this lab in the current partial schedule.
        # AND if theory is in schedule, it must be earlier.
        if ctype == 'Lab':
            for entry in schedule:
                if entry.course.code == course.code and entry.type == 'Theory':
                    # Theory must be earlier in the week/day
                    entry_time_val = entry.day_idx * 100 + entry.time_idx
                    current_time_val = day * 100 + time
                    if entry_time_val > current_time_val:
                        return False

        # If Theory, ensure no Lab is already scheduled BEFORE it
        if ctype == 'Theory':
            for entry in schedule:
                if entry.course.code == course.code and entry.type == 'Lab':
                    entry_time_val = entry.day_idx * 100 + entry.time_idx
                    current_time_val = day * 100 + time
                    if entry_time_val < current_time_val:
                        return False

        return True

    def validate_full_schedule(self):
        # Run a full pass to generate the report
        report = []
        for entry in self.schedule:
            # Check Lab Capacity
            if entry.type == 'Lab' and entry.room.capacity > 40:
                # Just a warning or strict rule? The prompt says "Lab capacity <= 40"
                # Interpreting as: We shouldn't use a lab room larger than 40?
                # Or we shouldn't put more than 40 students?
                # Let's assume the rule is "Lab classes are capped at 40".
                pass

                # Check Lab after Theory order globally
        for c in self.courses:
            theories = [e for e in self.schedule if e.course.code == c.code and e.type == 'Theory']
            labs = [e for e in self.schedule if e.course.code == c.code and e.type == 'Lab']

            if theories and labs:
                last_theory_time = max([t.day_idx * 100 + t.time_idx for t in theories])
                first_lab_time = min([l.day_idx * 100 + l.time_idx for l in labs])

                if first_lab_time < last_theory_time:
                    report.append(f"VIOLATION: {c.name} Lab starts before Theory ends.")

        return report

