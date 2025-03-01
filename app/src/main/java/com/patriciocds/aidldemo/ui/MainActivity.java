package com.patriciocds.aidldemo.ui;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.patriciocds.aidldemo.IMathematics;
import com.patriciocds.aidldemo.R;

public class MainActivity extends AppCompatActivity {

    private IMathematics mathematicsService;
    private boolean isBound = false;

    private EditText edtValueA;
    private EditText edtValueB;
    private TextView txtResult;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mathematicsService = IMathematics.Stub.asInterface(service);
            isBound = true;

            Log.d("AIDL_DEMO", "MathematicsService connected.");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            Log.d("AIDL_DEMO", "MathematicsService disconnected.");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtValueA = findViewById(R.id.edtValueA);
        edtValueB = findViewById(R.id.edtValueB);
        txtResult = findViewById(R.id.txtResult);

        findViewById(R.id.btnSum).setOnClickListener(v -> sumClick());

        connectService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isBound) {
            unbindService(connection);
            isBound = false;

            Log.d("AIDL_DEMO", "MathematicsService disconnected.");
        }
    }

    private void connectService() {
        Log.d("AIDL_DEMO", "Connecting to MathematicsService...");

        Intent intent = new Intent();
        intent.setAction("com.patriciocds.aidldemo.IMathematics");
        intent.setPackage(getPackageName());

        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @SuppressLint("SetTextI18n")
    private void sumClick() {
        if (isBound && connection != null) {
            try {
                int valueA = Integer.parseInt(edtValueA.getText().toString());
                int valueB = Integer.parseInt(edtValueB.getText().toString());

                int result = mathematicsService.sum(valueA, valueB);
                txtResult.setText("Resultado: " + result);

                Log.d("AIDL_DEMO", "Result: " + result);
            } catch (Exception e) {
                Log.e("AIDL_DEMO", "Error: " + e.getMessage());
            }
        } else {
            Toast.makeText(this,
                    "Connection error or service disconnected",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}