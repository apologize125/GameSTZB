package com.itgowo.gamestzb;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.itgowo.gamestzb.Entity.HeroEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import library.Info;
import library.PhotoView;

public class MainActivity extends AppCompatActivity {
    private STZBManager manager = new STZBManager();
    private RecyclerView recyclerView;
    private List<HeroEntity> randomHeroEntities = new ArrayList<>();
    private Random random = new Random(System.currentTimeMillis());
    private int height, width;
    private TextView msg5;
    private TextView msg4;
    private TextView msg3;
    private int count5, count4, count3, count2, count1;
    private View mParent;
    private View mBg;
    private PhotoView mPhotoView;
    private Info mInfo;

    private AlphaAnimation in = new AlphaAnimation(0, 1);
    private AlphaAnimation out = new AlphaAnimation(1, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        init();
        initRecyclerView();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerview);
        msg5 = (TextView) findViewById(R.id.msg5);
        msg4 = (TextView) findViewById(R.id.msg4);
        msg3 = (TextView) findViewById(R.id.msg3);
        mParent = findViewById(R.id.parent);
        mBg = findViewById(R.id.bg);
        mPhotoView = (PhotoView) findViewById(R.id.img);
        mPhotoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        width = getResources().getDisplayMetrics().widthPixels / 5;
        height = width * 410 / 300;
        in.setDuration(200);
        out.setDuration(200);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBg.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void initRecyclerView() {
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.anim_layout_animation_fall_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        recyclerView.setAdapter(new Myadapter());
        mPhotoView.enable();
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBg.startAnimation(out);
                mPhotoView.animaTo(mInfo, new Runnable() {
                    @Override
                    public void run() {
                        mParent.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    public boolean isWifiConnect() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    private void init() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mParent.getVisibility() == View.VISIBLE) {
                    mBg.startAnimation(out);
                    mPhotoView.animaTo(mInfo, new Runnable() {
                        @Override
                        public void run() {
                            mParent.setVisibility(View.GONE);
                        }
                    });
                }
                SuperDialog dialog = new SuperDialog(MainActivity.this);
                List<SuperDialog.DialogMenuItem> menuItems = new ArrayList<>();
                menuItems.add(new SuperDialog.DialogMenuItem("小试牛刀(1)", R.mipmap.ic_launcher_round));
                menuItems.add(new SuperDialog.DialogMenuItem("大胆尝试(5)", R.mipmap.liubei_round));
                menuItems.add(new SuperDialog.DialogMenuItem("疯狂剁手(15)", R.mipmap.sunquan_round));
                dialog.setTitle("选择抽取次数").setDialogMenuItemList(menuItems).setListener(new SuperDialog.onDialogClickListener() {
                    @Override
                    public void click(boolean isButtonClick, int position) {
                        if (position == 0) {
                            goodluck(1);
                        } else if (position == 1) {
                            goodluck(5);
                        } else {
                            goodluck(15);
                        }
                    }
                }).show();

            }
        });
//        try {
//            int count = 0;
//            String temp = Utils.ReadFile2String(getResources().openRawResource(R.raw.simpledata));
//            simpleEntities = JSON.parseArray(temp, SimpleEntity.class);
//            Collections.sort(simpleEntities);
//            String temp = Utils.ReadFile2String(getResources().openRawResource(R.raw.herolist));
//            manager.setTotalHeroList(JSON.parseArray(temp, HeroEntity.class));
//            heroEntities = manager.getTotalHeroList();

//            for (int i = 0; i < heroEntities.size(); i++) {
//                File h=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"stzb/hero_"+heroEntities.get(i).getId()+".jpg");
//                Utils.download(h,"https://stzb.res.netease.com/pc/qt/20170323200251/data/role/card_"+heroEntities.get(i).getId()+".jpg");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

    private void onRandomResult() {
        msg5.setText("5 星：" + count5);
        msg4.setText("4 星：" + count4);
        msg3.setText("3 星：" + count3);
    }

