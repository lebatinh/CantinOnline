package com.example.startopenapp.admin.food;

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
import com.example.startopenapp.main.home.product.Product;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder>{
    private List<Food> foodList;
    private OnItemClickListener onItemClickListener;

    public FoodAdapter(List<Food> foodList) {
        this.foodList = foodList;
    }

    public void setFoodList(List<Food> foodList) {
        this.foodList = foodList;
        notifyDataSetChanged();
    }
    public void updateData(ArrayList<Food> newFoodList) {
        this.foodList = newFoodList;
        notifyDataSetChanged();
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new FoodViewHolder(view, onItemClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        if (food == null) {
            return;
        }

        String base64String = food.getHinhBase64Product();
        if (base64String != null && !base64String.isEmpty()) {
            Bitmap bitmap = decodeBase64ToBitmap(base64String);
            holder.itemImage.setImageBitmap(bitmap);
        } else {
            holder.itemImage.setImageResource(R.drawable.account);
        }

        holder.itemId.setText(food.getItemId());
        holder.itemName.setText(food.getItemName());
        holder.itemDescription.setText(food.getItemDescription());
        holder.itemPrice.setText(food.getItemPrice());
    }

    @Override
    public int getItemCount() {
        if (foodList != null){
            return foodList.size();
        }
        return 0;
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView itemId, itemName, itemDescription, itemPrice;

        public FoodViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemId = itemView.findViewById(R.id.itemId);
            itemName = itemView.findViewById(R.id.itemName);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            itemPrice = itemView.findViewById(R.id.itemPrice);

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
}
