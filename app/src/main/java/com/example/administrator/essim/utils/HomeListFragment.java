package com.example.administrator.essim.utils;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.essim.R;
import com.example.administrator.essim.activities.PixivItemActivity;
import com.example.administrator.essim.adapters.AuthorWorksAdapter;
import com.example.administrator.essim.fragments.FragmentPixivLeft;
import com.example.administrator.essim.models.AuthorWorks;
import com.google.gson.Gson;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 列表信息展示所用页面
 *
 * @author linxiao
 * @version 1.0.0
 */
public class HomeListFragment extends ScrollObservableFragment {

    private int scrolledX = 0, scrolledY = 0;

    private Context mContext;
    private View contentView;
    public static AuthorWorks mAuthorWorks;
    private AuthorWorksAdapter mAuthorWorksAdapter;
    @Bind(R.id.rcvGoodsList)
    RecyclerView rcvGoodsList;

    public HomeListFragment() {
        // Required empty public constructor
    }

    public static HomeListFragment newInstance() {
        HomeListFragment fragment = new HomeListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Common.sHomeListFragment = this;
        mContext = getContext();
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_home_list, container, false);
            ButterKnife.bind(this, contentView);
            initView();
        }
        return contentView;
    }

    private void initView() {
        rcvGoodsList.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvGoodsList.setItemAnimator(new DefaultItemAnimator());
        rcvGoodsList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, final int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrolledX += dx;
                scrolledY += dy;
                if (HomeListFragment.this.isResumed()) {
                    doOnScrollChanged(scrolledX, scrolledY, dx, dy);
                }
            }
        });
        String url = "https://api.imjad.cn/pixiv/v1/?type=member_illust&id=";
        getData(url + FragmentPixivLeft.mPixivRankItem.response.get(0).works.get(((CloudMainActivity) getActivity()).index).work
                .user.getId());
    }

    private void getData(String address) {
        Common.sendOkhttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Gson gson = new Gson();
                mAuthorWorks = gson.fromJson(responseData, AuthorWorks.class);
                mAuthorWorksAdapter = new AuthorWorksAdapter(mAuthorWorks, getContext());
                mAuthorWorksAdapter.setOnItemClickListener(new AuthorWorksAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(mContext, PixivItemActivity.class);
                        intent.putExtra("which one is selected", position);
                        mContext.startActivity(intent);
                    }
                });
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rcvGoodsList.setAdapter(mAuthorWorksAdapter);
                    }
                });
            }
        });
    }


    @Override
    public void setScrolledY(int scrolledY) {
        if (rcvGoodsList != null) {
            if (this.scrolledY >= scrolledY) {
                int scrollDistance = (this.scrolledY - scrolledY) * -1;
                rcvGoodsList.scrollBy(0, scrollDistance);
            } else {
                rcvGoodsList.scrollBy(0, scrolledY);
            }
        }
    }
}
