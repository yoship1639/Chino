package com.rabbithouse.chino;

import android.os.Parcel;

/**
 * 店舗の詳細情報を格納したデータ
 * @author yoship
 */
public class StoreDetail extends StoreInfo
{
	public String Detail;
	public String URL;
	public String UpdateDate;
	
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		super.writeToParcel(dest, flags);
		dest.writeString(Detail);
		dest.writeString(URL);
		dest.writeString(UpdateDate);
	}
}
