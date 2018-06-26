package com.example.user.dipl1;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.user.dipl1.entity.FullPostEntity;
import com.example.user.dipl1.entity.PostTagsEntity;
import com.example.user.dipl1.network.QueryRepository;
import com.example.user.dipl1.security.Security;
import com.example.user.dipl1.utils.CommandHelper;
import com.example.user.dipl1.utils.CustomFaceDetector;
import com.example.user.dipl1.utils.PostStatus;
import com.fxn.cue.Cue;
import com.fxn.cue.enums.Duration;
import com.fxn.cue.enums.Type;
import com.geniusforapp.fancydialog.FancyAlertDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Response;
import ru.whalemare.sheetmenu.SheetMenu;

public class AddNewPostFragment extends Fragment {

    //Button rotatePhoto;
    Button savePhoto;
    Bitmap bitmap;
    Bitmap originalBitmap;
    ImageView imageView;

    EditText descriptionText;
    EditText tagsText;
    CheckBox privatePhoto;
    CheckBox hideFace;

    FullPostEntity postEntity;
    QueryRepository repository;
    String[] tags;

    TextInputLayout textInputLayout;

    CustomFaceDetector customFaceDetector;

    FancyAlertDialog.Builder alert;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            repository = new QueryRepository();
            repository.InitializeRetrofit(getContext().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            postEntity = new FullPostEntity();
            postEntity.setUser_id(Security.getCurrentUser(getContext().getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        try {
            postEntity = null;
            savePhoto = null;
            originalBitmap = null;
            bitmap = null;
            tags = null;
            alert = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_post, container, false);

        try {
            textInputLayout = (TextInputLayout)view.findViewById(R.id.uil2);
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError("Введите ключевые слова через запятую");

            Bundle param = getArguments();

            int px = getResources().getDimensionPixelSize(R.dimen.image_size);

            if (param != null) {
                byte[] array = param.getByteArray("photo");
                //bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
                bitmap = decodeSampledBitmapFromResource(array, px, px);
                originalBitmap = bitmap;
            }

            param.clear();

            imageView = (ImageView)view.findViewById(R.id.add_new_photo_view);
            imageView.setImageBitmap(bitmap);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SheetMenu.with(getContext())
                            .setAutoCancel(true)
                            .setTitle("Редактирование изображения")
                            .setMenu(R.menu.sheet)
                            .showIcons(true)
                            .setClick(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    if (item.getItemId() == R.id.rotateItem){

                                        bitmap = RotateBitmap(bitmap, 90);
                                        originalBitmap = bitmap;
                                        imageView.setImageBitmap(bitmap);
                                    }else if (item.getItemId() == R.id.hideItem){

                                        if (!hideFace.isChecked()){
                                            hideFaceForPicture();
                                            hideFace.setChecked(true);
                                        }
                                    }
                                    return true;
                                }
                            })
                            .show();
                }
            });

            //rotatePhoto = (Button)view.findViewById(R.id.rotate_post_button);
            savePhoto = (Button)view.findViewById(R.id.load_post_button);


            savePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alert = new FancyAlertDialog.Builder(getContext())
                            .setimageResource(R.drawable.zzz_comment_question_outline)
                            .setTextTitle("Turnaround Medicine")
                            .setTextSubTitle("Подтверждение публикации")
                            .setBody("Опубликовать пост?")
                            .setNegativeColor(R.color.md_red300)
                            .setNegativeButtonText("Нет")
                            .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                                @Override
                                public void OnClick(View view, Dialog dialog) {
                                    CommandHelper.replaceFragment(UserNewsFragment.class, getActivity(), R.id.container, null);
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButtonText("Да")
                            .setPositiveColor(R.color.md_green300)
                            .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                                @Override
                                public void OnClick(View view, Dialog dialog) {

                                    //
                                    if (descriptionText.getText().length() > 0 && tagsText.getText().length() > 0) {

                                        postEntity.setDescription(String.valueOf(descriptionText.getText()));

                                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);

                                        postEntity.setPhoto(bs.toByteArray());

                                        postEntity.setPost_date(new java.util.Date());

                                        if (privatePhoto.isChecked()) {
                                            postEntity.setPost_status(PostStatus.PRIVATE);
                                        } else if (!privatePhoto.isChecked()) {
                                            postEntity.setPost_status(PostStatus.PUBLIC);
                                        }



                                        Thread t2 = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    synchronized (postEntity) {
                                                        Response<FullPostEntity> post = repository.getApi().addPost(postEntity).execute();
                                                        postEntity = null;
                                                        postEntity = post.body();
                                                    }
                                                } catch (IOException e) {
                                                    Log.d(">>>>>>>>>>>>>>>", "userPOST ERROR");
                                                }
                                            }
                                        });
                                        t2.start();

                                        tags = getTagsArray(String.valueOf(tagsText.getText()));

                                        Thread t3 = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    synchronized (postEntity) {
                                                        for (int i = 0; i < tags.length; i++) {
                                                            PostTagsEntity pt = new PostTagsEntity();
                                                            pt.setTag_value(tags[i]);
                                                            pt.setPost_id(postEntity);
                                                            repository.getApi().addTag(pt).execute();
                                                        }
                                                    }

                                                    postToastMessage("Данные успешно опубликованы");

                                                } catch (IOException e) {
                                                    Log.d(">>>>>>>>>>>>>>>", "userTags ERROR");
                                                }
                                            }
                                        });
                                        t3.start();
                                    }else{
                                        Cue.init()
                                                .with(getContext())
                                                .setMessage("Введите описание и теги!")
                                                .setGravity(Gravity.CENTER_HORIZONTAL| Gravity.BOTTOM)
                                                .setType(Type.WARNING)
                                                .setDuration(Duration.LONG)
                                                .setBorderWidth(1)
                                                .setCornerRadius(10)
                                                .setPadding(30)
                                                .setTextSize(20)
                                                .show();
                                    }
                                    //

                                    dialog.cancel();
                                }
                            })
                            .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                            .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                            .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                            .setCancelable(false)
                            .build();

                    alert.show();
                }
            });


            descriptionText = (EditText)view.findViewById(R.id.add_description_new);
            tagsText = (EditText)view.findViewById(R.id.add_tags_new);
            privatePhoto = (CheckBox)view.findViewById(R.id.PrivatePhotocheckBox);
            hideFace = (CheckBox)view.findViewById(R.id.HideFacecheckBox);

            hideFace.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == true){
                        hideFaceForPicture();
                    }else{
                        bitmap = originalBitmap;
                        imageView.setImageBitmap(bitmap);
                    }
                }
            });
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

        return view;
    }

    public Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public Bitmap decodeSampledBitmapFromResource(byte[] pic,
                                                         int reqWidth, int reqHeight) {

        // Читаем с inJustDecodeBounds=true для определения размеров
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(pic, 0, pic.length, options);

        // Вычисляем inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Читаем с использованием inSampleSize коэффициента
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeByteArray(pic, 0, pic.length, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Реальные размеры изображения
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Вычисляем наибольший inSampleSize, который будет кратным двум
            // и оставит полученные размеры больше, чем требуемые
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public String[] getTagsArray(String value){
        return value.split(",");
    }


    public void postToastMessage(final String message) {
        try {
            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {

                @Override
                public void run() {
                    Cue.init()
                            .with(getContext())
                            .setMessage(message)
                            .setGravity(Gravity.CENTER_HORIZONTAL| Gravity.BOTTOM)
                            .setType(Type.SUCCESS)
                            .setDuration(Duration.LONG)
                            .setBorderWidth(1)
                            .setCornerRadius(10)
                            .setPadding(30)
                            .setTextSize(20)
                            .show();

                    CommandHelper.replaceFragment(UserNewsFragment.class, getActivity(), R.id.container, null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideFaceForPicture(){
        try {
            customFaceDetector = new CustomFaceDetector(getContext(), bitmap);
            bitmap = customFaceDetector.hideFace();
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
