import json, random

from places.models import db, Place

from .utils import get_access_token

endpoint = '/api/v1.0/places/new'

def test_with_token(app):
    c = app.test_client()
    tk = get_access_token(c)

    response = c.post(
        endpoint,
        data = json.dumps(
            dict(
                name='Test Place',
                slug='test_slug',
                city='Test City',
                state='Test State'
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 201
    assert 'place' in data

    with app.app_context():
        place = Place.query.filter(Place.name == 'Test Place').first()

        assert place is not None


def test_without_token(app):
    c = app.test_client()

    response = c.post(
        '/api/v1.0/places/new',
        data = json.dumps(
            dict(
                name='Test Place',
                slug='test_slug',
                city='Test City',
                state='Test State'
            )
        ),
        content_type='application/json',
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 401
    assert 'place' not in data

    with app.app_context():
        place = Place.query.filter(Place.name == 'Test Place').first()

        assert place is None


def test_incomplete_data(app):
    c = app.test_client()
    tk = get_access_token(c)

    response = c.post(
        endpoint,
        data = json.dumps(
            dict(

            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 400
    assert 'error_message' in data


def test_without_name(app):
    c = app.test_client()
    tk = get_access_token(c)

    response = c.post(
        endpoint,
        data = json.dumps(
            dict(
                slug='test_slug',
                city='Test City',
                state='Test State'
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 400
    assert 'error_message' in data
    assert data['error_message'] == '''Required fields not met, they should be: \'name\', \'slug\', \'city\' and \'state\''''


def test_without_slug(app):
    c = app.test_client()
    tk = get_access_token(c)

    response = c.post(
        endpoint,
        data = json.dumps(
            dict(
                name='Test Name',
                city='Test City',
                state='Test State'
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 400
    assert 'error_message' in data
    assert data['error_message'] == '''Required fields not met, they should be: \'name\', \'slug\', \'city\' and \'state\''''


def test_without_city(app):
    c = app.test_client()
    tk = get_access_token(c)

    response = c.post(
        endpoint,
        data = json.dumps(
            dict(
                name='Test Name',
                slug='test_slug',
                state='Test State'
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 400
    assert 'error_message' in data
    assert data['error_message'] == '''Required fields not met, they should be: \'name\', \'slug\', \'city\' and \'state\''''


def test_without_state(app):
    c = app.test_client()
    tk = get_access_token(c)

    response = c.post(
        endpoint,
        data = json.dumps(
            dict(
                name='Test Name',
                slug='test_slug',
                city='Test City'
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 400
    assert 'error_message' in data
    assert data['error_message'] == '''Required fields not met, they should be: \'name\', \'slug\', \'city\' and \'state\''''


def test_with_space_on_slug(app):
    c = app.test_client()
    tk = get_access_token(c)

    response = c.post(
        endpoint,
        data = json.dumps(
            dict(
                name='Test Name',
                slug='test with space',
                city='Test City',
                state='Test State'
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 400
    assert 'error_message' in data
    assert data['error_message'] == '''slug field should NOT contain spaces'''


def test_with_reserved_keyword(app):
    c = app.test_client()
    tk = get_access_token(c)

    reserved_keywords = ['new', 'edit', 'search']

    response = c.post(
        endpoint,
        data = json.dumps(
            dict(
                name='Test Name',
                slug=random.choice(reserved_keywords),
                city='Test City',
                state='Test State'
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 400
    assert 'error_message' in data
    assert data['error_message'] == 'slug field contains a reserved keyword, which is not allowed. Should NOT be any of: {0}'.format(', '.join(reserved_keywords))


def test_duplicate_by_name(app):
    c = app.test_client()
    tk = get_access_token(c)

    # Registering first Place
    response = c.post(
        endpoint,
        data = json.dumps(
            dict(
                name='Test Place',
                slug='test_slug',
                city='Test City',
                state='Test State'
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 201
    assert 'place' in data

    with app.app_context():
        place = Place.query.filter(Place.name == 'Test Place').first()

        assert place is not None

    # Registering duplicate Place
    response = c.post(
        endpoint,
        data = json.dumps(
            dict(
                name='Test Place',
                slug='different_slug',
                city='Different City',
                state='Different State'
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 409
    assert 'place' not in data
    assert 'error_message' in data
    assert data['error_message'] == '''A Place with this \'name\' or \'slug\' already exists'''

    with app.app_context():
        place = Place.query.filter(Place.slug == 'different_slug').first()

        assert place is None


def test_duplicate_by_slug(app):
    c = app.test_client()
    tk = get_access_token(c)

    # Registering first Place
    response = c.post(
        endpoint,
        data = json.dumps(
            dict(
                name='Test Place',
                slug='test_slug',
                city='Test City',
                state='Test State'
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 201
    assert 'place' in data

    with app.app_context():
        place = Place.query.filter(Place.slug == 'test_slug').first()

        assert place is not None

    # Registering duplicate Place
    response = c.post(
        endpoint,
        data = json.dumps(
            dict(
                name='Different Name',
                slug='test_slug',
                city='Different City',
                state='Different State'
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 409
    assert 'place' not in data
    assert 'error_message' in data
    assert data['error_message'] == '''A Place with this \'name\' or \'slug\' already exists'''

    with app.app_context():
        place = Place.query.filter(Place.name == 'Different Name').first()

        assert place is None