package BE;

public class Team {

    private int Id;
    private String name;

    public Team(){

    }


    public Team(int id, String name) {
        Id = id;
        this.name = name;
    }

    public int getId() {
        return Id;
    }

    public void setId(int employeeId) {
        Id = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String employeeName) {
        this.name = employeeName;
    }
}
