package com.rabbithouse.chino;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * このサービスは、一定間隔でiBeacon信号を受信し、その検知結果をactivityに返す。
 * @author yoship
 */
public class BeaconSearchService extends Service
{
	// ステータス通知用の信憑性の高い電波強度を得るためにとるデータの数
	private static final int UUID_STATUS_AVERAGE_NUM = 5;
	
	// 店の情報を受け取る電波強度の閾値
	private static final int UUID_STATUS_THRESHOLD_RSSI = -82;
	
	// 信憑性の高い電波強度を得るためにとるデータの数
	private static final int UUID_NEAR_AVERAGE_NUM = 8;
	
	// 店の近くにいると判断する電波強度の閾値
	private static final int UUID_NEAR_THRESHOLD_RSSI = -75;

	// iBeaconのUUIDを保持するリスト
	private ArrayList<UuidData> _uuidList = new ArrayList<UuidData>();
	
	// Bluetoothアダプタ
	private BluetoothAdapter _bluetoothAdapter = null;
	
	// Notificationマネージャ
	private NotificationManager _notificationManager = null;
	
	// Bluetooth用のブロードキャストレシーバ
	private BluetoothBroadcastReceiver _bluetoothBR = null;
	
	
	// ユーザ固有の番号
	private int _userID = 12345;
	
	public BeaconSearchService()
	{
	}

	@Override
	public void onCreate()
	{
		// bluetoothを利用する準備
		BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
		_bluetoothAdapter = bluetoothManager.getAdapter();
		//_bluetoothAdapter.startLeScan(mLeScanCallback);
		
		// ステータス通知を利用する準備
		_notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		// Bluetoothの状態の変化を受け取るレシーバを登録
		_bluetoothBR = new BluetoothBroadcastReceiver(this);
		
		//IntentFilter intentFilter = new IntentFilter();
		//intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		//registerReceiver(_bluetoothBR, intentFilter);
		
		// 固有のユーザIDを作成
		/*
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		Log.e("aaaaa", telephonyManager.toString());
		if(telephonyManager.getDeviceId() != null) Log.e("aaaaa", telephonyManager.getDeviceId());
		if(telephonyManager.getDeviceSoftwareVersion() != null) Log.e("aaaaa", telephonyManager.getDeviceSoftwareVersion());
		if(telephonyManager.getGroupIdLevel1() != null) Log.e("aaaaa", telephonyManager.getGroupIdLevel1());
		if(telephonyManager.getLine1Number() != null) Log.e("aaaaa", telephonyManager.getLine1Number());
		if(telephonyManager.getNetworkCountryIso() != null) Log.e("aaaaa", telephonyManager.getNetworkCountryIso());
		if(telephonyManager.getNetworkOperator() != null) Log.e("aaaaa", telephonyManager.getNetworkOperator());
		if(telephonyManager.getNetworkOperatorName() != null) Log.e("aaaaa", telephonyManager.getNetworkOperatorName());
		//_userID = telephonyManager.getDeviceId().hashCode();
		 */
	}
	
	private class BluetoothBroadcastReceiver extends BroadcastReceiver
	{
		BeaconSearchService bss;

