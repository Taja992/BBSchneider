package GUI.controller.tabs;

import BE.Employee;
import BE.Team;
import Exceptions.BBExceptions;
import GUI.model.EmployeeModel;
import GUI.model.TeamModel;
import com.neovisionaries.i18n.CountryCode;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverviewTab {

    private TextField markUpTxt;
    private ComboBox grossMarginComboBox;
    private double conversionRate = 0.93;
    private String currencySymbol = "$";
    private final TableColumn<Employee, String> nameCol;
    private final TableColumn<Employee, BigDecimal> annualSalaryCol;
    private final TableColumn<Employee, BigDecimal> overHeadMultiCol;
    private final TableColumn<Employee, BigDecimal> annualAmountCol;
    private final TableColumn<Employee, String> countryCol;
    private final TableColumn<Employee, String> teamCol;
    private final TableColumn<Employee, Integer> hoursCol;
    private final TableColumn<Employee, BigDecimal> utilCol;
    private final TableColumn<Employee, Boolean> overheadCol;
    private final TableView<Employee> overviewEmployeeTblView;
    private final Label employeeDayRateLbl;
    private final Label employeeHourlyRateLbl;
    private final EmployeeModel employeeModel;
    private final TextField searchTextField;
    private final TabPane teamTabPane;
    private final TeamModel teamModel;
    private final Button addTeambtn;
    private MFXToggleButton changeCurrencyToggleBtn;
    private Label teamDayRateLbl;
    private Label teamHourlyRateLbl;
    private final Map<String, Integer> teamNameToId = new HashMap<>();
    private final ObservableList<String> allTeamNames = FXCollections.observableArrayList();

    public OverviewTab(EmployeeModel employeeModel, TableColumn<Employee, String> nameCol,
                       TableColumn<Employee, BigDecimal> annualSalaryCol, TableColumn<Employee, BigDecimal> overHeadMultiCol,
                       TableColumn<Employee, BigDecimal> annualAmountCol, TableColumn<Employee, String> countryCol,
                       TableColumn<Employee, String> teamCol, TableColumn<Employee, Integer> hoursCol,
                       TableColumn<Employee, BigDecimal> utilCol, TableColumn<Employee, Boolean> overheadCol,
                       TableView<Employee> overviewEmployeeTblView, Label employeeDayRateLbl, Label employeeHourlyRateLbl, TextField searchTextField,
                       TabPane teamTabPane, TeamModel teamModel, Button addTeambtn,
                       Label teamDayRateLbl, Label teamHourlyRateLbl, MFXToggleButton currencyChangeToggleBtn,
                       ComboBox grossMarginComboBox, TextField markUpTxt) {
        this.employeeModel = employeeModel;
        this.nameCol = nameCol;
        this.annualSalaryCol = annualSalaryCol;
        this.overHeadMultiCol = overHeadMultiCol;
        this.annualAmountCol = annualAmountCol;
        this.countryCol = countryCol;
        this.teamCol = teamCol;
        this.hoursCol = hoursCol;
        this.utilCol = utilCol;
        this.overheadCol = overheadCol;
        this.overviewEmployeeTblView = overviewEmployeeTblView;
        this.employeeDayRateLbl = employeeDayRateLbl;
        this.employeeHourlyRateLbl = employeeHourlyRateLbl;
        this.searchTextField = searchTextField;
        this.teamTabPane = teamTabPane;
        this.teamModel = teamModel;
        this.addTeambtn = addTeambtn;
        this.changeCurrencyToggleBtn = currencyChangeToggleBtn;
        this.grossMarginComboBox = grossMarginComboBox;
        this.markUpTxt = markUpTxt;

        addTeambtn.setOnAction(this::addTeam);

        this.teamDayRateLbl = teamDayRateLbl;
        this.teamHourlyRateLbl = teamHourlyRateLbl;
    }


    public void initialize(){
        overviewEmployeeTblView.setEditable(true);
        ratesListener();
        populateEmployeeTableView();
        setSearchEvent();
        addTableTabs();
        teamRatesListener();
        currencyChangeToggleBtnListener();
        markUpListener();


    }

    public void currencyChangeToggleBtnListener() {
        changeCurrencyToggleBtn.setOnAction(event -> {
            if (!changeCurrencyToggleBtn.isSelected()) {
                // USD selected
                currencySymbol = "$";
            } else {
                // EUR selected
                currencySymbol = "€";
            }
            // Recalculate and update the rates (recalculation to dollar or euro is not yet implemented)
            calculateEmployeeRates();
            if (overviewEmployeeTblView.getSelectionModel().getSelectedItem().getTeamIdEmployee() != null) {
                setTeamRatesLabel(overviewEmployeeTblView.getSelectionModel().getSelectedItem().getTeamIdEmployee());
            }
        });
    }

    public void teamRatesListener() {
        //adding a listener to tabPane so the daily/hourly rates of the selected team will be shown
        teamTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TableView<Employee> selectedTable = (TableView<Employee>) newValue.getContent();
            if(!selectedTable.getItems().isEmpty()){ //if the tableview in the tab isn't empty...
                int teamId = selectedTable.getItems().getFirst().getTeamIdEmployee(); //get teamId from first row
                setTeamRatesLabel(teamId); //set all the rates based on the team
            } else{ //if the tableview is empty, then just print 0's for the rates
                teamHourlyRateLbl.setText("$0/Hour");
                teamDayRateLbl.setText("$0/Day");
            }

        });
    }

    private void setSearchEvent() {
        searchTextField.setOnKeyReleased(event -> {
            String keyword = searchTextField.getText();
            try {
                ObservableList<Employee> filteredEmployees = employeeModel.searchEmployees(keyword);
                overviewEmployeeTblView.setItems(filteredEmployees);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
    }

//    public void makeTabTitleEditable(Tab tab) {
//        final Label label = new Label(tab.getText());
//        final TextField textField = new TextField(tab.getText());
//
//        textField.setVisible(false); // Initially hide the text field
//
//        // When the user clicks the label, show the text field
//        label.setOnMouseClicked(event -> {
//            if (event.getClickCount() == 2) {
//                textField.setVisible(true);
//                textField.requestFocus();
//            }
//        });
//
//        // When the user presses Enter, save the new title and hide the text field
//        textField.setOnAction(event -> {
//            tab.setText(textField.getText());
//            label.setText(textField.getText());
//            textField.setVisible(false);
//        });
//
//        // When the text field loses focus, save the new title and hide the text field
//        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
//            if (!newValue) {
//                tab.setText(textField.getText());
//                label.setText(textField.getText());
//                textField.setVisible(false);
//            }
//        });
//
//        // Create a StackPane to hold the label and text field
//        StackPane stackPane = new StackPane();
//        stackPane.getChildren().addAll(label, textField);
//
//        // Set the StackPane as the tab's graphic
//        tab.setGraphic(stackPane);
//    }


    private void addTableTabs()  {
        List<Team> teams = null;
        try {
            teams = teamModel.getAllTeams(); //all the teams
            for (Team team: teams){ //for each team...
                Tab tab = new Tab(team.getName()); //create a new tab for that team
                tab.setClosable(false);
                tab.setContent(createTableForTeam(team)); //adds a table with the employees from team to the tab
                teamTabPane.getTabs().add(tab); //add that tab to TabPane
                //makeTabTitleEditable(tab); // make the tab title editable
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private TableView<Employee> createTableForTeam(Team team){
        //creating table and its columns and adding columns to table
        TableView<Employee> teamTblView = new TableView<>();

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
        utilCol.setText("util %");
        teamTblView.getColumns().add(utilCol);

        TableColumn<Employee, String> overHeadCol = new TableColumn<>();
        overHeadCol.setText("Overhead");
        teamTblView.getColumns().add(overHeadCol);

        TableColumn<Employee, String> rateCol = new TableColumn<>();
        rateCol.setText("Rates");
        teamTblView.getColumns().add(rateCol);

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

        formatSalaryColumn(salaryCol);
        formatSalaryColumn(annualCol);
        formatPercentageColumn(overHeadPerCol);
        formatPercentageColumn(utilCol);


        // Get the list of employees for the team
        ObservableList<Employee> employeesInTeam = employeeModel.getAllEmployeesFromTeam(team.getId());

        // Add a listener to the list
//        employeesInTeam.addListener(new ListChangeListener<Employee>() {
//            @Override
//            public void onChanged(Change<? extends Employee> change) {
//                // When the list changes, update the items of the table
//                teamTblView.setItems(employeesInTeam);
//            }
//        });

        teamTblView.setItems(employeesInTeam);

        return teamTblView;
    }

    private void formatPercentageColumn(TableColumn<Employee, BigDecimal> column){

        column.setCellFactory(tableColumn -> new TableCell<>() {
            @Override
            public void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty ? null : String.format("%.2f%%", value));
            }
        });
    }
    private void formatSalaryColumn(TableColumn<Employee, BigDecimal> column){
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
            Team newTeam = new Team(teamModel.getLastTeamId() + 1, "untitled team");
            teamModel.newTeam(newTeam);
            //put our newly created team into the hashmap/observable list for employees teamsCol
            teamNameToId.put(newTeam.getName(), newTeam.getId());
            allTeamNames.add(newTeam.getName());
            Tab tab = new Tab(newTeam.getName());
            tab.setClosable(false);
            tab.setContent(createTableForTeam(newTeam));
            teamTabPane.getTabs().add(tab);
            //makeTabTitleEditable(tab);

        } catch (BBExceptions e) {
            e.printStackTrace();
        }
    }

    private void setTeamRatesLabel(int teamId){
        double hourlyRate = teamModel.calculateTotalHourlyRate(teamId);
        double dailyRate = teamModel.calculateTotalDailyRate(teamId);
        if ("€".equals(currencySymbol)) {
            hourlyRate *= conversionRate;
            dailyRate *= conversionRate;
        }
        teamHourlyRateLbl.setText(currencySymbol +  String.format("%.2f", hourlyRate)+ "/Hour");
        teamDayRateLbl.setText(currencySymbol + String.format("%.2f", dailyRate) + "/Day");
    }

    public void calculateEmployeeRates() {
        Employee selectedEmployee = overviewEmployeeTblView.getSelectionModel().getSelectedItem();
        if(selectedEmployee != null){
            double hourlyRate = employeeModel.calculateHourlyRate(selectedEmployee);
            double dailyRate = employeeModel.calculateDailyRate(selectedEmployee);
            if ("€".equals(currencySymbol)) {
                hourlyRate *= conversionRate;
                dailyRate *= conversionRate;
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
            } catch (NumberFormatException e) {
                // If the new value is not a number, revert to 0
                markUpTxt.setText("0.00");
            }
        });
    }

    public void ratesListener() {
        // Listener to the overview table view to calculate the rates
        overviewEmployeeTblView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                calculateEmployeeRates();
            }
        });
    }



    //////////////////////////////////////////////////////////
    ///////////////Editing Employee Table/////////////////////
    //////////////////////////////////////////////////////////

    public void populateEmployeeTableView() {
        try {
            // Setup the TableView
            setupTableView();

            // Get the list of employees from the model
            ObservableList<Employee> employees = employeeModel.getEmployees();

            //Makes columns editable
            makeNameEditable();
            makeCountryEditable();
            makeTeamEditable();
            makeAnnualHoursEditable();
            //These methods format the tableview to have $ and commas as well as allows them to be editable
            formatAnnualSalaryCol();
            formatAnnualAmountCol();
            //These methods format the tableview to have % as well as allows them to be editable
            formatOverheadMultiPercent();
            formatUtilization();
            makeOverheadEditable();

            overviewEmployeeTblView.setItems(employees);
        } catch (BBExceptions e) {
            e.printStackTrace();
        }
    }

    public void setupTableView() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        overHeadMultiCol.setCellValueFactory(new PropertyValueFactory<>("overheadMultiPercent"));
        hoursCol.setCellValueFactory(new PropertyValueFactory<>("workingHours"));
        annualSalaryCol.setCellValueFactory(new PropertyValueFactory<>("annualSalary"));
        annualAmountCol.setCellValueFactory(new PropertyValueFactory<>("annualAmount"));
        utilCol.setCellValueFactory(new PropertyValueFactory<>("utilization"));
        overHeadMultiCol.setCellValueFactory(new PropertyValueFactory<>("overheadMultiPercent"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        overheadCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getIsOverheadCost()));
    }

    public void makeNameEditable() {
        // Make the cell able to become a textfield
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        // After editing, it sets the name in the database with .setOnEditCommit
        nameCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setName(event.getNewValue());
            try {
                employeeModel.updateEmployee(employee);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
    }

    public void makeAnnualHoursEditable() {
        // Make the cell able to become a textfield and we use IntegerStringConverter to convert it from a string to an Integer
        hoursCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        // After editing, it sets the name in the database with .setOnEditCommit
        hoursCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setWorkingHours(event.getNewValue());
            try {
                employeeModel.updateEmployee(employee);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
    }


    public void formatAnnualSalaryCol() {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);

        // Make the cell able to become a textfield
        annualSalaryCol.setCellFactory(tableColumn -> new TextFieldTableCell<>(new BigDecimalStringConverter()) {
            @Override
            public void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText("$" + format.format(item));
                }
            }
        });
        // After editing, it sets the annual salary in the database with .setOnEditCommit
        makeAnnualSalaryColEditable();
    }
    public void makeAnnualSalaryColEditable() {
        annualSalaryCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setAnnualSalary(event.getNewValue());
            try {
                employeeModel.updateEmployee(employee);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
    }


    public void formatAnnualAmountCol() {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);

        // Make the cell able to become a textfield
        annualAmountCol.setCellFactory(tableColumn -> new TextFieldTableCell<>(new BigDecimalStringConverter()) {
            @Override
            public void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText("$" + format.format(item));
                }
            }
        });
        // After editing, it sets the annual salary in the database with .setOnEditCommit
        makeAnnualAmountColEditable();
    }
    public void makeAnnualAmountColEditable() {
        // After editing, it sets the annual salary in the database with .setOnEditCommit
        annualAmountCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setAnnualAmount(event.getNewValue());
            try {
                employeeModel.updateEmployee(employee);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
    }


    public void formatUtilization() {

        utilCol.setCellFactory(tableColumn -> new TextFieldTableCell<>(new BigDecimalStringConverter()) {
            @Override
            public void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                //This checks if cell is empty, if not continues...
                //% is a placeholder for the value that will be inserted
                //.2 this tells our tableview we want 2 digits after the decimal
                //f indicates it's a floating point number (a number with a decimal)
                //% we add this to the end of the number
                setText(empty ? null : String.format("%.2f%%", value));
            }
        });
        makeutilizationEditable();
    }
    public void makeutilizationEditable(){
        utilCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setUtilization(event.getNewValue());
            try {
                employeeModel.updateEmployee(employee);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
    }


    public void formatOverheadMultiPercent() {

        overHeadMultiCol.setCellFactory(tableColumn -> new TextFieldTableCell<>(new BigDecimalStringConverter()) {
            @Override
            public void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                //This checks if cell is empty, if not continues...
                //% is a placeholder for the value that will be inserted
                //.2 this tells our tableview we want 2 digits after the decimal
                //f indicates it's a floating point number (a number with a decimal)
                //% we add this to the end of the number
                setText(empty ? null : String.format("%.2f%%", value));
            }
        });
        makeOverheadMultiPercentEditable();
    }
    public void makeOverheadMultiPercentEditable(){
        overHeadMultiCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setOverheadMultiPercent(event.getNewValue());
            try {
                employeeModel.updateEmployee(employee);
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
    }


    public void makeCountryEditable(){
        ObservableList<String> countries = FXCollections.observableArrayList();
        for (CountryCode code : CountryCode.values()) {
            countries.add(code.getName());
        }

        countryCol.setCellFactory(ComboBoxTableCell.forTableColumn(countries));

        countryCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setCountry(event.getNewValue());
            try {
                employeeModel.updateEmployee(employee);
            } catch (BBExceptions e){
                e.printStackTrace();
            }
        });
    }

    private void makeTeamEditable() throws BBExceptions {
        //We dont need the clear yet but maybe once we add a way to remove/rename teams
//        teamNameToId.clear();
//        allTeamNames.clear();
        //First we set up a hashmap so we can have a quick link between IDs and Names on the ComboBox
        //we use the getAllTeams method to populate this
        for (Team team : teamModel.getAllTeams()) {
            //when we use .put the first parameter is the "Key" so when we call .keySet it gives us team names
            teamNameToId.put(team.getName(), team.getId());
        }
        //Add No team to our list to be able to set things to Null
        //we use addFirst so it stays ontop after sort
        allTeamNames.addFirst("No Team");
        allTeamNames.addAll(teamNameToId.keySet());
        //now using our hashmap we make an observable list of the names by calling .keySet
        teamCol.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        teamCol.setCellFactory(ComboBoxTableCell.forTableColumn(allTeamNames));
        //Sorts things alphabetically
        FXCollections.sort(allTeamNames);




        teamCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            String newTeamName = event.getNewValue();
            //We then can get our new team Id by inputting the new team name into .get
            Integer newTeamId = teamNameToId.get(newTeamName);
            //Added an if statement to deal with no team and setting ID to Null
            if("No Team".equals(newTeamName)){
                employee.setTeamIdEmployee(null);
                employee.setTeamName("No Team");
            } else if (newTeamId != null) {
                employee.setTeamIdEmployee(newTeamId);
                //Because we extend team we are able to set the new team name easily
                employee.setTeamName(newTeamName);
            }
                try {
                    employeeModel.updateEmployee(employee);
                } catch (BBExceptions e){
                    e.printStackTrace();
                }
        });
    }

    public void makeOverheadEditable() {
        overheadCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getIsOverheadCost()));
        // Make the cell able to become a checkbox
        overheadCol.setCellFactory(tableColumn -> new CheckBoxTableCell<>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    CheckBox checkBox = (CheckBox) this.getGraphic();
                    checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                        if (isSelected != wasSelected) {
                            Employee employee = this.getTableView().getItems().get(this.getIndex());
                            employee.setIsOverheadCost(isSelected);
                            try {
                                employeeModel.updateEmployee(employee);
                            } catch (BBExceptions e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

}
