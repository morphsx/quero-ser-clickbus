import os
from os.path import abspath, dirname, join

_cwd = dirname(abspath(__file__))

from datetime import timedelta

SECRET_KEY = os.environ.get('CLICKBUS_SECRET_KEY', None)
SQLALCHEMY_DATABASE_URI = os.environ.get('CLICKBUS_DB_URI', None)
SQLALCHEMY_ECHO = False
SQLALCHEMY_TRACK_MODIFICATIONS = False
JWT_EXPIRATION_DELTA = timedelta(minutes=30)
BCRYPT_LOG_ROUNDS = 12
DEBUG = True
ENV = 'development'
JSON_AS_ASCII = False