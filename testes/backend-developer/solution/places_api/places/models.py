from flask_sqlalchemy import SQLAlchemy
from flask_bcrypt import Bcrypt
from sqlalchemy.ext.hybrid import hybrid_property

import datetime

bcrypt = Bcrypt()
db = SQLAlchemy()

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    first_name = db.Column(db.String(128), nullable=False)
    last_name = db.Column(db.String(255), nullable=False)
    email = db.Column(db.String(80), unique=True, nullable=False)
    username = db.Column(db.String(64), unique=True, nullable=False, index=True)
    _password = db.Column(db.String(128))
    active = db.Column(db.Boolean, nullable=False, default=True)

    @property
    def serialize(self):
        return {
            'id': self.id,
            'first_name': self.first_name,
            'last_name': self.last_name,
            'email': self.email,
        }

    @hybrid_property
    def password(self):
        return self._password

    @password.setter
    def password(self, entry):
        self._password = bcrypt.generate_password_hash(entry).decode('utf-8')

    def check_credentials(self, password):
        return bcrypt.check_password_hash(self.password, password)

    def __repr__(self):
        return ('{0} {1}'.format(self.first_name, self.last_name))


class Place(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    name = db.Column(db.String(128), unique=True, nullable=False)
    _slug = db.Column(db.String(128), unique=True, nullable=False)
    city = db.Column(db.String(128), nullable=False)
    state = db.Column(db.String(128), nullable=False)
    created_at = db.Column(db.DateTime(), nullable=False, default=datetime.datetime.now)
    updated_at = db.Column(db.DateTime())

    @hybrid_property
    def slug(self):
        return self._slug

    @slug.setter
    def slug(self, entry):
        reserved_keywords = ['new', 'edit', 'search']

        if ' ' in entry:
            e = Exception('slug field should NOT contain spaces')
            e.code = 400
            raise e

        if entry in reserved_keywords:
            e = Exception('slug field contains a reserved keyword, which is not allowed. Should NOT be any of: {0}'.format(', '.join(reserved_keywords)))
            e.code = 400
            raise e

        self._slug = entry

    @property
    def serialize(self):
        return {
            'id': self.id,
            'name': self.name,
            'slug': self.slug,
            'city': self.city,
            'state': self.state,
            'created_at': self.created_at,
            'updated_at': self.updated_at if self.updated_at else ''
        }