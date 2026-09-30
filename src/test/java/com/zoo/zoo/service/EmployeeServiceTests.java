package com.zoo.zoo.service;

import com.zoo.zoo.exceptions.employee.EmployeeWithIDNotFoundException;
import com.zoo.zoo.model.employee.*;
import com.zoo.zoo.repository.employee.EmployeeRepository;
import com.zoo.zoo.service.employee.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTests {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void shouldCreateEmployee() {
        EmployeeRequest request = new EmployeeRequest();
        request.setName("Damian");
        request.setEmployeeType(EmployeeType.BOSS);
        request.setDepartment("Reptiles");

        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmployeeType(request.getEmployeeType());
        employee.setDepartment(request.getDepartment());

        EmployeeResponse expectedResponse = new EmployeeResponse();
        expectedResponse.setName(request.getName());
        expectedResponse.setEmployeeType(request.getEmployeeType());
        expectedResponse.setDepartment(request.getDepartment());

        when(employeeMapper.toEntity(request)).thenReturn(employee);
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toResponse(employee)).thenReturn(expectedResponse);

        EmployeeResponse result = employeeService.create(request);

        assertEquals(expectedResponse, result);
        verify(employeeMapper).toEntity(request);
        verify(employeeRepository).save(employee);
        verify(employeeMapper).toResponse(employee);
    }

    @Test
    void shouldReturnAllEmployees() {
        Employee employee = new Employee();
        employee.setName("Adam");
        EmployeeResponse response = new EmployeeResponse();
        response.setName("Adam");
        List<Employee> employees = List.of(employee);

        // HINT: This stub must match the repository call that EmployeeService.getAll() makes after your merge.
        when(employeeRepository.findByActiveTrue()).thenReturn(employees);
        when(employeeMapper.toResponse(employee)).thenReturn(response);

        List<EmployeeResponse> result = employeeService.getAll();

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
        verify(employeeRepository).findByActiveTrue();
    }

    @Test
    void shouldReturnEmptyListWhenNoEmployeesExist() {
        when(employeeRepository.findByActiveTrue()).thenReturn(Collections.emptyList());

        List<EmployeeResponse> result = employeeService.getAll();

        assertTrue(result.isEmpty());
        verify(employeeRepository).findByActiveTrue();
        verifyNoInteractions(employeeMapper);
    }

    @Test
    void shouldReturnEmployeeById() {
        Long id = 1L;
        Employee employee = new Employee();
        employee.setId(id);
        EmployeeResponse response = new EmployeeResponse();

        when(employeeRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(employee));
        when(employeeMapper.toResponse(employee)).thenReturn(response);

        EmployeeResponse result = employeeService.getById(id);

        assertEquals(response, result);
        verify(employeeRepository).findByIdAndActiveTrue(id);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {
        Long id = 1L;
        when(employeeRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.empty());

        assertThrows(EmployeeWithIDNotFoundException.class, () -> employeeService.getById(id));

        verify(employeeRepository).findByIdAndActiveTrue(id);
        verify(employeeMapper, never()).toResponse(any());
    }

    @Test
    void shouldSoftDeleteEmployee() {
        Long id = 1L;
        Employee employee = new Employee();
        employee.setId(id);
        when(employeeRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(employee));

        employeeService.delete(id);

        assertFalse(employee.isActive());
        verify(employeeRepository).save(employee);
        verify(employeeRepository, never()).deleteById(any());
    }

    @Test
    void shouldThrowExceptionWhenDeletingNotFoundEmployee() {
        Long id = 1L;
        when(employeeRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.empty());

        assertThrows(EmployeeWithIDNotFoundException.class, () -> employeeService.delete(id));

        verify(employeeRepository, never()).deleteById(any());
    }

    @Test
    void shouldReturnEmployeesByType() {
        EmployeeType type = EmployeeType.BOSS;
        Employee employee = new Employee();
        EmployeeResponse response = new EmployeeResponse();

        when(employeeRepository.findByEmployeeType(type)).thenReturn(List.of(employee));
        when(employeeMapper.toResponse(employee)).thenReturn(response);

        List<EmployeeResponse> result = employeeService.getByType(type);

        assertEquals(1, result.size());
        verify(employeeRepository).findByEmployeeType(type);
    }

    @Test
    void shouldReturnEmptyListWhenNoEmployeesOfGivenType() {
        EmployeeType type = EmployeeType.STUDENT;
        when(employeeRepository.findByEmployeeType(type)).thenReturn(new ArrayList<>());

        List<EmployeeResponse> result = employeeService.getByType(type);

        assertTrue(result.isEmpty());
        verify(employeeRepository).findByEmployeeType(type);
        verify(employeeMapper, never()).toResponse(any());
    }

    @Test
    void shouldReturnEmployeeByName() {
        String name = "damian";
        Employee employee = new Employee();
        EmployeeResponse response = new EmployeeResponse();

        when(employeeRepository.findByNameContainingIgnoreCase(name)).thenReturn(List.of(employee));
        when(employeeMapper.toResponse(employee)).thenReturn(response);

        List<EmployeeResponse> result = employeeService.searchByName(name);

        assertEquals(1, result.size());
        verify(employeeRepository).findByNameContainingIgnoreCase(name);
    }

    @Test
    void shouldReturnEmptyListWhenSearchByNameYieldsNoResults() {
        String name = "Nieistniejacy";
        when(employeeRepository.findByNameContainingIgnoreCase(name)).thenReturn(Collections.emptyList());

        List<EmployeeResponse> result = employeeService.searchByName(name);

        assertTrue(result.isEmpty());
        verify(employeeRepository).findByNameContainingIgnoreCase(name);
        verify(employeeMapper, never()).toResponse(any());
    }

    @Test
    void shouldHandleNullTypeGracefullyInSearch() {
        when(employeeRepository.findByEmployeeType(null)).thenReturn(Collections.emptyList());

        List<EmployeeResponse> result = employeeService.getByType(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldTransferDepartment() {
        Long id = 1L;
        Employee employee = new Employee();
        employee.setId(id);
        employee.setDepartment("Reptiles");

        EmployeeResponse response = new EmployeeResponse();
        response.setId(id);
        response.setDepartment("Birds");

        when(employeeRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toResponse(employee)).thenReturn(response);

        EmployeeResponse result = employeeService.transferDepartment(id, "Birds");

        assertEquals(response, result);
        verify(employeeRepository).save(employee);
    }

    @Test
    void shouldCountEmployeesInDatabaseWithoutLoadingThem() {
        when(employeeRepository.countByActiveTrueAndNameIsNotNull()).thenReturn(42L);

        long result = employeeService.count();

        assertEquals(42L, result);
        verify(employeeRepository, never()).findAll();
    }
}