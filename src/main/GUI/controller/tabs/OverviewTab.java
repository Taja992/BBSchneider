package GUI.controller.tabs;

import BE.Employee;
import BE.Team;
import Exceptions.BBExceptions;
import GUI.model.EmployeeModel;
import GUI.model.TeamModel;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;

public class OverviewTab {

    private final TableColumn<Employee, String> nameCol;
    private final TableColumn<Employee, BigDecimal> annualSalaryCol;
    private final TableColumn<Employee, BigDecimal> overHeadMultiCol;
    private final TableColumn<Employee, BigDecimal> annualAmountCol;
    private final TableColumn<Employee, String> countryCol;
    private final TableColumn<Employee, Integer> teamCol;
    private final TableColumn<Employee, Integer> hoursCol;
    private final TableColumn<Employee, BigDecimal> utilCol;
    private final TableColumn<Employee, Boolean> overheadCol;
    private final TableView<Employee> overviewEmployeeTblView;
    private final Label employeeDayRateLbl;
    private final Label employeeHourlyRateLbl;
    private final EmployeeModel employeeModel;
    private final TextField searchTextField;
    private final TabPane TeamTabPane;
    private final TeamModel teamModel;
    private final Button addTeambtn;


    public OverviewTab(EmployeeModel employeeModel, TableColumn<Employee, String> nameCol, TableColumn<Employee, BigDecimal> annualSalaryCol,
                       TableColumn<Employee, BigDecimal> overHeadMultiCol, TableColumn<Employee, BigDecimal> annualAmountCol,
                       TableColumn<Employee, String> countryCol, TableColumn<Employee, Integer> teamCol, TableColumn<Employee, Integer> hoursCol,
                       TableColumn<Employee, BigDecimal> utilCol, TableColumn<Employee, Boolean> overheadCol,
                       TableView<Employee> overviewEmployeeTblView, Label employeeDayRateLbl, Label employeeHourlyRateLbl, TextField searchTextField,
                       TabPane teamTabPane, TeamModel teamModel, Button addTeambtn) {
        this.employeeModel = employeeModel;
        this.nameCol = nameCol;
        this.annualSalaryCol = annualSalaryCol;
        this.overHeadMultiCol = overHeadMultiCol;
        this.annualAmountCol = annualAmountCol;
        this.countryCol = countryCol;
        this.teamCol = teamCol;
        this.hoursCol = hoursCol;
        this.utilCol = utilCol;
        this.overheadCol = overheadCol;
        this.overviewEmployeeTblView = overviewEmployeeTblView;
        this.employeeDayRateLbl = employeeDayRateLbl;
        this.employeeHourlyRateLbl = employeeHourlyRateLbl;
        this.searchTextField = searchTextField;
        this.TeamTabPane = teamTabPane;
        this.teamModel = teamModel;
        this.addTeambtn = addTeambtn;
        addTeambtn.setOnAction(this::addTeam);
    }


    public void initialize(){
        ratesListener();
        populateEmployeeTableView();
        setSearchEvent();
        addTableTabs();
    }

