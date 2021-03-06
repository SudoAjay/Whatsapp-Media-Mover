package com.sudoajay.whatsapp_media_mover_to_sdcard.Main_Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.sudoajay.whatsapp_media_mover_to_sdcard.Draw_View_Canvas_Rectangle;
import com.sudoajay.whatsapp_media_mover_to_sdcard.Main_Navigation;
import com.sudoajay.whatsapp_media_mover_to_sdcard.Permission.AndroidExternalStoragePermission;
import com.sudoajay.whatsapp_media_mover_to_sdcard.Permission.AndroidSdCardPermission;
import com.sudoajay.whatsapp_media_mover_to_sdcard.R;
import com.sudoajay.whatsapp_media_mover_to_sdcard.SdCardPath;
import com.sudoajay.whatsapp_media_mover_to_sdcard.Storage_Info;
import com.sudoajay.whatsapp_media_mover_to_sdcard.HelperClass.CustomToast;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {
    private Main_Navigation main_navigation;
    private Toast toast;
    private View layout,layouts;
    private Storage_Info storage_info;
    private double internal_WhatsApp_Percentage, internal_Other_Percentage, external_WhatsApp_Percentage, external_Other_Percentage;
    private TextView internal_Storage_Available, internal_Storage_Total, internal_Storage_WhatsApp_Size, internal_Storage_Other_Size, external_Storage_Available, external_Storage_Total,
            external_Storage_WhatsApp_Size, external_Storage_Other_Size;
    private Draw_View_Canvas_Rectangle external_Draw_Bar, internal_Draw_Bar;
    private AndroidExternalStoragePermission androidExternalStoragepermission;
    private AndroidSdCardPermission android_sdCard_permission;
    public Home() {
        // Required empty public constructor
    }

    public Home createInstance(Main_Navigation main_navigation){
        this.main_navigation = main_navigation;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        layout =inflater.inflate(R.layout.fragment_home, container, false);


        LayoutInflater inflaters = getLayoutInflater();
        layouts = inflaters.inflate(R.layout.activity_custom_toast,
                (ViewGroup) layout.findViewById(R.id.toastcustom));
        Reference();
        if (!androidExternalStoragepermission.isExternalStorageWritable())
            androidExternalStoragepermission.call_Thread();

        call_Thread();

        return layout;
    }

    private void Reference() {
        internal_Storage_Available = layout.findViewById(R.id.internal_Storage_Available);
        internal_Storage_Total = layout.findViewById(R.id.internal_Storage_Total);
        internal_Storage_WhatsApp_Size = layout.findViewById(R.id.internal_Storage_WhatsApp_Size);
        internal_Storage_Other_Size = layout.findViewById(R.id.internal_Storage_Other_Size);

        external_Storage_Available = layout.findViewById(R.id.external_Storage_Available);
        external_Storage_Total = layout.findViewById(R.id.external_Storage_Total);
        external_Storage_WhatsApp_Size = layout.findViewById(R.id.external_Storage_WhatsApp_Size);
        external_Storage_Other_Size = layout.findViewById(R.id.external_Storage_Other_Size);

        internal_Draw_Bar = layout.findViewById(R.id.internal_Draw_Bar);
        external_Draw_Bar = layout.findViewById(R.id.external_Draw_Bar);

        androidExternalStoragepermission = new AndroidExternalStoragePermission(main_navigation , main_navigation);
        android_sdCard_permission = new AndroidSdCardPermission(main_navigation,this);

        // onclick class call
        OnClick_Class onClick_class = new OnClick_Class();
        internal_Storage_Available.setOnClickListener(onClick_class);
        internal_Storage_Total.setOnClickListener(onClick_class);
        internal_Storage_WhatsApp_Size.setOnClickListener(onClick_class);
        internal_Storage_Other_Size.setOnClickListener(onClick_class);
        external_Storage_Available.setOnClickListener(onClick_class);
        external_Storage_Total.setOnClickListener(onClick_class);
        external_Storage_WhatsApp_Size.setOnClickListener(onClick_class);
        external_Storage_Other_Size.setOnClickListener(onClick_class);
        internal_Draw_Bar.setOnClickListener(onClick_class);
        external_Draw_Bar.setOnClickListener(onClick_class);


    }

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
        String[] spilt = url.split("%3A");
        String[] getPaths = spilt[0].split("/");
        String[] paths = path.split(getPaths[getPaths.length - 1]);
        return paths[0] + getPaths[getPaths.length - 1];
    }

    private boolean isSamePath() {
        return androidExternalStoragepermission.getExternal_Path().equals(android_sdCard_permission.getSd_Card_Path_URL());
    }

    private void Find_External_Info() {
        if(androidExternalStoragepermission.isExternalStorageWritable()){

            internal_Storage_WhatsApp_Size.setText(storage_info.getWhatsAppInternalMemorySize());
            internal_Storage_Other_Size.setText(storage_info.getOtherInternalMemorySize());

        }

    }

    private void Find_SDCard_Info() {
        if(!sd_card_permission()) {
            external_Storage_WhatsApp_Size.setText(storage_info.getWhatsAppExternalMemorySize());
            external_Storage_Other_Size.setText(storage_info.getOtherExternalMemorySize());
        }
    }

    private void Find_The_Actual_Percentage_of_Size() {
        internal_WhatsApp_Percentage = (storage_info.getInternal_WhatsApp_Size()*100);
        internal_WhatsApp_Percentage =  convert(internal_WhatsApp_Percentage/storage_info.getInternal_Total_Size());


        internal_Other_Percentage =  (storage_info.getInternal_Other_Size()*100);
        internal_Other_Percentage= convert(internal_Other_Percentage/storage_info.getInternal_Total_Size());

        if(!sd_card_permission()) {
            external_WhatsApp_Percentage = (storage_info.getExternal_WhatsApp_Size() * 100);
            external_WhatsApp_Percentage = convert(external_WhatsApp_Percentage / storage_info.getExternal_Total_Size());

            external_Other_Percentage = (storage_info.getExternal_Other_Size() * 100);
            external_Other_Percentage = convert(external_Other_Percentage / storage_info.getExternal_Total_Size());
        }
    }

    private double convert(double angle) {
        double error= 00.0;
        try {
            DecimalFormat df = new DecimalFormat("#.00");
            return Double.parseDouble(df.format(angle));
        }catch (Exception e){
            return error;
        }
    }

    public void call_Thread(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {

                Call_After_Get_Sd_Card_Selected();
                if(androidExternalStoragepermission.isExternalStorageWritable()) {
                    Find_External_Info();
                    Find_SDCard_Info();
                    Find_The_Actual_Percentage_of_Size();


                    internal_Draw_Bar.get_Percentage(internal_WhatsApp_Percentage, internal_Other_Percentage);

                    internal_Draw_Bar.setMulti(1);
                    internal_Draw_Bar.invalidate();

                    if(!sd_card_permission()) {

                        external_Draw_Bar.get_Percentage(external_WhatsApp_Percentage, external_Other_Percentage);
                        external_Draw_Bar.setMulti(1);
                        external_Draw_Bar.invalidate();
                    }else{
                        external_Storage_WhatsApp_Size.setText("0.0 MB");
                        external_Storage_Other_Size.setText("0.0 GB");
                    }
                }else{
                    internal_Storage_WhatsApp_Size.setText("0.0 MB");
                    internal_Storage_Other_Size.setText("0.0 GB");

                    external_Storage_WhatsApp_Size.setText("0.0 MB");
                    external_Storage_Other_Size.setText("0.0 GB");

                }
                if((!androidExternalStoragepermission.isExternalStorageWritable()) || (android_sdCard_permission.isGetting())) call_Thread();

            }
        },1000);
    }
    @SuppressLint("SetTextI18n")
    private void Call_After_Get_Sd_Card_Selected() {
        storage_info = new Storage_Info(android_sdCard_permission.getSd_Card_Path_URL(),main_navigation);

        internal_Storage_Available.setText("Available : "+storage_info.getAvailableInternalMemorySize());
        internal_Storage_Total.setText("Total : "+storage_info.getTotalInternalMemorySize());
        external_Storage_Available.setText("Available : "+storage_info.getAvailableExternalMemorySize());
        external_Storage_Total.setText("Total : "+storage_info.getTotalExternalMemorySize());
    }
    public class  OnClick_Class implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (Build.VERSION.SDK_INT >= 23 && !androidExternalStoragepermission.isExternalStorageWritable()) {
                Toast_It("No Permission Granted");
                androidExternalStoragepermission.call_Thread();
                call_Thread();

            } else if (id == R.id.text_View_External_Storage || id == R.id.external_Storage_Available || id == R.id.external_Draw_Bar || id == R.id.external_Storage_Other_Data ||
                    id == R.id.external_Storage_WhatsApp_Data || id == R.id.external_Storage_Total || id == R.id.external_Storage_WhatsApp_Dot || id == R.id.external_Storage_Other_Size ||
                    id == R.id.external_Storage_WhatsApp_Size || id == R.id.external_Storage_Other_Dot) {

                if (sd_card_permission()) {
                    Toast_It("Select The Sd Card  ");
                    android_sdCard_permission.call_Thread();
                    call_Thread();
                } else if (id == R.id.external_Storage_WhatsApp_Data || id == R.id.external_Storage_WhatsApp_Size) {
                    main_navigation.getNavigationView().getMenu().getItem(1).setChecked(true);
                    main_navigation.onNavigationItemSelected(main_navigation.getNavigationView().getMenu().getItem(1));

                }
            } else if (id == R.id.textView20 || id == R.id.internal_Storage_WhatsApp_Size) {
                main_navigation.getNavigationView().getMenu().getItem(1).setChecked(true);
                main_navigation.onNavigationItemSelected(main_navigation.getNavigationView().getMenu().getItem(1));

            }
        }
    }
    private boolean sd_card_permission(){
        return android_sdCard_permission.getSd_Card_Path_URL().equals("")  || isSamePath()
                || android_sdCard_permission.isGetting();
    }
}
