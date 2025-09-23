from fastapi.testclient import TestClient
from app.main import app
import time

client = TestClient(app)


def test_create_shortlink_with_custom_code():
    payload = {
        "originalUrl": "https://example.com/page1",
        "customCode": "myCode123",
        "tenantId": "defaultTenant",
        "domain": "http://localhost:8000",
    }
    res = client.post("/shorten", json=payload)
    assert res.status_code == 200
    body = res.json()
    assert body["shortUrl"].endswith("/s/myCode123")
    assert body["createdAt"] is not None


def test_conflict_custom_code():
    payload1 = {
        "originalUrl": "https://example.com/page2",
        "customCode": "dupCode",
        "tenantId": "defaultTenant",
        "domain": "http://localhost:8000",
    }
    payload2 = {
        "originalUrl": "https://example.com/page3",
        "customCode": "dupCode",
        "tenantId": "defaultTenant",
        "domain": "http://localhost:8000",
    }
    r1 = client.post("/shorten", json=payload1)
    assert r1.status_code == 200
    r2 = client.post("/shorten", json=payload2)
    assert r2.status_code == 400
    assert "Custom code already exists" in r2.json()["detail"]


def test_redirect_and_expiry():
    code = "expire123"
    now = int(time.time() * 1000)
    past = now - 1000  # expired

    payload = {
        "originalUrl": "https://expired.com",
        "customCode": code,
        "tenantId": "defaultTenant",
        "domain": "http://localhost:8000",
        "expiresAt": past,
    }
    r = client.post("/shorten", json=payload)
    assert r.status_code == 200

    # Redirect should fail because expired
    redirect = client.get(f"/s/{code}", follow_redirects=False)
    assert redirect.status_code == 404


def test_list_links_pagination_and_delete():
    # Create multiple links
    for i in range(3):
        client.post(
            "/shorten",
            json={
                "originalUrl": f"https://list.com/{i}",
                "tenantId": "defaultTenant",
                "domain": "http://localhost:8000",
            },
        )

    res = client.get("/links?page=0&size=2")
    assert res.status_code == 200
    body = res.json()
    assert body["size"] == 2
    assert body["total"] >= 3
    assert len(body["items"]) == 2

    # Extract a code from one of the returned shortUrls
    short_url = body["items"][0]["shortUrl"]
    code = short_url.split("/s/")[1]

    # Delete
    del_res = client.delete(f"/shortlinks/{code}")
    assert del_res.status_code == 204

    # Delete again should 404
    del_res2 = client.delete(f"/shortlinks/{code}")
    assert del_res2.status_code == 404
