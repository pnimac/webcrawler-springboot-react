import React, { useState } from 'react';
import WebCrawlerForm from './components/WebCrawlerForm';
import Results from './components/Results';
import './App.css';

const App = () => {
  const [results, setResults] = useState([]);

  return (
    <div className="container mt-5">
      <div className="bg-primary text-white p-3 mb-4 text-center rounded">
        <h1 className="d-inline">Pnima's Web Crawler</h1> <i className="bi bi-search"></i>
      </div>
      <WebCrawlerForm onResults={setResults} />
      <Results results={results} />
    </div>
  );
};

export default App;
