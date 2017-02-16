package idv.shenrunwu.instsuv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CreatAccount extends AppCompatActivity {
    TextView newDisplayNameTV, newAccountTV, newPasswordTV, newRePasswordTV;

    private static final String TAG = "EmailPassword";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_account);
        newDisplayNameTV = (TextView) findViewById(R.id.newDisplyNameIn);
        newAccountTV = (TextView) findViewById(R.id.newAccountIn);
        newPasswordTV = (TextView) findViewById(R.id.newPasswordIn);
        newRePasswordTV = (TextView) findViewById(R.id.newRePasswordIn);
        Intent intent = getIntent();
        newAccountTV.setText(intent.getExtras().get("Account").toString());


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public void creatAccount(View view) {
        final String displayname, accout, password, repassword;
        displayname = newDisplayNameTV.getText().toString();
        accout = newAccountTV.getText().toString();
        password = newPasswordTV.getText().toString();
        repassword = newRePasswordTV.getText().toString();


        if (displayname.equals("")) {
            Toast.makeText(this, "顯示名稱不得空白", Toast.LENGTH_LONG).show();
            newDisplayNameTV.requestFocus();
            return;
        }


        if (accout.equals("")) {
            Toast.makeText(this, "帳號（電子郵件）不得空白", Toast.LENGTH_LONG).show();
            newAccountTV.requestFocus();
            return;
        }

        if (!accout.matches("^\\w+\\.*\\w+@(\\w+\\.){1,5}[a-zA-Z]{2,3}$")) {
            Toast.makeText(this, "帳號（電子郵件) 格式不符", Toast.LENGTH_LONG).show();
            newAccountTV.requestFocus();
            return;
        }


        if (password.length() < 6) {
            Toast.makeText(this, "密碼不得空白或小於六碼", Toast.LENGTH_LONG).show();
            newPasswordTV.requestFocus();
            return;
        }

        if (!password.equals(repassword)) {
            Toast.makeText(this, "確認密碼不一致，請重新輸入", Toast.LENGTH_LONG).show();
            newRePasswordTV.requestFocus();
            return;
        }



        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(accout, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            String errorMessage;
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                errorMessage = e.getMessage();
                            }
                            if (errorMessage.equals("The email address is already in use by another account.")) {
                                errorMessage = "己有人使用 " + accout + " 建立帳號，請使用舊密碼登入，或使用忘記密碼功能找回密碼";
                            }
                            Toast.makeText(CreatAccount.this,
                                    "無法建立帳號，錯誤訊息如下:\n" + errorMessage, Toast.LENGTH_LONG).show();

                            return;
                        }
                        FirebaseUser user = mAuth.getCurrentUser();

                        //設定資料庫基本資料
                        Map<String, String> newUser = new HashMap<String, String>();
                        newUser.put("Uid", user.getUid());
                        newUser.put("displayname", displayname);
                        newUser.put("email", accout);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user");
                        ref.child(user.getUid()).setValue(newUser);

                        CreatAccount.this.finish();
                    }
                });


    }



    public void cancle(View view) {
        this.finish();
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("CreatAccount Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
