package BE;
import java.math.BigDecimal;


//we extend team so we can carry the team_id and team_name from Team Table as well
public class Employee {
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
    private Team team;
    private String teamName;

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }


    public int getId() {
        return employeeId;
    }

    public void setId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return employeeName;
    }

    public void setName(String employeeName) {
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
//    public String getTeamName() {
//        return super.getName();
//    }
//
//    public void setTeamName(String teamName) {
//        super.setName(teamName);
//    }
//
//    public int getTeamId() {
//        return super.getId();
//    }
//
//    public void setTeamId(int teamId) {
//        super.setId(teamId);
//    }

}
