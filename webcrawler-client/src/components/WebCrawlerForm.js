import React, { useState } from 'react';
import axios from 'axios';

const WebCrawlerForm = ({ onResults }) => {
  const [url, setUrl] = useState('');
  const [breakPoint, setBreakPoint] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('/webcrawler/scan', {
        url,
        breakPoint,
      });
      onResults(response.data.data);
    } catch (error) {
      console.error('Error scanning URL:', error);
    }
  };

  const handleClear = () => {
    setUrl('');
    setBreakPoint('');
  };

  return (
    <form onSubmit={handleSubmit} className="mb-4">
      <div className="mb-3">
        <label htmlFor="url" className="form-label">URL:</label>
        <input
          type="text"
          id="url"
          className="form-control"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          required
        />
      </div>
      <div className="mb-3">
        <label htmlFor="breakPoint" className="form-label">Breakpoint:</label>
        <input
          type="text"
          id="breakPoint"
          className="form-control"
          value={breakPoint}
          onChange={(e) => setBreakPoint(e.target.value)}
        />
      </div>
      <button type="submit" className="btn btn-primary me-2 custom-button">Scan</button>
      <button type="button" className="btn btn-secondary" onClick={handleClear}>Clear</button>
    </form>
  );
};

export default WebCrawlerForm;
