import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import { AppBar, Toolbar, Button, Container, Typography, Box } from "@mui/material";
import CreateLinkPage from "./CreateLinkPage";
import ListLinksPage from "./ListLinksPage";
import logo from "./logo.png";

function App() {
  return (
    <Router>
      <AppBar position="static" sx={{ backgroundColor: "#1976d2" }}>
        <Toolbar>
          <Box display="flex" alignItems="center" sx={{ flexGrow: 1 }}>
            <img
              src={logo}
              alt="App Logo"
              style={{ width: "40px", height: "40px", marginRight: "12px" }}
            />
            <Typography variant="h6" sx={{ fontWeight: "bold" }}>
              ShortLink Manager
            </Typography>
          </Box>
          <Button color="inherit" component={Link} to="/">Create Link</Button>
          <Button color="inherit" component={Link} to="/list">List Links</Button>
        </Toolbar>
      </AppBar>
      <Container sx={{ marginTop: 4 }}>
        <Routes>
          <Route path="/" element={<CreateLinkPage />} />
          <Route path="/list" element={<ListLinksPage />} />
        </Routes>
      </Container>
    </Router>
  );
}

export default App;
