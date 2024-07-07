package com.pnimac.webcrawler.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.pnimac.webcrawler.db.entity.Operation;
import com.pnimac.webcrawler.db.repository.OperationRepository;
import com.pnimac.webcrawler.exception.NotFoundException;

@Service
public class OperationServiceImpl implements OperationService {

	private final OperationRepository operationRepository;

	@Autowired
	public OperationServiceImpl(OperationRepository operationRepository) {
		this.operationRepository = operationRepository;
	}

	@Override
	public Operation getById(Long id) {
		return operationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException(String.format("No Operation found for the give id [%s]", id)));
	}

	@Override
	public Operation saveOrUpdate(Operation o) {
		return operationRepository.save(o);
	}

	@Override
	public List<Operation> getAll(PageRequest pageRequest) {
		return operationRepository.findAll(pageRequest).toList();
	}
}
