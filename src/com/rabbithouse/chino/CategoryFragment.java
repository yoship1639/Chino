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
		// �J�e�S���̕ۑ�
		saveCategory();
		
		super.onStop();
	}
	
	private void saveCategory()
	{
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		map.put("�t�@�b�V����", fasion.isChecked());
		map.put("�O�b�Y", hobby.isChecked());
		map.put("���H�X", restaurant.isChecked());
		map.put("�H���i", grocery.isChecked());
		map.put("���p�G��", daily.isChecked());
		map.put("�Ƌ�E�C���e���A", interior.isChecked());
		map.put("�T�[�r�X", service.isChecked());
		map.put("���̑�", etc.isChecked());
		
		DataConnector.saveCategoryCheck(getActivity(), map);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_category, container, false);
		
		// TODO: �J�e�S����ǂݍ���
		HashMap<String, Boolean> map = DataConnector.loadCategoryCheckList(getActivity());

		fasion = ((CheckBox)view.findViewById(R.id.checkBox_category_fasion));
		fasion.setTag("�t�@�b�V����");
		
		hobby = ((CheckBox)view.findViewById(R.id.checkBox_category_hobby));
		hobby.setTag("�O�b�Y");
		
		restaurant = ((CheckBox)view.findViewById(R.id.checkBox_category_restaurant));
		restaurant.setTag("���H�X");
		
		grocery = ((CheckBox)view.findViewById(R.id.checkBox_category_grocery));
		grocery.setTag("�H���i");
		
		daily = ((CheckBox)view.findViewById(R.id.checkBox_category_dailyNecessities));
		daily.setTag("���p�G��");
		
		interior = ((CheckBox)view.findViewById(R.id.checkBox_category_interior));
		interior.setTag("�Ƌ�E�C���e���A");
		
		service = ((CheckBox)view.findViewById(R.id.checkBox_category_service));
		service.setTag("�T�[�r�X");
		
		etc = ((CheckBox)view.findViewById(R.id.checkBox_category_etc));
		etc.setTag("���̑�");
		
		fasion.setOnCheckedChangeListener(this);
		hobby.setOnCheckedChangeListener(this);
		restaurant.setOnCheckedChangeListener(this);
		grocery.setOnCheckedChangeListener(this);
		daily.setOnCheckedChangeListener(this);
		interior.setOnCheckedChangeListener(this);
		service.setOnCheckedChangeListener(this);
		etc.setOnCheckedChangeListener(this);
		
		fasion.setChecked(map.get("�t�@�b�V����"));
		hobby.setChecked(map.get("�O�b�Y"));
		restaurant.setChecked(map.get("���H�X"));
		grocery.setChecked(map.get("�H���i"));
		daily.setChecked(map.get("���p�G��"));
		interior.setChecked(map.get("�Ƌ�E�C���e���A"));
		service.setChecked(map.get("�T�[�r�X"));
		etc.setChecked(map.get("���̑�"));
		
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
