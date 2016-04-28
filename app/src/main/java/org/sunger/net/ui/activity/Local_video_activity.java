package org.sunger.net.ui.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.sunger.net.app.App;
import org.sunger.net.business.FileBusiness;
import org.sunger.net.database.DbHelper;
import org.sunger.net.entity.POMedia;
import org.sunger.net.service.MediaScannerService;
import org.sunger.net.ui.OPreference;
import org.sunger.net.ui.base.ArrayAdapter;
import org.sunger.net.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sunger.org.demo.R;

public class Local_video_activity extends BaseCompatActivity implements OnItemClickListener,MediaScannerService.IMediaScannerObserver {

    private FileAdapter mAdapter;
    private TextView first_letter_overlay;
    private ImageView alphabet_scroller;
    private FileAdapter mDownloadAdapter;
    protected ListView mListView;
    protected View mLoadingLayout;
    private Toolbar mToolbar;
    /** 临时列表 */

    private ListView mTempListView;
    private TextView mSDAvilable;
    /** 左下角进度显示 */
    private View mProgress;
    //扫描的服务
    private MediaScannerService mMediaScannerService;


    /**
     * 绑定服务
     */
    private ServiceConnection mMediaScannerServiceCooection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaScannerService = ((MediaScannerService.MediaScannerServiceBinder) service).getService();
            mMediaScannerService.addObserver(Local_video_activity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMediaScannerService=null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_file);
        setupActionBar();
        OPreference pref = new OPreference(this);
        //	首次运行，扫描SD卡
        if (pref.getBoolean(App.PREF_KEY_FIRST, true)) {
            this.startService(new Intent(getApplicationContext(), MediaScannerService.class).putExtra(MediaScannerService.EXTRA_DIRECTORY, Environment.getExternalStorageDirectory().getAbsolutePath()));
        }

        first_letter_overlay= (TextView) findViewById(R.id.first_letter_overlay);
        alphabet_scroller= (ImageView) findViewById(R.id.alphabet_scroller);
        mTempListView= (ListView) findViewById(R.id.templist);
        mSDAvilable= (TextView) findViewById(R.id.sd_block);
        mListView= (ListView) findViewById(android.R.id.list);
        mProgress = findViewById(android.R.id.progress);
        mLoadingLayout =findViewById(R.id.loading);

        // ~~~~~~~~~ 绑定事件
        alphabet_scroller.setClickable(true);
        alphabet_scroller.setOnTouchListener(asOnTouch);
        mListView.setOnItemClickListener(this);
        mTempListView.setOnItemClickListener(this);
        mListView.setOnCreateContextMenuListener(OnListViewMenu);
        mTempListView.setOnCreateContextMenuListener(OnTempListViewMenu);

        // ~~~~~~~~~ 加载数据

        new DataTask().execute();

