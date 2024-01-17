package it.rizzoli.progettovacanze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.rizzoli.progettovacanze.databinding.ActivityLoginPageBinding;

public class LoginPage extends AppCompatActivity {

    protected ActivityLoginPageBinding activityLoginPageBinding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        activityLoginPageBinding = DataBindingUtil.setContentView(this, R.layout.activity_login_page);

        mAuth = FirebaseAuth.getInstance();

        activityLoginPageBinding.cancelButton.setOnClickListener(v -> {
            finish();
        });

        activityLoginPageBinding.loginButton.setOnClickListener(v -> {

            if(String.valueOf(activityLoginPageBinding.emailEditText.getText()).equals("") ||
                    String.valueOf(activityLoginPageBinding.passwordEditText.getText()).equals("")){
                Toast.makeText(this, "Attenzione: assicurarsi di aver inserito email e password", Toast.LENGTH_SHORT).show();
            }else{
                mAuth.signInWithEmailAndPassword(String.valueOf(activityLoginPageBinding.emailEditText.getText())
                                , String.valueOf(activityLoginPageBinding.passwordEditText.getText()))
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Log.i("User UID", user.getUid());
                                    Intent itn = new Intent(LoginPage.this, HomePage.class);
                                    startActivity(itn);
                                } else {
                                    Toast.makeText(LoginPage.this, "Attenzione: email o password errati", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.i("User UID", user.getUid());
            Intent itn = new Intent(LoginPage.this, HomePage.class);
            startActivity(itn);
        }
    }
}