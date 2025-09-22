import React, { useEffect, useState } from "react";

function ListLinksPage() {
  const [links, setLinks] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const fetchLinks = async (pageNumber = 0) => {
    try {
      const res = await fetch(`http://localhost:8080/links?page=${pageNumber}&size=5`);
      if (!res.ok) throw new Error("Failed to fetch links");
      const data = await res.json();

      setLinks(data.content);
      setPage(data.pageable.pageNumber);
      setTotalPages(data.totalPages);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchLinks(page);
  }, []);

  const truncate = (str, max = 50) =>
    str.length > max ? str.substring(0, max) + "…" : str;

  return (
    <div style={styles.container}>
      <h2 style={styles.title}>📋 All Shortened Links</h2>

      <table style={styles.table}>
        <thead>
          <tr>
            <th style={styles.th}>ID</th>
            <th style={styles.th}>Original URL</th>
            <th style={styles.th}>Short URL</th>
            <th style={styles.th}>Tenant</th>
            <th style={styles.th}>Created At</th>
            <th style={styles.th}>Expires At</th>
          </tr>
        </thead>
        <tbody>
          {links.length > 0 ? (
            links.map((link) => (
              <tr key={link.id} style={styles.row}>
                <td style={styles.td}>{link.id.slice(0, 8)}...</td>
                <td style={styles.td}>
                  <a
                    href={link.originalUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    style={styles.link}
                    title={link.originalUrl} // ✅ tooltip with full link
                  >
                    {truncate(link.originalUrl, 50)}
                  </a>
                </td>
                <td style={styles.td}>
                  <a
                    href={`${link.domain}/s/${link.shortCode}`}
                    target="_blank"
                    rel="noopener noreferrer"
                    style={styles.shortLink}
                    title={`${link.domain}/s/${link.shortCode}`}
                  >
                    {`${link.domain}/s/${link.shortCode}`}
                  </a>
                </td>
                <td style={styles.td}>{link.tenantId}</td>
                <td style={styles.td}>
                  {new Date(link.createdAt).toLocaleString()}
                </td>
                <td style={styles.td}>
                  {link.expiresAt
                    ? new Date(link.expiresAt).toLocaleString()
                    : "Never"}
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="6" style={styles.empty}>
                No links found
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {/* Pagination */}
      <div style={styles.pagination}>
        <button
          onClick={() => fetchLinks(page - 1)}
          disabled={page === 0}
          style={{
            ...styles.button,
            ...(page === 0 ? styles.buttonDisabled : {}),
          }}
        >
          ⬅ Previous
        </button>

        <span style={styles.pageText}>
          Page {page + 1} of {totalPages}
        </span>

        <button
          onClick={() => fetchLinks(page + 1)}
          disabled={page + 1 >= totalPages}
          style={{
            ...styles.button,
            ...(page + 1 >= totalPages ? styles.buttonDisabled : {}),
          }}
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
    tableLayout: "fixed", // ✅ prevents stretching
    wordWrap: "break-word", // ✅ handles long text gracefully
  },
  row: {
    backgroundColor: "#fff",
    borderBottom: "1px solid #eee",
  },
  link: {
    color: "#007bff",
    textDecoration: "none",
  },
  shortLink: {
    color: "#28a745",
    fontWeight: "bold",
    textDecoration: "none",
  },
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
    textOverflow: "ellipsis", // ✅ adds … for long text
  },
  empty: {
    textAlign: "center",
    padding: "20px",
    color: "#999",
  },
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
  buttonDisabled: {
    backgroundColor: "#ccc",
    cursor: "not-allowed",
  },
  pageText: {
    fontWeight: "500",
    fontSize: "15px",
  },
};

export default ListLinksPage;
