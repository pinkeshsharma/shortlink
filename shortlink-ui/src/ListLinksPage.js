import React, { useEffect, useState } from "react";

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || "/api";

function ListLinksPage() {
  const [links, setLinks] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const fetchLinks = async (pageNumber = 0) => {
    try {
      const res = await fetch(`${API_BASE_URL}/links?page=${pageNumber}&size=5`);
      if (!res.ok) throw new Error("Failed to fetch links");
      const data = await res.json();

      // Normalize Java (Spring Boot) vs Python (custom)
      if (data.content) {
        setLinks(data.content || []);
        setPage(data.pageable?.pageNumber ?? pageNumber);
        setTotalPages(data.totalPages ?? 0);
      } else if (data.items) {
        setLinks(data.items || []);
        setPage(data.page ?? pageNumber);
        setTotalPages(Math.ceil((data.total ?? 0) / (data.size ?? 5)));
      } else {
        setLinks([]);
        setPage(0);
        setTotalPages(0);
      }
    } catch (err) {
      console.error(err);
      setLinks([]);
      setPage(0);
      setTotalPages(0);
    }
  };

  useEffect(() => {
    fetchLinks(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const truncate = (str, max = 50) =>
    str && str.length > max ? str.substring(0, max) + "…" : str;

  return (
    <div style={styles.container}>
      <h2 style={styles.title}>📋 All Shortened Links</h2>

      <table style={styles.table}>
        <thead>
          <tr>
            <th style={styles.th}>Short URL</th>
            <th style={styles.th}>Original URL</th>
            <th style={styles.th}>Created At</th>
            <th style={styles.th}>Expires At</th>
          </tr>
        </thead>
        <tbody>
          {links.length > 0 ? (
            links.map((link, index) => {
              const path = link.shortUrl
                ? new URL(link.shortUrl).pathname
                : `/s/${link.shortCode}`;
              const display = `${window.location.origin}${path}`;

              return (
                <tr key={index} style={styles.row}>
                  <td style={styles.td}>
                    <a href={display} target="_blank" rel="noopener noreferrer" style={styles.shortLink}>
                      {display}
                    </a>
                  </td>
                  <td style={styles.td}>
                    <a
                      href={link.originalUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      style={styles.link}
                      title={link.originalUrl}
                    >
                      {truncate(link.originalUrl, 50)}
                    </a>
                  </td>
                  <td style={styles.td}>
                    {link.createdAt
                      ? new Date(link.createdAt).toLocaleString()
                      : "—"}
                  </td>
                  <td style={styles.td}>
                    {link.expiresAt
                      ? new Date(link.expiresAt).toLocaleString()
                      : "Never"}
                  </td>
                </tr>
              );
            })
          ) : (
            <tr>
              <td colSpan="4" style={styles.empty}>No links found</td>
            </tr>
          )}
        </tbody>
      </table>

      <div style={styles.pagination}>
        <button
          onClick={() => fetchLinks(page - 1)}
          disabled={page === 0}
          style={{ ...styles.button, ...(page === 0 ? styles.buttonDisabled : {}) }}
        >
          ⬅ Previous
        </button>

        <span style={styles.pageText}>
          Page {page + 1} of {totalPages}
        </span>

        <button
          onClick={() => fetchLinks(page + 1)}
          disabled={page + 1 >= totalPages}
          style={{ ...styles.button, ...(page + 1 >= totalPages ? styles.buttonDisabled : {}) }}
        >
          Next ➡
        </button>
      </div>
    </div>
  );
}

const styles = {
  container: {
    maxWidth: "1000px",
    margin: "40px auto",
    padding: "20px",
    textAlign: "center",
    fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif",
  },
  title: {
    marginBottom: "20px",
    fontSize: "26px",
    fontWeight: "600",
    color: "#333",
  },
  table: {
    width: "100%",
    borderCollapse: "collapse",
    backgroundColor: "#fff",
    boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
    borderRadius: "10px",
    overflow: "hidden",
    border: "1px solid #ddd",
    tableLayout: "fixed",
    wordWrap: "break-word",
  },
  row: { backgroundColor: "#fff", borderBottom: "1px solid #eee" },
  link: { color: "#007bff", textDecoration: "none" },
  shortLink: { color: "#28a745", fontWeight: "bold", textDecoration: "none" },
  th: {
    padding: "14px",
    fontWeight: "600",
    backgroundColor: "#f8f9fa",
    borderBottom: "2px solid #e9ecef",
  },
  td: {
    padding: "14px",
    textAlign: "center",
    verticalAlign: "middle",
    borderBottom: "1px solid #ddd",
    whiteSpace: "nowrap",
    overflow: "hidden",
    textOverflow: "ellipsis",
  },
  empty: { textAlign: "center", padding: "20px", color: "#999" },
  pagination: {
    marginTop: "25px",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    gap: "15px",
  },
  button: {
    padding: "10px 20px",
    border: "none",
    borderRadius: "6px",
    backgroundColor: "#007bff",
    color: "#fff",
    cursor: "pointer",
    fontSize: "14px",
    boxShadow: "0 2px 6px rgba(0,0,0,0.15)",
  },
  buttonDisabled: { backgroundColor: "#ccc", cursor: "not-allowed" },
  pageText: { fontWeight: "500", fontSize: "15px" },
};

export default ListLinksPage;
