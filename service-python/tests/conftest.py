import pytest
from app.db.database import Base, engine, SessionLocal

@pytest.fixture(scope="session", autouse=True)
def setup_database():
    # Drop and recreate all tables before tests
    Base.metadata.drop_all(bind=engine)
    Base.metadata.create_all(bind=engine)
    yield
    Base.metadata.drop_all(bind=engine)
