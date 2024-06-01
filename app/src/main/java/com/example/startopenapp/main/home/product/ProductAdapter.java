package com.example.startopenapp.main.home.product;

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

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private OnItemClickListener onItemClickListener;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public void updateData(ArrayList<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        if (product == null) {
            return;
        }

        String base64String = product.getHinhBase64Product();
        if (base64String != null && !base64String.isEmpty()) {
            Bitmap bitmap = decodeBase64ToBitmap(base64String);
            holder.itemImage.setImageBitmap(bitmap);
        } else {
            holder.itemImage.setImageResource(R.drawable.account);
        }

        holder.itemId.setText(product.getItemId());
        holder.itemName.setText(product.getItemName());
        holder.itemDescription.setText(product.getItemDescription());
        holder.itemPrice.setText(product.getItemPrice());
    }

    @Override
    public int getItemCount() {
        if (productList != null) {
            return productList.size();
        }
        return 0;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView itemId, itemName, itemDescription, itemPrice;

        public ProductViewHolder(@NonNull View itemView, OnItemClickListener listener) {
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
