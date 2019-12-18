from flask import Blueprint, jsonify, request, url_for, abort, make_response
from flask_jwt import jwt_required

from .models import Place

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
