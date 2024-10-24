package com.example.weather.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weather.Model.Next6Day;
import com.example.weather.R;

import java.util.ArrayList;

public class Next6DayAdapter extends RecyclerView.Adapter<Next6DayAdapter.viewHolder> {
    ArrayList<Next6Day> items;
    Context context;
    SharedPreferences preferences;

    public Next6DayAdapter(ArrayList<Next6Day> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public Next6DayAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // tạo ra view từ viewholder_next7day đã custom từ trước
        View inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_next7day, parent, false);
        context = parent.getContext();
        preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        return new viewHolder(inflater);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Next6DayAdapter.viewHolder holder, int position) {
        if(preferences.getInt("backGround", 0)==1){
            holder.txtDay.setTextColor(context.getColor(R.color.text_color_background2));
            holder.textVStatus.setTextColor(context.getColor(R.color.text_color_background2));
            holder.txtHigh.setTextColor(context.getColor(R.color.text_color_background2));
            holder.txtLow.setTextColor(context.getColor(R.color.text_color_background2));

        }
        else if(preferences.getInt("backGround", 0)==2){
            holder.txtDay.setTextColor(context.getColor(R.color.white));
            holder.textVStatus.setTextColor(context.getColor(R.color.white));
            holder.txtHigh.setTextColor(context.getColor(R.color.white));
            holder.txtLow.setTextColor(context.getColor(R.color.white));
        }
        // lấy dữ liệu từ hàm Next7Day để hiển thị lên viewholder_next7day
        holder.txtDay.setText(items.get(position).getDay());
        if(preferences.getInt("Units", 0)==0) {
            holder.txtHigh.setText(items.get(position).getHighTemp() + "℃");
            holder.txtLow.setText(items.get(position).getLowTemp() + "℃");
        }
        else if(preferences.getInt("Units", 0)==1){
            holder.txtHigh.setText(items.get(position).getHighTemp() + "°F");
            holder.txtLow.setText(items.get(position).getLowTemp() + "°F");
        }
        holder.textVStatus.setText(items.get(position).getStatus());
        String pic = items.get(position).getPicPath();
         // https://www.weatherbit.io/static/img/icons/c04n.png
        Glide.with(context)
                .load("https://www.weatherbit.io/static/img/icons/"+pic+".png")
                .into(holder.imgPic);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{
        TextView txtDay, textVStatus, txtLow, txtHigh;
        ImageView imgPic;

        // ánh xạ các view vào viewHolder để chuẩn bị sử dụng đưa dữ liệu từ class vào các view
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            txtDay = itemView.findViewById(R.id.txtDay);
            textVStatus = itemView.findViewById(R.id.textVStatus);
            txtLow = itemView.findViewById(R.id.txtLow);
            txtHigh = itemView.findViewById(R.id.txtHigh);
            imgPic = itemView.findViewById(R.id.imgPic);
        }
    }
}
