package com.sudoajay.whatapp_media_mover_to_sdcard.Copy_delete_File;

import android.database.Cursor;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.View;

import com.sudoajay.whatapp_media_mover_to_sdcard.After_MainTransferFIle;
import com.sudoajay.whatapp_media_mover_to_sdcard.Database_Classes.Whatsapp_Mode_DataBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by sudoajay on 2/22/18.
 */

public class Copy_The_File {
    private String external_Path_Url,whats_App_Media_Path;
    private DocumentFile sd_Card_documentFile;
    private After_MainTransferFIle after_main_transferFIle;
    private long getSize ;
    private int get_Data_Count,normal_Changes;
    private boolean copy_Done;
    private boolean stop;
    private List<File> only_Selected_File ;
    private List<File> get_All_Data = new LinkedList<>();
    private List<String> get_All_Hash_Data = new ArrayList<>();
    private Whatsapp_Mode_DataBase whatsapp_mode_dataBase ;
    private String whatsapp_Path;

    public Copy_The_File(String external_Path_Url, String whats_App_Media_Path, DocumentFile sd_Card_documentFile,
                         After_MainTransferFIle after_main_transferFIle, List<File> only_Selected_File , int normal_Changes){
        this.external_Path_Url = external_Path_Url;
        this.whats_App_Media_Path = whats_App_Media_Path;
        this.sd_Card_documentFile=sd_Card_documentFile;
        this.after_main_transferFIle = after_main_transferFIle;
        this.only_Selected_File= only_Selected_File;
        this.normal_Changes = normal_Changes;

        whatsapp_mode_dataBase = new Whatsapp_Mode_DataBase(after_main_transferFIle);
        if(!whatsapp_mode_dataBase.check_For_Empty()){
            Cursor cursor= whatsapp_mode_dataBase.Get_All_Data();
            cursor.moveToNext();
            whatsapp_Path = cursor.getString(1);// /Gbwhatsapp/
        }

    }
    public void Copy_Folder_As_Per_Tick(int database, int audio , int video ,int document ,int images , int gif ,int voice
            ,int profile , int sticker  ){

        if(database == View.VISIBLE){
            Copy_Folder(11);
        }
        if(audio == View.VISIBLE){
            Copy_Folder(1);
        }
        if(video == View.VISIBLE){
            Copy_Folder(2);
        }
        if(document == View.VISIBLE){
            Copy_Folder(3);
        }
        if(images == View.VISIBLE){
            Copy_Folder(4);
            Log.e("Check" , external_Path_Url + whats_App_Media_Path + "/" + Return_Path(4));
        }
        if(gif == View.VISIBLE){
            Copy_Folder(5);
        }
        if(voice == View.VISIBLE){
            Copy_Folder(10);
        }
        if(profile == View.VISIBLE){
            Copy_Folder(7);
            Copy_Folder(8);
            Copy_Folder(6);
        }
        if(sticker == View.VISIBLE){
            Copy_Folder(9);
        }

        copy_Done = true;
    }
    public void Copy_Folder(int folder_No){
        try{
            File folder_File;
            DocumentFile exact_Path;
            if(folder_No!=11) {
                 exact_Path = Return_Absolute_Path(Return_Path(folder_No));

                 folder_File = new File(external_Path_Url + whats_App_Media_Path + "/" + Return_Path(folder_No));
                 Grab_The_Data(folder_File);
                 Get_List(exact_Path , folder_File);
            } else
            {
                folder_File = new File(external_Path_Url +whatsapp_Path + Return_Path(folder_No));
                 exact_Path = Return_Database_Path(Return_Path(folder_No));
                Remove_DataBase_Other_Files(folder_File);
                Delete_Database_File(exact_Path);
                File file[] = folder_File.listFiles();
                Copy_Files(file[0].getAbsolutePath(),exact_Path,file[0].getName());
            }

        }catch (Exception e){
            Log.e("Errors" , e.getMessage() );
        }
    }
    public DocumentFile Return_Absolute_Path(String folder_Name){
        DocumentFile whatsApp_dir =sd_Card_documentFile.findFile(check_For_Duplicate(sd_Card_documentFile ,
                whatsapp_Path.substring(1,whatsapp_Path.length()-1)));
        DocumentFile media_dir = whatsApp_dir.findFile(check_For_Duplicate(whatsApp_dir ,"Media"));
        return  media_dir.findFile(check_For_Duplicate(media_dir ,folder_Name));
    }
    public DocumentFile Return_Database_Path(String folder_Name){
        DocumentFile whatsApp_dir =sd_Card_documentFile.findFile(check_For_Duplicate(sd_Card_documentFile ,
                whatsapp_Path.substring(1,whatsapp_Path.length()-1)));
        return  whatsApp_dir.findFile(check_For_Duplicate(whatsApp_dir ,folder_Name));
    }
    public void Delete_Database_File(DocumentFile documentFile){
      DocumentFile documentFile1[] = documentFile.listFiles();
        for(DocumentFile f : documentFile1) {
            Objects.requireNonNull(documentFile.findFile(Objects.requireNonNull(f.getName()))).delete();
        }
    }
    public void Get_List(DocumentFile exact_Path,File folder_File){
        try{
            if(folder_File.exists() ) {
                File[] files = folder_File.listFiles();
                for (File file : files) {
                    if (!file.isDirectory()) {
                        if (Check_For_Extension(file.getAbsolutePath()) && !Selected_The_File(file) &&
                                Convert_The_LastMoified(file.lastModified())) {
                            if(Objects.requireNonNull(exact_Path.findFile("Sent")).canRead()) {
                                Copy_Files(file.getAbsolutePath(), exact_Path, file.getName());

                            }else{
                                for(String hash:get_All_Hash_Data){
                                    if(hash.equals(Grab_The_Has_Data(file))){
                                        Delete_The_Data(file);
                                    }
                                }
                                if(file.exists()){
                                    Copy_Files(file.getAbsolutePath(), exact_Path, file.getName());
                                }
                            }
                        }
                    } else {
                        Get_List(exact_Path.findFile("Sent"), file);
                        Get_List(exact_Path.findFile("Private"), file);
                    }

                }
            }
        }catch (Exception e){
            Log.e("Errora" , e.toString() );
        }
    }

