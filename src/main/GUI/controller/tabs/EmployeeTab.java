package GUI.controller.tabs;

import BE.Employee;
import Exceptions.BBExceptions;
import GUI.model.EmployeeModel;
import com.neovisionaries.i18n.CountryCode;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.math.BigDecimal;

public class EmployeeTab {
    private EmployeeModel employeeModel;
    private ListView<Employee> employeeLV;
    private ComboBox<String> countryCmbBox;
    private TextField nameTxt;
    private TextField annualSalaryTxt;
    private TextField overheadMultiTxt;
    private TextField annualAmtTxt;
    private CheckBox overheadChkBox;
    private TextField yearlyHrsTxt;
    private TextField utilizationTxt;
    private Button addEmployeeBtn;


    public EmployeeTab(EmployeeModel employeeModel, ListView<Employee> employeeLV, ComboBox<String> countryCmbBox, TextField nameTxt, TextField annualSalaryTxt, TextField overheadMultiTxt, TextField annualAmtTxt, CheckBox overheadChkBox, TextField yearlyHrsTxt, TextField utilizationTxt, Button addEmployeeBtn) {
        this.employeeModel = employeeModel;
        this.employeeLV = employeeLV;
        this.countryCmbBox = countryCmbBox;
        this.nameTxt = nameTxt;
        this.annualSalaryTxt = annualSalaryTxt;
        this.overheadMultiTxt = overheadMultiTxt;
        this.annualAmtTxt = annualAmtTxt;
        this.overheadChkBox = overheadChkBox;
        this.yearlyHrsTxt = yearlyHrsTxt;
        this.utilizationTxt = utilizationTxt;
        this.addEmployeeBtn = addEmployeeBtn;
        //Add ActionEvent to our button
        this.addEmployeeBtn.setOnAction(this::addEmployee);
    }

    public void initialize(){
        populateCountryComboBox();
        populateEmployeeListView();
    }

    private void populateEmployeeListView() {
        try {
            // Get the list of employees from the model
            ObservableList<Employee> employees = employeeModel.getEmployees();

            // Populate the ListView
            employeeLV.setCellFactory(param -> new ListCell<>() {
                @Override
                //We Override the current updateItem method for listviews by JavaFX and tell our compiler
                protected void updateItem(Employee employee, boolean empty) {
                    super.updateItem(employee, empty);
                    if (empty || employee == null) {
                        setText(null);
                    } else {
                        //We set the text to show the employee name
                        setText("ID# - " +employee.getName());
                    }
                }
            });
            employeeLV.setItems(employees);
        } catch (BBExceptions e) {
            e.printStackTrace();
        }
    }

    public void populateCountryComboBox() {
        for (CountryCode code : CountryCode.values()) {
            countryCmbBox.getItems().add(code.getName());
        }
    }

    private void addEmployee(ActionEvent actionEvent) {
        Employee employee = new Employee();
        employee.setName(nameTxt.getText());
        employee.setAnnualSalary(new BigDecimal(annualSalaryTxt.getText()));
        employee.setOverheadMultiPercent(new BigDecimal(overheadMultiTxt.getText()));
        employee.setAnnualAmount(new BigDecimal(annualAmtTxt.getText()));
        employee.setCountry(countryCmbBox.getValue());
        employee.setIsOverheadCost(overheadChkBox.isSelected());
        employee.setWorkingHours(Integer.parseInt(yearlyHrsTxt.getText()));
        employee.setUtilization(new BigDecimal(utilizationTxt.getText()));

        try {
            employeeModel.addNewEmployee(employee);
        } catch (BBExceptions e){
            e.printStackTrace();
        }
    }
}
