package com.example.demo.dto;

import com.example.demo.model.Gender;
import com.example.demo.model.Roles;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DemoResponseDTO {
    Long identity;
    Roles roles;
    String name;
    LocalDate birthday;
    Gender gender;
    String email;
    String phone;
    String password;
}
