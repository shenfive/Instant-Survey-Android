package idv.shenrunwu.instsuv;



import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shenrunwu on 2016/11/14.
 */

public class Fill_In_Survey extends AppCompatActivity {
    Button option1,option2,option3,option4,option5,monitorResult,fillInSurveySubmit;
    EditText suggetion;
    EditText alias;
    CheckBox fillInSuveyOptionAnonymity;
    TextView fillInSuveyTopic,fillInSuveyOptionSelected,fillInSuveyCreatorName;
    LinearLayout suggestLayout,anonymityLayout;
    String selectedID;
    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor myEditor;
    Bundle myExtra;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fillinsurvey);


//        UI物件設定
        option1=(Button)findViewById(R.id.fillInSuveyOption1);
        option2=(Button)findViewById(R.id.fillInSuveyOption2);
        option3=(Button)findViewById(R.id.fillInSuveyOption3);
        option4=(Button)findViewById(R.id.fillInSuveyOption4);
        option5=(Button)findViewById(R.id.fillInSuveyOption5);
        monitorResult=(Button)findViewById(R.id.monitorResult);
        fillInSurveySubmit=(Button)findViewById(R.id.fillInSurveySubmit);
        suggetion=(EditText)findViewById(R.id.fillInSuveyOptionSuggestion);
        alias=(EditText)findViewById(R.id.fillInSuveyOptionAlias);
        fillInSuveyTopic=(TextView)findViewById(R.id.fillInSuveyTopic);
        fillInSuveyCreatorName=(TextView)findViewById(R.id.fillInSuveyCreatorName);
        fillInSuveyOptionSelected=(TextView) findViewById(R.id.fillInSuveyOptionSelected);
        fillInSuveyOptionAnonymity=(CheckBox)findViewById(R.id.fillInSuveyOptionAnonymity);
        suggestLayout=(LinearLayout)findViewById(R.id.fillInSuggestLayout);
        anonymityLayout=(LinearLayout)findViewById(R.id.fillInAnonymityLayout);


//      初始化資料
        mySharedPreferences = getApplicationContext().getSharedPreferences(getResources()
                .getString(R.string.setting_pre),0);
        myEditor=mySharedPreferences.edit();

        selectedID="0";//表示尚未選擇

        myExtra=getIntent().getExtras();

        fillInSuveyTopic.setText("題目："+myExtra.get("topic").toString());
        fillInSuveyCreatorName.setText("問卷建立者："+myExtra.get("creatorDisplayname").toString());
        option1.setText(myExtra.get("option1").toString());
        option2.setText(myExtra.get("option2").toString());

        switch (Integer.parseInt(myExtra.get("numberOfOption").toString())){
            case 5:
                option5.setText(myExtra.get("option5").toString());
                option5.setVisibility(View.VISIBLE);
            case 4:
                option4.setText(myExtra.get("option4").toString());
                option4.setVisibility(View.VISIBLE);
            case 3:
                option3.setText(myExtra.get("option3").toString());
                option3.setVisibility(View.VISIBLE);
        }

        if(myExtra.get("suggest").equals("true")){
            suggestLayout.setVisibility(View.VISIBLE);
        }

        if(myExtra.get("allowAnonymity").equals("true")){
            anonymityLayout.setVisibility(View.VISIBLE);
        }
        checkIfAnswerd();
        if(myExtra.get("EOSFlag").equals("true")){
            fillInSuveyTopic.setText("題目："+myExtra.get("topic").toString()+"\n＝＝己結束本調查＝＝");
            fillInSuveyTopic.setBackgroundColor(Color.YELLOW);
            option1.setEnabled(false);
            option2.setEnabled(false);
            option3.setEnabled(false);
            option4.setEnabled(false);
            option5.setEnabled(false);
            fillInSurveySubmit.setVisibility(View.GONE);
        }


    }

    public void changeSelect(View view){
        Button selectedButton=(Button)view;
        selectedID=selectedButton.getTag().toString();
        option1.setTextSize(14f);
        option2.setTextSize(14f);
        option3.setTextSize(14f);
        option4.setTextSize(14f);
        option5.setTextSize(14f);
        selectedButton.setTextSize(20f);
        fillInSuveyOptionSelected.setText("目前選擇："+selectedButton.getText());
    }

    public void cancleSurvey(View view){
        finish();
    }

    public void submit(View view){

        if (selectedID.equals("0")){
            Toast.makeText(Fill_In_Survey.this,"尚未選擇，請選擇你的答案",Toast.LENGTH_SHORT).show();
        } else {
            String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String surveyID = myExtra.get("surveyUUID").toString();
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("survey")
                    .child(surveyID)
                    .child("answers");
            Map<String,String> data= new HashMap<>();

            data.put("displayname",mySharedPreferences.getString("displayname", ""));
            data.put("alias",alias.getText().toString());
            data.put("selectedID",selectedID);
            data.put("suggest",suggetion.getText().toString());

            ref.child(userUID).setValue(data);

            ref=FirebaseDatabase.getInstance()
                    .getReference("user")
                    .child(userUID)
                    .child("answers")
                    .child(surveyID);
            ref.child("topic").setValue(myExtra.get("topic").toString());
            ref.child("select").setValue((fillInSuveyOptionSelected.getText().toString()).substring(5));
            ref.child("endOfSurveyTime").setValue(myExtra.get("endOfSurveyTime").toString());
            ref.child("creatorDisplayname").setValue(myExtra.get("creatorDisplayname").toString());
        }
        Toast.makeText(this,"你的意見己送出，感謝你的參與！",Toast.LENGTH_LONG).show();
        Intent intent=new Intent(Fill_In_Survey.this,MonitorMySurvey.class);
        intent.putExtra("surveyUUID",myExtra.get("surveyUUID").toString());
        startActivity(intent);
        finish();

    }

    private void checkIfAnswerd(){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("survey");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()!=0){
                    //取資料
                    monitorResult.setVisibility(View.VISIBLE);
                    if(myExtra.get("EOSFlag").equals("true")) {

                        Toast.makeText(Fill_In_Survey.this, "你曾經有回答過本調查，目前己停止調查，仍可查看結果, 或取消回上一頁"
                                , Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(Fill_In_Survey.this, "你曾經有回答過本調查，目前尚未停止調查，仍可修改你的選擇"
                                , Toast.LENGTH_LONG).show();

                    }
                    alias.setText(dataSnapshot.child("alias").getValue().toString());
                    suggetion.setText(dataSnapshot.child("suggest").getValue().toString());
                    option1.setTextSize(14f);
                    option2.setTextSize(14f);
                    option3.setTextSize(14f);
                    option4.setTextSize(14f);
                    option5.setTextSize(14f);

                    switch (dataSnapshot.child("selectedID").getValue().toString()){
                        case "1":
                            option1.setTextSize(20f);
                            break;
                        case "2":
                            option2.setTextSize(20f);
                            break;
                        case "3":
                            option3.setTextSize(20f);
                            break;
                        case "4":
                            option4.setTextSize(20f);
                            break;
                        case "5":
                            option5.setTextSize(20f);
                            break;
                    }
                    fillInSurveySubmit.setText("修改");

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.child(myExtra.get("surveyUUID").toString())
                .child("answers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(valueEventListener);

    }
    public void checkResult(View v) {

        Intent intent=new Intent(Fill_In_Survey.this,MonitorMySurvey.class);
        intent.putExtra("surveyUUID",myExtra.get("surveyUUID").toString());
        startActivity(intent);
        finish();
    }


}
