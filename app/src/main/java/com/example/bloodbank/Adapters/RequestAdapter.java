package com.example.bloodbank.Adapters;

import static android.Manifest.permission.CALL_PHONE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bloodbank.DataModels.RequestModel;
import com.example.bloodbank.R;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

  private List<RequestModel> dataSet;
  private Context context;

  public RequestAdapter(
      List<RequestModel> dataSet, Context context) {
    this.dataSet = dataSet;
    this.context = context;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.request_item_layout, parent, false);
    return new ViewHolder(view);
  }

  //@SuppressLint("RecyclerView") final
  @Override
  public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
    holder.message.setText(dataSet.get(position).getReqMsg());
    holder.phone.setText(dataSet.get(position).getReqPhone());
    holder.location.setText(dataSet.get(position).getReqLocation());
    holder.division.setText(dataSet.get(position).getReqDivision());
    holder.blood.setText(dataSet.get(position).getReqBlood());

    Glide.with(context).load(dataSet.get(position).getReqImage()).into(holder.imageView);
    holder.callButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (PermissionChecker.checkSelfPermission(context, CALL_PHONE)
                == PermissionChecker.PERMISSION_GRANTED) {
          Intent intent = new Intent(Intent.ACTION_CALL);
          intent.setData(Uri.parse("tel:" + dataSet.get(position).getReqPhone()));
          context.startActivity(intent);
        } else {
          ((Activity) context).requestPermissions(new String[]{CALL_PHONE}, 401);
        }
      }
    });

    holder.shareButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                holder.message.getText().toString() +"\n\nLocation: "+holder.location.getText().toString()+"\n\nDivision: "+
                        holder.division.getText().toString()+"\n\nBlood Group: "+holder.blood.getText().toString()+ "\n\nContact: " + dataSet.get(position)
                        .getReqPhone());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Hey, could you help here");
        context.startActivity(Intent.createChooser(shareIntent, "Share..."));
      }
    });
  }


  @Override
  public int getItemCount() {
    return dataSet.size();
  }


  class ViewHolder extends RecyclerView.ViewHolder {

    TextView message,phone,location,division,blood;
    ImageView imageView, callButton, shareButton;

    ViewHolder(final View itemView) {
      super(itemView);
      phone=itemView.findViewById(R.id.mobile);
      location=itemView.findViewById(R.id.location);
      division=itemView.findViewById(R.id.division);
      blood=itemView.findViewById(R.id.blood);
      message=itemView.findViewById(R.id.message);
      imageView=itemView.findViewById(R.id.image);
      callButton = itemView.findViewById(R.id.call_button);
      shareButton=itemView.findViewById(R.id.share_button);


    }

  }

}
