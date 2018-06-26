package com.example.user.dipl1;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.user.dipl1.entity.PostCommentsEntity;
import com.example.user.dipl1.profile.MainActivity;
import com.example.user.dipl1.security.Security;
import com.example.user.dipl1.utils.CommandHelper;
import com.example.user.dipl1.utils.UserStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserCommentAdapter extends BaseAdapter {

    private class Pair {
        PostCommentsEntity item;
        int level;

        Pair (PostCommentsEntity item, int level) {
            this.item = item;
            this.level = level;
        }
    }

    private LayoutInflater mLayoutInflater;
    private ArrayList<Pair> hierarchyArray;

    private ArrayList<PostCommentsEntity> originalItems;
    private LinkedList<PostCommentsEntity> openItems;
    private Context context;
    public boolean isResponse = false;
    public int selectedId;

    int cntr = 0;

    private FragmentActivity fragmentActivity;

    private boolean curFlag = false;

    UserCommentAdapter(Context ctx, ArrayList<PostCommentsEntity> items) {
        mLayoutInflater = LayoutInflater.from(ctx);
        originalItems = items;

        hierarchyArray = new ArrayList<Pair>();
        openItems = new LinkedList<PostCommentsEntity>();

        context = ctx;

        generateHierarchy();
    }

    UserCommentAdapter(Context ctx, ArrayList<PostCommentsEntity> items, boolean curFlag) {
        mLayoutInflater = LayoutInflater.from(ctx);
        originalItems = items;

        hierarchyArray = new ArrayList<Pair>();
        openItems = new LinkedList<PostCommentsEntity>();

        context = ctx;
        this.curFlag = curFlag;

        generateHierarchy();
    }

    UserCommentAdapter(Context ctx, ArrayList<PostCommentsEntity> items, FragmentActivity fragmentActivity, boolean curFlag) {
        mLayoutInflater = LayoutInflater.from(ctx);
        originalItems = items;

        hierarchyArray = new ArrayList<Pair>();
        openItems = new LinkedList<PostCommentsEntity>();

        context = ctx;
        this.fragmentActivity = fragmentActivity;

        this.curFlag = curFlag;

        generateHierarchy();
    }

    @Override
    public int getCount() {
        return hierarchyArray.size();
    }

    @Override
    public PostCommentsEntity getItem(int i) {
        return hierarchyArray.get(i).item;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void add(PostCommentsEntity item){
        originalItems.add(item);
        notifyDataSetChanged();
    }

    public void setOriginalItems(ArrayList<PostCommentsEntity> list){
        originalItems = null;
        originalItems = list;
        notifyDataSetChanged();
    }

    // пункт списка
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.user_commnets_item, null);
        }

        final Pair p = hierarchyArray.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        ((TextView) view.findViewById(R.id.user_doctor_name)).setText(p.item.getUser_id().getUser_name());

        ((TextView) view.findViewById(R.id.user_comment_date)).setText(sdf.format(p.item.getComment_date()));
        ((TextView) view.findViewById(R.id.user_text_comment)).setText(p.item.getComment_value());

        CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.userIconComment);

        circleImageView.setImageBitmap(BitmapFactory.decodeByteArray(
                p.item.getUser_id().getUser_icon(), 0, p.item.getUser_id().getUser_icon().length
        ));

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(MainActivity.INTENT_OBJECT, p.item.getUser_id());
                CommandHelper.replaceFragment(MainActivity.class, fragmentActivity, R.id.container, bundle);
            }
        });

        (view.findViewById(R.id.itemCardComment)).setPadding(p.level * 15, 8, 8, 8);

        if (p.item.getChildsList().size() > 0) {
            ((TextView) view.findViewById(R.id.showMoreCommentsAction)).setText("Показать " + p.item.getChildsList().size() + " ответов");
        }else{
            ((TextView) view.findViewById(R.id.showMoreCommentsAction)).setText("Ответов нет");
        }

        ImageButton btn = (ImageButton)view.findViewById(R.id.otvComment);

        if (Security.getCurrentUser(context.getApplicationContext()).getUser_status().equals(UserStatus.DOCTOR) || curFlag)
            btn.setVisibility(View.VISIBLE);
        else
            btn.setVisibility(View.INVISIBLE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parent = v.getRootView();
                EditText txt = (EditText)parent.findViewById(R.id.inputCommentView);
                txt.setText(hierarchyArray.get(position).item.getUser_id().getUser_name() + ", ");
                txt.requestFocus();
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(txt, InputMethodManager.SHOW_IMPLICIT);
                txt.setSelection(txt.getText().length());
                isResponse = true;
                selectedId = position;
                Log.d("!!!!!!!!", selectedId + "");
            }
        });

        return view;
    }

    private void generateHierarchy() {
        hierarchyArray.clear();
        generateList(originalItems, 0);
    }

    private void generateList(ArrayList<PostCommentsEntity> items, int level) {
        Log.d(">>>>>>>>>>>>>>", " " + (++cntr));
        for (PostCommentsEntity i : items) {
            hierarchyArray.add(new Pair(i, level));
            if (openItems.contains(i)) {
                generateList(i.getChildsList(), level + 1);
            }
        }
    }

    public void clickOnItem (int position) {

        PostCommentsEntity i = hierarchyArray.get(position).item;
        if (!closeItem(i)) {
            openItems.add(i);
        }

        generateHierarchy();
        notifyDataSetChanged();
    }

    private boolean closeItem (PostCommentsEntity i) {
        if (openItems.remove(i)) {
            for (PostCommentsEntity c : i.getChildsList()) {
                closeItem(c);
            }
            return true;
        }
        return false;
    }
}
