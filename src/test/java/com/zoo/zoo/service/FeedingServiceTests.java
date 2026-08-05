package com.zoo.zoo.service;

import com.zoo.zoo.exceptions.feeding.*;
import com.zoo.zoo.model.animal.*;
import com.zoo.zoo.model.employee.Employee;
import com.zoo.zoo.model.employee.EmployeeType;
import com.zoo.zoo.model.feeding.FeedingRequest;
import com.zoo.zoo.model.feeding.FoodInventory;
import com.zoo.zoo.model.feeding.FoodType;
import com.zoo.zoo.repository.animal.AnimalRepository;
import com.zoo.zoo.repository.employee.EmployeeRepository;
import com.zoo.zoo.repository.food.FoodInventoryRepository;
import com.zoo.zoo.service.feeding.AuditService;
import com.zoo.zoo.service.feeding.FeedingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.LocalDate;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class)
class FeedingServiceTests {

    @Mock private AnimalRepository animalRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private FoodInventoryRepository inventoryRepository;
    @Mock private AuditService auditService;
    @Mock private AnimalMapper animalMapper;

    @InjectMocks
    private FeedingService feedingService;

    private FeedingRequest request;
    private Spider spider;
    private VenomousReptile cobra;
    private Employee student;
    private Employee boss;
    private FoodInventory cricketInv;
    private FoodInventory mouseInv;

    @BeforeEach
    void setUp() {
        spider = new Spider();
        spider.setId(1L);
        spider.setLastFeedingDate(LocalDate.now().minusDays(15));

        cobra = new VenomousReptile();
        cobra.setId(2L);
        cobra.setWeightInGrams(1000);
        cobra.setLastFeedingDate(LocalDate.now().minusDays(31));

        student = new Employee();
        student.setId(10L);
        student.setEmployeeType(EmployeeType.STUDENT);

        boss = new Employee();
        boss.setId(11L);
        boss.setEmployeeType(EmployeeType.BOSS);

        cricketInv = new FoodInventory();
        cricketInv.setFoodType(FoodType.CRICKET);
        cricketInv.setCurrentQuantity(10);

        mouseInv = new FoodInventory();
        mouseInv.setFoodType(FoodType.MOUSE);
        mouseInv.setCurrentQuantity(10);

        request = new FeedingRequest();
    }

    @Test
    void shouldFeedSpiderSuccessfullyIgnoringWeight() {
        request.setEmployeeId(10L);
        request.setFoodType(FoodType.CRICKET);
        request.setFoodWeightInGrams(999);

        when(animalRepository.findById(1L)).thenReturn(Optional.of(spider));
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(student));
        when(inventoryRepository.findByFoodType(FoodType.CRICKET)).thenReturn(Optional.of(cricketInv));
        when(animalRepository.save(any())).thenReturn(spider);

        feedingService.feedAnimal(1L, request);

