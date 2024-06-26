package UnitTest;

import BE.Employee;
import BLL.EmployeeBLL;

import Exceptions.BBExceptions;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class EmployeeBLLTest {
    // creating an instance fo the emplyoeeBLL
    private EmployeeBLL employeeBLL;

    //  @before annotation is used to execute the method before each test
    //  to set up the test environment and avoid code duplication
    @Before
    public void setUp() {
        employeeBLL = new EmployeeBLL();
    }

    @Test
    public void testCalculateHourlyRate() throws BBExceptions {
        // Arrange  --- setting up the testEmployee object with the mock values
        Employee testEmployee = new Employee();
        testEmployee.setAnnualSalary(BigDecimal.valueOf(40000));
        testEmployee.setOverheadMultiPercent(BigDecimal.valueOf(20));
        testEmployee.setAnnualAmount(BigDecimal.valueOf(5000));
        testEmployee.setUtilization(BigDecimal.valueOf(100));
        testEmployee.setWorkingHours(2080); // 40 hours/week * 52 weeks/year
        double expectedHourlyRate = 25.96; // Expected result based on the provided values

        // Act --- calling the calculateHourlyRate method from the employeeBLL to test functionality
        Double hourlyRate = employeeBLL.calculateHourlyRate(testEmployee);

        // Assert --- checking if the expected value is equal to the actual value
        assertEquals(expectedHourlyRate, hourlyRate, 0.01); // delta value is the difference between the expected and actual value
    }

    @Test
    public void testCalculateDailyRate() throws BBExceptions {
        // Arrange
        Employee testEmployee = new Employee();
        testEmployee.setAnnualSalary(BigDecimal.valueOf(40000));
        testEmployee.setOverheadMultiPercent(BigDecimal.valueOf(20));
        testEmployee.setAnnualAmount(BigDecimal.valueOf(5000));
        testEmployee.setUtilization(BigDecimal.valueOf(100));
        testEmployee.setWorkingHours(2080); // 40 hours/week * 52 weeks/year
        int hoursPerDay = 8;
        double expectedDailyRate = 207.68; // Expected result based on the provided values

        // Act
        Double dailyRate = employeeBLL.calculateDailyRate(testEmployee, hoursPerDay);

        // Assert
        assertEquals(expectedDailyRate, dailyRate, 0.01);
    }
}