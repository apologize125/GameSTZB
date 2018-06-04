package com.itgowo.gamestzb.Main;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.itgowo.gamestzb.Base.BaseActivity;
import com.itgowo.gamestzb.Base.BaseApp;
import com.itgowo.gamestzb.Base.BaseConfig;
import com.itgowo.gamestzb.BuildConfig;
import com.itgowo.gamestzb.Entity.BaseResponse;
import com.itgowo.gamestzb.Entity.HeroEntity;
import com.itgowo.gamestzb.Entity.UpdateVersion;
import com.itgowo.gamestzb.Guess.GameGuessActivity;
import com.itgowo.gamestzb.Manager.NetManager;
import com.itgowo.gamestzb.Manager.UserManager;
import com.itgowo.gamestzb.Manager.ViewCacheManager;
import com.itgowo.gamestzb.MusicService;
import com.itgowo.gamestzb.R;
import com.itgowo.gamestzb.UserActivity;
import com.itgowo.gamestzb.Utils;
import com.itgowo.gamestzb.View.FillVideoView;
import com.itgowo.gamestzb.View.HeroCard;
import com.itgowo.itgowolib.itgowoNetTool;
import com.itgowo.views.SuperDialog;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.taobao.sophix.SophixManager;

import org.xutils.common.util.DensityUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.weyye.hipermission.PermissionCallback;

public class MainActivity extends BaseActivity implements UserManager.onUserStatusListener, MainPresenter.onMainActivityActionListener {
    private MainPresenter presenter;
    private View layoutRootLayout;
    private LinearLayout countLayout, cardLayout;
    private TextView fabNotice;
    private List<HeroEntity> randomHeroEntities = new ArrayList<>();
    private TextView msg6;
    private TextView msg5;
    private TextView msg4;
    private TextView msg3;
    private TextView msg2;
    private TextView msg1;
    private FrameLayout videoRoot;
    private Button goodLuckBtn1, goodLuckBtn3, goodLuckBtn5;
    private int count5, count4, count3, count2, count1;
    private long countCost;
    private FloatingActionButton rightLowerButton;
    private VideoView viewVideoPlayView;
    private ImageView viewUserHeadImg;
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final int M = 1024 * 1024;
            final Runtime runtime = Runtime.getRuntime();

            Log.i("Memory", "最大可用内存：" + runtime.maxMemory() / M + "M");
            Log.i("Memory", "当前可用内存：" + runtime.totalMemory() / M + "M");
            Log.i("Memory", "当前空闲内存：" + runtime.freeMemory() / M + "M");
            Log.i("Memory", "当前已使用内存：" + (runtime.totalMemory() - runtime.freeMemory()) / M + "M");
            handler.sendEmptyMessageDelayed(1, 2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainPresenter(this, this);
        Utils.checkPermission(this, new PermissionCallback() {
            @Override
            public void onClose() {

            }

            @Override
            public void onFinish() {
                nextBoot();
            }

            @Override
            public void onDeny(String permission, int position) {

            }

            @Override
            public void onGuarantee(String permission, int position) {

            }
        });

//        handler.sendEmptyMessageDelayed(1, 2000);

    }

    private void nextBoot() {
        initView();
        initLstener();
        startFirst();
        checkVersion();
        presenter.CheckAndInitHeroListData();
    }


    private void refreshUserInfo() {
        if (viewUserHeadImg != null && BaseConfig.userInfo != null) {
            RequestOptions options = new RequestOptions().transform(new RoundedCorners(DensityUtil.dip2px(40)));
            Glide.with(viewUserHeadImg).load(BaseConfig.userInfo.getHead()).apply(options).into(viewUserHeadImg);
            countCost = UserManager.getMoney();
            msg6.setText(String.valueOf(countCost));
        }
    }


