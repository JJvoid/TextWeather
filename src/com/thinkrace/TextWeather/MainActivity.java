package com.thinkrace.TextWeather;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.thinkrace.TextWeather.EmptyFragment.InternetStateChanged;
import com.thinkrace.TextWeather.SearchFragment.CallBackValue;
@SuppressLint("NewApi")
public class MainActivity extends Activity implements CallBackValue,
		InternetStateChanged {

	private final static String APIKEY = "60b80bce0e96df9c323876a356fb89e6";
	RelativeLayout layout;

	private ListView listView;
	private ArrayAdapter<String> adapter;
	List<Fragment> fragmentList = new ArrayList<Fragment>();

	private SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	SearchFragment searchFragment;
	EmptyFragment fragment;

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Toast.makeText(getApplicationContext(), "", 1).show();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);

		layout = (RelativeLayout) findViewById(R.id.relative_layout);
		sharedPreferences = getSharedPreferences("CURRENTCITY", MODE_PRIVATE);
		editor = sharedPreferences.edit();

		ConnectivityManager con = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnected();
		boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnected();

		if (!wifi && !internet) {
			fragment = new EmptyFragment();
			FragmentManager fm = getFragmentManager();
			FragmentTransaction transaction = fm.beginTransaction();
			transaction.replace(R.id.relative_layout, fragment).commit();
			Toast.makeText(this, "网络故障", Toast.LENGTH_LONG).show();

		} else {
			if (sharedPreferences.contains("CITY")) {
				WeatherBean bean = new WeatherBean();
				bean.setCounty(sharedPreferences.getString("CITY", "深圳"));
				bean.setAreaId(sharedPreferences.getInt("CITYCODE", 101010100));
				RequestWeatherTask task = new RequestWeatherTask(bean);
				task.execute();
			} else {
				WeatherBean bean = new WeatherBean();
				bean.setCounty("青岛");
				bean.setTime("11:00");
				bean.setMin_tem("25º");
				bean.setWindStrength("2级");
				bean.setWindDirection("东南风");
				bean.setAreaId(10111100);
				bean.setMax_tem("32º");
				bean.setTemperature("30º");
				bean.setWeather("多云");
				bean.setProvince("山东省");
				bean.setYesType("多云");
				bean.setYesLowTemp("25度");
				bean.setYesHighTemp("32度");
				bean.setTomType("中雨");
				bean.setTomLowTemp("23度");
				bean.setTomHighTemp("28度");
				bean.setTdatType("小雨");
				bean.setTdatLowTemp("25度");
				bean.setTdatHighTemp("30度");
				MainFragment main = new MainFragment(bean);
				fragmentList.add(main);
			}
		}
		listView = (ListView) findViewById(R.id.view_pager);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				MainActivity.this, android.R.layout.simple_list_item_1);
		listView.setAdapter(adapter);
		
		initSystemBar(this);
		android.widget.Toolbar toolbar = (android.widget.Toolbar)findViewById(R.id.toolbar);
		setActionBar(toolbar);
	}
	
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_main, menu);
		MenuItem item = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView)MenuItemCompat.getActionView(item);
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				searchFragment = new SearchFragment();
				searchFragment.setINPUT(query);
				getFragmentManager().beginTransaction().replace(R.id.relative_layout, searchFragment).commit();
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				searchFragment = new SearchFragment();
				searchFragment.setINPUT(newText);
				getFragmentManager().beginTransaction().replace(R.id.relative_layout, searchFragment).commit();
				return true;
			}
		});
		return true;
	}
	
