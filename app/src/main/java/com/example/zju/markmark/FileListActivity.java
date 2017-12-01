package com.example.zju.markmark;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FileListActivity extends AppCompatActivity {

    private static final String TAG = "FileListActivity";
    private String fileType = "";
    private boolean bShowAll = false;
    private final String txtDefaultFolder = "/storage/emulated/0/TXT";
    private final String jsonDefaultFolder = "/storage/emulated/0/JS";
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private ProgressBar pb;
    private ArrayList<String> paths = new ArrayList<String>();
    private ArrayList<String> names = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        Intent intent = getIntent();
        fileType = intent.getStringExtra("file_type");
        Log.d(TAG,fileType);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initListWithPB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.file_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_show_all) {
            bShowAll = true;
            initListWithPB();
            return true;
        } else if (id == R.id.action_default_folder) {
            bShowAll = false;
            initListWithPB();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //遍历手机，对于符合格式要求的文件，把路径存入paths中，把单纯的文件名存入names中
    public void GenFileListAll(File dir, ArrayList<String> paths, ArrayList<String> names) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) { //是文件夹
                if (file.getAbsolutePath().equals("/storage/emulated/0/Android")) { //是系统文件夹（这里有一大堆日志txt）
                    continue;
                }
                 GenFileListAll(file,paths,names);
            } else { //是文件
                String name = file.getName();
                String extension = name.substring(name.lastIndexOf(".")+1);
                if (extension.toLowerCase().equals(fileType)) { //如果文件名以 .txt,.TXT或者.json,.JSON结尾
                    names.add(name);
                    paths.add(file.getParent());
                }
            }
        }
    }

    //在默认文件夹中，对于符合格式要求的文件，把路径存入paths中，把单纯的文件名存入names中
    public void GenFileListDefault(ArrayList<String> paths, ArrayList<String> names) {
        File dir;
        if(fileType.equals("txt")) {
            dir = new File(txtDefaultFolder);
        } else {
            dir = new File(jsonDefaultFolder);
        }
        GenFileListAll(dir, paths, names);
    }

    private void changeUI() {
        ArrayList<Map<String, String>> maps = new ArrayList<Map<String,String>>();
        for (int i=0; i<paths.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("first", names.get(i));
            map.put("second", paths.get(i));
            maps.add(map);
        }
        simpleAdapter = new SimpleAdapter(FileListActivity.this, maps, R.layout.file_list,
                new String[]{"first","second"}, new int[]{android.R.id.text1,android.R.id.text2});
        listView = (ListView) findViewById(R.id.file_list);
        listView.setAdapter(simpleAdapter);
        simpleAdapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                String absolutePath = paths.get(position) + "/" + names.get(position);
                Intent intent = new Intent(FileListActivity.this,MainActivity.class);
                intent.putExtra("file_path",absolutePath);
                intent.putExtra("file_type",fileType);
                Log.d(TAG, absolutePath+" of "+fileType);
                startActivity(intent);
            }
        });
        pb = (ProgressBar) findViewById(R.id.list_progress_bar);
        pb.setVisibility(ProgressBar.GONE);
    }

    //对列表进行初始化（带有进度条）
    private void initListWithPB() {
        pb = (ProgressBar) findViewById(R.id.list_progress_bar);
        pb.setVisibility(ProgressBar.VISIBLE);

        new Thread() {
            public void run() {
                paths.clear();
                names.clear();
                File root = Environment.getExternalStorageDirectory(); //获得外部存储的根目录
                if (bShowAll) {
                    GenFileListAll(root, paths, names);
                } else {
                    GenFileListDefault(paths, names);
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        changeUI();
                    }
                });
            }
        }.start();
    }
}
