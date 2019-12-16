package papb.project.hisam.five_baru;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.liuguangqiang.swipeback.SwipeBackActivity;
import com.squareup.picasso.Picasso;

public class AddEvent extends AppCompatActivity {

    private EditText inputEventName, inputEventDate, inputEventDesc;
    private Button btnUploadPoster, btnEventPlace, btnTambah;
    private ProgressBar mProgressBar;
    private int PLACE_PICKER_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 1;

    private static final String TAG="";
    private StorageTask mUploadTask;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private Uri mImageUri;
    private Place mPlace;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        mStorageRef = FirebaseStorage.getInstance().getReference("EVENTS");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("EVENTS");

        inputEventName = (EditText) findViewById(R.id.edit_text_event_name);
        inputEventDate = (EditText) findViewById(R.id.edit_text_event_date);
        inputEventDesc = (EditText) findViewById(R.id.edit_text_event_desc);
        btnUploadPoster = (Button) findViewById(R.id.button_choose_image);
        btnTambah = (Button) findViewById(R.id.button_tambah);
        btnEventPlace = (Button) findViewById(R.id.button_choose_place);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // get data location
        btnEventPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // membuat Intent untuk Place Picker
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    //menjalankan place picker
                    startActivityForResult(builder.build(AddEvent.this), PLACE_PICKER_REQUEST);

                    // check apabila <a title="Solusi Tidak Bisa Download Google Play Services di Android" href="http://www.twoh.co/2014/11/solusi-tidak-bisa-download-google-play-services-di-android/" target="_blank">Google Play Services tidak terinstall</a> di HP
                } catch (GooglePlayServicesRepairableException e) {
                    Log.d(TAG, "error disini 1");
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.d(TAG, "error disini 2");
                    e.printStackTrace();
                }
            }

        });

        // upload foto/poster on click
        btnUploadPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(); //ambil foto/poster
            }
        });

        //Tambah Event On click
        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUploadTask != null && mUploadTask.isInProgress()){
                    Toast.makeText(AddEvent.this, "In Progress", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(AddEvent.this, MainActivity.class));
                } else {
                    tambahEvent();
                }
            }
        });


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    //method pilih foto/poster
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
        }
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
               mPlace = PlacePicker.getPlace(data, this);
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void tambahEvent() {
        if(mImageUri != null){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Snackbar.make(findViewById(R.id.button_tambah), "Tambah event berhasil", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(AddEvent.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).show();


                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            final String sdownload_url = String.valueOf(downloadUrl);

                            //push data ke Database
                            Event event = new Event(
                                    inputEventName.getText().toString(),
                                    mPlace.getAddress().toString(),
                                    sdownload_url,
                                    inputEventDate.getText().toString(),
                                    inputEventDesc.getText().toString(),
                                    mPlace.getLatLng().longitude,
                                    mPlace.getLatLng().latitude);
                            String eventId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(eventId).setValue(event);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddEvent.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
