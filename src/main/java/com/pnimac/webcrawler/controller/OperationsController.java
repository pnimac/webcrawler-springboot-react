package com.pnimac.webcrawler.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.pnimac.webcrawler.db.entity.Operation;
import com.pnimac.webcrawler.model.GenericResponse;
import com.pnimac.webcrawler.service.OperationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/ops")
@RestController
public class OperationsController {

	private final OperationService operationService;

	public OperationsController(OperationService operationService) {
		this.operationService = operationService;
	}

	@GetMapping(value = "/all")
	public GenericResponse getAll(@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "20") Integer size,
			@RequestParam(required = false, defaultValue = "id") String sortBy,
			@RequestParam(required = false, defaultValue = "ASC") String direction) {

		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(direction), sortBy));

		log.info("requesting Operations info");

		List<Operation> list = operationService.getAll(pageRequest);
		return GenericResponse.builder().message("Success").code(2000).data(list).build();
	}
}