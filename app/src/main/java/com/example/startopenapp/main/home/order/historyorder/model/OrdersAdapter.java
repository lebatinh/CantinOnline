package com.example.startopenapp.main.home.order.historyorder.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startopenapp.R;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {
    private List<Orders> ordersList;

    public OrdersAdapter(List<Orders> ordersList) {
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_history_item, parent, false);
        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        Orders orders = ordersList.get(position);
        if (orders==null) return;
        holder.bind(orders);

    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public class OrdersViewHolder extends RecyclerView.ViewHolder{
        private TextView itemOrderId, itemPrice,itemOrderContent, itemPaymentMethod, itemOrderTime;
        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            itemOrderId = itemView.findViewById(R.id.itemOrderId);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemOrderContent = itemView.findViewById(R.id.itemOrderContent);
            itemPaymentMethod = itemView.findViewById(R.id.itemPaymentMethod);
            itemOrderTime = itemView.findViewById(R.id.itemOrderTime);
        }
        public void bind(Orders orders) {
            itemOrderId.setText(orders.getOrderId());
            itemPrice.setText(orders.getOrderPrice());
            itemOrderContent.setText(orders.getOrderContent());
            itemPaymentMethod.setText(orders.getOrderPayment());
            itemOrderTime.setText(orders.getOrderTime());
        }
    }

    public void setOrdersList(List<Orders> ordersList){
        this.ordersList.clear();
        this.ordersList.addAll(ordersList);
        notifyDataSetChanged();
    }
}