    public String check_For_Duplicate(DocumentFile file , String name){
        DocumentFile[] Files = file.listFiles();
        for(DocumentFile files :Files){
            if(Objects.requireNonNull(files.getName()).equalsIgnoreCase(name)){
                return files.getName();
            }
        }
        return name;
    }
    public boolean Check_For_Extension(String path){
        int i = path.lastIndexOf('.');
        String extension="";
        if (i > 0) {
            extension = path.substring(i+1);
        }
        if(extension.equals("jpg") ||extension.equals("mp3")||extension.equals("mp4")
                ||extension.equals("pptx")||extension.equals("pdf")||extension.equals("docx")
                ||extension.equals("opus") || extension.equals("crypt12")||extension.equals("m4a")
                || extension.equals("amr") || extension.equals("aac"))
            return true;
        else
            return false;

    }

    public String Return_Path(int no){
        switch (no){
            case 1: return whatsapp_Path.substring(1,whatsapp_Path.length()-1)+" Audio";
            case 2: return whatsapp_Path.substring(1,whatsapp_Path.length()-1)+" Video";
            case 3: return whatsapp_Path.substring(1,whatsapp_Path.length()-1)+" Documents";
            case 4: return whatsapp_Path.substring(1,whatsapp_Path.length()-1)+" Images";
            case 5: return whatsapp_Path.substring(1,whatsapp_Path.length()-1)+" Animated Gifs";
            case 6: return "WallPaper";
            case 7: return whatsapp_Path.substring(1,whatsapp_Path.length()-1)+" Profile Photos";
            case 8: return ".Statuses";
            case 9: return whatsapp_Path.substring(1,whatsapp_Path.length()-1)+" Stickers";
            case 10:return whatsapp_Path.substring(1,whatsapp_Path.length()-1)+" Voice Notes";
            default:return"Databases";
        }
    }

