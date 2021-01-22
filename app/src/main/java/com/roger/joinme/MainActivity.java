package com.roger.joinme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity {

    private Button login;
    private Button register;
    private Button ForgetPasswordLink;
    public Button loginPageBtn;
    public ImageView activityPhoto;

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    public static String[] docString = new String[1000000];
    public static int count = 0;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String currentUserID;
    private ProgressDialog loadingBar;

    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink ;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private static final String EMAIL = "email";
    private static final String USER_POSTS = "user_posts";
    private static final String AUTH_TYPE = "rerequest";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        initViews();
        initData();
        setListeners();
        count = 0;

            firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        String currentUserID = user.getUid();
                        final Map<String, Object> registerdata = new HashMap<>();
                        registerdata.put("currentUserID",currentUserID);
                        registerdata.put("device_token",deviceToken);
                        registerdata.put("email",user.getEmail());
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

                        SendUserToMainActivity();
                        Toast.makeText(MainActivity.this, "帳號登入成功...", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            db.collection("activity")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    docString[count] = document.getId();
                                    count++;
                                }
                            }
                        }
                    });
        }



    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthListener);
    }


    private void initViews()
    {
        login=(Button)findViewById(R.id.login);
        register=(Button)findViewById(R.id.register);
        ForgetPasswordLink=(Button)findViewById(R.id.forgetpassword);
        UserEmail = (EditText)findViewById(R.id.account);
        UserPassword = (EditText)findViewById(R.id.passwd);
        loadingBar = new ProgressDialog(this);
        loginPageBtn = (Button)findViewById(R.id.loginPage);
        activityPhoto = (ImageView) findViewById(R.id.imageView);
    }

    private void initData()
    {
    }

    private void setListeners()
    {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AllowUserToLogin();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,register.class);
                startActivity(intent);
            }
        });
        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,forgetpwd.class);
                startActivity(intent);
            }
        });
        loginPageBtn.setEnabled(false);

    }

    private void AllowUserToLogin()
    {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("登入中....");
            loadingBar.setMessage("登入中請稍後....");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user.isEmailVerified())
                                {
                                    String currentUserId = firebaseAuth.getCurrentUser().getUid();
                                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                    final Map<String, Object> logindata = new HashMap<>();
                                    logindata.put("device_token",deviceToken);
                                    db.collection("user").document(currentUserId).update(logindata);
                                    SendUserToMainActivity();
                                    Toast.makeText(MainActivity.this, "登入成功...", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();

                                }
                                else
                                {
                                    // email is not verified, so just prompt the message to the user and restart this activity.
                                    // NOTE: don't forget to log out the user.
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(MainActivity.this, "信箱尚未驗證...", Toast.LENGTH_SHORT).show();
                                    //restart this activity

                                }
//                                useraccount=UserEmail.getText().toString();


                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(MainActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(MainActivity.this, home.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
