package com.thinkrace.TextWeather;

import java.net.URLEncoder;import com.thinkrace.TextWeather.R;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


@SuppressLint("NewApi") public class SearchFragment extends DialogFragment {
	
	 private String INPUT;
	    List<WeatherBean> weatherBeenList = new ArrayList<WeatherBean>();
	    CallBackValue callBackValue;
	    ListView listView;
	    
	    public String getINPUT(){
	    	return INPUT;
	    }
	    
	    public void setINPUT(String INPUT){
	    	this.INPUT = INPUT;
	    }
	    
	    Handler handler = new Handler(){
	    	public void handleMessage(Message message){
	    		switch(message.what){
	    		case 0x123:
	    			weatherBeenList = (List<WeatherBean>)message.obj;
	    			displayInfo(weatherBeenList);
	    			break;
	    		}
	    	}
	    };
	    
	    private void displayInfo(List<WeatherBean> weatherBeanList){
	    	MyAdapter adapter = new MyAdapter(weatherBeanList);
	    	listView.setAdapter(adapter);
	    }

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.search_fragment, container,false);
			listView = (ListView)view.findViewById(R.id.list_view);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					WeatherBean mbean = (WeatherBean)parent.getItemAtPosition(position);//  获取下拉菜单中被选中的值
					callBackValue.sendValue(mbean);
				}
			});
			return view;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			String httpUrl = "http://apis.baidu.com/apistore/weatherservice/citylist?";
			String httpARg = "cityname=" + URLEncoder.encode(INPUT);
			String url = httpUrl + httpARg;
			parseJSON(url);
		}
		
		 @Override
		    public void onAttach(Activity activity) {
		        super.onAttach(activity);
		        callBackValue = (CallBackValue) activity;
		    }
		 
		 private void parseJSON(final String url){
			 new Thread(new Runnable(){
				 public void run(){
					 String response = HttpUtil.HttpGet(url,"GET");
					 try{
						 JSONObject jsonObject = new JSONObject(response);
						 int errNum = jsonObject.getInt("errNum");
						 String errMsg = jsonObject.getString("errMsg");
						 Log.e("ERROR", errMsg + " " +errNum);
						 
						 JSONArray retData = jsonObject.getJSONArray("retData");
						 List<WeatherBean> beans = new ArrayList<WeatherBean>();
						 for(int i = 0; i<retData.length();i++){
							 JSONObject object = retData.getJSONObject(i);
							 String Province = object.getString("province_cn");
							 String City = object.getString("district_cn");
							 String County = object.getString("name_cn");
							 int code = object.getInt("area_id");
							 WeatherBean weatherBean = new WeatherBean();
							 weatherBean.setProvince(Province);
							 weatherBean.setCity(City);
							 weatherBean.setCounty(County);
							 weatherBean.setAreaId(code);
							 beans.add(weatherBean);
						 }
						 Message message = new Message();
						 message.what = 0x123;
						 message.obj = beans;
						 handler.sendMessage(message);
					 }catch(JSONException e){
						 e.printStackTrace();
					 }
				 }
			 }).start();
		 }
		 
		 private class MyAdapter extends BaseAdapter{
			 private List<WeatherBean> weatherBeanList = new ArrayList<WeatherBean>();
			 public MyAdapter(List<WeatherBean> weatherBeanList){
				 this.weatherBeanList = weatherBeanList;
			 }
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return weatherBeanList.size();
			}
			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				WeatherBean weatherBean = weatherBeanList.get(position);
				return weatherBean;
			}
			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				ViewHolder viewHolder;
				WeatherBean bean = weatherBeanList.get(position);
				if(convertView == null){
					convertView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.search_fragment_listview, null);
					viewHolder = new ViewHolder();
					TextView county = (TextView)convertView.findViewById(R.id.county);
					TextView PCC = (TextView)convertView.findViewById(R.id.PCC);
					viewHolder.County = county;
					viewHolder.PCC = PCC;
					convertView.setTag(viewHolder);
				}else{
					viewHolder = (ViewHolder)convertView.getTag();
				}
				Typeface Segoe = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Segoe.TTF");
				Typeface SemiLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SegoeSemiLight.TTF");
				
				viewHolder.County.setTypeface(Segoe);
	            viewHolder.PCC.setTypeface(SemiLight);
	            viewHolder.County.setText(bean.getCounty());
	            viewHolder.PCC.setText(bean.getProvince()+"省 "+bean.getCity()+"市 ");
				return convertView;
			}
			
			public class ViewHolder{
				TextView County, PCC;
			}
			 
			 
		 }
	    
		 public interface CallBackValue{
			 void sendValue(WeatherBean bean);
		 }
	   

}
