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
 * ���̃T�[�r�X�́A���Ԋu��iBeacon�M������M���A���̌��m���ʂ�activity�ɕԂ��B
 * @author yoship
 */
public class BeaconSearchService extends Service
{
	// �M�ߐ��̍����d�g���x�𓾂邽�߂ɂƂ�f�[�^�̐�
	private static final int UUID_AVERAGE_NUM = 5;
	
	// �X�̋߂��ɂ���Ɣ��f����d�g���x��臒l
	private static final int RSSI_ACCEPT_THRESHOLD = -60;

	// iBeacon��UUID��ێ����郊�X�g
	private ArrayList<UuidData> _uuidList = new ArrayList<UuidData>();
	
	// Bluetooth�A�_�v�^
	private BluetoothAdapter _bluetoothAdapter = null;
	
	// Notification�}�l�[�W��
	private NotificationManager notificationManager = null;
	
	// �u���[�h�L���X�g���V�[�o
	private BluetoothBroadcastReceiver bluetoothBR = null;
	
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
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		// Bluetooth�̏�Ԃ��ς��̂����m
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		
		// �u���[�h�L���X�g���󂯎�郌�V�[�o��o�^
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
				 case BluetoothAdapter.STATE_ON:			// Bluetooth��ON�ɕς�����Ƃ�
					 // iBeacon�̃X�L�������J�n
					 bss._bluetoothAdapter.startLeScan(mLeScanCallback);
					 break;
					 
				 case BluetoothAdapter.STATE_TURNING_OFF:	// Bluetooth��OFF�ɕς��Ƃ�
					 // iBeacon�̃X�L�������~
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
	    	// UUID�𓾂�
	    	String uuid = GetBeaconUuid(scanRecord);
	    	if(uuid == null) return;
	    	
	    	Log.e("UUID", uuid);
	    	
	    	UuidData uuidData = findUuid(uuid);
	 
	    	// ���łɌ��m�ς݂�UUID�Ȃ��
	    	if(uuidData != null)
	    	{
	    		// ���ϒl�𓾂�̂ɏ\���ȃf�[�^���Ƃꂽ��
	    		if(uuidData.getRssiNum() >= UUID_AVERAGE_NUM)
	    		{
	    			// �d�g���x�̕��ϒl���Ƃ�
	    			int avgRssi = uuidData.getRssi();
	    			uuidData.clearRssis();
	    			
	    			// TODO: �d�g���x������������X�܂̋߂��ɂ��邱�Ƃ�`����
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
		builder.setTicker("���X�̏����L���b�`���܂���!");
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
		
		// �u���[�h�L���X�g���V�[�o�̓o�^���폜
		unregisterReceiver(bluetoothBR);
		
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
