package com.example.wp.wp_mvp_fragmentation.mvp.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.wp.wp_mvp_fragmentation.R;
import com.example.wp.wp_mvp_fragmentation.app.base.MySupportActivity;
import com.example.wp.wp_mvp_fragmentation.app.data.api.Router;
import com.example.wp.wp_mvp_fragmentation.di.component.DaggerVideoDetailComponent;
import com.example.wp.wp_mvp_fragmentation.di.module.VideoDetailModule;
import com.example.wp.wp_mvp_fragmentation.mvp.contract.VideoDetailContract;
import com.example.wp.wp_mvp_fragmentation.mvp.presenter.VideoDetailPresenter;
import com.example.wp.wp_mvp_fragmentation.mvp.ui.listener.AppBarStateChangeEvent;
import com.example.wp.wp_mvp_fragmentation.mvp.ui.listener.MyStandardVideoAllCallBack;
import com.example.wp.wp_mvp_fragmentation.mvp.ui.widget.player.LQRBiliPlayer;
import com.example.wp.wp_mvp_fragmentation.mvp.ui.widget.player.PlayerOptionBuilder;
import com.flyco.systembar.SystemBarHelper;
import com.flyco.tablayout.SlidingTabLayout;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;

@Route(path = Router.VIDEODETAIL_ACTIVITY)
public class VideoDetailActivity extends MySupportActivity<VideoDetailPresenter> implements VideoDetailContract.View {

    @Autowired(name = "aid")
    public String aid;


    // toolbar、appbarlayout
    private boolean isPlayImmediately = false;
    // Fab、锚点
    private boolean isHidingFab = false;
    private int mAnchorX = 30;
    private int mAnchorY = 60;
    // 播放器
    private boolean isPlay;
    private boolean isPause;
    private boolean cacheVideo = true;
    private OrientationUtils mOrientationUtils;
    private PlayerOptionBuilder mGsyVideoOptionBuilder;

    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.appbar)
    AppBarLayout mAppbar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_av)
    TextView mTvAv;
    @BindView(R.id.video_view)
    LQRBiliPlayer mVideoView;
    @BindView(R.id.rl_video_tip)
    RelativeLayout mRlVideoTip;
    @BindView(R.id.tv_video_start_info)
    TextView mTvVideoStartInfo;
    @BindView(R.id.iv_cover)
    ImageView mIvCover;
    @BindView(R.id.tv_play_immediately)
    TextView mTvPlayImmediately;

    @BindView(R.id.tablayout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerVideoDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .videoDetailModule(new VideoDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_video_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initStatusBar();
        initToolbar();
        initFab();
        initVideoPlayer();
        if (!TextUtils.isEmpty(aid)) {
            mPresenter.loadData(aid);
        } else {
            showMessage("aid = " + aid);
        }
    }

    private void initStatusBar() {
        //设置satusBar透明
        SystemBarHelper.immersiveStatusBar(this);
        SystemBarHelper.setHeightAndPadding(this, mToolbar);
    }

    private void initToolbar() {

        // 1.appbarLayout
        mAppbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> showHideFab(verticalOffset));
        mAppbar.addOnOffsetChangedListener(new AppBarStateChangeEvent() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state, int verticalOffset) {
                if (state == State.EXPANDED) {
                    mTvAv.setVisibility(View.VISIBLE);
                    mTvPlayImmediately.setVisibility(View.GONE);
                    //点击了立即播放
                    if (isPlayImmediately) {
                        mFab.performClick();
                    }
                } else if (state == State.COLLAPSED) {
                    mTvAv.setVisibility(View.GONE);
                    mTvPlayImmediately.setVisibility(View.VISIBLE);
                } else if (state == State.IDLE) {
                    mTvAv.setVisibility(View.VISIBLE);
                    mTvPlayImmediately.setVisibility(View.GONE);
                }
            }
        });
        //2.toolBar
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 给左上角图标的左边加上一个返回的图标 。
            actionBar.setDisplayHomeAsUpEnabled(true);
            //不设置标题
            actionBar.setDisplayShowTitleEnabled(false);
        }
        //设置返回图标监听事件
        mToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        mTvPlayImmediately.setOnClickListener(v -> {
            isPlayImmediately = true;
            mAppbar.setExpanded(true, true);
        });
        //3.设置CollapsingToolBarLayout
        mCollapsingToolbarLayout.setTitleEnabled(false);//必须关闭文字,否则toobar中自定义控件位置会受影响
    }

    private void showHideFab(int verticalOffset) {

    }

    private void initFab() {
        //悬浮按钮添加点击事件
        mFab.setOnClickListener(v -> {
            ObjectAnimator translationY = ObjectAnimator.ofFloat(mFab, "translationY",
                    ArmsUtils.dip2px(this, mAnchorY));
            translationY.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mFab.setVisibility(View.GONE);
                    showVideoStartTip();
                }
            });
            translationY.start();
        });
    }

    private void showVideoStartTip() {

    }

    private void initVideoPlayer() {
        //设置开始播放了才能旋转和全屏
        mOrientationUtils = new OrientationUtils(this,mVideoView);
        mOrientationUtils.setEnable(false);
        mGsyVideoOptionBuilder = new PlayerOptionBuilder()
                .setUrl("")
                .setEnlargeImageRes(R.mipmap.ic_portrait_fullscreen)
                .setIsTouchWidget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setSeekRatio(1)
                .setCacheWithPlay(cacheVideo)
                .setVideoTitle("")
                .setMyStandardVideoAllCallBack(new MyStandardVideoAllCallBack(){
                    @Override
                    public void onPrepared(String s, Object... objects) {
                        super.onPrepared(s, objects);
                        //显示播放器
                        mRlVideoTip.setVisibility(View.GONE);
                        mVideoView.setVisibility(View.VISIBLE);
                        //开始播放了才能旋转
                        mOrientationUtils.setEnable(true);
                        isPlay = true;
                    }

                    @Override
                    public void onQuitFullscreen(String s, Object... objects) {
                        super.onQuitFullscreen(s, objects);
                        if (mOrientationUtils !=null){
                            mOrientationUtils.backToProtVideo();
                        }
                    }
                });
        mGsyVideoOptionBuilder.build(mVideoView);
        mVideoView.getFullscreenButton().setOnClickListener(v -> {
            //直接横屏
            mOrientationUtils.resolveByClick();
             //第一个true是否需要隐藏actionbar,第二个是否小隐藏状态栏
            mVideoView.startWindowFullscreen(this,true,true);

        });
        mVideoView.setLockClickListener((view,lock)->{

        });

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    @Override
    public void post(Runnable runnable) {

    }
}
