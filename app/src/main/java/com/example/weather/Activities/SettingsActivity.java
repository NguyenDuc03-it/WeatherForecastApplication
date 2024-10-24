package com.example.weather.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weather.Adapters.BackgroundSettingAdapter;
import com.example.weather.Adapters.UnitSettingAdapter;
import com.example.weather.Model.BackgroundSetting;
import com.example.weather.Model.UnitSetting;
import com.example.weather.R;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private ImageView ic_back;
    private Spinner spinnerUnit, spinnerBackground;
    private UnitSettingAdapter unitSettingAdapter;
    private BackgroundSettingAdapter backgroundSettingAdapter;
    private LinearLayout containerUnit, containerBackground;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        Anhxa();
        Back();
        OnBackPressed();
        TempUnit();
        Background();

    }

    // Spinner background
    @SuppressLint("ClickableViewAccessibility")
    private void Background() {
        // Truyền vào: màn hình, kiểu hiển thị danh sách đổ xuống, dữ liệu để hiển thị lên danh sách đổ xuống
        backgroundSettingAdapter = new BackgroundSettingAdapter(this, R.layout.settings_unit_spinner, listBackground());
        // set dữ liệu vị trí hiển thị của dropdownView khi được click vào
        int dropdownOffset = getResources().getDimensionPixelOffset(R.dimen.spinner_dropdown_offset);
        int dropdownHorizontalOffset = getResources().getDimensionPixelOffset(R.dimen.spinner_dropdown_horizontal_offset);
        spinnerBackground.setDropDownVerticalOffset(dropdownOffset);
        spinnerBackground.setDropDownHorizontalOffset(dropdownHorizontalOffset);

        // Đọc vị trí đã lưu từ SharedPreferences
        int savedPosition = preferences.getInt("backGround", 0);

        // set adapter (set dữ liệu cho spinner)
        spinnerBackground.setAdapter(backgroundSettingAdapter);

        // Đặt vị trí đã lưu lên Spinner
        spinnerBackground.setSelection(savedPosition);

        // sự kiện chọn item trên dropdownView
        spinnerBackground.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Đưa dữ liệu của item được select vào hàm setSelectedPosition
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("backGround", i);
                editor.apply();
                Log.d("SETSelectedPosition", String.valueOf(i));
                // Phương thức notifyDataSetChanged() được sử dụng để thông báo cho Adapter rằng dữ liệu trong Adapter đã thay đổi
                // Khi gọi phương thức này, Adapter sẽ tự động cập nhật giao diện người dùng để phản ánh các thay đổi trong dữ liệu
                backgroundSettingAdapter.notifyDataSetChanged();
                // Hiện thông báo
                //Toast.makeText(SettingsActivity.this, backgroundSettingAdapter.getItem(i).getBackground(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        containerBackground.setOnClickListener(v -> {
            // Hiển thị Spinner khi click vào LinearLayout
            spinnerBackground.performClick();
        });

        containerBackground.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Hiệu ứng khi nhấn vào
                    containerBackground.setAlpha(0.5f);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Hiệu ứng khi nhả ra
                    containerBackground.    setAlpha(1.0f);
                    break;
            }
            return false;
        });
    }

    // Dữ liệu của background
    private List<BackgroundSetting> listBackground() {
        List<BackgroundSetting> arrBackground = new ArrayList<>();
        arrBackground.add(new BackgroundSetting("Type 1"));
        arrBackground.add(new BackgroundSetting("Type 2"));
        arrBackground.add(new BackgroundSetting("Type 3"));
        return arrBackground;
    }

    // Spinner Unit
    @SuppressLint("ClickableViewAccessibility")
    private void TempUnit() {
        // Truyền vào: màn hình, kiểu hiển thị danh sách đổ xuống, dữ liệu để hiển thị lên danh sách đổ xuống
        unitSettingAdapter = new UnitSettingAdapter(this, R.layout.settings_unit_spinner, listUnit());

        // set dữ liệu vị trí hiển thị của dropdownView khi được click vào
        int dropdownOffset = getResources().getDimensionPixelOffset(R.dimen.spinner_dropdown_offset);
        int dropdownHorizontalOffset = getResources().getDimensionPixelOffset(R.dimen.spinner_dropdown_horizontal_offset);
        spinnerUnit.setDropDownVerticalOffset(dropdownOffset);
        spinnerUnit.setDropDownHorizontalOffset(dropdownHorizontalOffset);

        // Đọc vị trí đã lưu từ SharedPreferences
        int savedPosition = preferences.getInt("Units", 0);

        // set adapter (set dữ liệu cho spinner)
        spinnerUnit.setAdapter(unitSettingAdapter);

        // Đặt vị trí đã lưu lên Spinner
        spinnerUnit.setSelection(savedPosition);

        // sự kiện chọn item trên dropdownView
        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("Units", i);
                editor.apply();
                Log.d("SETSelectedPosition", String.valueOf(i));
                unitSettingAdapter.notifyDataSetChanged();
                //Toast.makeText(SettingsActivity.this, unitSettingAdapter.getItem(i).getTemperatureUnit(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        containerUnit.setOnClickListener(v -> {
            // Hiển thị Spinner khi click vào LinearLayout
            spinnerUnit.performClick();
        });

        containerUnit.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Hiệu ứng khi nhấn vào
                    containerUnit.setAlpha(0.5f);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Hiệu ứng khi nhả ra
                    containerUnit.setAlpha(1.0f);
                    break;
            }
            return false;
        });
    }


    // Dữ liệu của Unit
    private List<UnitSetting> listUnit() {
        List<UnitSetting> arrUnit = new ArrayList<>();
        arrUnit.add(new UnitSetting("℃"));
        arrUnit.add(new UnitSetting("°F"));

        return arrUnit;
    }

    // Sự kiện quay lại màn hình chính
    private void Back() {
        ic_back.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
    private void OnBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
    // Ánh xạ các view
    private void Anhxa() {
        ic_back = findViewById(R.id.icBack);
        spinnerUnit = findViewById(R.id.SpinnerUnit);
        containerUnit = findViewById(R.id.containerUnit);
        spinnerBackground = findViewById(R.id.SpinnerBackground);
        containerBackground = findViewById(R.id.containerBackground);
    }
}