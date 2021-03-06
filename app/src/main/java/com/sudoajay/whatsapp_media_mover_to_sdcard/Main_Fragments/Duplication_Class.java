package com.sudoajay.whatsapp_media_mover_to_sdcard.Main_Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.sudoajay.whatsapp_media_mover_to_sdcard.Main_Navigation;
import com.sudoajay.whatsapp_media_mover_to_sdcard.Permission.AndroidExternalStoragePermission;
import com.sudoajay.whatsapp_media_mover_to_sdcard.Permission.AndroidSdCardPermission;
import com.sudoajay.whatsapp_media_mover_to_sdcard.R;
import com.sudoajay.whatsapp_media_mover_to_sdcard.SdCardPath;
import com.sudoajay.whatsapp_media_mover_to_sdcard.Show_Duplicate_File;
import com.sudoajay.whatsapp_media_mover_to_sdcard.Storage_Info;
import com.sudoajay.whatsapp_media_mover_to_sdcard.HelperClass.CustomToast;

import java.io.File;
import java.util.Objects;



public class Duplication_Class extends Fragment {
    private ImageView internal_Check;
    private ImageView external_Check;
    private AndroidExternalStoragePermission androidExternalStorage_permission;
    private AndroidSdCardPermission android_sdCard_permission;
    private Button file_Size_text;
    private long size;
    private View layout,layouts;
    private Toast toast;

    private Main_Navigation main_navigation ;
    private Storage_Info storage_info;

    public Duplication_Class() {
        // Required empty public constructor
    }

    public Duplication_Class createInstance(Main_Navigation main_navigation){
        this.main_navigation = main_navigation;
        return this;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout =inflater.inflate(R.layout.fragment_duplicate_class, container, false);

        references();
        LayoutInflater inflaters = getLayoutInflater();
        layouts = inflaters.inflate(R.layout.activity_custom_toast,
                (ViewGroup) layout.findViewById(R.id.toastcustom));
        if (!androidExternalStorage_permission.isExternalStorageWritable()){
            androidExternalStorage_permission.call_Thread();
                internal_Check.setVisibility(View.INVISIBLE);
                external_Check.setVisibility(View.INVISIBLE);

        }else {
            if(!new File(androidExternalStorage_permission.getExternal_Path()+storage_info.getWhatsapp_Path()+"/").exists()){

                internal_Check.setVisibility(View.INVISIBLE);
            }
        }

        if(android_sdCard_permission.isGetting()) {
            external_Check.setVisibility(View.INVISIBLE);
        }else{
            if(!new File(android_sdCard_permission.getSd_Card_Path_URL()+storage_info.getWhatsapp_Path()+"/").exists()) {
                external_Check.setVisibility(View.INVISIBLE);
            }

        }

        runThread();


        return  layout;

    }

