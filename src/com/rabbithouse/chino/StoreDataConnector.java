package com.rabbithouse.chino;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/findStoreInfo/");
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
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/findStoreDetail/");
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
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/notifyActiveCustomer/");
		uri.append(encryptString(uuid));
		uri.append("/");
		uri.append(userID);
		
		// HTTPリクエストを送る
		sendHTTPRequest(uri.toString());
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
	private static String encryptString(String text) {
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
}

