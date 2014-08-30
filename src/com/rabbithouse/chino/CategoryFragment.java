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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link CategoryFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link CategoryFragment#newInstance} factory method to create an instance of
 * this fragment.
 *
 */
public class CategoryFragment extends Fragment implements OnCheckedChangeListener
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
		// カテゴリの保存
		saveCategory();
		
		super.onStop();
	}
	
	private void saveCategory()
	{
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		map.put("ファッション", fasion.isChecked());
		map.put("グッズ", hobby.isChecked());
		map.put("飲食店", restaurant.isChecked());
		map.put("食料品", grocery.isChecked());
		map.put("日用雑貨", daily.isChecked());
		map.put("家具・インテリア", interior.isChecked());
		map.put("サービス", service.isChecked());
		map.put("その他", etc.isChecked());
		
		DataConnector.saveCategoryCheck(getActivity(), map);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_category, container, false);
		
		// TODO: カテゴリを読み込み
		HashMap<String, Boolean> map = DataConnector.loadCategoryCheckList(getActivity());

		fasion = ((CheckBox)view.findViewById(R.id.checkBox_category_fasion));
		fasion.setTag("ファッション");
		
		hobby = ((CheckBox)view.findViewById(R.id.checkBox_category_hobby));
		hobby.setTag("グッズ");
		
		restaurant = ((CheckBox)view.findViewById(R.id.checkBox_category_restaurant));
		restaurant.setTag("飲食店");
		
		grocery = ((CheckBox)view.findViewById(R.id.checkBox_category_grocery));
		grocery.setTag("食料品");
		
		daily = ((CheckBox)view.findViewById(R.id.checkBox_category_dailyNecessities));
		daily.setTag("日用雑貨");
		
		interior = ((CheckBox)view.findViewById(R.id.checkBox_category_interior));
		interior.setTag("家具・インテリア");
		
		service = ((CheckBox)view.findViewById(R.id.checkBox_category_service));
		service.setTag("サービス");
		
		etc = ((CheckBox)view.findViewById(R.id.checkBox_category_etc));
		etc.setTag("その他");
		
		fasion.setOnCheckedChangeListener(this);
		hobby.setOnCheckedChangeListener(this);
		restaurant.setOnCheckedChangeListener(this);
		grocery.setOnCheckedChangeListener(this);
		daily.setOnCheckedChangeListener(this);
		interior.setOnCheckedChangeListener(this);
		service.setOnCheckedChangeListener(this);
		etc.setOnCheckedChangeListener(this);
		
		fasion.setChecked(map.get("ファッション"));
		hobby.setChecked(map.get("グッズ"));
		restaurant.setChecked(map.get("飲食店"));
		grocery.setChecked(map.get("食料品"));
		daily.setChecked(map.get("日用雑貨"));
		interior.setChecked(map.get("家具・インテリア"));
		service.setChecked(map.get("サービス"));
		etc.setChecked(map.get("その他"));
		
		return view;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		int id = 0;
		if(isChecked) id = DataConnector.getCategoryIcon((String)buttonView.getTag());
		else id = DataConnector.getCategoryMonoIcon((String)buttonView.getTag());
		buttonView.setCompoundDrawablesRelativeWithIntrinsicBounds(id, 0, 0, 0);
		saveCategory();
	}

}