    private void references() {

        internal_Check = layout.findViewById(R.id.internal_Check);
        external_Check = layout.findViewById(R.id.external_Check);
        ImageView internal_Image_View = layout.findViewById(R.id.internal_Image_View);
        ImageView external_Image_View = layout.findViewById(R.id.external_Image_View);
        ConstraintLayout scanDuplicate = layout.findViewById(R.id.scanDuplicate);
        Button scanDuplicateButton = layout.findViewById(R.id.scanDuplicateButton);
        TextView internal_Text_View = layout.findViewById(R.id.internal_Text_View);
        TextView external_Text_View = layout.findViewById(R.id.external_Text_View);
        file_Size_text = layout.findViewById(R.id.file_Size_Text);

        // onclick
        OnClick_Class onClick_class = new OnClick_Class();
        internal_Check.setOnClickListener(onClick_class);
        external_Check.setOnClickListener(onClick_class);
        scanDuplicate.setOnClickListener(onClick_class);
        scanDuplicateButton.setOnClickListener(onClick_class);
        external_Image_View.setOnClickListener(onClick_class);
        internal_Image_View.setOnClickListener(onClick_class);
        internal_Text_View.setOnClickListener(onClick_class);
        external_Text_View.setOnClickListener(onClick_class);

        // create new clas object
        android_sdCard_permission = new AndroidSdCardPermission(main_navigation,Duplication_Class.this);
        androidExternalStorage_permission = new AndroidExternalStoragePermission(main_navigation, main_navigation);
        storage_info = new Storage_Info(android_sdCard_permission.getSd_Card_Path_URL(), main_navigation.getApplicationContext());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri sd_Card_URL;
        String sd_Card_Path_URL, string_URI;

        if (resultCode != Activity.RESULT_OK)
            return;
        sd_Card_URL = data.getData();
        main_navigation.grantUriPermission(main_navigation.getPackageName(), sd_Card_URL, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        assert sd_Card_URL != null;
        main_navigation.getContentResolver().takePersistableUriPermission(sd_Card_URL, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        sd_Card_Path_URL = SdCardPath.getFullPathFromTreeUri(sd_Card_URL, main_navigation);

        string_URI = sd_Card_URL.toString();
        sd_Card_Path_URL = Spilit_The_Path(string_URI, Objects.requireNonNull(sd_Card_Path_URL));

        if (!isSelectSdRootDirectory(sd_Card_URL.toString()) || !new File(sd_Card_Path_URL).exists()) {
            CustomToast.ToastIt(getContext(), getResources().getString(R.string.errorMes));
            return;
        }
        android_sdCard_permission.setSd_Card_Path_URL(sd_Card_Path_URL);
        android_sdCard_permission.setString_URI(string_URI);

    }

    private boolean isSelectSdRootDirectory(String path) {
        return path.substring(path.length() - 3).equals("%3A");
    }

    private String Spilit_The_Path(final String url, final String path) {
        try {
            String[] spilt = url.split("%3A");
            String[] getPaths = spilt[0].split("/");
            String[] paths = path.split(getPaths[getPaths.length - 1]);
            return paths[0] + getPaths[getPaths.length - 1];

        }catch (Exception ignored){
            return "";
        }
    }
    private void runThread(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Show_Size();
            }
        },1000);


    }
    @SuppressLint("SetTextI18n")
    private void Show_Size() {
        size=0;
        if(internal_Check.getVisibility() == View.VISIBLE && external_Check.getVisibility() == View.VISIBLE) {
            size +=storage_info.getFileSizeInBytes(androidExternalStorage_permission.getExternal_Path() + storage_info.getWhatsapp_Path()+"/");
            size+=storage_info.getFileSizeInBytes(android_sdCard_permission.getSd_Card_Path_URL()+
                    storage_info.getWhatsapp_Path()+"/");
            file_Size_text.setText("Data Size - " + storage_info.Convert_It(size));
        }
        else if(external_Check.getVisibility() == View.VISIBLE) {
                        size+=storage_info.getFileSizeInBytes(android_sdCard_permission.getSd_Card_Path_URL()+
                    storage_info.getWhatsapp_Path()+"/");
            file_Size_text.setText("Data Size - " + storage_info.Convert_It(size));

        }
        else  if(internal_Check.getVisibility() == View.VISIBLE) {
            size +=storage_info.getFileSizeInBytes(androidExternalStorage_permission.getExternal_Path() + storage_info.getWhatsapp_Path()+"/");
            file_Size_text.setText("Data Size - " + storage_info.Convert_It(size));

        }
    }

