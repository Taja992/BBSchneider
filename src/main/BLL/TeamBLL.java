package BLL;

import BE.Employee;
import BE.Team;
import DAL.EmployeeDAO;
import DAL.TeamDAO;
import Exceptions.BBExceptions;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class TeamBLL {

    TeamDAO teamDAO = new TeamDAO();
    EmployeeDAO employeeDAO = new EmployeeDAO();

    public List<Team> getAllTeams() throws BBExceptions {
        return teamDAO.getAllTeams();
    }

    public void createNewTeam(Team team) throws BBExceptions {
            teamDAO.createNewTeam(team);
    }

    public int getLastTeamId() throws BBExceptions {
        return teamDAO.getLastTeamId();
    }

    public void updateTeamName(int teamId, String newTeamName) throws BBExceptions {
        teamDAO.updateTeamName(teamId, newTeamName);
    }



    ////////////////////////////////////////////////////////
    /////////////////Calculation Logic//////////////////////
    ////////////////////////////////////////////////////////

    //sum of all the hourly rates in a team
    public Double calculateTotalHourlyRate(int teamId) throws BBExceptions{
        // Get all employees in the team with checking for overhead status, if overhead is on then they dont contribute to the calculation
        List<Employee> employees = employeeDAO.getEmployeesWithOverheadStatus(teamId);
        double totalHourlyRate = 0;
        // Loop through all employees in the team and calculate the hourly rate for each and summing it up
        for(Employee employee : employees){
            if (!employee.getTeamOverhead()) {  // Only calculate the hourly rate for non-overhead employees
                BigDecimal teamUtilization = employeeDAO.getUtilizationForTeam(employee.getId(), teamId);
                double hourlyRate = calculateTeamHourlyRate(employee, teamUtilization);
                totalHourlyRate += hourlyRate;
            }
        }

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        try {
            return nf.parse(nf.format(totalHourlyRate)).doubleValue();
        } catch (ParseException e) {
            throw new BBExceptions("Error parsing total hourly rate", e);
        }
    }

    //sum of all the daily rates in a team
    public Double calculateTotalDailyRate(int teamId, int hoursPerDay) throws BBExceptions{
        // Get all employees in the team with checking for overhead status, if overhead is on then they dont contribute to the calculation
        List<Employee> employees = employeeDAO.getEmployeesWithOverheadStatus(teamId);
        double totalDailyRate = 0;
        // Loop through all employees in the team and calculate the daily rate for each and summing it up
        for(Employee employee : employees){
            if (!employee.getTeamOverhead()) {  // Only calculate the daily rate for non-overhead employees
                double dailyRate = calculateTeamDailyRate(employee, employeeDAO.getUtilizationForTeam(employee.getId(), teamId), hoursPerDay);
                totalDailyRate += dailyRate;
            }
        }

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2); //ensures it's only to a decimal point of 2 (so it doesn't go on too long)

        try {
            return nf.parse(nf.format(totalDailyRate)).doubleValue();
        } catch (ParseException e) {
            throw new BBExceptions("Error parsing total daily rate", e);
        }
    }

    /////////////////////////////////////////////////////////////////
    /////////////////Calculation Logic for Team//////////////////////
    /////////////////////////////////////////////////////////////////

    public Double calculateTeamHourlyRate(Employee selectedEmployee, BigDecimal teamUtil) throws BBExceptions {
        double rate = calculateRateWithTeamUtil(selectedEmployee, teamUtil);  // The rate calculated is already in hourly rate

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        try {
            return nf.parse(nf.format(rate)).doubleValue();
        } catch (ParseException e) {
            throw new BBExceptions("Error parsing hourly rate", e);
        }
    }

    public Double calculateTeamDailyRate(Employee selectedEmployee, BigDecimal teamUtil, int hoursPerDay) throws BBExceptions {
        if (hoursPerDay < 0 || hoursPerDay > 24) {
            throw new BBExceptions("Invalid number of hours per day. It should be between 0 and 24.");
        }

        // Calculate the daily rate by multiplying the hourly rate with the hours per day
        double hourlyRate = calculateTeamHourlyRate(selectedEmployee, teamUtil);
        double dailyRate = hourlyRate * hoursPerDay;

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        try {
            return nf.parse(nf.format(dailyRate)).doubleValue();
        } catch (ParseException e) {
            throw new BBExceptions("Error parsing daily rate", e);
        }
    }

    private double calculateRateWithTeamUtil(Employee selectedEmployee, BigDecimal teamUtil) throws BBExceptions {
        if (selectedEmployee == null) {
            throw new BBExceptions("No employee selected");
        }

        // If the team utilization is null or 0, return 0
        if (teamUtil == null || teamUtil.doubleValue() == 0) {
            return 0.0;
        }

        // Adding each data to a variable to make it easier to read
        double annualSalary = selectedEmployee.getAnnualSalary().doubleValue();
        double overheadMultiplier = selectedEmployee.getOverheadMultiPercent().doubleValue() / 100; // convert to decimal
        double fixedAnnualAmount = selectedEmployee.getAnnualAmount().doubleValue();
        double utilizationPercentage = teamUtil.doubleValue() / 100; // convert to decimal
        double annualEffectiveWorkingHours = selectedEmployee.getWorkingHours(); // convert to total working hours in a year

        // Apply the utilization percentage to the annual salary and fixed amount before adding the overhead
        double adjustedAnnualSalary = annualSalary * utilizationPercentage;
        double adjustedFixedAnnualAmount = fixedAnnualAmount * utilizationPercentage;

        // Formula to calculate the rate, this is hourly based
        return (((adjustedAnnualSalary + adjustedFixedAnnualAmount) * (1 + overheadMultiplier)) / annualEffectiveWorkingHours);
    }
}
