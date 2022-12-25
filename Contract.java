package ru.volganap.nikolay.excavate_coordinator;

import android.view.ViewGroup;

public interface Contract {
    interface View {
        // get LayoutParams value of View
        ViewGroup.LayoutParams getViewPermitBlockParams();

        // set Permit Block LayoutParams
        void setViewPermitBlockParams(ViewGroup.LayoutParams params);

        // get View value of department user fro resources
        String getViewDepartmentUser();

        // set View value of department user
        void setViewDepartmentUser(String string);
    }

    interface Model {
        // nested interface for PermitBlock
        interface OnFinishedListenerPermitBlock {
            // function to be called   once the Handler of Model class completes its execution
            void onFinishedPermitBlock(ViewGroup.LayoutParams params);
        }

        void getModelParams(Contract.Model.OnFinishedListenerPermitBlock onFinishedListener, ViewGroup.LayoutParams params);

        // nested interface for DepartmentUser TextView
        interface OnFinishedListenerDepartmentUser {
            // function to be called   once the Handler of Model class completes its execution
            void onFinishedDepartmentUser(String string);
        }

        void getModelUser(Contract.Model.OnFinishedListenerDepartmentUser onFinishedListener, String department_user);
    }

    interface Presenter {
        // method to be called when Main Activity initiliazes
        void onChangePermitBlockViewParams();

        // method to be called when Main Activity initiliazes
        void onChangeSharedPrefs();

        // method to be called when the button is clicked
        //void onButtonClick();

        // method to destroy lifecycle of MainActivity
        void onDestroy();
    }
}

