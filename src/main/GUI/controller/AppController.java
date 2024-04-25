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
            overHeadMultiCol.setCellValueFactory(new PropertyValueFactory<>("overheadMultiPercent"));
            countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
            teamCol.setCellValueFactory(new PropertyValueFactory<>("teamId"));
            hoursCol.setCellValueFactory(new PropertyValueFactory<>("workingHours"));
            //This method just adds dollar signs in front of the money values
            //Once we add Conversion from USD to Euro this will probably need to be tweaked
            addDollarSignsTableview();
            //This method adds % signs to our tableview
            addPercentSignsTableView();
            overviewEmployeeTblView.setItems(employees);
        } catch (BBExceptions e) {
            e.printStackTrace();
        }
    }

    private void addDollarSignsTableview() {
        //set up Annual Salary cell
        annualSalaryCol.setCellValueFactory(new PropertyValueFactory<>("annualSalary"));
        annualSalaryCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                //This checks if cell is empty, if not it adds a $ and then the value
                //% is a placeholder for the value that will be inserted
                //.2 this tells our tableview we want 2 digits after the decimal
                //f indicates it's a floating point number (a number with a decimal)
                setText(empty ? null : String.format("$%.2f ", value));
            }
        });
        //set up Annual amount cell
        annualAmountCol.setCellValueFactory(new PropertyValueFactory<>("annualAmount"));
        annualAmountCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty ? null : String.format("$%.2f", value));
            }
        });
    }

    public void addPercentSignsTableView() {
        overHeadMultiCol.setCellValueFactory(new PropertyValueFactory<>("overheadMultiPercent"));
        overHeadMultiCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                //this works the same as the $ method % is a placeholder .2 is a decimal f is a floating number and % is what we want added
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
