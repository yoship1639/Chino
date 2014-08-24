package com.rabbithouse.chino;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Fragment;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link StoreDetailFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link StoreDetailFragment#newInstance} factory method to create an instance
 * of this fragment.
 *
 */
public class StoreDetailFragment extends Fragment 
{
	StoreDetail storeDetail;

	public static StoreDetailFragment newInstance(StoreDetail storeDetail)
	{
		StoreDetailFragment fragment = new StoreDetailFragment();
		Bundle args = new Bundle();
		args.putParcelable("StoreDetail", storeDetail);
		fragment.setArguments(args);
		return fragment;
	}

	public StoreDetailFragment() {
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_store_detail, container, false);
		
		if(storeDetail != null)
		{
			// 店舗名をセット
			((TextView)view.findViewById(R.id.TextView_store_detail_name)).setText(storeDetail.Name);
			
			// セールステキストをセット
			((TextView)view.findViewById(R.id.TextView_store_detail_sales)).setText(storeDetail.SalesText);
			
			// 詳細情報をセット
			WebView web = (WebView)view.findViewById(R.id.webView_store_detail_detail);
			web.loadData(storeDetail.Detail, "text/html", "utf-8");
			
			// リンクボタンを押したときの処理をセット
			if(storeDetail.URL != null && !storeDetail.URL.isEmpty())
			{
				Button lnkBtn = (Button)view.findViewById(R.id.button_store_detail_link);
				lnkBtn.setOnClickListener(new View.OnClickListener()
				{
					// ボタンが押されたとき
		            @Override
		            public void onClick(View v)
		            {
		            	// 店のURLに飛ぶ
		            	startActivity(new Intent( Intent.ACTION_VIEW, Uri.parse(storeDetail.URL)));
		            }
		        });
			}
		}
		
		return view;
	}

}
