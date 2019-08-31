package archit.fastsms.DatabseManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class DBHandler extends SQLiteOpenHelper {

    String query;
    SQLiteDatabase database;
    Cursor c;

    // TODO add new user status

    private static final int DATABASE_Version = 1;
    private static final String DATABASE_NAME = "data.db";
    public static final String TABLE = "DATA";
    public static final String MESSAGE = "MESSAGE";
    public static final String PHONE = "PHONE";
    public static final String TYPE = "TYPE";

    public DBHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        query = "CREATE TABLE " + TABLE + " (" +
                TYPE + " INTEGER, " +
                MESSAGE + " TEXT, " +
                PHONE + " VARCHAR(13) );" ;
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE + " ;");
        onCreate(db);
    }

    public void save(Data data){
        //save the values in DATABASE
        ContentValues values = new ContentValues();
        values.put(TYPE, data.getType());
        values.put(PHONE, data.getPhone());
        values.put(MESSAGE, data.getMessage());
        database = getWritableDatabase();
        database.insert(TABLE, null, values);
        database.close();
    }

    public Data load()
    {
        Data data;
        database = getWritableDatabase();
        query = "SELECT * FROM " + TABLE + " ;";
        String message = "";
        String phone = "";
        String type = "";

        //CURSOR POINT TO A LOCATION IN RESULT
        c = database.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast())
        {
            if(c.getString(c.getColumnIndex(TYPE))!= null)
            {
                type = c.getString(c.getColumnIndex(TYPE));
                message = c.getString(c.getColumnIndex(MESSAGE));
                phone = c.getString(c.getColumnIndex(PHONE));
                c.moveToNext();
            }
            Log.i("data", type + " " + phone + " : " + message);
        }
        data = new Data(message, String.valueOf(phone), String.valueOf(type));
        database.close();
        return data;
    }
}
