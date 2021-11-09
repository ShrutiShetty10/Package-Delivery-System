package com.example.packagedeliverysystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {

    String dest, whereToGo;
    String fullName, email, userName, password, address, resAddress, aadhaarNumber, phoneNumber;
    int slots;
    double lat, lon;

    PinView pinView;
    String codeBySystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        Intent intent = getIntent();
        fullName = intent.getStringExtra("fullName");
        email = intent.getStringExtra("email");
        userName = intent.getStringExtra("userName");
        password = intent.getStringExtra("password");
        address = intent.getStringExtra("address");
        phoneNumber = intent.getStringExtra("phoneNumber");

        pinView = findViewById(R.id.pin_view);

        dest = intent.getStringExtra("dest");
        whereToGo = intent.getStringExtra("whereToGo");

        if(dest.equals("delivery")) {
            aadhaarNumber = intent.getStringExtra("aadhaarNumber");
        } else if(dest.equals("warehouse")) {
            resAddress = intent.getStringExtra("resAddress");
            aadhaarNumber = intent.getStringExtra("aadhaarNumber");
            slots = intent.getIntExtra("slots", 0);
            lat = intent.getDoubleExtra("lat", 0);
            lon = intent.getDoubleExtra("lon", 0);
        }

        sendVerificationCodeToUser(phoneNumber);
    }

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private void sendVerificationCodeToUser(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    Log.d("Auto-complete", "onVerificationCompleted:" + credential);

                    String code = credential.getSmsCode();
                    if (code != null) {
                        pinView.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    Log.w("WrongCredentials", "onVerificationFailed", e);

                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    Log.d("ManualVerification", "onCodeSent:" + s);

                    super.onCodeSent(s, token);
                    codeBySystem = s;
                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("ProcessSucceeded", "signInWithCredential:success");

                            if(whereToGo.equals("setNewPassword")) {
                                updateOldUsersData();
                            } else if(whereToGo.equals("signUpSuccessfull")) {
                                storeNewUsersData();
                            }
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.d("ProcessFailed", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
    private void storeNewUsersData() {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference;
        if(dest.equals("delivery")) {
            reference = rootNode.getReference("Deliveries");
            reference.child(userName).child("State").setValue("Waiting");
        } else if(dest.equals("warehouse")) {
            Point point = new Point();
            point.setX(lat);
            point.setY(lon);

            reference = rootNode.getReference("WarehouseCoordinates");
            reference.child(userName).setValue(point);

            reference = rootNode.getReference("Warehouses");
            reference.child(userName).child("Slots").setValue(slots);

            for (int i = 1; i <= slots; i++)
                reference.child(userName).child(String.valueOf(i)).child("State").setValue("Unoccupied");
        }
        reference = rootNode.getReference("Registration").child(dest);

        Log.d("Jayesh", "Jayesh");
        //Create helperclass reference and store data using firebase
        if(dest.equals("delivery")){
            UserHelperClass addNewUser = new UserHelperClass(fullName, email, userName, password, address, aadhaarNumber, phoneNumber);
            reference.child(userName).setValue(addNewUser);
        } else if(dest.equals("warehouse")) {
            UserHelperClass addNewUser = new UserHelperClass(fullName, email, userName, password, address, resAddress, aadhaarNumber, phoneNumber);
            reference.child(userName).setValue(addNewUser);
        } else {
            UserHelperClass addNewUser = new UserHelperClass(fullName, email, userName, password, address, phoneNumber);
            reference.child(userName).setValue(addNewUser);
        }

        Intent intent = new Intent(getApplicationContext(), SignUpSuccessfull.class);
        intent.putExtra("dest", dest);
        startActivity(intent);
        finish();

    }

    private void updateOldUsersData() {
        Intent intent = new Intent(getApplicationContext(), SetNewPassword.class);
        intent.putExtra("phoneNo", phoneNumber);
        intent.putExtra("userName", userName);
        intent.putExtra("dest", dest);
        startActivity(intent);
        finish();
    }

    public void callNextScreenFromOTP(View view) {

        String code = pinView.getText().toString();
        if(!code.isEmpty())
            verifyCode(code);

    }
}