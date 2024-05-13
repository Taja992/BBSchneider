package GUI.controller;

import BE.Employee;
import BE.Team;
import DAL.SnapshotDAO;
import Exceptions.BBExceptions;
import GUI.model.EmployeeModel;
import GUI.model.TeamModel;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.math.BigDecimal;

public class AppController {


    @FXML
    private LineChart<String, Number> lineChart;
    //--------------------------------------
    //----------Overview Tab----------------
    @FXML
    public ComboBox grossMarginComboBox;
    @FXML
    public TextField markUpTxt;
    @FXML
    private JFXToggleButton changeCurrencyToggleBtn;
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
    private TableColumn<Employee, Integer> hoursCol;
    @FXML
    private TableColumn<Employee, BigDecimal> utilCol;
    @FXML
    private TableColumn<Employee, BigDecimal> teamUtilColSum;
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
    private ComboBox<String> overviewCountryCmbBox;
    @FXML
    private TextField conversionRateTxt;
    @FXML
    private Label countryDayRateLbl;
    @FXML
    private Label countryHourlyRateLbl;
    @FXML
    private Button addTeamBtn;
    @FXML
    private Button addEmployeeBtn2;
    // -------------------------------------

    private String currencySymbol = "$";
    private OverviewEmployeeTable overviewEmployeeTable;
    private final EmployeeModel employeeModel;
    private final TeamModel teamModel;
    private TeamTable teamTable;

    private SnapshotDAO snapDAO = new SnapshotDAO();

    public AppController(){
        teamModel = new TeamModel();
        employeeModel = new EmployeeModel();
    }

