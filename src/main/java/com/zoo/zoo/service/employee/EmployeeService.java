package com.zoo.zoo.service.employee;

import com.zoo.zoo.exceptions.employee.EmployeeWithIDNotFoundException;
import com.zoo.zoo.model.employee.*;
import com.zoo.zoo.repository.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private static final String CACHE = "employeesCache";

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        Employee saved = employeeRepository.save(employeeMapper.toEntity(request));
        return employeeMapper.toResponse(saved);
    }

    public List<EmployeeResponse> getAll() {
        return toResponses(employeeRepository.findAll());
    }

    @Cacheable(value = CACHE, key = "#id", sync = true)
    public EmployeeResponse getById(Long id) {
        return employeeMapper.toResponse(findEntityById(id));
    }

    @Transactional
    @CacheEvict(value = CACHE, key = "#id")
    public void delete(Long id) {
        Employee employee = findEntityById(id);
        employeeRepository.delete(employee);
    }

    @Transactional
    @CacheEvict(value = CACHE, allEntries = true)
    public void deleteAll() {
        employeeRepository.deleteAll();
    }

    public List<EmployeeResponse> getByType(EmployeeType type) {
        return toResponses(employeeRepository.findByEmployeeType(type));
    }

    public List<EmployeeResponse> searchByName(String name) {
        return toResponses(employeeRepository.findByNameContainingIgnoreCase(name));
    }

    private Employee findEntityById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeWithIDNotFoundException(
                        "Employee not found, id=" + id));
    }

    private List<EmployeeResponse> toResponses(Collection<Employee> employees) {
        return employees.stream()
                .map(employeeMapper::toResponse)
                .toList();
    }
}