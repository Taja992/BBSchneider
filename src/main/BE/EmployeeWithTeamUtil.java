package BE;

import java.math.BigDecimal;

public class EmployeeWithTeamUtil extends Employee {
    private BigDecimal teamUtil;

    public EmployeeWithTeamUtil(Employee employee, BigDecimal teamUtil) {
         super(employee.getId(), employee.getName(), employee.getAnnualSalary(), employee.getOverheadMultiPercent(), employee.getAnnualAmount(), employee.getCountry(), employee.getWorkingHours(), employee.getUtilization(), employee.getIsOverheadCost());
        this.teamUtil = teamUtil;
    }

    public BigDecimal getTeamUtil() {
        return teamUtil;
    }
}