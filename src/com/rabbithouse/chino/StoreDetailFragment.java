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
			// �X�ܖ����Z�b�g
			((TextView)view.findViewById(R.id.TextView_store_detail_name)).setText(storeDetail.Name);
			
			// �Z�[���X�e�L�X�g���Z�b�g
			((TextView)view.findViewById(R.id.TextView_store_detail_sales)).setText(storeDetail.SalesText);
			
			// �ڍ׏����Z�b�g
			WebView web = (WebView)view.findViewById(R.id.webView_store_detail_detail);
			//web.getSettings().setJavaScriptEnabled(true);
			//web.loadUrl("http://www.etecminds.com/");
			web.loadData(storeDetail.Detail, "text/html; charset=utf-8", "utf-8");
			
			
			// �����N�{�^�����������Ƃ��̏������Z�b�g
			Button lnkBtn = (Button)view.findViewById(R.id.button_store_detail_link);
			if(storeDetail.URL != null && !storeDetail.URL.isEmpty())
			{
				lnkBtn.setOnClickListener(new View.OnClickListener()
				{
					// �{�^���������ꂽ�Ƃ�
		            @Override
		            public void onClick(View v)
		            {
		            	// �X��URL�ɔ��
		            	startActivity(new Intent( Intent.ACTION_VIEW, Uri.parse(storeDetail.URL)));
		            }
		        });
			}
			else lnkBtn.setVisibility(Button.INVISIBLE);
			
			// �X�V����
			((TextView)view.findViewById(R.id.textView_storeDetail_updateDate)).setText("�X�V����: " + storeDetail.UpdateDate);
			
			
		}
		
		// �z�[���ɖ߂�
		Button homeBtn = (Button)view.findViewById(R.id.button_storeDetail_back);
		homeBtn.setOnClickListener(new View.OnClickListener()
		{
			// �{�^���������ꂽ�Ƃ�
            @Override
            public void onClick(View v)
            {
            	// �X��URL�ɔ��
            	Intent intent = new Intent();
            	intent.setClassName("com.rabbithouse.chino", "com.rabbithouse.chino.HomeActivity");
            	startActivity(intent);
            	getActivity().finish();
            }
        });
		
		return view;
	}

}
