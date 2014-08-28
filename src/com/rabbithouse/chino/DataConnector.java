package com.rabbithouse.chino;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataConnector
{
	/**
	 * iBeaconの信号を検知しているかしていないかの状態を保存
	 * @param context データを書き込むコンテキスト
	 * @param enable　iBeacon検知の状態
	 */
	public static void saveBeaconSearchEnabled(Context context, boolean enable)
	{
		SharedPreferences pref = context.getSharedPreferences("iniData", Context.MODE_PRIVATE );
		
		// プリファレンスに書き込むためのEditorオブジェクト取得 //
        Editor editor = pref.edit();
        
        // 書き込み
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
		
		// プリファレンスに書き込むためのEditorオブジェクト取得
        Editor editor = pref.edit();
        
        // 書き込み
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
		if(name.equals("ファッション"))
		{
			return R.drawable.icon_fasion;
		}
		else if(name.equals("グッズ"))
		{
			return R.drawable.icon_hobby;
		}
		else if(name.equals("飲食店"))
		{
			return R.drawable.icon_restaurant;
		}
		else if(name.equals("食料品"))
		{
			return R.drawable.icon_grocery;
		}
		else if(name.equals("日用雑貨"))
		{
			return R.drawable.icon_daily_necessities;
		}
		else if(name.equals("家具・インテリア"))
		{
			return R.drawable.icon_interior;
		}
		else if(name.equals("サービス"))
		{
			return R.drawable.icon_service;
		}
		else if(name.equals("その他"))
		{
			return R.drawable.icon_etc;
		}
		return R.drawable.ic_launcher;
	}
}