    /**
     * 0-45  5星
     * 46-134  4星
     * 135-219 3星
     * <p>
     */
    private void goodluck(int num) {
        NetManager.getRandomHero(num, new NetManager.onNetResultListener<BaseResponse<List<HeroEntity>>>() {

            @Override
            public void onResult(String requestStr, String responseStr, BaseResponse<List<HeroEntity>> result) {
                randomHeroEntities = result.getData();
                for (int i = 0; i < randomHeroEntities.size(); i++) {
                    switch (randomHeroEntities.get(i).getQuality()) {
                        case 1:
                            count1++;
                            break;
                        case 2:
                            count2++;
                            break;
                        case 3:
                            count3++;
                            break;
                        case 4:
                            count4++;
                            break;
                        case 5:
                            count5++;
                            break;
                    }
                }
                onRandomResult();
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });


    }

    public void showHeroDialog(Context context) {
        if (randomHeroEntities == null || randomHeroEntities.size() == 0) {
            return;
        }
        final HeroEntity entity = randomHeroEntities.remove(0);
        SuperDialog dialog = new SuperDialog(MainActivity.this).setShowButtonLayout(false);
        dialog.setImageListener(new SuperDialog.onDialogImageListener() {
            @Override
            public void onInitImageView(ImageView imageView) {
                RequestOptions options = new RequestOptions().dontAnimate().dontTransform();
                if (entity.getId() < 600000) {
                    final int res = getResources().getIdentifier("hero_" + entity.getId(), "drawable", getPackageName());
                    Glide.with(imageView).load(res).apply(options).into(imageView);
                } else {
                    Glide.with(imageView).load(entity.getIcon()).apply(options).into(imageView);
                }
            }
        }).setTitleTextSize(17)
                .setContent("恭喜主公喜获 " + entity.getQuality() + "星 " + entity.getContory() + " " + entity.getType() + " " + entity.getName())
                .setTitle("枫林制作，仅供娱乐");
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (entity.getQuality() == 3) {
                    count3++;
                    return;
                }
                if (entity.getQuality() == 4) {
                    count4++;
                }
                if (entity.getQuality() == 5) {
                    count5++;
                }
                if (entity.getQuality() == 2) {
                    count2++;
                }
                if (entity.getQuality() == 1) {
                    count1++;
                }
                onRandomResult();
                showHeroDialog(context);
            }
        });
        dialog.show();
    }

    class Myadapter extends RecyclerView.Adapter<viewHolder> {


        @Override
        public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            PhotoView p = new PhotoView(parent.getContext());
            return new viewHolder(p);
        }

        @Override
        public void onBindViewHolder(final viewHolder holder, final int position) {
            final PhotoView p = (PhotoView) holder.itemView;
            p.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            p.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // 把PhotoView当普通的控件把触摸功能关掉
            p.disenable();
            final HeroEntity entity = randomHeroEntities.get(position);
            final RequestOptions options = new RequestOptions().dontTransform().dontAnimate().format(DecodeFormat.PREFER_RGB_565);
//            Glide.with(holder.itemView).load(entity.getSrc()).apply(options).into(p);
            final int res = getResources().getIdentifier("hero_" + entity.getId(), "drawable", getPackageName());
            Glide.with(holder.itemView).load(res).apply(options).into(p);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PhotoView p = (PhotoView) v;
                    mInfo = p.getInfo();
                    Glide.with(holder.itemView).load(res).apply(options).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            mPhotoView.setImageDrawable(resource);
                            mPhotoView.setScaleType(ImageView.ScaleType.FIT_XY);
                            mBg.startAnimation(in);
                            mBg.setVisibility(View.VISIBLE);
                            mParent.setVisibility(View.VISIBLE);
                            mPhotoView.animaFrom(mInfo);
                        }
                    });
                }
            });


        }

        @Override
        public int getItemCount() {
            return randomHeroEntities.size();
        }
    }

    class viewHolder extends RecyclerView.ViewHolder {
        public viewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onBackPressed() {
        if (mParent.getVisibility() == View.VISIBLE) {
            mBg.startAnimation(out);
            mPhotoView.animaTo(mInfo, new Runnable() {
                @Override
                public void run() {
                    mParent.setVisibility(View.GONE);
                }
            });
        } else {
            super.onBackPressed();
        }
    }
}
