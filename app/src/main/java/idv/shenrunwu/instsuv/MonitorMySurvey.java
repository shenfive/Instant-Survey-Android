package idv.shenrunwu.instsuv;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MonitorMySurvey extends AppCompatActivity {


    Button optionLine1, optionLine2, optionLine3, optionLine4, optionLine5, reOpen;

    TextView monitorOption1, monitorOption2,monitorOption3,monitorOption4,monitorOption5;
    TextView optionSpace1, optionSpace2, optionSpace3, optionSpace4, optionSpace5;
    TextView monitorTopic,monitorEndOfSurveyTime;
    Date endOfSuvery;
    ListView answerList;

    ListAdapter mAdapter;
    DatabaseReference ref;

    boolean onGoingFlag,anonmityFlag,suggestionFlag;


    FrameLayout monitorLineLayout1, monitorLineLayout2, monitorLineLayout3, monitorLineLayout4, monitorLineLayout5;

    final List<String> list=new ArrayList<>();

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_my_survey);
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });


        //UI 設定
        optionLine1=(Button)findViewById(R.id.optionLine1);
        optionLine2=(Button)findViewById(R.id.optionLine2);
        optionLine3=(Button)findViewById(R.id.optionLine3);
        optionLine4=(Button)findViewById(R.id.optionLine4);
        optionLine5=(Button)findViewById(R.id.optionLine5);
        reOpen=(Button)findViewById(R.id.reOpen);


        optionSpace1=(TextView)findViewById(R.id.optionSpace1);
        optionSpace2=(TextView)findViewById(R.id.optionSpace2);
        optionSpace3=(TextView)findViewById(R.id.optionSpace3);
        optionSpace4=(TextView)findViewById(R.id.optionSpace4);
        optionSpace5=(TextView)findViewById(R.id.optionSpace5);

        monitorTopic=(TextView)findViewById(R.id.monitorTopic);
        monitorEndOfSurveyTime=(TextView)findViewById(R.id.monitorEndOfSurveyTime);


        monitorOption1=(TextView)findViewById(R.id.monitorOption1);
        monitorOption2=(TextView)findViewById(R.id.monitorOption2);
        monitorOption3=(TextView)findViewById(R.id.monitorOption3);
        monitorOption4=(TextView)findViewById(R.id.monitorOption4);
        monitorOption5=(TextView)findViewById(R.id.monitorOption5);

        monitorLineLayout1=(FrameLayout)findViewById(R.id.monitorLineLayout1);
        monitorLineLayout2=(FrameLayout)findViewById(R.id.monitorLineLayout2);
        monitorLineLayout3=(FrameLayout)findViewById(R.id.monitorLineLayout3);
        monitorLineLayout4=(FrameLayout)findViewById(R.id.monitorLineLayout4);
        monitorLineLayout5=(FrameLayout)findViewById(R.id.monitorLineLayout5);

        answerList=(ListView)findViewById(R.id.answerList);



        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

        findViewById(R.id.finishMonitor).setOnTouchListener(mDelayHideTouchListener);



        //初始化
        final Bundle myBundle=getIntent().getExtras();
        ref=FirebaseDatabase.getInstance().getReference("survey").child(myBundle.getString("surveyUUID"));
        onGoingFlag=false;
        anonmityFlag=false;
        suggestionFlag=false;



//        optionLine1.setLayoutParams(params);

//        建立問卷主要資料


        mAdapter = new ArrayAdapter<String>(MonitorMySurvey.this,android.R.layout.simple_list_item_1,list);
        answerList.setAdapter(mAdapter);


        ValueEventListener valueEventListener1=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numberOfOption=Integer.parseInt(dataSnapshot.child("numberOfOption").getValue().toString());
                monitorTopic.setText("建立者:"+dataSnapshot.child("creatorDisplayname").getValue().toString()+"\n"+
                        dataSnapshot.child("topic").getValue().toString());
                endOfSuvery=new Date(Long.parseLong(dataSnapshot.child("endOfSurveyTime").getValue().toString()));
                Date nowDate=new Date();
                SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy MM-dd HH:mm");
                monitorEndOfSurveyTime.setText(" 結束時間："+dateFormat.format(endOfSuvery)+"  調查代碼："+
                                            dataSnapshot.child("showID").getValue().toString());

                onGoingFlag=(nowDate.getTime()<endOfSuvery.getTime());
                anonmityFlag=(dataSnapshot.child("allowAnonymity").getValue().toString()).equals("true");
                suggestionFlag=(dataSnapshot.child("suggest").getValue().toString()).equals("true");


                monitorLineLayout3.setVisibility(View.GONE);
                monitorLineLayout4.setVisibility(View.GONE);
                monitorLineLayout5.setVisibility(View.GONE);
                monitorOption1.setText("  1."+dataSnapshot.child("option1").getValue().toString());
                monitorOption2.setText("  2."+dataSnapshot.child("option2").getValue().toString());

                switch (numberOfOption){
                    case 5:
                        monitorLineLayout5.setVisibility(View.VISIBLE);
                        monitorOption5.setText("  5."+dataSnapshot.child("option5").getValue().toString());
                    case 4:
                        monitorLineLayout4.setVisibility(View.VISIBLE);
                        monitorOption4.setText("  4."+dataSnapshot.child("option4").getValue().toString());
                    case 3:
                        monitorLineLayout3.setVisibility(View.VISIBLE);
                        monitorOption3.setText("  3."+dataSnapshot.child("option3").getValue().toString());
                }
                ValueEventListener valueEventListener2 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        updateUserList();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };

