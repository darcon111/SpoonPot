package app.com.spoonpot.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import app.com.spoonpot.R;
import app.com.spoonpot.config.Constants;


public class DataBase {

    private static final String DATABASE_NAME = "spoonpot";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = DataBase.class.getName();

    public static String TABLE_MSG = "tb_message";

    private Context context;
    private OpenHelper openHelper;
    private android.database.sqlite.SQLiteDatabase db;

    public DataBase(Context context) {
        this.context = context;
        openHelper = new OpenHelper(this.context);
        this.db = openHelper.getWritableDatabase();
    }

    public void open() {
        if (!db.isOpen()) {
            this.db = openHelper.getWritableDatabase();
        }
    }

    public long insert(String tableName, ContentValues pairValues) {
        long rowId = db.insertOrThrow(tableName, null, pairValues);

            Log.i(TAG, "Insertando registro en la tabla " + tableName
                    + ", id = " + rowId);

        return rowId;
    }

    public void insertString(String tableName, String query) {
        if (Constants.DEVELOPER_MODE) {
            Log.i(TAG, "Insertando registro en la tabla " + tableName);
        }
        db.execSQL(query);
    }

    public void close() {
        db.close();
        openHelper.close();
    }

    public void update(String tableName, ContentValues pairValues, String where) {
        db.update(tableName, pairValues, where, null);
        if (Constants.DEVELOPER_MODE) {
            Log.i(TAG, "Actualizando registro en la tabla " + tableName + " , condicion" + where);
        }

    }

    public long updateAll(String tableName, ContentValues pairValues) {
        if (Constants.DEVELOPER_MODE) {
            Log.i(TAG, "Actualizando registro en la tabla " + tableName);
        }
        return db.update(tableName, pairValues, null, null);
    }

    public void delete(String tableName, String whereClause, String[] whereArgs) {
        db.delete(tableName, whereClause, whereArgs);
        if (Constants.DEVELOPER_MODE) {
            Log.i(TAG, "Eliminando registros en la tabla " + tableName + " " + whereClause);
        }
    }

    public Cursor rawQuery(String query, String[] selectionArgs) {
        Cursor test = db.rawQuery(query, selectionArgs);
        return test;

    }

    public Cursor query(String tableName, String[] columnsName,
                        String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy) {

        return db.query(tableName, columnsName, selection, selectionArgs,
                groupBy, having, orderBy);
    }

    private static class OpenHelper extends android.database.sqlite.SQLiteOpenHelper {

        private Context context;

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(android.database.sqlite.SQLiteDatabase db) {
            String s;
            try {

                InputStream in = context.getResources().openRawResource(
                        R.raw.sql);
                DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                Document doc = builder.parse(in, null);
                NodeList statements = doc.getElementsByTagName("statement");
                for (int i = 0; i < statements.getLength(); i++) {
                    s = statements.item(i).getChildNodes().item(0)
                            .getNodeValue();
                    db.execSQL(s);
                }
            } catch (Throwable t) {
                if (Constants.DEVELOPER_MODE) {
                    Log.e(TAG, t.toString());
                }
            }
        }

        @Override
        public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
            onCreate(db);
        }

    }
}
