package com.example.weather.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weather.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CHECK_SETTINGS = 1001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private EditText edtSearch;
    private Button btnSearch, btnNext6Day;
    private TextView txtTenThanhPho, txtTenQuocGia, txtHumidity, txtCloud, txtWind, txtDate, txtTemp, txtStatus;
    private ImageView imgStatus;
    private DrawerLayout drawerLayout;
    private LinearLayout bannerBackground;
    private ImageView settings;
    private TextView tv_location, tv_temp, txtOtherPosition, txtReportWrLc, txtLocation, txtLine, txtLine2;
    private Button btnLocationManagement;
    private NavigationView navigationView;
    private ImageView imv_pic;
    private String nameCity = "";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.CustomStatusBar);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String units = "metric";
        if(preferences.getInt("Units", 0)==0){
            units = "metric";
        }
        else if(preferences.getInt("Units", 0)==1){
            units = "imperial";
        }
        AnhXa();
        SetBackGround();
        SetBackGroundNavigation();
        ShowDialog();
        initialValue(units);
        search(units);
        next6Day(nameCity);

        // Thực hiện hành động reload ở đây
        // this::reloadData: Sử dụng tham chiếu phương thức để tham chiếu đến phương thức reloadData()
        swipeRefreshLayout.setOnRefreshListener(this::reloadData);
    }

    private void next6Day(String nameCity) {
        btnNext6Day.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            // lấy giá trị id của city/country để truyền cho các màn hình khác được chuyển từ màn hình main sang
            intent.putExtra("nameCity", nameCity);
            startActivity(intent);
        });
    }

    private void search(String units) {
        // set sự kiện nhấn nút
        String finalUnits = units;
        btnSearch.setOnClickListener(v-> {
                    String city = edtSearch.getText().toString();
                    getWeatherData(city, finalUnits);
                    openNavigation(city, finalUnits);
                }
        );

    }

    private void initialValue(String units) {
        // Đặt giá trị ban đầu khi khởi động ứng dụng
        // khi người dùng khởi động ứng dụng dữ liệu data truyền vào mặc định là 'hanoi'
        // nếu người dùng nhập dữ liệu lên thanh tìm kiếm thì data sẽ lưu giá trị người dùng đã nhập
        if(edtSearch.getText().toString().isEmpty()){
            String city = "hanoi";
            getWeatherData(city, units);
            openNavigation(city, units);
        }else {
            String city = edtSearch.getText().toString();
            getWeatherData(city, units);
            openNavigation(city, units);
        }
    }

    private void reloadData() {
        // Lấy lại tên thành phố từ EditText hoặc biến toàn cục
        String city = edtSearch.getText().toString();
        String units = preferences.getInt("Units", 0) == 0 ? "metric" : "imperial"; // Lấy đơn vị từ SharedPreferences

        // Gọi lại phương thức lấy dữ liệu thời tiết
        getWeatherData(city.isEmpty() ? "hanoi" : city, units);

        // Sau khi hoàn tất, hãy gọi setRefreshing(false) để ẩn spinner
        swipeRefreshLayout.setRefreshing(false);
    }

    private void SetBackGroundNavigation() {
        if(preferences.getInt("backGround", 0)==1){
            navigationView.setBackground(ContextCompat.getDrawable(this, R.drawable.background_navigationview_type2));
            txtLocation.setTextColor(getColor(R.color.text_color_background2));
            tv_location.setTextColor(getColor(R.color.text_color_background2));
            Drawable newLocationIcon = ContextCompat.getDrawable(this, R.drawable.ic_location_on_type2);
            tv_location.setCompoundDrawablesWithIntrinsicBounds(newLocationIcon, null, null, null);

            tv_temp.setTextColor(getColor(R.color.text_color_background2));
            txtLine2.setTextColor(getColor(R.color.text_color_background2));
            txtLine.setTextColor(getColor(R.color.text_color_background2));
            txtOtherPosition.setTextColor(getColor(R.color.text_color_background2));
            Drawable newOtherPosition = ContextCompat.getDrawable(this, R.drawable.ic_add_location_type2);
            txtOtherPosition.setCompoundDrawablesWithIntrinsicBounds(newOtherPosition, null, null, null);
            txtReportWrLc.setTextColor(getColor(R.color.text_color_background2));
            Drawable newReportWrLc = ContextCompat.getDrawable(this, R.drawable.ic_error_outline_type2);
            txtReportWrLc.setCompoundDrawablesWithIntrinsicBounds(newReportWrLc, null, null, null);
            btnLocationManagement.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background_type2));
            btnLocationManagement.setTextColor(getColor(R.color.white));
            settings.setImageResource(R.drawable.ic_settings_type2);
        }
        else if(preferences.getInt("backGround", 0)==2){
            navigationView.setBackground(ContextCompat.getDrawable(this, R.drawable.background_navigationview_type3));
            txtLocation.setTextColor(getColor(R.color.white));
            tv_location.setTextColor(getColor(R.color.white));
            Drawable newLocationIcon = ContextCompat.getDrawable(this, R.drawable.ic_location_on);
            tv_location.setCompoundDrawablesWithIntrinsicBounds(newLocationIcon, null, null, null);

            tv_temp.setTextColor(getColor(R.color.white));
            txtLine2.setTextColor(getColor(R.color.white));
            txtLine.setTextColor(getColor(R.color.white));
            txtOtherPosition.setTextColor(getColor(R.color.white));
            Drawable newOtherPosition = ContextCompat.getDrawable(this, R.drawable.ic_add_location);
            txtOtherPosition.setCompoundDrawablesWithIntrinsicBounds(newOtherPosition, null, null, null);
            txtReportWrLc.setTextColor(getColor(R.color.white));
            Drawable newReportWrLc = ContextCompat.getDrawable(this, R.drawable.ic_error_outline);
            txtReportWrLc.setCompoundDrawablesWithIntrinsicBounds(newReportWrLc, null, null, null);
            btnLocationManagement.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background_type3));
            btnLocationManagement.setTextColor(getColor(R.color.white));
            settings.setImageResource(R.drawable.ic_settings);


        }
    }

    private void SetBackGround() {
        if(preferences.getInt("backGround", 0)==1){
            drawerLayout.setBackground(ContextCompat.getDrawable(this, R.color.background2_color));
            bannerBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.gardient_background_type2_banner));
            txtTenThanhPho.setTextColor(getColor(R.color.text_color_background2));
            txtTenQuocGia.setTextColor(getColor(R.color.text_color_background2));
            txtTemp.setTextColor(getColor(R.color.text_color_background2));
            txtStatus.setTextColor(getColor(R.color.text_color_background2));
            txtDate.setTextColor(getColor(R.color.text_color_background2));
            edtSearch.setTextColor(getColor(R.color.text_color_background2));
            edtSearch.setHintTextColor(getColor(R.color.text_color_background2));
            btnSearch.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background_type2));
            btnSearch.setTextColor(getColor(R.color.white));
            btnNext6Day.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background_type2));
            btnNext6Day.setTextColor(getColor(R.color.white));
        }
        else if(preferences.getInt("backGround", 0)==2){
            drawerLayout.setBackground(ContextCompat.getDrawable(this, R.color.background3_color));
            bannerBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.gradient_background_type3_banner));
            txtTenThanhPho.setTextColor(getColor(R.color.white));
            txtTenQuocGia.setTextColor(getColor(R.color.white));
            txtTemp.setTextColor(getColor(R.color.white));
            txtStatus.setTextColor(getColor(R.color.white));
            txtDate.setTextColor(getColor(R.color.white));
            edtSearch.setTextColor(getColor(R.color.white));
            edtSearch.setHintTextColor(getColor(R.color.white));
            btnSearch.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background_type3));
            btnSearch.setTextColor(getColor(R.color.white));
            btnNext6Day.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background_type3));
            btnNext6Day.setTextColor(getColor(R.color.white));
        }
    }

    // Tạo action cho toolbar mở navigationView
    // data được truyền vào là tên city hoặc country để lấy api json trả về từ url
    private void openNavigation(String data, String units) {



        Toolbar toolbar = findViewById(R.id.toolBar);
        // Tạo actionBar cho toolbar (nút ≡)
        setSupportActionBar(toolbar);
        // Tạo một ActionBarDrawerToggle để liên kết Toolbar với NavigationDrawer.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        // Thêm ActionBarDrawerToggle vào drawerLayout để nó có thể theo dõi và xử lý sự kiện mở/đóng NavigationDrawer.
        drawerLayout.addDrawerListener(toggle);
        SetBackGroundNavigation();
        // Đồng bộ hóa trạng thái của ActionBarDrawerToggle với trạng thái hiện tại của NavigationDrawer.
        toggle.syncState();

        // tạo 1 hàng đợi yêu cầu, biến này sẽ giúp thực hiện các yêu cầu gửi đi
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        // Đọc dữ liệu từ đường dẫn
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+data+"&units="+units+"&appid=0a37f5125008aa05015889700b7df443";
        @SuppressLint("SetTextI18n") StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        tv_location.setText(jsonObject.getString("name"));

                        JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                        String nhietDo = jsonObjectMain.getString("temp");
                        //Chuyển nhiệt độ từ dữ liệu thập phân(26.4) về kiểu nguyên(26)
                        double a = Double.parseDouble(nhietDo);
                        String temp = String.valueOf((int) a);

                        JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                        String icon = jsonObjectWeather.getString("icon");
                        // https://openweathermap.org/img/wn/"+icon+"@2x.png
                        //Lấy ảnh/icon từ url
                        Picasso.get()
                                .load("https://openweathermap.org/img/wn/"+icon+"@2x.png")
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.error_img)
                                .into(imv_pic, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        // Hình ảnh đã tải thành công
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        // Xử lý lỗi tải hình ảnh
                                        Log.e("Picasso", "Lỗi tải hình ảnh: " + e.getMessage());
                                    }
                                });
                        if(units.equals("metric"))
                            tv_temp.setText(temp+"℃");
                        else if(units.equals("imperial"))
                            tv_temp.setText(temp + "°F");

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {

                });
        // Thực thi yêu cầu với đường dẫn đã gán trong stringRequest
        requestQueue.add(stringRequest);

        txtOtherPosition.setOnClickListener(view -> Toast.makeText(MainActivity.this, "The function is coming soon", Toast.LENGTH_SHORT).show());

        txtReportWrLc.setOnClickListener(view -> Toast.makeText(MainActivity.this, "The function is coming soon", Toast.LENGTH_SHORT).show());

        btnLocationManagement.setOnClickListener(view -> Toast.makeText(MainActivity.this, "The function is coming soon", Toast.LENGTH_SHORT).show());

        settings.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            // lấy giá trị id của city/country để truyền cho các màn hình khác được chuyển từ màn hình main sang
            startActivity(intent);
        });

    }

    // Hàm hủy, khi chuyển sang Activity2 thì dữ liệu icon đã lấy sẽ được hủy
    // Tránh gây rò rỉ bộ nhớ
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //.....
    }

    // Lấy dữ liệu api thời tiết
    // trước đó ta phải cài 2 thư viện là volley và picasso/glide
    // #1: Volley giúp ta lấy được dữ liệu từ url
    // #2: Picasso/Glide giúp ta lấy các hình ảnh/icon từ url
    private void getWeatherData(String data, String units){
        // tạo 1 hàng đợi yêu cầu, biến này sẽ giúp thực hiện các yêu cầu gửi đi
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        // Đọc dữ liệu từ đường dẫn
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+data+"&units="+units+"&appid=0a37f5125008aa05015889700b7df443";
        // Dữ liệu được lấy từ url sẽ được trả về trong biến response
        // Nếu bị lỗi thì sẽ đưa thông báo lỗi vào biến error
        @SuppressLint("SetTextI18n") StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        // Phân tích dữ liệu JSON được trả về biến response và hiển thị ra màn hình
                        JSONObject jsonObject = new JSONObject(response);
                        nameCity = jsonObject.getString("name");

                        txtTenThanhPho.setText("Name: "+nameCity);

                        JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
                        String nameCountry = jsonObjectSys.getString("country");
                        txtTenQuocGia.setText("Country: "+nameCountry);

                        String day = jsonObject.getString("dt");
                        long l = Long.parseLong(day);
                        Date date = new Date(l*1000L);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
                        String Day = simpleDateFormat.format(date);
                        txtDate.setText(Day);

                        JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                        String status = jsonObjectWeather.getString("main");
                        String icon = jsonObjectWeather.getString("icon");
                        // https://openweathermap.org/img/wn/"+icon+"@2x.png
                        //Lấy ảnh/icon từ url
                        Picasso.get()
                                .load("https://openweathermap.org/img/wn/"+icon+"@4x.png")
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.error_img)
                                .into(imgStatus, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        // Hình ảnh đã tải thành công
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        // Xử lý lỗi tải hình ảnh
                                        Log.e("Picasso", "Lỗi tải hình ảnh: " + e.getMessage());
                                    }
                                });
                        txtStatus.setText(status);

                        Log.d("Icon", "https://openweathermap.org/img/wn/"+icon+"@4x.png");

                        JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                        String nhietDo = jsonObjectMain.getString("temp");
                        String humidity = jsonObjectMain.getString("humidity");

                        //Chuyển nhiệt độ từ dữ liệu thập phân về kiểu nguyên
                        double a = Double.parseDouble(nhietDo);
                        String temp = String.valueOf((int) a);
                        if(units.equals("metric"))
                            txtTemp.setText(temp + "℃");
                        else if(units.equals("imperial"))
                            txtTemp.setText(temp + "°F");

                        txtHumidity.setText(humidity+"%");

                        JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                        String speed = jsonObjectWind.getString("speed");
                        txtWind.setText(speed+"m/s");

                        JSONObject jsonObjectCloud = jsonObject.getJSONObject("clouds");
                        String cloud = jsonObjectCloud.getString("all");
                        txtCloud.setText(cloud+"%");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                },
                error -> {

                });
        // Thực thi yêu cầu với đường dẫn đã gán trong stringRequest
        requestQueue.add(stringRequest);
    }
    private boolean hasLocationPermission() {
        return  ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void ShowDialog() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000) // 10 giây
                .setWaitForAccurateLocation(true)
                .setMaxUpdateDelayMillis(5000)
                .build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> {
            // Vị trí đã được bật
            if (hasLocationPermission()) {
                getCurrentLocation(locationRequest); // Lấy vị trí
            } else {
                // Hiển thị hộp thoại yêu cầu người dùng cấp quyền
                // Yêu cầu quyền truy cập vị trí
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        });

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Xử lý lỗi
                    Log.e("Err Location: ", sendEx.getMessage());
                }
            }
        });



    }

    // Hiển thị hộp thoại hỏi người dùng truy cập vào cài đặt để cấp quyền vị trí
    private void showPermissionDeniedDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Permission Required");
        dialog.setMessage("Location permission is required to access your location. Please enable it in the app settings.");
        dialog.setIcon(R.drawable.ic_location_on_type2);
        dialog.setPositiveButton("Go to Settings", (dialogInterface, i) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });
        dialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = dialog.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            Button buttonOn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button buttonCancel = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            // Đặt màu chữ cho nút "On"
            buttonOn.setTextColor(ContextCompat.getColor(this, R.color.black));

            // Đặt màu chữ cho nút "No, thanks"
            buttonCancel.setTextColor(ContextCompat.getColor(this, R.color.black));
        });

        alertDialog.show();
    }

    private void getCurrentLocation(LocationRequest locationRequest) {
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Yêu cầu quyền truy cập vị trí
            showPermissionDeniedDialog();
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (!locationResult.getLocations().isEmpty()) {
                    Location location = locationResult.getLastLocation();
                    assert location != null;
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Toast.makeText(getApplicationContext(), "Current Location: " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
                    Log.i("Current Location", latitude + "," + longitude);
                    // Dừng việc cập nhật vị trí sau khi đã lấy được
                    fusedLocationProviderClient.removeLocationUpdates(this);
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to get location.", Toast.LENGTH_SHORT).show();
                }
            }
        }, Looper.getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000) // 10 giây
                        .setWaitForAccurateLocation(true)
                        .setMaxUpdateDelayMillis(5000)
                        .build();
                getCurrentLocation(locationRequest); // Gọi hàm để lấy vị trí nếu quyền đã được cấp
            } else {
                // Người dùng đã từ chối quyền truy cập
                showPermissionDeniedDialog(); // Hiển thị hộp thoại nếu quyền bị từ chối
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                // Người dùng đã bật vị trí
                Toast.makeText(this, "Location is enabled.", Toast.LENGTH_SHORT).show();
            } else {
                // Người dùng đã từ chối bật vị trí
                Toast.makeText(this, "Location is not enabled.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void AnhXa() {
        edtSearch = findViewById(R.id.editTextSearch);
        txtTenThanhPho = findViewById(R.id.txtTenThanhPho);
        txtHumidity = findViewById(R.id.txtHumidity);
        txtTenQuocGia = findViewById(R.id.txtTenQuocGia);
        txtCloud = findViewById(R.id.txtCloud);
        txtWind = findViewById(R.id.txtWind);
        txtDate = findViewById(R.id.txtDate);
        txtTemp = findViewById(R.id.txtTemp);
        txtStatus = findViewById(R.id.txtStatus);
        imgStatus = findViewById(R.id.imgStatus);
        btnSearch = findViewById(R.id.btnSearch);
        btnNext6Day = findViewById(R.id.btnNext6Day);
        drawerLayout = findViewById(R.id.drawerLayout);
        bannerBackground = findViewById(R.id.bannerBackground);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // NavigationView
        navigationView = findViewById(R.id.navigation);
        txtLocation = navigationView.findViewById(R.id.txtLocation);
        settings = navigationView.findViewById(R.id.ic_settings);
        txtLine = navigationView.findViewById(R.id.txtLine);
        txtLine2 = navigationView.findViewById(R.id.txtLine2);
        tv_location = navigationView.findViewById(R.id.tv_location);
        tv_temp = navigationView.findViewById(R.id.tv_temp);
        txtOtherPosition = navigationView.findViewById(R.id.txtOtherPosition);
        txtReportWrLc = navigationView.findViewById(R.id.txtReportWrLc);
        btnLocationManagement = navigationView.findViewById(R.id.btnLocationManagement);
        imv_pic = navigationView.findViewById(R.id.imv_pic);
    }
}