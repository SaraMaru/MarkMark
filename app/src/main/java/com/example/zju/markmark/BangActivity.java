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
import java.util.ArrayList;

public class BangActivity extends AppCompatActivity {

    private static final String EXTRA_ARTICLE_STRING = "com.example.zju.markmark.article_string";
    private static final String EXTRA_SENTENCE_NUMBER = "com.example.zju.markmark.sentence_number";
    private static final String EXTRA_SENTENCE_STRING = "com.example.zju.markmark.sentence_string";

    private static final String KEY_MARK_DATA = "mdata";
    private static final String KEY_CHOSEN_DATA = "mchosen";

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


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(KEY_MARK_DATA, mMark);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bang);
        mSmallBangView = (SmallBangView)findViewById(R.id.activity_bang);
        astr = getIntent().getStringExtra(EXTRA_ARTICLE_STRING);
        Bundle bundle = this.getIntent().getExtras();
        sid = bundle.getInt(EXTRA_SENTENCE_NUMBER);
        str = getIntent().getStringExtra(EXTRA_SENTENCE_STRING);
        if (savedInstanceState != null) {
            mMark = (Mark)savedInstanceState.getSerializable(KEY_MARK_DATA);
            mSmallBangView.setThisMark(mMark); // Delivery the Mark to small_bang_view and it starts to draw relations marking
            mSmallBangView.invalidate();
        }
        else {
            mMark = new Mark(astr, sid, str);
        }
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
            tv.setTextSize(1234f / length);
            tv.setGravity(Gravity.CENTER);
            tv.setWidth(SCREEN_WIDTH / COLUMN_COUNT);
            tv.setHeight(SCREEN_HEIGHT / ROW_COUNT);
            SmallBangView.Spec rowSpec = SmallBangView.spec(i / COLUMN_COUNT);
            SmallBangView.Spec colSpec = SmallBangView.spec(i % COLUMN_COUNT);
            SmallBangView.LayoutParams params = new SmallBangView.LayoutParams(rowSpec, colSpec);
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
        if (!mSmallBangView.getChosenEmpty()) {
            setEntity("Country");
            mSmallBangView.clearChosen();
            dialog.cancel();
        }
        else {
            Toast.makeText(BangActivity.this, "Please choose the words!", Toast.LENGTH_LONG).show();
            dialog.cancel();
        }
    }
    public void doclick2(View v) {
        if (!mSmallBangView.getChosenEmpty()) {
            setEntity("City");
            mSmallBangView.clearChosen();
            dialog.cancel();
        }
        else {
            Toast.makeText(BangActivity.this, "Please choose the words!", Toast.LENGTH_LONG).show();
            dialog.cancel();
        }
    }
    public void doclick3(View v) {
        if (!mSmallBangView.getChosenEmpty()) {
            setEntity("Person");
            mSmallBangView.clearChosen();
            dialog.cancel();
        }
        else {
            Toast.makeText(BangActivity.this, "Please choose the words!", Toast.LENGTH_LONG).show();
            dialog.cancel();
        }
    }
    public void doclick4(View v) {
        if (!mSmallBangView.getChosenEmpty()) {
            setRelation("City∈Country");
            waitingToggle();
            dialog.cancel();
            mSmallBangView.clearChosen();
        }
        else {
            Toast.makeText(BangActivity.this, "Please choose the words!", Toast.LENGTH_LONG).show();
            dialog.cancel();
        }
    }
    public void doclick5(View v) {
        if (!mSmallBangView.getChosenEmpty()) {
            setRelation("Person∈Country");
            waitingToggle();
            dialog.cancel();
            mSmallBangView.clearChosen();
        }
        else {
            Toast.makeText(BangActivity.this, "Please choose the words!", Toast.LENGTH_LONG).show();
            dialog.cancel();
        }
    }
    public void doclick6(View v) {
        if (!mSmallBangView.getChosenEmpty()) {
            cancelMarking();
            mSmallBangView.clearChosen();
            dialog.cancel();
        }
        else {
            Toast.makeText(BangActivity.this, "Please choose the words!", Toast.LENGTH_LONG).show();
            dialog.cancel();
        }
    }

    private void setEntity(String lab) {
        String label = lab;
        int start = mSmallBangView.getChosenStart();
        int end = mSmallBangView.getChosenEnd();
        String text = mSmallBangView.getChosen(start, end);
        MarkEntity markEntity = new MarkEntity(label, start, end, text);
        mMark.setEntityMentions(markEntity);
        /*mSmallBangView.Markit(mMark);*/
        updateJson(mMark);
        mSmallBangView.setThisMark(mMark); // Delivery the Mark to small_bang_view and it starts to draw relations marking
        mSmallBangView.invalidate();
        Toast.makeText(BangActivity.this, label + " + " + text, Toast.LENGTH_SHORT).show();
    }

    MarkRelation markRelation = null;

    private void setRelation(String lab) {
        String label = lab;
        if (mSmallBangView.isWaiting()) {
            if (markRelation.getLabel() != label) {
                Toast.makeText(BangActivity.this, "Please click the right button!", Toast.LENGTH_LONG).show();
                waitingToggle();
            }
            else {
                int start = mSmallBangView.getChosenStart();
                int end = mSmallBangView.getChosenEnd();
                String em2Text = mSmallBangView.getChosen(start, end);
                markRelation.setEm2Text(em2Text, start, end);
                mMark.setRelationMentions(markRelation);
                /*mSmallBangView.nowMarkit(markRelation);*/
                updateJson(mMark);
                mSmallBangView.setThisMark(mMark); // Delivery the Mark to small_bang_view and it starts to draw relations marking
                mSmallBangView.invalidate();
                Toast.makeText(BangActivity.this, label + " + " + em2Text + "\n" + label + " Marked", Toast.LENGTH_SHORT).show();
            }
        } // Choosing the second entity
        else {
            int start = mSmallBangView.getChosenStart();
            int end = mSmallBangView.getChosenEnd();
            String em1Text = mSmallBangView.getChosen(start, end);
            markRelation = new MarkRelation(em1Text, label, start, end);
            /*mSmallBangView.tmpMarkit(start, end);*/
            Toast.makeText(BangActivity.this, label + " + " + em1Text + "\nPlease Mark Another Entity", Toast.LENGTH_SHORT).show();
        } // When just choosing one entity in marking a relation
    }

    private void waitingToggle(){
        if (mSmallBangView.isWaiting()){
            mSmallBangView.setTempRelation(null);
            mSmallBangView.setWaiting(false);
            mSmallBangView.invalidate();
            Log.i(TAG, "is not waiting now");
        }
        else {
            mSmallBangView.setTempRelation(markRelation);
            mSmallBangView.setWaiting(true);
            mSmallBangView.invalidate();
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
            FileOutputStream out = new FileOutputStream(FileListActivity.jsonDefaultFolder + "/" + jsonfilename);
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

    private void cancelMarking() {
        int start = mSmallBangView.getChosenStart();
        int end = mSmallBangView.getChosenEnd();
        ArrayList<MarkEntity> entityMentions = mMark.getEntityMentions();
        for (MarkEntity mE : entityMentions) {
            if (start >= mE.getStart() && end <= mE.getEnd()) {
                entityMentions.remove(mE);
                break;
            } // Legal cancel choosing
        }
        ArrayList<MarkRelation> relationMentions = mMark.getRelationMentions();
        for (MarkRelation mR : relationMentions) {
            if ((start >= mR.getStart1() && end <= mR.getEnd1()) || (start >= mR.getStart2() && end <= mR.getEnd2())) {
                relationMentions.remove(mR);
                break;
            } // No matter which entity is chosen
        }
        updateJson(mMark);
        mSmallBangView.setThisMark(mMark); // Delivery the Mark to small_bang_view and it starts to draw relations marking
        mSmallBangView.invalidate();
    }

}