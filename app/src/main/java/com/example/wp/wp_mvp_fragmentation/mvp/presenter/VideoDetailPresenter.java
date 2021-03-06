package com.example.wp.wp_mvp_fragmentation.mvp.presenter;

import android.app.Application;

import com.example.wp.wp_mvp_fragmentation.app.data.api.Api;
import com.example.wp.wp_mvp_fragmentation.app.data.entry.video.PlayUrl;
import com.example.wp.wp_mvp_fragmentation.app.data.entry.video.Summary;
import com.example.wp.wp_mvp_fragmentation.app.data.entry.video.VideoDetail;
import com.example.wp.wp_mvp_fragmentation.mvp.contract.VideoDetailContract;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;


@ActivityScope
public class VideoDetailPresenter extends BasePresenter<VideoDetailContract.Model, VideoDetailContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;
    private final int page = 1;
    private final int rows = 20;
    private final String mStartInfoStr = "初始化播放器...";

    @Inject
    public VideoDetailPresenter(VideoDetailContract.Model model, VideoDetailContract.View rootView) {
        super(model, rootView);
        RetrofitUrlManager.getInstance().putDomain("video_detail_summary", Api.VIDEO_DETAIL_SUMMARY_BASE_URL);
        RetrofitUrlManager.getInstance().putDomain("video_detail_reply", Api.VIDEO_DETAIL_REPLY_BASE_URL);
    }


    public void loadData(String aid) {
        Observable.zip(
                mModel.getSummaryData(aid),
                mModel.getReplyData(aid, page, rows),
                (summary, reply) -> new VideoDetail(summary, reply))
                .retryWhen(new RetryWithDelay(3, 2))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<VideoDetail>(mErrorHandler) {
                    @Override
                    public void onNext(VideoDetail videoDetail) {
                        setViewDetail(videoDetail);
                    }
                });


    }

    private void setViewDetail(VideoDetail videoDetail) {
        if (videoDetail == null) {
            return;
        }
        //填充视频详情界面信息
        Summary summary = videoDetail.getSummary();
        if (summary == null || summary.getData() == null) {
            return;
        }
        Summary.DataBean dataBean = summary.getData();
        mRootView.setTvAvStr(dataBean.getStat() == null ? "" : String.valueOf(dataBean.getStat().getAid()));
        mImageLoader.loadImage(mAppManager.getCurrentActivity(), ImageConfigImpl
                .builder()
                .url(dataBean.getPic())
                .imageView(mRootView.getIvCover()).build());
        //填充简介评论Fragment信息
        mRootView.initViewPager(videoDetail);
    }

    public void loadPlayUrl(String aid) {
        //根据加载情况,动态修改提示
        mRootView.setTvVideoStartInfoStr(mStartInfoStr);
        mModel.getPlayurl(aid)
                .retryWhen(new RetryWithDelay(3, 2))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<PlayUrl>(mErrorHandler) {
                    @Override
                    public void onNext(PlayUrl url) {
                        mRootView.playVideo(url);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

}
