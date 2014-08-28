package com.rabbithouse.chino;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataConnector
{
	/**
	 * iBeacon�̐M�������m���Ă��邩���Ă��Ȃ����̏�Ԃ�ۑ�
	 * @param context �f�[�^���������ރR���e�L�X�g
	 * @param enable�@iBeacon���m�̏��
	 */
	public static void saveBeaconSearchEnabled(Context context, boolean enable)
	{
		SharedPreferences pref = context.getSharedPreferences("iniData", Context.MODE_PRIVATE );
		
		// �v���t�@�����X�ɏ������ނ��߂�Editor�I�u�W�F�N�g�擾 //
        Editor editor = pref.edit();
        
        // ��������
        editor.putBoolean("beaconSearchEnabled", enable);
        editor.commit();
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean loadBeaconSearchEnabled(Context context)
	{
		SharedPreferences pref = context.getSharedPreferences("iniData", Context.MODE_PRIVATE );
		
		return pref.getBoolean("beaconSearchEnabled", false);
	}
	
	public static void saveCategoryCheck(Context context, HashMap<String, Boolean> map)
	{
		SharedPreferences pref = context.getSharedPreferences("iniData", Context.MODE_PRIVATE );
		
		// �v���t�@�����X�ɏ������ނ��߂�Editor�I�u�W�F�N�g�擾
        Editor editor = pref.edit();
        
        // ��������
        editor.putBoolean("fasion", map.get("fasion"));
        editor.putBoolean("hobby", map.get("hobby"));
        editor.putBoolean("restaurant", map.get("restaurant"));
        editor.putBoolean("grocery", map.get("grocery"));
        editor.putBoolean("daily", map.get("daily"));
        editor.putBoolean("interior", map.get("interior"));
        editor.putBoolean("service", map.get("service"));
        editor.putBoolean("etc", map.get("etc"));
        
        editor.commit();
	}
	
	public static HashMap<String, Boolean> loadCategoryCheckList(Context context)
	{
		SharedPreferences pref = context.getSharedPreferences("iniData", Context.MODE_PRIVATE );
		
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		map.put("fasion", pref.getBoolean("fasion", false));
		map.put("hobby", pref.getBoolean("hobby", false));
		map.put("restaurant", pref.getBoolean("restaurant", false));
		map.put("grocery", pref.getBoolean("grocery", false));
		map.put("daily", pref.getBoolean("daily", false));
		map.put("interior", pref.getBoolean("interior", false));
		map.put("service", pref.getBoolean("service", false));
		map.put("etc", pref.getBoolean("etc", false));
		
		return map;
	}
	
	public static int getCategoryIcon(String name)
	{
		if(name.equals("�t�@�b�V����"))
		{
			return R.drawable.icon_fasion;
		}
		else if(name.equals("�O�b�Y"))
		{
			return R.drawable.icon_hobby;
		}
		else if(name.equals("���H�X"))
		{
			return R.drawable.icon_restaurant;
		}
		else if(name.equals("�H���i"))
		{
			return R.drawable.icon_grocery;
		}
		else if(name.equals("���p�G��"))
		{
			return R.drawable.icon_daily_necessities;
		}
		else if(name.equals("�Ƌ�E�C���e���A"))
		{
			return R.drawable.icon_interior;
		}
		else if(name.equals("�T�[�r�X"))
		{
			return R.drawable.icon_service;
		}
		else if(name.equals("���̑�"))
		{
			return R.drawable.icon_etc;
		}
		return R.drawable.ic_launcher;
	}
}
