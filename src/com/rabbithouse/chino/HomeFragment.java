package com.rabbithouse.chino;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link HomeFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link HomeFragment#newInstance} factory method to create an instance of this
 * fragment.
 *
 */
public class HomeFragment extends Fragment
{
	Switch beaconSwitch;
	ImageView iconView;
	
	// GestureDetectorインスタンス変数
	GestureDetector gestureDetector;
	
	public static HomeFragment newInstance()
	{
		HomeFragment fragment = new HomeFragment();
		Bundle args = new Bundle();
		return fragment;
	}

	public HomeFragment()
	{
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		
	}
	
	@Override
	public void onPause()
	{
		DataConnector.saveBeaconSearchEnabled(getActivity(), beaconSwitch.isChecked());
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		
		gestureDetector = new GestureDetector(view.getContext(), new OnGestureListener()
		{	
			@Override
			public boolean onSingleTapUp(MotionEvent e)
			{
				beaconSwitch.setChecked(!beaconSwitch.isChecked());
				return true;
			}
			
			@Override
			public void onShowPress(MotionEvent e) {}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return true; }
			
			@Override
			public void onLongPress(MotionEvent e) { }
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { return true; }
			
			@Override
			public boolean onDown(MotionEvent e) { return true; }
		});
		
		iconView = (ImageView)view.findViewById(R.id.imageView_home_icon);
		iconView.setOnTouchListener(new OnTouchListener()
		{	
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				return gestureDetector.onTouchEvent(event);
			}

		});
		
		beaconSwitch = (Switch)view.findViewById(R.id.switch_home_search);
		beaconSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked)
			{
				if(isChecked)
				{
					iconView.setImageResource(R.drawable.icon_chino);
					getActivity().startService(new Intent(getActivity(), BeaconSearchService.class));
				}
				else
				{
					iconView.setImageResource(R.drawable.icon_chino_mono);
					getActivity().stopService(new Intent(getActivity(), BeaconSearchService.class));
				}
			}
		});
		
		// スイッチの状態をセット
		boolean enable = DataConnector.loadBeaconSearchEnabled(getActivity());
		beaconSwitch.setChecked(enable);
		if (!enable) iconView.setImageResource(R.drawable.icon_chino_mono);
		
		return view;
	}
}
