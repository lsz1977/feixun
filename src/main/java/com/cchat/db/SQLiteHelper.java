package com.cchat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
/**
 * 实现对表的创建、更新、变更列名操作
 *
 */
public class SQLiteHelper extends SQLiteOpenHelper {
	public static final String TB_NAME = "message";

	public SQLiteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	/**
	 * 创建新表
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " +
				TB_NAME + "(" +
				ItemBean.ID + " integer primary key," +
				ItemBean.USER + " varchar," +
				ItemBean.SENDTEXT + " varchar,"+
				ItemBean.RECEIVETEXT + " varchar,"+
				ItemBean.TIME + " varchar,"+
				ItemBean.WITH + " varchar,"+
				ItemBean.MESSAGETYPE + " integer " +
				")");

	}
	
	/**
	 * 当检测与前一次创建数据库版本不一样时，先删除表再创建新表
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
		onCreate(db);
	}
	
	/**
	 * 变更列名
	 * @param db
	 * @param oldColumn
	 * @param newColumn
	 * @param typeColumn
	 */
	public void updateColumn(SQLiteDatabase db, String oldColumn, String newColumn, String typeColumn){
		try{
			db.execSQL("ALTER TABLE " +
					TB_NAME + " CHANGE " +
					oldColumn + " "+ newColumn +
					" " + typeColumn
			);
		}catch(Exception e){
			e.printStackTrace();
		}
	}



}
