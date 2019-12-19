import datetime

from flask import Blueprint, jsonify, request, url_for, abort, make_response
from flask_jwt import jwt_required

from sqlalchemy import exc

from .models import db, Place

app = Blueprint('places', __name__)


@app.route('/api/v1.0/places', methods=['GET'])
@jwt_required()
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


@app.route('/api/v1.0/places/new', methods=['POST'])
@jwt_required()
def create_place():

    if not request.json:
        abort(400)

    rules = ['name', 'slug', 'city', 'state']

    for r in rules:
        if not r in request.json:
            return make_response(
                jsonify(
                    error_message='Required fields not met, they should be: \'name\', \'slug\', \'city\' and \'state\''
                ), 400
            )

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
        return make_response(
            jsonify(
                error_message='A Place with this \'name\' or \'slug\' already exists'
            ), 409
        )
    except Exception as ex:
        db.session.rollback()
        abort(500)


@app.route('/api/v1.0/places/edit', methods=['PUT'])
@jwt_required()
def edit_place():

    # Checking if request has json data
    if not request.json:
        abort(400)

    # Checking if json data contains 'id' and 'fields' attributes
    if not 'id' in request.json or not 'fields' in request.json:
        return make_response(
            jsonify(
                error_message='Request Body should have \'id\' and \'fields\' attributes'
            ), 400
        )

    # Checking if 'id' of place informed exists
    place = Place.query.filter(Place.id==request.json['id']).first()

    if not place:
        return make_response(
            jsonify(
                error_message='A Place with this ID does not exists'
            ), 400
        )

    allowed_fields = ['name', 'slug', 'city', 'state']

    # Checking if 'fields' attribute has any field set for change
    if len(request.json['fields']) <= 0:
        return make_response(
            jsonify(
                error_message='No field specified for change'
            ), 400
        )

    for f in request.json['fields']:
        # Checking if 'fields' attribute has any unknown or incorrect field name
        if f not in allowed_fields:
            return make_response(
                jsonify(
                    error_message='Invalid field attribute: \'{0}\'. Should be one of: {1}'.format(f, ', '.join(allowed_fields))
                ), 400
            )

        # Checking if given field contains 'current_value' and 'new_value' attributes
        if not 'current_value' in request.json['fields'][f] or not 'new_value' in request.json['fields'][f]:
            return make_response(
                jsonify(
                    error_message='Fields attribute should have \'current_value\' and \'new_value\' attributes'
                ), 400
            )

        current_value = request.json['fields'][f]['current_value']
        new_value = request.json['fields'][f]['new_value']

        # Checking if 'current_value' of given field name corresponds to current_value of the model
        if not place.serialize[f] == current_value:
            return make_response(
                jsonify(
                    error_message='current_value for field \'{0}\' is incorrect'.format(f)
                ), 400
            )

        # Setting new_value to given field name in requested model instance
        setattr(place, f, new_value)

    place.updated_at = datetime.datetime.now()

    try:
        db.session.commit()

        return make_response(
            jsonify(
                place=place.serialize
            ), 200
        )
    except exc.IntegrityError:
        db.session.rollback()
        return make_response(
            jsonify(
                error_message='A Place with this \'name\' or \'slug\' already exists.'
            ), 409
        )
    except Exception:
        db.session.rollback()
        abort(500)


@app.route('/api/v1.0/places/<string:slug>', methods=['GET'])
@jwt_required()
def fetch_place(slug):
    
    try:
        place = Place.query.filter(Place.slug == slug).first()

        if place:
            return make_response(
                jsonify(
                    place=place.serialize
                ), 200
            )
        else:
            return make_response(
                jsonify(
                    error_message='Place not found'
                ), 404
            )
    except Exception as ex:
        abort(500)
