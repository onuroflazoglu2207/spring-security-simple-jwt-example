package com.example.demo.repository;

import com.example.demo.model.DemoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoRepository extends JpaRepository<DemoModel, Long> {
}