//	public boolean onOptionsItemSelected(MenuItem item){
//		switch(item.getItemId()){
//		case R.id.action_settings:
//			break;
//		case R.id.action_search:
//			break;
//		case R.id.share:
//			break;
//		}
//		return super.onOptionsItemSelected(item);
//	}
	
	public static void initSystemBar(Activity activity){
		if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT){
			setTranslucentStatus(activity,true);
		}
	}
	
	private static void setTranslucentStatus(Activity activity, boolean on){
		Window win = activity.getWindow();
	WindowManager.LayoutParams winParams = win.getAttributes();
	final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
	if(on){
		winParams.flags |= bits;
	}else{
		winParams.flags&= ~bits;
	}
	win.setAttributes(winParams);
	}

	public void sendValue(WeatherBean bean){
		editor.putString("CITY", bean.getCounty());
		editor.putInt("CITYCODE", bean.getAreaId());
		editor.commit();
		RequestWeatherTask task = new RequestWeatherTask(bean);
		task.execute();
	}
	
	public void stateChangeListener(boolean InternetState){
		if(InternetState){
			 getFragmentManager().beginTransaction().remove(fragment).commit();
			 sharedPreferences = getSharedPreferences("CURRENTCITY", MODE_PRIVATE);
			 editor = sharedPreferences.edit();
			 if(sharedPreferences.contains("CITY")){
				 WeatherBean bean = new WeatherBean();
				 bean.setCounty(sharedPreferences.getString("CITY","北京"));
	                bean.setAreaId(sharedPreferences.getInt("CITYCODE",101010100));
	                RequestWeatherTask task = new RequestWeatherTask(bean);
	                task.execute();
			 }else {
	                WeatherBean bean = new WeatherBean();
	                bean.setCounty("青岛");
	                bean.setTime("11:00");
	                bean.setMin_tem("25º");
	                bean.setWindStrength("2级");
	                bean.setWindDirection("东南风");
	                bean.setAreaId(10111100);
	                bean.setMax_tem("32º");
	                bean.setTemperature("30º");
	                bean.setWeather("多云");
	                bean.setProvince("山东省");
	                bean.setYesType("多云");
	                bean.setYesLowTemp("25度");
	                bean.setYesHighTemp("32度");
	                bean.setTomType("中雨");
	                bean.setTomLowTemp("23度");
	                bean.setTomHighTemp("28度");
	                bean.setTdatType("小雨");
	                bean.setTdatLowTemp("25度");
	                bean.setTdatHighTemp("30度");
	                MainFragment main = new MainFragment(bean);
	                fragmentList.add(main);
	                }
		}
	}
	
	private class RequestWeatherTask extends AsyncTask<Void, Void, WeatherBean>{
		
		private WeatherBean weatherBean;
		private int i = 0;
		
		public RequestWeatherTask(WeatherBean bean){
			this.weatherBean = bean;
		}

		@Override
		protected com.thinkrace.TextWeather.WeatherBean doInBackground(
				Void... params) {
			// TODO Auto-generated method stub
			String countyName = weatherBean.getCounty();
			int ID = weatherBean.getAreaId();
			String httpUrl = "http://apis.baidu.com/apistore/weatherservice/recentweathers?";
            String httpArg = "cityname="+URLEncoder.encode(countyName)+"&cityid="+ID;
            String url = httpUrl+httpArg;
            String response = HttpUtil.HttpGet(url, "GET");
			return parseJSON(response)	;
		}

		@Override
		protected void onPostExecute(
				com.thinkrace.TextWeather.WeatherBean bean) {
			// TODO Auto-generated method stub
			super.onPostExecute(bean);
			MainFragment fragment = new MainFragment(bean);
			fragmentList.add(fragment);
			adapter.notifyDataSetChanged();
			i++;
			listView.setSelector(i);
		}
	}
	private WeatherBean parseJSON(String response){
		WeatherBean weatherBean = new WeatherBean();
		try{
			JSONObject jsonObject = new JSONObject(response);
			int errNum = jsonObject.getInt("errNum");
			String errMsg = jsonObject.getString("errMsg");
			
			JSONObject retData = jsonObject.getJSONObject("retData");
			String county = retData.getString("city");
			JSONObject today = retData.getJSONObject("today");
			 String curTemp = today.getString("curTemp");
             //int PM2_5 = today.getInt("aqi");
             String fengxiang = today.getString("fengxiang");
             String fengli = today.getString("fengli");
             String hightemp = today.getString("hightemp");
             String lowtemp = today.getString("lowtemp");
             String type = today.getString("type");
             //明天和后天的天气信息
             JSONArray forecast = retData.getJSONArray("forecast");
             JSONObject tomObject = (JSONObject) forecast.get(1);
             String tomType = tomObject.getString("type");
             String tomHighTemp = tomObject.getString("hightemp");
             String tomLowTemp = tomObject.getString("lowtemp");
             JSONObject tdatObject = (JSONObject) forecast.get(2);
             String tdatType = tdatObject.getString("type");
             String tdatHighTemp = tdatObject.getString("hightemp");
             String tdatLowTemp = tdatObject.getString("lowtemp");
             //昨天的天气信息
             JSONArray history = retData.getJSONArray("history");
             JSONObject yesterday = history.getJSONObject(6);
             String yesType = yesterday.getString("type");
             String yesHighTemp = yesterday.getString("hightemp");
             String yesLowTemp = yesterday.getString("lowtemp");
             
             weatherBean.setCounty(county);
             weatherBean.setTemperature(curTemp);
             weatherBean.setWindDirection(fengxiang);
             weatherBean.setWindStrength(fengli);
             weatherBean.setMax_tem(hightemp);
             weatherBean.setMin_tem(lowtemp);
             weatherBean.setWeather(type);
             weatherBean.setYesType(yesType);
             weatherBean.setYesHighTemp(yesHighTemp);
             weatherBean.setYesLowTemp(yesLowTemp);
             weatherBean.setTomType(tomType);
             weatherBean.setTomHighTemp(tomHighTemp);
             weatherBean.setTomLowTemp(tomLowTemp);
             weatherBean.setTdatType(tdatType);
             weatherBean.setTdatHighTemp(tdatHighTemp);
             weatherBean.setTdatLowTemp(tdatLowTemp);
		}catch(JSONException e){
			e.printStackTrace();
		}
		return weatherBean;
	}
}
