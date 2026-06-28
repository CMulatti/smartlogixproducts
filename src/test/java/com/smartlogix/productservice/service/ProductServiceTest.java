package com.smartlogix.productservice.service;

import com.smartlogix.productservice.dto.StockUpdateRequest;
import com.smartlogix.productservice.entity.Product;
import com.smartlogix.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @ExtendWith(MockitoExtension.class): Activates Mockito annotations
 * @Mock : creates a fake ProductRepository
 * @InjectMocks: creates a real ProductService and injects the fake repository into it
 */

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    ProductRepository productRepository; //fake (no real DB)

    @InjectMocks
    ProductService productService; //real service, fake repository inside

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // A reusable product for each test
        testProduct = new Product();
        testProduct.setProductId(1L);
        testProduct.setProductName("calcetines azules");
        testProduct.setStock(50);
    }

    //T1: reduceStock with enough stock
    @Test
    void whenStockIsSufficient_reduceStockSucceeds() {
        //Arrange (preparation "given..."): fake repository returns our test product when asked for id=1
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));  //If the service asks the repository for product 1, give back testProduct.
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0)); //If the service saves a product, just return that same product.

        StockUpdateRequest request = new StockUpdateRequest(); //create the request object
        request.setProductId(1L);
        request.setQuantity(10); //request 10 (we have 50)

        //Act (our actual action being tested, "when...")
        Product result = productService.reduceStock(request);

        // Assert (verification, "then...")
        assertEquals(40, result.getStock(), "Stock should be 50 - 10 = 40");
        verify(productRepository).save(testProduct); //confirm save was called

        System.out.println("TEST PASSED: STOCK REDUCED CORRECTLY!");
        System.out.println("Before: 50 | Requested: 10 | After: " + result.getStock());

    }

    //T2: reduceStock with insufficient stock
    @Test
    void whenStockIsInsufficient_reduceStockThrowsException() {
        // Arrange: product has only 5 units
        testProduct.setStock(5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        StockUpdateRequest request = new StockUpdateRequest();
        request.setProductId(1L);
        request.setQuantity(10); // request 10, we only have 5

        //Act & Assert: should throw RuntimeException
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productService.reduceStock(request));

        assertTrue(ex.getMessage().contains("NO HAY SUFICIENTE STOCK"),
                "Exception message should mention insufficient stock");

        // Confirm save was NEVER called ( no stock change should happen)
        verify(productRepository, never()).save(any());

        System.out.println("TEST PASSED: INSUFFICIENT STOCK, CORRECTLY REJECTED!");
        System.out.println("Error: " + ex.getMessage());

    }

    //T3: getProductById not found
    @Test
    void whenProductNotFound_throwsException() {
        //Arrange: repository returns empty (product doesn't exist)
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productService.getProductById(99L));

        assertTrue(ex.getMessage().contains("Producto no encontrado"));

        System.out.println("TEST PASSED: PRODUCT NOT FOUND HANDLED CORRECTLY!");
        System.out.println("Error: " + ex.getMessage());

    }
}

