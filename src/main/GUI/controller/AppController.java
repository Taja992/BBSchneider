package GUI.controller;

import BE.Employee;
import GUI.controller.tabs.EmployeeTab;
import GUI.controller.tabs.OverviewEmployeeTable;
import GUI.controller.tabs.OverviewTab;
import GUI.model.EmployeeModel;
import GUI.model.TeamModel;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.math.BigDecimal;

public class AppController {


    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private javafx.scene.chart.StackedBarChart<String, Number> stackedBarChart;
    @FXML
    private Tab employeeTab;
    @FXML
    private LineChart<String, Number> lineChart;
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
    private JFXToggleButton currencyChangeToggleBtn;
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
    private ComboBox<String> overviewCountryCmbBox;
    @FXML
    private TextField conversionTxt;
    // -------------------------------------


    private final EmployeeModel employeeModel;
    private final TeamModel teamModel;

    public AppController(){
        teamModel = new TeamModel();
        employeeModel = new EmployeeModel();
    }

   public void initialize() {

       OverviewEmployeeTable overviewEmployeeTable = new OverviewEmployeeTable(employeeModel, teamModel, nameCol, annualSalaryCol, overHeadMultiCol,
               annualAmountCol, countryCol, teamCol, hoursCol, utilCol, overheadCol, overviewEmployeeTblView);

       overviewEmployeeTable.initialize();

        //We pass all our FXML elements and employeeModel to the overviewTab class constructor
       OverviewTab overviewTab = new OverviewTab(employeeModel, employeeDayRateLbl, employeeHourlyRateLbl, searchTextField, teamTabPane,
               teamModel, addTeambtn, teamDayRateLbl, teamHourlyRateLbl, currencyChangeToggleBtn, grossMarginComboBox, markUpTxt,
               overviewCountryCmbBox, conversionTxt, overviewEmployeeTable);
       //Create our own initialize to easily call the methods in the class
       overviewTab.initialize();

       //This is where we handle our EmployeeTab
       EmployeeTab employeeTab = new EmployeeTab(employeeModel, employeeLV, countryCmbBox, nameTxt, annualSalaryTxt,
               overheadMultiTxt, annualAmtTxt, overheadChkBox,yearlyHrsTxt, utilizationTxt, addEmployeeBtn,
               employeesSearchTxt);

       employeeTab.initialize();
       generateMockData();

   }


    public void generateMockData() {
        // For LineChart
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Team 1");
        for (int week = 1; week <= 52; week++) {
            series1.getData().add(new XYChart.Data<>(String.valueOf(week), Math.random() * 5000));
        }
        lineChart.getData().add(series1);

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Team 2");
        for (int week = 1; week <= 52; week++) {
            series2.getData().add(new XYChart.Data<>(String.valueOf(week), Math.random() * 5000));
        }
        lineChart.getData().add(series2);
    }


}
