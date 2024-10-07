package com.example.socialtrailsapp.adminpanel;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialtrailsapp.ModelData.Users; // Import the Users class
import com.example.socialtrailsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ModeratorlistActivity extends AdminBottomMenuActivity {

    private List<Users> moderators = new ArrayList<>();
    private ModeratorAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        getLayoutInflater().inflate(R.layout.activity_moderatorlist, findViewById(R.id.container));

        recyclerView = findViewById(R.id.recyclerViewModerators);
        adapter = new ModeratorAdapter(this, moderators);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchModerators();
    }

    private void fetchModerators() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                moderators.clear(); // Clear the existing list to avoid duplication
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Users user = userSnapshot.getValue(Users.class);

                    // Check if the user has the "moderator" role
                    if (user != null && user.getRoles() != null && user.getRoles().contains("moderator")) {
                        moderators.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.e("ModeratorActivity", "Failed to read users: " + error.getMessage());
            }
        });
    }
}
