package com.example.bloodbank.Adapters;

import static android.Manifest.permission.CALL_PHONE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bloodbank.DataModels.DonorModel;
import com.example.bloodbank.DataModels.RequestModel;
import com.example.bloodbank.R;

import java.util.List;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.ViewHolder> {

    private List<DonorModel> dataSet;
    private Context context;

    public DonorAdapter(
            List<DonorModel> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fetch_donor_layout, parent, false);
        return new ViewHolder(view);
    }

    //@SuppressLint("RecyclerView") final
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(dataSet.get(position).getName());
        holder.department.setText(dataSet.get(position).getDepartment());
        holder.batch.setText(dataSet.get(position).getBatch());
        holder.district.setText(dataSet.get(position).getDistrict());
        holder.blood.setText(dataSet.get(position).getBlood_group());
        holder.phone.setText(dataSet.get(position).getNumber());

        Glide.with(context).load(dataSet.get(position).getPhoto()).into(holder.imageView);
        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionChecker.checkSelfPermission(context, CALL_PHONE)
                        == PermissionChecker.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + dataSet.get(position).getNumber()));
                    context.startActivity(intent);
                } else {
                    ((Activity) context).requestPermissions(new String[]{CALL_PHONE}, 401);
                }
            }
        });

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "Donor Information" +"\n\nName: "+holder.name.getText().toString()+"\n\nDepartment: "+
                                holder.department.getText().toString()+"\n\nBatch: "+holder.batch.getText().toString()+ "\n\nDistrict: " + holder.district.getText().toString()
                                +"\n\nBlood Group: "+holder.blood.getText().toString()+"\n\nContact: "+holder.phone.getText().toString());
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Here's the donor Info");
                context.startActivity(Intent.createChooser(shareIntent, "Share..."));
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,department,batch,district,blood,phone;
        ImageView imageView, callButton, shareButton;

        ViewHolder(final View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.name);
            department=itemView.findViewById(R.id.department);
            batch=itemView.findViewById(R.id.batch);
            district=itemView.findViewById(R.id.district);
            blood=itemView.findViewById(R.id.blood);
            phone=itemView.findViewById(R.id.phone);
            imageView=itemView.findViewById(R.id.image);
            callButton = itemView.findViewById(R.id.call_button);
            shareButton=itemView.findViewById(R.id.share_button);


        }

    }

}

