package GUI.controller;

import BE.Employee;
import BE.Team;
import Exceptions.BBExceptions;
import GUI.model.SnapshotModel;
import GUI.model.TeamModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class SnapShotTable {

    private final SnapshotModel snapshotModel;
    private final ComboBox<String> snapshotComboBox;
    private final HBox snapshotHBox;
    private final TeamTable teamTable;
    private Button createSnapshotBtn;

    public SnapShotTable(SnapshotModel snapshotModel, ComboBox<String> snapshotComboBox, HBox snapshotHBox, TeamTable teamTable, Button createSnapshotBtn) {
        this.snapshotModel = snapshotModel;
        this.snapshotComboBox = snapshotComboBox;
        this.snapshotHBox = snapshotHBox;
        this.teamTable = teamTable;
        this.createSnapshotBtn = createSnapshotBtn;

        createSnapshotBtn.setOnAction(this::CreateSnapshotFile);
    }

    public void initialize(){
        createTabsForSnapshots();
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
}
