package com.example.startopenapp.admin.approve_request.recharge_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startopenapp.R;

import java.util.List;

public class ApproveAdapter extends RecyclerView.Adapter<ApproveAdapter.ApproveViewHolder> {
    private List<Approve> approveList;
    private boolean isGone;
    private OnItemLongClickListener onItemLongClickListener;
    public interface OnItemLongClickListener {
        void onItemLongClick(Approve approve);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public ApproveAdapter(List<Approve> approveList, boolean isGone) {
        this.approveList = approveList;
        this.isGone = isGone;
    }

    @NonNull
    @Override
    public ApproveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.approve_item, parent, false);
        return new ApproveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApproveViewHolder holder, int position) {
        Approve approve = approveList.get(position);
        if (approve == null){
            return;
        }
        holder.bind(approve, isGone);

        holder.itemView.setOnLongClickListener(view -> {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(approve);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        if (approveList != null){
            return approveList.size();
        }
        return 0;
    }

    public class ApproveViewHolder extends RecyclerView.ViewHolder{
        private TextView itemAccId, itemMaDon, itemSotien,itemLoiNhan, itemTime;
        private LinearLayout lnrTkNhan;
        public ApproveViewHolder(@NonNull View itemView) {
            super(itemView);
            itemAccId = itemView.findViewById(R.id.itemAccId);
            itemMaDon = itemView.findViewById(R.id.itemMaDon);
            itemSotien= itemView.findViewById(R.id.itemSotien);
            itemLoiNhan= itemView.findViewById(R.id.itemLoiNhan);
            itemTime  = itemView.findViewById(R.id.itemTime);
            lnrTkNhan = itemView.findViewById(R.id.lnrTkNhan);
        }

        public void bind(Approve approve, boolean isGone) {
            itemAccId.setText(approve.getAccid());
            itemMaDon.setText(approve.getMadon());
            itemSotien.setText(approve.getSotien());
            itemLoiNhan.setText(approve.getLoinhan());
            itemTime.setText(approve.getTime());

            // Hiển thị hoặc ẩn itemLoiNhan dựa trên giá trị của isGone
            lnrTkNhan.setVisibility(isGone ? View.GONE : View.VISIBLE);
        }
    }
    public void setApproveList(List<Approve> approveList){
        this.approveList.clear();
        this.approveList.addAll(approveList);
        notifyDataSetChanged();
    }
}
