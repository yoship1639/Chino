package com.rabbithouse.chino;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link CategoryFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link CategoryFragment#newInstance} factory method to create an instance of
 * this fragment.
 *
 */
public class CategoryFragment extends Fragment
{

	public static CategoryFragment newInstance()
	{
		CategoryFragment fragment = new CategoryFragment();
		Bundle args = new Bundle();
		return fragment;
	}

	public CategoryFragment()
	{
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// TODO: カテゴリを読み込み
	}
	
	@Override
	public void onStop()
	{
		// TODO: カテゴリの保存
		
		super.onStop();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_category, container, false);
		
		return view;
	}

}
