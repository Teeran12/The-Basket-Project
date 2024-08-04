package com.example.mybasket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybasket.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdminRegister extends AppCompatActivity implements LocationListener {

    private ImageButton backBtn, gpsBtn;
    private ImageView profile_pic;
    private EditText name, shopName, phoneEt, countryEt, stateiv, cityiv, addressET, emailEt, passwordET, confirmPasswordET;
    private Button RegisterBtn;
    private TextView RegisterUserTv;

    //permission constants
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    //permission arrays
    private String[] locationPermission;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    //Image picked url
    private Uri image_uri;

    private double latitude, longitude;

    private LocationManager locationManager;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);


        backBtn = (ImageButton) findViewById(R.id.backBtn);
        gpsBtn= (ImageButton) findViewById(R.id.gpsBtn);
        profile_pic= (ImageView) findViewById(R.id.profile_pic);
        name= (EditText) findViewById(R.id.name);
        shopName= (EditText) findViewById(R.id.shopName);
        phoneEt= (EditText) findViewById(R.id.phoneEt);
        countryEt=(EditText) findViewById(R.id.countryEt);
        stateiv=(EditText) findViewById(R.id.stateiv);
        cityiv=(EditText) findViewById(R.id.cityiv);
        addressET=(EditText) findViewById(R.id.addressET);
        emailEt=(EditText) findViewById(R.id.emailEt);
        passwordET=(EditText) findViewById(R.id.passwordET);
        confirmPasswordET=(EditText) findViewById(R.id.confirmPasswordET);
        RegisterBtn=(Button) findViewById(R.id.RegisterBtn);
        RegisterUserTv= (TextView) findViewById(R.id.RegisterUserTv);


        //init permission array
        locationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog =new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //detect current location
                if (checkLocationPermission()){
                    detectLocation();
                }
                else{
                    //not allowed, request
                    requestLocationPermission();

                }
            }
        });
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick image
                showImagePickDialog();
            }
        });
        RegisterUserTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminRegister.this, CustomerRegister.class));
            }
        });
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //register user
                inputData();
            }
        });
    }

    private String fullName, retailName, phoneNumber, country, state, city, address, email, password, confirmPassword;
    private void inputData() {
        //input data
        fullName = name.getText().toString().trim();
        retailName = shopName.getText().toString().trim();
        phoneNumber = phoneEt.getText().toString().trim();
        country = countryEt.getText().toString().trim();
        state = stateiv.getText().toString().trim();
        city = cityiv.getText().toString().trim();
        address = addressET.getText().toString().trim();
        email = emailEt.getText().toString().trim();
        password = passwordET.getText().toString().trim();
        confirmPassword = confirmPasswordET.getText().toString().trim();
        //validate data
        if (TextUtils.isEmpty(fullName)){
            Toast.makeText(this, "Enter Name...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(retailName)){
            Toast.makeText(this, "Enter Retail Name...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this, "Enter Phone Number...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (latitude == 0.0 || longitude == 0.0){
            Toast.makeText(this, "Please click on GPS button to detect your location", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid email pattern", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length()<7){
            Toast.makeText(this, "Password must be at least 7 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)){
            Toast.makeText(this, "Not Match!", Toast.LENGTH_SHORT).show();
            return;
        }

        createAccount();


    }

    private void createAccount() {
        progressDialog.setMessage("Creating Account");
        progressDialog.show();

        //create Account
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //account created
                        saveFirebaseData();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed creating account
                        progressDialog.dismiss();
                        Toast.makeText(AdminRegister.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void saveFirebaseData() {
        progressDialog.setMessage("Saving Account Info");

        String timestamp = ""+System.currentTimeMillis();

        if (image_uri==null){
            //save info without image

            //setup data to save
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("email",""+ email);
            hashMap.put("name",""+fullName);
            hashMap.put("retailName",""+retailName);
            hashMap.put("phone",""+phoneNumber);
            hashMap.put("country",""+country);
            hashMap.put("state",""+state);
            hashMap.put("city",""+city);
            hashMap.put("address",""+address);
            hashMap.put("latitude",""+latitude);
            hashMap.put("longitude",""+longitude);
            hashMap.put("timestamp", "" + timestamp);
            hashMap.put("accountType","Admin");
            hashMap.put("online","true");
            hashMap.put("retailOpen","true");
            hashMap.put("profileImage","");

            //save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //db updated
                            progressDialog.dismiss();
                            startActivity(new Intent(AdminRegister.this, adminMain.class));
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed updating db
                            progressDialog.dismiss();
                            startActivity(new Intent(AdminRegister.this, adminMain.class));
                            finish();

                        }
                    });
        }
        else{
            //save info with image

            //name and path of image
            String filePathAndName = "profile_images/" + ""+firebaseAuth.getUid();
            //upload image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //get url of uploaded image
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()){

                                //setup data to save
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid",""+firebaseAuth.getUid());
                                hashMap.put("email",""+email);
                                hashMap.put("name",""+fullName);
                                hashMap.put("retailName",""+retailName);
                                hashMap.put("phone",""+phoneNumber);
                                hashMap.put("country",""+country);
                                hashMap.put("state",""+state);
                                hashMap.put("city",""+city);
                                hashMap.put("address",""+address);
                                hashMap.put("latitude",""+latitude);
                                hashMap.put("longitude",""+longitude);
                                hashMap.put("timestamp", "" + timestamp);
                                hashMap.put("accountType","Admin");
                                hashMap.put("online","true");
                                hashMap.put("retailOpen","true");
                                hashMap.put("profileImage","" +downloadImageUri);//url of uploaded image

                                //save to db
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //db updated
                                                progressDialog.dismiss();
                                                startActivity(new Intent(AdminRegister.this, adminMain.class));
                                                finish();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed updating db
                                                progressDialog.dismiss();
                                                startActivity(new Intent(AdminRegister.this, adminMain.class));
                                                finish();

                                            }
                                        });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AdminRegister.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    private void showImagePickDialog() {
        //options to display in dialog
        String[] options = {"Camera", "Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle clicks
                        if (which == 0){
                            //camera clicked
                            if (checkCameraPermission()){
                                //camera permission allowed
                                pickFromCamera();

                            }
                            else{
                                //not allowed, request
                                requestCameraPermission();

                            }

                        }
                        else{
                            //gallery clicked
                            if (checkStoragePermission()){
                                //storage permission allowed
                                pickFromGallery();

                            }
                            else{
                                //not allowed, request
                                requestStoragePermission();

                            }

                        }
                    }
                })
                .show();
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }

    private void detectLocation() {
        Toast.makeText(this,"Please wait...",Toast.LENGTH_LONG).show();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

    }

    private void findAddress() {
        //find Address, country,state,city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try{
            addresses = geocoder.getFromLocation(latitude,longitude, 1);

            String address =addresses.get(0).getAddressLine(0); //complete address
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();


            //set addresses
            countryEt.setText(country);
            stateiv.setText(state);
            cityiv.setText(city);
            addressET.setText(address);

        }
        catch(Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkLocationPermission(){

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,locationPermission, LOCATION_REQUEST_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        //location detected
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        findAddress();
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        //gps location disabled
        Toast.makeText(this,"Please turn on location...",Toast.LENGTH_SHORT).show();
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted){
                        //permission allowed
                        detectLocation();
                    }
                    else{
                        //permission denied
                        Toast.makeText(this,"Location Permission is necessary...",Toast.LENGTH_SHORT).show();
                    }

                }
            }
            break;
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        //permission allowed
                        pickFromCamera();
                    }
                    else{
                        //permission denied
                        Toast.makeText(this,"Camera permissions are necessary...",Toast.LENGTH_SHORT).show();
                    }

                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        //permission allowed
                        pickFromGallery();
                    }
                    else{
                        //permission denied
                        Toast.makeText(this,"Storage permission are necessary...",Toast.LENGTH_SHORT).show();
                    }

                }
            }
            break;
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){

            if (requestCode == IMAGE_PICK_GALLERY_CODE){

                //get Picked image
                image_uri = data.getData();
                //set to imageview
                profile_pic.setImageURI(image_uri);
            } else if (resultCode == IMAGE_PICK_CAMERA_CODE){

                //set to imageview
                profile_pic.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}