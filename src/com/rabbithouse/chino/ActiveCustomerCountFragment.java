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
	// 店の詳細情報
	StoreDetail storeDetail;
	
	// 店舗にいるChinoユーザの人数を表示するTextView
	TextView textView_count;
	
	// 店舗にいるChinoユーザの人数を更新するためのタイマーとハンドラ
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
		// タイマー開始
		timer.schedule(new TimerTask()
		{
	        @Override
	        public void run()
	        {
	            // mHandlerを通じてUI Threadへ処理をキューイング
	            handler.post( new Runnable()
	            {
	                public void run()
	                {
	                	// サーバに店舗にいるChinoユーザの人数を問い合わせる
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
		// タイマーをストップ
		timer.cancel();
		
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_active_customer_count, container, false);
		
		// タイトルの表示名を変える
		TextView title = (TextView)view.findViewById(R.id.textView_userCount_title);
		title.setText("[" + storeDetail.Name + "]にいるChinoユーザの人数");
		
		return view;
	}
	
	

}
