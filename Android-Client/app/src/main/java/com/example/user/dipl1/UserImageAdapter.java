package com.example.user.dipl1;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.user.dipl1.local_db.PhotoTable;
import com.example.user.dipl1.utils.CommandHelper;

import java.util.ArrayList;

public class UserImageAdapter extends RecyclerView.Adapter<UserImageAdapter.PostVH> {
    private ArrayList<PhotoTable> listPhoto = new ArrayList<>();
    private Context mContext;
    private FragmentManager fragmentManager;

    public UserImageAdapter(Context c, FragmentManager fragmentManager) {
        mContext = c;
        this.fragmentManager = fragmentManager;
    }

    public int getCount() {
        return listPhoto.size();
    }

    public PhotoTable getItem(int position) {
        return listPhoto.get(position);
    }




    @Override
    public PostVH onCreateViewHolder(ViewGroup parent, int viewType) {
        PostVH viewHolder = null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout, parent, false);
        viewHolder = new PostVH(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PostVH holder, final int position) {
        try {
            if (listPhoto.get(position).getPhoto().length > 0) {
               /* Bitmap bitmap = BitmapFactory.decodeByteArray(listPhoto.get(position).getPhoto(),
                        0,
                        listPhoto.get(position).getPhoto().length);*/
               Bitmap bitmap = CommandHelper.decodeSampledBitmapFromResource(listPhoto.get(position).getPhoto(), 200, 200);
                //bitmap.recycle();
                holder.post_user_item.setImage(ImageSource.bitmap(bitmap));
            }else{
                holder.post_user_item.setImage(ImageSource.resource(R.drawable.placeholder));
            }

            holder.post_user_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = null;
                    Class fragmentClass = null;
                    fragmentClass = UserFullPostFragment.class;

                    Bundle bundle = new Bundle();
                    bundle.putInt("ID", listPhoto.get(position).getPhoto_id());

                    try{
                        fragment = (Fragment) fragmentClass.newInstance();
                        fragment.setArguments(bundle);
                        FragmentTransaction transaction = null;
                        transaction = fragmentManager.beginTransaction();
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        transaction.replace(R.id.container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listPhoto.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) convertView;
        }

        /*imageView.setImageBitmap(BitmapFactory.decodeByteArray(listPhoto.get(position).getPhoto(),
                0,
                listPhoto.get(position).getPhoto().length));*/

        imageView.setImageBitmap(CommandHelper.decodeSampledBitmapFromResource(listPhoto.get(position).getPhoto(), 200, 200));
        return imageView;
    }


    public void addItemNotify(PhotoTable photo){
        listPhoto.add(photo);
    }

    protected class PostVH extends RecyclerView.ViewHolder {
        SubsamplingScaleImageView post_user_item;

        public PostVH(View itemView) {
            super(itemView);
            post_user_item = (SubsamplingScaleImageView) itemView.findViewById(R.id.profileImageView);
            post_user_item.setClickable(true);
            post_user_item.setQuickScaleEnabled(false);
            post_user_item.setZoomEnabled(false);
        }
    }
}
