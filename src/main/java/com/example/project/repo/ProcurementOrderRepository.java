package com.example.project.repo;

import com.example.project.model.ProcurementOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcurementOrderRepository extends JpaRepository<ProcurementOrder, Long> {
}