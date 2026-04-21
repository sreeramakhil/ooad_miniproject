package com.stockflow.repository;

import com.stockflow.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByCustomerOrderByDateDesc(String customer);

    List<Order> findAllByOrderByDateDesc();

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o")
    double calculateTotalRevenue();
}