package org.sunger.net.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.sunger.net.entity.OnlineVideo;
import org.sunger.net.ui.widget.refresh.BaseLoadMoreRecyclerAdapter;

import java.util.List;

import sunger.org.demo.R;

/**
 * Created by xbp on 2016/4/27.
 */
public class TvProgramAdapter extends RecyclerView.Adapter<TvProgramAdapter.ViewHolder> {


    private onItemClickListener onItemClickListener;
    private List<OnlineVideo>  onlineVideoList;

    private Context mContext;

    public TvProgramAdapter(List<OnlineVideo> onlineVideos,Context context){
        this.mContext=context;
        this.onlineVideoList=onlineVideos;
    }

    public void setOnItemClickListener(onItemClickListener ItemClickListener){
        this.onItemClickListener=ItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.fragment_online_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final OnlineVideo onlineVideo=onlineVideoList.get(position);
        holder.webtitle.setText(onlineVideo.title);
        holder.imageView.setBackgroundResource(onlineVideo.iconId);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(onlineVideo);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return onlineVideoList.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{

        public final TextView webtitle;
        public final ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            webtitle= (TextView) itemView.findViewById(R.id.title);
            imageView= (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }

    public interface  onItemClickListener{
        void onItemClick(OnlineVideo onlineVideo);
    }
}
