import React, { useState } from "react";
import { TextField, Button, Paper, Typography, Box } from "@mui/material";

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || "/api";

// Helper: normalize backend shortUrl to UI origin
const toUiShortUrl = (shortUrl, fallbackCode) => {
  try {
    const path = shortUrl ? new URL(shortUrl).pathname : `/s/${fallbackCode}`;
    return `${window.location.origin}${path}`;
  } catch {
    return `${window.location.origin}/s/${fallbackCode}`;
  }
};

export default function CreateLinkPage() {
  const [url, setUrl] = useState("");
  const [customCode, setCustomCode] = useState("");
  const [expiresAt, setExpiresAt] = useState(""); // optional expiry
  const [shortUrl, setShortUrl] = useState("");
  const [error, setError] = useState("");

  const handleCreate = async () => {
    setError("");
    setShortUrl("");

    try {
      const payload = {
        originalUrl: url,
        customCode: customCode || null,
      };

      if (expiresAt) {
        const ts = new Date(expiresAt).getTime();
        if (!isNaN(ts)) {
          payload.expiresAt = ts;
        }
      }

      const res = await fetch(`${API_BASE_URL}/shorten`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Failed to create short link");
      }

      const data = await res.json();
      setShortUrl(toUiShortUrl(data.shortUrl, data.shortCode || customCode));
    } catch (err) {
      setError(err.message || "Error creating short link");
    }
  };

  return (
    <Paper sx={{ p: 4 }}>
      <Typography variant="h5" gutterBottom>
        Create Short Link
      </Typography>
      <Box display="flex" flexDirection="column" gap={2}>
        <TextField
          label="Original URL"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          fullWidth
        />
        <TextField
          label="Custom Code (optional)"
          value={customCode}
          onChange={(e) => setCustomCode(e.target.value)}
          fullWidth
        />
        <TextField
          label="Expires At (optional)"
          type="datetime-local"
          value={expiresAt}
          onChange={(e) => setExpiresAt(e.target.value)}
          InputLabelProps={{ shrink: true }}
          fullWidth
        />
        <Button variant="contained" onClick={handleCreate}>
          Create
        </Button>
        {shortUrl && (
          <Typography color="primary">
            Shortened URL:{" "}
            <a href={shortUrl} target="_blank" rel="noreferrer">
              {shortUrl}
            </a>
          </Typography>
        )}
        {error && <Typography color="error">{error}</Typography>}
      </Box>
    </Paper>
  );
}
