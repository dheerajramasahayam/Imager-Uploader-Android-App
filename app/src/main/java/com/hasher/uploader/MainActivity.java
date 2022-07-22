package com.hasher.uploader;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hasher.uploader.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityResultLauncher<String> launcher;

    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    EditText name,categories;
    FirebaseDatabase database;
    Button button, upload;
    ImageView imageview;
    Uri image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseFirestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        name = findViewById(R.id.name);
        categories = findViewById(R.id.category);
        button = findViewById(R.id.button);
        imageview = findViewById(R.id.imageView);
        upload = findViewById(R.id.uploadbutton);


        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                imageview.setImageURI(result);
                image = result;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String rName = name.getText().toString();
                String rCategory = categories.getText().toString();

                if (rName.isEmpty()){
                    name.setError("Enter the name");
                }else if (rCategory.isEmpty()) {
                    categories.setError("Enter the category");
                }else{

                    final StorageReference reference = firebaseStorage.getReference().child(rCategory).child(rName);
                    reference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    database.getReference().child(rCategory).child(rName).setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(MainActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });

                }


            }
        });




    }

}