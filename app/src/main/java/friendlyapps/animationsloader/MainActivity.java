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
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

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


    }

    private void copyDirectoryFromAssetsToExternalStorage(String directoryName) throws IOException {

        createDirectoryInExternalStorageIfNecessary(directoryName);

        String[] assetsIWant = null;
        assetsIWant = getAssets().list(directoryName);

        for(String fileName : assetsIWant)
                copyFile(directoryName + java.io.File.separator + fileName);


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

            // Create new file to copy into.
            File file = new File(Environment.getExternalStorageDirectory() + java.io.File.separator +
                    storageAppMainDirectoryName + File.separator + destinationPath);
            file.createNewFile();
            copyFdToFile(assetFileDescriptor.getFileDescriptor(), file);

        } catch (IOException e) {
            Log.e("Files", destinationPath + " failed " + e.getMessage());
        }
    }

    private static void copyFdToFile(FileDescriptor src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
            Log.i("Files", "File " + dst + " was copied");
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }



}
