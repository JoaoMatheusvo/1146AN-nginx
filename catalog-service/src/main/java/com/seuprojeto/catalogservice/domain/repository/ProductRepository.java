package com.seuprojeto.catalogservice.domain.repository;
import com.seuprojeto.catalogservice.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product, Long> {}
