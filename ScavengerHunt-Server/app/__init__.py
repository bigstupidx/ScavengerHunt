from flask import Flask
from flask_socketio import SocketIO, emit

__all__ = ["Game", "Objective", "Player"]

from .library.Game import Game
from .library.Objective import Objective
from .library.Player import Player

app = Flask(__name__, static_url_path='')
socketio = SocketIO(app, async_mode="threading", debug=True)
from app import views
from app import request
from app import api
