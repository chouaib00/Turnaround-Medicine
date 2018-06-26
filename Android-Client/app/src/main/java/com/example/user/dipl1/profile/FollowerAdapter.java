package com.example.user.dipl1.profile;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.dipl1.R;
import com.example.user.dipl1.entity.FollowingEntity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.PersonViewHolder>{

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CircleImageView personIcon;
        TextView personName;

        PersonViewHolder(View itemView) {
            super(itemView);

            personIcon = (CircleImageView) itemView.findViewById(R.id.followerIcon);
            personName = (TextView) itemView.findViewById(R.id.followerName);
        }
    }


    List<FollowingEntity> persons;

    FollowerAdapter(List<FollowingEntity> persons){
        this.persons = persons;
    }


    @Override
    public int getItemCount() {
        return persons.size();
    }


    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.followers_item, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }


    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {

        try {
            if (persons.get(i).getUserId().getUser_work().length() > 0) {
                personViewHolder.personName.setText(persons.get(i).getUserId().getUser_name() + " (" + persons.get(i).getUserId().getUser_work() + ")");

            }else{
                personViewHolder.personName.setText(persons.get(i).getUserId().getUser_name());
            }

            personViewHolder.personIcon.setImageBitmap(
                    BitmapFactory.decodeByteArray(
                            persons.get(i).getUserId().getUser_icon(),
                            0,
                            persons.get(i).getUserId().getUser_icon().length
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
