package com.rabbithouse.chino;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * iBeacon��ݒu���邨�X�ƃ��[�U�́A�X�܃f�[�^�̂������܂Ƃ߂��R�l�N�^
 * @author yoship
 */
public class StoreDataConnector
{

	/**
	 * �ȒP�ȓX�܏����擾����
	 * @param uuid �X�܂ɐݒu���Ă���iBeacon��UUID
	 * @return �X�e�[�^�X�ʒm�p�̓X�܏��
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 */
	public static StoreInfo getStoreInfo(String uuid) throws ClientProtocolException, IOException, JSONException
	{
		// UUID��URI�ɖ��ߍ���
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/");
		uri.append("findStoreInfo");
		uri.append("/");
		uri.append(encryptString(uuid));
		
		// JSON�f�[�^���󂯎��
		JSONObject jsonRoot = getJsonObjectByHTTPRequest(uri.toString());
		
		// JSON�I�u�W�F�N�g����X�܏��𒊏o����
		JSONObject obj = jsonRoot.getJSONArray("data").getJSONObject(0);
		StoreInfo info = new StoreInfo();
		info.Name = obj.getString("storeName");
		info.Category = obj.getString("categoryName");
		info.SalesText = obj.getString("salesText");
		info.UUID = uuid;
		
		return info;
		/*
		StoreInfo info = new StoreInfo();
		info.Name = "�������X";
		info.Category = "�z�r�[";
		info.SalesText = "���Ȃ�Ȃ�ƑS����10�������I�I�I";
		info.UUID = "11111";
		return info;*/
	}
	
	/**
	 * �ڍׂȓX�܏����擾����
	 * @param uuid �X�܂ɐݒu���Ă���iBeacon��UUID
	 * @return �X�e�[�^�X�ʒm�p�̓X�܏��
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 */
	public static StoreDetail getStoreDetail(String uuid) throws ClientProtocolException, IOException, JSONException
	{
		
		// UUID��URI�ɖ��ߍ���
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/");
		uri.append("findStoreDetail");
		uri.append("/");
		uri.append(encryptString(uuid));
		
		// JSON�f�[�^���󂯎��
		JSONObject jsonRoot = getJsonObjectByHTTPRequest(uri.toString());
		
		// JSON�I�u�W�F�N�g����X�܏��𒊏o����
		JSONObject obj = jsonRoot.getJSONArray("data").getJSONObject(0);
		StoreDetail detail = new StoreDetail();
		detail.Name = obj.getString("storeName");
		detail.Category = obj.getString("categoryName");
		detail.SalesText = obj.getString("salesText");
		detail.Detail = obj.getString("detailText");
		detail.URL = obj.getString("url");
		detail.UpdateDate = obj.getString("updatedAt");
		detail.UUID = uuid;
		
		return detail;
		
		/*
		StoreDetail detail = new StoreDetail();
		detail.Name = "�������X";
		detail.Category = "�z�r�[";
		detail.SalesText = "���Ȃ�Ȃ�ƑS����10�������I�I�I";
		detail.Detail = "<!DOCTYPE html><html><head></head><body>"
				+ "<p>�V�����I�V�����I�V�����I�V���������������킟����������������������������������������������I�I�I</p>"
				+ "<p>�������������c�����c���������[�I�����������������I�I�I�V�����V�����V�������������킟���������I�I�I</p>"
+ "<p>�����N���J�N���J�I�N���J�N���J�I�X�[�n�[�X�[�n�[�I�X�[�n�[�X�[�n�[�I�����������Ȃ��c���񂭂�</p>"
+ "<p>��͂����I�V�����E�t�����\���[�Y����̓��F�u�����h�̔����N���J�N���J���������I�N���J�N���J�I�������I�I</p>"
+ "<p>�ԈႦ���I���t���t���������I���t���t�I���t���t�I�������t���t�I�J���J�����t���t�c����񂫂�񂫂ア�I�I</p>"
+ "<p>12�b�̃V�������񂩂킢�������患�I�I�����������c�������c�����������������I�I�ӂ��������������I�I</p>"
+ "<p>�A�j��2����������ėǂ������˃V��������I���������������I���킢���I�V��������I���킢���I���������������I</p>"
+ "<p>�R�~�b�N2������������Ċ����c���₟�������������I�I�I�ɂႠ����������������I�I���Ⴀ���������������I�I</p>"
+ "<p>�������������������������I�I�I�R�~�b�N�Ȃ�Č�������Ȃ��I�I�I�I���c�������A�j�����悭�l������c</p>"
+ "<p>�V�@���@���@�� �� �� �� ���� �� �� �� ���H�ɂႠ��������������������������I�I�������������������������I�I</p>"
+ "<p>����Ȃ��������������I�I���₟�����������������������I�I�͂���������������I�I</p>"
+ "<p>���́I��������[�I��߂Ă��I�I�����Ȃ񂩂�߁c�āc���I�H���c�Ă�H�\���G�̃V��������񂪖l�����Ă�H</p>"
+ "<p>�\���G�̃V��������񂪖l�����Ă邼�I�V��������񂪖l�����Ă邼�I�}�G�̃V��������񂪖l�����Ă邼�I�I</p>"
+ "<p>�A�j���̃V��������񂪖l�ɘb�������Ă邼�I�I�I�悩�����c���̒��܂��܂��̂Ă���������Ȃ��񂾂˂��I</p>"
+ "<p>������ق����������������I�I�I�l�ɂ̓V��������񂪂���I�I</p>"
				+ "</body></html>";
		detail.URL = "https://www.google.co.jp/";
		detail.UpdateDate = "2014-08-28 12:00:00";
		detail.UUID = "11111";
		
		return detail;*/
	}
	
	
	
	/**
	 * HTTP���N�G�X�g����X�܏���JSON�f�[�^���󂯎��
	 * @param uri HTTP���N�G�X�g�p��URI
	 * @return �X�܏���JSON�f�[�^
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	private static JSONObject getJsonObjectByHTTPRequest(String uri) throws ClientProtocolException, IOException, JSONException
	{
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(uri.toString());
		
		// HTTP���N�G�X�g�𑗂�A���X�|���X���󂯎��
		HttpResponse httpResponse = httpClient.execute(request);
		
		// ���ʂ��F�����Ȃ��ꍇ��IOException
		int status = httpResponse.getStatusLine().getStatusCode(); 
		if (HttpStatus.SC_OK != status) throw new IOException();
		
		// �f�[�^��JSON�p�ɕϊ�
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        httpResponse.getEntity().writeTo(outputStream);
        String data = outputStream.toString();
        
        Log.e("aaaaa", data);

        // JSON�I�u�W�F�N�g��Ԃ�
		return new JSONObject(data);
		
		
		
	}
	
	/**
	 * �T�[�o�Ƀ��[�U���X�̋߂��ɂ��邱�Ƃ�ʒB����
	 * @param uuid �X�܂ɐݒu���Ă���iBeacon��UUID
	 * @param userID ���[�U�ŗL��ID
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static void notifyNearStoreUser(String uuid, int userID) throws ClientProtocolException, IOException
	{
		
		// UUID�ƃ��[�U�ŗLID��URI�ɖ��ߍ���
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/");
		uri.append("notifyActiveCustomer");
		uri.append("/");
		uri.append(encryptString(uuid));
		uri.append("/");
		uri.append(userID);
		
		final String strUri = uri.toString();
		
		// HTTP���N�G�X�g�𑗂�
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try {
					sendHTTPRequest(strUri);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		thread.start();
	}
	
	/**
	 * UUID����X�܂ɂ���Chino���[�U�̐l���𓾂�
	 * @param uuid �X�܂ɐݒu���Ă���iBeacon��UUID
	 * @return �X�܂ɂ���Chino���[�U�̐l��
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	public static int getActiveCustomerCount(String uuid) throws ClientProtocolException, IOException, JSONException
	{
		
		// UUID�ƃ��[�U�ŗLID��URI�ɖ��ߍ���
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/");
		uri.append("getActiveCustomerCount");
		uri.append("/");
		uri.append(encryptString(uuid));
		
		// HTTP���N�G�X�g�𑗂�AJSON�I�u�W�F�N�g�𓾂�
		JSONObject jsonRoot = getJsonObjectByHTTPRequest(uri.toString());
		
		// JSON�I�u�W�F�N�g����X�܂ɂ���Chino���[�U�̐��𓾂�
		JSONObject obj = jsonRoot.getJSONArray("data").getJSONObject(0);
		int count = obj.getInt("activeCustomerCount");
		
		return count;
		
		//return 10;
	}
	
	/**
	 * �w���URI��HTTP���N�G�X�g�𑗂�
	 * @param uri HTTP���N�G�X�g�𑗂�URI
	 * @return ���N�G�X�g����
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static HttpResponse sendHTTPRequest(String uri) throws ClientProtocolException, IOException
	{
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(uri.toString());
		
		// HTTP���N�G�X�g�𑗂�A���X�|���X���󂯎��
		return httpClient.execute(request);
	}
	
	/**
	 * �n�b�V����
	 * @param text �n�b�V�������镶����
	 * @return
	 */
	private static String encryptString(String text)
	{
	    // �ϐ�������
	    MessageDigest md = null;
	    StringBuffer buffer = new StringBuffer();
	 
	    try {
	        // ���b�Z�[�W�_�C�W�F�X�g�C���X�^���X�擾
	        md = MessageDigest.getInstance("SHA-256");
	    } catch (NoSuchAlgorithmException e) {
	        // ��O�������A�G���[���b�Z�[�W�o��
	        System.out.println("�w�肳�ꂽ�Í����A���S���Y��������܂���");
	    }
	 
	    // ���b�Z�[�W�_�C�W�F�X�g�X�V
	    md.update(text.getBytes());
	 
	    // �n�b�V���l���i�[
	    byte[] valueArray = md.digest();
	 
	    // �n�b�V���l�̔z������[�v
	    for(int i = 0; i < valueArray.length; i++){
	        // �l�̕����𔽓]�����A16�i���ɕϊ�
	        String tmpStr = Integer.toHexString(valueArray[i] & 0xff);
	 
	        if(tmpStr.length() == 1){
	            // �l���ꌅ�������ꍇ�A�擪��0��ǉ����A�o�b�t�@�ɒǉ�
	            buffer.append('0').append(tmpStr);
	        } else {
	            // ���̑��̏ꍇ�A�o�b�t�@�ɒǉ�
	            buffer.append(tmpStr);
	        }
	    }
	 
	    // ���������n�b�V���v�Z�l��ԋp
	    return buffer.toString();
	}
	
	/**
	 * �X�܏����L������
	 * @param context �f�[�^�x�[�X�ɐڑ����邽�߂̃R���e�L�X�g
	 * @param info �L������X�܏��
	 */
	public static void saveStoreInfo(Context context, StoreInfo info)
	{
		Log.i("save", "�X�܏������[�J���f�[�^�x�[�X�ɕۑ������I");
		// SQLite���g�p���鏀��
		MySQLiteOpenHelper sql = new MySQLiteOpenHelper(context);
		SQLiteDatabase db = sql.getWritableDatabase();
		
		// �w���UUID�����݂��邩������
		Cursor c = db.rawQuery("select uuid from table_chino where uuid=?;", new String[]{ info.UUID });
		
		// �ۑ�����f�[�^���쐬
		ContentValues values = new ContentValues();
		values.put("uuid", info.UUID);
		values.put("name", info.Name);
		values.put("category", info.Category);
		values.put("sales", info.SalesText);
		
		// �f�[�^�����݂��Ă��Ȃ��ꍇ�͑}��
		if(c.getCount() == 0)
		{
			db.insert("table_chino", null, values);
		}
		db.close();
		sql.close();
	}
	
	/**
	 * �L�����ꂽ�X�܏������ׂēǂݍ���
	 * @param context �f�[�^�x�[�X�ɐڑ����邽�߂̃R���e�L�X�g
	 * @return �X�܏��̔z��
	 */
	public static StoreInfo[] loadStoreInfos(Context context)
	{
		Log.i("load", "���X�̏���ǂݍ��݂܂����I");
		
		// SQLite���g�p���鏀��
		MySQLiteOpenHelper sql = new MySQLiteOpenHelper(context);
		SQLiteDatabase db = sql.getWritableDatabase();
		
		// ���ׂẴf�[�^����������N�G��
		Cursor c = db.rawQuery("select * from table_chino;", null);
		c.moveToFirst();
		
		// �f�[�^��1�������o��
		StoreInfo[] infos = new StoreInfo[c.getCount()];
		for(int i = 0; i < infos.length; i++)
		{
			int uuidIndex = c.getColumnIndex("uuid");
			int nameIndex = c.getColumnIndex("name");
			int categoryIndex = c.getColumnIndex("category");
			int salesIndex = c.getColumnIndex("sales");
			
			StoreInfo info = new StoreInfo();
			info.UUID = c.getString(uuidIndex);
			info.Name = c.getString(nameIndex);
			info.Category = c.getString(categoryIndex);
			info.SalesText = c.getString(salesIndex);
			
			infos[i] = info;
			c.moveToNext();
		}
		
		db.close();
		sql.close();
		
		return infos;
	}
	
	private static class MySQLiteOpenHelper extends SQLiteOpenHelper
	{
		private static final String DB_NAME = "sqlite_chino.db";
		private static final int DB_VERSION = 1;
		
		private static final String CREATE_TABLE = "create table table_chino ( uuid text primary key, name text not null, category text not null, sales text );";
		private static final String DROP_TABLE = "drop table table_chino;";

		public MySQLiteOpenHelper(Context context)
		{
			super(context, DB_NAME, null, DB_VERSION);
		}

		/**
		 * �f�[�^�x�[�X�����݂��Ȃ������Ƃ�
		 */
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			// �e�[�u�����쐬����
			db.execSQL(CREATE_TABLE);
		}

		/**
		 * �f�[�^�x�[�X�̃A�b�v�O���[�h���K�v�ɂȂ����Ƃ�
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			// �e�[�u���𐶐����Ȃ���
			db.execSQL(DROP_TABLE);
			onCreate(db);
		}
		
	}
}

