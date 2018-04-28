package friendlyapps.animationsloader;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import friendlyapps.animationsloader.api.entities.PicturesContainer;
import friendlyapps.animationsloader.api.managers.StorageAnimationsManager;
import friendlyapps.animationsloader.database.DatabaseManager;

public class MainActivity extends AppCompatActivity {

    private final String storageAppMainDirectoryName = "happyApplicationsAnimations";
    private final String picturesDirectoryName = "pictures";
    private final String backgroundDirectoryName = "background";
    private final String animationMovementsDirectoryName = "animationMovements";

    private File storageMainDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void loadAnimations(View v){

        prepareAppDirectoryInExternalStorage();

        try {
            copyDirectoryFromAssetsToExternalStorage(picturesDirectoryName);
            copyDirectoryFromAssetsToExternalStorage(animationMovementsDirectoryName);
            copyDirectoryFromAssetsToExternalStorage(backgroundDirectoryName);
            //colors?
        } catch (IOException e) {
            e.printStackTrace();
        }


        TextView messageTextView = findViewById(R.id.textView);
        messageTextView.setText("Animations loaded. You can uninstall this app now.");


        saveStorageStateToDatabase();
        getStorageStateFromDatabase();


    }

    private void copyDirectoryFromAssetsToExternalStorage(String directoryName) throws IOException {

        createDirectoryInExternalStorageIfNecessary(directoryName);

        String[] assetsIWant;
        assetsIWant = getAssets().list(directoryName);

        for(String fileName : assetsIWant) {

            //if fileName cointains a dot it means it is a file, not directory

            if(fileName.contains(".")){
                copyFile(directoryName + java.io.File.separator + fileName);
            }
            else{
                copyDirectoryFromAssetsToExternalStorage(directoryName +
                        java.io.File.separator + fileName);
            }
        }

        //getAssets().open("pictures/butterfly_red.png");
    }

    private void createDirectoryInExternalStorageIfNecessary(String directoryName){

        File newDirectory = new File(storageMainDirectory, directoryName);

        if (!newDirectory.exists()) {
            newDirectory.mkdir();
            Log.i("Files", directoryName + " directory was created");
        }
    }


    private void prepareAppDirectoryInExternalStorage(){

        storageMainDirectory = new File(Environment.getExternalStorageDirectory(), storageAppMainDirectoryName);

        if (!storageMainDirectory.exists()) {
            storageMainDirectory.mkdir();
            Log.i("Files", storageAppMainDirectoryName + " directory was created");
        }
    }

    private void copyFile(String destinationPath) throws IOException {

        AssetManager assetManager = getAssets();
        AssetFileDescriptor assetFileDescriptor;
        try {
            assetFileDescriptor = assetManager.openFd(destinationPath);

            InputStream in = assetFileDescriptor.createInputStream();
            FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() +
                    java.io.File.separator + storageAppMainDirectoryName + File.separator + destinationPath);
            byte[] buff = new byte[1024];
            int read = 0;

            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }

        } catch (IOException e) {
            Log.e("Files", destinationPath + " failed " + e.getMessage());
        }
    }


    public void saveStorageStateToDatabase(){
        int rows;
        List<PicturesContainer> storage = StorageAnimationsManager.getInstance().getAllAnimationsFromStorage();

        try {
            rows = new DatabaseManager(this).picturesContainerDao.create(storage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<PicturesContainer> getStorageStateFromDatabase(){

        List<PicturesContainer> storage = new ArrayList<>();

        try {
            storage =
                    new DatabaseManager(this).picturesContainerDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return storage;

    }




}
