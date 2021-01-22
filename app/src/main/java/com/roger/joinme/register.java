package com.roger.joinme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;


public class register extends AppCompatActivity {

    private Button back_to_login;
    private EditText UserEmail;
    private EditText UserPassword;
    private EditText UserPasswordVerify;
    public Button loginBtn;
    public Button registerBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        initViews();
        setListeners();
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }


//        memberaccount.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                Log.d("TAG", "Value is: " + value);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w("TAG", "Failed to read value.", error.toException());
//            }
//        });
    }

    private void initViews()
    {
        UserPassword = (EditText)findViewById(R.id.editText_pwd);
        UserEmail = (EditText)findViewById(R.id.editText_email);
        UserPasswordVerify= (EditText)findViewById(R.id.editText_verify);
        back_to_login=(Button)findViewById(R.id.btn_back_to_login);
        loadingBar = new ProgressDialog(this);
        loginBtn = (Button)findViewById(R.id.loginPage);
        registerBtn = (Button)findViewById(R.id.registerPage);
    }

    private void initData()
    {
    }

    private void setListeners()
    {

//        back_to_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(register.this,MainActivity.class);
//                startActivity(intent);
//                register.this.finish();
//            }
//        });
        back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateNewAccount();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent();
                                            intent.setClass(register.this,MainActivity.class);
                                            startActivity(intent);
                                        }
                                    });
        registerBtn.setEnabled(false);
    }
    private void CreateNewAccount()
    {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String passwordverify = UserPasswordVerify.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "請輸入信箱...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "請輸入密碼...", Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(passwordverify)){
            Toast.makeText(this, "確認密碼不符...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("新建帳號");
            loadingBar.setMessage("正在新建帳號請稍後...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                String currentUserID = mAuth.getCurrentUser().getUid();
                                final Map<String, Object> registerdata = new HashMap<>();
                                registerdata.put("currentUserID",currentUserID);
                                registerdata.put("device_token",deviceToken);
                                registerdata.put("email",UserEmail.getText().toString());
                                db.collection("user")
                                        .document(currentUserID)
                                        .set(registerdata)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("TAG", "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("TAG", "Error writing document", e);
                                            }
                                        });


                                Toast.makeText(register.this, "請到信箱認證帳號...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // email sent
                                                    // after email is sent just logout the user and finish this activity
//                                                    FirebaseAuth.getInstance().signOut();
//                                                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
//                                                    finish();
                                                    FirebaseAuth.getInstance().signOut();
                                                    SendUserToLoginActivity();
                                                }
                                                else
                                                {
                                                    // email not sent, so display message and restart the activity or do whatever you wish to do

                                                    //restart this activity
//                                                    overridePendingTransition(0, 0);
//                                                    finish();
//                                                    overridePendingTransition(0, 0);
//                                                    startActivity(getIntent());

                                                }
                                            }
                                        });

                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(register.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }


                        }
                    });
        }
    }




    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(register.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }
}
