package com.example.isweixin.use;

import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocalDayWeatherForecast;
import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.example.isweixin.R;

/**
 * 未来三天天气预报
 * */
public class FutureWeatherReportActivity extends Activity implements
		AMapLocalWeatherListener {

	private TextView mWeatherLocationTextView;// 天气预报地点
	private TextView mTodayTimeTextView;//
	private TextView mTomorrowTimeTextView;//
	private TextView mNextDayTimeTextView;//

	private TextView mTodayWeatherTextView;// 今天天气状况
	private TextView mTomorrowWeatherTextView;// 明天天气状况
	private TextView mNextDayWeatherTextView;// 后天天气状况

	private LocationManagerProxy mLocationManagerProxy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题�?
		setContentView(R.layout.activity_future_weather_report);
		initView();
		init();
	}

	private void init() {
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		// 获取未来天气预报
		// 如果�?要同时请求实时�?�未来三天天气，请确保定位获取位置后使用,分开调用，可忽略本句�?
		mLocationManagerProxy.requestWeatherUpdates(
				LocationManagerProxy.WEATHER_TYPE_FORECAST, this);

	}

	private void initView() {

		mWeatherLocationTextView = (TextView) findViewById(R.id.future_weather_location_text);

		mTodayTimeTextView = (TextView) findViewById(R.id.today_time_text);
		mTodayWeatherTextView = (TextView) findViewById(R.id.today_weather_des_text);
		mTomorrowTimeTextView = (TextView) findViewById(R.id.tomorrow_time_text);
		mTomorrowWeatherTextView = (TextView) findViewById(R.id.tomorrow_weather_des_text);
		mNextDayTimeTextView = (TextView) findViewById(R.id.netx_day_time_text);
		mNextDayWeatherTextView = (TextView) findViewById(R.id.netx_day_des_text);

	}

	@Override
	public void onWeatherForecaseSearched(
			AMapLocalWeatherForecast aMapLocalWeatherForecast) {
		// 未来天气预报回调
		if (aMapLocalWeatherForecast != null
				&& aMapLocalWeatherForecast.getAMapException().getErrorCode() == 0) {

			List<AMapLocalDayWeatherForecast> forcasts = aMapLocalWeatherForecast
					.getWeatherForecast();
			for (int i = 0; i < forcasts.size(); i++) {
				AMapLocalDayWeatherForecast forcast = forcasts.get(i);
				switch (i) {
				case 0:
					mWeatherLocationTextView.setText(forcast.getCity());
					mTodayTimeTextView.setText("���� (  " + forcast.getDate()
							+ " )");
					mTodayWeatherTextView.setText(forcast.getDayWeather()
							+ "    " + forcast.getDayTemp() + "��/"
							+ forcast.getNightTemp() + "��    "
							+ forcast.getDayWindPower() + "��");

					break;
				case 1:
					mTomorrowTimeTextView.setText("���� ( " + forcast.getDate()
							+ " )");
					mTomorrowWeatherTextView.setText(forcast.getDayWeather()
							+ "    " + forcast.getDayTemp() + "��/"
							+ forcast.getNightTemp() + "��   "
							+ forcast.getDayWindPower() + "��");
					break;
				case 2:
					mNextDayTimeTextView.setText("���� ( " + forcast.getDate()
							+ " )");
					mNextDayWeatherTextView.setText(forcast.getDayWeather()
							+ "    " + forcast.getDayTemp() + "��/"
							+ forcast.getNightTemp() + "��   "
							+ forcast.getDayWindPower() + "��");
					break;
				}
			}
		} else {

			// 获取天气预报失败
			Toast.makeText(
					this,
					"��ȡ����Ԥ��ʧ��:"
							+ aMapLocalWeatherForecast.getAMapException()
									.getErrorMessage(), Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onWeatherLiveSearched(AMapLocalWeatherLive arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPause() {
		super.onPause();
		// �?毁定�?
		mLocationManagerProxy.destroy();
	}

	protected void onDestroy() {
		super.onDestroy();

	}
}
