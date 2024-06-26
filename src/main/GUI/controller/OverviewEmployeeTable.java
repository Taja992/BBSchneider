package GUI.controller;

import BE.Employee;
import BE.Team;
import Exceptions.BBExceptions;
import GUI.model.EmployeeModel;
import com.neovisionaries.i18n.CountryCode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;


public class OverviewEmployeeTable {

    private final TableColumn<Employee, String> nameCol;
    private final TableColumn<Employee, String> teamCol;
    private final TableColumn<Employee, BigDecimal> annualSalaryCol;
    private final TableColumn<Employee, BigDecimal> overHeadMultiCol;
    private final TableColumn<Employee, BigDecimal> annualAmountCol;
    private final TableColumn<Employee, String> countryCol;
    private final TableColumn<Employee, Integer> hoursCol;
    private final TableColumn<Employee, BigDecimal> utilCol;
    private final TableView<Employee> overviewEmployeeTblView;
    private final EmployeeModel employeeModel;
    private final TableColumn<Employee, BigDecimal> teamUtilColSum;
    private Button addEmployeeBtn;




    public OverviewEmployeeTable (EmployeeModel employeeModel,
                                  TableColumn<Employee, String> nameCol, TableColumn<Employee, String> teamCol, TableColumn<Employee, BigDecimal> annualSalaryCol,
                                  TableColumn<Employee, BigDecimal> overHeadMultiCol, TableColumn<Employee, BigDecimal> annualAmountCol,
                                  TableColumn<Employee, String> countryCol, TableColumn<Employee, Integer> hoursCol,
                                  TableColumn<Employee, BigDecimal> utilCol, TableColumn<Employee, BigDecimal> teamUtilColSum,
                                  TableView<Employee> overviewEmployeeTblView, Button addEmployeeBtn) {
        this.employeeModel = employeeModel;
        this.nameCol = nameCol;
        this.teamCol = teamCol;
        this.annualSalaryCol = annualSalaryCol;
        this.overHeadMultiCol = overHeadMultiCol;
        this.annualAmountCol = annualAmountCol;
        this.countryCol = countryCol;
        this.hoursCol = hoursCol;
        this.utilCol = utilCol;
        this.teamUtilColSum = teamUtilColSum;
        this.overviewEmployeeTblView = overviewEmployeeTblView;
        this.addEmployeeBtn = addEmployeeBtn;

        addEmployeeBtn.setOnAction(this::addEmployeeBtn);
    }


    public void initialize(){
        overviewEmployeeTblView.setEditable(true);
        populateEmployeeTableView();
        dragAndDrop();
    }


    private void populateEmployeeTableView() {
        try {
            // Set up the TableView
            setupTableView();

            // Get the list of employees from the model
            ObservableList<Employee> employees = employeeModel.getEmployees();

            //Makes columns editable
            makeNameEditable();
            makeCountryEditable();
            makeAnnualHoursEditable();
            //These methods format the tableview to have $ and commas as well as allows them to be editable
            formatAnnualSalaryCol();
            formatAnnualAmountCol();
            //These methods format the tableview to have % as well as allows them to be editable
            formatOverheadMultiPercent();
            formatUtilization();
            formatTeamUtilSum();
            populateTeamUtilizationSumColumn();


            overviewEmployeeTblView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
            overviewEmployeeTblView.setItems(employees);
        } catch (BBExceptions e) {
            showAlert("Error", e.getMessage());
        }
    }


