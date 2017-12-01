package com.example.zju.markmark;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private String fileType = "";
    private String filePath = "";
    private TextView textView;
    private String text;
    private String splitedText;
    private FloatingActionButton editFab;
    private FloatingActionButton backFab;
    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editFab = (FloatingActionButton) findViewById(R.id.edit_fab);
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editFab.setVisibility(View.GONE);
                backFab.setVisibility(View.VISIBLE);
                textView = (TextView) findViewById(R.id.content);
                text = textView.getText().toString();
                editMode = !editMode;
                StringBuffer newText = new StringBuffer();
                for (String retval : text.replace("\n", "").split("。")) {
                    retval = retval.concat("。\n\n");
                    newText.append(retval);
                }
                splitedText = newText.toString().trim();
                textView.setText(splitedText, TextView.BufferType.SPANNABLE);
                getEachSentence(textView);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
            }
        });

        backFab = (FloatingActionButton) findViewById(R.id.back_fab);
        backFab.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               backFab.setVisibility(View.GONE);
               editFab.setVisibility(View.VISIBLE);
               textView.setText(text);
           }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        File txtDir = new File(FileListActivity.txtDefaultFolder);
        if (!txtDir.exists()) { txtDir.mkdirs(); }
        File jsonDir = new File(FileListActivity.jsonDefaultFolder);
        if (!jsonDir.exists()) { jsonDir.mkdirs(); }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent!=null) {
            Log.d(TAG, "INTENT IS NOT NULL");
            fileType = intent.getStringExtra("file_type");
            filePath = intent.getStringExtra("file_path");
        }
    }

    void drawLines() {
       ;
    }

    @Override
    public void onStart(){
        super.onStart();
        if (filePath!=null && !filePath.equals("")) {
            Log.d(TAG, filePath + "  of  " + fileType);
            textView = (TextView) findViewById(R.id.content);
            if(editMode) {
                editFab.setVisibility(View.GONE);
                backFab.setVisibility(View.VISIBLE);
                textView.setText(splitedText, TextView.BufferType.SPANNABLE);
                getEachSentence(textView);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                drawLines();
            }
            else {
                editFab.setVisibility(View.VISIBLE);
                backFab.setVisibility(View.GONE);
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
                    StringBuffer sb = new StringBuffer();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n"); //readLine会消除\n
                    }
                    text = sb.toString();
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    if (br!=null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                textView.setText(text);
            }
            Log.i(TAG, "onStart()");
        }
    }

    @Override
    public void onBackPressed() { //按下手机的后退按钮
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START); //如果左侧的菜单处于打开状态，就关闭菜单
        } else {
            AlertDialog.Builder exitDialog = new AlertDialog.Builder(MainActivity.this);
            exitDialog.setTitle("MarkMark");
            exitDialog.setMessage("您确定要退出吗?");
            exitDialog.setCancelable(true);
            exitDialog.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            exitDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            exitDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this,"Setting",Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.font_big) {
            textView = (TextView) findViewById(R.id.content);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textView.getTextSize()+3);//getTextSize获取的值是px的值
        } else if (id == R.id.font_small) {
            textView = (TextView) findViewById(R.id.content);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textView.getTextSize()-3);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_opentxt) {
            editMode =false;
            Intent intent = new Intent(MainActivity.this,FileListActivity.class);
            intent.putExtra("file_type","txt");
            startActivity(intent);
        } else if (id == R.id.nav_savejson) {

        } else if (id == R.id.nav_openjson) {
            editMode =false;
            Intent intent = new Intent(MainActivity.this,FileListActivity.class);
            intent.putExtra("file_type","json");
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getEachSentence(TextView textView) {
        Spannable spans = (Spannable)textView.getText();
        Integer[] indices = getIndices(textView.getText().toString().trim(), '。');
        int start = 0;
        int end;
        for (int i = 0; i < indices.length; i++) {
            ClickableSpan clickSpan = getClickableSpan(i);
            end = (i < indices.length ? indices[i] : spans.length());
            spans.setSpan(clickSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            start = end + 3; //考虑到两个回车键
        }
        /*textView.setHighlightColor(Color.BLUE);*/
    }

    private ClickableSpan getClickableSpan(final int index){
        return new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                TextView tv = (TextView) widget;
                String s = tv.getText().subSequence(tv.getSelectionStart(), tv.getSelectionEnd()).toString();
                if (text != null) {
                    String sentenceString = s.trim();
                    int sentenceNumber = index;
                    String articleString = filePath;
                    Intent intent = BangActivity.newIntent(MainActivity.this, articleString, sentenceNumber, sentenceString);
                    startActivity(intent);
                }
                Log.i(TAG, "tapped on:" + s);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ContextCompat.getColor(MainActivity.this,R.color.textDarkGray));
                ds.setUnderlineText(false);
            }
        };
    }

    public static Integer[] getIndices(String s, char c) {
        int pos = s.indexOf(c, 0);
        List<Integer> indices = new ArrayList<Integer>();
        while (pos != -1) {
            indices.add(pos);
            pos = s.indexOf(c, pos + 1);
        }
        return (Integer[]) indices.toArray(new Integer[0]);
    }

}