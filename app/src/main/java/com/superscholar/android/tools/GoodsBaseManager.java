package com.superscholar.android.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.superscholar.android.entity.ConsumptionDetail;
import com.superscholar.android.entity.Goods;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhqy on 2017/6/30.
 */

public class GoodsBaseManager {

    public class GoodsBaseHelper extends SQLiteOpenHelper {
        private static final int VERSION=1;
        private static final String DATABASE_NAME="Goods.db";

        public GoodsBaseHelper(Context context){
            super(context,DATABASE_NAME,null,VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(
                    "CREATE TABLE " +
                            TABLE_GOODS_ITEM+
                            "("+
                            "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                            COL_GOODS_UUID+" TEXT,"+
                            COL_GOODS_NAME+" TEXT,"+
                            COL_GOODS_PRICE+" INTEGER,"+
                            COL_GOODS_DETAIL+" TEXT" +
                            ")"
            );

            sqLiteDatabase.execSQL(
                    "CREATE TABLE " +
                            TABLE_CONSUMPTION_DETAIL+
                            "("+
                            "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                            COL_CONSUMPTION_UUID+" TEXT,"+
                            COL_CONSUMPTION_DATE+" TEXT,"+
                            COL_CONSUMPTION_TIME+" TEXT,"+
                            COL_CONSUMPTION_NAME+" TEXT,"+
                            COL_CONSUMPTION_PRICE+" INTEGER" +
                            ")"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    public class GoodsCursorWrapper extends CursorWrapper {

        public GoodsCursorWrapper(Cursor cursor){
            super(cursor);
        }

        public Goods getGoods(){
            Goods goods=new Goods();
            String uuidString=getString(getColumnIndex(COL_GOODS_UUID));
            UUID uuid=UUID.fromString(uuidString);
            goods.setUuid(uuid);
            goods.setName(getString(getColumnIndex(COL_GOODS_NAME)));
            goods.setPrice(getInt(getColumnIndex(COL_GOODS_PRICE)));
            goods.setDetail(getString(getColumnIndex(COL_GOODS_DETAIL)));
            return goods;
        }
    }

    public class ConsumptionDetailCursorWrapper extends CursorWrapper {

        public ConsumptionDetailCursorWrapper(Cursor cursor){
            super(cursor);
        }

        public ConsumptionDetail getConsumptionDetail(){
            ConsumptionDetail consumptionDetail=new ConsumptionDetail();
            String uuidString=getString(getColumnIndex(COL_CONSUMPTION_UUID));
            UUID uuid=UUID.fromString(uuidString);
            consumptionDetail.setUuid(uuid);
            consumptionDetail.setDate(getString(getColumnIndex(COL_CONSUMPTION_DATE)));
            consumptionDetail.setTime(getString(getColumnIndex(COL_CONSUMPTION_TIME)));
            consumptionDetail.setName(getString(getColumnIndex(COL_CONSUMPTION_NAME)));
            consumptionDetail.setPrice(getInt(getColumnIndex(COL_CONSUMPTION_PRICE)));
            return consumptionDetail;
        }
    }

    private static final String TABLE_GOODS_ITEM="goodsitem";
    private static final String TABLE_CONSUMPTION_DETAIL="consumptiondetail";

    private static final String COL_GOODS_UUID="uuid";
    private static final String COL_GOODS_NAME="name";
    private static final String COL_GOODS_PRICE="price";
    private static final String COL_GOODS_DETAIL="detail";
    
    private static final String COL_CONSUMPTION_UUID="uuid";
    private static final String COL_CONSUMPTION_DATE="date";
    private static final String COL_CONSUMPTION_TIME="time";
    private static final String COL_CONSUMPTION_NAME="name";
    private static final String COL_CONSUMPTION_PRICE="price";
    

    private SQLiteDatabase database;

    public GoodsBaseManager(Context context){
        database=new GoodsBaseHelper(context).getWritableDatabase();
    }

    private ContentValues getGoodsContentValues(Goods goods) {
        ContentValues values=new ContentValues();
        values.put(COL_GOODS_UUID,goods.getUuid().toString());
        values.put(COL_GOODS_NAME,goods.getName());
        values.put(COL_GOODS_PRICE,goods.getPrice());
        values.put(COL_GOODS_DETAIL,goods.getDetail());
        return values;
    }

    private ContentValues getConsumptionDetailContentValues(ConsumptionDetail consumptionDetail){
        ContentValues values=new ContentValues();
        values.put(COL_CONSUMPTION_UUID,consumptionDetail.getUuid().toString());
        values.put(COL_CONSUMPTION_DATE,consumptionDetail.getDate());
        values.put(COL_CONSUMPTION_TIME,consumptionDetail.getTime());
        values.put(COL_CONSUMPTION_NAME,consumptionDetail.getName());
        values.put(COL_CONSUMPTION_PRICE,consumptionDetail.getPrice());
        return values;
    }

    //获取商品列表
    public List<Goods> getGoodsList(){
        Cursor cursor=database.query(
                TABLE_GOODS_ITEM,
                null,
                null,
                null,
                null,
                null,
                null
        );
        GoodsBaseManager.GoodsCursorWrapper wrapper=new GoodsBaseManager.GoodsCursorWrapper(cursor);
        List<Goods>goodsList=new ArrayList<>();
        try{
            wrapper.moveToFirst();
            while(!wrapper.isAfterLast()){
                goodsList.add(wrapper.getGoods());
                wrapper.moveToNext();
            }
        }finally{
            wrapper.close();
        }
        return goodsList;
    }

    //根据uuid获取商品
    public Goods getGoods(UUID uuid){
        Cursor cursor=database.query(
                TABLE_GOODS_ITEM,
                null,
                COL_GOODS_UUID+" = ?",
                new String[]{uuid.toString()},
                null,
                null,
                null
        );
        GoodsBaseManager.GoodsCursorWrapper wrapper=new GoodsBaseManager.GoodsCursorWrapper(cursor);
        Goods goods;
        try{
            wrapper.moveToFirst();
            goods=wrapper.getGoods();
        }finally{
            wrapper.close();
        }
        return goods;
    }

    //向数据库添加一条商品数据
    public void insertGoods(Goods goods) {
        ContentValues values=getGoodsContentValues(goods);
        database.insert(TABLE_GOODS_ITEM,null,values);
    }

    //更新单个商品数据
    public void updateGoods(Goods goods){
        ContentValues values=getGoodsContentValues(goods);
        String uuidString=goods.getUuid().toString();
        database.update(TABLE_GOODS_ITEM,values,COL_GOODS_UUID+" = ?",new String[]{uuidString});
    }

    //删除单个商品数据
    public void deleteGoods(Goods goods){
        String uuidString=goods.getUuid().toString();
        database.delete(TABLE_GOODS_ITEM,COL_GOODS_UUID+" = ?",new String[]{uuidString});
    }

    //获取消费明细列表
    public List<ConsumptionDetail>  getConsumptionDetailList(){
        Cursor cursor=database.query(
                TABLE_CONSUMPTION_DETAIL,
                null,
                null,
                null,
                null,
                null,
                null
        );
        GoodsBaseManager.ConsumptionDetailCursorWrapper wrapper=new GoodsBaseManager.ConsumptionDetailCursorWrapper(cursor);
        List<ConsumptionDetail>consumptionDetailList=new ArrayList<>();
        try{
            wrapper.moveToFirst();
            while(!wrapper.isAfterLast()){
                consumptionDetailList.add(wrapper.getConsumptionDetail());
                wrapper.moveToNext();
            }
        }finally{
            wrapper.close();
        }
        return consumptionDetailList;
    }

    //根据uuid获取消费明细
    public ConsumptionDetail getConsumptionDetail(UUID uuid){
        Cursor cursor=database.query(
                TABLE_GOODS_ITEM,
                null,
                COL_GOODS_UUID+" = ?",
                new String[]{uuid.toString()},
                null,
                null,
                null
        );
        GoodsBaseManager.ConsumptionDetailCursorWrapper wrapper=new GoodsBaseManager.ConsumptionDetailCursorWrapper(cursor);
        ConsumptionDetail consumptionDetail=null;
        try{
            wrapper.moveToFirst();
            consumptionDetail=wrapper.getConsumptionDetail();
        }finally{
            wrapper.close();
        }
        return consumptionDetail;
    }

    //向数据库添加一条消费明细数据
    public void insertConsumptionDetail(ConsumptionDetail consumptionDetail) {
        ContentValues values=getConsumptionDetailContentValues(consumptionDetail);
        database.insert(TABLE_CONSUMPTION_DETAIL,null,values);
    }

    //更新一个消费明细数据
    public void updateConsumptionDetail(ConsumptionDetail consumptionDetail){
        ContentValues values=getConsumptionDetailContentValues(consumptionDetail);
        String uuidString=consumptionDetail.getUuid().toString();
        database.update(TABLE_CONSUMPTION_DETAIL,values,COL_CONSUMPTION_UUID+" = ?",new String[]{uuidString});
    }

    //删除一个消费明细数据
    public void deleteConsumptionDetail(ConsumptionDetail consumptionDetail){
        String uuidString=consumptionDetail.getUuid().toString();
        database.delete(TABLE_CONSUMPTION_DETAIL,COL_CONSUMPTION_UUID+" = ?",new String[]{uuidString});
    }
}
