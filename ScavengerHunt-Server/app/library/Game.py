import time
import uuid
from random import randint
from datetime import datetime, timedelta
from apscheduler.schedulers.gevent import GeventScheduler

from Player import Player
from Objective import Objective

PLAYER_TIMEOUT = 900  # seconds


def binary_search(seq, num):
    lo = 0
    hi = len(seq)
    while lo < hi:
        mid = (lo + hi)//2
        if num >= seq[mid]:
            hi = mid
        else:
            lo = mid + 1
    return hi


class Game:
    __game_map = {}

    @staticmethod
    def get_list():
        return Game.__game_map.keys()

    @staticmethod
    def get_by_key(key):
        if not key  in Game.__game_map:
            raise KeyError("Game not found with game key %s" % key)

        return Game.__game_map[key]

    def __init__(self, name, description=None):
        self.timestamp = time.time()

        while True:
            self.key = "".join(chr(randint(0, 25)+65) for i in xrange(4))
            if not self.key in Game.get_list():
                break

        self.name = name
        self.description = description
        self.players = {}
        self.objectives = {}
        self.scores = []

        self.started = False
        self.ended = False
        self.start_time = None

        Game.__game_map[self.key] = self

    def __del__(self):
        del Game.__game_map[self.key]

    def serialize_objectives(self):
        data = []
        for objective in self.objectives.values():
            data.append(objective.serialize())
        return data

    def add_player(self, name, sid):
        player = Player(name, self.key, sid)
        self.players[player.id] = player
        self.scores.append(0)
        return player

    def player_list(self):
        return self.players.values()

    def player_complete_objective(self, player, objective, time, url):
        player.complete_objective(objective.id, time, url)
        objective.player_complete(player.id)
        self.update_player_score(player)

    def update_player_score(self, player):
        score = player.score
        self.scores[binary_search(self.scores, score)] += 1
        player.score += 1

    def get_player_rank(self, player):
        return binary_search(self.scores, player.score) + 1

    def deactivate_player(self, player):
        player.active = False
        scheduler = GeventScheduler()
        timeout_date = datetime.now()+timedelta(seconds=PLAYER_TIMEOUT)
        scheduler.add_job(self.delete_player, 'date',
                          run_date=timeout_date, args=[player])
        g = scheduler.start()
        g.join()

    def delete_player(self, player):
        if player.active:
            return

        print "Player", player.name, "is being deleted for inactivity."

        self.scores.remove(player.score)
        del self.players[player.id]
        del player

    def add_objective(self, location, name, description):
        if self.started:
            raise AssertionError("Can't add objective if game has already begun")

        objective = Objective(location, name, description, self.key)
        self.objectives[objective.id] = objective

    def objective_list(self):
        return self.objectives.values()

    def delete_objective(self, objective_id):
        if game.started:
            raise AssertionError("Can't delete objective if game has already begun")

        if not objective_id in self.objectives:
            raise AssertionError("Objective does not exist")

        del self.objectives[objective_id]

    def start(self):
        if game.started:
            raise AssertionError("Game already started")

        self.start_time = time.time()
        self.started = True
