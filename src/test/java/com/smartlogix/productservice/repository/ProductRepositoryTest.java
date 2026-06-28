package com.smartlogix.productservice.repository;

import com.smartlogix.productservice.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    //T1: When a product is saved, it can later be found by its ID.
    @Test
    @Order(1)
    void whenProductSaved_canBeFoundById() {
        Product product = new Product();
        product.setProductName("zapatillas test");
        product.setStock(25);

        Product saved = productRepository.save(product);
        Optional<Product> found = productRepository.findById(saved.getProductId());

        assertTrue(found.isPresent(), "Product should be found after saving");
        assertEquals("zapatillas test", found.get().getProductName());
        assertEquals(25, found.get().getStock());

        System.out.println("TEST 1 PASSED: ID=" + saved.getProductId() + " | " + saved.getProductName());
    }

    //T2: If several products are saved, findAll() should return them.
    @Test
    @Order(2)
    void whenMultipleProductsSaved_findAllReturnsThem() {
        Product p1 = new Product(); p1.setProductName("polera roja");
        p1.setStock(10);
        Product p2 = new Product(); p2.setProductName("hoodie");
        p2.setStock(5);
        Product p3 = new Product(); p3.setProductName("calcetines azules");
        p3.setStock(50);

        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);

        List<Product> all = productRepository.findAll();

        assertTrue(all.size() >= 3, "Should find at least the 3 saved products");

        System.out.println("TEST 2 PASSED: findAll RETURNED " + all.size() + " PRODUCTS");
        all.forEach(p -> System.out.println("  - " + p.getProductName() + " (stock: " + p.getStock() + ")"));
    }

    //T3:When the product does not exist, findById() should return an empty result.
    @Test
    @Order(3)
    void whenProductDoesNotExist_findByIdReturnsEmpty() {
        Optional<Product> found = productRepository.findById(9999L);
        assertFalse(found.isPresent(), "Should return empty for non-existent id");
        System.out.println("TEST 3 PASSED: NON-EXISTING PRODUCT CORRECTLY RETURNS EMPTY");
    }
}