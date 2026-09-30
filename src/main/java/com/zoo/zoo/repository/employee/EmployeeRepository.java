package com.zoo.zoo.repository.employee;

import com.zoo.zoo.model.employee.Employee;
import com.zoo.zoo.model.employee.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

 public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByEmployeeType(EmployeeType type);

    List<Employee> findByNameContainingIgnoreCase(String name);

    // HINT: Every query used for listing/counting employees must now ignore inactive rows.
    // HINT: Look at ALL methods in EmployeeService (and their tests) that read employees, not only the conflicting ones.
    List<Employee> findByActiveTrue();

    Optional<Employee> findByIdAndActiveTrue(Long id);

    long countByActiveTrueAndNameIsNotNull();
}
