package com.superscholar.android.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.superscholar.android.entity.EventItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhqy on 2017/9/5.
 */

public class EventBaseManager {

    private static final String TABLE_EVENT_ITEM="eventitem";

    private static final String COL_ITEM_UUID="uuid";
    private static final String COL_ITEM_DATE="date";
    private static final String COL_ITEM_STIME="stime";
    private static final String COL_ITEM_ETIME="etime";
    private static final String COL_ITEM_TYPE="type";
    private static final String COL_ITEM_NAME="name";


    public class EventBaseHelper extends SQLiteOpenHelper {
        private static final int VERSION=1;
        private static final String DATABASE_NAME="Event.db";

        public EventBaseHelper(Context context){
            super(context,DATABASE_NAME,null,VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(
                    "CREATE TABLE " +
                            TABLE_EVENT_ITEM+
                            "("+
                            "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                            COL_ITEM_UUID+" TEXT,"+
                            COL_ITEM_DATE+" TEXT,"+

                            COL_ITEM_STIME+" TEXT,"+
                            COL_ITEM_ETIME+" TEXT," +

                            COL_ITEM_TYPE+" TEXT,"+
                            COL_ITEM_NAME+" TEXT"+
                            ")"
            );

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    public class EventItemCursorWrapper extends CursorWrapper {

        public EventItemCursorWrapper(Cursor cursor){
            super(cursor);
        }

        public EventItem getEventItem(){
            EventItem item=new EventItem();
            item.setUuid(UUID.fromString(getString(getColumnIndex(COL_ITEM_UUID))));
            item.setDate(Date.parseDate(getString(getColumnIndex(COL_ITEM_DATE))));
            Time startTime=Time.parseTime(getString(getColumnIndex(COL_ITEM_STIME)));
            Time endTime=Time.parseTime(getString(getColumnIndex(COL_ITEM_ETIME)));
            item.setStartTime(startTime);
            item.setEndTime(endTime);
            item.setLastedTime(startTime.getTimeDifference(endTime));
            item.setType(getString(getColumnIndex(COL_ITEM_TYPE)));
            item.setName(getString(getColumnIndex(COL_ITEM_NAME)));
            return item;
        }
    }

    private SQLiteDatabase database;

    public EventBaseManager(Context context){
        database=new EventBaseHelper(context).getWritableDatabase();
    }

    private ContentValues getGoodsContentValues(EventItem item) {
        ContentValues values=new ContentValues();
        values.put(COL_ITEM_UUID,item.getUuid().toString());
        values.put(COL_ITEM_DATE,item.getDate().toString());
        values.put(COL_ITEM_STIME,item.getStartTime().toString());
        values.put(COL_ITEM_ETIME,item.getEndTime().toString());
        values.put(COL_ITEM_TYPE,item.getType());
        values.put(COL_ITEM_NAME,item.getName());
        return values;
    }

    //获取某一天的事件记录列表
    public List<EventItem> getItemsByDate(String date){
        List<EventItem>items=new ArrayList<>();
        Cursor cursor=database.query(
                TABLE_EVENT_ITEM,
                null,
                COL_ITEM_DATE+" = ?",
                new String[]{date},
                null,
                null,
                null
        );
        EventItemCursorWrapper wrapper=new EventItemCursorWrapper(cursor);
        wrapper.moveToFirst();
        while(!wrapper.isAfterLast()){
            items.add(wrapper.getEventItem());
            wrapper.moveToNext();
        }
        wrapper.close();
        return items;
    }

    //向数据库添加一条商品数据
    public void insertItem(EventItem item) {
        ContentValues values=getGoodsContentValues(item);
        database.insert(TABLE_EVENT_ITEM,null,values);
    }

    //删除除了某一天外的事件记录
    public void deleteItemsExceptDate(String date){
        database.delete(TABLE_EVENT_ITEM,COL_ITEM_DATE+" != ?",new String[]{date});
    }
}
