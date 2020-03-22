package com.example.restservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.example.restservice.model.Product;

public interface ProductRepository extends SolrCrudRepository<Product, String> {

    public List<Product> findByName(String name);

}
