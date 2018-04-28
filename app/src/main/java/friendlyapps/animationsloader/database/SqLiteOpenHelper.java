package friendlyapps.animationsloader.database;

/**
 * Created by Ann on 26.09.2016.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class SqLiteOpenHelper extends SQLiteOpenHelper {

    private static SqLiteOpenHelper sInstance;

    private static final String DATABASE_NAME = "animationsManagement";

    private SQLiteDatabase db;

    public static synchronized SqLiteOpenHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new SqLiteOpenHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    private SqLiteOpenHelper(final Context context)
    {
        super(new DatabaseContext(context), DATABASE_NAME, null, 4);
        db = getWritableDatabase();



    }

    public void onCreate(SQLiteDatabase db)
    {
        this.db = db;

    }

    public void onOpen(SQLiteDatabase db){

        this.db = db;

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        this.db = db;

    }



}
