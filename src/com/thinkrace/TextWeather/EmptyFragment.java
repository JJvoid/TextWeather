package com.thinkrace.TextWeather;

import android.annotation.SuppressLint;
import com.thinkrace.TextWeather.R;
import android.app.Activity;
import android.app.Fragment;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;


@SuppressLint("NewApi") public class EmptyFragment extends Fragment {
	
	private ImageButton refresh;
	InternetStateChanged changed;
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.empty_fragment, container,false);
		refresh = (ImageButton)view.findViewById(R.id.refresh);
		refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ConnectivityManager con = (ConnectivityManager)getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
				boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
				boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
				if(!wifi&&!internet){
					Toast.makeText(getActivity(), "没网你刷新个毛啊", Toast.LENGTH_LONG).show();
				}else{
					changed.stateChangeListener(true);
				}
			}
		});
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		changed = (InternetStateChanged)activity;
	}
	
	public interface InternetStateChanged{
		void stateChangeListener(boolean InternetState);
	}
	

}
