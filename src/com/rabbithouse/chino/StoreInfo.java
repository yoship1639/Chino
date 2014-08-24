package com.rabbithouse.chino;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ステータス通知に通達する店舗データ
 * @author yoship
 */
public class StoreInfo implements Parcelable
{
	public String Name;
	public String Category;
	public String SalesText;
	public String UUID;
	
	@Override
	public int describeContents() { return 0; }
	
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(Name);
		dest.writeString(Category);
		dest.writeString(SalesText);
		dest.writeString(UUID);
	}
	
}
