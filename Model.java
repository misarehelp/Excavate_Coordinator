package ru.volganap.nikolay.excavate_coordinator;

import android.os.Handler;
import android.view.ViewGroup;

import java.util.Random;

public class Model implements Contract.Model {

    // this method will invoke when MainActivity initiate ViewLayout
    @Override
    public void getModelParams(Contract.Model.OnFinishedListenerPermitBlock listener, ViewGroup.LayoutParams params) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                params.height = 0;
                listener.onFinishedPermitBlock(params);
            }
        }, 0);

    };

    // this method will invoke when User changes Department
    @Override
    public void getModelUser(Contract.Model.OnFinishedListenerDepartmentUser listener, String department_user) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listener.onFinishedDepartmentUser(department_user);
            }
        }, 0);

    };

}

