package GUI.controller;

import BE.Employee;
import BE.Team;
import Exceptions.BBExceptions;
import GUI.model.EmployeeModel;
import GUI.model.TeamModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.util.converter.BigDecimalStringConverter;
import java.math.BigDecimal;
import java.text.NumberFormat;



public class TeamTable {

    private final EmployeeModel employeeModel;
    private final TeamModel teamModel;
    private TabPane teamTabPane;
    private final Button addTeamBtn;
    private final OverviewEmployeeTable overviewEmployeeTable;

    public TeamTable(EmployeeModel employeeModel, TeamModel teamModel, TabPane teamTabPane, Button addTeamBtn, OverviewEmployeeTable overviewEmployeeTable) {
        this.employeeModel = employeeModel;
        this.teamModel = teamModel;
        this.teamTabPane = teamTabPane;
        this.overviewEmployeeTable = overviewEmployeeTable;
        this.addTeamBtn = addTeamBtn;

        addTeamBtn.setOnAction(this::addTeam);
    }

    public void initialize(){
        addTableTabs();
    }

    private void addTeam(ActionEvent event) {
        try {
            int generatedId = teamModel.getLastTeamId() + 1;
            Team newTeam = new Team(generatedId, "Team " + generatedId);
            teamModel.createNewTeam(newTeam);
            Tab tab = new Tab(newTeam.getName());
            tab.setUserData(newTeam); //So our new tab carries the team data
            tab.setClosable(false);
            tab.setContent(createTableForTeam(newTeam));
            teamTabPane.getTabs().add(tab);
            makeTeamTabTitleEditable(tab);

        } catch (BBExceptions e) {
            showAlert("Error", e.getMessage());
        }
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

        // Sort the tabs
        sortTeamTabs();
    }

    private void sortTeamTabs() {
        // Get all the tabs
        ObservableList<Tab> tabs = teamTabPane.getTabs();
        // This sorting method uses a comparison-based sorting algorithm, which typically has a time complexity of O(n log n),
        // where n is the number of teams.
        tabs.sort((tab1, tab2) -> {
            // Get team names
            String name1 = ((Team) tab1.getUserData()).getName();
            String name2 = ((Team) tab2.getUserData()).getName();

            try {
                // This assumes the team is formatted "Team #" and takes 5th digit "#" and uses Integer.parseInt to check its a number
                // This assumes the team name is formatted as "Team " followed by a number. It extracts the number part and converts it to an integer.
                int num1 = Integer.parseInt(name1.substring(5));
                int num2 = Integer.parseInt(name2.substring(5));
                // If successfully a number we compare the 2
                return Integer.compare(num1, num2);
            } catch (NumberFormatException e) {
                // If parsing fails, sort the teams alphabetically
                return name1.compareTo(name2);
            }
        });
    }

    private TableView<Employee> createTableForTeam(Team team){
        //creating table and its columns and adding columns to table
        TableView<Employee> teamTblView = new TableView<>();
        teamTblView.setUserData(team);
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

        TableColumn<Employee, Boolean> teamOverHeadCol = new TableColumn<>();
        teamOverHeadCol.setText("Overhead");
        teamTblView.getColumns().add(teamOverHeadCol);


        //setting the column values to their values in the database
        teamNameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        teamSalaryCol.setCellValueFactory(new PropertyValueFactory<>("AnnualSalary"));
        teamOverHeadPerCol.setCellValueFactory(new PropertyValueFactory<>("OverheadMultiPercent"));
        teamAnnualCol.setCellValueFactory(new PropertyValueFactory<>("AnnualAmount"));
        teamCountryCol.setCellValueFactory(new PropertyValueFactory<>("Country"));
        teamHoursCol.setCellValueFactory(new PropertyValueFactory<>("WorkingHours"));
        teamUtilCol.setCellValueFactory(new PropertyValueFactory<>("TeamUtil"));
        teamOverHeadCol.setCellValueFactory(new PropertyValueFactory<>("teamIsOverhead"));

        //formatting all the columns that need it, these methods have comments explaining them in OverviewEmployeeTable class
        formatSalaryColumnForTeams(teamSalaryCol);
        formatSalaryColumnForTeams(teamAnnualCol);
        formatPercentageColumnForTeams(teamOverHeadPerCol);
        formatPercentageColumnForTeams(teamUtilCol);
        formatUtilization(teamUtilCol);
        editUtilization(teamUtilCol, team);
        makeOverheadEditable(teamOverHeadCol, team);


        // Get the list of employees for the team
        ObservableList<Employee> employeesInTeam = employeeModel.getAllEmployeesFromTeam(team.getId());
        //enabling editing in table
        teamTblView.setEditable(true);
        teamTblView.setItems(employeesInTeam);

        dragAndDrop(teamTblView);
        contextMenu(teamTblView, team);

        return teamTblView;
    }

