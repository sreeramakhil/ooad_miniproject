package com.stockflow.repository;

import com.stockflow.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Product p WHERE p.stock < 10")
    List<Product> findLowStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock < 10")
    long countLowStockProducts();

    @Query("SELECT COALESCE(SUM(p.sales * p.price), 0) FROM Product p")
    double calculateTotalRevenue();
}