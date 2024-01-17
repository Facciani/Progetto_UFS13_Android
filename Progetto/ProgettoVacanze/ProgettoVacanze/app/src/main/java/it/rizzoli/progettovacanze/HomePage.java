package it.rizzoli.progettovacanze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import it.rizzoli.progettovacanze.databinding.ActivityHomePageBinding;

public class HomePage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    protected ActivityHomePageBinding activityHomePageBinding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String facolta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        facolta = "";
        activityHomePageBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_page);
        mAuth = FirebaseAuth.getInstance();

        activityHomePageBinding.logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            onStart();
        });


        db.collection("facoltaUtenti")
                .whereEqualTo("uidUtenti", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i("Result321", document.getId() + " => " + document.getData());
                                facolta = (String) document.getString("facolta");
                                activityHomePageBinding.facoltaTextView.setText(facolta);
                                applicaFragment("download");
                            }
                        } else {
                            Log.i("Result321", "Error getting documents: ", task.getException());
                        }
                    }
                });

        activityHomePageBinding.bottomNav.setOnItemSelectedListener(item -> {
            applicaFragment(item.getTitle().toString());
            return true;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.i("User UID", user.getUid());
            activityHomePageBinding.welcomeTitle.setText("Welcome " + user.getEmail());
        }else{
            Intent itn = new Intent(HomePage.this, WelcomePage.class);
            startActivity(itn);
        }
    }

    protected void applicaFragment(String title){
        if(title.equals("download")){
            download downloadFragment = download.newInstance("Facolta = ",facolta);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, downloadFragment, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("name")
                    .commit();
        }else{
            upload uploadFragment = upload.newInstance("Facolta = ",facolta);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, uploadFragment, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("name")
                    .commit();
        }
    }


}