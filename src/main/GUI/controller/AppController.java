package GUI.controller;

import BE.Employee;
import BE.Team;
import Exceptions.BBExceptions;
import GUI.model.EmployeeModel;
import GUI.model.SnapshotModel;
import GUI.model.TeamModel;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AppController {



    @FXML
    private TabPane snapshotTabPane;
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
    public TableColumn<Employee, String> teamCol;
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
    private Button addEmployeeBtn;
    // -------------------------------------

    private String currencySymbol = "$";
    private OverviewEmployeeTable overviewEmployeeTable;
    private final EmployeeModel employeeModel;
    private final TeamModel teamModel;
    private TeamTable teamTable;
    private SnapshotModel snapshotModel;

        //Coupling
    public AppController(){
        teamModel = new TeamModel();
        employeeModel = new EmployeeModel();
        snapshotModel = new SnapshotModel();
    }

   public void initialize() {
            //Cohesion
       this.overviewEmployeeTable = new OverviewEmployeeTable(employeeModel, teamModel, nameCol, teamCol, annualSalaryCol, overHeadMultiCol,
               annualAmountCol, countryCol, hoursCol, utilCol, teamUtilColSum, overheadCol, overviewEmployeeTblView, addEmployeeBtn);

       this.overviewEmployeeTable.initialize();

       this.teamTable = new TeamTable(employeeModel, teamModel, teamTabPane, addTeamBtn, overviewEmployeeTable, this);

       this.teamTable.initialize();

       generateMockData();
       employeeRatesListener();
       setSearchEvent();
       teamRatesListener();
       currencyChangeToggleBtnListener();
       markUpListener();
       grossMarginListener();
       populateComboBox();
       setupCountryBox();
       addCountryListener();
       countryRatesListener();
       selectTeamOnStart();
       selectFirstEmployee();
       createTabsForSnapshots();
   }

   public void selectFirstEmployee() {
       if (!overviewEmployeeTblView.getItems().isEmpty()) {
           overviewEmployeeTblView.getSelectionModel().selectFirst();
       }
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

    public void CreateSnapshotFile(ActionEvent event) {

        LocalDateTime currentDate = LocalDateTime.now();

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        String newFileName = snapshotModel.createSnapshotFile("Snapshot on " + currentDate.format(format));

        String tabName = newFileName.substring(12);
        tabName = tabName.replace("-", "/");


        Tab tab = new Tab(tabName);
        tab.setClosable(false);
        tab.setId(tabName);
        tab.setContent(createTabPaneForSnapshot(newFileName + ".db"));

        snapshotTabPane.getTabs().add(tab);

    }

    //creates the tabs for each snapshot
    private void createTabsForSnapshots(){
        Map<String, String> allSnapshots = snapshotModel.getAllSnapshotNames();
        //snapshotTabPane.getStyleClass().add(".snapShotTabPane");

        for(String name : allSnapshots.keySet()){
            //System.out.println(name);
            Tab tab = new Tab(name);
            tab.setClosable(false);
            tab.setId(name);
            //tab.getStyleClass().add(".snapShotTabPane");
            tab.setContent(createTabPaneForSnapshot(allSnapshots.get(name)));

            snapshotTabPane.getTabs().add(tab);
            //System.out.println(tab.getId());
        }
        orderSnapshotTabs();


    }

    //creates the content inside each snapshot tab (the tabpane including all the teams)
    private TabPane createTabPaneForSnapshot(String filename){
        TabPane snapTabPane = new TabPane();
        List<Team> teams = null;
        try {
            teams = snapshotModel.getAllTeamsInSnapshot(filename);
        } catch (BBExceptions e) {
            throw new RuntimeException(e);
        }

        for (Team team: teams){
            Tab tab = new Tab(team.getName());
            tab.setUserData(team);
            tab.setClosable(false);
            ObservableList<Employee> employeesInTeam = null;
            try {
                employeesInTeam = (ObservableList<Employee>) snapshotModel.getAllEmployeesFromTeam(team.getId(), filename);
            } catch (BBExceptions e) {
                throw new RuntimeException(e);
            }
            TableView<Employee> content = teamTable.createTableForTeam(team, employeesInTeam);

            TableColumn<Employee, Boolean> teamOverheadCol = (TableColumn<Employee, Boolean>) content.getColumns().get(7);
            makeOverheadColumnNotEditable(teamOverheadCol);
            tab.setContent(content);
            snapTabPane.getTabs().add(tab);

        }

        return snapTabPane;
    }

    private void makeOverheadColumnNotEditable(TableColumn<Employee, Boolean> Col){
        Col.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getTeamOverhead()));

        Col.setCellFactory(column -> new TableCell<Employee, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {

                    Employee employee = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(employee.getTeamOverhead());
                    checkBox.setDisable(true);
                    setGraphic(checkBox);
                }
            }
        });
    }

    private void orderSnapshotTabs(){
        ObservableList<Tab> allTabs = snapshotTabPane.getTabs();

        allTabs.sort((tab1, tab2) ->{

            String tab1Name = tab1.getId();
            String tab2Name = tab2.getId();

            //if either name contains "(2)" in case this is a duplicate file
            if(tab1Name.contains("(") || tab2Name.contains("(")){
                return tab1Name.compareTo(tab2Name); //compare them as strings if can't compare as date

            } else {
                DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate date1 = LocalDate.parse(tab1Name, parser);


                LocalDate date2 = LocalDate.parse(tab2Name, parser);


                return date1.compareTo(date2);
            }


        });

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

            ObservableList<Employee> filteredEmployees = null;
            try {
                filteredEmployees = employeeModel.searchEmployees(keyword, overviewCountryCmbBox.getSelectionModel().getSelectedItem());
            } catch (BBExceptions e) {
                throw new RuntimeException(e);
            }
            overviewEmployeeTable.setItems(filteredEmployees);

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
    try{
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
      } catch (BBExceptions e) {
            showAlert("Error", e.getMessage());
      }
    }

    void calculateTeamRates(int teamId) {
    try {
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
          } catch (BBExceptions e) {
            showAlert("Error calculating team rates", e.getMessage());
         }
    }

    public void calculateEmployeeRates() {
        try {
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
    } catch (BBExceptions e) {
        showAlert("Error", e.getMessage());
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

                try {
                    // Get the current hourly and daily rates
                    double individualHourlyRate = employeeModel.calculateHourlyRate(overviewEmployeeTable.getSelectedEmployee());
                    double individualDailyRate = employeeModel.calculateDailyRate(overviewEmployeeTable.getSelectedEmployee());

                    double teamHourlyRate = teamModel.calculateTotalHourlyRate(((Team) teamTabPane.getSelectionModel().getSelectedItem().getUserData()).getId());
                    double teamDailyRate = teamModel.calculateTotalDailyRate(((Team) teamTabPane.getSelectionModel().getSelectedItem().getUserData()).getId());

                    // Apply the multiplier using method in employeebll
                    individualHourlyRate *= employeeModel.calculateMarkUp(markupValue);
                    individualDailyRate *= employeeModel.calculateMarkUp(markupValue);

                    teamHourlyRate *= employeeModel.calculateMarkUp(markupValue);
                    teamDailyRate *= employeeModel.calculateMarkUp(markupValue);

                    // Update the labels
                    employeeHourlyRateLbl.setText(currencySymbol + String.format("%.2f", individualHourlyRate) + "/Hour");
                    employeeDayRateLbl.setText(currencySymbol +  String.format("%.2f", individualDailyRate)+ "/Day");

                    teamHourlyRateLbl.setText(currencySymbol + String.format("%.2f", teamHourlyRate) + "/Hour");
                    teamDayRateLbl.setText(currencySymbol +  String.format("%.2f", teamDailyRate)+ "/Day");
                } catch (BBExceptions e) {
                    showAlert("Error", e.getMessage());
                }

            } catch (NumberFormatException e) {
                // If the new value is not a number, revert to 0
                markUpTxt.setText("0.00");
            }
        });
    }

    public void grossMarginListener(){
        grossMarginComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Parse the selected value to a double
                double grossMarginValue = Double.parseDouble(grossMarginComboBox.getSelectionModel().getSelectedItem().toString().replace("%", ""));

                // Get the current hourly and daily rates
                try {
                    double individualHourlyRate = employeeModel.calculateHourlyRate(overviewEmployeeTable.getSelectedEmployee());
                    double individualDailyRate = employeeModel.calculateDailyRate(overviewEmployeeTable.getSelectedEmployee());

                    double teamHourlyRate = teamModel.calculateTotalHourlyRate(((Team) teamTabPane.getSelectionModel().getSelectedItem().getUserData()).getId());
                    double teamDailyRate = teamModel.calculateTotalDailyRate(((Team) teamTabPane.getSelectionModel().getSelectedItem().getUserData()).getId());

                    // Apply the multiplier using method in employeebll
                    individualHourlyRate *= employeeModel.calculateGrossMargin(grossMarginValue);
                    individualDailyRate *= employeeModel.calculateGrossMargin(grossMarginValue);

                    teamHourlyRate *= employeeModel.calculateGrossMargin(grossMarginValue);
                    teamDailyRate *= employeeModel.calculateGrossMargin(grossMarginValue);

                    // Update the labels
                    employeeHourlyRateLbl.setText(currencySymbol + String.format("%.2f", individualHourlyRate) + "/Hour");
                    employeeDayRateLbl.setText(currencySymbol +  String.format("%.2f", individualDailyRate)+ "/Day");

                    teamHourlyRateLbl.setText(currencySymbol + String.format("%.2f", teamHourlyRate) + "/Hour");
                    teamDayRateLbl.setText(currencySymbol +  String.format("%.2f", teamDailyRate)+ "/Day");
                } catch (BBExceptions e) {
                    showAlert("Error", e.getMessage());
                }
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