//        若己過期，取一次就好
                if(onGoingFlag){
                    ref.child("answers").addValueEventListener(valueEventListener2);
                }else {
//                    if((myBundle.get("creatorDisplayname").toString())
//                            .equals(myBundle.getString("userDispalyname").toString())) {
//                        reOpen.setVisibility(View.VISIBLE);
//                    }
                    ref.child("answers").addListenerForSingleValueEvent(valueEventListener2);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener1);





    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);




    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    public void endMonitor(View v){

        finish();
    }


    private LinearLayout.LayoutParams getParams(int w) {
        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight=w;
        params.width=0;
        return params;
    }

    private void updateUserList(){
        ValueEventListener valueEvent = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                list.clear();

                SuveyConter suveyConter=new SuveyConter();

                for (DataSnapshot ans:dataSnapshot.getChildren())
                {
                    String answerName="";
                    if(anonmityFlag){
                        answerName=ans.child("alias").getValue().toString();
                    }else {
                        answerName=ans.child("displayname").getValue().toString();
                    }
                    String suggestion="";
                    if(suggestionFlag){
                        suggestion="\n建議為："+ans.child("suggest").getValue().toString();
                    }

                    list.add("回覆者："+answerName+"\n"+
                            "選項："+ans.child("selectedID").getValue().toString()+suggestion
                            );
                    switch (ans.child("selectedID").getValue().toString()){
                        case "1":
                            suveyConter.o1++;
                            break;
                        case "2":
                            suveyConter.o2++;
                            break;
                        case "3":
                            suveyConter.o3++;
                            break;
                        case "4":
                            suveyConter.o4++;
                            break;
                        case "5":
                            suveyConter.o5++;
                            break;
                    }

                }
                answerList.invalidateViews();
                suveyConter.setView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.child("answers").addListenerForSingleValueEvent(valueEvent);
    }

    protected class SuveyConter {
        int o1 = 0, o2 = 0, o3 = 0, o4 = 0, o5 = 0;

        SuveyConter() {
        }

        public int total() {
            return o1 + o2 + o3 + o4 + o5;
        }

        public float average(int code) {
            float answer = 0f;
            switch (code) {
                case 1:
                    answer = (float) o1 / (float) this.total();
                    break;
                case 2:
                    answer = (float) o2 / (float) this.total();
                    break;
                case 3:
                    answer = (float) o3 / (float) this.total();
                    break;
                case 4:
                    answer = (float) o4 / (float) this.total();
                    break;
                case 5:
                    answer = (float) o5 / (float) this.total();
                    break;
            }
            return answer;
        }

        public int maxNumber() {
            int[] intArry = {o1, o2, o3, o4, o5};
            int max = 0;
            for (int counter = 0; counter <= 4; counter++) {
                if (max < intArry[counter]) {
                    max = intArry[counter];
                }
            }
            return max;
        }

        public int maxKey() {
            int[] intArry = {o1, o2, o3, o4, o5};
            int key = -1;
            int max = 0;
            for (int counter = 0; counter <= 4; counter++) {
                if (max < intArry[counter]) {
                    max = intArry[counter];
                    key = counter;
                }
            }
            return key;
        }

        private void setView(){

            int total=this.total();
            Log.d("xxxx",total+"");

            if(total>0) {
                optionLine1.setLayoutParams(getParams((100 / total) * this.o1 + 1));
                optionSpace1.setLayoutParams(getParams(111 - (100 / total) * this.o1));
                optionSpace1.setText(this.o1 + "  ");

                optionLine2.setLayoutParams(getParams((100 / total) * this.o2 + 1));
                optionSpace2.setLayoutParams(getParams(111 - (100 / total) * this.o2));
                optionSpace2.setText(this.o2 + "  ");

                optionLine3.setLayoutParams(getParams((100 / total) * this.o3 + 1));
                optionSpace3.setLayoutParams(getParams(111 - (100 / total) * this.o3));
                optionSpace3.setText(this.o3 + "  ");

                optionLine4.setLayoutParams(getParams((100 / total) * this.o4 + 1));
                optionSpace4.setLayoutParams(getParams(111 - (100 / total) * this.o4));
                optionSpace4.setText(this.o4 + "  ");

                optionLine5.setLayoutParams(getParams((100 / total) * this.o5));
                optionSpace5.setLayoutParams(getParams(111 - (100 / total) * this.o5));
                optionSpace5.setText(this.o5 + "  ");
            }else{
                Toast.makeText(MonitorMySurvey.this,"目前尚無人票，請耐心等候",Toast.LENGTH_LONG).show();
            }


        }


    }

    public void reOpenSuvey(View v){
//        Date now=new Date();
//        long reEndOfSurveyTime=now.getTime()+10000;
//        Bundle myBundle=getIntent().getExtras();
//        DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
//        ref.child("survey")
//            .child(myBundle.getString("surveyUUID"))
//            .child("endOfSurveyTime")
//            .setValue(endOfSuvery+"");
//        ref.child("survey")
//            .child("idlist")
//            .child(myBundle.getString("showID"))
//            .child(myBundle.getString("surveyUUID"))
//            .child("endOfSurveyTime")
//            .setValue(endOfSuvery+"");
//        ref.child("user")
//            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//            .child("mySurvey")
//            .child(myBundle.getString("surveyUUID"))
//            .child("endOfSurveyTime")
//            .setValue(endOfSuvery);
//        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy MM-dd HH:mm");
//        finish();
    }


}
