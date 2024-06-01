package com.example.startopenapp.admin.cashier;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startopenapp.R;

import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private List<Bill> billList;
    private OnItemClickListener onItemClickListener;

    public BillAdapter(List<Bill> billList) {
        this.billList = billList;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new BillViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = billList.get(position);
        if (bill == null){
            return;
        }
        holder.itemOrderId.setText(bill.getOderId());
        holder.itemPrice.setText(bill.getPrice());
        holder.itemReceiver.setText(bill.getReceiver());
        holder.itemPhone.setText(bill.getPhone());
        holder.itemPaymentMethod.setText(bill.getPaymentmethod());
        holder.itemOrderTime.setText(bill.getTime());
    }

    @Override
    public int getItemCount() {
        if (billList != null){
            return billList.size();
        }
        return 0;
    }

    public class BillViewHolder extends RecyclerView.ViewHolder{
        private TextView itemOrderId, itemPrice, itemAccId, itemReceiver, itemPhone, itemPaymentMethod, itemOrderTime;
        public BillViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            itemOrderId = itemView.findViewById(R.id.itemOrderId);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemAccId = itemView.findViewById(R.id.itemAccId);
            itemReceiver = itemView.findViewById(R.id.itemReceiver);
            itemPhone = itemView.findViewById(R.id.itemPhone);
            itemPaymentMethod = itemView.findViewById(R.id.itemPaymentMethod);
            itemOrderTime = itemView.findViewById(R.id.itemOrderTime);

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    public void setBillList(List<Bill> billList) {
        this.billList = billList;
        notifyDataSetChanged();
    }
}
