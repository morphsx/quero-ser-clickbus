import json

from places.models import db, Place

def get_access_token(c):
    response = c.post(
        '/auth',
        data = json.dumps(
            dict(
                username='test',
                password='test'
            )
        ),
        content_type='application/json'
    )

    data = json.loads(response.data.decode())

    return data['access_token']


def create_test_place(app):

    with app.app_context():
        place = Place(
            name='Test Place',
            slug='test_slug',
            city='Test City',
            state='Test State'
        )

        db.session.add(place)
        db.session.commit()

        assert place is not None
        return place.id
