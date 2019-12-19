import json

from .utils import get_access_token, create_test_place

endpoint = '/api/v1.0/places'

def test_with_token(app):
    c = app.test_client()
    tk = get_access_token(c)
    create_test_place(app)

    response = c.get(
        endpoint + '/test_slug',
        headers = {'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 200
    assert 'place' in data


def test_without_token(app):
    c = app.test_client()
    create_test_place(app)

    response = c.get(
        endpoint + '/test_slug'
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 401
    assert 'place' not in data


def test_unknown_slug(app):
    c = app.test_client()
    tk = get_access_token(c)
    create_test_place(app)

    response = c.get(
        endpoint + '/unknown_slug',
        headers = {'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 404
    assert 'place' not in data
    assert 'error_message' in data
    assert data['error_message'] == 'Place not found'