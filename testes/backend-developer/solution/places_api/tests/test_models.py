import json, secrets, datetime, random, pytest

from places.models import db, User, Place

def test_model_user(app):
    with app.app_context():
        a = User(
            username='teste',
            password='teste',
            first_name='Teste',
            last_name='Teste',
            email='a@b.com'
        )
        db.session.add(a)
        db.session.commit()
        assert a is not None

def test_model_place(app):
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
        assert place.updated_at == None
        assert place.created_at is not None

def test_model_place_reserved_keyword(app):
    reserved_keywords = ['new', 'search', 'edit']

    with app.app_context():
        with pytest.raises(Exception) as ex:
            place = Place(
                name='Test Place',
                slug=random.choice(reserved_keywords),
                city='Test City',
                state='Test State'
            )

            assert ex.code == 400
            assert place is None

def test_model_place_space(app):

    with app.app_context():
        with pytest.raises(Exception) as ex:
            place = Place(
                name='Test Place',
                slug='Test Slug With Space',
                city='Test City',
                state='Test State'
            )

            assert ex.code == 400
            assert place is None