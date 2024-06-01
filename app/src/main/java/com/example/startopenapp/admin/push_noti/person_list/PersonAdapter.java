package com.example.startopenapp.admin.push_noti.person_list;

import static com.example.startopenapp.display_manager.ImageHelper.decodeBase64ToBitmap;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startopenapp.R;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {
    private List<Person> personList;

    public PersonAdapter(List<Person> personList) {
        this.personList = personList;
    }

    public void setPersonList(List<Person> personList) {
        this.personList = personList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_item, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        Person person = personList.get(position);
        if (person == null){
            return;
        }

        String base64String = person.getHinhBase64();
        if (base64String != null && !base64String.isEmpty()){
            Bitmap bitmap = decodeBase64ToBitmap(base64String);

            holder.imgAvatar.setImageBitmap(bitmap);

        }else {
            holder.imgAvatar.setImageResource(R.drawable.account);
        }
        holder.cbselect.setOnCheckedChangeListener((compoundButton, b) -> {
            person.setSelected(!person.isSelected());
            notifyDataSetChanged();
        });
        holder.tvStdId.setText(person.getId());
        holder.tvName.setText(person.getName());
    }

    @Override
    public int getItemCount() {
        if (personList != null){
            return personList.size();
        }
        return 0;
    }

    class PersonViewHolder extends RecyclerView.ViewHolder{

        private CheckBox cbselect;
        private ImageView imgAvatar;
        private TextView tvStdId, tvName;
        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            cbselect = itemView.findViewById(R.id.cbselect);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvStdId = itemView.findViewById(R.id.tvStdId);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
