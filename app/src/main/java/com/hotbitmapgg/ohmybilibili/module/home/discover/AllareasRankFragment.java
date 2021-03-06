package com.hotbitmapgg.ohmybilibili.module.home.discover;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hotbitmapgg.ohmybilibili.R;
import com.hotbitmapgg.ohmybilibili.adapter.AllareasRankAdapter;
import com.hotbitmapgg.ohmybilibili.base.RxLazyFragment;
import com.hotbitmapgg.ohmybilibili.entity.discover.AllareasRankInfo;
import com.hotbitmapgg.ohmybilibili.module.video.VideoDetailsActivity;
import com.hotbitmapgg.ohmybilibili.network.RetrofitHelper;
import com.hotbitmapgg.ohmybilibili.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hcc on 16/8/11 20:23
 * 100332338@qq.com
 * <p/>
 * 全区排行榜界面
 */
public class AllareasRankFragment extends RxLazyFragment
{

    @Bind(R.id.recycle)
    RecyclerView mRecyclerView;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private String type;

    private static final String EXTRA_KEY = "extra_type";

    private List<AllareasRankInfo.RankBean.ListBean> allRanks = new ArrayList<>();

    private AllareasRankAdapter mAdapter;


    public static AllareasRankFragment newInstance(String type)
    {

        AllareasRankFragment mFragment = new AllareasRankFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(EXTRA_KEY, type);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public int getLayoutResId()
    {

        return R.layout.fragment_all_areas_rank;
    }

    @Override
    public void finishCreateView(Bundle state)
    {

        type = getArguments().getString(EXTRA_KEY);
        initRefreshLayout();
        initRecyclerView();
    }

    private void initRefreshLayout()
    {

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.postDelayed(() -> {

            mSwipeRefreshLayout.setRefreshing(true);
            getAllareasRanks();
        }, 500);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mSwipeRefreshLayout.setRefreshing(false));
    }

    private void getAllareasRanks()
    {

        RetrofitHelper.getAllareasRankApi()
                .getAllareasRanks(type)
                .compose(bindToLifecycle())
                .map(allareasRankInfo -> allareasRankInfo.getRank().getList())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listBeans -> {

                    allRanks.addAll(listBeans.subList(0, 20));
                    finishTask();
                }, throwable -> {

                    mSwipeRefreshLayout.setRefreshing(false);
                    ToastUtil.ShortToast("加载失败啦,请重新加载~");
                });
    }

    private void finishTask()
    {

        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.notifyDataSetChanged();
    }


    private void initRecyclerView()
    {

        mSwipeRefreshLayout.setRefreshing(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new AllareasRankAdapter(mRecyclerView, allRanks);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((position, holder) -> VideoDetailsActivity.launch(getActivity(),
                allRanks.get(position).getAid(),
                allRanks.get(position).getPic()));
    }

    @Override
    protected void lazyLoad()
    {

    }
}
