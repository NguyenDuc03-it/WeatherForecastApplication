package com.example.weather.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weather.Adapters.Next6DayAdapter;
import com.example.weather.Model.Next6Day;
import com.example.weather.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    // https://api.weatherbit.io/v2.0/current?city=ha+nam&key=005509f6bb1b4f65b329af0f569e45e9&include=minutely
    // https://api.weatherbit.io/v2.0/forecast/daily?city=hanoi&days=7&key=005509f6bb1b4f65b329af0f569e45e9
    // https://www.weatherbit.io/static/img/icons/c04n.png
    private TextView txtTemplate, textViewStatus, txtHumidity, txtCloud, txtWind, txtMaxTemp, txtMinTemp, txtName, textView, tv_slash;
    private ImageView imageViewStatus, imgBack;
    public RecyclerView recyclerView;
    private ConstraintLayout constraintLayout;
    private LinearLayout banner;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        Intent intent = getIntent();
        String nameCity = intent.getStringExtra("nameCity");
        Log.d("Name city: ", nameCity);
        String units = "M";
        if(preferences.getInt("Units", 0)==0)
            units = "M";
        else if(preferences.getInt("Units", 0)==1)
            units = "I";

        Anhxa();
        SetBackGround();
        OnBackPressed();
        // Tạo sự kiện nhấn nút quay lại màn hình
        // Tuy nhiên ở đây là xét sự kiện cho cả layout chứ không chỉ trên imgBack
        // Có thể thay ConstraintLayout thành ImageView/Button/... và id tương ứng để xét sự kiện onClick, xét trên Layout để khu vực nhấn được rộng hơn
        setBackButtonListener();
        getWeatherData(nameCity, units);
    }

    private void OnBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void SetBackGround() {
        if(preferences.getInt("backGround", 0)==1){
            imgBack.setImageResource(R.drawable.ic_back_type2);
            txtName.setTextColor(getColor(R.color.text_color_background2));
            constraintLayout.setBackground(ContextCompat.getDrawable(this, R.color.background2_color));
            banner.setBackground(ContextCompat.getDrawable(this, R.drawable.gardient_background_type2_banner));
            textView.setTextColor(getColor(R.color.white));
            txtTemplate.setTextColor(getColor(R.color.white));
            txtMaxTemp.setTextColor(getColor(R.color.white));
            txtMinTemp.setTextColor(getColor(R.color.white));
            tv_slash.setTextColor(getColor(R.color.white));
            textViewStatus.setTextColor(getColor(R.color.white));
            txtHumidity.setTextColor(getColor(R.color.white));
            txtCloud.setTextColor(getColor(R.color.white));
            txtWind.setTextColor(getColor(R.color.white));
        }
        else if(preferences.getInt("backGround", 0)==2){
            txtName.setTextColor(getColor(R.color.white));
            constraintLayout.setBackground(ContextCompat.getDrawable(this, R.color.background3_color));
            banner.setBackground(ContextCompat.getDrawable(this, R.drawable.gradient_background_type3_banner));
            textView.setTextColor(getColor(R.color.white));
            txtTemplate.setTextColor(getColor(R.color.white));
            txtMaxTemp.setTextColor(getColor(R.color.white));
            txtMinTemp.setTextColor(getColor(R.color.white));
            tv_slash.setTextColor(getColor(R.color.white));
            textViewStatus.setTextColor(getColor(R.color.white));
            txtHumidity.setTextColor(getColor(R.color.white));
            txtCloud.setHintTextColor(getColor(R.color.white));
            txtWind.setHintTextColor(getColor(R.color.white));
        }
    }

    // Lấy dữ liệu JSON từ url
    private void getWeatherData(String data, String units) {
        txtName.setText(data);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity2.this);
        // Đọc dữ liệu từ đường dẫn
        String url = "https://api.weatherbit.io/v2.0/forecast/daily?city="+data+"&days=7&units="+units+"&key=005509f6bb1b4f65b329af0f569e45e9";

        // SuppressLint được dùng để bỏ qua cảnh báo của Android Lint về vấn đề tiềm ẩn trong code
        // ví dụ trong hàm này phương thức setText để nhập vào 1 văn bản trực tiếp mà không sử dụng tài nguyên i18n (quốc tế hóa) như @string/my_text
        // Nên sử dụng @string/text để đảm bảo bảo mật, quy trình sửa đổi dễ dàng...
        @SuppressLint("SetTextI18n") StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);

                JSONArray jsonArray = jsonObject.getJSONArray("data");



                JSONObject jsonObjectTomorrow = jsonArray.getJSONObject(1);

                String maxTemp = jsonObjectTomorrow.getString("high_temp");
                double a = Double.parseDouble(maxTemp);
                maxTemp = String.valueOf((int) a);
                Log.d("UNITS URL1:", units);
                if(preferences.getInt("Units", 0)==0)
                    txtMaxTemp.setText(maxTemp+"℃");
                else if(preferences.getInt("Units", 0)==1)
                    txtMaxTemp.setText(maxTemp + "°F");
                Log.d("UNITS URL2:", units);
                Log.d("MAX TEMP", maxTemp);

                String minTemp = jsonObjectTomorrow.getString("low_temp");
                double b = Double.parseDouble(minTemp);
                minTemp = String.valueOf((int) b);
                if(preferences.getInt("Units", 0)==0)
                    txtMinTemp.setText(minTemp+"℃");
                else if(preferences.getInt("Units", 0)==1)
                    txtMinTemp.setText(minTemp + "°F");
                Log.d("MIN TEMP", minTemp);

                String temp = jsonObjectTomorrow.getString("temp");
                double c = Double.parseDouble(temp);
                temp = String.valueOf((int) c);
                if(preferences.getInt("Units", 0)==0)
                    txtTemplate.setText(temp+"℃");
                else if(preferences.getInt("Units", 0)==1)
                    txtTemplate.setText(temp + "°F");

                String rHumidity = jsonObjectTomorrow.getString("rh");
                txtHumidity.setText(rHumidity+"%");

                String clouds = jsonObjectTomorrow.getString("clouds");
                txtCloud.setText(clouds+"%");

                String wind = jsonObjectTomorrow.getString("wind_spd");
                txtWind.setText(wind+"m/s");

                JSONObject jsonObjectWeather = jsonObjectTomorrow.getJSONObject("weather");
                textViewStatus.setText(jsonObjectWeather.getString("description"));
                Log.d("description", jsonObjectWeather.getString("description"));
                String icon = jsonObjectWeather.getString("icon");
                Picasso.get()
                        .load("https://www.weatherbit.io/static/img/icons/"+icon+".png")
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error_img)
                        .into(imageViewStatus, new Callback() {
                            @Override
                            public void onSuccess() {
                                // Hình ảnh đã tải thành công
                            }

                            @Override
                            public void onError(Exception e) {
                                // Xử lý lỗi tải hình ảnh
                                Log.e("Picasso 2", "Lỗi tải hình ảnh: " + e.getMessage());
                            }
                        });
                Log.d("Icon 2", "https://www.weatherbit.io/static/img/icons/"+icon+".png");

                // Đổ dữ liệu vào recyclerView
                initRecyclerView(jsonArray);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {

        });


        // Thực thi yêu cầu với đường dẫn đã gán trong stringRequest
        requestQueue.add(stringRequest);
    }


    // Lấy dữ liệu có sẵn từ biến jsonArray đã được khởi tạo và có giá trị từ hàm getWeatherData để đưa dữ liệu vào các biến
    private void initRecyclerView(JSONArray jsonArray) {
        ArrayList<Next6Day> items = new ArrayList<>();

        // lấy dữ liệu từ ngày thứ 2 trở đi
        // trang web cho dữ liệu 7 ngày: ngày hiện tại (ngày đầu) và 6 ngày sau
        // vì đã hiển thị dữ liệu ngày tiếp theo sau ngày hiện tại (tomorrow) nên sẽ lấy dữ liệu từ ngày thứ 3 trở đi (tomorrow là ngày 2 tính từ ngày đầu)
        // mảng Array sẽ hiển thị dữ liệu từ 0 (0,1,2,...) nên ngày thứ 3 sẽ lấy giá trị tương ứng là 2
        for (int i = 2; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObjectData = jsonArray.getJSONObject(i);

                //Lấy giá trị times (kiểu unix)
                String day = jsonObjectData.getString("ts");
                // Đổi từ kiểu chuỗi lấy được từ json sang kiểu long vì kiểu unix là chuỗi số dài vd:1722531660
                long l = Long.parseLong(day);
                // đổi sang kiểu date, vì SimpleDateFormat đổi từ đơn vị 'ms'(mini giây) mà unix là đơn vị 's'(giây) nên ta *1000
                Date date = new Date(l*1000L);
                // Ta sẽ chuyển đổi thành Thứ trong tuần và theo tiếng Anh
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                // Chuyển đổi (format) thời gian kiểu unix sang Thứ trong tuần trả về kiểu chuỗi
                String Day = simpleDateFormat.format(date);

                // Lấy nhiệt độ cao nhất
                String maxTemp = jsonObjectData.getString("high_temp");
                double a = Double.parseDouble(maxTemp);
                int highTemp = (int) a;
                Log.d("MAXRecycler TEMP", highTemp+"");

                // Lấy nhiệt độ thấp nhất
                String minTemp = jsonObjectData.getString("low_temp");
                double b = Double.parseDouble(minTemp);
                int lowTemp = (int) b;
                Log.d("MINRecycler TEMP", lowTemp+"");

                JSONObject jsonObjectWeather = jsonObjectData.getJSONObject("weather");

                // Lấy chuỗi đại diện cho icon
                String icon = jsonObjectWeather.getString("icon");
                Log.d("Icon Recycler", "https://www.weatherbit.io/static/img/icons/"+icon+".png");

                // Lấy mô tả thời tiết
                String description = jsonObjectWeather.getString("description");

                // Truyền dữ liệu đã lấy được từ Json vào hàm next7Day, tạo dữ liệu mới và đồng thời thêm vào ArrayList
                items.add(new Next6Day(Day, icon, description, highTemp, lowTemp));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

        // Xét layout hiển thị cho items (cụ thể là hiển thị viewholder_next7day) cho recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));

        // Đưa dữ liệu đã nhận từ items đã lấy trước đó từ vòng for vào adapter
        RecyclerView.Adapter<Next6DayAdapter.viewHolder> adapterTomorrow = new Next6DayAdapter(items);
        // biến recyclerView nhận dữ liệu
        recyclerView.setAdapter(adapterTomorrow);


    }


    private void setBackButtonListener() {
        ConstraintLayout backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(view -> {
            //startActivity(new Intent(MainActivity2.this, MainActivity.class));
            this.finish();
        });
    }

    private void Anhxa() {
        txtTemplate = findViewById(R.id.textViewTemplate);
        textViewStatus = findViewById(R.id.textViewStatus);
        txtHumidity = findViewById(R.id.txtHumidity2);
        txtCloud = findViewById(R.id.txtCloud2);
        txtWind = findViewById(R.id.txtWind2);
        textView = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recyclerView);
        txtMaxTemp = findViewById(R.id.txtMaxTemp);
        txtMinTemp = findViewById(R.id.txtMinTemp);
        imageViewStatus = findViewById(R.id.imageViewStatus);
        txtName = findViewById(R.id.txtName);
        constraintLayout = findViewById(R.id.Background_Activity2);
        banner = findViewById(R.id.banner_activity2);
        tv_slash = findViewById(R.id.tv_slash);
        imgBack = findViewById(R.id.imgBack);
    }
}