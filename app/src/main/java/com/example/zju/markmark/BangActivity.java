package com.example.zju.markmark;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class BangActivity extends AppCompatActivity {

    private static final String EXTRA_ARTICLE_STRING = "com.example.zju.markmark.article_string";
    private static final String EXTRA_SENTENCE_NUMBER = "com.example.zju.markmark.sentence_number";
    private static final String EXTRA_SENTENCE_STRING = "com.example.zju.markmark.sentence_string";

    public static Intent newIntent(Context packageContext, String articleString, int sentenceNumber, String sentenceString) {
        Intent intent = new Intent(packageContext, BangActivity.class);
        intent.putExtra(EXTRA_ARTICLE_STRING, articleString);
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_SENTENCE_NUMBER, sentenceNumber);
        intent.putExtras(bundle);
        intent.putExtra(EXTRA_SENTENCE_STRING, sentenceString);
        return intent;
    }

    private static final String TAG = "BangActivity";
    SmallBangView mSmallBangView;
    String astr;
    String str;
    int sid;
    BottomSheetDialog dialog;
    Mark mMark;
    private Gson gson;
    private GsonBuilder builder;
    private final String jsonDefaultFolder = "/storage/emulated/0/JS/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bang);
        mSmallBangView = (SmallBangView)findViewById(R.id.activity_bang);
        astr = getIntent().getStringExtra(EXTRA_ARTICLE_STRING);
        Bundle bundle = this.getIntent().getExtras();
        sid = bundle.getInt(EXTRA_SENTENCE_NUMBER);
        str = getIntent().getStringExtra(EXTRA_SENTENCE_STRING);
        mMark = new Mark(astr, sid, str);
        Log.i(TAG, str);
    }

    @Override
    protected void onStart() {
        super.onStart();

        int length = str.length();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int SCREEN_WIDTH = dm.widthPixels;

        Rect outRect1 = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = this.getResources().getDimensionPixelSize(resourceId);
        int SCREEN_HEIGHT = outRect1.height() - result;

        int COLUMN_COUNT = (int)Math.sqrt(((double)length * (double)SCREEN_WIDTH) / (double)SCREEN_HEIGHT);
        int ROW_COUNT = length / COLUMN_COUNT + 1;
        mSmallBangView.setColumnCount(COLUMN_COUNT);
        mSmallBangView.setRowCount(ROW_COUNT);

        for (int i = 0; i < length; i++) {
            TextView tv = new TextView(this);
            tv.setText(String.valueOf(str.charAt(i)));
            if (length > 100) {
                tv.setTextSize(10);
            }
            else if (length < 50) {
                tv.setTextSize(30);
            }
            else {
                tv.setTextSize(20);
            }
            tv.setGravity(Gravity.CENTER);
            tv.setWidth(SCREEN_WIDTH / COLUMN_COUNT);
            tv.setHeight(SCREEN_HEIGHT / ROW_COUNT);
            SmallBangView.Spec rowSpec = SmallBangView.spec(i / COLUMN_COUNT);
            SmallBangView.Spec colSpec = SmallBangView.spec(i % COLUMN_COUNT);
            SmallBangView.LayoutParams params = new SmallBangView.LayoutParams(rowSpec, colSpec);
            /*params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dp_01);
            params.topMargin = getResources().getDimensionPixelSize(R.dimen.dp_01);
            params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dp_01);
            params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dp_01);*/
            mSmallBangView.addView(tv, params);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                dialog = new BottomSheetDialog(this);
                View view = getLayoutInflater().inflate(R.layout.popup_layout, null);
                dialog.setContentView(view);
                dialog.show();
        }
        return true;
    }

    public void doclick1(View v) {
        setEntity("Location->Country");
        mSmallBangView.clearChosen();
        dialog.cancel();
    }
    public void doclick2(View v) {
        setEntity("Location->City");
        mSmallBangView.clearChosen();
        dialog.cancel();
    }
    public void doclick3(View v) {
        setEntity("Person");
        mSmallBangView.clearChosen();
        dialog.cancel();
    }
    public void doclick4(View v) {
        setRelation("City∈Country");
        waitingToggle();
        dialog.cancel();
        mSmallBangView.clearChosen();
    }
    public void doclick5(View v) {
        setRelation("Person∈Country");
        waitingToggle();
        dialog.cancel();
        mSmallBangView.clearChosen();
    }

    private void setEntity(String lab) {
        String label = lab;
        int start = mSmallBangView.getChosenStart();
        int end = mSmallBangView.getChosenEnd();
        String text = mSmallBangView.getChosen(start, end);
        MarkEntity markEntity = new MarkEntity(label, start, end, text);
        mMark.setEntityMentions(markEntity);
        mSmallBangView.Markit(mMark);
        updateJson(mMark);
        Toast.makeText(BangActivity.this, label + " + " + text, Toast.LENGTH_SHORT).show();
    }

    MarkRelation markRelation = null;

    private void setRelation(String lab) {
        String label = lab;
        if (mSmallBangView.isWaiting()) {
            int start = mSmallBangView.getChosenStart();
            int end = mSmallBangView.getChosenEnd();
            String em2Text = mSmallBangView.getChosen(start, end);
            markRelation.setEm2Text(em2Text, start, end);
            mMark.setRelationMentions(markRelation);
            mSmallBangView.nowMarkit(markRelation);
            updateJson(mMark);
            Toast.makeText(BangActivity.this, label + " + " + em2Text + "\n" + label + " Marked", Toast.LENGTH_SHORT).show();
        }
        else {
            int start = mSmallBangView.getChosenStart();
            int end = mSmallBangView.getChosenEnd();
            String em1Text = mSmallBangView.getChosen(start, end);
            markRelation = new MarkRelation(em1Text, label, start, end);
            mSmallBangView.tmpMarkit(start, end);
            Toast.makeText(BangActivity.this, label + " + " + em1Text + "\nPlease Mark Another Entity", Toast.LENGTH_SHORT).show();
        }
    }

    private void waitingToggle(){
        if (mSmallBangView.isWaiting()){
            mSmallBangView.setWaiting(false);
            Log.i(TAG, "is not waiting now");
        }
        else {
            mSmallBangView.setWaiting(true);
            Log.i(TAG, "is waiting now");
        }
    }

    private void updateJson(Mark mark) {
        builder=new GsonBuilder();
        gson=builder.create();
        String json = gson.toJson(mark, Mark.class);
        BufferedWriter writer = null;
        try{
            String originalName = astr.substring(astr.lastIndexOf("/") + 1).replace(".txt", ".json");
            String jsonfilename = "No." + String.valueOf(sid) + " sentence of: " + originalName;
            FileOutputStream out = new FileOutputStream(jsonDefaultFolder + jsonfilename);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(json);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
