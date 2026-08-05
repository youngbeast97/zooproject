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
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeResponse create(EmployeeRequest request) {
        Employee employee = employeeMapper.toEntity(request);
        employeeRepository.save(employee);
        return employeeMapper.toResponse(employee);
    }



    public List<EmployeeResponse> getAll() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    // CacheEvict - usuwa wpis
    // CachePut - nadpisuje wpis nowa wartoscia
    @Cacheable(value = "employeesCache", key = "#id", sync = true) // zapisuje jesli brak
    public EmployeeResponse getById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeWithIDNotFoundException("Employee not found"));
        return employeeMapper.toResponse(employee);
    }


    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeWithIDNotFoundException("Employee not found");
        }
        employeeRepository.deleteById(id);
    }

    public void deleteAll() {
        employeeRepository.deleteAll();
    }

    public List<EmployeeResponse> getByType(EmployeeType type) {
        return employeeRepository.findByEmployeeType(type)
                .stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    public List<EmployeeResponse> searchByName(String name) {
        return employeeRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

}
