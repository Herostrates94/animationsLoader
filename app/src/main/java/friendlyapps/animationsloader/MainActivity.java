package friendlyapps.animationsloader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import friendlyapps.animationsloader.api.entities.Picture;
import friendlyapps.animationsloader.api.entities.PicturesContainer;
import friendlyapps.animationsloader.api.managers.StorageAnimationsManager;
import friendlyapps.animationsloader.categorylist.ListAdapter;
import friendlyapps.animationsloader.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private List<PicturesContainer> storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        new MyAssetsManager(getAssets()).loadAnimations();

        checkExternalStorageAndDatabaseIntegrity();
        storage = getStorageStateFromDatabase(); // getting state from db to acquire ids of resources as well
        loadCategoriesToGUI();

    }

    private void checkExternalStorageAndDatabaseIntegrity() {

        List<PicturesContainer> externalStorage = StorageAnimationsManager.getInstance().getAllAnimationsFromStorage();
        List<PicturesContainer> databaseStorageState = getStorageStateFromDatabase();

        for (PicturesContainer storagePicturesContainer : externalStorage) {

            PicturesContainer databasePicturesContainer = getDatabasePicturesContainer(databaseStorageState, storagePicturesContainer);

            // create record for container if it does not exist
            if (databasePicturesContainer == null) {
                try {
                    databaseHelper.getPictureContainerDao().create(storagePicturesContainer);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // create records for pictures in the container if they are not exist
            for (Picture picture : storagePicturesContainer.getPicturesInCategory()) {
                if (! isPictureInDatabase(storagePicturesContainer, picture)) {
                    try {
                        databaseHelper.getPictureDao().create(picture);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    PicturesContainer getDatabasePicturesContainer(List<PicturesContainer> datatbaseStorageState, PicturesContainer picturesContainer){

        for(PicturesContainer picturesContainer1 : datatbaseStorageState){
            if(picturesContainer1.getCategoryName().equals(picturesContainer.getCategoryName())){
                return picturesContainer1;
            }
        }
        return null;
    }

    boolean isPictureInDatabase(PicturesContainer picturesContainer, Picture picture){

        for(Picture picture1 : picturesContainer.getPicturesInCategory()){
            if(picture1.getPath().equals(picture1.getPath())){
                return true;
            }
        }
        return false;
    }




    private void loadCategoriesToGUI(){

        ListView yourListView = (ListView) findViewById(R.id.itemListView);

        // get data from the table by the ListAdapter
        ListAdapter customAdapter = new ListAdapter(this, R.layout.itemlistrow, storage);

        yourListView.setAdapter(customAdapter);

    }

    public List<PicturesContainer> getStorageStateFromDatabase(){

        List<PicturesContainer> storage = new ArrayList<>();

        try {

            storage = databaseHelper.getPictureContainerDao().queryForAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return storage;

    }


}
