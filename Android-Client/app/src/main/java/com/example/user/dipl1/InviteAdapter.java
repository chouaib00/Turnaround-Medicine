package com.example.user.dipl1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.dipl1.entity.InviteEntity;

import java.util.List;

public class InviteAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<InviteEntity> invites;

    public InviteAdapter(Context context, List<InviteEntity> invites) {
        this.context = context;
        this.invites = invites;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return invites.size();
    }

    @Override
    public InviteEntity getItem(int position) {
        return invites.get(position);
    }

    @Override
    public long getItemId(int position) {
        return invites.get(position).getInviteId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.invite_item, parent, false);
            }

            InviteEntity invite = getItem(position);

            ((TextView) view.findViewById(R.id.inviteTextView))
                    .setText("Приглашение: " + invite.getInviteFrom().getUser_name() + "/" + invite.getInviteTo().getUser_name());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}
