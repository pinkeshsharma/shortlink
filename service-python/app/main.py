from fastapi import FastAPI
from app.api.routes import router
from app.db.database import Base, engine

app = FastAPI(title="ShortLink API")

# Ensure schema exists at startup
Base.metadata.create_all(bind=engine)

# Mount router without prefix
app.include_router(router)
