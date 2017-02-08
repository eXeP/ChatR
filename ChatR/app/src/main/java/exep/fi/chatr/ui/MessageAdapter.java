package exep.fi.chatr.ui;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;

import exep.fi.chatr.ChatRApplication;
import exep.fi.chatr.R;
import exep.fi.chatr.model.ServerOutgoingMessage;

/**
 * Created by pietu on 2/5/17.
 */

public class MessageAdapter extends ArrayAdapter<ServerOutgoingMessage> {

    ChatRApplication application;
    ArrayList<ServerOutgoingMessage> messages;

    public MessageAdapter(ChatRApplication application) {
        super(application.getApplicationContext(), R.layout.message_layout);
        this.application = application;
    }

    public MessageAdapter(ChatRApplication application, ArrayList<ServerOutgoingMessage> messages) {
        super(application.getApplicationContext(), R.layout.message_layout, messages);
        this.application = application;
        this.messages = messages;
    }

    public void addToTop(ArrayList<ServerOutgoingMessage> messages) {
        for (int i = messages.size() - 1; i >= 0; --i) {
            insert(messages.get(i), 0);
        }
        notifyDataSetChanged();
    }

    public void addToBottom(ArrayList<ServerOutgoingMessage> messages) {
        for (int i = 0; i < messages.size(); ++i) {
            add(messages.get(i));
        }
        notifyDataSetChanged();
    }

    public void setMessages(ArrayList<ServerOutgoingMessage> messages) {
        clear();
        addAll(messages);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ServerOutgoingMessage msg = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_layout, parent, false);
        }
        TextView userTv = (TextView) convertView.findViewById(R.id.userText);
        TextView timeTv = (TextView) convertView.findViewById(R.id.timeText);
        TextView messageTv = (TextView) convertView.findViewById(R.id.messageText);
        RelativeLayout msgLayout = (RelativeLayout) convertView.findViewById(R.id.msgLayout);

        if (msg.getUsername().toLowerCase().compareTo(application.getCredentialsUtil().getCredentials().getUsername().toLowerCase()) == 0) {
            msgLayout.setGravity(Gravity.RIGHT);
        } else {
            msgLayout.setGravity(Gravity.LEFT);
        }
        userTv.setText(msg.getUsername());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        timeTv.setText(sdf.format(new Date(msg.getTimestamp())));
        messageTv.setText(msg.getContent());
        return convertView;
    }

}
