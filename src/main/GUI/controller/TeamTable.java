package GUI.controller;

import BE.Employee;
import BE.Team;
import Exceptions.BBExceptions;
import GUI.model.EmployeeModel;
import GUI.model.TeamModel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.util.converter.BigDecimalStringConverter;

import java.math.BigDecimal;
import java.text.NumberFormat;



public class TeamTable {

    private final EmployeeModel employeeModel;
    private final TeamModel teamModel;
    private TabPane teamTabPane;
    private final Button addTeamBtn;

    public TeamTable(EmployeeModel employeeModel, TeamModel teamModel, TabPane teamTabPane, Button addTeamBtn) {
        this.employeeModel = employeeModel;
        this.teamModel = teamModel;
        this.teamTabPane = teamTabPane;

        this.addTeamBtn = addTeamBtn;

        addTeamBtn.setOnAction(this::addTeam);
    }

    public void initialize(){
        addTableTabs();
    }

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
                //update combobox show new name
                //  makeTeamEditable();
            } catch (BBExceptions e) {
                showAlert("Error", e.getMessage());
            }
        });

//        // When the text field loses focus, save the new title, hide the text field, and update the team name in the database
//        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
//            if (!newValue) {
//                String newTeamName = textField.getText();
//                tab.setText(newTeamName);
//                label.setText(newTeamName);
//                textField.setVisible(false);
//                Team team = (Team) tab.getUserData();
//                try {
//                    teamModel.updateTeamName(team.getId(), newTeamName);
//                    //update combobox show new name
//                 //   makeTeamEditable();
//                } catch (BBExceptions e) {
//                    e.printStackTrace();
//                }
//            }
//        });

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

        TableColumn<Employee, String> teamNameCol = new TableColumn<>();
        teamNameCol.setText("Name");
        teamTblView.getColumns().add(teamNameCol);

        TableColumn<Employee, BigDecimal> teamSalaryCol = new TableColumn<>();
        teamSalaryCol.setText("Annual Salary");
        teamTblView.getColumns().add(teamSalaryCol);

        TableColumn<Employee, BigDecimal> teamOverHeadPerCol = new TableColumn<>();
        teamOverHeadPerCol.setText("Overhead %");
        teamTblView.getColumns().add(teamOverHeadPerCol);

        TableColumn<Employee, BigDecimal> teamAnnualCol = new TableColumn<>();
        teamAnnualCol.setText("Annual Amount");
        teamTblView.getColumns().add(teamAnnualCol);

        TableColumn<Employee, String> teamCountryCol = new TableColumn<>();
        teamCountryCol.setText("Country");
        teamTblView.getColumns().add(teamCountryCol);

        TableColumn<Employee, String> teamHoursCol = new TableColumn<>();
        teamHoursCol.setText("Annual Hrs");
        teamTblView.getColumns().add(teamHoursCol);

        TableColumn<Employee, BigDecimal> teamUtilCol = new TableColumn<>();
        teamUtilCol.setText(team.getName() + " Util %");
        teamTblView.getColumns().add(teamUtilCol);

        TableColumn<Employee, String> teamOverHeadCol = new TableColumn<>();
        teamOverHeadCol.setText("Overhead");
        teamTblView.getColumns().add(teamOverHeadCol);

//        TableColumn<Employee, String> rateCol = new TableColumn<>();
//        rateCol.setText("Rates");
//        teamTblView.getColumns().add(rateCol);

        //setting the column values to their values in the database
        teamNameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        teamSalaryCol.setCellValueFactory(new PropertyValueFactory<>("AnnualSalary"));
        teamOverHeadPerCol.setCellValueFactory(new PropertyValueFactory<>("OverheadMultiPercent"));
        teamAnnualCol.setCellValueFactory(new PropertyValueFactory<>("AnnualAmount"));
        teamCountryCol.setCellValueFactory(new PropertyValueFactory<>("Country"));
        teamHoursCol.setCellValueFactory(new PropertyValueFactory<>("WorkingHours"));
        teamUtilCol.setCellValueFactory(new PropertyValueFactory<>("Utilization"));
        teamOverHeadCol.setCellValueFactory(new PropertyValueFactory<>("isOverheadCost"));



        //formatting all the columns that need it, check the "make editable" methods for more comments

        formatSalaryColumnForTeams(teamSalaryCol);
        formatSalaryColumnForTeams(teamAnnualCol);
        formatPercentageColumnForTeams(teamOverHeadPerCol);
        formatPercentageColumnForTeams(teamUtilCol);

        //formatting the utilization column to show the percentage
        teamUtilCol.setCellFactory(tableColumn -> new TextFieldTableCell<Employee, BigDecimal>(new BigDecimalStringConverter()) {
            @Override
            public void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty ? null : String.format("%.2f%%", value));
            }
        });
        //enabling editing in table
        teamTblView.setEditable(true);
        //util column is editable
        teamUtilCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setUtilization(event.getNewValue());
            try {
                employeeModel.updateTeamUtilForEmployee(team.getId(), employee.getId(), event.getNewValue());
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });



        // Get the list of employees for the team
        ObservableList<Employee> employeesInTeam = employeeModel.getAllEmployeesFromTeamWithTeamUtil(team.getId());

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
            Team newTeam = new Team(generatedId, "Team " + generatedId);
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
