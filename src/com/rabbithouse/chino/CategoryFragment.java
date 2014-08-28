package com.rabbithouse.chino;

import java.util.HashMap;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
	CheckBox fasion;
	CheckBox hobby;
	CheckBox restaurant;
	CheckBox grocery;
	CheckBox daily;
	CheckBox interior;
	CheckBox service;
	CheckBox etc;

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

	}
	
	@Override
	public void onStop()
	{
		// TODO: ÉJÉeÉSÉäÇÃï€ë∂
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		map.put("fasion", fasion.isChecked());
		map.put("hobby", hobby.isChecked());
		map.put("restaurant", restaurant.isChecked());
		map.put("grocery", grocery.isChecked());
		map.put("daily", daily.isChecked());
		map.put("interior", interior.isChecked());
		map.put("service", service.isChecked());
		map.put("etc", etc.isChecked());
		
		DataConnector.saveCategoryCheck(getActivity(), map);
		
		super.onStop();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_category, container, false);
		
		// TODO: ÉJÉeÉSÉäÇì«Ç›çûÇ›
		HashMap<String, Boolean> map = DataConnector.loadCategoryCheckList(getActivity());

		fasion = ((CheckBox)view.findViewById(R.id.checkBox_category_fasion));
		fasion.setChecked(map.get("fasion"));
		
		hobby = ((CheckBox)view.findViewById(R.id.checkBox_category_hobby));
		hobby.setChecked(map.get("hobby"));
		
		restaurant = ((CheckBox)view.findViewById(R.id.checkBox_category_restaurant));
		restaurant.setChecked(map.get("restaurant"));
		
		grocery = ((CheckBox)view.findViewById(R.id.checkBox_category_grocery));
		grocery.setChecked(map.get("grocery"));
		
		daily = ((CheckBox)view.findViewById(R.id.checkBox_category_dailyNecessities));
		daily.setChecked(map.get("daily"));
		
		interior = ((CheckBox)view.findViewById(R.id.checkBox_category_interior));
		interior.setChecked(map.get("interior"));
		
		service = ((CheckBox)view.findViewById(R.id.checkBox_category_service));
		service.setChecked(map.get("service"));
		
		etc = ((CheckBox)view.findViewById(R.id.checkBox_category_etc));
		etc.setChecked(map.get("etc"));
		
		return view;
	}

}