    private void contextMenu(TableView<Employee> teamTblView, Team team) {
        //creating context menu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Remove");
        deleteItem.setOnAction(event -> {
            Employee selectedEmployee = teamTblView.getSelectionModel().getSelectedItem();
            if (selectedEmployee != null) {
                try {
                    employeeModel.removeEmployeeFromTeam(selectedEmployee.getId(), team.getId());
                } catch (BBExceptions e) {
                    e.printStackTrace();
                }
            }
        });
        contextMenu.getItems().add(deleteItem);
        teamTblView.setContextMenu(contextMenu);
    }






    private void dragAndDrop(TableView<Employee> teamTblView) {
        teamTblView.setOnDragOver(event -> {
            //check to see if the dragged item has a string
            if (event.getDragboard().hasString()) {
                //accept transfer
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            //end event
            event.consume();
        });
        dragAndDropDropped(teamTblView);
    }


    private void dragAndDropDropped (TableView<Employee> teamTblView) {
        //When our dragged item is dropped, do this
        teamTblView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                //if it has our converted string, we switch it back to the index variable
                int draggedIdx = Integer.parseInt(db.getString());
                //this gets the employee from our overviewEmployeeTable using the list # index via .get(draggedIdx
                Employee draggedEmployee = overviewEmployeeTable.getTableView().getItems().get(draggedIdx);

                // Get the team associated with the TableView
                Team team = (Team) teamTblView.getUserData();

                // Add the employee to the team
                if (team != null) {
                    try {
                        employeeModel.addEmployeeToTeam(draggedEmployee, team);
                    } catch (BBExceptions e) {
                        throw new RuntimeException(e);
                    }
                }

                // Set the items of the TableView to the employees of the team
                teamTblView.setItems(employeeModel.getAllEmployeesFromTeam(team.getId()));
            }
            event.consume();
        });
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
            } catch (BBExceptions e) {
                showAlert("Error", e.getMessage());
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
                    showAlert("Error", e.getMessage());
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

    /////////////////////Format and Editing///////////////////////////

    public void makeOverheadEditable(TableColumn<Employee, Boolean> teamOverHeadCol, Team team) {
        teamOverHeadCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getIsTeamIsOverhead()));
        teamOverHeadCol.setCellFactory(column -> new TableCell<Employee, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(checkBox);
                    Employee employee = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(employee.getIsTeamIsOverhead());
                    //we use setOnAction with the checkbox to make it listen if there is a change
                    checkBox.setOnAction(e -> {
                        employee.setTeamIsOverhead(checkBox.isSelected());
                        try {
                            employeeModel.updateTeamIsOverheadForEmployee(team.getId(), employee.getId(), checkBox.isSelected());
                        } catch (BBExceptions ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    private void editUtilization(TableColumn<Employee, BigDecimal> teamUtilCol, Team team){
        //util column is editable
        teamUtilCol.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setTeamUtil(event.getNewValue());
            try {
                employeeModel.updateTeamUtilForEmployee(team.getId(), employee.getId(), event.getNewValue());
            } catch (BBExceptions e) {
                e.printStackTrace();
            }
        });
    }

    private void formatUtilization(TableColumn<Employee, BigDecimal> teamUtilCol){
        //formatting the utilization column to show the percentage
        teamUtilCol.setCellFactory(tableColumn -> new TextFieldTableCell<Employee, BigDecimal>(new BigDecimalStringConverter()) {
            @Override
            public void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty ? null : String.format("%.2f%%", value));
            }
        });
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
