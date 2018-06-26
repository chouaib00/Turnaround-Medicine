package com.direct.apps.user.direct;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class SentMessageHolder extends RecyclerView.ViewHolder{

    TextView messageText, timeText;

    public SentMessageHolder(View itemView) {
        super(itemView);

        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        timeText = (TextView) itemView.findViewById(R.id.text_message_time);
    }

    void bind(Message message) {
        messageText.setText(message.getMessage());

        // Format the stored timestamp into a readable String using method.
        timeText.setText(message.getDate());
    }
}
