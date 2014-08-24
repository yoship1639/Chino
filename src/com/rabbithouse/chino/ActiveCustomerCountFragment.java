package com.rabbithouse.chino;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
	StoreDetail storeDetail;

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
	}
	
	@Override
	public void onResume()
	{
		
		super.onResume();
	}
	
	@Override
	public void onPause()
	{
		
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_active_customer_count, container, false);
	}
	
	

}
