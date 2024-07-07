package com.pnimac.webcrawler.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;

public interface CrudService<T, I> {

	T getById(I id);

	T saveOrUpdate(T object);

	List<T> getAll(PageRequest pagerequest);

}
