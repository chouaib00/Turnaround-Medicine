package com.example.user.dipl1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.dipl1.entity.FullPostEntity;
import com.example.user.dipl1.entity.PostFavoritesEntity;
import com.example.user.dipl1.network.QueryRepository;
import com.example.user.dipl1.security.Security;
import com.example.user.dipl1.utils.CommandHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 21.08.2017.
 */



public class UserPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<FullPostEntity> posts;
    private Context context;

    private boolean isLoadingAdded = false;

     int counter = 0;

    QueryRepository repository;
    Handler handler;

    @SuppressLint("HandlerLeak")
    public UserPostAdapter(final Context context) {
        this.context = context;
        posts = new ArrayList<>();

        repository = new QueryRepository();
        repository.InitializeRetrofit(context);

        handler = new Handler(){
            public void handleMessage(Message message){
                if (message.arg1 == 1){
                    Toast.makeText(context, "Запись добавлена в список Избранное", Toast.LENGTH_SHORT).show();
                }else if (message.arg1 == 2){
                    Toast.makeText(context, "Запись уже находится в списке Избранное", Toast.LENGTH_SHORT).show();
                }
            }
        };

    }

    public List<FullPostEntity> getPosts() {
        return posts;
    }

    public void setPosts(List<FullPostEntity> posts) {
        this.posts = posts;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.user_news_list_item, parent, false);
        viewHolder = new PostVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final FullPostEntity post = posts.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        try {

            switch (getItemViewType(position)) {
                case ITEM:
                    PostVH postVH = (PostVH) holder;

                    postVH.icon_user_item.setImageBitmap(
                            BitmapFactory.decodeByteArray(post.getUser_id().getUser_icon(), 0, post.getUser_id().getUser_icon().length)
                    );

                    postVH.nick_user_item.setText(post.getUser_id().getUser_name());
                    postVH.date_user_item.setText(sdf.format(post.getPost_date()));



                    if (post.getPhoto().length > 0) {
                        postVH.post_user_item.setVisibility(View.VISIBLE);
                        postVH.post_user_item.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        postVH.user_post_text_view.setVisibility(View.INVISIBLE);
                        //Bitmap bitmap = BitmapFactory.decodeByteArray(post.getPhoto(), 0, post.getPhoto().length);
                        Bitmap bitmap = CommandHelper.decodeSampledBitmapFromResource(post.getPhoto(), 1024, 600);
                        postVH.post_user_item.setImageBitmap(bitmap);
                    } else {
                        postVH.post_user_item.setVisibility(View.INVISIBLE);
                        postVH.post_user_item.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
                        postVH.user_post_text_view.setVisibility(View.VISIBLE);
                        postVH.user_post_text_view.setText(post.getDescription());
                    }


                    postVH.user_count_com_item.setText(String.valueOf(counter));

                    postVH.like_user_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PostFavoritesEntity postFavoritesEntity = new PostFavoritesEntity();
                            postFavoritesEntity.setPost_id(posts.get(position));
                            postFavoritesEntity.setUserId(Security.getCurrentUser(context.getApplicationContext()));
                            repository.getApi().addFavorite(postFavoritesEntity).enqueue(new Callback<PostFavoritesEntity>() {
                                @Override
                                public void onResponse(Call<PostFavoritesEntity> call, Response<PostFavoritesEntity> response) {
                                    if (response.body() != null) {
                                        Message message = new Message();
                                        message.arg1 = 1;
                                        handler.sendMessage(message);
                                    } else {
                                        Message message = new Message();
                                        message.arg1 = 2;
                                        handler.sendMessage(message);
                                    }
                                }

                                @Override
                                public void onFailure(Call<PostFavoritesEntity> call, Throwable t) {
                                    Message message = new Message();
                                    message.arg1 = 2;
                                    handler.sendMessage(message);
                                }
                            });
                        }
                    });
                    break;
                case LOADING:
                    //Do nothing
                    break;
            }

        }catch(Exception e){

        }
    }



    @Override
    public int getItemCount() {
        return posts == null ? 0 : posts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == posts.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    //helpers

    public void add(FullPostEntity pe, int cnt) {
        try {
            posts.add(pe);
            notifyItemInserted(posts.size() - 1);
            counter = cnt;
        }catch(Exception e){

        }
    }

    public void addAll(List<FullPostEntity> peList, int cnt) {
        for (FullPostEntity pe : peList) {
            add(pe, cnt);
        }
    }

    public void remove(FullPostEntity pe) {
        try {
            int position = posts.indexOf(pe);
            if (position > -1) {
                posts.remove(position);
                notifyItemRemoved(position);
            }
        }catch (Exception e){

        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        try {
            isLoadingAdded = true;
            add(new FullPostEntity(), counter);
        }catch (Exception e){

        }
    }

    public void removeLoadingFooter() {
        try {
            isLoadingAdded = false;

            int position = posts.size() - 1;
            FullPostEntity item = getItem(position);

            if (item != null) {
                posts.remove(position);
                notifyItemRemoved(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FullPostEntity getItem(int position) {
        return posts.get(position);
    }

    //view holders

    protected class PostVH extends RecyclerView.ViewHolder {
        CardView cv;
        CircleImageView icon_user_item;
        TextView nick_user_item;
        TextView date_user_item;
        ImageView post_user_item;
        ImageButton like_user_item;
        ImageView imageViewCom;
        TextView user_count_com_item;
        TextView user_post_text_view;

        public PostVH(View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.cv);
            icon_user_item = (CircleImageView) itemView.findViewById(R.id.icon_user_item);
            nick_user_item = (TextView)itemView.findViewById(R.id.nick_user_item);
            date_user_item = (TextView)itemView.findViewById(R.id.date_user_item);
            post_user_item = (ImageView)itemView.findViewById(R.id.post_user_item);
            like_user_item = (ImageButton)itemView.findViewById(R.id.like_user_item);
            imageViewCom = (ImageView)itemView.findViewById(R.id.imageViewCom);
            user_count_com_item = (TextView)itemView.findViewById(R.id.user_count_com_item);
            user_post_text_view = (TextView)itemView.findViewById(R.id.post_user_item_text);
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }
}