//    @SuppressLint("StaticFieldLeak")
//    public class MultiThreading_Task extends AsyncTask<String, String, String> {
//
//        @Override
//        protected void onPreExecute() {
//
//
//
//            super.onPreExecute();
//        }
//
//        @SuppressLint("WrongThread")
//        @Override
//        protected String doInBackground(String... strings) {
//            duplication_data.Duplication(getContext(),new File(androidExternalStorage_permission.getExternal_Path() + storage_info.getWhatsapp_Path() + "/"+""),
//                    new File(android_sdCard_permission.getSd_Card_Path_URL() + storage_info.getWhatsapp_Path() + "/"),
//                    internal_Check.getVisibility(), external_Check.getVisibility());
//            return null;
//        }
//        @Override
//        protected void onProgressUpdate(String... values) {
//
//            super.onProgressUpdate(values);
//        }
//        @Override
//        protected void onPostExecute(String s) {
//
//
//            Intent intent = new Intent(main_navigation, Show_Duplicate_File.class);
//            // To speed things up :)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            Show_Duplicate_File.DataHolder.setData(duplication_data.getList());
//            startActivity(intent);
//            super.onPostExecute(s);
//
//
//        }
//
//    }

    private void Toast_It(String Message) {
        TextView toast_TextView = layouts.findViewById(R.id.text);
        if (toast == null || toast.getView().getWindowVisibility() != View.VISIBLE) {
            toast = new Toast(main_navigation.getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layouts);
            toast_TextView.setText(Message);
            toast.show();
        } else {
            toast.cancel();
        }

    }
    public class  OnClick_Class implements View.OnClickListener {

        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.internal_Check :
                case R.id.internal_Image_View :
                case R.id.internal_Text_View :
                    if(internal_Check.getVisibility() == View.VISIBLE) {
                        internal_Check.setVisibility(View.INVISIBLE);
                        size -= storage_info.getFileSizeInBytes(androidExternalStorage_permission.getExternal_Path() + storage_info.getWhatsapp_Path()+"/");
                    }
                    else{
                        if(!androidExternalStorage_permission.isExternalStorageWritable()) {
                            Toast_It("No Permission Granted");
                            androidExternalStorage_permission.call_Thread();
                        }
                        else if(!new File(androidExternalStorage_permission.getExternal_Path()+storage_info.getWhatsapp_Path()).exists()){
                            Toast_It("No WhatsApp Data Present");
                        }
                        else {
                            internal_Check.setVisibility(View.VISIBLE);
                            size +=storage_info.getFileSizeInBytes(androidExternalStorage_permission.getExternal_Path() + storage_info.getWhatsapp_Path()+"/");

                        }

                    }
                    break;

                case R.id.external_Check :
                case R.id.external_Image_View :
                case R.id.external_Text_View :
                    if(external_Check.getVisibility() == View.VISIBLE) {
                        external_Check.setVisibility(View.INVISIBLE);
                        size-=storage_info.getFileSizeInBytes(android_sdCard_permission.getSd_Card_Path_URL()+storage_info.getWhatsapp_Path()+"/");
                    }
                    else{
                        android_sdCard_permission.Grab();

                        if(android_sdCard_permission.isGetting()) {
                            Toast_It("Select The Sd Card  ");
                            android_sdCard_permission.call_Thread();
                        }  else if(!new File(android_sdCard_permission.getSd_Card_Path_URL()+storage_info.getWhatsapp_Path()+"/").exists()){
                            Toast_It("No WhatsApp Data Present");
                        }

                        else {
                            external_Check.setVisibility(View.VISIBLE);
                            size+=storage_info.getFileSizeInBytes(android_sdCard_permission.getSd_Card_Path_URL()+
                                    storage_info.getWhatsapp_Path()+"/");
                        }
                    }
                    break;
                case R.id.scanDuplicate:
                case R.id.scanDuplicateButton:
                    if(internal_Check.getVisibility() == View.VISIBLE || external_Check.getVisibility() == View.VISIBLE) {
                        try {
                            SendForward();
                        } catch (Exception ignored) {
                        }
                    }
                    else{
                        Toast_It("You Supposed To Select Something");
                    }
                    break;

            }
            file_Size_text.setText("Data Size - " + storage_info.Convert_It(size));

        }

    }

    private void SendForward() {
        Intent intent = new Intent(main_navigation, Show_Duplicate_File.class);
        intent.putExtra("internalCheck", internal_Check.getVisibility());
        intent.putExtra("externalCheck", external_Check.getVisibility());
        startActivity(intent);

    }
}

