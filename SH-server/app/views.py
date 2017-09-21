
from app import app, Game
from flask import render_template, send_from_directory

## TEMPORRARY REMOVE FOR FINAL
@app.route('/')
def test():
    return Game.get_by_key(Game.get_list()[0]).key + render_template('test.html')

@app.route('/create')
def create_app():
    return send_from_directory('client', 'index.html')

@app.route('/create/<path:path>')
def send_resource(path):
    return send_from_directory('client', path)

@app.route('/create/static/<path:path>')
def send_static_resource(path):
    return send_from_directory('client/static', path)
