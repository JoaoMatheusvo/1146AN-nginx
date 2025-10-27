package com.seuprojeto.catalogservice.adapters.inbound.rest;
import com.seuprojeto.catalogservice.domain.Product;
import com.seuprojeto.catalogservice.application.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/catalog")
public class ProductController {
    private final ProductService service;
    public ProductController(ProductService service){this.service=service;}
    @GetMapping("/teste") public String hello(){ return "Catalog service rodando!"; }
    @GetMapping("/products") public List<Product> all(){ return service.findAll(); }
    @PostMapping("/products") public Product create(@RequestBody Product p){ return service.save(p); }
}
