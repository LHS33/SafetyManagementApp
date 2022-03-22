package com.example.safetymanagementapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class HelmetAdapter extends RecyclerView.Adapter<HelmetAdapter.HelmetViewHolder> {

    private ArrayList<Capture> arrayList;
    private Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();

    public HelmetAdapter(ArrayList<Capture> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public HelmetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.helmet_item, parent, false);
        HelmetViewHolder holder = new HelmetViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HelmetViewHolder holder, int position) {
        // 이미지 출력
        String fileName = arrayList.get(position).getFileName();
        StorageReference captureRef = storageReference.child("capture_image/"+fileName);
        captureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView)
                        .load(uri)
                        .into(holder.iv_imageUrl);
            }
        });
        // 장소 출력
        holder.location.setText(arrayList.get(position).getLocation());
        // 시간 출력
        String date = String.valueOf(arrayList.get(position).getTime());
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6);
        date = year + "년 " + month +"월 " + day + "일";
        holder.time.setText(date);
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class HelmetViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_imageUrl;
        TextView location;
        TextView time;

        public HelmetViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_imageUrl = itemView.findViewById(R.id.capture_image);
            this.location = itemView.findViewById(R.id.text_location);
            this.time = itemView.findViewById(R.id.text_date);

            Dialog dialog1;
            dialog1 = new Dialog((HelmetActivity)context);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.setContentView(R.layout.helmet_dialog);

            // 리사이클러뷰 아이템 클릭 이벤트
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView iv_dialog = dialog1.findViewById(R.id.dialog_image);
                    int pos = getAbsoluteAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Capture item = arrayList.get(pos);
                        String item_name = item.getFileName();
                        StorageReference captureRef = storageReference.child("capture_image/"+item_name);
                        captureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with((HelmetActivity)context)
                                        .load(uri)
                                        .into(iv_dialog);
                            }
                        });
                    }
                    dialog1.show();

                    // 확인 버튼 클릭
                    dialog1.findViewById(R.id.okBtn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog1.dismiss();
                        }
                    });
                }
            });
        }
    }
}
