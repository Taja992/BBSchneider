package GUI.controller;

import BE.Employee;
import Exceptions.BBExceptions;
import GUI.model.EmployeeModel;
import com.neovisionaries.i18n.CountryCode;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;

public class AppController {
    // --------Calculation Label Fields--------
    public Label annualSalaryLbl;
    public Label overheadMultiplierLbl;
    public Label fixedAnnualAmountLbl;
    public Label countryLbl;
    public Label teamLbl;
    public Label annualWorkingHourLbl;
    public Label utilizationLbl;
    public Label resourceLbl;
    // ----------------------------------
    @FXML
    private TableColumn<Employee, String> nameCol;
    @FXML
    private TableColumn<Employee, BigDecimal> annualSalaryCol;
    @FXML
    private TableColumn<Employee, BigDecimal> overHeadMultiCol;
    @FXML
    private TableColumn<Employee, BigDecimal> annualAmountCol;
    @FXML
    private TableColumn<Employee, String> countryCol;
    @FXML
    private TableColumn<Employee, Integer> teamCol;
    @FXML
    private TableColumn<Employee, Integer> hoursCol;
    @FXML
    private TableColumn<Employee, BigDecimal> utilCol;
    @FXML
    private TableColumn<Employee, Boolean> overheadCol;
    @FXML
    private TableView<Employee> overviewEmployeeTblView;
    @FXML
    private ListView<Employee> employeeLV;
    @FXML
    private TextField nameTxt;
    @FXML
    private TextField annualSalaryTxt;
    @FXML
    private TextField overheadMultiTxt;
    @FXML
    private TextField annualAmtTxt;
    @FXML
    private TextField yearlyHrsTxt;
    @FXML
    private TextField utilizationTxt;
    @FXML
    private CheckBox overheadChkBox;
    @FXML
    private ComboBox<String> countryCmbBox;

    private final EmployeeModel employeeModel;

    public AppController(){
        employeeModel = new EmployeeModel();
    }

   public void initialize(){
       populateCountryComboBox();
       populateEmployeeTableView();
       populateEmployeeListView();
   }

    public void populateEmployeeTableView() {
        try {
            // Get the list of employees from the model
            ObservableList<Employee> employees = employeeModel.getEmployees();

            // Populate the TableView
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            annualSalaryCol.setCellValueFactory(new PropertyValueFactory<>("annualSalary"));
            overHeadMultiCol.setCellValueFactory(new PropertyValueFactory<>("overheadMultiPercent"));
            annualAmountCol.setCellValueFactory(new PropertyValueFactory<>("annualAmount"));
            countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
            teamCol.setCellValueFactory(new PropertyValueFactory<>("teamId"));
            hoursCol.setCellValueFactory(new PropertyValueFactory<>("workingHours"));
            utilCol.setCellValueFactory(new PropertyValueFactory<>("utilization"));
            overheadCol.setCellValueFactory(new PropertyValueFactory<>("isOverheadCost"));
            overviewEmployeeTblView.setItems(employees);
        } catch (BBExceptions e) {
            e.printStackTrace();
        }
    }

    public void populateEmployeeListView() {
        try {
            // Get the list of employees from the model
            ObservableList<Employee> employees = employeeModel.getEmployees();

            // Populate the ListView
            employeeLV.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Employee item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
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

    public void addEmployee(ActionEvent actionEvent) {
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
