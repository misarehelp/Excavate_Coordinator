package ru.volganap.nikolay.excavate_coordinator;

import android.os.Handler;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModelPermit implements Contract.ModelPermit,  KM_Constants, Enums {

    private String place, date_start, date_end, comment;
    private boolean [] required_array;
    private  int length;

    private DataParameters dataParameters;

    // initiating the objects of Model
    public ModelPermit( DataParameters dataParameters, int length  ) {

        this.dataParameters = dataParameters;
        this.length = length;
        dataParameters.setStateCode(DATA_WAS_NOT_CHANGED);
    }

    //set  Model Permit Place Date Comment
    public void setModelPermitPlaceDateComment (String place, String date_start, String date_end, String comment) {
        this.place = place;
        this.date_start = date_start;
        this.date_end = date_end;
        this.comment = comment;
    }

    // Set Model Permit List Item Click
    @Override
    public void setModelPermitListItemClick( Contract.ModelPermit.OnFinishedSetMainViewLayout permit_listener,
                                             Contract.ViewMainLayout view_listener, Contract.ModelMain modelMain, int position, int lv_id, int viewItew) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                    // Get info about chosen work permit
                    DepLinesData dep_line_data;
                    String permit_code;
                    int dld_array_pos;
                    // a permit made by user was chosen
                    if (lv_id == viewItew) {

                        permit_code = EDIT_MASTER_PERMIT_CODE;

                        dld_array_pos = modelMain.getPermitArrayUserMade().get(position);
                        dep_line_data = modelMain.getModelDepLinesDataArray().get(dld_array_pos);

                    } else {

                        dld_array_pos = modelMain.getPermitArrayAwaiting().get(position);
                        dep_line_data = modelMain.getModelDepLinesDataArray().get(dld_array_pos);

                        if (dataParameters.getDispatcherMode()) {
                            permit_code = SHOW_PERMIT_CODE;
                        } else {
                            if (dep_line_data.getHashmapCommExist().get(dataParameters.getModelDepartmentUser()).equals(Approvement.UN)) {
                                // a permit awaiting for user approvement, not finished yet
                                permit_code = CHANGE_PERMIT_CODE;
                            } else {
                                // a permit awiting for user approvement, just for browse
                                permit_code = SHOW_PERMIT_CODE;
                            }
                        }
                    }

                    dataParameters.setPosition(dld_array_pos);
                    dataParameters.setStateCode(permit_code);
                    dataParameters.setDepLineData(dep_line_data);

                    permit_listener.OnFinishedSetPermitSimpleAdapter( fillInDepsApproveList(permit_code) );
                    permit_listener.OnFinishedSetPermitIDtextView(dep_line_data.getId());
                    permit_listener.OnFinishedSetPlaceDateComment(dep_line_data.getPlace(), dep_line_data.getStringDateStart(),
                            dep_line_data.getStringDateEnd(), dep_line_data.getComment());

                    view_listener.OnFinishedButtonSaveSetViewButtonsVisibility( permit_code );
                    view_listener.OnFinishedSetPermitBlockState( PermitBlock.VISIBLE );

            }
        }, 0);
    }

    //set Required Deps Array for Permit List View
    @Override
    public void setRequiredDepsArray (AdapterView<?> parent) {

        required_array = new boolean[ length ];
        SparseBooleanArray chosen = ((ListView) parent).getCheckedItemPositions();
        for (int i = 0; i < chosen.size(); i++) {
            required_array[chosen.keyAt(i)] = chosen.valueAt(i);
        }
    }

    //Start Maps Activity
    @Override
    public void setModelPermitButtonDepsChooseClick( Contract.ModelPermit.OnFinishedSetMainViewLayout permit_listener,
                                                     Contract.ViewMainLayout view_listener, String value, String sample ) {
        if (value.equals(sample)){
            //Show Department listview
            permit_listener.OnFinishedButtonDepsChooseVisible();
        } else {
            //Hide Department listview
            String message = checkCorrectInputChosenDeps();
            // Check if correct departments are put in
            if (message.equals("")) {
                String code = dataParameters.getStateCode();
                // Set Deps Block invisible
                permit_listener.OnFinishedButtonDepsChooseInvisible();
                // Set View Buttons visibility
                view_listener.OnFinishedButtonSaveSetViewButtonsVisibility( FILLED_PERMIT_CODE );
                // Fill in Department approvement List
                permit_listener.OnFinishedSetPermitSimpleAdapter( fillInDepsApproveList(code) );

            } else {
                permit_listener.OnFinishedShowToast(message);
            }
        }
    }

    // check if appropreiate departments chosen
    private String checkCorrectInputChosenDeps() {
        final String SOME_DEPS_MUST_BE_CHOSEN = "Необходимо отметить подразделения для согласования в списке ";
        final String AUTHOR_MUST_NOT_BE_CHOSEN = "Подразделение(автор) наряда не должно быть отмечено";

        int sum = 0;
        String[] department_array = dataParameters.getDepartmentArray();
        // check if the fields departments are empty or inappropriate
        String master = dataParameters.getDepLineData().getDepartMaster();

        for (int i = 0; i < required_array.length; i++) {
            if  (required_array[i]) {
                //check for if department of user-author is unchecked
                if (department_array[i].equals(master)) {
                    return AUTHOR_MUST_NOT_BE_CHOSEN;
                }  else {
                    sum ++;
                }
            }
        }
        if (sum == 0) {
            return SOME_DEPS_MUST_BE_CHOSEN;
        }
        return "";
    }

    // Fill In Permits User Made List
    private ArrayList<Map<String, String>> fillInDepsApproveList(String permit_code) {

        ArrayList<Map<String, String>> data = new ArrayList<>();
        Map<String, String> hashmap_adapter;

        // fill in fields for a new permit or edited permit
        if (permit_code.equals(NEW_PERMIT_CODE)) {
            initPermitFields();
        }

        DepLinesData dep_line_data = dataParameters.getDepLineData();

        for (String department: dep_line_data.getDateApproveHashmap().keySet()) {

            hashmap_adapter = new HashMap<>();

            hashmap_adapter.put(FROM[0], department);
            hashmap_adapter.put(FROM[1], Approvement.YES.getValue());
            hashmap_adapter.put(FROM[2], dep_line_data.getHashmapCommExist().get(department).getValue());
            hashmap_adapter.put(FROM[3], dep_line_data.getDateApproveHashmap().get(department));

            data.add(hashmap_adapter);
        }

        return data;
    }

    private void initPermitFields() {

        HashMap<String, Approvement> hashmap_communication = new HashMap<>();
        HashMap<String, String> hashmap_date_approve = new HashMap<>();
        ArrayList<String> department_array = new ArrayList();

        DepLinesData dep_line_data = dataParameters.getDepLineData();
        department_array = getDepartmentRequiredArray();

        for (String department: department_array) {

            hashmap_communication.put(department, Approvement.UN);
            hashmap_date_approve.put(department, Approvement.ND.getValue());
        }

        dep_line_data.setPlace(place);
        dep_line_data.setStringDateStart(date_start);
        dep_line_data.setStringDateEnd(date_end);
        dep_line_data.setComment(comment);

        dep_line_data.setHashmapCommExist(hashmap_communication);
        dep_line_data.setDateApproveHashmap(hashmap_date_approve);

        dataParameters.setDepLineData(dep_line_data);
    }

    private ArrayList<String>  getDepartmentRequiredArray() {

        String [] da  = dataParameters.getDepartmentArray();
        ArrayList<String> requiredDepartmentArray = new ArrayList<>();
        for (int i = 0; i < required_array.length; i++) {
            if (required_array[i]) {
                requiredDepartmentArray.add(da[i]);
            }
        }
        return requiredDepartmentArray;
    }
}