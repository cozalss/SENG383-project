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
            # Handle possible key mismatch between JSON ("type") and Room.__init__ ("type_")
            rooms = []
            for r in data.get("rooms", []):
                room = Room(
                    name=r.get("name"),
                    capacity=r.get("capacity"),
                    # Support both legacy "type" and possible "type_" keys
                    type_=r.get("type_") if "type_" in r else r.get("type")
                )
                rooms.append(room)
            self.schedule_model.rooms = rooms
            f.close()
            return True
        return False

