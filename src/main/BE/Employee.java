package BE;
import java.math.BigDecimal;


//we extend team so we can carry the team_id and team_name from Team Table as well
public class Employee extends Team {
    private int employeeId;
    private String employeeName;
    private BigDecimal annualSalary;
    private BigDecimal overheadMultiPercent;
    private BigDecimal annualAmount;
    private String country;
    private Integer teamIdEmployee;
    private int workingHours;
    private BigDecimal utilization;
    private boolean isOverheadCost;


    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public BigDecimal getAnnualSalary() {
        return annualSalary;
    }

    public void setAnnualSalary(BigDecimal annualSalary) {
        this.annualSalary = annualSalary;
    }

    public BigDecimal getOverheadMultiPercent() {
        return overheadMultiPercent;
    }

    public void setOverheadMultiPercent(BigDecimal overheadMultiPercent) {
        this.overheadMultiPercent = overheadMultiPercent;
    }

    public BigDecimal getAnnualAmount() {
        return annualAmount;
    }

    public void setAnnualAmount(BigDecimal annualAmount) {
        this.annualAmount = annualAmount;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getTeamIdEmployee() {
        return teamIdEmployee;
    }

    public void setTeamIdEmployee(Integer teamIdEmployee) {
        this.teamIdEmployee = teamIdEmployee;
    }

    public int getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(int workingHours) {
        this.workingHours = workingHours;
    }

    public BigDecimal getUtilization() {
        return utilization;
    }

    public void setUtilization(BigDecimal utilization) {
        this.utilization = utilization;
    }

    public boolean getIsOverheadCost() {
        return isOverheadCost;
    }

    public void setIsOverheadCost(boolean overheadCost) {
        isOverheadCost = overheadCost;
    }

    //we return super here to be able to pull the info out of the Team class
    public String getTeamName() {
        return super.getEmployeeName();
    }

    public void setTeamName(String teamName) {
        super.setEmployeeName(teamName);
    }

    public int getTeamId() {
        return super.getEmployeeId();
    }

    public void setTeamId(int teamId) {
        super.setEmployeeId(teamId);
    }

}
