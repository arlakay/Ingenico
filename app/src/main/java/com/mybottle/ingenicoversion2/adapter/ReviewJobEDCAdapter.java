package com.mybottle.ingenicoversion2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mybottle.ingenicoversion2.R;
import com.mybottle.ingenicoversion2.model.ReviewJob;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ILM on 8/1/2016.
 */
public class ReviewJobEDCAdapter extends RecyclerView.Adapter<ReviewJobEDCAdapter.VersionViewHolder> {

    private List<ReviewJob> loginList;
    private int rowLayout;
    Context context;
    OnItemClickListener clickListener;

    public ReviewJobEDCAdapter(List<ReviewJob> login, int rowLayout, Context context, OnItemClickListener listener) {
        this.loginList = login;
        this.rowLayout = rowLayout;
        this.context = context;
        this.clickListener = listener;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VersionViewHolder versionViewHolder, int i) {
        final ReviewJob model = loginList.get(i);
        versionViewHolder.bind(model, clickListener);
    }

    @Override
    public int getItemCount() {
        return loginList == null ? 0 : loginList.size();
    }

    public void animateTo(List<ReviewJob> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<ReviewJob> newModels) {
        for (int i = loginList.size() - 1; i >= 0; i--) {
            final ReviewJob model = loginList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ReviewJob> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ReviewJob model = newModels.get(i);
            if (!loginList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ReviewJob> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ReviewJob model = newModels.get(toPosition);
            final int fromPosition = loginList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public ReviewJob removeItem(int position) {
        final ReviewJob model = loginList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, ReviewJob model) {
        loginList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ReviewJob model = loginList.remove(fromPosition);
        loginList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    class VersionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_hari_service) TextView titleRes;
        @BindView(R.id.txt_edc_sn) TextView descRes;

        public VersionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final ReviewJob model, final OnItemClickListener listener) {
            titleRes.setText(model.getStart_time() + " - " + model.getFinish_time());

            if (model.getTerminal_code() != null) {
                descRes.setText(model.getTerminal_code());
            }else{
                descRes.setText(model.getItem_code());
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(model);

                }
            });
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(ReviewJob model);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

}
