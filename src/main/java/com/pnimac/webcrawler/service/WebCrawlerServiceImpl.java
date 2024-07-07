package com.pnimac.webcrawler.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import com.pnimac.webcrawler.utils.ScanUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "WebCrawler")
@Service
public class WebCrawlerServiceImpl implements WebCrawlerService {

	private final ScanUtils scanUtils;

	private Pattern pattern;

	@Value("${default-pattern}")
	private String defaultPattern;
	
    private static final int THREAD_POOL_SIZE = 10;

	@Autowired
	public WebCrawlerServiceImpl(ScanUtils scanUtils) {
		this.scanUtils = scanUtils;
	}

	@PostConstruct
	private void init() {
		log.info("default-pattern:{}", defaultPattern);
		// create a regex pattern
		// the url is a single word ,and we'll be excluding two types of protocols
		pattern = Pattern.compile(defaultPattern);
	}

	@Override
	public List<String> scan(String rootURL, boolean rootOnly, Integer breakpoint) throws IOException {

		Queue<String> urlQueue = new LinkedList<>();		// urls to be scanned

        Set<String> visitedURLs = ConcurrentHashMap.newKeySet();		// already scanned urls

        UriComponents uriComponents = scanUtils.analyze(rootURL);
        log.info("host: {}", uriComponents.getHost());

        urlQueue.add(rootURL);		// initialize the queue with root url

        visitedURLs.add(rootURL);

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        while (!urlQueue.isEmpty() && breakpoint > 0) {
            URL url = new URL(urlQueue.remove());				// remove the head url string from this queue to begin traverse.

            executorService.submit(() -> {
                try {
                    Matcher matcher = pattern.matcher(scanUtils.getHtmlContent(url));
                    if (rootOnly) {
                    	// Each time the regex matches a URL in the HTML,
        				// add it to the queue for the next traverse and to the list of visited URLs.
                        processUrls(urlQueue, visitedURLs, matcher, breakpoint, uriComponents.getHost());
                    } else {
        				// case we needed to include all results
                        processUrls(urlQueue, visitedURLs, matcher, breakpoint);
                    }
                } catch (IOException e) {
                    log.error("Error processing URL: {}", url, e);
                }
            });

            breakpoint--;
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);

        log.info("num of results: {}", visitedURLs.size());
        return new ArrayList<>(visitedURLs);
		
	}

	
	private void processUrls(Queue<String> urlQueue, Set<String> visitedURLs, Matcher matcher, int breakpoint, String... host) {
        while (matcher.find() && breakpoint > 0) {
            String currentURL = matcher.group();
            boolean valid = host.length == 0 || currentURL.contains(host[0]);
            if (valid && visitedURLs.add(currentURL)) {
                urlQueue.add(currentURL);
                breakpoint--;
            }
        }
    }

    private boolean isValidUrl(String url, String host) {
        try {
            URL parsedUrl = new URL(url);
            return parsedUrl.getHost().equals(host);
        } catch (MalformedURLException e) {
            return false;
        }
    }
    
    
	
}
