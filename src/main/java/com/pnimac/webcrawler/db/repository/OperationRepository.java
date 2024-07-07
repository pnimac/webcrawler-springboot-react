package com.pnimac.webcrawler.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pnimac.webcrawler.db.entity.Operation;

public interface OperationRepository extends JpaRepository<Operation, Long> {

}
