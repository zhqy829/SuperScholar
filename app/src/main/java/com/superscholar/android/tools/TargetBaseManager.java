package com.superscholar.android.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.superscholar.android.entity.TargetItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by zhqy on 2017/6/19.
 */

public class TargetBaseManager {

    public class TargetBaseHelper extends SQLiteOpenHelper{
        private static final int VERSION=1;
        private static final String DATABASE_NAME="Target.db";

        public TargetBaseHelper(Context context){
            super(context,DATABASE_NAME,null,VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(
                    "CREATE TABLE " +
                            TABLE_NAME+
                            "("+
                            "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                            COL_UUID+" TEXT,"+
                            COL_NAME+" TEXT,"+
                            COL_TIME_PER_WEEK+" INTEGER,"+
                            COL_LASTED_WEEK+" INTEGER," +
                            COL_IS_CHECK+" INTEGER," +
                            COL_NEED_REMIND+" INTEGER," +
                            COL_REMIND_HOUR+" INTEGER," +
                            COL_REMIND_MIN+" INTEGER," +
                            COL_START_DATE+" TEXT," +
                            COL_SIGN_DATES+" TEXT," +
                            COL_CURRENCY_REWARD+" INTEGER"+
                            ")"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    public class TargetCursorWrapper extends CursorWrapper{

        public TargetCursorWrapper(Cursor cursor){
            super(cursor);
        }

        public TargetItem getTargetItem(){
            String uuidString=getString(getColumnIndex(COL_UUID));
            String name=getString(getColumnIndex(COL_NAME));
            int timesPerWeek=getInt(getColumnIndex(COL_TIME_PER_WEEK));
            int lastedWeek=getInt(getColumnIndex(COL_LASTED_WEEK));
            int isCheck=getInt(getColumnIndex(COL_IS_CHECK));
            int needRemind=getInt(getColumnIndex(COL_NEED_REMIND));
            int remindHour=getInt(getColumnIndex(COL_REMIND_HOUR));
            int remindMin=getInt(getColumnIndex(COL_REMIND_MIN));
            String startDateString=getString(getColumnIndex(COL_START_DATE));
            String signDatesString=getString(getColumnIndex(COL_SIGN_DATES));
            int currencyReward=getInt(getColumnIndex(COL_CURRENCY_REWARD));

            TargetItem targetItem=new TargetItem();
            targetItem.setUuid(UUID.fromString(uuidString));
            targetItem.setName(name);
            targetItem.setTimesPerWeek(timesPerWeek);
            targetItem.setLastedWeek(lastedWeek);
            targetItem.setCheck(isCheck!=0);
            targetItem.setNeedRemind(needRemind!=0);
            targetItem.setRemindTime(new BoundsTime(remindHour,remindMin));
            targetItem.setStartDate(Date.parseDate(startDateString));
            targetItem.setCurrencyReward(currencyReward);

            //打卡列表，打卡总天数
            StringTokenizer splitDates=new StringTokenizer(signDatesString,"*");
            List<Date>signdates=new ArrayList<>();
            int signdays=0;
            while (splitDates.hasMoreTokens()){
                signdates.add(Date.parseDate(splitDates.nextToken()));
                signdays++;
            }
            targetItem.setSignDays(signdays);
            targetItem.setSignDates(signdates);

            //是否失效，当前周数
            Date todayDate=new Date(Calendar.getInstance());
            Date endDate=(Date)todayDate.clone();//失效日期，该天已失效
            endDate.dayAdd(lastedWeek*7);
            if(!endDate.isLate(todayDate)){
                targetItem.setValid(false);
                targetItem.setCurrentWeek(lastedWeek);
            }else{
                int currentWeek=1;
                Date startDate=Date.parseDate(startDateString);
                startDate.dayAdd(7);
                while(!startDate.isLate(todayDate)){
                    currentWeek++;
                    startDate.dayAdd(7);
                }
                targetItem.setCurrentWeek(currentWeek);
            }

            //连续打卡天数
            int consecutiveSignDays=0;
            Date tp1=null,tp2=null;
            Iterator <Date> iterator=signdates.iterator();
            while(iterator.hasNext()){
                if(consecutiveSignDays==0){
                    tp1=(Date)iterator.next().clone();
                    consecutiveSignDays++;
                }else{
                    tp1.dayAddOne();
                    tp2=iterator.next();
                    if(tp1.isEquals(tp2)){
                        consecutiveSignDays++;
                    }else{
                        consecutiveSignDays=1;
                    }
                    tp1=(Date)tp2.clone();
                }
            }
            targetItem.setConsecutiveSignDays(consecutiveSignDays);

            //今天是否打卡
            if(signdates.size()!=0&&todayDate.isEquals(signdates.get(signdates.size()-1))){
                targetItem.setTodaySign(true);
            }

            //昨天是否打卡
            Date yesterdayDate=(Date)todayDate.clone();
            yesterdayDate.daySubtractOne();
            if(signdates.size()==1&&yesterdayDate.isEquals(signdates.get(signdates.size()-1))){
                targetItem.setYesterdaySign(true);
            }else if(signdates.size()>1&&(yesterdayDate.isEquals(signdates.get(signdates.size()-1))||
                    (signdates.size()>1&&yesterdayDate.isEquals(signdates.get(signdates.size()-2))))){
                targetItem.setYesterdaySign(true);
            }

            //各周打卡天数
            int []weekSignTimes=new int[lastedWeek];
            for(int i=0,j=0;i<lastedWeek;i++){
                todayDate.dayAdd(7);
                while(j<signdates.size()){
                    if(signdates.get(j).isEarly(todayDate)){
                        weekSignTimes[i]++;
                        j++;
                    }else{
                        break;
                    }
                }
            }
            targetItem.setWeekSignTimes(weekSignTimes);

            return targetItem;
        }
    }

    private static final String TABLE_NAME="targetitem";

    private static final String COL_UUID="uuid";
    private static final String COL_NAME="name";
    private static final String COL_TIME_PER_WEEK="timesPerWeek";
    private static final String COL_LASTED_WEEK="lastedWeek";
    private static final String COL_IS_CHECK="isCheck";
    private static final String COL_NEED_REMIND="needRemind";
    private static final String COL_REMIND_HOUR="remindHour";
    private static final String COL_REMIND_MIN="remindMin";
    private static final String COL_START_DATE="startDate";
    private static final String COL_SIGN_DATES="signDates";
    private static final String COL_CURRENCY_REWARD="currencyReward";

    private SQLiteDatabase database;

    public TargetBaseManager(Context context){
        database=new TargetBaseHelper(context).getWritableDatabase();
    }

    private ContentValues getContentValues(TargetItem targetItem){
        ContentValues values=new ContentValues();
        values.put(COL_UUID,targetItem.getUuid().toString());
        values.put(COL_NAME,targetItem.getName());
        values.put(COL_TIME_PER_WEEK,targetItem.getTimesPerWeek());
        values.put(COL_LASTED_WEEK,targetItem.getLastedWeek());
        values.put(COL_IS_CHECK,targetItem.isCheck()?1:0);
        values.put(COL_NEED_REMIND,targetItem.isNeedRemind()?1:0);
        values.put(COL_REMIND_HOUR,targetItem.getRemindTime().getHour());
        values.put(COL_REMIND_MIN,targetItem.getRemindTime().getMin());
        values.put(COL_START_DATE,targetItem.getStartDate().toString());
        values.put(COL_CURRENCY_REWARD,targetItem.getCurrencyReward());
        String signsDateStr="";
        List<Date>signDates=targetItem.getSignDates();
        Iterator<Date>iterator=signDates.iterator();
        while(iterator.hasNext()){
            signsDateStr+=iterator.next().toString();
            if(iterator.hasNext()){
                signsDateStr+="*";
            }
        }
        values.put(COL_SIGN_DATES,signsDateStr);
        return  values;
    }

    //SELECT 获取目标列表
    public List<TargetItem> getTargetItemList(){
        Cursor cursor=database.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        TargetCursorWrapper wrapper=new TargetCursorWrapper(cursor);
        List<TargetItem>targetItemList=new ArrayList<>();
        try{
            wrapper.moveToFirst();
            while(!wrapper.isAfterLast()){
                targetItemList.add(wrapper.getTargetItem());
                wrapper.moveToNext();
            }
        }finally{
            wrapper.close();
        }
        return targetItemList;
    }

    //SELECT 根据uuid获取目标
    public TargetItem getTargetItem(UUID uuid){
        Cursor cursor=database.query(
                TABLE_NAME,
                null,
                COL_UUID+" = ?",
                new String[]{uuid.toString()},
                null,
                null,
                null
        );
        TargetCursorWrapper wrapper=new TargetCursorWrapper(cursor);
        TargetItem targetItem;
        try{
            wrapper.moveToFirst();
            targetItem=wrapper.getTargetItem();
        }finally{
            wrapper.close();
        }
        return targetItem;
    }

    //INSERT 向数据库添加数据
    public void insertItem(TargetItem targetItem) {
        ContentValues values=getContentValues(targetItem);
        database.insert(TABLE_NAME,null,values);
    }

    //UPDATE 更新数据
    public void updateItem(TargetItem targetItem){
        ContentValues values=getContentValues(targetItem);
        String uuidString=targetItem.getUuid().toString();
        database.update(TABLE_NAME,values,COL_UUID+" = ?",new String[]{uuidString});
    }

    //DELETE 删除数据
    public void deleteItem(TargetItem targetItem){
        String uuidString=targetItem.getUuid().toString();
        database.delete(TABLE_NAME,COL_UUID+" = ?",new String[]{uuidString});
    }

}
