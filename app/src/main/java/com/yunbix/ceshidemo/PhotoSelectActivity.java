package com.yunbix.ceshidemo;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hellosliu.easyrecyclerview.EasyRecylerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * Created by Administrator on 2017/4/14.
 */

public class PhotoSelectActivity extends Activity {
    private boolean flag = true;
    private EasyRecylerView mEasyRecylerView;
    private List<BaseBean> list = new ArrayList<>();
    public static String IMAGE_NUM = "imagenum";
    public static String DATA = "data";
    private int intExtra;
    private TextView tv_cancel;
    final List<String> data = new ArrayList<>();
    final List<String> imdata = new ArrayList<>();
    private LinearLayout back;
    private HashMap<Integer, String> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        intExtra = intent.getIntExtra(IMAGE_NUM, 0);
        setContentView(R.layout.activity_photoselect);
        EventBus.getDefault().register(this);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mEasyRecylerView = (EasyRecylerView) findViewById(R.id.mEasyRecylerView);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imdata.clear();
                for (int i = 0; i < map.size(); i++) {
                    String s = map.get(i);
                    if (s.equals("true")) {
                        BaseBean bean = list.get(i);
                        imdata.add(bean.getUrl());
                    }
                }
                if (imdata.size() == 0) {
                    Toast.makeText(PhotoSelectActivity.this, "请选择图片", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent1 = new Intent(PhotoSelectActivity.this, ImagePreviewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", (Serializable) imdata);
                    intent1.putExtras(bundle);
                    startActivity(intent1);
                }
            }
        });
        getPath();
        for (int i = 0; i < list.size(); i++) {
            map.put(i, "false");
        }
        final PhotoSelectAdapter adapter = new PhotoSelectAdapter(this);
        mEasyRecylerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        mEasyRecylerView.setAdapter(adapter);
        adapter.setOnVideoClickLinstener(new OnVideoClickLinstener() {
            @Override
            public void OnVideoClick(int position) {
                if (flag) {
                    Intent intent1 = new Intent(PhotoSelectActivity.this, VideoPreviewActivity.class);
                    intent1.putExtra("videopath", list.get(position).getUrl());
                    startActivity(intent1);
                } else {
                    Toast.makeText(PhotoSelectActivity.this, "您已选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getPath() {
                /*获取视频列表*/
        String[] projections = {
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION
        };

        for (int a = 0; a < projections.length; a++) {
            Log.e("projections", projections[a] + "");
        }
        String orderBys = MediaStore.Video.Media.DISPLAY_NAME;
        Uri uris = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        getVideoContentProvider(uris, projections, orderBys);//视频获取报错
         /*获取图片列表*/
        String[] projection = {
                MediaStore.Images.Media.DATA};
        String orderBy = MediaStore.Images.Media.DISPLAY_NAME;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        getImageContentProvider(uri, projection, orderBy);
    }

    /*获取手机所有图片地址*/
    public void getImageContentProvider(Uri uri, String[] projection, String orderBy) {
        // TODO Auto-generated method stub
        Cursor cursor = getContentResolver().query(uri, projection, null,
                null, orderBy);
        if (null == cursor) {
            return;
        }
        while (cursor.moveToNext()) {
            for (int i = 0; i < projection.length; i++) {
                BaseBean bean = new BaseBean();
                bean.setUrl(cursor.getString(i));
                bean.setIsvideo(false);
                list.add(bean);
            }
        }
    }

    /*获取手机所有视频地址和视频时长*/
    public void getVideoContentProvider(Uri uri, String[] projection, String orderBy) {
        // TODO Auto-generated method stub
        Cursor cursor = getContentResolver().query(uri, projection, null,
                null, orderBy);
        if (null == cursor) {
            return;
        }
        List<String> path = new ArrayList<>();
        List<String> time = new ArrayList<>();
        while (cursor.moveToNext()) {
            for (int i = 0; i < projection.length; i++) {
                if (i % 2 == 0) {
                    path.add(cursor.getString(i));
                } else {
                    time.add(cursor.getString(i) + "");
                }
            }
        }
        Log.e("path", path + "");
        Log.e("time", time + "");
        for (int i = 0; i < path.size(); i++) {
            String s = time.get(i);
            Log.e("s", s + "");
            long l = Long.parseLong(s);
            int times = (int) l / 1000;
            if (times < 10 && times > 0) {
                BaseBean bean = new BaseBean();
                bean = new BaseBean();
                bean.setIsvideo(true);
                bean.setUrl(path.get(i));
                bean.setVideosize("00:0" + times);
                list.add(bean);
            }
        }

    }

    public interface OnVideoClickLinstener {
        void OnVideoClick(int position);
    }

    public interface OnImageClickLinstener {
        void OnImageClick(int position);
    }

    public interface OnImageSelectClickLinstener {
        void OnImageSelectClick(int position, boolean b, CheckBox box);
    }

    class PhotoSelectAdapter extends EasyRecylerView.Adapter {
        private Context context;
        private OnVideoClickLinstener onVideoClickLinstener;
        private OnImageClickLinstener onImageClickLinstener;
        private OnImageSelectClickLinstener onImageSelectClickLinstener;

        public PhotoSelectAdapter(Context context) {
            this.context = context;
        }

        public void setOnImageSelectClickLinstener(OnImageSelectClickLinstener onImageSelectClickLinstener) {
            this.onImageSelectClickLinstener = onImageSelectClickLinstener;
        }

        public void setOnVideoClickLinstener(OnVideoClickLinstener onVideoClickLinstener) {
            this.onVideoClickLinstener = onVideoClickLinstener;
        }

        public void setOnImageClickLinstener(OnImageClickLinstener onImageClickLinstener) {
            this.onImageClickLinstener = onImageClickLinstener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {        //视频地址
                View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
                return new PhotoSelectVideoHolder(view);
            } else {                  //图片地址
                View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
                return new PhotoSelectImageHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            int type = getItemViewType(position);
            BaseBean baseBean = list.get(position);
            if (type == 0) {
                PhotoSelectVideoHolder videoHolder = (PhotoSelectVideoHolder) holder;
                Glide.with(context).load(baseBean.getUrl()).into(videoHolder.video_iv);
                videoHolder.video_time.setText(baseBean.getVideosize());
            } else {
                final PhotoSelectImageHolder imageHolder = (PhotoSelectImageHolder) holder;
                Glide.with(context).load(baseBean.getUrl()).into(imageHolder.image_iv);
                String s = map.get(position);
                if (s.equals("true")) {
                    imageHolder.image_mCheckBox.setImageResource(R.mipmap.ok);
                } else {
                    imageHolder.image_mCheckBox.setImageResource(R.mipmap.no);
                }
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            BaseBean bean = list.get(position);
            if (bean.isvideo()) {
                return 0;
            } else {
                return 1;
            }
        }

        class PhotoSelectVideoHolder extends EasyRecylerView.ViewHolder {
            TextView video_time;
            ImageView video_iv;
            LinearLayout video_ll;

            public PhotoSelectVideoHolder(View itemView) {
                super(itemView);
                video_time = (TextView) itemView.findViewById(R.id.video_time);
                video_iv = (ImageView) itemView.findViewById(R.id.video_iv);
                video_ll = (LinearLayout) itemView.findViewById(R.id.video_ll);
                video_ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onVideoClickLinstener.OnVideoClick(getAdapterPosition());
                    }
                });
            }
        }

        class PhotoSelectImageHolder extends EasyRecylerView.ViewHolder {
            ImageView image_mCheckBox;
            LinearLayout image_ll;
            ImageView image_iv;

            public PhotoSelectImageHolder(View itemView) {
                super(itemView);
                image_mCheckBox = (ImageView) itemView.findViewById(R.id.image_mCheckBox);
                image_ll = (LinearLayout) itemView.findViewById(R.id.image_ll);
                image_iv = (ImageView) itemView.findViewById(R.id.image_iv);
                image_ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String s = map.get(getAdapterPosition());
                        if (s.equals("true")) {
                            image_mCheckBox.setImageResource(R.mipmap.no);
                            map.put(getAdapterPosition(), "false");
                        } else {
                            if (getmapsize() < intExtra) {
                                image_mCheckBox.setImageResource(R.mipmap.ok);
                                map.put(getAdapterPosition(), "true");
                            } else {
                                image_mCheckBox.setImageResource(R.mipmap.no);
                                Toast.makeText(context, "您已选择" + intExtra + "张图片", Toast.LENGTH_SHORT).show();
                                map.put(getAdapterPosition(), "false");
                            }
                        }
                        if (getmapsize() == 0) {
                            flag = true;
                        } else {
                            flag = false;
                        }
                    }
                });
                image_mCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String s = map.get(getAdapterPosition());
                        if (s.equals("true")) {
                            image_mCheckBox.setImageResource(R.mipmap.no);
                            map.put(getAdapterPosition(), "false");
                        } else {
                            if (getmapsize() < intExtra) {
                                image_mCheckBox.setImageResource(R.mipmap.ok);
                                map.put(getAdapterPosition(), "true");
                            } else {
                                image_mCheckBox.setImageResource(R.mipmap.no);
                                Toast.makeText(context, "您已选择" + intExtra + "张图片", Toast.LENGTH_SHORT).show();
                                map.put(getAdapterPosition(), "false");
                            }
                        }
                        if (getmapsize() == 0) {
                            flag = true;
                        } else {
                            flag = false;
                        }
                    }
                });
            }

            private int getmapsize() {
                int count = 0;
                for (int i = 0; i < map.size(); i++) {
                    String s = map.get(i);
                    if (s.equals("true")) {
                        count++;
                    }
                }
                return count;
            }
        }
    }

    @Subscribe
    public void videoEvent(VideoMsg msg) {
        data.clear();
        data.add(msg.getUrl());
        Intent intent1 = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA, (Serializable) data);
        intent1.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent1);
        finish();
    }

    @Subscribe
    public void ImageEvent(ImageMsg msg) {
        imdata.clear();
        imdata.addAll(msg.getList());
        Intent intent1 = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA, (Serializable) imdata);
        intent1.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent1);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
