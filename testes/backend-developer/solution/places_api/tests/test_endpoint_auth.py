import json


def test_auth(app):
    c = app.test_client()

    response = c.post(
        '/auth',
        data=json.dumps(
            dict(
                username='test',
                password='test'
            )
        ),
        content_type='application/json'
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 200
    assert 'access_token' in data


def test_auth_no_user(app):
    c = app.test_client()

    response = c.post(
        '/auth',
        data=json.dumps(
            dict(
                username='test',
                password=''
            )
        ),
        content_type='application/json'
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 401
    assert 'access_token' not in data
    assert 'error' in data
    assert 'description' in data
    assert data['error'] == 'Bad Request'
    assert data['description'] == 'Invalid credentials'
