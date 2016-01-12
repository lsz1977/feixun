package com.cchat;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cchat.common.base.data.ChatMessage;
import com.cchat.db.ItemBean;
import com.cchat.db.SQLiteHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by holand on 15/12/6.
 */
public class CChatApplication extends Application{

    private SQLiteHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    private static String DB_NAME = "cchat.db";
    private static int DB_VERSION = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (db!=null){
            db.close();
            db = null;
        }

    }

    public void init(){
        try {
          /* 初始化并创建数据库 */
            dbHelper = new SQLiteHelper(this, DB_NAME, null, DB_VERSION);
          /* 创建表 */
            db = dbHelper.getWritableDatabase();    //调用SQLiteHelper.OnCreate()
          /* 查询表，得到cursor对象 */
            cursor = db.query(SQLiteHelper.TB_NAME, null, null, null, null, null, ItemBean.USER + " DESC");
            cursor.moveToFirst();
            while(!cursor.isAfterLast() && (cursor.getString(1) != null)){
                cursor.moveToNext();
            }

        } catch (IllegalArgumentException e){
            //当用SimpleCursorAdapter装载数据时，表ID列必须是_id，否则报错column '_id' does not exist
            e.printStackTrace();
            //当版本变更时会调用SQLiteHelper.onUpgrade()方法重建表 注：表以前数据将丢失
            ++ DB_VERSION;
            dbHelper.onUpgrade(db, --DB_VERSION, DB_VERSION);
//             dbHelper.updateColumn(db, SQLiteHelper.ID, "_"+SQLiteHelper.ID, "integer");
        }
    }

    /**
     * 此方法方便在那些没有context对象的类中使用
     * @return CChatApplication实例
     */
    public CChatApplication getApplicationInstance() {
        return this;
    }

    public void saveMessage(ChatMessage chatMessage, String account){

        //send
        String currentUser = "222222";//chatMessage.getFrom();

        ContentValues values = new ContentValues();
        values.put(ItemBean.USER, currentUser.trim());
        values.put(ItemBean.SENDTEXT, /*chatMessage.getDataTalk().*/"abc".trim());
        values.put(ItemBean.RECEIVETEXT, "");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        //curDate.toString();
        String currentTime = formatter.format(curDate);

        values.put(ItemBean.TIME, currentTime.trim());
        values.put(ItemBean.WITH, "111111");
        //插入数据 用ContentValues对象也即HashMap操作,并返回ID号
        Long cityID = db.insert(SQLiteHelper.TB_NAME, ItemBean.ID, values);

        if (false){ //receive
            /*ContentValues values = new ContentValues();
            values.put(ItemBean.USER, currentUser.trim());
            values.put(ItemBean.SENDTEXT, "");
            values.put(ItemBean.MESSAGETYPE, 0);
            values.put(ItemBean.RECEIVETEXT, messageDetail.Message.trim());

            SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy/MM/dd HH:mm");
            Date   curDate   =   new   Date(System.currentTimeMillis());//获取当前时间
            //curDate.toString();
            String   currentTime   =   formatter.format(curDate);

            values.put(ItemBean.TIME, currentTime.trim());
            values.put(ItemBean.WITH, withSb.trim());
            //插入数据 用ContentValues对象也即HashMap操作,并返回ID号
            Long cityID = db.insert(SQLiteHelper.TB_NAME, ItemBean.ID, values);*/
        }

        //file
        /*//发送文件  记录  入库
          ContentValues values = new ContentValues();

          values.put(ItemBean.USER, (firstLoginInfo.userName+firstLoginInfo.Domain).trim());
          values.put(ItemBean.SENDTEXT, fileNameAppend.trim());
          values.put(ItemBean.RECEIVETEXT, "");
          values.put(ItemBean.MESSAGETYPE, 1);


          SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy/MM/dd HH:mm");
          Date   curDate   =   new   Date(System.currentTimeMillis());//获取当前时间
          //curDate.toString();
          String   currentTime   =   formatter.format(curDate);

          values.put(ItemBean.TIME, currentTime.trim());

          values.put(ItemBean.WITH, currentChatWithSbInfo.userName.trim());
          //插入数据 用ContentValues对象也即HashMap操作,并返回ID号
          Long cityID = db.insert(SQLiteHelper.TB_NAME, ItemBean.ID, values);*/
    }

    private void sqliteQuery(String withSb) { //查询

       /* String currentUser = firstLoginInfo.userName+firstLoginInfo.Domain+"/"+firstLoginInfo.ResoureId;
        String sqlCity = ItemBean.USER + " = '" + currentUser + "'";
        String sqlWith = ItemBean.WITH + " = '" + withSb + "'";
        String sql = sqlCity + " and " + sqlWith;

        cursor = db.query(true, SQLiteHelper.TB_NAME,
                new String[]{ItemBean.ID, ItemBean.USER, ItemBean.SENDTEXT, ItemBean.RECEIVETEXT, ItemBean.TIME,
                        ItemBean.WITH, ItemBean.MESSAGETYPE, ItemBean.FILEPATH, ItemBean.READ, ItemBean.TASKID, ItemBean.TRANSMISSIONSTATE},
                sql,
                null, null, null, null, null);

        cursor.moveToFirst();
//        pRelationship.clear();
        int a = cursor.getCount();
        Log.i("", "a:" + "--"
                + cursor.getColumnCount());

        while(!cursor.isAfterLast() && (cursor.getString(1) != null)){

            if(cursor.getString(8)!=null&&cursor.getString(8).equals("1")){
                String id_Str = cursor.getString(0);
                int id=Integer.parseInt(id_Str);
                ItemInfo mItemInfo = new ItemInfo();
                mItemInfo.USER = cursor.getString(1);
                mItemInfo.SENDTEXT = cursor.getString(2);
                mItemInfo.RECEIVETEXT = cursor.getString(3);
                mItemInfo.TIME = cursor.getString(4);
                mItemInfo.WITH = cursor.getString(5);
                mItemInfo.MESSAGETYPE = cursor.getInt(6)+"";
                mItemInfo.FILEPATH = cursor.getString(7);
                mItemInfo.READ = "0";

                updateOneData(id,mItemInfo);

                Log.i("", "");
            }
            if(!cursor.getString(2).equals("")){
                ChatMsgEntity entity = new ChatMsgEntity();
                entity.setId(cursor.getString(0));
                entity.setDate(cursor.getString(4));
                entity.setName("me");

                Map<Long, Map<String, Integer>> pMap = new HashMap<Long, Map<String, Integer>>(); //Map<Integer, Map<String, Integer>>

                Map<String, Integer> pInfo= new HashMap<String, Integer>();
                String randomNumStr = getRandomNumChar(64);
                pInfo.put(randomNumStr, 0);
                pMap.put(pId, pInfo);
                pRelationship.put(pId, randomNumStr);
                entity.setProgressInfoMap(pMap);

                entity.setName("me");
                entity.setMsgType(false);
                entity.setText(cursor.getString(2));
                entity.setMessageType(cursor.getInt(6));
                entity.setFilePath(cursor.getString(7));
                entity.setRead(cursor.getString(8));
                entity.setTaskid(cursor.getString(9));
                if(cursor.getString(10)!=null&&cursor.getString(10).equals("true")){
                    entity.setTransmissionState(true);
                }else{
                    entity.setTransmissionState(false);
                }
                mDataArrays.add(entity);
                Log.i("YL", "mDataArrays-->"+mDataArrays.size());

            }else{
                ChatMsgEntity entity = new ChatMsgEntity();
                entity.setId(cursor.getString(0));
                entity.setDate(cursor.getString(4));
                entity.setName(cursor.getString(5));
                entity.setMsgType(true);
                entity.setText(cursor.getString(3));
                entity.setMessageType(cursor.getInt(6));
                entity.setFilePath(cursor.getString(7));
                entity.setRead(cursor.getString(8));
                entity.setTaskid(cursor.getString(9));
                if(cursor.getString(10)!=null&&cursor.getString(10).equals("true")){
                    entity.setTransmissionState(true);
                }else{
                    entity.setTransmissionState(false);
                }

                Map<Long, Map<String, Integer>> pMap = new HashMap<Long, Map<String, Integer    >>(); //Map<Integer, Map<String, Integer>>

                Map<String, Integer> pInfo= new HashMap<String, Integer>();
                String randomNumStr = getRandomNumChar(64);
                pInfo.put(randomNumStr, 0);
                pMap.put(pId, pInfo);
                pRelationship.put(pId, randomNumStr);
                entity.setProgressInfoMap(pMap);

                mDataArrays.add(entity);
                Log.i("YL", "mDataArrays-->"+mDataArrays.size());
            }
            pId++;
            cursor.moveToNext();
        }
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(mListView.getCount() - 1);*/

    }
}
