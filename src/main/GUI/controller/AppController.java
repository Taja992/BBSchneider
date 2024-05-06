package GUI.controller;

import BE.Employee;
import GUI.controller.tabs.EmployeeTab;
import GUI.controller.tabs.OverviewTab;
import GUI.model.EmployeeModel;
import GUI.model.TeamModel;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;

public class AppController {



    // --------Employee tab ---------------
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
    @FXML
    private Button addEmployeeBtn;
    //--------------------------------------
    //----------Overview Tab----------------
    @FXML
    public ComboBox grossMarginComboBox;
    @FXML
    public TextField markUpTxt;
    @FXML
    public MFXToggleButton currencyChangeToggleBtn;
    @FXML
    private Label employeeDayRateLbl;
    @FXML
    private Label employeeHourlyRateLbl;
    @FXML
    private Label teamDayRateLbl;
    @FXML
    private Label teamHourlyRateLbl;
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
    private TableColumn<Employee, String> teamCol;
    @FXML
    private TableColumn<Employee, Integer> hoursCol;
    @FXML
    private TableColumn<Employee, BigDecimal> utilCol;
    @FXML
    private TableColumn<Employee, Boolean> overheadCol;
    @FXML
    private TableView<Employee> overviewEmployeeTblView;
    @FXML
    private TextField searchTextField;
    @FXML
    private TextField employeesSearchTxt;
    @FXML
    private TabPane teamTabPane;
    @FXML
    private Button addTeambtn;
    @FXML
    private ChoiceBox countryChcBox;
    // -------------------------------------


    private final EmployeeModel employeeModel;
    private final TeamModel teamModel;

    public AppController(){
        teamModel = new TeamModel();
        employeeModel = new EmployeeModel();
    }

   public void initialize() {
        //We pass all our FXML elements and employeeModel to the overviewTab class constructor
       OverviewTab overviewTab = new OverviewTab(employeeModel, nameCol, annualSalaryCol, overHeadMultiCol, annualAmountCol,
               countryCol, teamCol, hoursCol, utilCol, overheadCol, overviewEmployeeTblView,
               employeeDayRateLbl, employeeHourlyRateLbl, searchTextField, teamTabPane, teamModel, addTeambtn, teamDayRateLbl, teamHourlyRateLbl, currencyChangeToggleBtn,
               grossMarginComboBox, markUpTxt, countryChcBox);
       //Create our own initialize to easily call the methods in the class
       overviewTab.initialize();

       //This is where we handle our EmployeeTab
       EmployeeTab employeeTab = new EmployeeTab(employeeModel, employeeLV, countryCmbBox, nameTxt, annualSalaryTxt,
               overheadMultiTxt, annualAmtTxt, overheadChkBox,yearlyHrsTxt, utilizationTxt, addEmployeeBtn,
               employeesSearchTxt);

       employeeTab.initialize();
   }

}
