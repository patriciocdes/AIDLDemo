package com.patriciocds.aidldemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.patriciocds.aidldemo.IMathematics;

public class MathematicsService extends Service {

    private final IMathematics.Stub binder = new IMathematics.Stub() {
        @Override
        public int sum(int a, int b) throws RemoteException {
            return a + b;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}