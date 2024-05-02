package GUI.controller.tabs;

import BE.Team;
import Exceptions.BBExceptions;
import GUI.model.TeamModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.List;

public class TeamTab {
    @FXML
    private ListView<Team> teamsLV;
    private TeamModel teamModel;


    public TeamTab(ListView<Team> teamsLv, TeamModel teamModel) {
        this.teamsLV = teamsLv;
        this.teamModel = teamModel;
    }

    public void initialize() {
        teamListView();
    }

    private void teamListView()  {
        try {
            // Fetch the list of teams
            List<Team> teams = teamModel.getAllTeams();

            // Convert the list of teams to an ObservableList
            ObservableList<Team> observableTeams = FXCollections.observableArrayList(teams);

            // Set the items of the ListView
            teamsLV.setItems(observableTeams);

            // Set the cell factory of the ListView
            teamsLV.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Team team, boolean empty) {
                    super.updateItem(team, empty);
                    if (empty || team == null) {
                        setText(null);
                    } else {
                        // Display the team name
                        setText(team.getName());
                    }
                }
            });
        } catch (BBExceptions e) {
            e.printStackTrace();
        }
    }
}