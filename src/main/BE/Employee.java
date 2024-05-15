package BE;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



public class Employee {
    private int employeeId;
    private String employeeName;
    private BigDecimal annualSalary;
    private BigDecimal overheadMultiPercent;
    private BigDecimal annualAmount;
    private String country;
    private int workingHours;
    private BigDecimal utilization;
    private BigDecimal teamUtil;
    private boolean isOverheadCost;
    //because an employee can have multiple teams
    private List<Team> teams;



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

    public BigDecimal getTeamUtil() {
        return teamUtil;
    }

    public void setTeamUtil(BigDecimal teamUtil) {
        this.teamUtil = teamUtil;
    }

    public List<Team> getTeams() {
        if (teams == null) {
            teams = new ArrayList<>();
        }
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public void addTeam(Team team) {
        if (teams == null) {
            teams = new ArrayList<>();
        }
        teams.add(team);
    }

    public String getTeamNames() {
        //our team should never be null because of our DAO but just in case...
        if (teams == null || teams.isEmpty()) {
            return "No Team";
        }
        StringBuilder teamNames = new StringBuilder();
        for (Team team : teams) {
            if (!teamNames.isEmpty()) {
                teamNames.append(", ");
            }
            teamNames.append(team.getName());
        }
        return teamNames.toString();
    }

}
