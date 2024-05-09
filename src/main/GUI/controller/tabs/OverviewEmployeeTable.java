package GUI.controller.tabs;

import BE.Employee;
import BE.Team;
import Exceptions.BBExceptions;
import GUI.model.EmployeeModel;
import GUI.model.TeamModel;
import com.neovisionaries.i18n.CountryCode;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class OverviewEmployeeTable {

    private final TableColumn<Employee, String> nameCol;
    private final TableColumn<Employee, BigDecimal> annualSalaryCol;
    private final TableColumn<Employee, BigDecimal> overHeadMultiCol;
    private final TableColumn<Employee, BigDecimal> annualAmountCol;
    private final TableColumn<Employee, String> countryCol;
    private final TableColumn<Employee, Integer> hoursCol;
    private final TableColumn<Employee, BigDecimal> utilCol;
    private final TableColumn<Employee, Boolean> overheadCol;
    private final TableView<Employee> overviewEmployeeTblView;
    private final EmployeeModel employeeModel;
    private final TeamModel teamModel;
    private final Map<String, Integer> teamNameToId = new HashMap<>();
    private final ObservableList<String> allTeamNames = FXCollections.observableArrayList();

    public OverviewEmployeeTable (EmployeeModel employeeModel, TeamModel teamModel,
           TableColumn<Employee, String> nameCol, TableColumn<Employee, BigDecimal> annualSalaryCol,
           TableColumn<Employee, BigDecimal> overHeadMultiCol, TableColumn<Employee, BigDecimal> annualAmountCol,
           TableColumn<Employee, String> countryCol, TableColumn<Employee, Integer> hoursCol,
           TableColumn<Employee, BigDecimal> utilCol, TableColumn<Employee, Boolean> overheadCol,
           TableView<Employee> overviewEmployeeTblView) {
        this.employeeModel = employeeModel;
        this.teamModel = teamModel;
        this.nameCol = nameCol;
        this.annualSalaryCol = annualSalaryCol;
        this.overHeadMultiCol = overHeadMultiCol;
        this.annualAmountCol = annualAmountCol;
        this.countryCol = countryCol;
        this.hoursCol = hoursCol;
        this.utilCol = utilCol;
        this.overheadCol = overheadCol;
        this.overviewEmployeeTblView = overviewEmployeeTblView;
    }

    public void setItems(ObservableList<Employee> employees) {
        overviewEmployeeTblView.setItems(employees);
    }

    public Employee getSelectedEmployee() {
        return overviewEmployeeTblView.getSelectionModel().getSelectedItem();
    }

    public TableView<Employee> getTableView() {
        return overviewEmployeeTblView;
    }

    public Map<String, Integer> getTeamNameToId() {
        return teamNameToId;
    }

    public void initialize(){
        overviewEmployeeTblView.setEditable(true);
        populateEmployeeTableView();
        addEmployeeListener();
    }


    private void populateEmployeeTableView() {
        try {
            // Setup the TableView
            setupTableView();

            // Get the list of employees from the model
            ObservableList<Employee> employees = employeeModel.getEmployees();

            //Makes columns editable
            makeNameEditable();
            makeCountryEditable();
            makeTeamEditable();
            makeAnnualHoursEditable();
            //These methods format the tableview to have $ and commas as well as allows them to be editable
            formatAnnualSalaryCol();
            formatAnnualAmountCol();
            //These methods format the tableview to have % as well as allows them to be editable
            formatOverheadMultiPercent();
            formatUtilization();
            makeOverheadEditable();

            overviewEmployeeTblView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
            overviewEmployeeTblView.setItems(employees);
        } catch (BBExceptions e) {
            e.printStackTrace();
        }
    }

    //This listener was added because of a weird bug that once you update an employee the add employee
    //button wasnt properly updating the tableview anymore
    private void addEmployeeListener(){
        employeeModel.employeeAddedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                populateEmployeeTableView();
                employeeModel.employeeAddedProperty().set(false);
            }
        });
    }

    private void setupTableView() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        overHeadMultiCol.setCellValueFactory(new PropertyValueFactory<>("overheadMultiPercent"));
        hoursCol.setCellValueFactory(new PropertyValueFactory<>("workingHours"));
        annualSalaryCol.setCellValueFactory(new PropertyValueFactory<>("annualSalary"));
        annualAmountCol.setCellValueFactory(new PropertyValueFactory<>("annualAmount"));
        utilCol.setCellValueFactory(new PropertyValueFactory<>("utilization"));
        overHeadMultiCol.setCellValueFactory(new PropertyValueFactory<>("overheadMultiPercent"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        overheadCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getIsOverheadCost()));
    }

    private void makeNameEditable() {
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

    private void makeAnnualHoursEditable() {
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

    private void formatAnnualSalaryCol() {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);

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

    private void makeAnnualSalaryColEditable() {
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

    private void formatAnnualAmountCol() {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);

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

    private void makeAnnualAmountColEditable() {
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

    private void formatUtilization() {

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

    private void makeutilizationEditable(){
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

    private void formatOverheadMultiPercent() {

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

    private void makeOverheadMultiPercentEditable(){
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

    private void makeCountryEditable(){
        ObservableList<String> countries = FXCollections.observableArrayList();
        for (CountryCode code : CountryCode.values()) {
            countries.add(code.getName());
        }

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

    public void makeTeamEditable() throws BBExceptions {
        clearTeamData();
        populateTeamData();
        //setupTeamColumn();
        //handleTeamColumnEdit();
    }

    private void clearTeamData() {
        //we clear these to prevent getting duplicated lists
        //teamNameToId = HashMap, allTeamNames = ObservableList- clear both to prevent duplicating
        teamNameToId.clear();
        allTeamNames.clear();
    }

    private void populateTeamData() throws BBExceptions {
        //First we set up a hashmap so we can have a quick link between IDs and Names on the ComboBox
        //we use the getAllTeams method to populate this
        for (Team team : teamModel.getAllTeams()) {
            //when we use .put the first parameter is the "Key" so when we call .keySet it gives us team names
            teamNameToId.put(team.getName(), team.getId());
        }
        //Add No team to our list to be able to set things to Null
        //we use addFirst so it stays ontop after sort
        allTeamNames.addFirst("No Team");
        //now using our hashmap we add everything to out observable list by calling hashmap.keySet
        allTeamNames.addAll(teamNameToId.keySet());
        //Sorts things alphabetically
        FXCollections.sort(allTeamNames);
    }

    /*
    private void setupTeamColumn() {
        teamCol.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        teamCol.setCellFactory(ComboBoxTableCell.forTableColumn(allTeamNames));
    }
     */

    /*
    private void handleTeamColumnEdit() {
        teamCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            String newTeamName = event.getNewValue();
            //We then can get our new team Id by inputting the new team name into .get
            Integer newTeamId = teamNameToId.get(newTeamName);
            //Added an if statement to deal with no team and setting ID to Null
            if("No Team".equals(newTeamName)){
                employee.setTeamIdEmployee(null);
                employee.setTeamName("No Team");
            } else if (newTeamId != null) {
                employee.setTeamIdEmployee(newTeamId);
                //Because we added team object to employee we are able to set the new team name easily
                employee.setTeamName(newTeamName);
            }
            try {
                employeeModel.updateEmployee(employee);
            } catch (BBExceptions e){
                e.printStackTrace();
            }
        });
    }
     */

    private void makeOverheadEditable() {
        overheadCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getIsOverheadCost()));
        // Make the cell able to become a checkbox
        overheadCol.setCellFactory(tableColumn -> new CheckBoxTableCell<>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    //we use .getGraphic for a visual representation of the checkbox
                    CheckBox checkBox = (CheckBox) this.getGraphic();
                    //add a listener onto our checkbox
                    checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                        if (isSelected != wasSelected) {
                            //.getIndex for getting the employee of selected cell
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
}
