package com.stockflow.repository;

import com.stockflow.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findAllByOrderByDateDesc();

    long countByReadFalse();
}