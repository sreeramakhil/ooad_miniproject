package com.stockflow.service;

import com.stockflow.model.Product;
import com.stockflow.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public List<Product> searchProducts(String query) {
        if (query == null || query.isBlank()) return getAllProducts();
        return repo.findByNameContainingIgnoreCase(query);
    }

    public Optional<Product> getById(String id) {
        return repo.findById(id);
    }

    public Product save(Product product) {
        return repo.save(product);
    }

    public Product addProduct(String name, Double price, Integer stock, String icon) {
        // Generate next product ID
        long count = repo.count() + 1;
        String id = String.format("PRD-%03d", count);

        // Make sure ID is unique
        while (repo.existsById(id)) {
            count++;
            id = String.format("PRD-%03d", count);
        }

        Product p = Product.builder()
            .id(id)
            .name(name)
            .price(price)
            .stock(stock)
            .sales(0)
            .icon(icon != null ? icon : "📦")
            .build();

        return repo.save(p);
    }

    public Optional<Product> updateProduct(String id, String name, Double price, Integer stock) {
        return repo.findById(id).map(p -> {
            p.setName(name);
            p.setPrice(price);
            p.setStock(stock);
            return repo.save(p);
        });
    }

    public boolean deleteProduct(String id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public void decrementStock(String productId, int qty) {
        repo.findById(productId).ifPresent(p -> {
            p.setStock(p.getStock() - qty);
            p.setSales(p.getSales() + qty);
            repo.save(p);
        });
    }

    // ── Dashboard stats ──────────────────────────────────────────────────
    public long countProducts() {
        return repo.count();
    }

    public long countLowStock() {
        return repo.countLowStockProducts();
    }

    public double calculateRevenue() {
        return repo.calculateTotalRevenue();
    }
}