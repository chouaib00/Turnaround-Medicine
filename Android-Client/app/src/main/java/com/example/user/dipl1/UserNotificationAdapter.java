package com.example.user.dipl1;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.dipl1.realm.Notification;
import com.example.user.dipl1.utils.CommandHelper;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;


public class UserNotificationAdapter extends RealmBaseAdapter {

    private Context ctx;
    private LayoutInflater lInflater;
    private OrderedRealmCollection<Notification> objects;

    UserNotificationAdapter(Context context, @Nullable OrderedRealmCollection<Notification> products) {
        super(products);

        try {
            ctx = context;
            objects = products;
            lInflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Notification getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        try {
            if (view == null) {
                view = lInflater.inflate(R.layout.user_notify_list_item, parent, false);
            }

            Notification p = getProduct(position);

            ((TextView) view.findViewById(R.id.user_date_notify)).setText(p.getNotificationDate());
            ((TextView) view.findViewById(R.id.user_author_notify)).setText(p.getUserFLF());

            if (p.getPostIcon().length > 0) {
                ((CircleImageView) view.findViewById(R.id.user_photo_notify)).setImageBitmap(
                        //BitmapFactory.decodeByteArray(p.getPostIcon(), 0, p.getPostIcon().length)
                        CommandHelper.decodeSampledBitmapFromResource(p.getPostIcon(), 72, 72)
                );
            }else{
                ((CircleImageView) view.findViewById(R.id.user_photo_notify)).setImageResource(R.drawable.placeholder);
            }
            ImageView newBox = (ImageView) view.findViewById(R.id.newBox);

        /*if (p.getNotificationRead() == 0)
            view.setBackgroundColor(ctx.getResources().getColor(R.color.bRos));
        else
            view.setBackgroundColor(ctx.getResources().getColor(R.color.bGreen));*/

            if (p.getNotificationRead() == 0)
                newBox.setVisibility(View.VISIBLE);
            else
                newBox.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    Notification getProduct(int position) {
        return ((Notification) getItem(position));
    }
}
