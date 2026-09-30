package com.zoo.zoo.service.employee;

import com.zoo.zoo.exceptions.employee.EmployeeWithIDNotFoundException;
import com.zoo.zoo.model.employee.*;
import com.zoo.zoo.repository.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private static final String EMPLOYEES_CACHE = "employeesCache";

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        if (request.getEmployeeType() == null) {
            throw new IllegalArgumentException("Employee type must be provided");
        }
        validateDepartment(request);
        Employee saved = employeeRepository.save(employeeMapper.toEntity(request));
        return employeeMapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = EMPLOYEES_CACHE, key = "#id")
    public EmployeeResponse transferDepartment(Long id, String newDepartment) {
        Employee employee = findEmployeeOrThrow(id);
        employee.setDepartment(newDepartment);
        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAll() {
        // HINT: Two filters apply to the employee list: soft-deleted rows and legacy rows without a name.
        // HINT: Decide where each filter should run (DB vs. stream) and check which repository methods remain unused.
        List<Employee> employees = employeeRepository.findByActiveTrue().stream()
                .filter(employee -> employee.getName() != null)
                .toList();
        return toResponses(employees);
    }

    @Transactional(readOnly = true)
    public long count() {
        return employeeRepository.countByActiveTrueAndNameIsNotNull();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = EMPLOYEES_CACHE, key = "#id", sync = true)
    public EmployeeResponse getById(Long id) {
        return employeeMapper.toResponse(findEmployeeOrThrow(id));
    }

    @Transactional
    @CacheEvict(value = EMPLOYEES_CACHE, key = "#id")
    public void delete(Long id) {
        Employee employee = findEmployeeOrThrow(id);
        employee.setActive(false);
        employeeRepository.save(employee);
    }

    @Transactional
    @CacheEvict(value = EMPLOYEES_CACHE, allEntries = true)
    public void deleteAll() {
        employeeRepository.deleteAll();
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getByType(EmployeeType type) {
        return toResponses(employeeRepository.findByEmployeeType(type));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> searchByName(String name) {
        List<EmployeeResponse> results = toResponses(employeeRepository.findByNameContainingIgnoreCase(name));
        return results.size() > 50 ? results.subList(0, 50) : results;
    }

    private void validateDepartment(EmployeeRequest request) {
        if (request.getDepartment() == null || request.getDepartment().isBlank()) {
            throw new IllegalArgumentException("Department is required");
        }
    }

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EmployeeWithIDNotFoundException("Employee with id " + id + " not found"));
    }

    private List<EmployeeResponse> toResponses(List<Employee> employees) {
        return employees.stream()
                .map(employeeMapper::toResponse)
                .toList();
    }
}