    private void setSearchEvent() {
        searchTextField.setOnKeyReleased(event -> {
            String keyword = searchTextField.getText();
            try {
                ObservableList<Employee> filteredEmployees = employeeModel.searchEmployees(keyword);
                overviewEmployeeTblView.setItems(filteredEmployees);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
    }


    private void addTableTabs()  {
        List<Team> teams = null;
        try {
            teams = teamModel.getAllTeams(); //all the teams
            for (Team team: teams){ //for each team...
                Tab tab = new Tab(team.getName()); //create a new tab for that team
                tab.setContent(createTableForTeam(team)); //adds a table with the employees from team to the tab
                TeamTabPane.getTabs().add(tab); //add that tab to TabPane
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private TableView<Employee> createTableForTeam(Team team){
        //creating table and its columns and adding columns to table
        TableView<Employee> teamTblView = new TableView<>();
        TableColumn<Employee, String> nameCol = new TableColumn<>();
        nameCol.setText("Name");
        teamTblView.getColumns().add(nameCol);
        TableColumn<Employee, String> rateCol = new TableColumn<>();
        rateCol.setText("Rates");
        teamTblView.getColumns().add(rateCol);


        nameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));

        //getting all employees in team and adding them to the table
        ObservableList<Employee> employeesInTeam = employeeModel.getAllEmployeesFromTeam(team.getId());
        teamTblView.setItems(employeesInTeam);

        return teamTblView;
    }

    private void addTeam(ActionEvent event) {
        Team newTeam = new Team(teamModel.getLastTeamId()+1, "untitled team");
        teamModel.newTeam(newTeam);
        Tab tab = new Tab(newTeam.getName());
        tab.setContent(createTableForTeam(newTeam));
        TeamTabPane.getTabs().add(tab);
    }



    public void populateEmployeeTableView() {
        try {
            // Get the list of employees from the model
            ObservableList<Employee> employees = employeeModel.getEmployees();

            // Populate the TableView
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            overHeadMultiCol.setCellValueFactory(new PropertyValueFactory<>("overheadMultiPercent"));
            countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
            teamCol.setCellValueFactory(new PropertyValueFactory<>("teamId"));
            hoursCol.setCellValueFactory(new PropertyValueFactory<>("workingHours"));
            //This method just adds dollar signs in front of the money values
            //Once we add Conversion from USD to Euro this will probably need to be tweaked
            addDollarSignsTableview();
            //This method adds % signs to our tableview
            addPercentSignsTableView();
            overheadCol.setCellValueFactory(new PropertyValueFactory<>("isOverheadCost"));
            overviewEmployeeTblView.setItems(employees);
        } catch (BBExceptions e) {
            e.printStackTrace();
        }
    }

    private void addDollarSignsTableview() {
        NumberFormat format = NumberFormat.getNumberInstance();
        //we set the format to have a minimum of 2 and a maximum of 2 digits after the decimal
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        //set up Annual Salary cell
        annualSalaryCol.setCellValueFactory(new PropertyValueFactory<>("annualSalary"));
        annualSalaryCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                //Here we use the NumberFormat class to format our numbers to have commas and add a $
                setText(empty ? null : "$" + format.format(value));
            }
        });
        //set up Annual amount cell the same way
        annualAmountCol.setCellValueFactory(new PropertyValueFactory<>("annualAmount"));
        annualAmountCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty ? null : "$" + format.format(value));
            }
        });
    }

    public void addPercentSignsTableView() {
        overHeadMultiCol.setCellValueFactory(new PropertyValueFactory<>("overheadMultiPercent"));
        overHeadMultiCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                //This checks if cell is empty, if not continues...
                //% is a placeholder for the value that will be inserted
                //.2 this tells our tableview we want 2 digits after the decimal
                //f indicates it's a floating point number (a number with a decimal)
                //% we add this to the end of the number
                setText(empty ? null : String.format("%.2f%%", value));
            }
        });

        utilCol.setCellValueFactory(new PropertyValueFactory<>("utilization"));
        utilCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty ? null : String.format("%.2f%%", value));
            }
        });
    }

    public void calculateEmployeeRates() {
        Employee selectedEmployee = overviewEmployeeTblView.getSelectionModel().getSelectedItem();
        if(selectedEmployee != null){
            employeeHourlyRateLbl.setText("$" + employeeModel.calculateHourlyRate(selectedEmployee) + "/Hr without overhead");
            employeeDayRateLbl.setText("$" + employeeModel.calculateDailyRate(selectedEmployee) + "/Day without overhead");
        }
    }

    public void ratesListener() {
        // Listener to the overview table view to calculate the rates
        overviewEmployeeTblView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                calculateEmployeeRates();
            }
        });
    }

}
