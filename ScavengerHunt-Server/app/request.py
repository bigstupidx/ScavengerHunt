import datetime
import json
from flask import request
from flask_socketio import emit
from app import app, socketio, Game, Objective, Player

# Fake data
game1 = Game('game1')
print game1.key

game1.add_player('Yan', '')
game1.add_player('Sas', '')

game1.add_objective({"lat": 40.633388, "long": -73.931379}, "NOT CALI OR BUST", "description")
game1.add_objective({"lat": 40.632397, "long": -73.931349}, "YAN IS GAY", "description2")
game1.add_objective({"lat": 40.632377, "long": -73.931364}, "MEMES", "description2")

for o in game1.objective_list():
    print o.id

@socketio.on('connect')
def connect():
    print "Someone connected!"


@socketio.on('connection')
def connection(message):
    print "Connection: ", message['data'], request.sid

    if message['data']['type'] == 'player':
        if 'playerId' in message['data']:
            player = Player.get_by_id(message['data']['playerId'])
            print "Player", player.name, "reconnected"

            print "Updating player sid to:", request.sid
            player.update_sid(request.sid)
            emit('connection', {'data': {'objectives complete': player.objectives_complete}})

        else:
            name = message['data']['name']
            # Use actual game code when implemented
            # game = Game.get_by_key(message['data']['game key'])
            player = game1.add_player(name, request.sid)

            emit('connection', {'data': {'id': player.id}})

        # DEBUG
        print "Current players: "
        for p in game1.player_list():
            print p.name,
        print

@socketio.on('disconnect')
def disconnect():
    player = Player.get_by_id(request.sid)
    if player:
        print player.name, "disconnected.", request.sid
        if player.active:
            game = Game.get_by_key(player.game_key)
            print "Deactivating player"
            game.deactivate_player(player)
    else:
        print "Someone disconnected", request.sid


@socketio.on('rank')
def rank():
    player = Player.get_by_id(request.sid)
    print player.name, "requested its rank."

    game = Game.get_by_key(player.game_key)
    rank = game.get_player_rank(player)

    emit('rank', {'data': {
        'rank': rank,
        'num players': len(game.player_list())
    }})


@socketio.on('objective')
def rank(message):
    player = Player.get_by_id(request.sid)
    print player.name, "completed an objective: ", message

    game = Game.get_by_key(player.game_key)
    print "game:", game.name
    objective = Objective.get_by_id(message['data']['objectiveId'])
    print "completing: ", objective.name

    if player.id in objective.players_completed:
        print "Player ", player.name, "is trying to complete objective", objective.name, "twice!"
        return

    time = message['data']['time']
    pic_url = message['data']['url']

    game.player_complete_objective(player, objective, time, pic_url)
    print "Process completed objective"
