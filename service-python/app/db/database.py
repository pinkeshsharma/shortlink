import os
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, DeclarativeBase, scoped_session

DB_URL = os.getenv("DATABASE_URL", "sqlite:///./shortlinks.db")

# For SQLite, check_same_thread must be False with scoped sessions
engine = create_engine(DB_URL, connect_args={"check_same_thread": False} if DB_URL.startswith("sqlite") else {})
SessionLocal = scoped_session(sessionmaker(bind=engine, autocommit=False, autoflush=False))

class Base(DeclarativeBase):
    pass

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
