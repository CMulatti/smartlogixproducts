package com.smartlogix.productservice.service;

import com.smartlogix.productservice.dto.StockUpdateRequest;
import com.smartlogix.productservice.entity.Product;
import com.smartlogix.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//@Service tells Spring to manage this class as a bean (singleton).
//RequiredArgsConstructor (Lombok) auto-generates a constructor that injects all final fields, that's how we get productRepository injected.
/**Lombok is saving us having to type:
 *  public ProductService (ProductRepository repository){
 *  this.repository = repository;}
 *  So when Lombok sees "private final ProductRepository productRepository;" it generates the constructor injection automatically*/

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado, ID prod: " + id));
    }

    //Stock update called by ORDERSERVICE
    public Product reduceStock(StockUpdateRequest request) {
        Product product = getProductById(request.getProductId());

        int newStock = product.getStock() - request.getQuantity();

        if (newStock < 0){
            throw new RuntimeException(
                    "No hay suficiente stock de " + product.getProductName() +
                            ". Cantidad disponible: " + product.getStock() +
                            ". Cantidad solicitada: " + request.getQuantity()
            );
        }
        product.setStock(newStock);
        return productRepository.save(product);
    }

    //Create, Update, Delete (Admin operations)
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = getProductById(id);
        existing.setProductName(updatedProduct.getProductName());
        existing.setStock(updatedProduct.getStock());
        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        getProductById(id);
        productRepository.deleteById(id);
    }
}
