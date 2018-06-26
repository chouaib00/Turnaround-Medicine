package com.direct.apps.user.direct;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder{

    TextView messageText, timeText, nameText;
    CircleImageView profileImage;
    private Context context;

    public ReceivedMessageHolder(View itemView, Context context) {
        super(itemView);

        this.context = context;
        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        nameText = (TextView) itemView.findViewById(R.id.text_message_name);
        profileImage = (CircleImageView) itemView.findViewById(R.id.image_message_profile);
    }

    void bind(Message message) {
        messageText.setText(message.getMessage());

        // Format the stored timestamp into a readable String using method.
        timeText.setText(message.getDate());
        nameText.setText(message.getAuthor().getUserName());

        // Insert the profile image from the URL into the ImageView.
        profileImage.setImageBitmap(Helper.createIconForUser(message.getAuthor().getUserName().charAt(0), context));
    }
}
