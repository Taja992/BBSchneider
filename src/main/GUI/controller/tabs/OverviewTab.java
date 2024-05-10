package GUI.controller.tabs;

import BE.Employee;
import BE.Team;
import Exceptions.BBExceptions;
import GUI.model.EmployeeModel;
import GUI.model.TeamModel;
import com.jfoenix.controls.JFXToggleButton;
import com.neovisionaries.i18n.CountryCode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverviewTab {

    private final TextField markUpTxt;
    private ComboBox grossMarginComboBox;
    private String currencySymbol = "$";
    private final Label employeeDayRateLbl;
    private final Label employeeHourlyRateLbl;
    private final EmployeeModel employeeModel;
    private final TextField searchTextField;
    private final TabPane teamTabPane;
    private final TeamModel teamModel;
    private final Button addTeambtn;
    private final JFXToggleButton changeCurrencyToggleBtn;
    private final Label teamDayRateLbl;
    private final Label teamHourlyRateLbl;
    private final ComboBox<String> overviewCountryCmbBox;
    private final TextField conversionRate;
    private OverviewEmployeeTable overviewEmployeeTable;


    public OverviewTab(EmployeeModel employeeModel, Label employeeDayRateLbl, Label employeeHourlyRateLbl, TextField searchTextField,
                       TabPane teamTabPane, TeamModel teamModel, Button addTeambtn,
                       Label teamDayRateLbl, Label teamHourlyRateLbl, JFXToggleButton currencyChangeToggleBtn,
                       ComboBox grossMarginComboBox, TextField markUpTxt, ComboBox<String> overviewCountryCmbBox, TextField conversionRate, OverviewEmployeeTable overviewEmployeeTable) {
        this.employeeModel = employeeModel;
        this.employeeDayRateLbl = employeeDayRateLbl;
        this.employeeHourlyRateLbl = employeeHourlyRateLbl;
        this.searchTextField = searchTextField;
        this.teamTabPane = teamTabPane;
        this.teamModel = teamModel;
        this.addTeambtn = addTeambtn;
        this.changeCurrencyToggleBtn = currencyChangeToggleBtn;
        this.grossMarginComboBox = grossMarginComboBox;
        this.markUpTxt = markUpTxt;
        this.conversionRate = conversionRate;

        addTeambtn.setOnAction(this::addTeam);

        this.teamDayRateLbl = teamDayRateLbl;
        this.teamHourlyRateLbl = teamHourlyRateLbl;
        this.overviewCountryCmbBox = overviewCountryCmbBox;
        this.overviewEmployeeTable = overviewEmployeeTable;

    }


    public void initialize() {
        employeeRatesListener();
        setSearchEvent();
        addTableTabs();
        teamRatesListener();
        currencyChangeToggleBtnListener();
        markUpListener();
        populateComboBox();
        setupCountryBox();
        selectTeamOnStart();
    }

    public void populateComboBox() {
        for (int i = 0; i <= 100; i++) {
            grossMarginComboBox.getItems().add(i + "%");
        }
    }


    ////////////////////////////////////////////////////////
    //////////////////Filtering/////////////////////////////
    ////////////////////////////////////////////////////////

    private void setupCountryBox(){
    List<String> allCountries = FXCollections.observableArrayList();
    allCountries.add("All Countries");
    for(CountryCode code : CountryCode.values()){ //adding list of all countries
        allCountries.add(code.getName());
    }

    overviewCountryCmbBox.getItems().addAll(allCountries);
    overviewCountryCmbBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            filterEmployeeTableByCountry((String) newValue);
        }
    });
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
    //////////////////Create Team///////////////////////////
    ////////////////////////////////////////////////////////

    

    public void makeTeamTabTitleEditable(Tab tab) {
        final Label label = new Label(tab.getText());
        final TextField textField = new TextField(tab.getText());

        textField.setVisible(false); // Initially hide the text field

        // When the user clicks the label, show the text field
        label.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                textField.setVisible(true);
                textField.requestFocus();
            }
        });

        // When the user presses Enter, save the new title, hide the text field, and update the team name in the database
        textField.setOnAction(event -> {
            String newTeamName = textField.getText();
            tab.setText(newTeamName);
            label.setText(newTeamName);
            textField.setVisible(false);
            Team team = (Team) tab.getUserData();
            try {
                teamModel.updateTeamName(team.getId(), newTeamName);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }

        });

        // When the text field loses focus, save the new title, hide the text field, and update the team name in the database
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String newTeamName = textField.getText();
                tab.setText(newTeamName);
                label.setText(newTeamName);
                textField.setVisible(false);
                Team team = (Team) tab.getUserData();
                try {
                    teamModel.updateTeamName(team.getId(), newTeamName);
                } catch (BBExceptions e) {
                    e.printStackTrace();
                }
            }
        });

        // Create a StackPane to hold the label and text field
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(label, textField);
        stackPane.setAlignment(Pos.CENTER_LEFT);

        // Set the StackPane as the tab's graphic
        tab.setGraphic(stackPane);
    }


    private void addTableTabs()  {

            ObservableList<Team> teams = teamModel.getAllTeams(); //all the teams
            for (Team team: teams){ //for each team...
                Tab tab = new Tab(team.getName()); //create a new tab for that team
                tab.setUserData(team);
                tab.setClosable(false);
                tab.setContent(createTableForTeam(team)); //adds a table with the employees from team to the tab
                teamTabPane.getTabs().add(tab); //add that tab to TabPane
                makeTeamTabTitleEditable(tab); // make the tab title editable
            }
    }

    private TableView<Employee> createTableForTeam(Team team){
        //creating table and its columns and adding columns to table
        TableView<Employee> teamTblView = new TableView<>();
        teamTblView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Employee, String> nameCol = new TableColumn<>();
        nameCol.setText("Name");
        teamTblView.getColumns().add(nameCol);

        TableColumn<Employee, BigDecimal> salaryCol = new TableColumn<>();
        salaryCol.setText("Annual Salary");
        teamTblView.getColumns().add(salaryCol);

        TableColumn<Employee, BigDecimal> overHeadPerCol = new TableColumn<>();
        overHeadPerCol.setText("Overhead %");
        teamTblView.getColumns().add(overHeadPerCol);

        TableColumn<Employee, BigDecimal> annualCol = new TableColumn<>();
        annualCol.setText("Annual Amount");
        teamTblView.getColumns().add(annualCol);

        TableColumn<Employee, String> countryCol = new TableColumn<>();
        countryCol.setText("Country");
        teamTblView.getColumns().add(countryCol);

        TableColumn<Employee, String> hoursCol = new TableColumn<>();
        hoursCol.setText("Annual Hrs");
        teamTblView.getColumns().add(hoursCol);

        TableColumn<Employee, BigDecimal> utilCol = new TableColumn<>();
        utilCol.setText("Util %");
        teamTblView.getColumns().add(utilCol);

        TableColumn<Employee, String> overHeadCol = new TableColumn<>();
        overHeadCol.setText("Overhead");
        teamTblView.getColumns().add(overHeadCol);

//        TableColumn<Employee, String> rateCol = new TableColumn<>();
//        rateCol.setText("Rates");
//        teamTblView.getColumns().add(rateCol);

        //setting the column values to their values in the database
        nameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("AnnualSalary"));
        overHeadPerCol.setCellValueFactory(new PropertyValueFactory<>("OverheadMultiPercent"));
        annualCol.setCellValueFactory(new PropertyValueFactory<>("AnnualAmount"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("Country"));
        hoursCol.setCellValueFactory(new PropertyValueFactory<>("WorkingHours"));
        utilCol.setCellValueFactory(new PropertyValueFactory<>("Utilization"));
        overHeadCol.setCellValueFactory(new PropertyValueFactory<>("isOverheadCost"));

        //formatting all the columns that need it, check the "make editable" methods for more comments

        formatSalaryColumnForTeams(salaryCol);
        formatSalaryColumnForTeams(annualCol);
        formatPercentageColumnForTeams(overHeadPerCol);
        formatPercentageColumnForTeams(utilCol);


        // Get the list of employees for the team
        ObservableList<Employee> employeesInTeam = employeeModel.getAllEmployeesFromTeam(team.getId());

        teamTblView.setItems(employeesInTeam);

        return teamTblView;
    }

    private void formatPercentageColumnForTeams(TableColumn<Employee, BigDecimal> column){

        column.setCellFactory(tableColumn -> new TableCell<>() {
            @Override
            public void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty ? null : String.format("%.2f%%", value));
            }
        });
    }

    private void formatSalaryColumnForTeams(TableColumn<Employee, BigDecimal> column){
        NumberFormat salaryFormat = NumberFormat.getNumberInstance();
        salaryFormat.setMinimumFractionDigits(2);
        salaryFormat.setMaximumFractionDigits(2);
        column.setCellFactory(tableColumn -> new TableCell<>() {
            @Override
            public void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText("$" + salaryFormat.format(item));
                }
            }
        });
    }

    private void addTeam(ActionEvent event) {
        try {
            int generatedId = teamModel.getLastTeamId() + 1;
            Team newTeam = new Team(generatedId, "Team (" + generatedId + ")");
            teamModel.newTeam(newTeam);
            Tab tab = new Tab(newTeam.getName());
            tab.setUserData(newTeam); //So our new tab carries the team data
            tab.setClosable(false);
            tab.setContent(createTableForTeam(newTeam));
            teamTabPane.getTabs().add(tab);
            makeTeamTabTitleEditable(tab);

        } catch (BBExceptions e) {
            e.printStackTrace();
        }
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
            Employee selectedEmployee = overviewEmployeeTable.getSelectedEmployee();
            if (selectedEmployee != null) {
            // Recalculate and update the rates
            calculateEmployeeRates();
            } else {
                employeeDayRateLbl.setText("No employee selected");
                employeeHourlyRateLbl.setText("No employee selected");
            }
            Tab selectedTab = teamTabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null && selectedTab.getContent() instanceof TableView<?>) {
                TableView<Employee> selectedTable = (TableView<Employee>) selectedTab.getContent();
                if (!selectedTable.getItems().isEmpty()) {
                    Team team = (Team) selectedTab.getUserData(); //get the Team object from the selected tab
                    int teamId = team.getId(); //get the teamId from the Team object
                    calculateTeamRates(teamId);
                } else {
                    teamDayRateLbl.setText("$0/Day");
                    teamHourlyRateLbl.setText("$0/Hour");
                }
            }
        });
    }

    private void calculateTeamRates(int teamId){

        double hourlyRate = teamModel.calculateTotalHourlyRate(teamId);
        double dailyRate = teamModel.calculateTotalDailyRate(teamId);
        if ("€".equals(currencySymbol)) {
            String conversionText = conversionRate.getText();
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
                String conversionText = conversionRate.getText();
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
                teamHourlyRateLbl.setText("$0/Hour");
                teamDayRateLbl.setText("$0/Day");
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
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
