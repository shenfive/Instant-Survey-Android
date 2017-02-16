package idv.shenrunwu.instsuv;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private FirebaseAuth mAuth;
    LinearLayout mainLinearLayout;
    TextView messageText;
    Button loginButton, fpButton, newAccount;
    EditText accountET, passwordET, forgetpasswordET;

    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor myEditor;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "EmailPassword";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onResume(){
        super.onResume();
        accountET.setText(mySharedPreferences.getString("lastLoginEmail",""));
        passwordET.requestFocus();
        // put your code here...
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent=new Intent(this,LunchScreen.class);
        startActivity(intent);


        //設定UI段
        accountET = (EditText) findViewById(R.id.inAccount);
        passwordET = (EditText) findViewById(R.id.inPassword);
        forgetpasswordET = (EditText) findViewById(R.id.inForgetPasswordEmail);
        mainLinearLayout = (LinearLayout)findViewById(R.id.activity_main);
        newAccount = (Button) findViewById(R.id.newAccount);
        loginButton = (Button) findViewById(R.id.login);
        fpButton = (Button) findViewById(R.id.fp);
        mySharedPreferences = getApplicationContext().getSharedPreferences(getResources()
                .getString(R.string.setting_pre),0);
        myEditor=mySharedPreferences.edit();


        //設定UIListenner
        loginButton.setOnClickListener(this);
        fpButton.setOnClickListener(this);
        newAccount.setOnClickListener(this);
        mainLinearLayout.setOnClickListener(this);


        //預設值
        accountET.setText(mySharedPreferences.getString("lastLoginEmail",""));
        passwordET.requestFocus();

        //隱狀態列
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        //設定Login Listnner
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in 成功登入
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    passwordET.setText("");
                    myEditor.putString("lastLoginEmail",user.getEmail()).commit();//記錄最後使用者

                    Intent intent = new Intent(MainActivity.this,StartActivity.class);
                    startActivity(intent);
                    NotificationManager notif=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                    Toast.makeText(MainActivity.this,"使用者己登出或尚未登入",Toast.LENGTH_LONG).show();
                }
                // ...
            }
        };

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    // 登入設定段
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login:
                //取得輸入資訊
                String email = accountET.getText().toString();
                String password = passwordET.getText().toString();

                //檢查是否為空白
                if (email.equals("")) {//accountET.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "帳號不可空白",
                            Toast.LENGTH_LONG).show();
                    accountET.requestFocus();
                    break;
                }
                if (password.equals("")) {
                    Toast.makeText(MainActivity.this, "密碼不可空白",
                            Toast.LENGTH_LONG).show();
                    passwordET.requestFocus();
                    break;
                }


                //試圖登入
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                String message;

                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "signInWithEmail", task.getException());
                                    try{
                                        message=task.getException().getLocalizedMessage();
                                    }catch (Exception e){
                                        message=e.getLocalizedMessage();
                                    }
                                    Toast.makeText(MainActivity.this, "無法登入，原因：\n"+message,
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "登入成功",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        });


                break;

            case R.id.fp:
                AlertDialog.Builder myDialogBox = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.forget_password, (ViewGroup) findViewById(R.id.forget_password));
                myDialogBox.setView(layout);
                EditText theEmailET = (EditText) layout.findViewById(R.id.inForgetPasswordEmail);
                theEmailET.setText(accountET.getText().toString());
                myDialogBox.setPositiveButton("送出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                EditText theEmailET = (EditText) layout.findViewById(R.id.inForgetPasswordEmail);
                                String emailAddress = theEmailET.getText().toString();
                                if (emailAddress.equals("")) {//accountET.getText().toString().equals("")){
                                    Toast.makeText(MainActivity.this, "帳號不可空白",
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }

                                auth.sendPasswordResetEmail(emailAddress)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Email sent.");
                                                    Toast.makeText(MainActivity.this, "MailSended",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        }


                );
                myDialogBox.show();
                break;
            case R.id.newAccount:
                Intent intent = new Intent(MainActivity.this, CreatAccount.class);
                intent.putExtra("Account",accountET.getText().toString());
                startActivity(intent);
                break;
        }


    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus)
//    {
//        super.onWindowFocusChanged(hasFocus);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus)
//        {
//            this.getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        }
//    }


    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        mAuth.addAuthStateListener(mAuthListener);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
