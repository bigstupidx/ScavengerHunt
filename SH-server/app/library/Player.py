import uuid


class Player:
    __player_map = {}

    @staticmethod
    def get_all():
        return Player.__player_map.values()

    @staticmethod
    def get_by_id(id):
        if id in Player.__player_map:
            return Player.__player_map[id]
        return None

    def __init__(self, name, game_key, sid):
        self.id = str(uuid.uuid1())
        self.name = name
        self.game_key = game_key
        self.objectives_complete = {}
        self.sid = sid
        self.active = True
        self.score = 0

        self.location = None

        Player.__player_map[self.id] = self
        Player.__player_map[self.sid] = self

    def __del__(self):
        del Player.__player_map[self.id]

    def update_location(self, location):
        self.location = location

    def update_sid(self, sid):
        self.active = True
        del Player.__player_map[self.sid]
        self.sid = sid
        Player.__player_map[self.sid] = self

    def complete_objective(self, objective_id, time, url):
        self.objectives_complete[objective_id] = {
            'time': time,
            'url': url
        }

    def serialize(self):
        return {
            'id': self.id,
            'name': self.name,
            'objectives_complete': self.objectives_complete,
            'score': self.score,
        }
