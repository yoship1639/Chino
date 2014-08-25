package com.rabbithouse.chino;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the
 * {@link ActiveCustomerCountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events. Use the
 * {@link ActiveCustomerCountFragment#newInstance} factory method to create an
 * instance of this fragment.
 *
 */
public class ActiveCustomerCountFragment extends Fragment
{
	// �X�̏ڍ׏��
	StoreDetail storeDetail;
	
	// �X�܂ɂ���Chino���[�U�̐l����\������TextView
	TextView textView_count;
	
	// �X�܂ɂ���Chino���[�U�̐l�����X�V���邽�߂̃^�C�}�[�ƃn���h��
	Timer timer;
	Handler handler;

	public static ActiveCustomerCountFragment newInstance(StoreDetail storeDetail)
	{
		ActiveCustomerCountFragment fragment = new ActiveCustomerCountFragment();
		Bundle args = new Bundle();
		args.putParcelable("StoreDetail", storeDetail);
		fragment.setArguments(args);
		return fragment;
	}

	public ActiveCustomerCountFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() == null) return;
		storeDetail = getArguments().getParcelable("StoreDetail");
		
		timer = new Timer();
		handler = new Handler();
	}
	
	@Override
	public void onResume()
	{
		// �^�C�}�[�J�n
		timer.schedule(new TimerTask()
		{
	        @Override
	        public void run()
	        {
	            // mHandler��ʂ���UI Thread�֏������L���[�C���O
	            handler.post( new Runnable()
	            {
	                public void run()
	                {
	                	// �T�[�o�ɓX�܂ɂ���Chino���[�U�̐l����₢���킹��
	                	int count = 0;
						try {
							count = StoreDataConnector.getActiveCustomerCount(storeDetail.UUID);
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
	                	
	                	textView_count.setText(count+"");
	                }
	            });
	        }
	    }, 1000, 1000);
		
		super.onResume();
	}
	
	@Override
	public void onPause()
	{
		// �^�C�}�[���X�g�b�v
		timer.cancel();
		
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_active_customer_count, container, false);
		
		// �^�C�g���̕\������ς���
		TextView title = (TextView)view.findViewById(R.id.textView_userCount_title);
		title.setText("[" + storeDetail.Name + "]�ɂ���Chino���[�U�̐l��");
		
		return view;
	}
	
	

}
