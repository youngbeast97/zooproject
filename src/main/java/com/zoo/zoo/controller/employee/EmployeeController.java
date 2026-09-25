package com.zoo.zoo.controller.employee;

import com.zoo.zoo.model.employee.EmployeeRequest;
import com.zoo.zoo.model.employee.EmployeeResponse;
import com.zoo.zoo.model.employee.EmployeeType;
import com.zoo.zoo.service.employee.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor

public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping ("/employee")
    public ResponseEntity<EmployeeResponse> create(@Valid @NotNull @RequestBody EmployeeRequest request) {
    return ResponseEntity.status(201).body(employeeService.create(request));
    }

    @PatchMapping("/{id}/department")
    public EmployeeResponse transferDepartment(@PathVariable Long id, @RequestParam String department) {
        return employeeService.transferDepartment(id, department);
    }

    @GetMapping("/get-all-employees")
    public List<EmployeeResponse> getAll() {
        return employeeService.getAll();
    }

    @GetMapping("/{id}")
    public EmployeeResponse getById(@PathVariable Long id) {
        return employeeService.getById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteEmployee(@PathVariable Long id){
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-all")
    public void deleteAll() {
        employeeService.deleteAll();
    }

    @GetMapping("/type/{type}")
    public List<EmployeeResponse> getByType(@PathVariable EmployeeType type) {
        return employeeService.getByType(type);
    }

    @GetMapping("/search")
    public List<EmployeeResponse> searchByName(@RequestParam String name) {
        return employeeService.searchByName(name);
    }

    @GetMapping("/count")
    public long count() {
        return employeeService.getAll().size();
    }
}
