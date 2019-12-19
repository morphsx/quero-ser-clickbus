import pytest

from os.path import abspath, dirname, join
import os

from flask import Flask
from flask_bcrypt import Bcrypt

from places.models import db, bcrypt
from places.endpoints import app as bp
from places import create_app

_cwd = dirname(abspath(__file__))

@pytest.fixture
def app():
    app = create_app()
    app.config['TESTING'] = True
    app.config['SECRET_KEY'] = 'secret_key'
    app.config['JWT_SECRET_KEY'] = 'secret_key'
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:////home/giovani/clickbus_test_db.db'
    app.config['SQLALCHEMY_ECHO'] = False
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    app.config['BCRYPT_LOG_ROUNDS'] = 12
    app.config['DEBUG'] = True
    app.config['ENV'] = 'development'
    app.config['JSON_AS_ASCII'] = False

    app.register_blueprint(bp)

    with app.app_context():
        db.create_all()
        yield app
        db.session.remove()
        db.drop_all()