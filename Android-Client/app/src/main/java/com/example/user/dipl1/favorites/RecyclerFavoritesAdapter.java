package com.example.user.dipl1.favorites;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.dipl1.R;
import com.example.user.dipl1.entity.PostFavoritesEntity;
import com.example.user.dipl1.network.QueryRepository;
import com.example.user.dipl1.utils.CommandHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerFavoritesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<PostFavoritesEntity> posts;
    private Context context;

    private boolean isLoadingAdded = false;
    QueryRepository repository;

    public RecyclerFavoritesAdapter(Context context) {
        this.context = context;
        repository = new QueryRepository();
        repository.InitializeRetrofit(context);

        posts = new ArrayList<>();
    }

    public List<PostFavoritesEntity> getPosts() {
        return posts;
    }

    public void setPosts(List<PostFavoritesEntity> posts) {
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
                viewHolder = new RecyclerFavoritesAdapter.LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.user_favorites_list_item, parent, false);
        viewHolder = new RecyclerFavoritesAdapter.PostVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        try {
            PostFavoritesEntity post = posts.get(position);

            switch (getItemViewType(position)) {
                case ITEM:
                    PostVH postVH = (PostVH) holder;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                    postVH.icon_user_item.setImageBitmap(
                            CommandHelper.decodeSampledBitmapFromResource(post.getPost_id().getUser_id().getUser_icon(), 48, 48)
                           // BitmapFactory.decodeByteArray(post.getPost_id().getUser_id().getUser_icon(), 0, post.getPost_id().getUser_id().getUser_icon().length)
                    );
                    postVH.nick_user_item.setText(post.getPost_id().getUser_id().getUser_name());
                    postVH.date_user_item.setText(sdf.format(post.getPost_id().getPost_date()));

                    if (post.getPost_id().getPhoto().length > 0) {
                        //Bitmap bitmap = BitmapFactory.decodeByteArray(post.getPost_id().getPhoto(), 0, post.getPost_id().getPhoto().length);
                        Bitmap bitmap = CommandHelper.decodeSampledBitmapFromResource(post.getPost_id().getPhoto(), 300, 300);
                        postVH.post_user_item.setImageBitmap(bitmap);
                    }else{
                        postVH.post_user_item.setImageResource(R.drawable.placeholder);
                    }

                    break;
                case LOADING:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public void add(PostFavoritesEntity pe) {
        posts.add(pe);
        notifyItemInserted(posts.size() - 1);
    }

    public void addAll(List<PostFavoritesEntity> peList) {
        for (PostFavoritesEntity pe : peList) {
            add(pe);
        }
    }

    public void remove(PostFavoritesEntity pe) {
        int position = posts.indexOf(pe);
        if (position > -1) {
            posts.remove(position);
            notifyItemRemoved(position);
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
        isLoadingAdded = true;
        add(new PostFavoritesEntity());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = posts.size() - 1;
        PostFavoritesEntity item = getItem(position);

        if (item != null) {
            posts.remove(position);
            notifyItemRemoved(position);
        }
    }

    public PostFavoritesEntity getItem(int position) {
        return posts.get(position);
    }

    //view holders
    protected class PostVH extends RecyclerView.ViewHolder {
        RelativeLayout layout;
        CardView cv;
        CircleImageView icon_user_item;
        TextView nick_user_item;
        TextView date_user_item;
        ImageView post_user_item;
        Spinner spinner;

        public PostVH(View itemView) {
            super(itemView);

            layout = (RelativeLayout)itemView.findViewById(R.id.frel);
            cv = (CardView)itemView.findViewById(R.id.cv2);
            icon_user_item = (CircleImageView)itemView.findViewById(R.id.icon_user_item2);
            nick_user_item = (TextView)itemView.findViewById(R.id.nick_user_item2);
            date_user_item = (TextView)itemView.findViewById(R.id.date_user_item2);
            post_user_item = (ImageView)itemView.findViewById(R.id.post_user_item2);
            spinner = (Spinner)itemView.findViewById(R.id.spinMenu);
            fillSpiner(itemView.getContext());
        }

        public void fillSpiner(Context ctx){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, new String[]{"", "Удалить"}){
                @Override
                public View getDropDownView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getDropDownView(position + 1, convertView, parent);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(">>>>>>>>", " " + getLayoutPosition());
                            Log.d(">>>>>>>>", " " + RecyclerFavoritesAdapter.this.getItem(getLayoutPosition()).getPost_id().getPost_date().toString());

                            new RecyclerFavoritesAdapter.DeleteTask().execute(RecyclerFavoritesAdapter.this.getItem(getLayoutPosition()).getFavorit_id());

                            RecyclerFavoritesAdapter.this.remove(RecyclerFavoritesAdapter.this.getItem(getLayoutPosition()));
                            notifyDataSetChanged();
                        }
                    });
                    return view;
                }

                @Override
                public int getCount() {
                    return 1;
                }

            };
            spinner.setAdapter(adapter);
        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    private class DeleteTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            try {
                repository.getApi().deleteFavorite(integers[0]).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(context, "Запись удалена из избранного", Toast.LENGTH_SHORT).show();
        }
    }
}
