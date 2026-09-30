package com.zoo.zoo.model.employee;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    private EmployeeType employeeType;
    private String department;
    // HINT: Hire date is set once on creation (EmployeeMapper.toEntity) and must never be changed by an UPDATE.
    @Column(updatable = false)
    private LocalDate hireDate;

    // HINT: Domain helper used by the veterans report - keep entity helpers free of repository/service calls.
    public long monthsOfService(LocalDate today) {
        return hireDate == null ? 0 : ChronoUnit.MONTHS.between(hireDate, today);
    }

}
