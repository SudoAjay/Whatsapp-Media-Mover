package com.sudoajay.whatapp_media_mover_to_sdcard.Permission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.sudoajay.whatapp_media_mover_to_sdcard.After_MainTransferFIle;
import com.sudoajay.whatapp_media_mover_to_sdcard.Main_Fragments.Duplication_Class;
import com.sudoajay.whatapp_media_mover_to_sdcard.Main_Fragments.Home;
import com.sudoajay.whatapp_media_mover_to_sdcard.Main_Fragments.MainTransferFIle;
import com.sudoajay.whatapp_media_mover_to_sdcard.Sd_Card_dialog;
import com.sudoajay.whatapp_media_mover_to_sdcard.Toast.CustomToast;
import com.sudoajay.whatapp_media_mover_to_sdcard.sharedPreferences.SdCardPathSharedPreference;

import java.io.File;

@SuppressLint("Registered")
public class AndroidSdCardPermission {
    private Activity activity;
    private Context context;
    private String sd_Card_Path_URL = "",string_URI;
    private Duplication_Class duplication_class;
    private MainTransferFIle mainTransferFIle;
    private Home home;
    private SdCardPathSharedPreference sdCardPathSharedPreference;
    private After_MainTransferFIle after_mainTransferFIle;

    public AndroidSdCardPermission(Activity activity , Context context, Duplication_Class duplication_class){
        this.activity= activity;
        this.context =context;
        this.duplication_class=duplication_class;
        Grab();
    }
    public AndroidSdCardPermission(Activity activity , Context context, MainTransferFIle mainTransferFIle){
        this.activity= activity;
        this.context =context;
        this.mainTransferFIle=mainTransferFIle;
        Grab();
    }
    public AndroidSdCardPermission(Activity activity , Context context, Home home){
        this.activity= activity;
        this.context =context;
        this.home=home;
        Grab();
    }
    public AndroidSdCardPermission(Context context){
        this.context =context;
        Grab();
    }
    public AndroidSdCardPermission(Activity activity , Context context, After_MainTransferFIle after_mainTransferFIle){
        this.activity= activity;
        this.context =context;
        this.after_mainTransferFIle=after_mainTransferFIle;
        Grab();
    }


    public void call_Thread(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                    Call_Custom_Dailog_Changes();
            }
        },1800);
    }
    public void Storage_Access_FrameWork(){
        try {
            final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            int REQUEST_CODE_OPEN_DOCUMENT_TREE = 42;
            if(mainTransferFIle != null) mainTransferFIle.startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT_TREE);
            else if(duplication_class != null) {
                duplication_class.startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT_TREE);
            }else if(home != null){
                home.startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT_TREE);
            }else if( after_mainTransferFIle != null){
                after_mainTransferFIle.startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT_TREE);
            }
        }catch (Exception e){
            CustomToast.ToastIt(context,"There is Error Please Report It");
        }
    }

    public void Call_Custom_Dailog_Changes() {
        try {
            FragmentTransaction ft = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
            Sd_Card_dialog sd_card_dialog = new Sd_Card_dialog(this);
            sd_card_dialog.show(ft, "dialog");
        }catch (Exception ignored){

        }
    }
    public boolean isGetting(){
        return (sd_Card_Path_URL.equals(Environment.getExternalStorageDirectory().getAbsolutePath())) || (!new File(sd_Card_Path_URL).exists());
    }
    public void Grab(){
        // gran the data from shared preference
        sdCardPathSharedPreference = new SdCardPathSharedPreference(context);
        try {

            sd_Card_Path_URL = sdCardPathSharedPreference.getSdCardPath();
            string_URI = sdCardPathSharedPreference.getStringURI();
        }catch (Exception ignored){

            
        }
    }

    public String getString_URI() {
        return string_URI;
    }

    public String getSd_Card_Path_URL() {
        return sd_Card_Path_URL;
    }


    public void setSd_Card_Path_URL(String sd_Card_Path_URL) {
        this.sd_Card_Path_URL = sd_Card_Path_URL;
        sdCardPathSharedPreference.setSdCardPath(sd_Card_Path_URL);
    }

    public void setString_URI(String string_URI) {
        this.string_URI = string_URI;
        sdCardPathSharedPreference.setStringURI(string_URI);

    }
}
