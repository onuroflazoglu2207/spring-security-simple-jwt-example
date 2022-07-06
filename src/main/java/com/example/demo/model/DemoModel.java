package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class DemoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long identity;

    @Builder.Default
    @NotNull(message = "Roles cannot be null!")
    @Enumerated(EnumType.STRING)
    Roles roles = Roles.USER;

    @NotBlank(message = "Name cannot be blank!")
    String name;

    @NotNull(message = "Birthday cannot be null!")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = ISO.DATE)
    LocalDate birthday;

    @NotNull(message = "Gender cannot be null!")
    @Enumerated(EnumType.STRING)
    Gender gender;

    String email;

    String phone;

    @NotBlank(message = "Password cannot be blank!")
    String password;
}