   public void initialize() {

       this.overviewEmployeeTable = new OverviewEmployeeTable(employeeModel, teamModel, nameCol, annualSalaryCol, overHeadMultiCol,
               annualAmountCol, countryCol, hoursCol, utilCol, teamUtilColSum, overheadCol, overviewEmployeeTblView, addEmployeeBtn2);

       this.overviewEmployeeTable.initialize();

       this.teamTable = new TeamTable(employeeModel, teamModel, teamTabPane, addTeamBtn);

       this.teamTable.initialize();

       generateMockData();
       employeeRatesListener();
       setSearchEvent();
       teamRatesListener();
       currencyChangeToggleBtnListener();
       markUpListener();
       populateComboBox();
       setupCountryBox();
       addCountryListener();
       countryRatesListener();
       selectTeamOnStart();
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

    public void populateComboBox() {
        for (int i = 0; i <= 100; i++) {
            grossMarginComboBox.getItems().add(i + "%");
        }
    }


    ////////////////////////////////////////////////////////
    //////////////////Filtering/////////////////////////////
    ////////////////////////////////////////////////////////

    private void setupCountryBox() {

        overviewCountryCmbBox.getItems().addAll(employeeModel.getAllCountriesUsed());
        overviewCountryCmbBox.getSelectionModel().select(0);

        overviewCountryCmbBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(newValue != null){
                    filterEmployeeTableByCountry((String) newValue);
                } else {
                    countryDayRateLbl.setText("No country selected");
                    countryHourlyRateLbl.setText("No country selected");
                }
            }
        });

    }

    private void addCountryListener(){
        employeeModel.countryAddedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                updateCountryBox();
                employeeModel.countryAddedProperty().set(false);
            }
        });
    }

    private void updateCountryBox(){
        overviewCountryCmbBox.getItems().clear();
        overviewCountryCmbBox.getItems().addAll(employeeModel.getAllCountriesUsed());
        overviewCountryCmbBox.getSelectionModel().select(0);
    }

    private void filterEmployeeTableByCountry(String country){
        overviewEmployeeTable.setItems(employeeModel.filterEmployeesByCountry(country));
    }

    private void setSearchEvent() {
        searchTextField.setOnKeyReleased(event -> {
            String keyword = searchTextField.getText();
            try {
                ObservableList<Employee> filteredEmployees = employeeModel.searchEmployees(keyword, overviewCountryCmbBox.getSelectionModel().getSelectedItem());
                overviewEmployeeTable.setItems(filteredEmployees);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
    }


    ////////////////////////////////////////////////////////
    ///////////////////////Rates////////////////////////////
    ////////////////////////////////////////////////////////

    public void currencyChangeToggleBtnListener() {
        changeCurrencyToggleBtn.setOnAction(event -> {
            if (!changeCurrencyToggleBtn.isSelected()) {
                // USD selected
                currencySymbol = "$";
            } else {
                // EUR selected
                currencySymbol = "€";
            }

            //updating Employee rates
            Employee selectedEmployee = overviewEmployeeTable.getSelectedEmployee();
            if (selectedEmployee != null) {
                // Recalculate and update the rates
                calculateEmployeeRates();
            } else {
                employeeDayRateLbl.setText("No employee selected");
                employeeHourlyRateLbl.setText("No employee selected");
            }

            //updating total team rates
            Tab selectedTab = teamTabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null && selectedTab.getContent() instanceof TableView<?>) {
                TableView<Employee> selectedTable = (TableView<Employee>) selectedTab.getContent();
                if (!selectedTable.getItems().isEmpty()) {
                    Team team = (Team) selectedTab.getUserData(); //get the Team object from the selected tab
                    int teamId = team.getId(); //get the teamId from the Team object
                    calculateTeamRates(teamId);
                } else {
                    teamDayRateLbl.setText("$0.00/Day");
                    teamHourlyRateLbl.setText("$0.00/Hour");
                }
            }

            if(overviewCountryCmbBox.getSelectionModel().getSelectedItem() != null){
                String selectedCountry = overviewCountryCmbBox.getSelectionModel().getSelectedItem();
                calculateCountryRates(selectedCountry);
            }


        });
    }

    private void calculateCountryRates(String country){

        double hourlyRate = employeeModel.calculateTotalHourlyRateForCountry(country);
        double dailyRate = employeeModel.calculateTotalDailyRateForCountry(country);

        //convert the currency if needed
        if ("€".equals(currencySymbol)) {
            String conversionText = conversionRateTxt.getText();
            double conversion = 0.92; //default conversion rate if nothing is set
            if(conversionText != null && !conversionText.isEmpty()){
                try{
                    conversion = Double.parseDouble(conversionText);
                } catch(NumberFormatException e){
                    showAlert("Invalid input", "Please enter a valid number for the conversion rate.");
                }
            }

            hourlyRate *= conversion;
            dailyRate *= conversion;

        }

        countryHourlyRateLbl.setText(currencySymbol + String.format("%.2f",hourlyRate)+ "/Hour");
        countryDayRateLbl.setText(currencySymbol + String.format("%.2f",dailyRate)+ "/Day");
    }

    private void calculateTeamRates(int teamId){

        double hourlyRate = teamModel.calculateTotalHourlyRate(teamId);
        double dailyRate = teamModel.calculateTotalDailyRate(teamId);
        if ("€".equals(currencySymbol)) {
            String conversionText = conversionRateTxt.getText();
            double conversion = 0.92;
            if (conversionText != null && !conversionText.isEmpty()) {
                try {
                    conversion = Double.parseDouble(conversionText);
                } catch (NumberFormatException e) {
                    showAlert("Invalid input", "Please enter a valid number for the conversion rate.");
                }
            }
            hourlyRate *= conversion;
            dailyRate *= conversion;
        }
        teamHourlyRateLbl.setText(currencySymbol +  String.format("%.2f", hourlyRate)+ "/Hour");
        teamDayRateLbl.setText(currencySymbol + String.format("%.2f", dailyRate) + "/Day");
    }

    public void calculateEmployeeRates() {
        Employee selectedEmployee = overviewEmployeeTable.getSelectedEmployee();
        if(selectedEmployee != null){
            double hourlyRate = employeeModel.calculateHourlyRate(selectedEmployee);
            double dailyRate = employeeModel.calculateDailyRate(selectedEmployee);
            if ("€".equals(currencySymbol)) {
                String conversionText = conversionRateTxt.getText();
                double conversion = 0.92;
                if (conversionText != null && !conversionText.isEmpty()) {
                    try {
                        conversion = Double.parseDouble(conversionText);
                    } catch (NumberFormatException e) {
                        showAlert("Invalid input", "Please enter a valid number for the conversion rate.");
                    }
                }
                hourlyRate *= conversion;
                dailyRate *= conversion;
            }
            employeeHourlyRateLbl.setText(currencySymbol + String.format("%.2f", hourlyRate) + "/Hour");
            employeeDayRateLbl.setText(currencySymbol +  String.format("%.2f", dailyRate)+ "/Day");
        }
    }

    public void markUpListener() {
        markUpTxt.focusedProperty().addListener((observable, oldValue, newValue) -> {
            try {
                // Parse the new value to a double
                double markupValue = Double.parseDouble(markUpTxt.getText());

                // If the value is greater than 100, set it to 100
                if (markupValue > 100) {
                    markUpTxt.setText("100.00");
                } else if (markupValue < 0) {
                    // If the value is less than 0, set it to 0
                    markUpTxt.setText("0.00");
                } else {
                    // If the value is within the range, format it to two decimal places
                    markUpTxt.setText(String.format("%.2f", markupValue));
                }

                // Get the current hourly and daily rates
                double hourlyRate = employeeModel.calculateHourlyRate(overviewEmployeeTable.getSelectedEmployee());
                double dailyRate = employeeModel.calculateDailyRate(overviewEmployeeTable.getSelectedEmployee());

                // Apply the multiplier using method in employeebll
                hourlyRate *= employeeModel.calculateMarkUp(markupValue);
                dailyRate *= employeeModel.calculateMarkUp(markupValue);

                // Update the labels
                employeeHourlyRateLbl.setText(currencySymbol + String.format("%.2f", hourlyRate) + "/Hour");
                employeeDayRateLbl.setText(currencySymbol +  String.format("%.2f", dailyRate)+ "/Day");

            } catch (NumberFormatException e) {
                // If the new value is not a number, revert to 0
                markUpTxt.setText("0.00");
            }
        });
    }

    public void employeeRatesListener() {

        // Listener to the overview table view to calculate the rates
        overviewEmployeeTable.getTableView().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                calculateEmployeeRates();
            }
        });
    }

    public void teamRatesListener() {
        //adding a listener to tabPane so the daily/hourly rates of the selected team will be shown
        teamTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){ //if the selected tab isn't empty
                Team team = (Team) newValue.getUserData(); //get the Team object from the selected tab
                int teamId = team.getId(); //get the teamId from the Team object
                calculateTeamRates(teamId); //set all the rates based on the team and the conversion rate
            } else{ //if the tableview is empty, then just print 0's for the rates
                teamHourlyRateLbl.setText("$0.00/Hour");
                teamDayRateLbl.setText("$0.00/Day");
            }
        });
    }

    private void countryRatesListener(){
        //adding a listener to the country combobox so the total country rates can be update
        overviewCountryCmbBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue != null){
                    calculateCountryRates(newValue);
                }else{
                    teamHourlyRateLbl.setText("$0.00/Hour");
                    teamDayRateLbl.setText("$0.00/Day");
                }
            }
        });
    }

    public void selectTeamOnStart() {

        int teamId = teamModel.getAllTeams().getFirst().getId(); //getting our first team
        calculateTeamRates(teamId);
    }

    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });

    }
}
