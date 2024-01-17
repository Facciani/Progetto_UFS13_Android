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

import it.rizzoli.progettovacanze.databinding.PageWelcomeBinding;

public class WelcomePage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    protected PageWelcomeBinding pageWelcomeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_welcome);

        pageWelcomeBinding = DataBindingUtil.setContentView(this, R.layout.page_welcome);

        pageWelcomeBinding.buttonLogin.setOnClickListener(v -> {
            Intent itn = new Intent(WelcomePage.this, LoginPage.class);
            startActivity(itn);
        });

        pageWelcomeBinding.buttonRegister.setOnClickListener(v -> {
            Intent itn = new Intent(WelcomePage.this, RegisterPage.class);
            startActivity(itn);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.i("User UID", user.getUid());
            Intent itn = new Intent(WelcomePage.this, HomePage.class);
            startActivity(itn);
        }
    }
}