package com.pnimac.webcrawler.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pnimac.webcrawler.request.ScanRequest;
import com.pnimac.webcrawler.response.ScanResponse;
import com.pnimac.webcrawler.service.WebCrawlerService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/webcrawler")
@RestController
public class WebCrawlerController {

	private final WebCrawlerService crawlerService;

	@Autowired
	public WebCrawlerController(WebCrawlerService crawlerService) {
		this.crawlerService = crawlerService;
	}

	@PostMapping("/scan")
	public ResponseEntity<ScanResponse> scan(@Valid @RequestBody ScanRequest scanRequest) throws IOException, InterruptedException {
		log.info("WebCrawler Scan Request begins for: {}", scanRequest.getUrl());
		List<String> list = crawlerService.scan(scanRequest.getUrl(), scanRequest.getDomainOnly(),
				scanRequest.getBreakPoint());
		return ResponseEntity
				.ok(ScanResponse.builder().message("Successfully scanned!").data(list).code(2000).build());
	}

}
