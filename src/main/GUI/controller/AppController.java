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
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AppController {

// in this class we handle filtering, snapshots and rates calculations


    @FXML
    private TextField workingHoursTxt;
    @FXML
    private HBox snapshotHBox;
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
    public TableColumn<Employee, String> teamCol;
    @FXML
    private TableView<Employee> overviewEmployeeTblView;
    @FXML
    private TextField searchTextField;
    @FXML
    private TabPane teamTabPane;
    @FXML
    private ComboBox<String> overviewCountryCmbBox;
    @FXML
    private ComboBox<String> snapshotComboBox;
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
    @FXML
    private Button createSnapshotBtn;
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
               annualAmountCol, countryCol, hoursCol, utilCol, teamUtilColSum, overviewEmployeeTblView, addEmployeeBtn);

       this.overviewEmployeeTable.initialize();

       this.teamTable = new TeamTable(employeeModel, teamModel, teamTabPane, addTeamBtn, overviewEmployeeTable, this);

       this.teamTable.initialize();


       workingHoursTxt.setText(Integer.toString(8));
       employeeRatesListener();
       setSearchEvent();
       teamRatesListener();
       currencyChangeToggleBtnListener();
       markUpListener();
       grossMarginListener();
       setupCountryBox();
       addCountryListener();
       countryRatesListener();
       workingHoursListener();
       selectTeamOnStart();
       selectFirstEmployee();
       createTabsForSnapshots();
       populateComboBox();
       toolTips();
   }

   public void selectFirstEmployee() {
       if (!overviewEmployeeTblView.getItems().isEmpty()) {
           overviewEmployeeTblView.getSelectionModel().selectFirst();
       }
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

       snapshotComboBox.getItems().add(tabName);

    }
    // POPULATE COMBOBOX FROM HERE
    //creates the tabs for each snapshot
    private void createTabsForSnapshots(){
        Map<String, String> allSnapshots = snapshotModel.getAllSnapshotNames();

        // Assuming snapshotComboBox is your ComboBox
        snapshotComboBox.getItems().clear(); // Clear existing items if any

        for(String name : allSnapshots.keySet()){
            snapshotComboBox.getItems().add(name);
        }

        // Set an action listener on the ComboBox
        snapshotComboBox.setOnAction((event) -> {
            String selectedSnapshot = snapshotComboBox.getSelectionModel().getSelectedItem();
            if (selectedSnapshot != null) {
                Map<String, String> snapshotList = snapshotModel.getAllSnapshotNames();

                snapshotHBox.getChildren().clear();
                TabPane snapTabPane = createTabPaneForSnapshot(snapshotList.get(selectedSnapshot));
                snapshotHBox.getChildren().add(snapTabPane);
                HBox.setHgrow(snapTabPane, Priority.ALWAYS);
            }
        });

        orderSnapshotTabs();

        // Select the first item by default
        if (!snapshotComboBox.getItems().isEmpty()) {
            snapshotComboBox.getSelectionModel().select(0);
            String selectedSnapshot = snapshotComboBox.getSelectionModel().getSelectedItem();
            if (selectedSnapshot != null) {
                Map<String, String> snapshotList = snapshotModel.getAllSnapshotNames();

                snapshotHBox.getChildren().clear();
                TabPane snapTabPane = createTabPaneForSnapshot(snapshotList.get(selectedSnapshot));
                snapshotHBox.getChildren().add(snapTabPane);
                HBox.setHgrow(snapTabPane, Priority.ALWAYS);

            }
        }


    }

    //creates the content inside each snapshot tab (the tabpane including all the teams)
    private TabPane createTabPaneForSnapshot(String filename){
        TabPane snapTabPane = new TabPane();
        snapTabPane.getStyleClass().add("teamTabPane");
        snapTabPane.setMinWidth(826);
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
            content.setEditable(false);

            //to counteract the columns being editable (from the createTableForTeam() method)
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
        ObservableList<String> allItems = snapshotComboBox.getItems();

        allItems.sort((item1, item2) ->{

            String tab1Name = item1;
            String tab2Name = item2;

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
                calculateCountryRates(selectedCountry, Integer.parseInt(workingHoursTxt.getText()));
            }


        });
    }

    private void calculateCountryRates(String country, int hoursPerDay){
    try{
        double hourlyRate = employeeModel.calculateTotalHourlyRateForCountry(country);
        double dailyRate = employeeModel.calculateTotalDailyRateForCountry(country, hoursPerDay);

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
        Task<Void> calculateRatesTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                double hourlyRate = teamModel.calculateTotalHourlyRate(teamId);
                double dailyRate = teamModel.calculateTotalDailyRate(teamId, Integer.parseInt(workingHoursTxt.getText()));
                if ("€".equals(currencySymbol)) {
                    String conversionText = conversionRateTxt.getText();
                    double conversion = 0.92;
                    if (conversionText != null && !conversionText.isEmpty()) {
                        try {
                            conversion = Double.parseDouble(conversionText);
                        } catch (NumberFormatException e) {
                            Platform.runLater(() -> showAlert("Invalid input", "Please enter a valid number for the conversion rate."));
                        }
                    }
                    hourlyRate *= conversion;
                    dailyRate *= conversion;
                }
                final double finalHourlyRate = hourlyRate;
                final double finalDailyRate = dailyRate;
                Platform.runLater(() -> {
                    teamHourlyRateLbl.setText(currencySymbol +  String.format("%.2f", finalHourlyRate)+ "/Hour");
                    teamDayRateLbl.setText(currencySymbol + String.format("%.2f", finalDailyRate) + "/Day");
                });
                return null;
            }
        };

        calculateRatesTask.setOnFailed(e -> {
            Throwable ex = calculateRatesTask.getException();
            showAlert("Error calculating team rates", ex.getMessage());
        });

        new Thread(calculateRatesTask).start();
    }

    public void calculateEmployeeRates() {
        try {
        Employee selectedEmployee = overviewEmployeeTable.getSelectedEmployee();
        if(selectedEmployee != null){
            double hourlyRate = employeeModel.calculateHourlyRate(selectedEmployee);
            double dailyRate = employeeModel.calculateDailyRate(selectedEmployee, Integer.parseInt(workingHoursTxt.getText()));
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

    public void workingHoursListener() {
        // Action event handler for when the user presses the Enter key
        workingHoursTxt.setOnAction(event -> updateWorkingHours());

        // Focus change listener for when the TextField loses focus
        workingHoursTxt.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // If the TextField has lost focus
                updateWorkingHours();
            }
        });
    }

    private void updateWorkingHours() {
        String newValue = workingHoursTxt.getText();
        try {
            // Try to parse the new value to an integer
            int newWorkingHours = Integer.parseInt(newValue);

            // Check if the new value is within the range 0-24
            if (newWorkingHours >= 0 && newWorkingHours <= 24) {
                // If it is, update the working hours in the BLLController
                EmployeeModel.setWorkingHours(newWorkingHours);
            } else {
                // If it's not, show an error message to the user and revert the TextField to the previous valid value
                showAlert("Invalid input", "Working hours must be between 0 and 24.");
                workingHoursTxt.setText(Integer.toString(EmployeeModel.getWorkingHours()));
            }
        } catch (NumberFormatException e) {
            // If the new value is not a number, show an error message to the user and revert the TextField to the previous valid value
            showAlert("Invalid input", "Working hours must be a number.");
            workingHoursTxt.setText(Integer.toString(EmployeeModel.getWorkingHours()));
        }
        calculateEmployeeRates();
        calculateTeamRates(((Team) teamTabPane.getSelectionModel().getSelectedItem().getUserData()).getId());
        calculateCountryRates(overviewCountryCmbBox.getSelectionModel().getSelectedItem(), Integer.parseInt(workingHoursTxt.getText()));
    }

