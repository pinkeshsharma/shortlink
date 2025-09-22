import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import { AppBar, Toolbar, Button, Container } from "@mui/material";
import CreateLinkPage from "./CreateLinkPage";
import ListLinksPage from "./ListLinksPage";

function App() {
  return (
    <Router>
      <AppBar position="static">
        <Toolbar>
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
