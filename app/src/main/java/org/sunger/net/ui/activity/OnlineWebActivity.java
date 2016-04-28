package org.sunger.net.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


import org.sunger.net.entity.OnlineVideo;
import org.sunger.net.ui.adapter.TvProgramAdapter;

import java.util.ArrayList;

import sunger.org.demo.R;

public class OnlineWebActivity extends BaseCompatActivity {

    private RecyclerView mListView;
    private final static ArrayList<OnlineVideo> videos = new ArrayList<OnlineVideo>();
    private TvProgramAdapter mAdapter;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_web);
        setupActionBar();
        mListView = (RecyclerView) findViewById(R.id.recycler_view);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter=new TvProgramAdapter(videos,this);
        mListView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new TvProgramAdapter.onItemClickListener() {
            @Override
            public void onItemClick(OnlineVideo onlineVideo) {
                Intent intent = WebViewActivity.createIntent(OnlineWebActivity.this, onlineVideo.url);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
       finish();
    }

    private void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("视频网站");
        setSupportActionBar(mToolbar);
        toobarAsBackButton(mToolbar);

    }
    static {

        videos.add(new OnlineVideo("优酷视频", R.drawable.logo_youku, 0,
                "http://3g.youku.com"));
        videos.add(new OnlineVideo("搜狐视频", R.drawable.logo_sohu, 0,
                "http://m.tv.sohu.com"));
        videos.add(new OnlineVideo("乐视TV", R.drawable.logo_letv, 0,
                "http://m.letv.com"));
        videos.add(new OnlineVideo("爱奇异", R.drawable.logo_iqiyi, 0,
                "http://3g.iqiyi.com/"));
        videos.add(new OnlineVideo("PPTV", R.drawable.logo_pptv, 0,
                "http://m.pptv.com/"));
        videos.add(new OnlineVideo("腾讯视频", R.drawable.logo_qq, 0,
                "http://3g.v.qq.com/"));
        videos.add(new OnlineVideo("56.com", R.drawable.logo_56, 0,
                "http://m.56.com/"));
        videos.add(new OnlineVideo("新浪视频", R.drawable.logo_sina, 0,
                "http://video.sina.cn/"));
        videos.add(new OnlineVideo("土豆视频", R.drawable.logo_tudou, 0,
                "http://m.tudou.com"));
    }
}
