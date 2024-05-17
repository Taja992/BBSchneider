package BLL;

import DAL.SnapshotDAO;

public class SnapshotBLL {

    private SnapshotDAO snapDAO = new SnapshotDAO();

    public void createSnapshotFile(String fileName){
        int copyNum = 2;
        String finalFileName = fileName;

        while(snapDAO.doesFileExist(finalFileName + ".db")){
            finalFileName = fileName + " (" + copyNum + ")";
            copyNum++;
        }

        snapDAO.createNewSnapshotFile(finalFileName);
    }

}
