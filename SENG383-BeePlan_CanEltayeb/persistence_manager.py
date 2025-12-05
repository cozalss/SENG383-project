# ==========================================
# PERSISTENCE MANAGER
# ==========================================

import json
from tkinter import filedialog
from schedule_model import Course, Room


class PersistenceManager:
    def __init__(self, schedule_model):
        self.schedule_model = schedule_model

    def save_json(self):
        data = {
            "courses": [vars(c) for c in self.schedule_model.courses],
            "rooms": [vars(r) for r in self.schedule_model.rooms]
        }
        f = filedialog.asksaveasfile(mode='w', defaultextension=".json")
        if f:
            json.dump(data, f, indent=4)
            f.close()

    def load_json(self):
        f = filedialog.askopenfile(mode='r', defaultextension=".json")
        if f:
            data = json.load(f)
            self.schedule_model.courses = [Course(**c) for c in data.get("courses", [])]
            self.schedule_model.rooms = [Room(**r) for r in data.get("rooms", [])]
            f.close()
            return True
        return False

