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
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/findStoreInfo/");
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
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/findStoreDetail/");
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
		StringBuilder uri = new StringBuilder("http://chino.herokuapp.com/api/notifyActiveCustomer/");
		uri.append(encryptString(uuid));
		uri.append("/");
		uri.append(userID);
		
		// HTTP���N�G�X�g�𑗂�
		sendHTTPRequest(uri.toString());
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
	private static String encryptString(String text) {
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
}

