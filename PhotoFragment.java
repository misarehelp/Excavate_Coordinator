package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoFragment extends Fragment implements Constants, Enums, Contract.RecordActivityToPhotoFragment {

    private Context context;
    private Button bt_back;
    private TextView tv_client_name;
    private ImageView img_client_photo;
    private String date;
    private SharedPreferences sharedPrefs;
    private int host = 0;
    private Contract.PhotoFragmentToRecordActivity callbackToActivity;
    private Contract.SomeFragmentToRecordActivity callbackToRecordFragment;
    public PhotoFragment (String date, SharedPreferences sharedPrefs) {
        this.date = date;
        this.sharedPrefs = sharedPrefs;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
      try {
         callbackToActivity = (Contract.PhotoFragmentToRecordActivity) context;
         callbackToRecordFragment = (Contract.SomeFragmentToRecordActivity) context;
      } catch (ClassCastException e) {
         throw new ClassCastException(context + " must implement Contract.RecordFragmentToRecordActivity");
      }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.photo_holder, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        bt_back = view.findViewById(R.id.bt_back);
        tv_client_name = view.findViewById(R.id.tv_client_name);
        TextView tv_date = view.findViewById(R.id.tv_date);
        img_client_photo = view.findViewById(R.id.img_client_photo);

        tv_date.setText(date);
        // go back
        bt_back.setOnClickListener(v -> {
            callbackToRecordFragment.backToRecordFragment(host);
        });

        setNoImageLayout();
        callbackToActivity.onPhotoFragmentViewCreated();
    }

    private void setNoImageLayout() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_photo);
        setBitmapFullScreen(bitmap);
    }

    @Override
    public void getSavedFileByName( String filename, int activity_host) {

        host = activity_host;
        tv_client_name.setText(filename);
        Bitmap bitmap = loadBitmapFromRepository( filename );
        if ((bitmap == null)) {
            setNoImageLayout();
        } else {
            setBitmapFullScreen(bitmap);
            callbackToActivity.onLoadPictureResult(true);
        }
    }

    private Bitmap loadBitmapFromRepository (String filename) {
        ImageSaver is = new ImageSaver(context);
        is.setFileName(filename + ".jpg");
        is.setDirectory(FILE_STORAGE);
        is.setExternal(true);
        Bitmap bm = is.load();
        return bm;
    }

    @Override
    public void saveBitmapToRepository( Bitmap orig_bitmap, String filename, int activity_host ) {

        int compress = Integer.parseInt(sharedPrefs.getString(COMPRESS, "31"));
        int rotate = Integer.parseInt(sharedPrefs.getString(ROTATE, "0"));
        Bitmap bitmap = getRotatedBitmap(orig_bitmap, rotate);
        host = activity_host;

        ImageSaver is = new ImageSaver(context);
                is.setFileName(filename + ".jpg");
                is.setExternal(true); //image save in external directory or app folder default value is false
                is.setDirectory(FILE_STORAGE);

        boolean value = is.save(bitmap, compress);
        if (value) { //Bitmap from your code
            Toast.makeText(context, DATA_WAS_SAVED, Toast.LENGTH_SHORT).show();
            setBitmapFullScreen(bitmap);
        } else {
            Toast.makeText(context, DATA_WAS_NOT_SAVED, Toast.LENGTH_SHORT).show();
        }

        callbackToActivity.onSavePictureResult(value);
    }

    private Bitmap getRotatedBitmap (Bitmap finalBitmap, int rotate) {
        Matrix rotationMatrix = new Matrix();
        if (finalBitmap.getWidth() >= finalBitmap.getHeight()) {
            rotationMatrix.setRotate( rotate );
        } else{
            rotationMatrix.setRotate(0);
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(finalBitmap,0,0,finalBitmap.getWidth(),finalBitmap.getHeight(),rotationMatrix,true);
        return rotatedBitmap;
    }

    @Override
    public void deletePictureFile(String filename) {
        ImageSaver is = new ImageSaver(context);
        is.setFileName(filename + ".jpg");
        is.setExternal(true); //image save in external directory or app folder default value is false
        is.setDirectory(FILE_STORAGE);
        boolean value = is.deleteFile();
        if (value) { //Bitmap from your code
            Toast.makeText(context, DATA_WAS_DELETED, Toast.LENGTH_SHORT).show();
            img_client_photo.setImageDrawable(null);
            tv_client_name.clearComposingText();
        } else {
            Toast.makeText(context, DATA_WAS_NOT_CHANGED, Toast.LENGTH_SHORT).show();
        }

        callbackToActivity.onSavePictureResult(false);
    }

    private void setBitmapFullScreen(Bitmap bitmap){
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int scaleWidth = metrics.widthPixels;
        Double aspect = Double.parseDouble(sharedPrefs.getString(ASPECT, "0.68"));
        int scaleHeight = (int) (metrics.heightPixels * aspect);
        Bitmap bitmapOutput = Bitmap.createScaledBitmap( bitmap, scaleWidth, scaleHeight, false );
        img_client_photo.setImageBitmap(bitmapOutput);
    }

    @Override
    public void onDetach() {
        callbackToActivity = null;
        callbackToRecordFragment = null;
        super.onDetach();
    }
}