    private void setupTableView() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        teamCol.setCellValueFactory(new PropertyValueFactory<>("teamNames"));
        overHeadMultiCol.setCellValueFactory(new PropertyValueFactory<>("overheadMultiPercent"));
        hoursCol.setCellValueFactory(new PropertyValueFactory<>("workingHours"));
        annualSalaryCol.setCellValueFactory(new PropertyValueFactory<>("annualSalary"));
        annualAmountCol.setCellValueFactory(new PropertyValueFactory<>("annualAmount"));
        utilCol.setCellValueFactory(new PropertyValueFactory<>("utilization"));
        overHeadMultiCol.setCellValueFactory(new PropertyValueFactory<>("overheadMultiPercent"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
    }

    private void addEmployeeBtn(ActionEvent actionEvent) {
        Employee newEmployee = new Employee();
        newEmployee.setName("New Employee");
        newEmployee.setOverheadMultiPercent(BigDecimal.ZERO);
        newEmployee.setWorkingHours(0);
        newEmployee.setAnnualAmount(BigDecimal.ZERO);
        newEmployee.setAnnualSalary(BigDecimal.ZERO);
        newEmployee.setUtilization(BigDecimal.ZERO);
        newEmployee.setOverheadMultiPercent(BigDecimal.ZERO);
        newEmployee.setCountry("");

        overviewEmployeeTblView.getItems().addFirst(newEmployee);

        int newIndex = 0;

        overviewEmployeeTblView.getSelectionModel().select(newIndex);
        nameCol.getTableView().edit(newIndex, nameCol);
        try {
            employeeModel.addNewEmployee(newEmployee);
        }catch (BBExceptions e) {
            showAlert("Error",e.getMessage());
        }
    }

    //This shows which employees are getting filtered on the table
    public void setItems(ObservableList<Employee> employees) {
        overviewEmployeeTblView.setItems(employees);
    }

    public Employee getSelectedEmployee() {
        return overviewEmployeeTblView.getSelectionModel().getSelectedItem();
    }

    public TableView<Employee> getTableView() {
        return overviewEmployeeTblView;
    }

    private void dragAndDrop() {
        overviewEmployeeTblView.setRowFactory(tv -> {
            TableRow<Employee> row = new TableRow<>();
            //set the Drag method event to the new row
            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    //first we take the index(list number) from the tableview
                    Integer index = row.getIndex();
                    //set the transfermode to move only on the row
                    Dragboard dragBoard = row.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent clipboardContent = new ClipboardContent();
                    //change the int index to a string to attach to clipboard
                    clipboardContent.putString(index.toString());
                    //attach our clipboard to the dragboard
                    dragBoard.setContent(clipboardContent);
                    //close the event
                    event.consume();
                }
            });
            return row;
        });
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
                showAlert("Error", e.getMessage());
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
                showAlert("Error", e.getMessage());
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
                showAlert("Error", e.getMessage());
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
                showAlert("Error", e.getMessage());
            }
        });
    }

    private void populateTeamUtilizationSumColumn() {
        teamUtilColSum.setCellValueFactory(cellData -> {
            Employee employee = cellData.getValue();
            int employeeId = employee.getId();
            // Check if the value is already cached
            if (employeeModel.getTeamUtilSumCache(employeeId) != null) {
                return new SimpleObjectProperty<>(employeeModel.getTeamUtilSumCache(employeeId));
                // If not, calculate the sum of the utilizations for all teams
            } else {
                List<Team> teams = employee.getTeams();
                BigDecimal totalUtilization = BigDecimal.ZERO;
                // Loop through all teams and sum the utilizations
                for (Team team : teams) {
                    try {
                        BigDecimal teamUtilization = employeeModel.getUtilizationForTeam(employeeId, team.getId());
                        totalUtilization = totalUtilization.add(teamUtilization);
                    } catch (BBExceptions e) {
                        throw new RuntimeException(e);
                    }
                }
                // Cache the value if it wasnt stored before
                employeeModel.setTeamUtilSumCache(employeeId, totalUtilization);
                return new SimpleObjectProperty<>(totalUtilization);
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

                makeutilizationEditable();
                 }
             });
    }

    private void formatTeamUtilSum() {
        teamUtilColSum.setCellFactory(column -> new TableCell<Employee, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f%%", item));
                }
            }
        });
    }

    private void makeutilizationEditable(){
        utilCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setUtilization(event.getNewValue());
            try {
                employeeModel.updateEmployee(employee);
            } catch (BBExceptions e) {
                showAlert("Error", e.getMessage());
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
                showAlert("Error", e.getMessage());
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
                showAlert("Error", e.getMessage());
            }
        });
    }

    //////////////////////////////////////////////////////////
    //////////////////////Error handling//////////////////////
    //////////////////////////////////////////////////////////


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
