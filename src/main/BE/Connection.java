package BE;

import java.math.BigDecimal;

public class Connection {
    private int empId;
    private int teamId;
    private BigDecimal teamUtil;

    public Connection(int empId, int teamId, BigDecimal teamUtil) {
        this.empId = empId;
        this.teamId = teamId;
        this.teamUtil = teamUtil;
    }

    public int getEmpId() {
        return empId;
    }

    public int getTeamId() {
        return teamId;
    }

    public BigDecimal getTeamUtil() {
        return teamUtil;
    }
}