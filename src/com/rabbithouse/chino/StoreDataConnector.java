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
 * iBeaconを設置するお店とユーザの、店舗データのやり取りをまとめたコネクタ
 * @author yoship
 */
public class StoreDataConnector
{

	/**
	 * 簡単な店舗情報を取得する
	 * @param uuid 店舗に設置しているiBeaconのUUID
	 * @return ステータス通知用の店舗情報
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 */
	public static StoreInfo getStoreInfo(String uuid) throws ClientProtocolException, IOException, JSONException
	{
		// UUIDをURIに埋め込む
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/");
		uri.append("findStoreInfo");
		uri.append("/");
		uri.append(encryptString(uuid));
		
		// JSONデータを受け取る
		JSONObject jsonRoot = getJsonObjectByHTTPRequest(uri.toString());
		
		// JSONオブジェクトから店舗情報を抽出する
		JSONObject obj = jsonRoot.getJSONArray("data").getJSONObject(0);
		StoreInfo info = new StoreInfo();
		info.Name = obj.getString("storeName");
		info.Category = obj.getString("categoryName");
		info.SalesText = obj.getString("salesText");
		info.UUID = uuid;
		
		return info;
		/*
		StoreInfo info = new StoreInfo();
		info.Name = "藤嶋書店";
		info.Category = "ホビー";
		info.SalesText = "今ならなんと全書籍10割引き！！！";
		info.UUID = "11111";
		return info;*/
	}
	
	/**
	 * 詳細な店舗情報を取得する
	 * @param uuid 店舗に設置しているiBeaconのUUID
	 * @return ステータス通知用の店舗情報
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 */
	public static StoreDetail getStoreDetail(String uuid) throws ClientProtocolException, IOException, JSONException
	{
		
		// UUIDをURIに埋め込む
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/");
		uri.append("findStoreDetail");
		uri.append("/");
		uri.append(encryptString(uuid));
		
		// JSONデータを受け取る
		JSONObject jsonRoot = getJsonObjectByHTTPRequest(uri.toString());
		
		// JSONオブジェクトから店舗情報を抽出する
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
		detail.Name = "藤嶋書店";
		detail.Category = "ホビー";
		detail.SalesText = "今ならなんと全書籍10割引き！！！";
		detail.Detail = "<!DOCTYPE html><html><head></head><body>"
				+ "<p>シャロ！シャロ！シャロ！シャロぅぅうううわぁああああああああああああああああああああああん！！！</p>"
				+ "<p>あぁああああ…ああ…あっあっー！あぁああああああ！！！シャロシャロシャロぅううぁわぁああああ！！！</p>"
+ "<p>あぁクンカクンカ！クンカクンカ！スーハースーハー！スーハースーハー！いい匂いだなぁ…くんくん</p>"
+ "<p>んはぁっ！シャロ・フランソワーズたんの桃色ブロンドの髪をクンカクンカしたいお！クンカクンカ！あぁあ！！</p>"
+ "<p>間違えた！モフモフしたいお！モフモフ！モフモフ！髪髪モフモフ！カリカリモフモフ…きゅんきゅんきゅい！！</p>"
+ "<p>12話のシャロたんかわいかったよぅ！！あぁぁああ…あああ…あっあぁああああ！！ふぁぁあああんんっ！！</p>"
+ "<p>アニメ2期放送されて良かったねシャロたん！あぁあああああ！かわいい！シャロたん！かわいい！あっああぁああ！</p>"
+ "<p>コミック2巻も発売されて嬉し…いやぁああああああ！！！にゃああああああああん！！ぎゃああああああああ！！</p>"
+ "<p>ぐあああああああああああ！！！コミックなんて現実じゃない！！！！あ…小説もアニメもよく考えたら…</p>"
+ "<p>シ　ャ　ロ　ち ゃ ん は 現実 じ ゃ な い？にゃあああああああああああああん！！うぁああああああああああ！！</p>"
+ "<p>そんなぁああああああ！！いやぁぁぁあああああああああ！！はぁああああああん！！</p>"
+ "<p>この！ちきしょー！やめてやる！！現実なんかやめ…て…え！？見…てる？表紙絵のシャロちゃんが僕を見てる？</p>"
+ "<p>表紙絵のシャロちゃんが僕を見てるぞ！シャロちゃんが僕を見てるぞ！挿絵のシャロちゃんが僕を見てるぞ！！</p>"
+ "<p>アニメのシャロちゃんが僕に話しかけてるぞ！！！よかった…世の中まだまだ捨てたモンじゃないんだねっ！</p>"
+ "<p>いやっほぉおおおおおおお！！！僕にはシャロちゃんがいる！！</p>"
				+ "</body></html>";
		detail.URL = "https://www.google.co.jp/";
		detail.UpdateDate = "2014-08-28 12:00:00";
		detail.UUID = "11111";
		
		return detail;*/
	}
	
	
	
