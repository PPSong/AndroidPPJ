package com.penn.ppj.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.penn.ppj.R;
import com.penn.ppj.activities.MomentDetailActivity;
import com.penn.ppj.models.MomentCreated;
import com.penn.ppj.models.MomentOverview;
import com.penn.ppj.models.NotifyObject;
import com.penn.ppj.utils.MomentsListAdapter;
import com.penn.ppj.utils.PPData;
import com.penn.ppj.utils.RecyclerViewClickListener;

import java.util.Observable;
import java.util.Observer;

import static com.penn.ppj.R.id.rv_moments;

public class MomentsFragment extends Fragment implements RecyclerViewClickListener, Observer {

    private MomentsListAdapter mAdapter;
    private RecyclerView rcMomentsList;

    public MomentsFragment() {
        // Required empty public constructor
    }

    public static MomentsFragment newInstance() {
        MomentsFragment fragment = new MomentsFragment();

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
        View view = inflater.inflate(R.layout.fragment_moments, container, false);
        rcMomentsList = (RecyclerView) view.findViewById(rv_moments);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rcMomentsList.setLayoutManager(layoutManager);

        mAdapter = new MomentsListAdapter(this);
        rcMomentsList.setAdapter(mAdapter);

        PPData.getInstance().addObserver(this);

        return view;
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        Intent it = new Intent(getContext(), MomentDetailActivity.class);
        it.putExtra("momentId", PPData.getInstance().getTotalMoments(position).getMomentId());
        startActivity(it);
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.v("P", "observer updated");
        NotifyObject no = (NotifyObject) arg;

        if (no.getType() == "momentCreated") {
            Log.v("P", no.toString());
            MomentCreated mc = (MomentCreated) ((NotifyObject) arg).getObj();
            rcMomentsList.smoothScrollToPosition(0);
            mAdapter.notifyItemInserted(0);
            //mAdapter.notifyDataSetChanged();
            //上传新建moment
            mc.upload();

        } else if (no.getType() == "momentsRefresh") {
            Log.v("P", "refresh moments");
        } else if (no.getType() == "momentUpdated") {
            Log.v("P", "momentUpdated");
            MomentOverview mo = (MomentOverview) ((NotifyObject) arg).getObj();
            //刷新moments list中对应mo的记录
            mAdapter.notifyItemChanged(PPData.getInstance().getMomentOverviewPosition(mo));
        }
    }
}
