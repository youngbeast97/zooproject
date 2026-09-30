package com.zoo.zoo.model.employee;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private EmployeeType employeeType;
    private String department;
    // HINT: Soft delete - former employees stay in the table because feeding_error_logs reference them.
    // HINT: The column default matters: ddl-auto=update adds this column to a table that already has rows.
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;
    private LocalDate hireDate;

}