public void markUpListener() {
    markUpTxt.setOnAction(event -> {
        if (markUpTxt.getText() == null || markUpTxt.getText().isEmpty()) {
            workingHoursTxt.requestFocus();
            return;
        }
        try {
            double markupValue = Double.parseDouble(markUpTxt.getText());
            if (markupValue > 100) {
                markUpTxt.setText("100.00");
            } else if (markupValue < 0) {
                markUpTxt.setText("0.00");
            } else {
                markUpTxt.setText(String.format("%.2f", markupValue));
            }

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    //create a task and start new thread to run updateRates
                    updateRates(markupValue);
                    return null;
                }
            };
            new Thread(task).start();

        } catch (NumberFormatException e) {
            markUpTxt.setText("0.00");
        }
        workingHoursTxt.requestFocus();
    });
}

public void grossMarginListener() {
        //listener on grossMarginComboBox
    grossMarginComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        if (grossMarginComboBox.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        if (newValue != null) {
            //removes the % for calculation
            double grossMarginValue = Double.parseDouble(grossMarginComboBox.getSelectionModel().getSelectedItem().toString().replace("%", ""));
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    //we override call method and call our updateRates method using our value as calculation
                    updateRates(grossMarginValue);
                    return null;
                }
            };
            //start our task in a new thread
            new Thread(task).start();
        }
    });
}

