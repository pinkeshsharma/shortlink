import React, { useState } from "react";
import { TextField, Button, Paper, Typography, Box } from "@mui/material";

export default function CreateLinkPage() {
  const [url, setUrl] = useState("");
  const [customCode, setCustomCode] = useState("");
  const [shortUrl, setShortUrl] = useState("");

  const handleCreate = async () => {
    try {
      const res = await fetch("http://localhost:8080/shorten", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          originalUrl: url,
          customCode: customCode || null,
          tenantId: null,
          domain: null,
        }),
      });
      const data = await res.json();
      setShortUrl(data.shortUrl);
    } catch (err) {
      alert("Error creating short link");
    }
  };

  return (
    <Paper sx={{ p: 4 }}>
      <Typography variant="h5" gutterBottom>Create Short Link</Typography>
      <Box display="flex" flexDirection="column" gap={2}>
        <TextField label="Original URL" value={url} onChange={(e) => setUrl(e.target.value)} fullWidth />
        <TextField label="Custom Code (optional)" value={customCode} onChange={(e) => setCustomCode(e.target.value)} fullWidth />
        <Button variant="contained" onClick={handleCreate}>Create</Button>
        {shortUrl && (
          <Typography color="primary">
            Shortened URL: <a href={shortUrl} target="_blank" rel="noreferrer">{shortUrl}</a>
          </Typography>
        )}
      </Box>
    </Paper>
  );
}
