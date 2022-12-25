package ru.volganap.nikolay.excavate_coordinator;

import android.view.ViewGroup;

public class Presenter implements Contract.Presenter, Contract.Model.OnFinishedListenerPermitBlock,
                                                    Contract.Model.OnFinishedListenerDepartmentUser {

    // creating object of View Interface
    private Contract.View mainView;

    // creating object of Model Interface
    private Contract.Model model;

    // instantiating the objects of View and Model Interface
    public Presenter(Contract.View mainView, Contract.Model model) {
        this.mainView = mainView;
        this.model = model;
    }

    @Override
    // operations to be performed - Init Main MainViewLayout
    public void onChangePermitBlockViewParams() {
        if (mainView != null) {
            ViewGroup.LayoutParams params = mainView.getViewPermitBlockParams();
            model.getModelParams(this, params);
        }
    }

    @Override
    // operations to be performed - Init Main MainViewLayout
    public void onChangeSharedPrefs() {
        if (mainView != null) {
            String department_user = mainView.getViewDepartmentUser();
            model.getModelUser(this, department_user);
        }
    }

    @Override
    // method to return Layout Params of Permit Block
    public void onFinishedPermitBlock (ViewGroup.LayoutParams params) {
        if (mainView != null) {
            mainView.setViewPermitBlockParams(params);
        }
    }

    @Override
    // method to return Dapartment name
    public void onFinishedDepartmentUser (String department_user) {
        if (mainView != null) {
            mainView.setViewDepartmentUser(department_user);
        }
    }

    @Override
    public void onDestroy() {
        mainView = null;
    }

}

