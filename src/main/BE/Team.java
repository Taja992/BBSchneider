package BE;

public class Team {

    private int Id;
    private String name;

    public Team(){

    }

    public Team(String name){
        this.name = name;
    }

    public Team(int id){
        this.Id = id;
    }

    public Team(int id, String name) {
        Id = id;
        this.name = name;
    }

    public int getEmployeeId() {
        return Id;
    }

    public void setEmployeeId(int employeeId) {
        Id = employeeId;
    }

    public String getEmployeeName() {
        return name;
    }

    public void setEmployeeName(String employeeName) {
        this.name = employeeName;
    }

    public String getName() {
        return name;
    }
}
