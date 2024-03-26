package com.example.sync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private final List<Notification> notifications;
    private final LayoutInflater mInflater;

    // Constructor
    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.mInflater = LayoutInflater.from(context);
        this.notifications = notifications;
    }

    // ViewHolder class to hold each item layout
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notificationTitle);
            message = itemView.findViewById(R.id.notificationMessage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ensure the correct layout is inflated
        View view = mInflater.inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.title.setText(notification.getTitle());
        holder.message.setText(notification.getMessage());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}