	/**
	 * HTTPリクエストから店舗情報のJSONデータを受け取る
	 * @param uri HTTPリクエスト用のURI
	 * @return 店舗情報のJSONデータ
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	private static JSONObject getJsonObjectByHTTPRequest(String uri) throws ClientProtocolException, IOException, JSONException
	{
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(uri.toString());
		
		// HTTPリクエストを送り、レスポンスを受け取る
		HttpResponse httpResponse = httpClient.execute(request);
		
		// 結果が芳しくない場合はIOException
		int status = httpResponse.getStatusLine().getStatusCode(); 
		if (HttpStatus.SC_OK != status) throw new IOException();
		
		// データをJSON用に変換
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        httpResponse.getEntity().writeTo(outputStream);
        String data = outputStream.toString();
        
        Log.e("aaaaa", data);

        // JSONオブジェクトを返す
		return new JSONObject(data);
		
		
		
	}
	
	/**
	 * サーバにユーザが店の近くにいることを通達する
	 * @param uuid 店舗に設置しているiBeaconのUUID
	 * @param userID ユーザ固有のID
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static void notifyNearStoreUser(String uuid, int userID) throws ClientProtocolException, IOException
	{
		
		// UUIDとユーザ固有IDをURIに埋め込む
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/");
		uri.append("notifyActiveCustomer");
		uri.append("/");
		uri.append(encryptString(uuid));
		uri.append("/");
		uri.append(userID);
		
		final String strUri = uri.toString();
		
		// HTTPリクエストを送る
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
	 * UUIDから店舗にいるChinoユーザの人数を得る
	 * @param uuid 店舗に設置しているiBeaconのUUID
	 * @return 店舗にいるChinoユーザの人数
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	public static int getActiveCustomerCount(String uuid) throws ClientProtocolException, IOException, JSONException
	{
		
		// UUIDとユーザ固有IDをURIに埋め込む
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/");
		uri.append("getActiveCustomerCount");
		uri.append("/");
		uri.append(encryptString(uuid));
		
		// HTTPリクエストを送り、JSONオブジェクトを得る
		JSONObject jsonRoot = getJsonObjectByHTTPRequest(uri.toString());
		
		// JSONオブジェクトから店舗にいるChinoユーザの数を得る
		JSONObject obj = jsonRoot.getJSONArray("data").getJSONObject(0);
		int count = obj.getInt("activeCustomerCount");
		
		return count;
		
		//return 10;
	}
	
	/**
	 * 指定のURIにHTTPリクエストを送る
	 * @param uri HTTPリクエストを送るURI
	 * @return リクエスト結果
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static HttpResponse sendHTTPRequest(String uri) throws ClientProtocolException, IOException
	{
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(uri.toString());
		
		// HTTPリクエストを送り、レスポンスを受け取る
		return httpClient.execute(request);
	}
	
	/**
	 * ハッシュ化
	 * @param text ハッシュ化する文字列
	 * @return
	 */
	private static String encryptString(String text)
	{
	    // 変数初期化
	    MessageDigest md = null;
	    StringBuffer buffer = new StringBuffer();
	 
	    try {
	        // メッセージダイジェストインスタンス取得
	        md = MessageDigest.getInstance("SHA-256");
	    } catch (NoSuchAlgorithmException e) {
	        // 例外発生時、エラーメッセージ出力
	        System.out.println("指定された暗号化アルゴリズムがありません");
	    }
	 
	    // メッセージダイジェスト更新
	    md.update(text.getBytes());
	 
	    // ハッシュ値を格納
	    byte[] valueArray = md.digest();
	 
	    // ハッシュ値の配列をループ
	    for(int i = 0; i < valueArray.length; i++){
	        // 値の符号を反転させ、16進数に変換
	        String tmpStr = Integer.toHexString(valueArray[i] & 0xff);
	 
	        if(tmpStr.length() == 1){
	            // 値が一桁だった場合、先頭に0を追加し、バッファに追加
	            buffer.append('0').append(tmpStr);
	        } else {
	            // その他の場合、バッファに追加
	            buffer.append(tmpStr);
	        }
	    }
	 
	    // 完了したハッシュ計算値を返却
	    return buffer.toString();
	}
	
	/**
	 * 店舗情報を記憶する
	 * @param context データベースに接続するためのコンテキスト
	 * @param info 記憶する店舗情報
	 */
	public static void saveStoreInfo(Context context, StoreInfo info)
	{
		Log.i("save", "店舗情報をローカルデータベースに保存するよ！");
		// SQLiteを使用する準備
		MySQLiteOpenHelper sql = new MySQLiteOpenHelper(context);
		SQLiteDatabase db = sql.getWritableDatabase();
		
		// 指定のUUIDが存在するかを検索
		Cursor c = db.rawQuery("select uuid from table_chino where uuid=?;", new String[]{ info.UUID });
		
		// 保存するデータを作成
		ContentValues values = new ContentValues();
		values.put("uuid", info.UUID);
		values.put("name", info.Name);
		values.put("category", info.Category);
		values.put("sales", info.SalesText);
		
		// データが存在していない場合は挿入
		if(c.getCount() == 0)
		{
			db.insert("table_chino", null, values);
		}
		db.close();
		sql.close();
	}
	
	/**
	 * 記憶された店舗情報をすべて読み込む
	 * @param context データベースに接続するためのコンテキスト
	 * @return 店舗情報の配列
	 */
	public static StoreInfo[] loadStoreInfos(Context context)
	{
		Log.i("load", "お店の情報を読み込みまぁす！");
		
		// SQLiteを使用する準備
		MySQLiteOpenHelper sql = new MySQLiteOpenHelper(context);
		SQLiteDatabase db = sql.getWritableDatabase();
		
		// すべてのデータを検索するクエリ
		Cursor c = db.rawQuery("select * from table_chino;", null);
		c.moveToFirst();
		
		// データを1件ずつ取り出す
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
		 * データベースが存在しなかったとき
		 */
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			// テーブルを作成する
			db.execSQL(CREATE_TABLE);
		}

		/**
		 * データベースのアップグレードが必要になったとき
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			// テーブルを生成しなおす
			db.execSQL(DROP_TABLE);
			onCreate(db);
		}
		
	}
}

