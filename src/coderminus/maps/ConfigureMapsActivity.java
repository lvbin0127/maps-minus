package coderminus.maps;

import android.app.ListActivity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;

public class ConfigureMapsActivity extends ListActivity
{
    private static final String TAG = "ConfigureMapsActivity";

    private static final String DATABASE_NAME = "configuration.db";
    private static final int    DATABASE_VERSION = 1;
    private static final String MAPS_TABLE_NAME = "maps";
    
    public static final class MapsColumns implements BaseColumns 
    {
        private MapsColumns() {}

        public static final String DEFAULT_SORT_ORDER = "modified DESC";
        public static final String TITLE              = "title";
        public static final String CACHE_PATH         = "cache_path";
		public static final String FUNCTION           = "function";
    }
    
    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            db.execSQL("CREATE TABLE " + MAPS_TABLE_NAME + " ("
                    + MapsColumns._ID + " INTEGER PRIMARY KEY,"
                    + MapsColumns.TITLE + " TEXT,"
                    + MapsColumns.CACHE_PATH + " TEXT,"
                    + MapsColumns.FUNCTION + " TEXT,"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

//    private DatabaseHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
//		dbHelper = new DatabaseHelper(this);
//		
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        
//        HashMap<String, String> mapsProjectionMap = new HashMap<String, String>();
//        mapsProjectionMap.put(MapsColumns._ID       , MapsColumns._ID);
//        mapsProjectionMap.put(MapsColumns.TITLE     , MapsColumns.TITLE);
//        mapsProjectionMap.put(MapsColumns.CACHE_PATH, MapsColumns.CACHE_PATH);
//
//        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
//        qb.setTables(MapsColumns.TITLE);
//        qb.setProjectionMap(mapsProjectionMap);
        
        //Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        
//        startManagingCursor(c);
//		this.setListAdapter(new SimpleCursorAdapter(
//				this, 
//				android.R.layout.simple_list_item_2, 
//				c, 
//				new String[] { Phones.NAME, Phones.NUMBER }, 
//                new int[] { android.R.id.text1, android.R.id.text2 }));
	}
}
