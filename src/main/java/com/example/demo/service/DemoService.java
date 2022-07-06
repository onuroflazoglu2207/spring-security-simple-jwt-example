package com.example.demo.service;

import com.example.demo.dto.DemoRequestDTO;
import com.example.demo.model.RequestModel;
import com.example.demo.model.Roles;
import org.springframework.http.ResponseEntity;

public interface DemoService {

    public ResponseEntity<?> authenticate(RequestModel request);

    public ResponseEntity<?> getAll();

    public ResponseEntity<?> getById();

    public ResponseEntity<?> getById(Long identity);

    public ResponseEntity<?> save(DemoRequestDTO dto);

    public ResponseEntity<?> update(DemoRequestDTO dto, Long identity);

    public ResponseEntity<?> changeRole(Roles roles, Long identity);

    public ResponseEntity<?> delete(Long identity);
}
