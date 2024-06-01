package com.example.startopenapp.main.history;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startopenapp.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<History> historyList;

    public HistoryAdapter(List<History> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = historyList.get(position);
        if (history == null){
            return;
        }
        holder.itemType.setText(history.getItemType());
        holder.itemReceiver.setText(history.getItemReceiver());
        holder.itemTime.setText(history.getItemTime());
        holder.itemStatus.setText(history.getItemStatus());
        if (holder.itemType.getText().equals("chuyển tiền")){
            holder.imgHistory.setImageResource(R.drawable.send_money);
            holder.itemSotienGD.setText("-"+history.getItemSotienGD()+"đ");
        } else if (holder.itemType.getText().equals("nhận tiền")) {
            holder.imgHistory.setImageResource(R.drawable.receive_money);
            holder.itemSotienGD.setText("+"+history.getItemSotienGD()+"đ");
        } else if (holder.itemType.getText().equals("Thanh toán hóa đơn")) {
            holder.imgHistory.setImageResource(R.drawable.paybill);
            holder.itemSotienGD.setText("-"+history.getItemSotienGD()+"đ");
            holder.itemReceiver.setVisibility(View.GONE);
        }

//        else if (holder.itemType.getText() == "nạp tiền") {
//            holder.imgHistory.setImageResource(R.drawable.deposit);
//            holder.itemSotienGD.setText("+"+history.getItemSotienGD());
//        } else if (holder.itemType.getText() == "rút tiền") {
//            holder.imgHistory.setImageResource(R.drawable.withdraw);
//            holder.itemSotienGD.setText("-"+history.getItemSotienGD());
//        }
    }

    @Override
    public int getItemCount() {
        if (historyList != null){
            return historyList.size();
        }
        return 0;
    }

    public void updateData(ArrayList<History> newProductList) {
        this.historyList = newProductList;
        notifyDataSetChanged();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgHistory;
        private TextView itemType,itemReceiver, itemTime, itemStatus, itemSotienGD;
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHistory = itemView.findViewById(R.id.imgHistory);
            itemReceiver = itemView.findViewById(R.id.itemReceiver);
            itemType = itemView.findViewById(R.id.itemType);
            itemTime = itemView.findViewById(R.id.itemTime);
            itemStatus = itemView.findViewById(R.id.itemStatus);
            itemSotienGD = itemView.findViewById(R.id.itemSotienGD);
        }
    }

    public void setHistoryList(List<History> historyList){
        this.historyList.clear();
        this.historyList.addAll(historyList);
        notifyDataSetChanged();
    }
}