		public BluetoothBroadcastReceiver(BeaconSearchService bss)
		{
			this.bss = bss;
		}
		@Override
		public void onReceive(Context context, Intent intent)
		{
			 if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED))
			 {
				 int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
				 
				 switch(state)
				 {
				 case BluetoothAdapter.STATE_ON:			// BluetoothがONに変わったとき
					 // iBeaconのスキャンを開始
					 Log.i("bluetooth", "bluetoothがONになったよ!");
					 bss._bluetoothAdapter.startLeScan(mLeScanCallback);
					 break;
					 
				 case BluetoothAdapter.STATE_TURNING_OFF:	// BluetoothがOFFに変わるとき
					 // iBeaconのスキャンを停止
					 Log.i("bluetooth", "bluetoothがOFFになったよ!");
					 bss._bluetoothAdapter.stopLeScan(mLeScanCallback);
					 break;
				 }
			 }
		}
	}
	
	/**
	 * BlurtoothLEの信号を受け取るコールバック
	 */
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
	{
		int ret = 0;
		/**
		 * BluetoothLEの信号をキャッチしたとき
		 */
	    @Override
	    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
	    {
	    	if(ret++ < 3) return;
	    	ret = 0;
	    	
	    	//Log.i("beacon", "何かきた RSSI:" + rssi);
	    	// UUIDを得る
	    	String uuid = GetBeaconUuid(scanRecord);
	    	if(uuid == null) return;
	    	
	    	UuidData uuidData = findUuid(uuid);
	    	
	    	// 初めて受け取るUUIDなら
	    	if(uuidData == null)
	    	{
	    		Log.i("beacon", "初めてのUUIDだよ: " + uuid);
	    		try
	    		{
	    			// ステータス通知用の店舗情報を受け取る
	    			StoreInfo info = StoreDataConnector.getStoreInfo(uuid);
	    		}
	    		catch(Exception e)
	    		{
	    			e.printStackTrace();
	    			return;
	    		}
	    		// リストに追加
	    		uuidData = new UuidData(uuid);
	    		_uuidList.add(uuidData);
	    	}
	    	
	    	// RSSIを追加
	    	uuidData.addRssi(rssi);
	    	//Log.i("beacon", "RSSIの数: " + uuidData.getRssiNum());
	    	
	    	// まだステータス通知を送っていなくて店の情報を受け取るに値する電波強度のデータ数が取れたら
	    	if(!uuidData.IsAlreadyNotify && uuidData.getRssiNum() >= UUID_STATUS_AVERAGE_NUM)
	    	{
	    		
	    		// 平均値を得る
		    	int aveRssi = uuidData.getRssi();
		    	
    			// 店の情報を受け取るに値する電波強度なら
    			if(aveRssi >= UUID_STATUS_THRESHOLD_RSSI)
    			{
    				Log.i("beacon", "店の情報を受け取るに値する平均値でした：　" + aveRssi);
    				
    				StoreInfo info = null;
		    		try
		    		{
		    			// ステータス通知用の店舗情報を受け取る
		    			info = StoreDataConnector.getStoreInfo(uuid);
		    		}
		    		catch(Exception e)
		    		{
		    			e.printStackTrace();
		    			return;
		    		}
		    		// データベースにお店情報を保存
		    		StoreDataConnector.saveStoreInfo(getApplicationContext(), info);
		    		
		    		HashMap<String, Boolean> map = DataConnector.loadCategoryCheckList(getApplicationContext());
		    		if(map.get(info.Category))
		    		{
		    			// 店舗情報をステータス通知に送る
			    		NotifyStoreInfo(info);
			    		uuidData.IsAlreadyNotify = true;
		    		}
		    		
		    		uuidData.clearRssis();
    			}
	    	}
	    	// すでにステータス通知を送っていて、店の近くにいるという情報を店に送るだけのデータ数があったら
	    	else if(uuidData.IsAlreadyNotify && uuidData.getRssiNum() >= UUID_NEAR_AVERAGE_NUM)
	    	{
	    		// 平均値を得る
		    	int aveRssi = uuidData.getRssi();
		    	uuidData.clearRssis();
		    	
		    	// 平均値が閾値を超えるとき
		    	if(aveRssi >= UUID_NEAR_THRESHOLD_RSSI)
		    	{
		    		Log.i("beacon", "店の近くにいると判断する平均値でした：　" + aveRssi);
		    		
		    		try {
						StoreDataConnector.notifyNearStoreUser(uuidData.UUID, _userID);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
		    	}
	    	}
	    	
	    	/*
	    	// すでに検知済みのUUIDならば
	    	if(uuidData != null)
	    	{
	    		Log.i("beacon", "すでに検知済みのUUID");
	    		
	    		// 平均値を得るのに十分なデータがとれたら
	    		if(uuidData.getRssiNum() >= UUID_AVERAGE_NUM)
	    		{
	    			Log.i("beacon", "平均値を得るのに十分なデータがとれたyo!");
	    			
	    			// 電波強度の平均値をとる
	    			int avgRssi = uuidData.getRssi();
	    			uuidData.clearRssis();
	    			
	    			
	    			// 電波強度が強かったら店舗の近くにいることを伝える
	    			if(avgRssi >= RSSI_ACCEPT_THRESHOLD)
	    			{
	    				try {
							StoreDataConnector.notifyNearStoreUser(uuidData.UUID, _userID);
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
	    			}
	    		}
	    		// 信憑性のかける電波強度なのでもっとデータ数を取る
	    		else
	    		{	
	    			uuidData.addRssi(rssi);
	    			return;
	    		}
	    	}
	    	// はじめて検知するUUIDならば
	    	else
	    	{
	    		Log.i("beacon", "はじめて検知するUUID");
	    		
	    		StoreInfo info = null;
	    		try
	    		{
	    			// ステータス通知用の店舗情報を受け取る
	    			info = StoreDataConnector.getStoreInfo(uuid);
	    		}
	    		catch(Exception e)
	    		{
	    			e.printStackTrace();
	    			return;
	    		}
	    		
	    		// UUIDリストに追加
	    		_uuidList.add(new UuidData(uuid));
	    		
	    		// 店舗情報をステータス通知に送る
	    		NotifyStoreInfo(info);
	    	}
	    	*/
	    }
	};
	
	
	
	/**
	 * 指定のUUIDを持つUuidDataを検索する
	 * @param uuid 検索したいUUID
	 * @return 指定のUUIDを持つUuidData
	 */
	private UuidData findUuid(String uuid)
	{
		int size = _uuidList.size();
		int hash = uuid.hashCode();
		for(int i=0; i<size; i++)
		{
			if(_uuidList.get(i).hashCode() == hash) return _uuidList.get(i);
		}
		return null;
	}
	
	/**
	 * UUIDと電波強度の平均値を持つデータ
	 * @author yoship
	 */
	class UuidData
	{
		public String UUID;
		public int[] Rssis = new int[UUID_NEAR_AVERAGE_NUM];
		public boolean IsAlreadyNotify = false;
		
		private int containsNum = 0;
		
		public UuidData(String uuid)
		{
			UUID = uuid;
		}
		
		/**
		 * 電波強度(RSSI)を追加する
		 * @param rssi 追加する電波強度
		 */
		public void addRssi(int rssi)
		{
			if(containsNum >= UUID_NEAR_AVERAGE_NUM )
			{
				for(int i=0; i<UUID_NEAR_AVERAGE_NUM - 1; i++)
				{
					Rssis[i] = Rssis[i+1];
				}
				Rssis[UUID_NEAR_AVERAGE_NUM - 1] = rssi;
			}
			else
			{
				Rssis[containsNum] = rssi;
				containsNum++;
			}
		}
		
		/**
		 * 電波強度の平均値を得る
		 * @return 電波強度の平均値
		 */
		public int getRssi()
		{
			int sum = 0;
			for(int i=0; i<containsNum; i++)
			{
				sum += Rssis[i];
			}
			
			return sum / containsNum;
		}
		
		/**
		 * 電波強度のデータを削除する
		 */
		public void clearRssis()
		{
			for(int i=0; i<UUID_NEAR_AVERAGE_NUM; i++)
			{
				Rssis[i] = 0;
			}
			containsNum = 0;
		}
		
		/**
		 * 追加された電波強度のデータ数を得る
		 * @return 電波強度のデータ数
		 */
		public int getRssiNum()
		{
			return containsNum;
		}
		
		@Override
		public int hashCode()
		{
			return UUID.hashCode();
		}
	}
	
	/**
	 * UUIDを文字列として取得する
	 * @param record bluetoothから得られたscanRecord
	 * @return UUIDの文字列
	 */
	private String GetBeaconUuid(byte[] record) {
		
		// 30に満たなかったらnull
		if (record.length <= 30) return null;
		
		// iBeaconの信号のみをキャッチする
		if (!(record[5] == (byte) 0x4c) && (record[6] == (byte) 0x00) && (record[7] == (byte) 0x02) && (record[8] == (byte) 0x15)) return null;
		
		// byte配列をUUIDの文字列に変換
		byte[] buf = new byte[16];
		for(int i=0; i<16; i++)buf[i] = record[i + 9];
		StringBuilder sb = new StringBuilder(bytesToHex(buf));
		sb.insert(8, "-");
	    sb.insert(12 + 1, "-");
	    sb.insert(16 + 2, "-");
	    sb.insert(20 + 3, "-");
		
		return sb.toString();
	}
	
	final private static char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	/**
	 * byte配列を16進数の文字列に変換する
	 * @param bytes byte配列
	 * @return 16進数の文字列
	 */
	private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
	
	/**
	 * ステータス通知に店舗の情報を送る
	 * @param info 店舗情報
	 */
	private void NotifyStoreInfo(StoreInfo info)
	{
		
		Builder builder = new Builder(getApplicationContext());
		builder.setContentTitle(info.Name);
		builder.setContentText(info.SalesText);
		builder.setTicker("[" + info.Category + "]お店の情報をキャッチしました!");
		int icon = DataConnector.getCategoryIcon(info.Category);
		builder.setSmallIcon(icon);
		
		Intent intent = new Intent();
		intent.setClassName("com.rabbithouse.chino", "com.rabbithouse.chino.StoreDetailActivity");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("UUID", info.UUID);
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);
		
		_notificationManager.notify(info.UUID.hashCode(), builder.build());
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO: bluetoothLEの受信を開始
		Log.i("onStartCommand", "onStartCommandが呼ばれたよ!");
		
		// スキャン開始
		if (_bluetoothAdapter != null) _bluetoothAdapter.startLeScan(mLeScanCallback);
		
		// ブロードキャストレシーバを登録
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		
		registerReceiver(_bluetoothBR, intentFilter);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy()
	{
		Log.i("onStartCommand", "onDestroyが呼ばれたよ!");
		
		// ブロードキャストレシーバの登録を削除
		unregisterReceiver(_bluetoothBR);
		
		// BlurtoothLEの検知をストップする
		_bluetoothAdapter.stopLeScan(mLeScanCallback);
		
		_uuidList.clear();
		
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
