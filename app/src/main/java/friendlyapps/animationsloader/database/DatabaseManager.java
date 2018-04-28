package friendlyapps.animationsloader.database;

import android.content.Context;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

import friendlyapps.animationsloader.api.entities.Picture;
import friendlyapps.animationsloader.api.entities.PicturesContainer;
import friendlyapps.animationsloader.api.managers.StorageAnimationsManager;

public class DatabaseManager {

    SqLiteOpenHelper sqLiteOpenHelper;


    public Dao<Picture, String> pictureDao;
    public Dao<PicturesContainer, Integer> picturesContainerDao;

    ConnectionSource connectionSource;



    public DatabaseManager(Context context){

        this.sqLiteOpenHelper = SqLiteOpenHelper.getInstance(context);
        connectionSource =
                new AndroidConnectionSource(sqLiteOpenHelper);

        //deleteTablesInDatabase();
        //createTablesInDatabase();
        clearTables();


        try {
            pictureDao = DaoManager.createDao(connectionSource, Picture.class);
            picturesContainerDao = DaoManager.createDao(connectionSource, PicturesContainer.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



    private void createTablesInDatabase(){

        try {
            TableUtils.createTableIfNotExists(connectionSource, PicturesContainer.class);
            TableUtils.createTableIfNotExists(connectionSource, Picture.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteTablesInDatabase(){

        try {
            TableUtils.dropTable(connectionSource, Picture.class, true);
            TableUtils.dropTable(connectionSource, PicturesContainer.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void clearTables(){

        try {
            TableUtils.clearTable(connectionSource, Picture.class);
            TableUtils.clearTable(connectionSource, PicturesContainer.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
