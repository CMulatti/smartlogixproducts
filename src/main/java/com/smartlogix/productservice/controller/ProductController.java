package com.smartlogix.productservice.controller;

import com.smartlogix.productservice.dto.StockUpdateRequest;
import com.smartlogix.productservice.entity.Product;
import com.smartlogix.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


 //@RestController = @Controller + @ResponseBody (returns JSON automatically)

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }


    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); //HTTP204
    }

    // POST products/reduce-stock
    //this endpoint is an internal endpoint, it is called by ORDERSERVICE via RestTemplate when an order is placed.
    @PostMapping("/reduce-stock")
    public ResponseEntity<Product> reduceStock(@RequestBody StockUpdateRequest request) {
        System.out.println("REDUCE STOCK HIT"); //to check for errors. If it is printed it means the request is being allowed by security, and the problem is somewhere else
        return ResponseEntity.ok(productService.reduceStock(request));
    }


}