    public void Copy_Files(String path , DocumentFile sd_Card_documentFile,String file_Name)
    {
        Log.e("Check" , "path" +path + "   docum  " + sd_Card_documentFile + " file" + file_Name);

        InputStream in;
        try {
            if(!stop){
            in = new FileInputStream(new File(path));
            getSize += after_main_transferFIle.getStorage_Info().getFileSizeInBytes(path);
            get_Data_Count++;
            if ((sd_Card_documentFile.findFile(file_Name) == null)) {
                DocumentFile documentFile = sd_Card_documentFile.createFile("image/", file_Name);
                OutputStream out = after_main_transferFIle.getContentResolver().openOutputStream(documentFile.getUri());
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }

            }
        }
            after_main_transferFIle.getMultiThreading_task().onProgressUpdate();
        }catch (Exception e){
            Log.e("GetByte" , e.getMessage());
            if(e.getMessage().equals("write failed: ENOSPC (No space left on device)"))
                stop=true;

        }


    }

    public long getGetSize() {
        return getSize;
    }

    public int getGet_Data_Count() {
        return get_Data_Count;
    }

    public boolean isCopy_Done() {
        return copy_Done;
    }

    public boolean Selected_The_File(File file){
        for (File data: only_Selected_File) {
            if (file.equals(data))
                return true;
        }

        return false;

    }
    public void Remove_DataBase_Other_Files(File database_File){
        List<File> files = new ArrayList<>();
        try{
           for(File database : database_File.listFiles()){
               files.add(database);
                          }
            Convert_Into_Last_Modified(files);
            for (int i = files.size()-1 ; i >=1;i--){
                getSize += after_main_transferFIle.getStorage_Info().getFileSizeInBytes(files.get(i).getAbsolutePath());
                get_Data_Count++;
                 new File(files.get(i).getAbsolutePath()).delete();
            }
        }catch (Exception e){
            Log.i("E" , e.getMessage());
        }
    }
    public void Convert_Into_Last_Modified(List<File> files){
        File temp_File;
        for (int i = 0 ; i < files.size();i++){
            for (int j = i ; j < files.size()-1;j++){
                Date date1 = new Date(files.get(i).lastModified());
                Date date2 = new Date(files.get(j+1).lastModified());
                if(date1.compareTo(date2) < 0){
                   temp_File=files.get(i);
                   files.set(i,files.get(j+1));
                   files.set(j+1,temp_File);
                }
            }
        }
    }
    private static MessageDigest messageDigest;
    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
        }
    }
    public String Grab_The_Has_Data(File file) {
            try {
                FileInputStream fileInput = new FileInputStream(file);
                byte fileData[] = new byte[(int) file.length()];
                fileInput.read(fileData);
                fileInput.close();
                String uniqueFileHash = new BigInteger(1, messageDigest.digest(fileData)).toString(16);
               return uniqueFileHash;
            } catch (Exception e) {
                return  null;
            }

    }
    public void Grab_The_Data(File Data_Path ){

        for(File file : Data_Path.listFiles()){
            if(!file.isDirectory()){
                get_All_Data.add(file);
            }
        }
        get_All_Hash_Data.clear();
        for(File file :get_All_Data) {
            get_All_Hash_Data.add(Grab_The_Has_Data(file));
        }

    }

    public void Delete_The_Data(File path){
        path.delete();
        if(path.exists()){
            try {
                path.getCanonicalFile().delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(path.exists()){
                after_main_transferFIle.getApplicationContext().deleteFile(path.getName());
            }
        }

    }
    public boolean isStop() {
        return stop;
    }

    public boolean Convert_The_LastMoified(long last_Modified){

        int days =0;

        Calendar current_Time = Calendar.getInstance();
        Calendar calendar_Last_Modified =Calendar.getInstance();
        calendar_Last_Modified.setTime(new Date(last_Modified));

        int current_Date = current_Time.get(Calendar.DATE);
        int last_Modified_Current_Time = calendar_Last_Modified.get(Calendar.DATE);

        int current_Month = current_Time.get(Calendar.MONTH);
        int last_Modified_Current_Month = calendar_Last_Modified.get(Calendar.MONTH);

        int current_Year = current_Time.get(Calendar.YEAR);
        int last_Modified_Current_Year = calendar_Last_Modified.get(Calendar.YEAR);

        days += (360*(current_Year-last_Modified_Current_Year));
        days += (30*(current_Month-last_Modified_Current_Month));
        days += ((current_Date-last_Modified_Current_Time));


        if(days >= normal_Changes) return true;
        return false;
    }
}
