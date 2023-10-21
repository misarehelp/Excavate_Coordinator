package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;

public class ImageSaver {
   private String directoryName = "images";
   private String fileName = "image.jpg";
   private Context context;
   private boolean external = false;

   public ImageSaver(Context context) {
      this.context = context;
   }

   public ImageSaver setFileName(String fileName) {
      this.fileName = fileName;
      return this;
   }

   public ImageSaver setExternal(boolean external) {
      this.external = external;
      return this;
   }

   public ImageSaver setDirectory(String directoryName) {
      this.directoryName = directoryName;
      return this;
   }

   public boolean save(Bitmap bitmapImage, int compress) {
      boolean success = true;
      FileOutputStream fileOutputStream = null;
      try {
         fileOutputStream = new FileOutputStream(createFile());
         bitmapImage.compress(Bitmap.CompressFormat.WEBP, compress, fileOutputStream);
      } catch (Exception e) {
         success = false;
         e.printStackTrace();
      } finally {
         try {
            if (fileOutputStream != null) {
               fileOutputStream.close();
            }
         } catch (IOException e) {
            success = false;
            e.printStackTrace();
         }
      }
      return success;
   }

   @NonNull
   private File createFile() {
      File directory;
      if (external) {
         directory = getAlbumStorageDir(directoryName);
         if (!directory.exists()){
            directory.mkdir();
         }
      } else {
         directory = new File(context.getFilesDir()+"/"+directoryName);
         if (!directory.exists()){
            directory.mkdir();
         }
      }

      return new File(directory, fileName);
   }

   private File getAlbumStorageDir(String albumName) {
      File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
      if (!file.mkdirs()) {
         Log.e("ImageSaver", "Directory not created");
      }
      return file;
   }

   public Bitmap load() {
      FileInputStream inputStream = null;
      try {
         inputStream = new FileInputStream(createFile());
         return BitmapFactory.decodeStream(inputStream);
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            if (inputStream != null) {
               inputStream.close();
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      return null;
   }

   public boolean deleteFile() {
      File file = createFile();
      return file.delete();
   }
}

   /* public static boolean isExternalStorageWritable() {
      String state = Environment.getExternalStorageState();
      return Environment.MEDIA_MOUNTED.equals(state);
   }

   public static boolean isExternalStorageReadable() {
      String state = Environment.getExternalStorageState();
      return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
   } */