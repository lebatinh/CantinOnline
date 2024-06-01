package com.example.startopenapp.main.home.cart;

import static com.example.startopenapp.display_manager.ImageHelper.decodeBase64ToBitmap;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startopenapp.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{
    private List<Cart> cartList;
    private CartInteractionListener listener;
    private List<String> selectedItems;
    private TotalPriceUpdateListener totalPriceUpdateListener;

    public CartAdapter(List<Cart> cartList, CartInteractionListener listener, List<String> selectedItems, TotalPriceUpdateListener totalPriceUpdateListener) {
        this.cartList = cartList;
        this.listener = listener;
        this.selectedItems = selectedItems;
        this.totalPriceUpdateListener = totalPriceUpdateListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = cartList.get(position);
        if (cart == null){
            return;
        }

        String base64String = cart.getHinhBase64Product();
        if (base64String != null && !base64String.isEmpty()){
            Bitmap bitmap = decodeBase64ToBitmap(base64String);

            holder.itemImage.setImageBitmap(bitmap);
        }else {
            holder.itemImage.setImageResource(R.drawable.account);
        }

        holder.itemId.setText(cart.getItemId());
        holder.itemName.setText(cart.getItemName());
        holder.itemPrice.setText(cart.getItemPrice());
        holder.itemAmount.setText(cart.getItemAmount());

        holder.btnMinus.setOnClickListener(view -> {
            int amount = Integer.parseInt(holder.itemAmount.getText().toString());
            if (amount > 0){
                amount--;
                holder.itemAmount.setText(String.valueOf(amount));
                updateAmount(position, amount);
                // Kiểm tra xem mặt hàng hiện tại có trong danh sách các mặt hàng được chọn không
                if (selectedItems.contains(cart.getItemId())) {
                    // Nếu có, cập nhật tổng số tiền
                    updateTotalPrice();
                }
            } else {
                holder.btnMinus.setClickable(false);
                updateAmount(position, amount);
                removeItem(position);
            }
        });

        holder.btnSum.setOnClickListener(view -> {
            int amount = Integer.parseInt(holder.itemAmount.getText().toString());
            amount++;
            holder.itemAmount.setText(String.valueOf(amount));
            updateAmount(position, amount);
            // Kiểm tra xem mặt hàng hiện tại có trong danh sách các mặt hàng được chọn không
            if (selectedItems.contains(cart.getItemId())) {
                // Nếu có, cập nhật tổng số tiền
                updateTotalPrice();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            String itemId = cart.getItemId();
            if (selectedItems.contains(itemId)) {
                selectedItems.remove(itemId);
            } else {
                selectedItems.add(itemId);
            }

            // Kiểm tra xem có ít nhất một mặt hàng được chọn
            boolean atLeastOneItemSelected = !selectedItems.isEmpty();
            if (atLeastOneItemSelected) {
                // Nếu có ít nhất một mặt hàng được chọn, cập nhật tổng số tiền
                updateTotalPrice();
            }
            notifyItemChanged(position);  // Cập nhật lại RecyclerView để áp dụng màu nền mới
        });

        // Đổi màu nền của item nếu nó được chọn
        if (selectedItems.contains(cart.getItemId())) {
            holder.itemView.setBackgroundColor(Color.DKGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
    }
    private void updateTotalPrice() {
        double totalPrice = getTotalPrice();
        // Gửi số tiền tổng mới đến CartActivity thông qua interface
        totalPriceUpdateListener.onTotalPriceUpdated(totalPrice);
    }
    public interface TotalPriceUpdateListener {
        void onTotalPriceUpdated(double totalPrice);
    }
    private void updateAmount(int position, int amount) {
        Cart cart = cartList.get(position);
        cart.setItemAmount(String.valueOf(amount));
        cartList.set(position, cart);


        if (amount == 0) {
            if (listener != null) {
                listener.onItemAmountZero(cart);
            }
        }

        getTotalPrice();
        notifyDataSetChanged();
    }

    private void removeItem(int position) {
        cartList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (cartList != null){
            return cartList.size();
        }
        return 0;
    }
    public void setCartList(List<Cart> cartList) {
        this.cartList = cartList;
        notifyDataSetChanged();
    }
    public interface CartInteractionListener {
        void onItemAmountZero(Cart cart);
    }
    public class CartViewHolder extends RecyclerView.ViewHolder{
        private ImageView itemImage;
        private TextView itemId, itemName, itemPrice, itemAmount;
        private ImageButton btnMinus, btnSum;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemId = itemView.findViewById(R.id.itemId);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemAmount = itemView.findViewById(R.id.itemAmount);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnSum = itemView.findViewById(R.id.btnSum);
        }
    }

    // Phương thức tính tổng giá của tất cả các item trong itemList
    public double getTotalPrice() {
        int totalPrice = 0;
        for (Cart cart : cartList) {
            try {
                int price = Integer.parseInt(cart.getItemPrice());
                int amount = Integer.parseInt(cart.getItemAmount());
                totalPrice += price * amount;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return totalPrice;
    }

}