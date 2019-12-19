from flask import Flask, make_response, jsonify
from flask_bcrypt import Bcrypt
from flask_jwt import JWT
from flask_cors import CORS
from sqlalchemy import text

from .models import db, bcrypt, User
from .endpoints import app

def authenticate(username, password):
    # Check if test_user exists only for the sake of this exam,
    # just so that we have something to get an access token from.
    if username == 'test' and password == 'test':
        user = User(
            id=999999,
            username='test',
            password='test',
            first_name='First Name',
            last_name='Last Name',
            email='test@test.com'
        )

        return user

    # Correct auth impl
    user = User.query.filter(User.username==username).first()

    if user and user.check_credentials(password) and user.active:
        return user

def identity(payload):
    user_id = payload['identity']
    return user_id

def create_app():
    application = Flask(__name__)
    application.config.from_object('config')

    bcrypt = Bcrypt(application)
    db.init_app(application)
    jwt = JWT(application, authenticate, identity)

    # Not needed if frontend is served from same origin.
    # Resources and origins should be set up properly.
    CORS(application, resources={r'/': {'origins': '*'}})

    application.register_blueprint(app)
    application.register_error_handler(Exception, default_error_handler)

    return application

def default_error_handler(e):

    if hasattr(e, 'code'):
        message = e.name
        code = e.code
    else:
        message = e
        code = 500

    return make_response(
        jsonify(
            error_message='{0} {1}'.format(code, message)
        ), code
    )
