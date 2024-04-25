package GUI.controller;

import BE.Employee;
import Exceptions.BBExceptions;
import GUI.model.EmployeeModel;
import com.neovisionaries.i18n.CountryCode;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    private TabPane mainTabPane;
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
     //  fitTabs();
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

//    private void fitTabs() {
//        DoubleBinding binding = Bindings.createDoubleBinding(() ->
//                        mainTabPane.getWidth() / mainTabPane.getTabs().size(),
//                mainTabPane.widthProperty(),
//                mainTabPane.getTabs());
//
//        mainTabPane.tabMinWidthProperty().bind(binding);
//        mainTabPane.tabMaxWidthProperty().bind(binding);
//    }


}
