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
    private final EmployeeModel employeeModel;
    private final ListView<Employee> employeeLV;
    private final ComboBox<String> countryCmbBox;
    private final TextField nameTxt;
    private final TextField annualSalaryTxt;
    private final TextField overheadMultiTxt;
    private final TextField annualAmtTxt;
    private final CheckBox overheadChkBox;
    private final TextField yearlyHrsTxt;
    private final TextField utilizationTxt;

    private final TextField employeesSearchTxt;


    public EmployeeTab(EmployeeModel employeeModel, ListView<Employee> employeeLV, ComboBox<String> countryCmbBox,
                       TextField nameTxt, TextField annualSalaryTxt, TextField overheadMultiTxt, TextField annualAmtTxt,
                       CheckBox overheadChkBox, TextField yearlyHrsTxt, TextField utilizationTxt, Button addEmployeeBtn,TextField employeesSearchTxt) {
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
        this.employeesSearchTxt = employeesSearchTxt;
        //Add ActionEvent to our button
        addEmployeeBtn.setOnAction(this::addEmployee);
    }

    public void initialize(){
        populateCountryComboBox();
        populateEmployeeListView();
        setSearchEvent();
    }

    private void setSearchEvent() {
        employeesSearchTxt.setOnKeyReleased(event -> {
            String keyword = employeesSearchTxt.getText();

            try {
                ObservableList<Employee> filteredEmployees = employeeModel.searchEmployees(keyword);
                employeeLV.setItems(filteredEmployees);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
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
                        setText("ID# - " +employee.getEmployeeName());
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
        employee.setEmployeeName(nameTxt.getText());
        employee.setAnnualSalary(convertToBigDecimal(annualSalaryTxt.getText()));
        employee.setOverheadMultiPercent(convertToBigDecimal(overheadMultiTxt.getText()));
        employee.setAnnualAmount(convertToBigDecimal(annualAmtTxt.getText()));
        employee.setCountry(countryCmbBox.getValue());
        employee.setIsOverheadCost(overheadChkBox.isSelected());
        employee.setWorkingHours(convertToInt(yearlyHrsTxt.getText()));
        employee.setUtilization(convertToBigDecimal(utilizationTxt.getText()));

        try {
            employeeModel.addNewEmployee(employee);
        } catch (BBExceptions e){
            e.printStackTrace();
        }
    }


    //Incase user uses $/% or , when they input the values this will account for that
    private BigDecimal convertToBigDecimal(String input) {
        //This uses regex to replace everything that isnt an int
        // the carrot '^' means to negate anything not defined
        //then we add the range 0-9 and a .  this tells regex to negate anything that is not those and replace it with ""
        //The replaceAll was not working for % signs so I added .replace as well to handle if the user inputs %
        String number = input.replaceAll("[^0-9.]", "").replace("%", "");
        return new BigDecimal(number);
    }

    //Same thing as above but for the Integer field
    private int convertToInt(String input) {
        String number = input.replaceAll("[^0-9]", "");
        return Integer.parseInt(number);
    }

}
