package com.pnimac.webcrawler.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WebCrawlerServiceImpl implements WebCrawlerService {

	@Value("${default-pattern}")
	private String defaultPattern;

	private HttpClient httpClient;

	private static final int THREAD_POOL_SIZE = 10;

	@PostConstruct
	private void init() {
		log.info("default-pattern:{}", defaultPattern);
		httpClient = HttpClient.newBuilder().executor(Executors.newFixedThreadPool(THREAD_POOL_SIZE)).build();
	}

	@Override
	public List<String> scan(String rootURL, boolean rootOnly, Integer breakpoint)
			throws IOException, InterruptedException {

		Queue<String> urlQueue = new ConcurrentLinkedQueue<>(); // thread-safe queue to hold URLs to be processed.
		Set<String> visitedURLs = ConcurrentHashMap.newKeySet(); // thread-safe set to keep track of already visited URLs.

		UriComponents uriComponents = analyze(rootURL);
		log.info("Starting scan for url: {}", uriComponents.getHost());

		urlQueue.add(rootURL);
		visitedURLs.add(rootURL);

		AtomicInteger remainingBreakpoints = new AtomicInteger(breakpoint);//thread-safe
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		// Continues processing as long as there are URLs in the queue and the breakpoint limit has not been reached.
		while (!urlQueue.isEmpty() && remainingBreakpoints.get() > 0) {
			String currentUrl = urlQueue.poll(); // Retrieves and removes the head of the queue (the next URL to process).

			if (currentUrl != null) {
				CompletableFuture<Void> future = processUrl(currentUrl, rootOnly, remainingBreakpoints,
						uriComponents.getHost(), urlQueue, visitedURLs);
				futures.add(future);

				remainingBreakpoints.decrementAndGet();
			}
		}

		CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
		allOf.join(); // Wait for all tasks to complete

		log.info("num of results: {}", visitedURLs.size());
		return new ArrayList<>(visitedURLs);
	}

	private CompletableFuture<Void> processUrl(String url, boolean rootOnly, AtomicInteger breakpoint, String host,
			Queue<String> urlQueue, Set<String> visitedURLs) {
		try {
			URI uri = new URI(url);
			HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

			return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
				int statusCode = response.statusCode();
				log.info("Response Status Code: {}", statusCode);
				return response.body();
			}).thenAccept(body -> {
				if (body instanceof String) {
					log.info("Response Body: {}", body);
					Document doc = Jsoup.parse((String) body);
					Elements links = doc.select("a[href]");

					for (Element link : links) {
						String currentURL = link.attr("abs:href");
						boolean valid = !rootOnly || isValidUrl(currentURL, host);

						if (valid && visitedURLs.add(currentURL) && breakpoint.get() > 0) {
							urlQueue.add(currentURL);
							breakpoint.decrementAndGet();
						}
					}
				} else {
					log.error("Received unexpected response body type: {}", body.getClass().getName());
				}
			}).exceptionally(e -> {
				log.error("Error processing URL: {}", url, e);
				return null;
			});

		} catch (URISyntaxException e) {
			log.error("Invalid URL: {}", url, e);
			return CompletableFuture.completedFuture(null);
		}
	}

	private boolean isValidUrl(String url, String host) {
		try {
			URI uri = new URI(url);
			return uri.getHost().equals(host);
		} catch (URISyntaxException e) {
			return false;
		}
	}

	public UriComponents analyze(String url) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUri(URI.create(url));
		return builder.buildAndExpand();
	}
}
