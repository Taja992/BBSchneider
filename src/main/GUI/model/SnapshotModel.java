package GUI.model;

import BLL.SnapshotBLL;

public class SnapshotModel {

    private SnapshotBLL snapBLL = new SnapshotBLL();

    public void createSnapshotFile(String fileName){
        snapBLL.createSnapshotFile(fileName);
    }

}
