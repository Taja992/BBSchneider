package GUI.controller;

import BE.Employee;
import Exceptions.BBExceptions;
import GUI.model.EmployeeModel;
import GUI.model.TeamModel;
import com.neovisionaries.i18n.CountryCode;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private final TableColumn<Employee, BigDecimal> teamUtilColSum;
    private Map<Integer, BigDecimal> totalUtilizationCache = new HashMap<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Button addEmployeeBtn2;


    public OverviewEmployeeTable (EmployeeModel employeeModel, TeamModel teamModel,
                                  TableColumn<Employee, String> nameCol, TableColumn<Employee, BigDecimal> annualSalaryCol,
                                  TableColumn<Employee, BigDecimal> overHeadMultiCol, TableColumn<Employee, BigDecimal> annualAmountCol,
                                  TableColumn<Employee, String> countryCol, TableColumn<Employee, Integer> hoursCol,
                                  TableColumn<Employee, BigDecimal> utilCol, TableColumn<Employee, BigDecimal> teamUtilColSum, TableColumn<Employee, Boolean> overheadCol,
                                  TableView<Employee> overviewEmployeeTblView, Button addEmployeeBtn2) {
        this.employeeModel = employeeModel;
        this.teamModel = teamModel;
        this.nameCol = nameCol;
        this.annualSalaryCol = annualSalaryCol;
        this.overHeadMultiCol = overHeadMultiCol;
        this.annualAmountCol = annualAmountCol;
        this.countryCol = countryCol;
        this.hoursCol = hoursCol;
        this.utilCol = utilCol;
        this.teamUtilColSum = teamUtilColSum;
        this.overheadCol = overheadCol;
        this.overviewEmployeeTblView = overviewEmployeeTblView;
        this.addEmployeeBtn2 = addEmployeeBtn2;

        addEmployeeBtn2.setOnAction(this::addEmployeeBtn2);
    }

    private void addEmployeeBtn2(ActionEvent actionEvent) {
        Employee newEmployee = new Employee();
        newEmployee.setName("New Employee");
        newEmployee.setOverheadMultiPercent(BigDecimal.ZERO);
        newEmployee.setWorkingHours(0);
        newEmployee.setAnnualAmount(BigDecimal.ZERO);
        newEmployee.setAnnualSalary(BigDecimal.ZERO);
        newEmployee.setUtilization(BigDecimal.ZERO);
        newEmployee.setOverheadMultiPercent(BigDecimal.ZERO);
        newEmployee.setIsOverheadCost(false);
        newEmployee.setCountry(""); //revisit this

        overviewEmployeeTblView.getItems().addFirst(newEmployee);

        int newIndex = 0;

        overviewEmployeeTblView.getSelectionModel().select(newIndex);
        nameCol.getTableView().edit(newIndex, nameCol);

        employeeModel.addNewEmployee(newEmployee);
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
            makeAnnualHoursEditable();
            //These methods format the tableview to have $ and commas as well as allows them to be editable
            formatAnnualSalaryCol();
            formatAnnualAmountCol();
            //These methods format the tableview to have % as well as allows them to be editable
            formatOverheadMultiPercent();
            formatUtilization();
            formatTeamUtilSum();
            makeOverheadEditable();
            populateTeamUtilizationSumColumn();


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

    private void populateTeamUtilizationSumColumn() {
        teamUtilColSum.setCellValueFactory(cellData -> {
            Employee employee = cellData.getValue();
            int employeeId = employee.getId();
            BigDecimal totalUtilization = totalUtilizationCache.get(employeeId);

            // If total utilization is not in the cache, calculate it in a background thread
            // executorService single thread executor
            if (totalUtilization == null) {
                executorService.submit(() -> {
                    try {
                        BigDecimal calculatedTotalUtilization = employeeModel.calculateTotalTeamUtil(employeeId);
                        Platform.runLater(() -> {
                            //add the calculation to the hashmap
                            totalUtilizationCache.put(employeeId, calculatedTotalUtilization);
                        });
                    } catch (BBExceptions e) {
                        e.printStackTrace();
                    }
                });
            }

            return new SimpleObjectProperty<>(totalUtilization);
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

//    private void formatUtilization() {
//
////        utilCol.setCellFactory(tableColumn -> new TextFieldTableCell<>(new BigDecimalStringConverter()) {
////            @Override
////            public void updateItem(BigDecimal value, boolean empty) {
////                super.updateItem(value, empty);
////                //This checks if cell is empty, if not continues...
////                //% is a placeholder for the value that will be inserted
////                //.2 this tells our tableview we want 2 digits after the decimal
////                //f indicates it's a floating point number (a number with a decimal)
////                //% we add this to the end of the number
////                setText(empty ? null : String.format("%.2f%%", value));
////            }
////        });
////        makeutilizationEditable();

    private void formatUtilization() {
        // We use a hashmap to store the results so we dont need to do the calculation everytime a cell is rendered
        Map<Integer, BigDecimal> totalUtilizationCache = new HashMap<>();

        // Create an ExecutorService that has a single thread to prevent lag
        //while the employeeModel.calculateTotalTeamUtil(employeeId); calculation runs
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        utilCol.setCellFactory(column -> new TextFieldTableCell<>(new BigDecimalStringConverter()) {
            @Override
            public void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);

                setText(empty ? null : String.format("%.2f%%", item));

                TableRow<Employee> currentRow = getTableRow();

                if (currentRow != null) {
                    Employee employee = currentRow.getItem();
                    if (employee != null) {
                        int employeeId = employee.getId();
                        BigDecimal totalUtilization = totalUtilizationCache.get(employeeId);

                        // If total utilization is not in the hashmap, calculate it in a background thread
                        if (totalUtilization == null) {
                            executorService.submit(() -> {
                                try {
                                    BigDecimal calculatedTotalUtilization = employeeModel.calculateTotalTeamUtil(employeeId);
                                    Platform.runLater(() -> {
                                        totalUtilizationCache.put(employeeId, calculatedTotalUtilization);
                                        updateItem(item, empty);
                                    });
                                } catch (BBExceptions e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } else {
                            if (item != null && totalUtilization.compareTo(item) > 0) {
                                setStyle("-fx-text-fill: #dc0101; -fx-background-color: #efefef;");
                            } else {
                                setStyle("");
                            }
                        }
                    }
                }
            }
        });
        makeutilizationEditable();
    }

    private void formatTeamUtilSum() {
        teamUtilColSum.setCellFactory(tableColumn -> new TextFieldTableCell<>(new BigDecimalStringConverter()) {
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


    public void makeOverheadEditable() {
        overheadCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getIsOverheadCost()));
        overheadCol.setCellFactory(column -> new TableCell<Employee, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(checkBox);
                    Employee employee = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(employee.getIsOverheadCost());
                    //we use setOnAction with the checkbox to make it listen if there is a change
                    checkBox.setOnAction(e -> {
                        employee.setIsOverheadCost(checkBox.isSelected());
                        try {
                            employeeModel.updateEmployee(employee);
                        } catch (BBExceptions ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            }
        });
    }
}