package com.example.startopenapp.main.main_screen.notification.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startopenapp.R;

import java.util.List;

public class NotiAdapter extends RecyclerView.Adapter<NotiAdapter.NotiViewHolder> {

    private List<Noti> listNoti;

    public NotiAdapter(List<Noti> listNoti) {
        this.listNoti = listNoti;
    }

    @NonNull
    @Override
    public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiViewHolder holder, int position) {
        Noti noti = listNoti.get(position);
        if (noti == null){
            return;
        }

        holder.image.setImageResource(R.mipmap.ic_launcher);
        holder.title.setText(noti.getTitle());
        holder.description.setText(noti.getNotification());
        holder.time.setText(noti.getTime());
    }

    @Override
    public int getItemCount() {
        if (listNoti != null){
            return listNoti.size();
        }
        return 0;
    }

    class NotiViewHolder extends RecyclerView.ViewHolder{

        private ImageView image;
        private TextView title, description, time;

        public NotiViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
            time = itemView.findViewById(R.id.tvTime);
        }
    }

    public void setListNoti(List<Noti> listNoti) {
        this.listNoti.clear();
        this.listNoti.addAll(listNoti);
        notifyDataSetChanged();
    }
}
