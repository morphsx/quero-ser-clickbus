import json

from places.models import db, Place

from .utils import get_access_token, create_test_place

endpoint = '/api/v1.0/places/edit'

def test_with_token(app):
    c = app.test_client()
    tk = get_access_token(c)

    place_id = create_test_place(app)

    response = c.put(
        endpoint,
        data = json.dumps(
            dict(
                id=place_id,
                fields=dict(
                    name=dict(
                        current_value='Test Place',
                        new_value='New Name'
                    )
                )
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 200
    assert 'place' in data

    with app.app_context():
        place = Place.query.filter(Place.name == 'New Name').first()

        assert place is not None


def test_without_token(app):
    c = app.test_client()

    place_id = create_test_place(app)

    response = c.put(
        endpoint,
        data = json.dumps(
            dict(
                id=place_id,
                fields=dict(
                    name=dict(
                        current_value='Test Place',
                        new_value='New Name'
                    )
                ),
            )
        ),
        content_type='application/json'
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 401
    assert 'place' not in data

    with app.app_context():
        place = Place.query.filter(Place.name == 'New Name').first()

        assert place is None


def test_without_data(app):
    c = app.test_client()
    tk = get_access_token(c)
    place_id = create_test_place(app)

    response = c.put(
        endpoint,
        # data = json.dumps(
        #     dict(

        #     )
        # ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 400
    assert 'place' not in data
    assert 'error_message' in data


def test_without_id(app):
    c = app.test_client()
    tk = get_access_token(c)

    response = c.put(
        endpoint,
        data = json.dumps(
            dict(
                fields=dict(
                    name=dict(
                        current_value='Test Place',
                        new_value='New Name',
                    )
                )
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 400
    assert 'place' not in data
    assert 'error_message' in data
    assert data['error_message'] == '''Request Body should have 'id' and 'fields' attributes'''

    with app.app_context():
        place = Place.query.filter(Place.name == 'New Name').first()

        assert place is None


def test_invalid_id(app):
    c = app.test_client()
    tk = get_access_token(c)
    place_id = 10

    response = c.put(
        endpoint,
        data = json.dumps(
            dict(
                id=place_id,
                fields=dict(
                    name=dict(
                        current_value='Test Place',
                        new_value='New Name',
                    )
                )
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 400
    assert 'place' not in data
    assert 'error_message' in data
    assert data['error_message'] == 'A Place with this ID does not exists'


def test_without_fields(app):
    c = app.test_client()
    tk = get_access_token(c)
    place_id = create_test_place(app)

    response = c.put(
        endpoint,
        data = json.dumps(
            dict(
                id=place_id
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 400
    assert 'place' not in data
    assert 'error_message' in data
    assert data['error_message'] == '''Request Body should have 'id' and 'fields' attributes'''


def test_unknown_fields(app):
    c = app.test_client()
    tk = get_access_token(c)
    place_id = create_test_place(app)

    response = c.put(
        endpoint,
        data = json.dumps(
            dict(
                id=place_id,
                fields=dict(
                    name=dict(
                        current_value='Test Place',
                        new_value='New Name'
                    ),
                    zipcode=dict(
                        current_value='14096280',
                        new_value='14600000'
                    )
                )
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 400
    assert 'place' not in data
    assert 'error_message' in data
    assert data['error_message'] == '''Invalid field attribute: 'zipcode'. Should be one of: name, slug, city, state'''

    with app.app_context():
        place = Place.query.filter(Place.name == 'New Name').first()

        assert place is None


def test_space_on_slug(app):
    c = app.test_client()
    tk = get_access_token(c)
    place_id = create_test_place(app)

    response = c.put(
        endpoint,
        data = json.dumps(
            dict(
                id=place_id,
                fields=dict(
                    name=dict(
                        current_value='Test Place',
                        new_value='New Name'
                    ),
                    slug=dict(
                        current_value='test_slug',
                        new_value='slug with space'
                    )
                )
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 400
    assert 'place' not in data
    assert 'error_message' in data
    assert data['error_message'] == 'slug field should NOT contain spaces'

    with app.app_context():
        place = Place.query.filter(Place.slug == 'slug with space').first()

        assert place is None


def test_without_current_value(app):
    c = app.test_client()
    tk = get_access_token(c)
    place_id = create_test_place(app)

    response = c.put(
        endpoint,
        data = json.dumps(
            dict(
                id=place_id,
                fields=dict(
                    name=dict(
                        new_value='New Name'
                    )
                )
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 400
    assert 'place' not in data
    assert 'error_message' in data
    assert data['error_message'] == '''Fields attribute should have 'current_value' and 'new_value' attributes'''

    with app.app_context():
        place = Place.query.filter(Place.name == 'New Name').first()

        assert place is None


def test_without_new_value(app):
    c = app.test_client()
    tk = get_access_token(c)
    place_id = create_test_place(app)

    response = c.put(
        endpoint,
        data = json.dumps(
            dict(
                id=place_id,
                fields=dict(
                    name=dict(
                        current_value='Test Place'
                    )
                )
            )
        ),
        content_type='application/json',
        headers={'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 400
    assert 'place' not in data
    assert 'error_message' in data
    assert data['error_message'] == '''Fields attribute should have 'current_value' and 'new_value' attributes'''


def test_wrong_current_value(app):
    c = app.test_client()
    tk = get_access_token(c)
    place_id = create_test_place(app)

    response = c.put(
        endpoint,
        data = json.dumps(
            dict(
                id=place_id,
                fields=dict(
                    name=dict(
                        current_value='Wrong Name',
                        new_value='New Name'
                    )
                )
            )
        ),
        content_type='application/json',
        headers = {'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 400
    assert 'place' not in data
    assert 'error_message' in data
    assert data['error_message'] == '''current_value for field 'name' is incorrect'''


def test_duplicate_name(app):
    c = app.test_client()
    tk = get_access_token(c)

    place_id=0

    with app.app_context():
        place1 = Place(
            name='Test Place',
            slug='test_slug',
            city='Test City',
            state='Test State'
        )

        place2 = Place(
            name='Test Place 2',
            slug='test_slug_2',
            city='Test City',
            state='Test State'
        )

        db.session.add(place1)
        db.session.add(place2)

        db.session.commit()

        place_id = place1.id

        assert place1 is not None
        assert place2 is not None

    response = c.put(
        endpoint,
        data = json.dumps(
            dict(
                id=place_id,
                fields=dict(
                    name=dict(
                        current_value='Test Place',
                        new_value='Test Place 2'
                    )
                )
            )
        ),
        content_type='application/json',
        headers = {'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 409
    assert 'place' not in data
    assert 'error_message' in data
    assert data['error_message'] == '''A Place with this 'name' or 'slug' already exists.'''


def test_duplicate_slug(app):
    c = app.test_client()
    tk = get_access_token(c)

    place_id=0

    with app.app_context():
        place1 = Place(
            name='Test Place',
            slug='test_slug',
            city='Test City',
            state='Test State'
        )

        place2 = Place(
            name='Test Place 2',
            slug='test_slug_2',
            city='Test City',
            state='Test State'
        )

        db.session.add(place1)
        db.session.add(place2)

        db.session.commit()

        place_id = place1.id

        assert place1 is not None
        assert place2 is not None

    response = c.put(
        endpoint,
        data = json.dumps(
            dict(
                id=place_id,
                fields=dict(
                    slug=dict(
                        current_value='test_slug',
                        new_value='test_slug_2'
                    )
                )
            )
        ),
        content_type='application/json',
        headers = {'Authorization': 'JWT {0}'.format(tk)}
    )

    data = json.loads(response.data.decode())

    assert response.status_code == 409
    assert 'place' not in data
    assert 'error_message' in data
    assert data['error_message'] == '''A Place with this 'name' or 'slug' already exists.'''