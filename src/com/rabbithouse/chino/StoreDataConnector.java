package com.rabbithouse.chino;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
 * iBeacon��ݒu���邨�X�ƃ��[�U�́A�X�܃f�[�^�̂������܂Ƃ߂��R�l�N�^
 * @author yoship
 */
public class StoreDataConnector {

	/**
	 * �ȒP�ȓX�܏����擾����
	 * @param UUID �X�܂ɐݒu���Ă���iBeacon��UUID
	 * @return �X�e�[�^�X�ʒm�p�̓X�܏��
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 */
	public static StoreInfo getStoreInfo(String UUID) throws ClientProtocolException, IOException, JSONException
	{
		
		// UUID��URI�ɖ��ߍ���
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/findStoreInfo/");
		uri.append(UUID);
		
		// JSON�f�[�^���󂯎��
		JSONObject jsonRoot = getJsonObjectByHTTPRequest(uri.toString());
		
		Log.e("json", jsonRoot.toString(0));
		
		JSONObject obj = jsonRoot.getJSONArray("data").getJSONObject(0);
		//JSONObject obj = jsonRoot.getJSONObject("data");
		
		StoreInfo info = new StoreInfo();
		info.Name = obj.getString("name");
		info.Category = obj.getInt("categoryID") + "";
		info.SalesText = obj.getString("salesText");
		info.UUID = UUID;
		
		
		/*
		StoreInfo info = new StoreInfo();
		info.Name = "�������X";
		info.Category = "�G���{";
		info.SalesText = "���ւ�";
		info.UUID = UUID;
		*/
		
		return info;
	}
	
	/**
	 * �ڍׂȓX�܏����擾����
	 * @param UUID �X�܂ɐݒu���Ă���iBeacon��UUID
	 * @return �X�e�[�^�X�ʒm�p�̓X�܏��
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 */
	public static StoreInfo getStoreDetail(String UUID) throws ClientProtocolException, IOException, JSONException
	{
		// UUID��URI�ɖ��ߍ���
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/findStoreDetail/");
		uri.append(UUID);
		
		// JSON�f�[�^���󂯎��
		JSONObject jsonRoot = getJsonObjectByHTTPRequest(uri.toString());

		return null;
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

        // JSON�I�u�W�F�N�g��Ԃ�
		return new JSONObject(data);
		
	}
}

