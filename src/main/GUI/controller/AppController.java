package GUI.controller;

import BE.Employee;
import Exceptions.BBExceptions;
import GUI.controller.tabs.OverviewTab;
import GUI.model.EmployeeModel;
import com.neovisionaries.i18n.CountryCode;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;

public class AppController {

    // --------Employee tab Label Fields--------
    @FXML
    private Label annualSalaryLbl;
    @FXML
    private Label overheadMultiplierLbl;
    @FXML
    private Label fixedAnnualAmountLbl;
    @FXML
    private Label countryLbl;
    @FXML
    private Label teamLbl;
    @FXML
    private Label annualWorkingHourLbl;
    @FXML
    private Label utilizationLbl;
    @FXML
    private Label resourceLbl;
    //--------------------------------------
    //----------Overview Rate Labels--------
    @FXML
    private Label employeeDayRateLbl;
    @FXML
    private Label employeeHourlyRateLbl;
    @FXML
    private Label teamDayRateLbl;
    @FXML
    private Label teamHourlyRateLbl;
    // -------------------------------------
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

   public void initialize() {
        //We pass all our FXML elements and employeeModel to the overviewTab class constructor
       OverviewTab overviewTab = new OverviewTab(employeeModel, nameCol, annualSalaryCol, overHeadMultiCol, annualAmountCol, countryCol, teamCol, hoursCol, utilCol, overheadCol, overviewEmployeeTblView, employeeDayRateLbl, employeeHourlyRateLbl);
       //Create our own initialize to easily call the methods in the class
       overviewTab.initialize();

       populateCountryComboBox();
       populateEmployeeListView();
   }

    public void populateEmployeeListView() {
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
