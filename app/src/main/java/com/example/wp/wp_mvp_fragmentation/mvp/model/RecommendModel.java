package com.example.wp.wp_mvp_fragmentation.mvp.model;

import android.app.Application;

import com.example.wp.wp_mvp_fragmentation.app.data.api.Api;
import com.example.wp.wp_mvp_fragmentation.app.data.api.cache.RecommendCache;
import com.example.wp.wp_mvp_fragmentation.app.data.api.service.RecommendService;
import com.example.wp.wp_mvp_fragmentation.app.data.entry.recomment.IndexData;
import com.example.wp.wp_mvp_fragmentation.mvp.contract.RecommendContract;
import com.example.wp.wp_mvp_fragmentation.mvp.ui.adapter.entity.RecommendMultiItem;
import com.google.gson.Gson;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictProvider;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import timber.log.Timber;


@FragmentScope
public class RecommendModel extends BaseModel implements RecommendContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    private boolean isOdd = true;

    @Inject
    public RecommendModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
        super(repositoryManager);
        mGson = gson;
        mApplication = application;
        RetrofitUrlManager.getInstance().putDomain("recommend", Api.RECOMMEND_BASE_URL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<IndexData> getRecommendIndexData(int idx, boolean refresh, boolean clearCache) {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(RecommendService.class)
                .getRecommendIndexData(idx, refresh ? "true" : "false", clearCache ? 1 : 0))
                .flatMap(new Function<Observable<IndexData>, ObservableSource<IndexData>>() {
                    @Override
                    public ObservableSource<IndexData> apply(@NonNull Observable<IndexData> indexDataObservable) throws Exception {
                        return mRepositoryManager.obtainCacheService(RecommendCache.class)
                                .getRecommendIndexData(indexDataObservable, new DynamicKey(idx), new EvictProvider(clearCache))
                                .map(indexDataReply -> indexDataReply.getData());
                    }
                });

    }

    @Override
    public List<RecommendMultiItem> parseIndexData(IndexData indexData) {
        List<RecommendMultiItem> list = new ArrayList<>();
        List<IndexData.DataBean> data = indexData.getData();
        Timber.i(String.valueOf(data));
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                IndexData.DataBean dataBean = data.get(i);
                if (dataBean != null) {
                    String gotoX = dataBean.getGotoX();
                    if (RecommendMultiItem.isWeNeed(gotoX)) {
                        RecommendMultiItem item = new RecommendMultiItem();
                        item.setItemTypeWithGoto(gotoX, dataBean.getRcmd_reason() == null);
                        item.setIndexDataBean(dataBean);
                        if (RecommendMultiItem.isItemData(gotoX)) {
                            item.setOdd(isOdd);
                            isOdd = !isOdd;
                        } else {
                            isOdd = true;
                        }
                        list.add(item);
                    }
                }
            }
        }
        return list;
    }
}