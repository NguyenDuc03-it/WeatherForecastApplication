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

import com.example.weather.Model.BackgroundSetting;
import com.example.weather.R;

import java.util.List;

public class BackgroundSettingAdapter extends ArrayAdapter<BackgroundSetting> {

    SharedPreferences preferences;

    // lấy dữ liệu từ danh sách (list) được thêm vào từ List<BackgroundSetting>, (Context) màn hình hiển thị
    public BackgroundSettingAdapter(@NonNull Context context, int resource, @NonNull List<BackgroundSetting> objects) {
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

        BackgroundSetting backgroundSetting = this.getItem(preferences.getInt("backGround", 0));

        if (backgroundSetting!=null){
            textViewSpinnerSelected.setText(backgroundSetting.getBackground());
        }
        return convertView;
    }

    // hiển thị dữ liệu của danh sách đổ xuống trong spinner (DropDownView)
    // lấy dữ liệu background đã được cài trước cho danh sách đổ xuống (file layout settings_unit_spinner) hiển thị lên DropDownView của spinner
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_unit_spinner, parent, false);
        TextView txtType = convertView.findViewById(R.id.textViewSpinner);
        ImageView icCheckType = convertView.findViewById(R.id.icCheckSpinner);

        BackgroundSetting backgroundSetting = this.getItem(position);

        if (backgroundSetting!=null){
            txtType.setText(backgroundSetting.getBackground());
            // Kiểm tra xem item hiện tại có trùng với item được chọn trong Spinner hay không
            if (position == preferences.getInt("backGround", 0)) {
                // Nếu trùng, set color chữ tương ứng
                txtType.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.text_color_settings));
                icCheckType.setVisibility(View.VISIBLE);
            } else {
                // Nếu không trùng, set color chữ tương ứng
                txtType.setTextColor(ContextCompat.getColor(parent.getContext(), android.R.color.white));
                icCheckType.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    // lấy dữ liệu vị trí người dùng đã chọn trên danh sách đổ xuống dựa vào dữ liệu hiển thị trên view của spinner
    // vd: danh sách dropdown là C, F lần lượt là 0, 1; nếu người dùng chọn F, F hiển thị trên view của spinner
    // F trong danh sách là 1 nên position = 1 (F => position = 1) và ngược lại (position = 1 => F)

}
