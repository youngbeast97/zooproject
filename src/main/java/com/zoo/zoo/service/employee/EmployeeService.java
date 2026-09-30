package com.zoo.zoo.service.employee;

import com.zoo.zoo.exceptions.employee.EmployeeWithIDNotFoundException;
import com.zoo.zoo.model.employee.*;
import com.zoo.zoo.repository.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDate;
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
        return toResponses(employeeRepository.findAllByNameIsNotNull());
    }

    @Transactional(readOnly = true)
    public long count() {
        return employeeRepository.countByNameIsNotNull();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = EMPLOYEES_CACHE, key = "#id", sync = true)
    public EmployeeResponse getById(Long id) {
        return employeeMapper.toResponse(findEmployeeOrThrow(id));
    }

    @Transactional
    @CacheEvict(value = EMPLOYEES_CACHE, key = "#id")
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeWithIDNotFoundException("Employee with id " + id + " not found");
        }
        employeeRepository.deleteById(id);
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
    public List<EmployeeResponse> getVeterans(int minMonths) {
        LocalDate cutoff = LocalDate.now().minusMonths(minMonths);
        return toResponses(employeeRepository.findByHireDateBeforeOrderByHireDateAsc(cutoff));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> searchByName(String name) {
        List<EmployeeResponse> results = toResponses(employeeRepository.findByNameContainingIgnoreCase(name));
        return results.size() > 50 ? results.subList(0, 50) : results;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getHiredAfter(LocalDate date) {
        return toResponses(employeeRepository.findByHireDateAfterOrderByHireDateAsc(date));
    }

    private void validateDepartment(EmployeeRequest request) {
        if (request.getDepartment() == null || request.getDepartment().isBlank()) {
            throw new IllegalArgumentException("Department is required");
        }
    }

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeWithIDNotFoundException("Employee with id " + id + " not found"));
    }

    private List<EmployeeResponse> toResponses(List<Employee> employees) {
        return employees.stream()
                .map(employeeMapper::toResponse)
                .toList();
    }
}