        verify(animalRepository).save(spider);
        assertEquals(9, cricketInv.getCurrentQuantity());
    }


    @Test
    void shouldFeedVenomousReptileWhenWeightIsCorrect() {
        request.setEmployeeId(11L);
        request.setFoodType(FoodType.MOUSE);
        request.setFoodWeightInGrams(100);

        AnimalResponse expectedResponse = new AnimalResponse();
        expectedResponse.setName("Cobra");

        when(animalRepository.findById(2L)).thenReturn(Optional.of(cobra));
        when(employeeRepository.findById(11L)).thenReturn(Optional.of(boss));
        when(inventoryRepository.findByFoodType(FoodType.MOUSE)).thenReturn(Optional.of(mouseInv));
        when(animalRepository.save(any())).thenReturn(cobra);
        when(animalMapper.territoryToResponse(cobra)).thenReturn(expectedResponse);

        AnimalResponse result = feedingService.feedAnimal(2L, request);

        assertNotNull(result);
        assertEquals("Cobra", result.getName());
        assertEquals(9, mouseInv.getCurrentQuantity());
        assertEquals(LocalDate.now(), cobra.getLastFeedingDate());
        verify(animalRepository).save(cobra);
    }

    @Test
    void shouldThrowExceptionWhenBossFeedsCobraWithTooLightMouse() {
        request.setEmployeeId(11L);
        request.setFoodType(FoodType.MOUSE);
        request.setFoodWeightInGrams(50);

        when(animalRepository.findById(2L)).thenReturn(Optional.of(cobra));
        when(employeeRepository.findById(11L)).thenReturn(Optional.of(boss));
        when(inventoryRepository.findByFoodType(FoodType.MOUSE)).thenReturn(Optional.of(mouseInv));

        assertThrows(IncorrectWeightOfFoodException.class, () -> feedingService.feedAnimal(2L, request));
    }

    @Test
    void shouldThrowExceptionWhenStudentTriesToFeedVenomous() {
        request.setEmployeeId(10L);
        request.setFoodType(FoodType.MOUSE);

        when(animalRepository.findById(2L)).thenReturn(Optional.of(cobra));
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(student));
        when(inventoryRepository.findByFoodType(FoodType.MOUSE)).thenReturn(Optional.of(mouseInv));

        assertThrows(IncorrectEmployeeFeedingException.class, () -> feedingService.feedAnimal(2L, request));
    }

    @Test
    void shouldThrowExceptionWhenExperiencedTriesToFeedVenomous() {
        Employee employee = new Employee();
        employee.setId(12L);
        employee.setEmployeeType(EmployeeType.EXPERIENCED);
        request.setEmployeeId(12L);
        request.setFoodType(FoodType.MOUSE);
        when(animalRepository.findById(2L)).thenReturn(Optional.of(cobra));
        when(employeeRepository.findById(12L)).thenReturn(Optional.of(employee));
        when(inventoryRepository.findByFoodType(FoodType.MOUSE)).thenReturn(Optional.of(mouseInv));
        assertThrows(IncorrectEmployeeFeedingException.class, () -> feedingService.feedAnimal(2L, request));
    }
    @Test
    void shouldLogToAuditServiceWhenFeedingFails() {
        request.setEmployeeId(10L);
        request.setFoodType(FoodType.MOUSE);

        when(animalRepository.findById(1L)).thenReturn(Optional.of(spider));
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(student));
        when(inventoryRepository.findByFoodType(FoodType.MOUSE)).thenReturn(Optional.of(mouseInv));

        try {
            feedingService.feedAnimal(1L, request);
        }
        catch (IncorrectFoodMatchedToAnimal e) {
        }
        verify(auditService, times(1)).logError(eq(1L), eq(10L), anyString(), anyString());
    }
    @Test
    void shouldThrowExceptionWhenFoodIsTooHeavyForCobra() {
        request.setEmployeeId(11L);
        request.setFoodType(FoodType.MOUSE);
        request.setFoodWeightInGrams(200);
        when(animalRepository.findById(2L)).thenReturn(Optional.of(cobra));
        when(employeeRepository.findById(11L)).thenReturn(Optional.of(boss));
        when(inventoryRepository.findByFoodType(FoodType.MOUSE)).thenReturn(Optional.of(mouseInv));
        assertThrows(IncorrectWeightOfFoodException.class, () -> feedingService.feedAnimal(2L, request));
    }
    @Test
    void shouldThrowExceptionWhenAnimalIsStillFull() {
        spider.setLastFeedingDate(LocalDate.now().minusDays(1));
        request.setEmployeeId(10L);
        request.setFoodType(FoodType.CRICKET);

        when(animalRepository.findById(1L)).thenReturn(Optional.of(spider));
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(student));
        when(inventoryRepository.findByFoodType(FoodType.CRICKET)).thenReturn(Optional.of(cricketInv));

        assertThrows(AnimalAlreadyFeededException.class, () -> feedingService.feedAnimal(1L, request));
    }
    @Test
    void shouldThrowExceptionWhenFoodTypeNotFound() {
        request.setEmployeeId(11L);
        request.setFoodType(FoodType.RABBIT);
        when(animalRepository.findById(2L)).thenReturn(Optional.of(cobra));
        when(employeeRepository.findById(11L)).thenReturn(Optional.of(boss));
        when(inventoryRepository.findByFoodType(FoodType.RABBIT)).thenReturn(Optional.empty());

        assertThrows(FoodTypeNotFoundException.class, () -> feedingService.feedAnimal(2L, request));
    }

    @Test
    void shouldThrowExceptionWhenFoodIsOutOfStock() {
        mouseInv.setCurrentQuantity(0);
        request.setEmployeeId(11L);
        request.setFoodType(FoodType.MOUSE);
        when(animalRepository.findById(2L)).thenReturn(Optional.of(cobra));
        when(employeeRepository.findById(11L)).thenReturn(Optional.of(boss));
        when(inventoryRepository.findByFoodType(FoodType.MOUSE)).thenReturn(Optional.of(mouseInv));

        assertThrows(FoodTypeNotFoundException.class, () -> feedingService.feedAnimal(2L, request));
    }

}