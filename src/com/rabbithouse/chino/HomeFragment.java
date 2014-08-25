package com.rabbithouse.chino;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		
		// TODO: スウィッチを切り替えたとき
		Switch sw = (Switch)view.findViewById(R.id.switch_home_search);
		
		return view;
	}
}
