import json
from .utils import get_access_token

def test_with_token(app):
    c = app.test_client()

    tk = get_access_token(c)

    response = c.get(
        '/api/v1.0/places',
        headers={
            'Authorization': 'JWT {0}'.format(tk)
        }
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 200


def test_no_token(app):
    c = app.test_client()

    response = c.get(
        '/api/v1.0/places'
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 401