package com.rabbithouse.chino;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

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
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * ���̃T�[�r�X�́A���Ԋu��iBeacon�M������M���A���̌��m���ʂ�activity�ɕԂ��B
 * @author yoship
 */
public class BeaconSearchService extends Service
{
	// �M�ߐ��̍����d�g���x�𓾂邽�߂ɂƂ�f�[�^�̐�
	private static final int UUID_AVERAGE_NUM = 8;
	
	// �X�̋߂��ɂ���Ɣ��f����d�g���x��臒l
	private static final int RSSI_ACCEPT_THRESHOLD = -70;

	// iBeacon��UUID��ێ����郊�X�g
	private ArrayList<UuidData> _uuidList = new ArrayList<UuidData>();
	
	// Bluetooth�A�_�v�^
	private BluetoothAdapter _bluetoothAdapter = null;
	
	// Notification�}�l�[�W��
	private NotificationManager _notificationManager = null;
	
	// Bluetooth�p�̃u���[�h�L���X�g���V�[�o
	private BluetoothBroadcastReceiver _bluetoothBR = null;
	
	
	// ���[�U�ŗL�̔ԍ�
	private int _userID;
	
	public BeaconSearchService()
	{
	}

	@Override
	public void onCreate()
	{
		// bluetooth�𗘗p���鏀��
		final BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
		_bluetoothAdapter = bluetoothManager.getAdapter();
		_bluetoothAdapter.startLeScan(mLeScanCallback);
		
		// �X�e�[�^�X�ʒm�𗘗p���鏀��
		_notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		// Bluetooth�̏�Ԃ̕ω����󂯎�郌�V�[�o��o�^
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		_bluetoothBR = new BluetoothBroadcastReceiver(this);
		registerReceiver(_bluetoothBR, intentFilter);
		
		// �ŗL�̃��[�UID���쐬
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
				 case BluetoothAdapter.STATE_ON:			// Bluetooth��ON�ɕς�����Ƃ�
					 // iBeacon�̃X�L�������J�n
					 Log.i("bluetooth", "bluetooth��ON�ɂȂ�����!");
					 bss._bluetoothAdapter.startLeScan(mLeScanCallback);
					 break;
					 
				 case BluetoothAdapter.STATE_TURNING_OFF:	// Bluetooth��OFF�ɕς��Ƃ�
					 // iBeacon�̃X�L�������~
					 Log.i("bluetooth", "bluetooth��OFF�ɂȂ�����!");
					 bss._bluetoothAdapter.stopLeScan(mLeScanCallback);
					 break;
				 }
			 }
		}
	}
	
	/**
	 * BlurtoothLE�̐M�����󂯎��R�[���o�b�N
	 */
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
	{
		/**
		 * BluetoothLE�̐M�����L���b�`�����Ƃ�
		 */
	    @Override
	    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
	    {
	    	//_bluetoothAdapter.stopLeScan(mLeScanCallback);
	    	
	    	Log.i("beacon", "�������� RSSI:" + rssi);
	    	// UUID�𓾂�
	    	String uuid = GetBeaconUuid(scanRecord);
	    	if(uuid == null) return;
	    	
	    	UuidData uuidData = findUuid(uuid);
	 
	    	// ���łɌ��m�ς݂�UUID�Ȃ��
	    	if(uuidData != null)
	    	{
	    		Log.i("beacon", "���łɌ��m�ς݂�UUID");
	    		
	    		// ���ϒl�𓾂�̂ɏ\���ȃf�[�^���Ƃꂽ��
	    		if(uuidData.getRssiNum() >= UUID_AVERAGE_NUM)
	    		{
	    			Log.i("beacon", "���ϒl�𓾂�̂ɏ\���ȃf�[�^���Ƃꂽyo!");
	    			
	    			// �d�g���x�̕��ϒl���Ƃ�
	    			int avgRssi = uuidData.getRssi();
	    			uuidData.clearRssis();
	    			
	    			
	    			// �d�g���x������������X�܂̋߂��ɂ��邱�Ƃ�`����
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
	    		// �M�ߐ��̂�����d�g���x�Ȃ̂ł����ƃf�[�^�������
	    		else
	    		{	
	    			uuidData.addRssi(rssi);
	    			return;
	    		}
	    	}
	    	// �͂��߂Č��m����UUID�Ȃ��
	    	else
	    	{
	    		Log.i("beacon", "�͂��߂Č��m����UUID");
	    		
	    		StoreInfo info = null;
	    		try
	    		{
	    			// �ȒP�ȓX�܏����󂯎��
	    			info = StoreDataConnector.getStoreInfo(uuid);
	    		}
	    		catch(Exception e)
	    		{
	    			e.printStackTrace();
	    			return;
	    		}
	    		
	    		// UUID���X�g�ɒǉ�
	    		_uuidList.add(new UuidData(uuid));
	    		
	    		// �X�܏����X�e�[�^�X�ʒm�ɑ���
	    		NotifyStoreInfo(info);
	    	}
	    	
	    	//_bluetoothAdapter.startLeScan(mLeScanCallback);
	    }
	};
	
	
	
	/**
	 * �w���UUID������UuidData����������
	 * @param uuid ����������UUID
	 * @return �w���UUID������UuidData
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
	 * UUID�Ɠd�g���x�̕��ϒl�����f�[�^
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
		 * �d�g���x(RSSI)��ǉ�����
		 * @param rssi �ǉ�����d�g���x
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
		 * �d�g���x�̕��ϒl�𓾂�
		 * @return �d�g���x�̕��ϒl
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
		 * �d�g���x�̃f�[�^���폜����
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
		 * �ǉ����ꂽ�d�g���x�̃f�[�^���𓾂�
		 * @return �d�g���x�̃f�[�^��
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
	 * UUID�𕶎���Ƃ��Ď擾����
	 * @param record bluetooth���瓾��ꂽscanRecord
	 * @return UUID�̕�����
	 */
	private String GetBeaconUuid(byte[] record) {
		
		// 30�ɖ����Ȃ�������null
		if (record.length <= 30) return null;
		
		// iBeacon�̐M���݂̂��L���b�`����
		if (!(record[5] == (byte) 0x4c) && (record[6] == (byte) 0x00) && (record[7] == (byte) 0x02) && (record[8] == (byte) 0x15)) return null;
		
		// byte�z���UUID�̕�����ɕϊ�
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
	 * byte�z���16�i���̕�����ɕϊ�����
	 * @param bytes byte�z��
	 * @return 16�i���̕�����
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
	 * �X�e�[�^�X�ʒm�ɓX�܂̏��𑗂�
	 * @param info �X�܏��
	 */
	private void NotifyStoreInfo(StoreInfo info)
	{
		
		Builder builder = new Builder(getApplicationContext());
		builder.setContentTitle(info.Name);
		builder.setContentText(info.SalesText);
		builder.setTicker("[" + info.Category + "]���X�̏����L���b�`���܂���!");
		builder.setSmallIcon(DataConnector.getCategoryIcon(info.Category));
		
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
		// TODO: bluetoothLE�̎�M���J�n
		Log.i("onStartCommand", "onStartCommand���Ă΂ꂽ��!");
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy()
	{
		Log.i("onStartCommand", "onDestroy���Ă΂ꂽ��!");
		
		// �u���[�h�L���X�g���V�[�o�̓o�^���폜
		unregisterReceiver(_bluetoothBR);
		
		// BlurtoothLE�̌��m���X�g�b�v����
		_bluetoothAdapter.stopLeScan(mLeScanCallback);
		
		_uuidList.clear();
		
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
