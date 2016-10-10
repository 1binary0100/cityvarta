package com.newsvarta.dvimaniya.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newsvarta.dvimaniya.R;
import com.newsvarta.dvimaniya.WebViewActivity;
import com.newsvarta.dvimaniya.lists.NewsList;
import com.squareup.picasso.Picasso;

import java.util.List;
import CustomInterfaces.CustomInterface;

/**
 * Created by Sam on 21-05-2016.
 */
public class NewsRecycler extends RecyclerView.Adapter<NewsRecycler.CustomViewHolder>  {

    private List<NewsList> newsLists;
    private Context mContext;
    NewsList newsList;
    String ab;
    private CustomInterface customInterface;
    final static String TAG="NEWSRECYCLER";
    public NewsRecycler(Context context, List<NewsList> newsLists, CustomInterface customInterface){
        this.mContext = context;
        this.newsLists = newsLists;
        this.customInterface = customInterface;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, null);
        CustomViewHolder customViewHolder = new CustomViewHolder(view, newsLists);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        newsList = newsLists.get(holder.getAdapterPosition());
        Picasso.with(mContext).load(newsList.getThumbnail()).
                error(R.drawable.shine)
                .placeholder(R.drawable.shine)
                .into(holder.imageView);
        holder.tvTitle.setText(newsList.getTitle());
        holder.tvNews.setText(newsList.getNews());
        holder.tvSource.setText(newsList.getSource());
        holder.tvTimeStamp.setText(newsList.getTimeStamp());
        //Log.d(TAG,newsList.getSourceUrl() + holder.getAdapterPosition());
        ab = holder.tvSource.getText().toString();

    }

    @Override
    public int getItemCount() {
        return (null != newsLists ? newsLists.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public TextView tvTitle, tvNews, tvSource, tvTimeStamp;
        public List<NewsList> newsLists;
        public CustomViewHolder(View view, List<NewsList> newsLists){
            super(view);
            this.newsLists = newsLists;
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            this.tvNews = (TextView) view.findViewById(R.id.tvNews);
            this.tvSource=(TextView) view.findViewById(R.id.tvSource);
            this.tvTimeStamp = (TextView) view.findViewById(R.id.tvTimeStamp);
            tvSource.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            NewsList newsList = this.newsLists.get(pos);
            String url = newsList.getSourceUrl().toString();
            Intent intent = new Intent(mContext, WebViewActivity.class).putExtra("url", url);
            mContext.startActivity(intent);

        }
}
}
