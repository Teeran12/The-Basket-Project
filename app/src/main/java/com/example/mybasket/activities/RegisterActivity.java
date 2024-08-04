package com.example.mybasket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybasket.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    TextView btn;

    private EditText inputLoginEmail,inputEmail,inputContactNumber,inputAddress,inputPostcode,inputPassword,inputConfirmPassword;
    private Spinner spinner_usertype,spinner_gender,spinner_State;

    Button btnSignUp;
    private FirebaseAuth mAuth;
    private ProgressDialog mLoadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn=findViewById(R.id.dontHaveAnAccount);
        inputLoginEmail=findViewById(R.id.inputLoginEmail);
        spinner_usertype=findViewById(R.id.spinner_usertype);
        inputEmail=findViewById(R.id.inputEmail);
        inputContactNumber=findViewById(R.id.inputContactNumber);
        spinner_gender=findViewById(R.id.spinner_gender);
        inputAddress=findViewById(R.id.inputAddress);
        inputPostcode=findViewById(R.id.inputPostcode);
        spinner_State=findViewById(R.id.spinner_State);
        inputPassword=findViewById(R.id.inputPassword);
        inputConfirmPassword=findViewById(R.id.inputConfirmPassword);
        mAuth=FirebaseAuth.getInstance();
        mLoadingBar=new ProgressDialog(RegisterActivity.this);


        spinner_usertype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getItemAtPosition(position).equals("Admin"))
                {
                    Intent intent = new Intent(RegisterActivity.this,AdminRegister.class);
                    startActivity(intent);

                }
                return;




            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnSignUp=findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCrededentials();
            }

        });



        btn.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this,LoginActivity.class)));
    }

    private void checkCrededentials() {
        String username=inputLoginEmail.getText().toString();
        String usertype=spinner_usertype.getSelectedItem().toString();
        String email=inputEmail.getText().toString();
        String contactNumber=inputContactNumber.getText().toString();
        String gender=spinner_gender.getSelectedItem().toString();
        String address=inputAddress.getText().toString();
        String postcode=inputPostcode.getText().toString();
        String state=spinner_State.getSelectedItem().toString();
        String password=inputPassword.getText().toString();
        String confirmPassword=inputConfirmPassword.getText().toString();

        if (username.isEmpty() || username.length()<7)
        {
            showError(inputLoginEmail, "Your username is not valid!");
        }
        else if (usertype.equals("What's your role?") ) {
            Toast.makeText(RegisterActivity.this, "What's your role?", Toast.LENGTH_SHORT).show();
            return;




        }
        else if (email.isEmpty() || !email.contains("@")){
            showError(inputEmail,"Email is not valid!");
        }
        if (contactNumber.isEmpty() || contactNumber.length()>11) {
            showError(inputContactNumber, "Contact Number is not valid!");
        }
        else if (gender.equals("Choose Gender") ){
            Toast.makeText(RegisterActivity.this,"Choose Gender",Toast.LENGTH_SHORT).show();

        }
        else if (address.isEmpty() ){
            showError(inputAddress,"Field can't be empty!");
        }
        else if (postcode.isEmpty() || postcode.length()<5){
            showError(inputPostcode,"Postcode is not valid!");
        }
        else if (state.equals("State")){
            Toast.makeText(RegisterActivity.this,"State",Toast.LENGTH_SHORT).show();

        }
        else if (password.isEmpty() || password.length()<7){
            showError(inputPassword,"Password must be 7 character!");
        }
        else if (confirmPassword.isEmpty() || !confirmPassword.equals(password)){
            showError(inputConfirmPassword,"Password not match");
        }
        else
        {
            mLoadingBar.setTitle("Registration");
            mLoadingBar.setMessage("Please wait,while check your credentials");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();


            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(RegisterActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();

                        mLoadingBar.dismiss();
                        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }



    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();

    }
}