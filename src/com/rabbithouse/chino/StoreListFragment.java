package com.rabbithouse.chino;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link StoreListFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link StoreListFragment#newInstance} factory method to create an instance of
 * this fragment.
 *
 */
public class StoreListFragment extends Fragment
{
	StoreInfo[] storeInfos;

	public static StoreListFragment newInstance(StoreInfo[] infos)
	{
		StoreListFragment fragment = new StoreListFragment();
		Bundle args = new Bundle();
		for(int i=0; i<infos.length; i++)
		{
			args.putParcelable("StoreInfo" + i, infos[i]);
		}
		args.putInt("StoreInfoNum", infos.length);
		fragment.setArguments(args);
		return fragment;
	}

	public StoreListFragment()
	{
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
			int num = getArguments().getInt("StoreInfoNum");
			storeInfos = new StoreInfo[num];
			for(int i=0; i<num; i++)
			{
				storeInfos[i] = (StoreInfo)getArguments().getParcelable("StoreInfo" + i);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_store_list, container, false);
		
		ListView listView = (ListView)view.findViewById(R.id.listView_storeList);
		
		return view;
	}

}
