import json

from .utils import get_access_token, create_test_place

endpoint = '/api/v1.0/places/search'

def test_with_token(app):
    c = app.test_client()
    tk = get_access_token(c)
    create_test_place(app)

    response = c.get(
        endpoint + '/tes',
        headers = {'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 200
    assert 'places' in data


def test_without_token(app):
    c = app.test_client()
    create_test_place(app)

    response = c.get(
        endpoint + '/test'
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 401
    assert 'places' not in data


def test_unknown_name(app):
    c = app.test_client()
    tk = get_access_token(c)
    create_test_place(app)

    response = c.get(
        endpoint + '/unknown',
        headers = {'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 200
    assert 'places' in data
    assert len(data['places']) == 0