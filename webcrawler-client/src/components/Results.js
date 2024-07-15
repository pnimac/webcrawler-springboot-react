import React from 'react';

const Results = ({ results }) => {
  // Function to group URLs by domain
  const groupByDomain = (urls) => {
    const grouped = {};
    urls.forEach(url => {
      try {
        const domain = new URL(url).hostname;
        if (!grouped[domain]) {
          grouped[domain] = [];
        }
        grouped[domain].push(url);
      } catch (error) {
        console.error(`Invalid URL: ${url}`, error);
        // Optionally, you can handle or log the error here
      }
    });
    return grouped;
  };

  // Render site map structure
  const renderSiteMap = (groupedUrls) => {
    return (
      <ul className="list-group">
        {Object.keys(groupedUrls).map((domain, index) => (
          <li key={index} className="list-group-item">
            <strong>{domain}</strong>
            <ul className="list-group mt-2">
              {groupedUrls[domain].map((url, subIndex) => (
                <li key={subIndex}>
                  <a href={url}>{url}</a>
                </li>
              ))}
            </ul>
          </li>
        ))}
      </ul>
    );
  };

  // Group URLs by domain
  const groupedUrls = groupByDomain(results);

  return (
    <div className="card mt-4">
      <div className="card-header">
        <h2>Results As Site Map <i className="bi bi-list-task"></i></h2>
      </div>
      <div className="card-body">
        {results.length === 0 ? (
          <p>No URLs found.</p>
        ) : (
          renderSiteMap(groupedUrls)
        )}
      </div>
    </div>
  );
};

export default Results;
