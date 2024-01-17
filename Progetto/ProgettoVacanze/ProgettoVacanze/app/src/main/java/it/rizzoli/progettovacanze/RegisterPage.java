package it.rizzoli.progettovacanze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import it.rizzoli.progettovacanze.databinding.ActivityRegisterPageBinding;

public class RegisterPage extends AppCompatActivity {
    protected ActivityRegisterPageBinding activityRegisterPageBinding;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        mAuth = FirebaseAuth.getInstance();
        activityRegisterPageBinding = DataBindingUtil.setContentView(this, R.layout.activity_register_page);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.facolta_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityRegisterPageBinding.facoltaSpinner.setAdapter(adapter);

        activityRegisterPageBinding.cancelButton.setOnClickListener(v -> {
            finish();
        });

        activityRegisterPageBinding.registerButton.setOnClickListener(v -> {
            if(String.valueOf(activityRegisterPageBinding.emailEditText.getText()).equals("") ||
                    String.valueOf(activityRegisterPageBinding.passwordEditText.getText()).equals("")){
                Toast.makeText(this, "Attenzione: assicurarsi di aver inserito email e password", Toast.LENGTH_SHORT).show();
            }else if (!String.valueOf(activityRegisterPageBinding.passwordEditText.getText())
                    .equals(String.valueOf(activityRegisterPageBinding.passwordConfEditText.getText()))){
                Toast.makeText(this, "Attenzione: le due password non corrispondono ", Toast.LENGTH_SHORT).show();
            }else{
                mAuth.createUserWithEmailAndPassword(String.valueOf(activityRegisterPageBinding.emailEditText.getText()),
                                String.valueOf(activityRegisterPageBinding.passwordEditText.getText()))
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Map<String, String> userInfo = new HashMap<>();
                                    userInfo.put("uidUtenti", user.getUid());
                                    userInfo.put("facolta", activityRegisterPageBinding.facoltaSpinner.getSelectedItem().toString());
                                    db.collection("facoltaUtenti")
                                            .add(userInfo)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Intent itn = new Intent(RegisterPage.this, HomePage.class);
                                                    startActivity(itn);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(RegisterPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(RegisterPage.this, "Attenzione: qualcosa è andato storto durante la registrazione", Toast.LENGTH_SHORT).show();
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
            Intent itn = new Intent(RegisterPage.this, HomePage.class);
            startActivity(itn);
        }
    }
}