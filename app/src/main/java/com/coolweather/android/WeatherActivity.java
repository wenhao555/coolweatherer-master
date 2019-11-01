package com.coolweather.android;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.gson.Weather2;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity
{

    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private Button navButton;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;

    private String mWeatherId;

    //Baidu LBS
    public LocationClient mLocationClient;

    private TextView positionText;

    //弹窗提示定位
    AlertDialog alertLocation;

    //通过districtcode.json查询得到的地区代码
    public String districtCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21)
        {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener(this));

        setContentView(R.layout.activity_weather);

        // 初始化各控件
        //Baidu LBS
        positionText = (TextView) findViewById(R.id.position_text_view);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(WeatherActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!permissionList.isEmpty())
        {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(WeatherActivity.this, permissions, 1);
        } else
        {
            requestLocation();
        }


        //其他
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (titleCity.getText().toString().equals(""))
        {
            titleCity.setText("长沙");
        }
        getWeather(titleCity.getText().toString());
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
//                requestWeather(mWeatherId);
                getWeather(titleCity.getText().toString());
            }
        });
        navButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    private void requestLocation()
    {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation()
    {
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0)
                {
                    for (int result : grantResults)
                    {
                        if (result != PackageManager.PERMISSION_GRANTED)
                        {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else
                {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:

        }
    }

    private String getDistrictCode(BDLocation location)
    {
        //处理字符串，去掉“省”、“市”和“区”字，以满足districtcode.json的格式需求
        String resultCode = null;
        String provinceNameR = location.getProvince();
        String cityNameR = location.getCity();
        String districtNameR = location.getDistrict();
        String provinceName = provinceNameR.substring(0, provinceNameR.length() - 1);
        String cityName = cityNameR.substring(0, cityNameR.length() - 1);
        String districtName = districtNameR.substring(0, districtNameR.length() - 1);


        //从citycode.json中查询所在地的代码
        try
        {
            InputStreamReader isr = new InputStreamReader(getAssets().open("districtcode.json"), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null)
            {
                builder.append(line);
            }
            br.close();
            isr.close();

            //builder读取了JSON中的数据
            JSONObject citycode = new JSONObject(builder.toString());
            JSONArray provinceArray = citycode.getJSONArray("zone");         //从JSONObject中取出数组对象
            for (int i = 0; i < provinceArray.length(); i++)
            {
                JSONObject province = provinceArray.getJSONObject(i);

                if (province.getString("name").equals(provinceName))
                {
                    JSONArray cityArray = province.getJSONArray("zone");
                    for (int j = 0; j < cityArray.length(); j++)
                    {
                        JSONObject city = cityArray.getJSONObject(j);

                        if (city.getString("name").equals(cityName))
                        {
                            JSONArray districtArray = city.getJSONArray("zone");
                            for (int k = 0; k < districtArray.length(); k++)
                            {
                                JSONObject district = districtArray.getJSONObject(k);

                                if (district.getString("name").equals(districtName))
                                {
                                    resultCode = "CN" + district.getString("code");
                                    break;
                                }
                            }
                            //如果没有对应的地区代码，则查询所在城市代码
                            if (resultCode == null)
                            {
                                for (int k = 0; k < districtArray.length(); k++)
                                {
                                    JSONObject district = districtArray.getJSONObject(k);
                                    if (district.getString("name").equals(cityName))
                                    {
                                        resultCode = "CN" + district.getString("code");
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                    break;
                }

            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return resultCode;
    }

    public class MyLocationListener implements BDLocationListener
    {
        Context context;

        public MyLocationListener(Context context)
        {
            this.context = context;
        }

        @Override
        public void onReceiveLocation(final BDLocation location)
        {

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    StringBuilder currentPosition = new StringBuilder();
                    currentPosition.append("纬度：").append(location.getLatitude()).append("\n");
                    currentPosition.append("经度：").append(location.getLongitude()).append("\n");
                    currentPosition.append("国家：").append(location.getCountry()).append("\n");
                    currentPosition.append("省：").append(location.getProvince()).append("\n");
                    currentPosition.append("市：").append(location.getCity()).append("\n");
                    currentPosition.append("区：").append(location.getDistrict()).append("\n");
                    currentPosition.append("街道：").append(location.getStreet()).append("\n");
                    currentPosition.append("定位方式：");
                    if (location.getLocType() == BDLocation.TypeGpsLocation)
                    {
                        currentPosition.append("GPS\n");
                    } else if (location.getLocType() == BDLocation.TypeNetWorkLocation)
                    {
                        currentPosition.append("网络\n");
                    }

                    districtCode = getDistrictCode(location);
                    currentPosition.append("地区代码：").append(districtCode);
                    positionText.setText(currentPosition);


                    //弹窗提示定位
                    alertLocation = new AlertDialog.Builder(context)
                            .setTitle("小贴士")
                            .setMessage("定位到您目前在" + location.getProvince() + location.getCity() + location.getDistrict() + "，是否需要切换到该地？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    if (districtCode != null)
                                    {
                                        titleCity.setText(location.getCity());
                                        getWeather(titleCity.getText().toString());
                                    }

                                }
                            })

                            .setNegativeButton("取消", new DialogInterface.OnClickListener()
                            {//添加取消
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                }
                            })
                            .create();
                    alertLocation.setCancelable(false); //点击对话框外的部分，对话框不消失
                    alertLocation.show();
                }
            });
        }


    }

    List<Weather2.Result.Future> futures = new ArrayList<>();

    public void getWeather(String districtCode)
    {
//        if (titleCity.getText().toString().equals(""))
//        {
//            titleCity.setText("长沙");
//        }
        final String weatherUrl = "http://v.juhe.cn/weather/index?format=2&cityname=" + districtCode + "&key=de07a210ffecf3952bc11e320daf706a";

        OkHttpUtils.post()
                .url(weatherUrl)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id)
                    {

                    }

                    @Override
                    public void onResponse(String response, int id)
                    {
                        JsonObject resultJson = new JsonParser().parse(response).getAsJsonObject();
                        JsonObject result = resultJson.get("result").getAsJsonObject();
                        futures = new Gson().fromJson(result.getAsJsonArray("future"), new TypeToken<ArrayList<Weather2.Result.Future>>()
                        {
                        }.getType());
                        Log.e("asd", futures.size() + "");
                    }
                });
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback()
        {
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                String responseText = response.body().string();
                final Weather2 weather = new Gson().fromJson(responseText, Weather2.class);
                if (weather.getResultcode().equals("200"))
                {
                    Weather2.Result.Future future = new Weather2.Result.Future();
                    //这用province中就有了[]中的所有数据，下面遍历就可以了
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (weather != null)
                            {
                                mWeatherId = weather.getResult().getToday().getCity();
                                showWeatherInfo(weather);
                            } else
                            {
                                Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                            }
                            swipeRefresh.setRefreshing(false);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }


    /**
     * 加载必应每日一图
     */
    private void loadBingPic()
    {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback()
        {
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather2 weather)
    {
        String cityName = weather.getResult().getToday().getCity();
        String updateTime = weather.getResult().getSk().getTime();
        String degree = weather.getResult().getSk().getTemp() + "℃";
        String weatherInfo = weather.getResult().getToday().getWeather();
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Weather2.Result.Future future : futures)
        {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(future.getWeek());
            infoText.setText(future.getWeather());
            maxText.setText(future.getTemperature());
            forecastLayout.addView(view);
        }
//        if (weather.aqi != null)
//        {
//            aqiText.setText(weather.aqi.city.aqi);
//            pm25Text.setText(weather.aqi.city.pm25);
//        }
        String comfort = "穿衣建议：" + weather.getResult().getToday().getDressing_advice();
        String carWash = "洗车指数：" + weather.getResult().getToday().getWash_index();
        String sport = "旅游建议：" + weather.getResult().getToday().getTravel_index();
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

}
