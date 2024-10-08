    package com.example.socialtrailsapp;

    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.text.TextUtils;
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

    import com.example.socialtrailsapp.Interface.DataOperationCallback;
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
        UserService userService;

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

            userService = new UserService();
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
                    Glide.with(this)
                            .load(R.drawable.user) // Replace with your image URI or resource
                            .transform(new CircleCrop())
                            .into(profile);
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
                    updateUserProfile();
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

        private void updateUserProfile()
        {
            String newName = nameEdit.getText().toString().trim();
            String newBio = bioEdit.getText().toString().trim();
            if (TextUtils.isEmpty(newName)) {
                nameEdit.setError("User name is required");
                nameEdit.requestFocus();
                return;
            }
            else
            {
                if (imageUri != null) {
                    userService.uploadProfileImage(sessionManager.getUserID(), imageUri, new DataOperationCallback<String>() {
                        @Override
                        public void onSuccess(String imageurl) {
                            String currentUsername = sessionManager.getUsername();
                            String currentBio = sessionManager.getBio();

                            if(!newName.equals(currentUsername) || !newBio.equals(currentBio))
                            {
                                updateNameandBio(imageurl);
                            }
                            else
                            {
                                sessionManager.updateUserInfo(newName, newBio,imageurl);
                                Intent intent = new Intent(EditProfileActivity.this, ViewProfileActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(EditProfileActivity.this, "Image upload failed: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }else
                {
                    String currentUsername = sessionManager.getUsername();
                    String currentBio = sessionManager.getBio();
                    if(!newName.equals(currentUsername) || !newBio.equals(currentBio))
                    {
                        updateNameandBio(sessionManager.getProfileImage());
                    }
                }
            }
        }
        private void updateNameandBio(String imageurl)
        {

            String newName = nameEdit.getText().toString().trim();
            String newBio = bioEdit.getText().toString().trim();
            userService.updateNameandBio(sessionManager.getUserID(),newBio,newName , new OperationCallback() {
                @Override
                public void onSuccess() {

                    sessionManager.updateUserInfo(newName, newBio,imageurl);
                    Intent intent = new Intent(EditProfileActivity.this, ViewProfileActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(EditProfileActivity.this, "Image upload failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }




    }
