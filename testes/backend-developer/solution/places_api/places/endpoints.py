from flask import Blueprint, jsonify, request, url_for, abort, make_response
from flask_jwt import jwt_required

app = Blueprint('places', __name__)