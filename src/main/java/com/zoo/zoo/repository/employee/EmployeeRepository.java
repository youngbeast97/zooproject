package com.zoo.zoo.repository.employee;

import com.zoo.zoo.model.employee.Employee;
import com.zoo.zoo.model.employee.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

 public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByEmployeeType(EmployeeType type);

    List<Employee> findByNameContainingIgnoreCase(String name);

    long countByNameIsNotNull();

    // HINT: Filtering in the database instead of loading every row and filtering in Java.
    List<Employee> findAllByNameIsNotNull();

    List<Employee> findByHireDateBeforeOrderByHireDateAsc(LocalDate date);

    List<Employee> findByHireDateAfterOrderByHireDateAsc(LocalDate date);
}
