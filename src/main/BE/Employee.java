package BE;
import java.math.BigDecimal;


public class Employee {
    private int employeeId;
    private String employeeName;
    private BigDecimal annualSalary;
    private BigDecimal overheadMultiPercent;
    private BigDecimal annualAmount;
    private String country;
    private int workingHours;
    private BigDecimal utilization;
    private boolean isOverheadCost;

    public Employee(int id, String name, BigDecimal annualSalary, BigDecimal overheadMultiPercent, BigDecimal annualAmount, String country, int workingHours, BigDecimal utilization, Object isOverheadCost) {
        this.employeeId = id;
        this.employeeName = name;
        this.annualSalary = annualSalary;
        this.overheadMultiPercent = overheadMultiPercent;
        this.annualAmount = annualAmount;
        this.country = country;
        this.workingHours = workingHours;
        this.utilization = utilization;
        this.isOverheadCost = (boolean) isOverheadCost;
    }

    public Employee() {
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

}
