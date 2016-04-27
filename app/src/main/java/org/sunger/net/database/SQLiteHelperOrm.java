package org.sunger.net.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.sunger.net.app.App;
import org.sunger.net.exception.Logger;
import org.sunger.net.entity.POMedia;

import java.sql.SQLException;

/**
 * Created by xuebp on 2016/1/26.
 */
public class SQLiteHelperOrm extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "oplayer.db";
    private static final int DATABASE_VERSION = 1;
    public SQLiteHelperOrm(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLiteHelperOrm(){
        super(App.getContext(),DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try{
            TableUtils.createTable(connectionSource, POMedia.class);
        }catch (SQLException e){
            Logger.e(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource, POMedia.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            Logger.e(e);
        }
    }
}
