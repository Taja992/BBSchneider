package GUI.controller.tabs;

import BE.Employee;
import Exceptions.BBExceptions;
import GUI.model.EmployeeModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.text.NumberFormat;

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


    public OverviewTab(EmployeeModel employeeModel, TableColumn<Employee, String> nameCol, TableColumn<Employee, BigDecimal> annualSalaryCol,
                       TableColumn<Employee, BigDecimal> overHeadMultiCol, TableColumn<Employee, BigDecimal> annualAmountCol,
                       TableColumn<Employee, String> countryCol, TableColumn<Employee, Integer> teamCol, TableColumn<Employee, Integer> hoursCol,
                       TableColumn<Employee, BigDecimal> utilCol, TableColumn<Employee, Boolean> overheadCol,
                       TableView<Employee> overviewEmployeeTblView, Label employeeDayRateLbl, Label employeeHourlyRateLbl, TextField searchTextField) {
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
    }


    public void initialize(){
        ratesListener();
        populateEmployeeTableView();
        setSearchEvent();

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
