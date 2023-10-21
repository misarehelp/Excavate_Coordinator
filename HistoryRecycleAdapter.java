package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryRecycleAdapter extends RecyclerView.Adapter<HistoryRecycleAdapter.ViewHolder> implements Constants {

    private final Contract.Recycle.HistoryInterface clickListener;
    private final LayoutInflater inflater;
    private ArrayList<RecordData> data = new ArrayList<>();

    HistoryRecycleAdapter(Context context ) {
        this.inflater = LayoutInflater.from(context);
        this.clickListener = (Contract.Recycle.HistoryInterface) context;
    }

    public void swap(ArrayList<RecordData> datas)  {
        if (data != null) {
            data.clear();
            data.addAll(datas);
        } else {
            data = datas;
        }
        notifyDataSetChanged();
    }

    @Override
    public HistoryRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.history_list_item, parent, false);
        return new HistoryRecycleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryRecycleAdapter.ViewHolder holder, int position) {
        RecordData recordData = data.get(position);
        if (recordData != null) {

            holder.tv_date.setText(recordData.getDate());
            holder.tv_job.setText(recordData.getJob());
            holder.tv_job_price.setText(recordData.getPrice());
            holder.tv_client_comment.setText(recordData.getComment());
            holder.tv_client_has_photo.setText(Boolean.toString(recordData.getPicBefore()));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tv_date, tv_job, tv_job_price, tv_client_comment, tv_client_has_photo;

        ViewHolder(View view) {
            super(view);

            tv_date = view.findViewById(R.id.tv_date);
            tv_job = view.findViewById(R.id.tv_job);
            tv_job_price = view.findViewById(R.id.tv_job_price);
            tv_client_comment = view.findViewById(R.id.tv_client_comment);
            tv_client_has_photo = view.findViewById(R.id.tv_client_has_photo);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    RecordData recordData = data.get(position);
                    if (recordData.getPicBefore()) {
                        String filename = recordData.getName() + "-" + recordData.getPhone() + "-" + recordData.getDate();
                        clickListener.onItemClick(filename);
                    }
                }
            });
        }
    }
}