    private void initLstener() {
        viewUserHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                UserActivity.go(MainActivity.this, INTENT_UserActivity);
            }
        });
        findViewById(R.id.helpDev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuperDialog dialog = new SuperDialog(context).setShowImage().setImageListener(imageView -> Glide.with(imageView).load("http://file.itgowo.com/game/pay/allpay.png").into(imageView)).setShowButtonLayout(false);
                dialog.setAspectRatio(0.8f).show();
            }
        });
        fabNotice.setOnClickListener(v -> BaseApp.getStzbManager().goUpdateVersion(context));
        goodLuckBtn1.setOnClickListener(v -> goodluck(1));
        goodLuckBtn3.setOnClickListener(v -> goodluck(3));
        goodLuckBtn5.setOnClickListener(v -> goodluck(5));
    }

    private void checkVersion() {
        NetManager.getUpdateInfo(new itgowoNetTool.onReceviceDataListener<BaseResponse<UpdateVersion>>() {

            @Override
            public void onResult(String requestStr, String responseStr, BaseResponse<UpdateVersion> result) {
                if (result.isSuccess()) {
                    if (result.getData().getVersioncode() > BuildConfig.VERSION_CODE) {
                        fabNotice.setVisibility(View.VISIBLE);
                        BaseConfig.updateInfo = result.getData();
                    } else {
                        fabNotice.setVisibility(View.GONE);
                        SophixManager.getInstance().queryAndLoadNewPatch();
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    @Override
    protected void onPause() {
        if (viewVideoPlayView != null) {
            viewVideoPlayView.pause();
        }
        super.onPause();
        UserManager.removeUserStatusListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewVideoPlayView != null) {
            viewVideoPlayView.start();
        }
        UserManager.addUserStatusListener(this);
        refreshUserInfo();
    }

    private void reSetStyle() {
        if (!BaseConfig.getData(BaseConfig.USER_ISPLAYVIDEO, true)) {
            layoutRootLayout.setBackgroundResource(R.drawable.background2);
            if (viewVideoPlayView != null) {
                if (viewVideoPlayView.isPlaying()) {
                    viewVideoPlayView.stopPlayback();
                }
                viewVideoPlayView.setVisibility(View.GONE);
                videoRoot.removeAllViews();
                viewVideoPlayView = null;
            }
        } else {
            layoutRootLayout.setBackground(null);
            viewVideoPlayView = new FillVideoView(this);
            videoRoot.addView(viewVideoPlayView);
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cg_1);
            viewVideoPlayView.setVideoURI(uri);
            viewVideoPlayView.setClickable(false);
            viewVideoPlayView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mPlayer) {
                    mPlayer.start();
                    mPlayer.setLooping(true);
                }
            });
        }
        if (BaseConfig.getData(BaseConfig.USER_ISPLAYMUSIC, true)) {
            MusicService.playMusic(this, null);
        } else {
            MusicService.stopMusic(this);
        }
    }

    private void startFirst() {
        if (!BaseConfig.getData(BaseConfig.USER_ISPLAYVIDEO, true)) {
            layoutRootLayout.setVisibility(View.VISIBLE);
            layoutRootLayout.setBackgroundResource(R.drawable.background2);
            ObjectAnimator anim = ObjectAnimator.ofFloat(layoutRootLayout, "alpha", 0f, 0.2f, 0.3f, 0.5f, 1f);
            anim.setDuration(1200);// 动画持续时间
            anim.start();
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(rightLowerButton, "alpha", 0f, 0.2f, 0.3f, 0.5f, 1f);
            anim1.setDuration(1200);// 动画持续时间
            anim1.start();
        } else {
            viewVideoPlayView = new FillVideoView(this);
            videoRoot.addView(viewVideoPlayView);
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cg_1);
            viewVideoPlayView.setVideoURI(uri);
            viewVideoPlayView.setClickable(false);
            viewVideoPlayView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mPlayer) {
                    mPlayer.start();
                    mPlayer.setLooping(true);
                    viewVideoPlayView.setClickable(false);
                    layoutRootLayout.setVisibility(View.VISIBLE);
                    ObjectAnimator anim = ObjectAnimator.ofFloat(layoutRootLayout, "alpha", 0f, 0.2f, 0.3f, 0.5f, 1f);
                    anim.setDuration(1200);// 动画持续时间
                    anim.start();
                    ObjectAnimator anim1 = ObjectAnimator.ofFloat(rightLowerButton, "alpha", 0f, 0.2f, 0.3f, 0.5f, 1f);
                    anim1.setDuration(1200);// 动画持续时间
                    anim1.start();
                }
            });
            viewVideoPlayView.start();
        }
        if (BaseConfig.getData(BaseConfig.USER_ISPLAYMUSIC, true)) {
            MusicService.playMusic(this, null);
        } else {
            MusicService.stopMusic(this);
        }
    }

    private void initView() {
        viewUserHeadImg = findViewById(R.id.User_Head_Img);
        msg6 = (TextView) findViewById(R.id.msg6);
        msg5 = (TextView) findViewById(R.id.msg5);
        msg4 = (TextView) findViewById(R.id.msg4);
        msg3 = (TextView) findViewById(R.id.msg3);
        msg2 = (TextView) findViewById(R.id.msg2);
        msg1 = (TextView) findViewById(R.id.msg1);
        viewVideoPlayView = findViewById(R.id.videoview);
        layoutRootLayout = findViewById(R.id.rootlayout);
        videoRoot = findViewById(R.id.videoRoot);
        fabNotice = findViewById(R.id.fabNotice);
        goodLuckBtn1 = findViewById(R.id.goodBt1);
        goodLuckBtn3 = findViewById(R.id.goodBt3);
        goodLuckBtn5 = findViewById(R.id.goodBt5);
        countLayout = findViewById(R.id.countLayout);
        cardLayout = findViewById(R.id.cardLayout);
        initFloatingActionButton();
    }

    private void initFloatingActionButton() {
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_dialog_dialer));
        rightLowerButton = new FloatingActionButton.Builder(this).setContentView(fabIconNew).build();
        rightLowerButton.setAlpha(0.0f);
        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(getAction("图鉴")).build())
                .addSubActionView(rLSubBuilder.setContentView(getAction("猜将")).build())
                .addSubActionView(rLSubBuilder.setContentView(getAction("制作")).build())
                .addSubActionView(rLSubBuilder.setContentView(getAction("反馈")).build())
                .attachTo(rightLowerButton)
                .build();
        for (int i = 0; i < rightLowerMenu.getSubActionItems().size(); i++) {
            int finalI = i;
            rightLowerMenu.getSubActionItems().get(i).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rightLowerMenu.close(true);
                    switch (finalI) {
                        case 0:
                            break;
                        case 1:
                            GameGuessActivity.go(context);
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        default:
                    }

                }
            });
        }
    }

    private TextView getAction(String text) {
        TextView textView = new TextView(this);
//        textView.setBackgroundResource(R.drawable.shape_btn_blue);
        textView.setText(text);
        textView.setTextSize(10);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("getAction.onClick" + text);
            }
        });
        return textView;
    }

    private void onRandomResult() {
        countLayout.setVisibility(View.VISIBLE);
        msg6.setText(String.valueOf(countCost));
        msg5.setText("5 星：" + count5);
        msg4.setText("4 星：" + count4);
        msg3.setText("3 星：" + count3);
        msg2.setText("2 星：" + count2);
        msg1.setText("1 星：" + count1);
    }

    /**
     * 0-45  5星
     * 46-134  4星
     * 135-219 3星
     * <p>
     */
    private void goodluck(int num) {
        if (UserManager.getMoney() < 0) {
            showToastShort("没玉了，去做任务领取玉符吧");
            return;
        }
        NetManager.getRandomHero(num, new itgowoNetTool.onReceviceDataListener<BaseResponse<List<HeroEntity>>>() {

            @Override
            public void onResult(String requestStr, String responseStr, BaseResponse<List<HeroEntity>> result) {
                if (result.isSuccess()) {
                    if (result.getData() == null) {
                        return;
                    }
                    if (num == 10) {
                        countCost -= 1800;
                    } else if (num == 5) {
                        countCost -= 950;
                    } else {
                        countCost -= 200 * num;
                    }
                    randomHeroEntities = result.getData();
                    showHeros();
                } else {
                    Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }
        });


    }

    private void showHeros() {
        if (randomHeroEntities == null) {
            return;
        }
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
        ViewCacheManager<LinearLayout> cacheManager = new ViewCacheManager<>();
        cacheManager.setOnCacheListener(new ViewCacheManager.onCacheListener<HeroCard>() {
            @Override
            public View onAddView(int position) {
                HeroCard card = new HeroCard(context);
                return card;
            }

            @Override
            public void onRemoveView(int position) {

            }

            @Override
            public void onBindView(int position, HeroCard mView) {
                mView.setData(randomHeroEntities.get(position));
                mView.clearAnimation();
                mView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_in));
                String uri;
                if (new File(randomHeroEntities.get(position).getHeroFilePath()).exists()) {
                    uri = randomHeroEntities.get(position).getHeroFilePath();
                    mView.headimg.setImageURI(Uri.parse(uri));
                } else {
                    final RequestOptions options = new RequestOptions().dontTransform().dontAnimate();
                    uri = String.format(NetManager.ROOTURL_DOWNLOAD_HERO_IMAGE, randomHeroEntities.get(position).getId());
                    Glide.with(mView.headimg).load(uri).apply(options).into(mView.headimg);
                }
            }
        });
        cacheManager.onRefresh(cardLayout, randomHeroEntities.size());
        cardLayout.startLayoutAnimation();
        onRandomResult();
    }

    @Override
    public void onChanged() {
        refreshUserInfo();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UserManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_UserActivity) {
                reSetStyle();
            }
        }
        System.gc();
    }


    @Override
    public void showWaitDialog() {

    }

    @Override
    public void hideWaitDialog() {

    }
}
