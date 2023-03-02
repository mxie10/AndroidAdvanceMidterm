package com.example.midterm;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.midterm.model.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messages;
    private LayoutInflater mInflater;

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView sendTextView;
        TextView botTextView;
        ViewHolder(View itemView) {
            super(itemView);
            sendTextView = itemView.findViewById(R.id.tv_message);
            botTextView = itemView.findViewById(R.id.tv_bot_message);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            messages.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
        }
    }

    // data is passed into the constructor
    MessageAdapter(Context context, List<Message> messageList) {
        this.mInflater = LayoutInflater.from(context);
        this.messages = messageList;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.message_item_view, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messages.get(position);
        switch (message.getType()) {
            case "SEND":
                holder.sendTextView.setText(message.getMessage());
                holder.sendTextView.setVisibility(View.VISIBLE);
                holder.botTextView.setVisibility(View.GONE);
                break;
            case "RECEIVE":
                holder.botTextView.setText(message.getMessage());
                holder.botTextView.setVisibility(View.VISIBLE);
                holder.sendTextView.setVisibility(View.GONE);
                break;
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        Log.v("getItemCount", messages.size()+"");
        return messages.size();
    }

    public void insertMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size());
    }
}






