package com.evolet.myapplication.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.evolet.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;


public class LoginActivity extends Activity {

    private FirebaseAuth mAuth;
    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn;
    private TextView forgotPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        loginEmailText = findViewById(R.id.username);
        loginPassText = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginButton);
        forgotPassword=findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPwd = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(forgotPwd);
            }
        });



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final String loginEmail=loginEmailText.getText().toString();
                final String loginPass =loginPassText.getText().toString();
                if (!TextUtils.isEmpty(loginEmail)&&!TextUtils.isEmpty(loginPass)){

                    final ProgressDialog progresRing = ProgressDialog.show(LoginActivity.this, "Love and Care Admin", "Logging in...", true);
                    progresRing.setCancelable(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass)
                            .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete( Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        progresRing.dismiss();
                                        sendToMain();



                                    } else {
                                        progresRing.dismiss();
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(LoginActivity.this, "Invalid UserName or Password",Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                            } catch (Exception e) {

                            }

                        }
                    }).start();
                }else{
                    Toast.makeText(LoginActivity.this,"Enter all the fields",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void sendToMain(){
        Intent mainIntent=new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(mainIntent);
        finish();

    }
}


