package com.smartlogix.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogix.productservice.dto.StockUpdateRequest;
import com.smartlogix.productservice.entity.Product;
import com.smartlogix.productservice.repository.ProductRepository;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) //this tells Spring to start the application context. || WebEnvironment.MOCK : Tells Spring not to start Tomcat, instead, Spring uses MockMvc to pretend requests are arriving.
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StockReductionRestTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ProductRepository productRepository;

    private MockMvc mockMvc; //MockMvc simulates HTTP requests. We can do GET /products or POST or PUT without opening a browser, as if a real client made the request.
    private final ObjectMapper objectMapper = new ObjectMapper(); //to convert Java object into JSON

    private static Long testProductId; //the ID must be shared btw tests, so we use static to indicate it's one variable for the entire class

    @BeforeEach //runs before every test
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @BeforeAll //runs once before all tests
    //we create a product, save it and store its ID. Now every test can use this product
    static void seedProduct(@Autowired ProductRepository productRepository) {
        Product product = new Product();
        product.setProductName("calcetines azules");
        product.setStock(50);
        Product saved = productRepository.save(product);
        testProductId = saved.getProductId();
    }

    @Test
    @Order(1)
    @WithMockUser(roles = "ADMIN")
    void whenValidStockRequest_returns200() throws Exception {
        StockUpdateRequest request = new StockUpdateRequest();
        request.setProductId(testProductId);
        request.setQuantity(10); // stock is 50, reduce by 10 --> 40

        mockMvc.perform(post("/products/reduce-stock") //This simualtes the POST /products/reduce-stock
                        .with(csrf()) //Spring Security protects POST requests. This adds a fake CSRF token. Without it, we would get '403 Forbidden'
                        .contentType(MediaType.APPLICATION_JSON) //equivalent of 'Content-Type: application/json'
                        .content(objectMapper.writeValueAsString(request))) //produces the JSON "productId":1, "quantity":10
                .andExpect(status().isOk()) //expect http 200 OK
                .andExpect(jsonPath("$.productName").value("calcetines azules")) //Response "productName": "calcetines azules","stock": 40 || $ means root JSON object.
                .andExpect(jsonPath("$.stock").value(40));

        System.out.println("TEST 1 PASSED: POST /products/reduce-stock RETURNS 200, STOCK=40");
    }

    @Test
    @Order(2)
    @WithMockUser(roles = "ADMIN")
    void whenInsufficientStock_throwsRuntimeException() throws Exception {
        StockUpdateRequest request = new StockUpdateRequest();
        request.setProductId(testProductId);
        request.setQuantity(9999); //reduce 9999 but stock is only 40

        try {
            mockMvc.perform(post("/products/reduce-stock")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

        } catch (Exception e) {
            assertTrue(e.getCause() instanceof RuntimeException); // means "The underlying problem was a RuntimeException."
            System.out.println("TEST 2 PASSED: INSUFFICIENT STOCK CORRECTLY THROWS RuntimeException !");
            return;
        }

        fail("EXPECTED RuntimeException WAS NOT THROWN");
    }
}