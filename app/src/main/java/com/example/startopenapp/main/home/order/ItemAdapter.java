package com.example.startopenapp.main.home.order;

import static com.example.startopenapp.display_manager.ImageHelper.decodeBase64ToBitmap;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startopenapp.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.OrderViewHolder> {
    private List<Item> itemList;

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    public void setOrderList(List<Item> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Item item = itemList.get(position);
        if (item == null){
            return;
        }

        String base64String = item.getHinhBase64Product();
        if (base64String != null && !base64String.isEmpty()) {
            Bitmap bitmap = decodeBase64ToBitmap(base64String);
            holder.itemImageOder.setImageBitmap(bitmap);
        } else {
            holder.itemImageOder.setImageResource(R.drawable.account);
        }

        holder.itemIdOder.setText(item.getItemId());
        holder.itemNameOder.setText(item.getItemName());
        holder.itemQuantityOder.setText(item.getItemQuantity());
        holder.itemPriceOder.setText(String.valueOf(item.getItemPrice()));
    }

    @Override
    public int getItemCount() {
        if (itemList != null){
            return itemList.size();
        }
        return 0;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder{
        private ImageView itemImageOder;
        private TextView itemQuantityOder, itemIdOder, itemNameOder, itemPriceOder;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageOder = itemView.findViewById(R.id.itemImageOder);
            itemQuantityOder = itemView.findViewById(R.id.itemQuantityOder);
            itemIdOder = itemView.findViewById(R.id.itemIdOder);
            itemNameOder = itemView.findViewById(R.id.itemNameOder);
            itemPriceOder = itemView.findViewById(R.id.itemPriceOder);
        }
    }

    // Phương thức tính tổng giá của tất cả các item trong itemList
    public double getTotalPrice() {
        int totalPrice = 0;
        for (Item item : itemList) {
            try {
                int price = item.getItemPrice();
                totalPrice += price;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return totalPrice;
    }

    public String generateItemStringList() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Item item : itemList) {
            String itemString = "ID: "+item.getItemId() + " - Tên: " + item.getItemName() + " - Số lượng: "
                    + item.getItemQuantity() + " - Giá: " + item.getItemPrice();
            stringBuilder.append(itemString).append("\n");
        }
        return stringBuilder.toString().trim();
    }
}
