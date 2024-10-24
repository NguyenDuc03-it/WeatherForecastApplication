package com.example.weather.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.weather.Model.UnitSetting;
import com.example.weather.R;

import java.util.List;

public class UnitSettingAdapter extends ArrayAdapter<UnitSetting> {

    SharedPreferences preferences;

    public UnitSettingAdapter(@NonNull Context context, int resource, @NonNull List<UnitSetting> objects) {
        super(context, resource, objects);
        preferences = getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
    }

    // hiển thị dữ liệu người dùng đã chọn từ danh sách đổ xuống vào View của spinner
    // Lấy dữ liệu background đã được cài trước (file layout spinner_item_selected) hiển thị lên View của spinner
    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_selected, parent, false);
        TextView textViewSpinnerSelected = convertView.findViewById(R.id.textViewSpinnerSelected);

        // lấy vị trí đã lưu từ SharedPreferences
        UnitSetting unitSetting = this.getItem(preferences.getInt("Units", 0));

        if (unitSetting != null) {
            textViewSpinnerSelected.setText(unitSetting.getTemperatureUnit());
        }
        return convertView;
    }


    // hiển thị dữ liệu của danh sách đổ xuống trong spinner (DropDownView)
    // lấy dữ liệu background đã được cài trước cho danh sách đổ xuống (file layout settings_unit_spinner) hiển thị lên DropDownView của spinner
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_unit_spinner, parent, false);
        TextView txtUnit = convertView.findViewById(R.id.textViewSpinner);
        ImageView icCheckUnit = convertView.findViewById(R.id.icCheckSpinner);

        UnitSetting unitSetting = this.getItem(position);

        if (unitSetting != null) {
            txtUnit.setText(unitSetting.getTemperatureUnit());
            if (position == preferences.getInt("Units", 0)) {
                txtUnit.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.text_color_settings));
                icCheckUnit.setVisibility(View.VISIBLE);
            } else {
                txtUnit.setTextColor(ContextCompat.getColor(parent.getContext(), android.R.color.white));
                icCheckUnit.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    // lấy dữ liệu vị trí người dùng đã chọn trên danh sách đổ xuống

}
