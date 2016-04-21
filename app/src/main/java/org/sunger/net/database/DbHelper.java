package org.sunger.net.database;


import android.content.ContentValues;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.sunger.net.exception.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuebp on 2016/1/26.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class DbHelper<T>  {
    public int create(T po){
        SQLiteHelperOrm db=new SQLiteHelperOrm();
        try{
            Dao dao=db.getDao(po.getClass());
            return dao.create(po);
        }catch (SQLException e){
            Logger.e(e);
        }finally {
            if (db!=null)
                db.close();
        }
        return -1;
    }
    public boolean exists(T po,Map<String,Object> where){
        SQLiteHelperOrm db=new SQLiteHelperOrm();
        try{
            Dao dao=db.getDao(po.getClass());
            if(dao.queryForFieldValues(where).size()>0){
                return  true;
            }
        }catch (SQLException e){
            Logger.e(e);
        }finally {
            if(db!=null)
                db.close();
        }
        return  false;
    }
    public int createIfNotExists(T po,Map<String,Object> where){
        SQLiteHelperOrm db=new SQLiteHelperOrm();
        try{
            Dao dao=db.getDao(po.getClass());
            if (dao.queryForFieldValues(where).size()<1){
                return dao.create(po);
            }
        }catch (SQLException e){
            Logger.e(e);
        }finally {
            if(db!=null)
                db.close();
        }
        return  -1;
    }
    public List<T>queryForEq(Class<T> c,String fileName,Object value){
        SQLiteHelperOrm db=new SQLiteHelperOrm();
        try{
            Dao dao=db.getDao(c);
            return dao.queryForEq(fileName,value);
        }catch (SQLException e){
            Logger.e(e);
        }finally {
            if(db!=null)
                db.close();
        }
        return  new ArrayList<T>();
    }
    public int remove(T po) {
        SQLiteHelperOrm db = new SQLiteHelperOrm();
        try {
            Dao dao = db.getDao(po.getClass());
            return dao.delete(po);
        } catch (SQLException e) {
            Logger.e(e);
        } finally {
            if (db != null)
                db.close();
        }
        return -1;
    }


    public int update(Class<T> c, ContentValues values, String columnName, Object value) {
        SQLiteHelperOrm db = new SQLiteHelperOrm();
        try {
            Dao dao = db.getDao(c);
            UpdateBuilder<T, Long> updateBuilder = dao.updateBuilder();
            updateBuilder.where().eq(columnName, value);
            for (String key : values.keySet()) {
                updateBuilder.updateColumnValue(key, values.get(key));
            }
            return updateBuilder.update();
        } catch (SQLException e) {
            Logger.e(e);
        } finally {
            if (db != null)
                db.close();
        }
        return -1;
    }

    public int update(T po) {
        SQLiteHelperOrm db = new SQLiteHelperOrm();
        try {

            Dao dao = db.getDao(po.getClass());
            return dao.update(po);
        } catch (SQLException e) {
            Logger.e(e);
        } finally {
            if (db != null)
                db.close();
        }
        return -1;
    }

    public List<T> queryForAll(Class<T> c) {
        SQLiteHelperOrm db = new SQLiteHelperOrm();
        try {
            Dao dao = db.getDao(c);
            return dao.queryForAll();
        } catch (SQLException e) {
            Logger.e(e);
        } finally {
            if (db != null)
                db.close();
        }
        return new ArrayList<T>();
    }
}

