package exep.fi.chatr.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import exep.fi.chatr.ChatRApplication;
import exep.fi.chatr.R;
import exep.fi.chatr.model.Chat;

/**
 * Created by pietu on 2/5/17.
 */

public class UserListAdapter extends ArrayAdapter<String> {

    ChatRApplication application;
    ArrayList<String> users;

    public UserListAdapter(ChatRApplication application) {
        super(application.getApplicationContext(), R.layout.message_layout);
        this.application = application;
    }

    public UserListAdapter(ChatRApplication application, ArrayList<String> users) {
        super(application.getApplicationContext(), R.layout.message_layout, users);
        this.application = application;
        this.users = users;
    }

    public void setMessages(ArrayList<String> messages) {
        clear();
        addAll(messages);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String user = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_item_layout, parent, false);
        }

        TextView userTv = (TextView) convertView.findViewById(R.id.userText);


        userTv.setText(user);

        return convertView;
    }

}
