package org.sunger.net.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.sunger.net.entity.OnlineVideo;
import org.sunger.net.ui.adapter.TvProgramAdapter;
import org.sunger.net.ui.base.ArrayAdapter;
import org.sunger.net.ui.helper.XmlReaderHelper;
import org.sunger.net.util.FileUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import sunger.org.demo.R;

public class OnlineWebActivity extends AppCompatActivity {

    private RecyclerView mListView;
    private String mTitle;
    private final static ArrayList<OnlineVideo> videos = new ArrayList<OnlineVideo>();
    private TvProgramAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_web);
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
