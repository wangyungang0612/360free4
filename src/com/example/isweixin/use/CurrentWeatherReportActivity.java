package com.example.isweixin.use;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;
import com.example.isweixin.R;

/**
 * 瀹炴椂澶╂皵绀轰緥 鑾峰彇褰撳墠鍦板尯澶╂皵鎯呭喌
 * */
public class CurrentWeatherReportActivity extends Activity implements
		AMapLocalWeatherListener {
	private LocationManagerProxy mLocationManagerProxy;

	private TextView mLocationTextView;// 鍦扮偣
	private TextView mWeatherTextView;// 澶╂皵
	private TextView mWeatherTemperatureTextView;// 姘旀俯
	private TextView mWindDirctionTextView;// 椋庡悜
	private TextView mWindPowerTextView;// 椋庡姏
	private TextView mAirHumidityTextView;// 绌烘皵婀垮害
	private TextView mWeatherPublishTextView;// 鍙戝竷鏃堕棿

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 涓嶆樉绀虹▼搴忕殑鏍囬鏍?
		setContentView(R.layout.activity_current_weather_report);
		init();
		initView();

	}

	private void init() {
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		// 鑾峰彇瀹炴椂澶╂皵棰勬姤
		// 濡傛灉闇?瑕佸悓鏃惰姹傚疄鏃躲?佹湭鏉ヤ笁澶╁ぉ姘旓紝璇风‘淇濆畾浣嶈幏鍙栦綅缃悗浣跨敤,鍒嗗紑璋冪敤锛屽彲蹇界暐鏈彞銆?
		mLocationManagerProxy.requestWeatherUpdates(
				LocationManagerProxy.WEATHER_TYPE_LIVE, this);

	}

	/**
	 * 鍒濆鍖栨帶浠?
	 */
	private void initView() {
		mLocationTextView = (TextView) findViewById(R.id.current_weather_location_text);
		mWeatherTextView = (TextView) findViewById(R.id.current_weather_weather_text);
		mWeatherTemperatureTextView = (TextView) findViewById(R.id.current_weather_temperature_text);
		mWindDirctionTextView = (TextView) findViewById(R.id.current_weather_wind_direction_text);
		mWindPowerTextView = (TextView) findViewById(R.id.current_weather_wind_power_text);
		mAirHumidityTextView = (TextView) findViewById(R.id.current_weather_air_humidity_text);
		mWeatherPublishTextView = (TextView) findViewById(R.id.current_weather_weather_publish_text);
	}

	@Override
	public void onWeatherForecaseSearched(AMapLocalWeatherForecast arg0) {

	}

	@Override
	public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {

		if (aMapLocalWeatherLive != null
				&& aMapLocalWeatherLive.getAMapException().getErrorCode() == 0) {
		
			mLocationTextView.setText(aMapLocalWeatherLive.getCity());
			mWeatherTextView.setText(aMapLocalWeatherLive.getWeather());
			mWeatherTemperatureTextView.setText(aMapLocalWeatherLive
					.getTemperature() + "℃");
			mWindDirctionTextView.setText(aMapLocalWeatherLive.getWindDir()
					+ "风");
			mWindPowerTextView.setText(aMapLocalWeatherLive.getWindPower()
					+ "级");
			mAirHumidityTextView.setText(aMapLocalWeatherLive.getHumidity()
					+ "%");
			mWeatherPublishTextView.setText(aMapLocalWeatherLive
					.getReportTime());
		} else {

			// 鑾峰彇澶╂皵棰勬姤澶辫触
			Toast.makeText(
					this,
					"获取天气预报失败:"
							+ aMapLocalWeatherLive.getAMapException()
									.getErrorMessage(), Toast.LENGTH_SHORT)
					.show();

		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		// 閿?姣佸畾浣?
		mLocationManagerProxy.destroy();
	}

	protected void onDestroy() {
		super.onDestroy();

	}
}
