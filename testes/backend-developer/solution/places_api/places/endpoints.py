from flask import Blueprint, jsonify, request, url_for, abort, make_response
from flask_jwt import jwt_required

from sqlalchemy import exc

from .models import db, Place

app = Blueprint('places', __name__)


@jwt_required()
@app.route('/api/v1.0/places', methods=['GET'])
def list_places():
    try:

        places = Place.query.filter().order_by('name')

        return make_response(
            jsonify(
                places=[p.serialize for p in places]
            ), 201
        )

    except Exception as ex:
        abort(500)


@jwt_required()
@app.route('/api/v1.0/places/new', methods=['POST'])
def create_place():

    if not request.json:
        abort(400)

    rules = ['name', 'slug', 'city', 'state']

    for r in rules:
        if not r in request.json:
            abort(400)

    new_place = Place(
        name=request.json['name'],
        slug=request.json['slug'],
        city=request.json['city'],
        state=request.json['state']
    )

    try:
        db.session.add(new_place)
        db.session.commit()
        return make_response(
            jsonify(
                place=new_place.serialize
            ), 201
        )
    except exc.IntegrityError:
        db.session.rollback()
        abort(409)
    except Exception as ex:
        db.session.rollback()
        abort(500)