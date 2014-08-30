package com.rabbithouse.chino;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
	ListView listView;
	
	public static StoreListFragment newInstance()
	{
		StoreListFragment fragment = new StoreListFragment();
		Bundle args = new Bundle();
		/*for(int i=0; i<infos.length; i++)
		{
			args.putParcelable("StoreInfo" + i, infos[i]);
		}
		args.putInt("StoreInfoNum", infos.length);*/
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
			/*
			int num = getArguments().getInt("StoreInfoNum");
			storeInfos = new StoreInfo[num];
			for(int i=0; i<num; i++)
			{
				storeInfos[i] = (StoreInfo)getArguments().getParcelable("StoreInfo" + i);
			}*/
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_store_list, container, false);
		
		listView = (ListView)view.findViewById(R.id.listView_storeList);
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				StoreInfo info = (StoreInfo)listView.getItemAtPosition(position);
				
				Intent intent = new Intent();
				intent.setClass(getActivity(), StoreDetailActivity.class);
				intent.putExtra("UUID", info.UUID);
				startActivity(intent);
			}
		});
		upadateListView();
		
		Button btn = (Button)view.findViewById(R.id.button_listView_update);
		btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				upadateListView();
			}
		});
		
		return view;
	}

	private void upadateListView()
	{
		StoreInfo[] infos = StoreDataConnector.loadStoreInfos(getActivity());
		
		ArrayAdapter<StoreInfo> adapter = new ArrayAdapter<StoreInfo>(getActivity(), android.R.layout.simple_list_item_single_choice);
		for (StoreInfo storeInfo : infos)
		{
			adapter.add(storeInfo);
		}

		listView.setAdapter(adapter);
	}
}
