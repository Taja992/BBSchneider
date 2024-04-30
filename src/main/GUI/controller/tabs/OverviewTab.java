package GUI.controller.tabs;

import BE.Employee;
import BE.Team;
import Exceptions.BBExceptions;
import GUI.model.EmployeeModel;
import GUI.model.TeamModel;
import com.neovisionaries.i18n.CountryCode;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverviewTab {

    private final TableColumn<Employee, String> nameCol;
    private final TableColumn<Employee, BigDecimal> annualSalaryCol;
    private final TableColumn<Employee, BigDecimal> overHeadMultiCol;
    private final TableColumn<Employee, BigDecimal> annualAmountCol;
    private final TableColumn<Employee, String> countryCol;
    private final TableColumn<Employee, String> teamCol;
    private final TableColumn<Employee, Integer> hoursCol;
    private final TableColumn<Employee, BigDecimal> utilCol;
    private final TableColumn<Employee, Boolean> overheadCol;
    private final TableView<Employee> overviewEmployeeTblView;
    private final Label employeeDayRateLbl;
    private final Label employeeHourlyRateLbl;
    private final EmployeeModel employeeModel;
    private final TextField searchTextField;
    private final TabPane teamTabPane;
    private final TeamModel teamModel;
    private final Button addTeambtn;
    private Label teamDayRateLbl;
    private Label teamHourlyRateLbl;


    public OverviewTab(EmployeeModel employeeModel, TableColumn<Employee, String> nameCol,
                       TableColumn<Employee, BigDecimal> annualSalaryCol, TableColumn<Employee, BigDecimal> overHeadMultiCol,
                       TableColumn<Employee, BigDecimal> annualAmountCol, TableColumn<Employee, String> countryCol,
                       TableColumn<Employee, String> teamCol, TableColumn<Employee, Integer> hoursCol,
                       TableColumn<Employee, BigDecimal> utilCol, TableColumn<Employee, Boolean> overheadCol,
                       TableView<Employee> overviewEmployeeTblView, Label employeeDayRateLbl, Label employeeHourlyRateLbl, TextField searchTextField,
                       TabPane teamTabPane, TeamModel teamModel, Button addTeambtn,
                       Label teamDayRateLbl, Label teamHourlyRateLbl) {
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
        this.teamTabPane = teamTabPane;
        this.teamModel = teamModel;
        this.addTeambtn = addTeambtn;

        addTeambtn.setOnAction(this::addTeam);

        this.teamDayRateLbl = teamDayRateLbl;
        this.teamHourlyRateLbl = teamHourlyRateLbl;
    }


    public void initialize(){
        overviewEmployeeTblView.setEditable(true);
        ratesListener();
        populateEmployeeTableView();
        setSearchEvent();
        addTableTabs();


        //adding a listener to tabPane so the daily/hourly rates of the selected team will be shown
        teamTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                TableView<Employee> selectedTable = (TableView<Employee>) newValue.getContent();
                if(!selectedTable.getItems().isEmpty()){ //if the tableview in the tab isn't empty...
                    int teamId = selectedTable.getItems().getFirst().getTeamIdEmployee(); //get teamId from first row
                    setTeamRatesLabel(teamId); //set all the rates based on the team
                } else{ //if the tableview is empty, then just print 0's for the rates
                    teamHourlyRateLbl.setText("$0/Hour");
                    teamDayRateLbl.setText("$0/Day");
                }

            }
        });

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
                tab.setClosable(false);
                tab.setContent(createTableForTeam(team)); //adds a table with the employees from team to the tab
                teamTabPane.getTabs().add(tab); //add that tab to TabPane
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
        try {
            Team newTeam = new Team(teamModel.getLastTeamId() + 1, "untitled team");
            teamModel.newTeam(newTeam);
            Tab tab = new Tab(newTeam.getName());
            tab.setClosable(false);
            tab.setContent(createTableForTeam(newTeam));
            teamTabPane.getTabs().add(tab);
        } catch (BBExceptions e) {
            e.printStackTrace();
        }
    }

    private void setTeamRatesLabel(int teamId){
        teamHourlyRateLbl.setText("$" + teamModel.calculateTotalHourlyRate(teamId) + "/Hour");
        teamDayRateLbl.setText("$" + teamModel.calculateTotalDailyRate(teamId) + "/Day");
    }


    public void populateEmployeeTableView() {
        try {
            // Get the list of employees from the model
            ObservableList<Employee> employees = employeeModel.getEmployees();

            // Populate the TableView
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            //Makes name editable
            makeNameEditable();
            overHeadMultiCol.setCellValueFactory(new PropertyValueFactory<>("overheadMultiPercent"));
           // countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
            makeCountryEditable();
            makeTeamEditable();
            hoursCol.setCellValueFactory(new PropertyValueFactory<>("workingHours"));
            makeAnnualHoursEditable();
            //These methods format the tableview to have $ and commas as well as allows them to be editable
            formatAnnualSalaryCol();
            formatAnnualAmountCol();
            //These methods format the tableview to have % as well as allows them to be editable
            formatOverheadMultiPercent();
            formatUtilization();

            makeOverheadEditable();
            overviewEmployeeTblView.setItems(employees);
        } catch (BBExceptions e) {
            e.printStackTrace();
        }
    }

    public void calculateEmployeeRates() {
        Employee selectedEmployee = overviewEmployeeTblView.getSelectionModel().getSelectedItem();
        if(selectedEmployee != null){
            employeeHourlyRateLbl.setText("$" + employeeModel.calculateHourlyRate(selectedEmployee) + "/Hour");
            employeeDayRateLbl.setText("$" + employeeModel.calculateDailyRate(selectedEmployee) + "/Day");
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



    //////////////////////////////////////////////////////////
    ///////////////Editing Employee Table/////////////////////
    //////////////////////////////////////////////////////////

    public void makeNameEditable() {
        // Make the cell able to become a textfield
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        // After editing, it sets the name in the database with .setOnEditCommit
        nameCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setName(event.getNewValue());
            try {
                employeeModel.updateEmployee(employee);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
    }

    public void makeAnnualHoursEditable() {
        // Make the cell able to become a textfield and we use IntegerStringConverter to convert it from a string to an Integer
        hoursCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        // After editing, it sets the name in the database with .setOnEditCommit
        hoursCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setWorkingHours(event.getNewValue());
            try {
                employeeModel.updateEmployee(employee);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
    }


    public void formatAnnualSalaryCol() {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);

        // Set the cell value factory
        annualSalaryCol.setCellValueFactory(new PropertyValueFactory<>("annualSalary"));

        // Make the cell able to become a textfield
        annualSalaryCol.setCellFactory(tableColumn -> new TextFieldTableCell<>(new BigDecimalStringConverter()) {
            @Override
            public void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText("$" + format.format(item));
                }
            }
        });
        // After editing, it sets the annual salary in the database with .setOnEditCommit
        makeAnnualSalaryColEditable();
    }
    public void makeAnnualSalaryColEditable() {
        annualSalaryCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setAnnualSalary(event.getNewValue());
            try {
                employeeModel.updateEmployee(employee);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
    }


    public void formatAnnualAmountCol() {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);

        // Set the cell value factory
        annualAmountCol.setCellValueFactory(new PropertyValueFactory<>("annualAmount"));

        // Make the cell able to become a textfield
        annualAmountCol.setCellFactory(tableColumn -> new TextFieldTableCell<>(new BigDecimalStringConverter()) {
            @Override
            public void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText("$" + format.format(item));
                }
            }
        });
        // After editing, it sets the annual salary in the database with .setOnEditCommit
        makeAnnualAmountColEditable();
    }
    public void makeAnnualAmountColEditable() {
        // After editing, it sets the annual salary in the database with .setOnEditCommit
        annualAmountCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setAnnualAmount(event.getNewValue());
            try {
                employeeModel.updateEmployee(employee);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
    }


    public void formatUtilization() {
        utilCol.setCellValueFactory(new PropertyValueFactory<>("utilization"));

        utilCol.setCellFactory(tableColumn -> new TextFieldTableCell<>(new BigDecimalStringConverter()) {
            @Override
            public void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                //This checks if cell is empty, if not continues...
                //% is a placeholder for the value that will be inserted
                //.2 this tells our tableview we want 2 digits after the decimal
                //f indicates it's a floating point number (a number with a decimal)
                //% we add this to the end of the number
                setText(empty ? null : String.format("%.2f%%", value));
            }
        });
        makeutilizationEditable();
    }
    public void makeutilizationEditable(){
        utilCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setUtilization(event.getNewValue());
            try {
                employeeModel.updateEmployee(employee);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
    }


    public void formatOverheadMultiPercent() {
        overHeadMultiCol.setCellValueFactory(new PropertyValueFactory<>("overheadMultiPercent"));

        overHeadMultiCol.setCellFactory(tableColumn -> new TextFieldTableCell<>(new BigDecimalStringConverter()) {
            @Override
            public void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                //This checks if cell is empty, if not continues...
                //% is a placeholder for the value that will be inserted
                //.2 this tells our tableview we want 2 digits after the decimal
                //f indicates it's a floating point number (a number with a decimal)
                //% we add this to the end of the number
                setText(empty ? null : String.format("%.2f%%", value));
            }
        });
        makeOverheadMultiPercentEditable();
    }
    public void makeOverheadMultiPercentEditable(){
        overHeadMultiCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setOverheadMultiPercent(event.getNewValue());
            try {
                employeeModel.updateEmployee(employee);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
    }


    public void makeCountryEditable(){
        ObservableList<String> countries = FXCollections.observableArrayList();
        for (CountryCode code : CountryCode.values()) {
            countries.add(code.getName());
        }
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        //Give the cell the ability to become a combobox and fill it with our countries library
        countryCol.setCellFactory(ComboBoxTableCell.forTableColumn(countries));

        countryCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setCountry(event.getNewValue());
            try {
                employeeModel.updateEmployee(employee);
            } catch (BBExceptions e){
                e.printStackTrace();
            }
        });
    }

    private void makeTeamEditable() throws BBExceptions {
        //First we set up a hashmap so we can have a quick link between IDs and Names on the ComboBox
        //we use the getAllTeams method to populate this
        Map<String, Integer> teamNameToId = new HashMap<>();
        for (Team team : teamModel.getAllTeams()) {
            //when we use .put the first parameter is the "Key" so when we call .keySet it gives us team names
            teamNameToId.put(team.getName(), team.getId());
        }

        //now using our hashmap we make an observable list of the names by calling .keySet
        ObservableList<String> allTeamNames = FXCollections.observableArrayList(teamNameToId.keySet());
        teamCol.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        teamCol.setCellFactory(ComboBoxTableCell.forTableColumn(allTeamNames));

        teamCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            String newTeamName = event.getNewValue();
            //We then can get our new team Id by inputting the new team name into .get
            Integer newTeamId = teamNameToId.get(newTeamName);
            if (newTeamId != null) {
                employee.setTeamIdEmployee(newTeamId);
                //Because we extend team we are able to set the new team name easily
                employee.setTeamName(newTeamName);
                try {
                    employeeModel.updateEmployee(employee);
                } catch (BBExceptions e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void makeOverheadEditable() {
        overheadCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getIsOverheadCost()));
        // Make the cell able to become a checkbox
        overheadCol.setCellFactory(tableColumn -> new CheckBoxTableCell<>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    CheckBox checkBox = (CheckBox) this.getGraphic();
                    checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                        if (isSelected != wasSelected) {
                            Employee employee = this.getTableView().getItems().get(this.getIndex());
                            employee.setIsOverheadCost(isSelected);
                            try {
                                employeeModel.updateEmployee(employee);
                            } catch (BBExceptions e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

}
