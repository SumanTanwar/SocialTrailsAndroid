    package com.example.socialtrailsapp;

    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;

    import com.bumptech.glide.Glide;
    import com.bumptech.glide.load.resource.bitmap.CircleCrop;

    import com.example.socialtrailsapp.Interface.OperationCallback;
    import com.example.socialtrailsapp.Utility.SessionManager;
    import com.example.socialtrailsapp.Utility.UserService;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.auth.UserProfileChangeRequest;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;

    public class EditProfileActivity extends BottomMenuActivity {

        EditText nameEdit, emailEdit, bioEdit;
        ImageView profile;
        TextView addProfilePicture ;
        Button saveBtn;
        FirebaseAuth mAuth;
        DatabaseReference mDatabase;
        private SessionManager sessionManager;

        private static final int PICK_IMAGE_REQUEST = 1;
        private Uri imageUri;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getLayoutInflater().inflate(R.layout.activity_edit_profile, findViewById(R.id.container));

            nameEdit = findViewById(R.id.nameEdit);
            emailEdit = findViewById(R.id.emailEdit);
            bioEdit = findViewById(R.id.bioEdit);
            addProfilePicture = findViewById(R.id.addProfilePicture);
            saveBtn = findViewById(R.id.saveBtn);
            profile = findViewById(R.id.profileImageView);

            sessionManager = SessionManager.getInstance(this);
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();


            if (sessionManager.userLoggedIn()) {
                nameEdit.setText(sessionManager.getUsername() != null ? sessionManager.getUsername() : "");
                emailEdit.setText(sessionManager.getEmail() != null ? sessionManager.getEmail() : "");
                bioEdit.setText(sessionManager.getBio() != null ? sessionManager.getBio() : "");

                // Convert String to Uri
                String profileImageUriString = sessionManager.getProfileImage();
                if (profileImageUriString != null) {
                    Uri profileImageUri = Uri.parse(profileImageUriString); // Convert String to Uri
                    Glide.with(this)
                            .load(profileImageUri)
                            .transform(new CircleCrop())
                            .into(profile);
                } else {
                    profile.setImageResource(R.drawable.user); // Set a placeholder image
                }
            }


            addProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFileChooser();
                }
            });


            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        updateProfile(currentUser);
                    }
                }
            });
        }

            private void openFileChooser() {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }

            @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                    imageUri = data.getData(); // Get the image URI


                    ImageView profileImageView = findViewById(R.id.profileImageView);
                  //  profileImageView.setImageURI(imageUri);

                    Glide.with(this)
                            .load(imageUri)
                            .transform(new CircleCrop())
                            .into(profileImageView);


                }
            }


        private void updateProfile(FirebaseUser user) {
            String newName = nameEdit.getText().toString().trim();
            String newBio = bioEdit.getText().toString().trim();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (imageUri != null) {
                                UserService userService = new UserService();
                                userService.uploadProfileImage(user.getUid(), imageUri, new OperationCallback() {
                                    @Override
                                    public void onSuccess() {
                                        // Now update user data in the database
                                        updateUserDataInDatabase(newName, newBio);
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        Toast.makeText(EditProfileActivity.this, "Image upload failed: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // If no image was selected, just update the user data
                                updateUserDataInDatabase(newName, newBio);
                            }
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Profile update failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        private void updateUserDataInDatabase(String name, String bio) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                mDatabase.child("users").child(userId).child("username").setValue(name);
                mDatabase.child("users").child(userId).child("bio").setValue(bio);

                SessionManager sessionManager = SessionManager.getInstance(this);
                sessionManager.updateUserInfo(name, bio);

                Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra("name", name);
                intent.putExtra("bio", bio);
                if (imageUri != null) {
                    intent.putExtra("profileImage", imageUri.toString()); // Convert Uri to String
                }

                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
