package com.rabbithouse.chino;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * iBeaconを設置するお店とユーザの、店舗データのやり取りをまとめたコネクタ
 * @author yoship
 */
public class StoreDataConnector {

	/**
	 * 簡単な店舗情報を取得する
	 * @param UUID 店舗に設置しているiBeaconのUUID
	 * @return ステータス通知用の店舗情報
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 */
	public static StoreInfo getStoreInfo(String UUID) throws ClientProtocolException, IOException, JSONException
	{
		/*
		// UUIDをURIに埋め込む
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/findStoreInfo/");
		uri.append(UUID);
		
		// JSONデータを受け取る
		JSONObject jsonRoot = getJsonObjectByHTTPRequest(uri.toString());
		*/
		
		StoreInfo info = new StoreInfo();
		info.Name = "藤嶋書店";
		info.Category = "本屋";
		info.SalesText = "今ならなんと、すべての本が10割引!!!";
		info.UUID = UUID;
		
		return info;
	}
	
	/**
	 * 詳細な店舗情報を取得する
	 * @param UUID 店舗に設置しているiBeaconのUUID
	 * @return ステータス通知用の店舗情報
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 */
	public static StoreInfo getStoreDetail(String UUID) throws ClientProtocolException, IOException, JSONException
	{
		// UUIDをURIに埋め込む
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/findStoreDetail/");
		uri.append(UUID);
		
		// JSONデータを受け取る
		JSONObject jsonRoot = getJsonObjectByHTTPRequest(uri.toString());

		return null;
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

        // JSONオブジェクトを返す
		return new JSONObject(data);
		
	}
}

