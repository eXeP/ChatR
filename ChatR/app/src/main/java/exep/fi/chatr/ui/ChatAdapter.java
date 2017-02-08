package exep.fi.chatr.ui;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import exep.fi.chatr.ChatRApplication;
import exep.fi.chatr.R;
import exep.fi.chatr.model.Chat;
import exep.fi.chatr.model.ServerOutgoingMessage;

/**
 * Created by pietu on 2/5/17.
 */

public class ChatAdapter extends ArrayAdapter<Chat> {

    ChatRApplication application;
    ArrayList<Chat> messages;

    public ChatAdapter(ChatRApplication application) {
        super(application.getApplicationContext(), R.layout.message_layout);
        this.application = application;
    }

    public ChatAdapter(ChatRApplication application, ArrayList<Chat> messages) {
        super(application.getApplicationContext(), R.layout.message_layout, messages);
        this.application = application;
        this.messages = messages;
    }

    public void setMessages(ArrayList<Chat> messages) {
        clear();
        addAll(messages);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Chat chat = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_item_layout, parent, false);
        }

        TextView chatTv = (TextView) convertView.findViewById(R.id.chatText);
        TextView infoTv = (TextView) convertView.findViewById(R.id.chatInfoText);

        chatTv.setText(chat.getName());
        infoTv.setText("Started on " + DateFormat.getDateInstance().format(new Date(chat.getStarted())) + " by " + chat.getStarter());
        return convertView;
    }

}
