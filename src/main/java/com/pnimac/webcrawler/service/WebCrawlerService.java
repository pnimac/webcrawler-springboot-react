package com.pnimac.webcrawler.service;

import java.io.IOException;
import java.util.List;

public interface WebCrawlerService {
	
	List<String> scan(String rootURL, Integer breakPoint) throws IOException, InterruptedException;

}