        this.bindService(new Intent(getApplicationContext(), MediaScannerService.class), mMediaScannerServiceCooection, Context.BIND_AUTO_CREATE);
    }
    private void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("视频网站");
        setSupportActionBar(mToolbar);
        toobarAsBackButton(mToolbar);

    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        unbindService(mMediaScannerServiceCooection);
        super.onDestroy();
    }

    /**
     *
     * @param flag 0 开始扫描 1 正在扫描 2 扫描完成
     * @param media 扫描到的视频文件
     */
    @Override
    public void update(int flag, POMedia media) {
        //		Logger.i(flag + " " + media.path);
        switch (flag) {
            case MediaScannerService.SCAN_STATUS_START:

                break;
            case MediaScannerService.SCAN_STATUS_END://扫描完成
                if (mProgress != null)
                    mProgress.setVisibility(View.GONE);
                new DataTask().execute();
                break;
            case MediaScannerService.SCAN_STATUS_RUNNING://扫到一个文件
                if (mAdapter != null && media != null) {
                    mAdapter.add(media);
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //SD卡剩余数量
        mSDAvilable.setText(FileUtils.showFileAvailable());

        if (MediaScannerService.isRunning())
            mProgress.setVisibility(View.VISIBLE);
        else
            mProgress.setVisibility(View.GONE);
    }

    ListView.OnCreateContextMenuListener OnListViewMenu = new ListView.OnCreateContextMenuListener() {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(R.string.file_oper);
            menu.add(0, 0, 0, R.string.file_rename);
            menu.add(0, 1, 0, R.string.file_delete);
        }
    };

    ListView.OnCreateContextMenuListener OnTempListViewMenu = new ListView.OnCreateContextMenuListener() {

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(R.string.file_oper);
            menu.add(0, 2, 0, R.string.file_rename);
            menu.add(0, 3, 0, R.string.file_delete);
        }
    };

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuInfo info = item.getMenuInfo();
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) info;
        int position = contextMenuInfo.position;
        switch (item.getItemId()) {
            case 0:
                renameFile(mAdapter, mAdapter.getItem(position), position);
                break;
            case 1:
                deleteFile(mAdapter, mAdapter.getItem(position), position);
                break;
            case 2:
                renameFile(mDownloadAdapter, mDownloadAdapter.getItem(position), position);
                break;
            case 3:
                deleteFile(mDownloadAdapter, mDownloadAdapter.getItem(position), position);
                break;
        }
        return super.onContextItemSelected(item);
    };

    /** 删除文件 */
    private void deleteFile(final FileAdapter adapter, final POMedia f, final int position) {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.file_delete).setMessage(getString(R.string.file_delete_confirm, f.title)).setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    File file = new File(f.path);
                    if (file.canRead() && file.exists())
                        file.delete();

                    //					FileBusiness.deleteFile(getActivity(), f);
                    new DbHelper<POMedia>().remove(f);
                    adapter.delete(position);
                } catch (Exception e) {

                }
            }

        }).setPositiveButton(android.R.string.no, null).show();
    }

    /** 重命名文件 */
    private void renameFile(final FileAdapter adapter, final POMedia f, final int position) {
        final EditText et = new EditText(this);
        et.setText(f.title);
        new AlertDialog.Builder(this).setTitle(R.string.file_rename).setIcon(android.R.drawable.ic_dialog_info).setView(et).setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = et.getText().toString().trim();
                if (name == null || name.trim().equals("") || name.trim().equals(f.title))
                    return;

                try {
                    File fromFile = new File(f.path);
                    File nf = new File(fromFile.getParent(), name.trim());
                    if (nf.exists()) {
                        Toast.makeText(Local_video_activity.this, R.string.file_rename_exists, Toast.LENGTH_LONG).show();
                    } else if (fromFile.renameTo(nf)) {
                        f.title = name;
                        f.path = nf.getPath();
                        //						FileBusiness.renameFile(getActivity(), f);

                        new DbHelper<POMedia>().update(f);
                        adapter.notifyDataSetChanged();
                    }
                } catch (SecurityException se) {
                    Toast.makeText(Local_video_activity.this, R.string.file_rename_failed, Toast.LENGTH_LONG).show();
                }
            }

        }).setPositiveButton(android.R.string.no, null).show();
    }

    /** 单击启动播放 */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final POMedia f = parent == mListView ? mAdapter.getItem(position) : mDownloadAdapter.getItem(position);
        Intent intent = new Intent(this, LocalVideoPlayerActivity.class);
        intent.putExtra("path", f.path);
        intent.putExtra("title", f.title);
        startActivity(intent);
    }

    private class DataTask extends AsyncTask<Void, Void, List<POMedia>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingLayout.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }

        @Override
        protected List<POMedia> doInBackground(Void... params) {
            return FileBusiness.getAllSortFiles();
        }

        @Override
        protected void onPostExecute(List<POMedia> result) {
            super.onPostExecute(result);

            mAdapter = new FileAdapter(Local_video_activity.this, result);
            mListView.setAdapter(mAdapter);

            mLoadingLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    private class FileAdapter extends ArrayAdapter<POMedia> {

        private HashMap<String, POMedia> maps = new HashMap<String, POMedia>();

        public FileAdapter(Context ctx, List<POMedia> l) {
            super(ctx, (ArrayList<POMedia>) l);
            maps.clear();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final POMedia f = getItem(position);
            if (convertView == null) {
                final LayoutInflater mInflater =getLayoutInflater();
                convertView = mInflater.inflate(R.layout.fragment_file_item, null);
            }
            ((TextView) convertView.findViewById(R.id.title)).setText(f.title);

            //显示文件大小
            String file_size;
            if (f.temp_file_size > 0) {
                file_size = FileUtils.showFileSize(f.temp_file_size) + " / " + FileUtils.showFileSize(f.file_size);
            } else {
                file_size = FileUtils.showFileSize(f.file_size);
            }
            ((TextView) convertView.findViewById(R.id.file_size)).setText(file_size);
            ImageView thumb= (ImageView) convertView.findViewById(R.id.thumbnail);
            if(f.thumb_path!=null){
                thumb.setImageURI(Uri.parse(f.thumb_path));
            }

            return convertView;
        }

        public void add(POMedia item, String url) {
            super.add(item);
            if (!maps.containsKey(url))
                maps.put(url, item);
        }

        public void delete(int position) {
            synchronized (mLock) {
                mObjects.remove(position);
            }
            notifyDataSetChanged();
        }

        public POMedia getItem(String url) {
            return maps.get(url);
        }
    }

    /**
     * A-Z
     */
    private OnTouchListener asOnTouch = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:// 0
                    alphabet_scroller.setPressed(true);
                    first_letter_overlay.setVisibility(View.VISIBLE);
                    mathScrollerPosition(event.getY());
                    break;
                case MotionEvent.ACTION_UP:// 1
                    alphabet_scroller.setPressed(false);
                    first_letter_overlay.setVisibility(View.GONE);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mathScrollerPosition(event.getY());
                    break;
            }
            return false;
        }
    };

    /**
     * 显示字符
     *
     * @param y
     */
    private void mathScrollerPosition(float y) {
        int height = alphabet_scroller.getHeight();
        float charHeight = height / 28.0f;
        char c = 'A';
        if (y < 0)
            y = 0;
        else if (y > height)
            y = height;

        int index = (int) (y / charHeight) - 1;
        if (index < 0)
            index = 0;
        else if (index > 25)
            index = 25;

        String key = String.valueOf((char) (c + index));
        first_letter_overlay.setText(key);

        int position = 0;
        if (index == 0)
            mListView.setSelection(0);
        else if (index == 25)
            mListView.setSelection(mAdapter.getCount() - 1);
        else {
            if (mAdapter != null && mAdapter.getAll() != null) {
                for (POMedia item : mAdapter.getAll()) {
                    if (item.title_key.startsWith(key)) {
                        mListView.setSelection(position);
                        break;
                    }
                    position++;
                }
            }
        }
    }

    // ~~~~~~~~~~~~~~ 后续弃用，直接使用Vitamio提供的

    //	/** 扫描SD卡 */
    //	private class ScanVideoTask extends AsyncTask<Void, File, ArrayList<PFile>> {
    //		private ProgressDialog pd;
    //		private ArrayList<File> files = new ArrayList<File>();
    //
    //		@Override
    //		protected void onPreExecute() {
    //			super.onPreExecute();
    //			pd = new ProgressDialog(getActivity());
    //			pd.setMessage("正在扫描视频文件...");
    //			pd.setCanceledOnTouchOutside(false);
    //			pd.setCancelable(false);
    //			pd.show();
    //		}
    //
    //		@Override
    //		protected ArrayList<PFile> doInBackground(Void... params) {
    //			// ~~~ 遍历文件夹
    //			eachAllMedias(Environment.getExternalStorageDirectory());
    //
    //			// ~~~ 提取缩略图、视频尺寸等。
    //			FileBusiness.batchBuildThumbnail(getActivity(), files);
    //
    //			// ~~~ 入库
    //			FileBusiness.batchInsertFiles(getActivity(), files);
    //
    //			// ~~~ 查询数据
    //			return FileBusiness.getAllSortFiles(getActivity());
    //		}
    //
    //		@Override
    //		protected void onProgressUpdate(final File... values) {
    //			pd.setMessage(values[0].getName());
    //		}
    //
    //		/** 遍历所有文件夹，查找出视频文件 */
    //		public void eachAllMedias(File f) {
    //			if (f != null && f.exists() && f.isDirectory()) {
    //				File[] files = f.listFiles();
    //				if (files != null) {
    //					for (File file : f.listFiles()) {
    //						if (file.isDirectory()) {
    //							eachAllMedias(file);
    //						} else if (file.exists() && file.canRead() && FileUtils.isVideo(file)) {
    //							this.files.add(file);
    //						}
    //						publishProgress(file);
    //					}
    //				}
    //			}
    //		}
    //
    //		@Override
    //		protected void onPostExecute(ArrayList<PFile> result) {
    //			super.onPostExecute(result);
    //			mAdapter = new FileAdapter(getActivity(), result);
    //			mListView.setAdapter(mAdapter);
    //			pd.dismiss();
    //		}
    //	}
}