public void updateRates(double value) {
    try {
        double individualHourlyRate = employeeModel.calculateHourlyRate(overviewEmployeeTable.getSelectedEmployee());
        double individualDailyRate = employeeModel.calculateDailyRate(overviewEmployeeTable.getSelectedEmployee(), Integer.parseInt(workingHoursTxt.getText()));

        double teamHourlyRate = teamModel.calculateTotalHourlyRate(((Team) teamTabPane.getSelectionModel().getSelectedItem().getUserData()).getId());
        double teamDailyRate = teamModel.calculateTotalDailyRate(((Team) teamTabPane.getSelectionModel().getSelectedItem().getUserData()).getId(), Integer.parseInt(workingHoursTxt.getText()));

        individualHourlyRate *= employeeModel.calculateMarkUp(value);
        individualDailyRate *= employeeModel.calculateMarkUp(value);

        teamHourlyRate *= employeeModel.calculateMarkUp(value);
        teamDailyRate *= employeeModel.calculateMarkUp(value);

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        final double finalIndividualHourlyRate = individualHourlyRate;
        final double finalIndividualDailyRate = individualDailyRate;
        final double finalTeamHourlyRate = teamHourlyRate;
        final double finalTeamDailyRate = teamDailyRate;

        //platform.runlater to make sure the labels are updated on the JavaFX thread and not our new one
        Platform.runLater(() -> {
            employeeHourlyRateLbl.setText(currencySymbol + nf.format(finalIndividualHourlyRate) + "/Hour");
            employeeDayRateLbl.setText(currencySymbol + nf.format(finalIndividualDailyRate) + "/Day");

            teamHourlyRateLbl.setText(currencySymbol + nf.format(finalTeamHourlyRate) + "/Hour");
            teamDayRateLbl.setText(currencySymbol + nf.format(finalTeamDailyRate) + "/Day");
        });
    } catch (BBExceptions e) {
        Platform.runLater(() -> showAlert("Error", e.getMessage()));
    } catch (NumberFormatException e) {
        Platform.runLater(() -> showAlert("Invalid input", "Please enter valid numbers for the markup and gross margin."));
    }
}

    public void employeeRatesListener() {

        // Listener to the overview table view to calculate the rates
        overviewEmployeeTable.getTableView().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                calculateEmployeeRates();
                calculateTeamRates(((Team) teamTabPane.getSelectionModel().getSelectedItem().getUserData()).getId());
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
                    calculateCountryRates(newValue, Integer.parseInt(workingHoursTxt.getText()));
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
    //////////////////////Error handling//////////////////////
    //////////////////////////////////////////////////////////

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void toolTips() {
        installTooltip(addEmployeeBtn, "Create new employee");
        installTooltip(addTeamBtn, "Create new team");
        installTooltip(createSnapshotBtn, "Create database snapshot");

    }

    private void installTooltip(javafx.scene.Node node, String text) {
        Tooltip tooltip = new Tooltip(text);
        Tooltip.install(node, tooltip);
    }
}
