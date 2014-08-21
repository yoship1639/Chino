package com.rabbithouse.chino;

import java.util.ArrayList;

import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * このサービスは、一定間隔でiBeacon信号を受信し、その検知結果をactivityに返す。
 * @author yoship
 */
public class BeaconSearchService extends Service
{
	// 信憑性の高い電波強度を得るためにとるデータの数
	private static final int UUID_AVERAGE_NUM = 5;
	
	// 店の近くにいると判断する電波強度の閾値
	private static final int RSSI_ACCEPT_THRESHOLD = -60;

	// iBeaconのUUIDを保持するリスト
	private ArrayList<UuidData> _uuidList = new ArrayList<UuidData>();
	
	// Bluetoothアダプタ
	private BluetoothAdapter _bluetoothAdapter = null;
	
	// Notificationマネージャ
	private NotificationManager notificationManager = null;
	
	// ブロードキャストレシーバ
	private BluetoothBroadcastReceiver bluetoothBR = null;
	
	public BeaconSearchService()
	{
	}

	@Override
	public void onCreate()
	{
		// bluetoothを利用する準備
		final BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
		_bluetoothAdapter = bluetoothManager.getAdapter();
		_bluetoothAdapter.startLeScan(mLeScanCallback);
		
		// ステータス通知を利用する準備
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		// Bluetoothの状態が変わるのを検知
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		
		// ブロードキャストを受け取るレシーバを登録
		bluetoothBR = new BluetoothBroadcastReceiver(this);
		registerReceiver(bluetoothBR, intentFilter);
	}
	
	private class BluetoothBroadcastReceiver extends BroadcastReceiver
	{
		BeaconSearchService bss;

		public BluetoothBroadcastReceiver(BeaconSearchService bss)
		{
			this.bss = bss;
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			 if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED))
			 {
				 int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
				 
				 switch(state)
				 {
				 case BluetoothAdapter.STATE_ON:			// BluetoothがONに変わったとき
					 // iBeaconのスキャンを開始
					 bss._bluetoothAdapter.startLeScan(mLeScanCallback);
					 break;
					 
				 case BluetoothAdapter.STATE_TURNING_OFF:	// BluetoothがOFFに変わるとき
					 // iBeaconのスキャンを停止
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
		/**
		 * BluetoothLEの信号をキャッチしたとき
		 */
	    @Override
	    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
	    {
	    	// UUIDを得る
	    	String uuid = GetBeaconUuid(scanRecord);
	    	if(uuid == null) return;
	    	
	    	Log.e("UUID", uuid);
	    	
	    	UuidData uuidData = findUuid(uuid);
	 
	    	// すでに検知済みのUUIDならば
	    	if(uuidData != null)
	    	{
	    		// 平均値を得るのに十分なデータがとれたら
	    		if(uuidData.getRssiNum() >= UUID_AVERAGE_NUM)
	    		{
	    			// 電波強度の平均値をとる
	    			int avgRssi = uuidData.getRssi();
	    			uuidData.clearRssis();
	    			
	    			// TODO: 電波強度が強かったら店舗の近くにいることを伝える
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
	    		StoreInfo info = null;
	    		try
	    		{
	    			// 簡単な店舗情報を受け取る
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
		public int[] Rssis = new int[UUID_AVERAGE_NUM];
		
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
			if(containsNum >= UUID_AVERAGE_NUM )
			{
				for(int i=0; i<UUID_AVERAGE_NUM - 1; i++)
				{
					Rssis[i] = Rssis[i+1];
				}
				Rssis[UUID_AVERAGE_NUM - 1] = rssi;
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
			for(int i=0; i<UUID_AVERAGE_NUM; i++)
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
		builder.setTicker("お店の情報をキャッチしました!");
		builder.setSmallIcon(R.drawable.ic_launcher);
		notificationManager.notify(info.UUID.hashCode(), builder.build());
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		
		// ブロードキャストレシーバの登録を削除
		unregisterReceiver(bluetoothBR);
		
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
