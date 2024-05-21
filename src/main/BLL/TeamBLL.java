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
    EmployeeBLL employeeBLL = new EmployeeBLL();
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

    public List<Team> getTeamsForEmployee(int employeeId) throws BBExceptions {
            return teamDAO.getTeamsForEmployee(employeeId);
    }


    ////////////////////////////////////////////////////////
    /////////////////Calculation Logic//////////////////////
    ////////////////////////////////////////////////////////

    public Double calculateTotalHourlyRate(int teamId) throws BBExceptions{
        List<Employee> employees = employeeDAO.getEmployeesWithOverheadStatus(teamId);
        double totalHourlyRate = 0;
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

    public Double calculateTotalDailyRate(int teamId, int hoursPerDay) throws BBExceptions{
        List<Employee> employees = employeeDAO.getEmployeesWithOverheadStatus(teamId);
        double totalDailyRate = 0;
        for(Employee employee : employees){
            if (!employee.getTeamOverhead()) {  // Only calculate the daily rate for non-overhead employees
                double dailyRate = calculateTeamDailyRate(employee, employeeDAO.getUtilizationForTeam(employee.getId(), teamId), hoursPerDay);
                totalDailyRate += dailyRate;
            }
        }

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

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

        if (teamUtil == null || teamUtil.doubleValue() == 0) {
            return 0.0;
        }

        double annualSalary = selectedEmployee.getAnnualSalary().doubleValue();
        double overheadMultiplier = selectedEmployee.getOverheadMultiPercent().doubleValue() / 100; // convert to decimal
        double fixedAnnualAmount = selectedEmployee.getAnnualAmount().doubleValue();
        double utilizationPercentage = teamUtil.doubleValue() / 100; // convert to decimal
        double annualEffectiveWorkingHours = selectedEmployee.getWorkingHours(); // convert to total working hours in a year
        return (((annualSalary + fixedAnnualAmount) * (1 + overheadMultiplier)) / (annualEffectiveWorkingHours * utilizationPercentage));
    }
}
