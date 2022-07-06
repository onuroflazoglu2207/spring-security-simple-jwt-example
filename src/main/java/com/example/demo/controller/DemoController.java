package com.example.demo.controller;

import com.example.demo.dto.DemoRequestDTO;
import com.example.demo.model.RequestModel;
import com.example.demo.model.Roles;
import com.example.demo.service.DemoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DemoController {

    private final DemoServiceImpl service;

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody RequestModel request) {
        return service.authenticate(request);
    }

    @GetMapping("/admin/getAll")
    public ResponseEntity<?> getAll() {
        return service.getAll();
    }

    @GetMapping("/admin/getById/{identity}")
    public ResponseEntity<?> getById(@PathVariable Long identity) {
        return service.getById(identity);
    }

    @GetMapping("/user/getById")
    public ResponseEntity<?> getById() {
        return service.getById();
    }

    @PostMapping("/admin/save")
    public ResponseEntity<?> save(@RequestBody DemoRequestDTO dto) {
        return service.save(dto);
    }

    @PutMapping("/admin/update/{identity}")
    public ResponseEntity<?> update(@RequestBody DemoRequestDTO dto, @PathVariable Long identity) {
        return service.update(dto, identity);
    }

    @PutMapping("/admin/changeRole/{identity}/{roles}")
    public ResponseEntity<?> changeRole(@PathVariable Roles roles, @PathVariable Long identity) {
        return service.changeRole(roles, identity);
    }

    @DeleteMapping("/admin/delete/{identity}")
    public ResponseEntity<?> delete(@PathVariable Long identity) {
        return service.delete(identity);
    }
}