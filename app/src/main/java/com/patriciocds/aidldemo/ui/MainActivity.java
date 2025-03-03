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

    private static final String AIDL_DEMO_DESC = "AIDL_DEMO";

    private IMathematics mathematicsService;
    private boolean isServiceConnected = false;

    private EditText edtValueA;
    private EditText edtValueB;
    private TextView txtResult;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mathematicsService = IMathematics.Stub.asInterface(service);
            isServiceConnected = true;

            printLog("MathematicsService connected.");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            closeService();
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

        if (isServiceConnected) {
            unbindService(connection);
            closeService();
        }
    }

    private void connectService() {
        printLog("Connecting to MathematicsService...");

        Intent intent = new Intent();
        intent.setAction("com.patriciocds.aidldemo.IMathematics");
        intent.setPackage(getPackageName());

        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void closeService() {
        isServiceConnected = false;
        mathematicsService = null;

        printLog("MathematicsService disconnected.");
    }

    @SuppressLint("SetTextI18n")
    private void sumClick() {
        if (isServiceConnected && connection != null) {
            try {
                if (checkForm()) {
                    int valueA = Integer.parseInt(edtValueA.getText().toString().trim());
                    int valueB = Integer.parseInt(edtValueB.getText().toString().trim());
                    int result = mathematicsService.sum(valueA, valueB);

                    txtResult.setText("Resultado: " + result);

                    printLog("Result: " + result);
                }
            } catch (Exception e) {
                printLog("Error: " + e.getMessage(), true);
            }
        } else {
            makeText("Connection error or service disconnected");
        }
    }

    private void printLog(String message) {
        printLog(message, false);
    }

    private void printLog(String message, boolean isError) {
        if (isError) {
            Log.e(AIDL_DEMO_DESC, message);
        } else {
            Log.v(AIDL_DEMO_DESC, message);
        }
    }

    private void makeText(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private boolean checkForm() {
        if (edtValueA.getText().toString().isEmpty() ||
                edtValueA.getText().toString().isBlank() ||
                edtValueB.getText().toString().isEmpty() ||
                edtValueB.getText().toString().isBlank()) {
            makeText("Preencha os valores necessários.");
            return false;
        }

        return true;
    }
}