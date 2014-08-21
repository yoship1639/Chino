package com.rabbithouse.chino;

import android.app.*;
import android.app.Notification.Builder;
import android.bluetooth.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;

public class TestActivity extends Activity {
	
	BluetoothManager bluetoothManager = null;
	BluetoothAdapter mBluetoothAdapter = null;
	
	NotificationManager notificationManager = null;
	
	Handler mHandler = null;
	
	int index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		// bluetooth�𗘗p���鏀��
		bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		
		// Notification���g����悤�ɂ���
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				SetRssi(msg.arg1);
			}
		};
		
		// [Start Search]�{�^���������ꂽ��
		Button button_start = (Button) findViewById(R.id.button_searchtest);
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	// iBeacon�����m����T�[�r�X���J�n����
            	startService(new Intent(TestActivity.this, BeaconSearchService.class));
            }
        });
        
        // [Start Search]�{�^���������ꂽ��
 		Button button_stop = (Button) findViewById(R.id.button_stoptest);
         button_stop.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
             	// iBeacon�����m����T�[�r�X���~����
             	stopService(new Intent(TestActivity.this, BeaconSearchService.class));
             }
         });
		
		// Scan Start�{�^���������ꂽ��
		Button button_scanStart = (Button) findViewById(R.id.button_scanStart);
        button_scanStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // �{�^�����N���b�N���ꂽ���ɌĂяo����܂�

        		Log.e("aaaaaaaaaaaaa", mBluetoothAdapter.isEnabled() + ", Scan Start!!!!!!");
        		mBluetoothAdapter.startLeScan(mLeScanCallback);
            	
            	/*
            	// Notification�e�X�g
            	Builder builder = new Builder(getApplicationContext());
        		builder.setContentTitle("Test");
        		builder.setContentText("�e�X�g�ł���");
        		builder.setTicker("���ӂ�" + index);
        		builder.setSmallIcon(R.drawable.ic_launcher);
        		notificationManager.notify(index++, builder.build());
        		*/
            }
        });
        
        // Scan Stop�{�^���������ꂽ��
        Button button_scanStop = (Button) findViewById(R.id.button_scanStop);
        button_scanStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Log.e("aaaaaaaaaaaaaaaaaa", "Stop Scan BluetoothLE!!!!!!!!!!!!");
            	mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        });
	}
	
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		int index = 0;
	    @Override
	    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
	    	String uuid = GetUuidFromRecord(scanRecord);
	    	Message mes = new Message();
	    	mes.arg1 = rssi;
	    	mHandler.sendMessage(mes);

	    	//Log.e(index + "", uuid + ": " + rssi);
	    	//index++;
	    }
	};
	
	public void SetRssi(int rssi)
	{
		TextView text = (TextView)findViewById(R.id.textView_rssi);
    	text.setText("RSSI: " + rssi);
	}
	
	/**
	 * UUID�𕶎���Ƃ��Ď擾����
	 * @param record bluetooth���瓾��ꂽscanRecord
	 * @return UUID�̕�����
	 */
	private String GetUuidFromRecord(byte[] record){
		
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
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
