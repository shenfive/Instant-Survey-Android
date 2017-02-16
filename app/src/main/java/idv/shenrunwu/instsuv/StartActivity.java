package idv.shenrunwu.instsuv;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class StartActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    List<SurveyListItem> surveyItems= new ArrayList<SurveyListItem>();
    List<MySurveyListItem> mySurveyItems = new ArrayList<>();

    Button logout,requestEmailVerification;
    ListView surveyList,mySurveys;
    EditText topic,option1,option2,option3,option4,option5,modifyDisplayNameET,surveyCodeET;
    Spinner numberOfQ,surveyAvailableTime;
    TextView modifyDisplaynameTV,dispplayEmailAndStatusTV;
    CheckBox allowAnonymity,suggestCB;
    int numberOfOption=2;
    long surveyAvailableTimeLong=3_600_000;
    String surveyAvailableTimeString="一小時";
    FirebaseUser currentUser;
    final String administratorName="Survey    Admin";


    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor myEditor;
    private static final String cr = String.format("%n");
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    public void onResume(){
        super.onResume();

            Toast toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.FILL, 0, 0);
            LinearLayout toastView = (LinearLayout) toast.getView();
            ImageView imageCodeProject = new ImageView(getApplicationContext());
            imageCodeProject.setImageResource(R.drawable.resumepicture);
            toastView.addView(imageCodeProject, 0);
            toast.show();
        updateMySurveys();
    }



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //進入時不出現鍵盤
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //取得 設定檔
        mySharedPreferences = getApplicationContext().getSharedPreferences(getResources()
                .getString(R.string.setting_pre),0);
        myEditor = mySharedPreferences.edit();

        //隱狀態列
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //抓取現有使用者
        currentUser=FirebaseAuth.getInstance().getCurrentUser();

        // 設定UI物件
        logout=(Button)findViewById(R.id.start_logout);
        requestEmailVerification=(Button)findViewById(R.id.requestEmailVerification);
        surveyList=(ListView)findViewById(R.id.surveyList);
        mySurveys=(ListView)findViewById(R.id.mySurveys);
        numberOfQ=(Spinner)findViewById(R.id.numbersOfQspinner);
        surveyAvailableTime=(Spinner)findViewById(R.id.surveyAvailableTime);
        modifyDisplaynameTV=(TextView)findViewById(R.id.modifyDisplaynameTV);
        dispplayEmailAndStatusTV=(TextView)findViewById(R.id.dispplayEmailAndStatusTV);
        surveyCodeET=(EditText)findViewById(R.id.surveyCodeET);
        topic=(EditText)findViewById(R.id.topic);
        option1=(EditText)findViewById(R.id.option1);
        option2=(EditText)findViewById(R.id.option2);
        option3=(EditText)findViewById(R.id.option3);
        option4=(EditText)findViewById(R.id.option4);
        option5=(EditText)findViewById(R.id.option5);
        modifyDisplayNameET=(EditText) findViewById(R.id.modifyDisplayNameET);
        allowAnonymity=(CheckBox)findViewById(R.id.allowAnonymity);
        suggestCB=(CheckBox)findViewById(R.id.suggest);

        //設定 surveyList Listener

        surveyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String surveyIDSelected=((SurveyListItem)parent.getItemAtPosition(position)).surveyID;
                DatabaseReference databaseReference=getDatabaseReference("survey").child(surveyIDSelected);
                ValueEventListener surveyListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Intent intent = new Intent(StartActivity.this,Fill_In_Survey.class);
                        intent.putExtra("allowAnonymity",dataSnapshot.child("allowAnonymity").getValue().toString());
                        intent.putExtra("createTime",dataSnapshot.child("createTime").getValue().toString());
                        intent.putExtra("creatorUid",dataSnapshot.child("creatorUid").getValue().toString());
                        intent.putExtra("endOfSurveyTime",dataSnapshot.child("endOfSurveyTime").getValue().toString());
                        intent.putExtra("numberOfOption",dataSnapshot.child("numberOfOption").getValue().toString());
                        intent.putExtra("option1",dataSnapshot.child("option1").getValue().toString());
                        intent.putExtra("option2",dataSnapshot.child("option2").getValue().toString());
                        intent.putExtra("suggest",dataSnapshot.child("suggest").getValue().toString());
                        intent.putExtra("topic",dataSnapshot.child("topic").getValue().toString());
                        intent.putExtra("surveyUUID",dataSnapshot.getKey());
                        intent.putExtra("userDispalyname",mySharedPreferences
                                .getString("displayname","未具名"));
                        intent.putExtra("showID",dataSnapshot.child("showID").getValue().toString());
                        intent.putExtra("creatorDisplayname",dataSnapshot.child("creatorDisplayname").getValue().toString());
                        String numberOfOption=dataSnapshot.child("numberOfOption").getValue().toString();
                        switch (Integer.parseInt(numberOfOption)){
                            case 5:
                                intent.putExtra("option5",dataSnapshot.child("option5").getValue().toString());
                            case 4:
                                intent.putExtra("option4",dataSnapshot.child("option4").getValue().toString());
                            case 3:
                                intent.putExtra("option3",dataSnapshot.child("option3").getValue().toString());
                        }
                        //檢查是否過期
                        long now=(new Date()).getTime();
                        long end=Long.parseLong(dataSnapshot.child("endOfSurveyTime").getValue().toString());
                        if(now>end){
                            intent.putExtra("EOSFlag","true");
                        }else{
                            intent.putExtra("EOSFlag","flase");
                        }


                        startActivity(intent);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                databaseReference.addListenerForSingleValueEvent(surveyListener);
            }
        });

        mySurveys.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String surveyID=((MySurveyListItem)parent.getItemAtPosition(position)).surveyUUID;
                Intent instent= new Intent(StartActivity.this,MonitorMySurvey.class);
                instent.putExtra("surveyUUID",surveyID);
                startActivity(instent);
            }
        });




        //解決 Enter 問題
        View.OnKeyListener keyboardListener = new View.OnKeyListener()
        {
            /**
             * This listens for the user to press the enter button on
             * the keyboard and then hides the virtual keyboard
             */
            public boolean onKey(View arg0, int arg1, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ( (event.getAction() == KeyEvent.ACTION_DOWN  ) &&
                        (arg1 == KeyEvent.KEYCODE_ENTER)   )
                {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    onClickCheckSurveyCode(surveyCodeET);
                    return true;
                }
                return false;
            }
        } ;
        surveyCodeET.setOnKeyListener(keyboardListener);


        //設定 選項數量 Spinner 依選項數量出現
        numberOfQ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numberOfOption=position+2;
                LinearLayout linearLayout;
                linearLayout=(LinearLayout)findViewById(R.id.option5ct);
                linearLayout.setVisibility(View.GONE);
                linearLayout=(LinearLayout)findViewById(R.id.option4ct);
                linearLayout.setVisibility(View.GONE);
                linearLayout=(LinearLayout)findViewById(R.id.option3ct);
                linearLayout.setVisibility(View.GONE);
                switch (position) {
                    case 3:
                        linearLayout=(LinearLayout)findViewById(R.id.option5ct);
                        linearLayout.setVisibility(View.VISIBLE);
                    case 2:
                        linearLayout=(LinearLayout)findViewById(R.id.option4ct);
                        linearLayout.setVisibility(View.VISIBLE);
                    case 1:
                        linearLayout=(LinearLayout)findViewById(R.id.option3ct);
                        linearLayout.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        surveyAvailableTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                surveyAvailableTimeString=parent.getSelectedItem().toString();
                switch (position) {
                    case 0:
                        surveyAvailableTimeLong = 3_600_000;
                        break;
                    case 1:
                        surveyAvailableTimeLong = 3_600_000*4;
                        break;
                    case 2:
                        surveyAvailableTimeLong = 3_600_000*24;
                        break;
                    case 3:
                        surveyAvailableTimeLong = 3_600_000*24*7;
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });








        //設定 顯示初始值
        if(currentUser.isEmailVerified()){
            dispplayEmailAndStatusTV.setText("帳號："+currentUser.getEmail()+cr
                    +"己完成電字郵件驗證");
            requestEmailVerification.setVisibility(View.GONE);
        }else {
            dispplayEmailAndStatusTV.setText("帳號："+currentUser.getEmail()+cr
                    +"尚未完成電子郵件驗證，建議進行驗證，以利抽獎活動作業");
        }

        //更新使用者設定
        updateUserData();

        //設定 TabView
        TabHost tabHost=(TabHost)findViewById(R.id.startTab);
        tabHost.setup();
        TabHost.TabSpec tabSpec;
        tabSpec =tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("我要填問卷");
        tabHost.addTab(tabSpec);
        tabSpec =tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("建立的問卷");
        tabHost.addTab(tabSpec);
        tabSpec =tabHost.newTabSpec("tag3");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator("個人資料");
        tabHost.addTab(tabSpec);
        tabHost.setCurrentTab(0);

        updateMySurveys();
        checkSurveyCode("10000");
    }


    //建立新題目

    public void newSurvey(View view){

        String optionString5="",optionString4="",
                optionString3="",optionString2="",optionString1="",
                topicString="",suveryUUID;
        long createTime,endOfSurveyTime;
        boolean allowAnm=false,suggest=true;
        int showID;
        Map<String,String> surveyData=new HashMap<>();

        allowAnm=allowAnonymity.isChecked();
        suggest=suggestCB.isChecked();


        //確認輸入選項
        topicString=topic.getText().toString();
        if(topicString.equals("")){
            showMessage("主題不得空白, 請輸入題問主題");
            topic.requestFocus();
            return;
        }
        surveyData.put("topic",topicString);
        switch (numberOfOption){
            case 5:
                optionString5=option5.getText().toString();
                if(optionString5.equals("")){
                    showMessage("選項不得空白, 請輸入選項內容");
                    option5.requestFocus();
                    return;
                }
            case 4:
                optionString4=option4.getText().toString();
                if(optionString4.equals("")){
                    showMessage("選項不得空白, 請輸入選項內容");
                    return;
                }
            case 3:
                optionString3=option3.getText().toString();
                if(optionString3.equals("")){
                    showMessage("選項不得空白, 請輸入選項內容");
                    option3.requestFocus();
                    return;
                }

            default:
                optionString2=option2.getText().toString();
                optionString1=option1.getText().toString();

                if(optionString1.equals("")){
                    showMessage("選項不得空白, 請輸入選項內容");
                    option1.requestFocus();
                    return;
                }

                if(optionString2.equals("")){
                    showMessage("選項不得空白, 請輸入選項內容");
                    option2.requestFocus();
                    return;
                }

        }
        //處理問題

        //抓目前時間
        Date date=new Date();
        createTime =date.getTime();
        Log.d("過期比對0",createTime+"");
        endOfSurveyTime=createTime+surveyAvailableTimeLong;


        //產生 Show ID
        Random random=new Random();
        showID=random.nextInt(88999)+11000;

        //抓取使用者名稱
        String createrName=mySharedPreferences.getString("displayname","未具名");
        //若為管理者 ID設為 10000 並加上一周的有效期60*60*1000*24*7=604,800,000
        if(createrName.equals(administratorName)) {
            showID=10000;
            endOfSurveyTime=createTime+604_800_000;

        }


        //產生 UUID
        suveryUUID=UUID.randomUUID().toString();

        //產生 調查表
        switch (numberOfOption) {
            case 5:
                surveyData.put("option5", optionString5);
            case 4:
                surveyData.put("option4", optionString4);
            case 3:
                surveyData.put("option3", optionString3);
        }
        surveyData.put("allowAnonymity",allowAnm+"");
        surveyData.put("numberOfOption",numberOfOption+"");
        surveyData.put("option2",optionString2);
        surveyData.put("option1",optionString1);
        surveyData.put("createTime",createTime+"");
        surveyData.put("endOfSurveyTime",endOfSurveyTime+"");
        surveyData.put("showID",showID+"");
        surveyData.put("creatorUid",FirebaseAuth.getInstance().getCurrentUser().getUid());
        surveyData.put("suggest",suggest+"");
        surveyData.put("creatorDisplayname",createrName);


        //寫入問題單
        DatabaseReference reference=getDatabaseReference("survey");
        reference.child(suveryUUID).setValue(surveyData);

        Map<String,String> showIDData = new HashMap<>();
        showIDData.put("endOfSurveyTime",endOfSurveyTime+"");
        showIDData.put("topic",topicString);
        showIDData.put("creatorDisplayname",mySharedPreferences.getString("displayname","未具名"));
        reference.child("idlist").child(showID+"").child(suveryUUID).setValue(showIDData);

        //寫入使用者自建資料
        reference =getDatabaseReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("mySurvey").child(suveryUUID);
        reference.child("endOfSurveyTime").setValue(endOfSurveyTime+"");
        reference.child("topic").setValue(topicString);
        reference.child("showID").setValue(showID+"");
        ((TextView)findViewById(R.id.lastID)).setText(showID+"");


        //更新自製項目
        updateMySurveys();

        //完成通知
        AlertDialog.Builder builder= new AlertDialog.Builder(StartActivity.this);
        builder.setTitle("完成，ID："+showID);
        builder.setMessage("請將 ID 提供給參與投票的人，"+surveyAvailableTimeString+"內有效");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchToMySurveyList((Button)findViewById(R.id.switchToMySurveyList));
            }
        });
        AlertDialog alert=builder.create();
        alert.show();

    }
    private void showMessage(String string){
        Toast.makeText(StartActivity.this,string,Toast.LENGTH_LONG).show();
    }


    //查詢項目

    public void onClickCheckSurveyCode(View view){
        String code=surveyCodeET.getText().toString();

        if(code.equals("")){
            showMessage("代碼不得空白！");
            return;
        }
        checkSurveyCode(code);
    }
    public void onClickCheckSystemSurveyCode(View v){
        checkSurveyCode("10000");
    }


    public void checkSurveyCode(String code){

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                surveyItems.clear();

                if(dataSnapshot.getChildrenCount()==0){
                    showMessage("目前查無資料，感謝你的參與！\n");

                }else {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        long endDate = Long.valueOf(item.child("endOfSurveyTime")
                                .getValue().toString()).longValue();
                        long nowDate = (new Date()).getTime();

                        SurveyListItem surveyListItem=new SurveyListItem();
                        surveyListItem.topic=item.child("topic").getValue().toString();
                        surveyListItem.surveyID=item.getKey();
                        surveyListItem.creatorDisplayName=item.child("creatorDisplayname").getValue().toString();
                        surveyListItem.endOfSurveyTime=endDate;
                        surveyItems.add(surveyListItem);
                        //只取未過期的項目
                        if(nowDate>endDate) {

//                            Date date=new Date(endDate);
//                            java.text.DateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
//                            showMessage("調查項目："+item.child("topic").getValue().toString()
//                                    +"\n己於"+dateFormat.format(date)
//                                    +"結束調查，\n" + "感謝你的參與。");

                            //超過一周即刪除 24*60*60*1000*7 ＝60480000
                            if(nowDate>(endDate+(604_800_000))) {
                                DatabaseReference databaseReference =
                                        getDatabaseReference("survey");
                                databaseReference
                                        .child("idlist")
                                        .child(dataSnapshot.getKey().toString())
                                        .child(item.getKey().toString()).removeValue();
                            }
                        }
                    }

                    SurveyListAdapter surveyListAdapter=new SurveyListAdapter(StartActivity.this,surveyItems);
                    surveyList.setAdapter(surveyListAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                showMessage("無法查詢，原因：\n"+
                databaseError.getMessage());
            }
        };
        DatabaseReference reference=getDatabaseReference("survey");
        reference.child("idlist").child(code).addListenerForSingleValueEvent(valueEventListener);


    }



    public void requestEmailVerification(View view){

        if(currentUser.isEmailVerified()){
            dispplayEmailAndStatusTV.setText("帳號："+currentUser.getEmail()+"己經驗證");
            requestEmailVerification.setVisibility(View.GONE);
        }else {
            currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(!task.isSuccessful()) {
                        try {
                            task.getException();
                        } catch (Exception e) {
                            showMessage(e.getLocalizedMessage());
                        }
                    }else {
                        requestEmailVerification.setText("重發驗證郵件");
                        showMessage("己發出驗證郵件，請檢查你的電子郵件後更新狀態" + cr +
                                "請注意：驗證完成後重新登入才會生效");
                    }
                }
            });
        }

    }


    public void modifyDisplayName(View view){

        Button button=(Button)view;
        if (button.getText().equals("變更")){
            modifyDisplaynameTV.setVisibility(View.GONE);
            modifyDisplayNameET.setVisibility(View.VISIBLE);
            button.setText("確認");
        }else{
            final String newDisplayname=modifyDisplayNameET.getText().toString();

            if(newDisplayname.equals("")){
                showMessage("顯示名稱不得空白");
                modifyDisplayNameET.requestFocus();
                return;
            }
            DatabaseReference databaseReference=getDatabaseReference("user");
            databaseReference.child(currentUser.getUid())
                    .child("displayname")
                    .setValue(newDisplayname);
            modifyDisplaynameTV.setText(newDisplayname);
            myEditor.putString("displayname",newDisplayname).commit();

            modifyDisplaynameTV.setVisibility(View.VISIBLE);
            modifyDisplayNameET.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            button.setText("變更");
        }
    }


    @Override
    public void onItemClick(AdapterView<?>adapterView,View v, int pos, long id){


    }


    public void logout(View v){
        FirebaseAuth.getInstance().signOut();
        this.finish();
    }

    public DatabaseReference getDatabaseReference(String source){
        DatabaseReference databaseReference=database.getReference(source);

        return databaseReference;
    }


    @IgnoreExtraProperties
    private static class SurveryUser {
        public String Uid;
        public String email;
        public String displayname;
        public Map<String,String> extras=new HashMap<String, String>();

        public SurveryUser(){

        }

        public SurveryUser(String theEmail, String theDisplayname, String theUid){
            this.Uid=theUid;
            this.displayname=theDisplayname;
            this.email=theEmail;
        }

        public SurveryUser(String theEmail, String theDisplayname, String theUid, Map<String,String> theExtras){
            this.Uid=theUid;
            this.displayname=theDisplayname;
            this.email=theEmail;
            this.extras=theExtras;
        }


    }

    @IgnoreExtraProperties
    private static class SurveyItem{
        public String sbject;

        public SurveyItem(){

        };
        public SurveyItem(String subject){
            this.sbject=subject;
        }

    }



    //由FirebaseDB 取得與更新使用者資料
    private void updateUserData(){
        ValueEventListener displaynameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String theDisplayname=dataSnapshot.child("displayname").getValue().toString();
                myEditor.putString("displayname",theDisplayname).commit();
                modifyDisplayNameET.setText(theDisplayname);
                modifyDisplaynameTV.setText(theDisplayname);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                showMessage("無法取得使用稱，原因：\n"+databaseError.getMessage());
            }
        };
        DatabaseReference ref = getDatabaseReference("user").child(currentUser.getUid());
        ref.addListenerForSingleValueEvent(displaynameListener);


    }

    //調查清單項目物件
    public class SurveyListItem{
        public String creatorDisplayName;
        public long endOfSurveyTime;
        public String topic;
        public String surveyID;

    }

    //我建立的調查清單項目物件
    public class MySurveyListItem{
        public long endOfSurveyTime;
        public String topic;
        public String showID;
        public String surveyUUID;
    }

    private void updateMySurveys(){

        mySurveyItems.clear();

        List<String> mySurveyIDs=new ArrayList<>();

        DatabaseReference ref=getDatabaseReference("user")
                .child(currentUser.getUid())
                .child("mySurvey");


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0) {
                    for (DataSnapshot item : dataSnapshot.getChildren()
                            ) {
                        MySurveyListItem mySurverListItem = new MySurveyListItem();
                        mySurverListItem.topic = item.child("topic").getValue().toString();
                        long endOfSurveyTime = Long.parseLong(
                                item.child("endOfSurveyTime").getValue().toString());
                        mySurverListItem.endOfSurveyTime = endOfSurveyTime;
                        mySurverListItem.showID = item.child("showID").getValue().toString();
                        mySurverListItem.surveyUUID=item.getKey();
                        mySurveyItems.add(mySurverListItem);
                    }
                    MySurveyListAdapater mySurveryListAdapter =
                            new MySurveyListAdapater(StartActivity.this, mySurveyItems);
                    mySurveys.setAdapter(mySurveryListAdapter);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        ref.addListenerForSingleValueEvent(valueEventListener);
//
//


    }

    public void switchToMySurveyList(View v){
        ((Button)findViewById(R.id.switchToMySurveyList)).setTextSize(18);
        ((Button)findViewById(R.id.switchToNewSurveyEdit)).setTextSize(14);
        findViewById(R.id.createNewSurveyLayout).setVisibility(View.GONE);
        findViewById(R.id.mySurveysList).setVisibility(View.VISIBLE);
        updateMySurveys();
    }
    public void switchToNewSurveyEdit(View v){
        ((Button)findViewById(R.id.switchToMySurveyList)).setTextSize(14);
        ((Button)findViewById(R.id.switchToNewSurveyEdit)).setTextSize(18);
        findViewById(R.id.createNewSurveyLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.mySurveysList).setVisibility(View.GONE);
    }
    public void onClickCheckMyGroup(View v){
        showMessage("尚未完成此功能，敬請期待");
    }

    public void onClickCheckMyAnswers(View v){

        mySurveyItems.clear();
        List<String> mySurveyIDs=new ArrayList<>();
        DatabaseReference ref=getDatabaseReference("user")
                .child(currentUser.getUid())
                .child("answers");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                surveyItems.clear();
                if (dataSnapshot.getChildrenCount() == 0) {
                    showMessage("您尚未參與任何活動！\n");
                } else {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        long endDate = Long.valueOf(item.child("endOfSurveyTime")
                                .getValue().toString()).longValue();
                        long nowDate = (new Date()).getTime();

                        SurveyListItem surveyListItem = new SurveyListItem();
                        surveyListItem.topic = item.child("topic").getValue().toString();
                        surveyListItem.surveyID = item.getKey();
                        surveyListItem.creatorDisplayName = item.child("creatorDisplayname").getValue().toString();
                        surveyListItem.endOfSurveyTime = endDate;
                        surveyItems.add(surveyListItem);
                        //只取未過期的項目
                        if (nowDate > endDate) {

//                            Date date=new Date(endDate);
//                            java.text.DateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
//                            showMessage("調查項目："+item.child("topic").getValue().toString()
//                                    +"\n己於"+dateFormat.format(date)
//                                    +"結束調查，\n" + "感謝你的參與。");

                            //超過一周即刪除 24*60*60*1000*7 ＝60480000
                            if (nowDate > (endDate + (604_800_000))) {
                                DatabaseReference databaseReference =
                                        getDatabaseReference("survey");
                                databaseReference
                                        .child("idlist")
                                        .child(dataSnapshot.getKey().toString())
                                        .child(item.getKey().toString()).removeValue();
                            }

                        }

                    }
                }
                SurveyListAdapter surveyListAdapter=new SurveyListAdapter(StartActivity.this,surveyItems);
                surveyList.setAdapter(surveyListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
    }
}

