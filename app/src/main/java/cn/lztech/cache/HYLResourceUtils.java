package cn.lztech.cache;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;


import cn.elnet.andrmb.elconnector.WSConnector;
import cn.lztech.bean.AppTagGson;

/**
 * Created by Administrator on 2015/7/21.
 */
public class HYLResourceUtils {
    public interface  HYLResourceUtilsCallback{
        public  void onFinishedDownload(boolean issuc);
    }
    public  static void startDownloadUI(final Context ctx, final String fileName,HYLResourceUtilsCallback callback){
        if(callback==null){
            callback=new HYLResourceUtilsCallback(){

                @Override
                public void onFinishedDownload(boolean issuc) {
                    if(issuc){
                        Toast.makeText(ctx,"下载成功",Toast.LENGTH_LONG).show();
                        HYLSharePreferences.cacheDownloadDirName(ctx,fileName);
                    }
                }

            };
        }

        downloadUIResources(ctx, fileName, callback);
    }




    public static  boolean isUseCustomResource(Context ctx){
        if(HYLSharePreferences.getDownloadDirName(ctx)!=null){
            return true;
        }else{
            return false;
        }
    }
    public static void  resetToSystemResource(Context ctx){
         HYLSharePreferences.cacheDownloadDirName(ctx,null);
    }


    public  static  String rootPath(Context ctx){
        String dirName= HYLSharePreferences.getDownloadDirName(ctx);
        if(enableAssetAvailable(ctx)){
            return "file:///android_asset/";
        }else{
            return  "file:///"+ctx.getFilesDir().getPath()+"/"+dirName+"/";
        }
    }
    public static String userCustomUIResPath(Context ctx){
        if(userCustomRootPath(ctx)!=null){
            return userCustomRootPath(ctx)+"/ui/";
        }else{
            return null;
        }
    }
    private static boolean enableAssetAvailable(Context ctx){
        String uiPath=userCustomUIResPath(ctx);
        if(uiPath==null){
            return true;
        }else{
            File uifile=new File(uiPath);
            if(uifile.exists()){
                return false;
            }else{
                return true;
            }
        }
    }
    public static String userCustomRootPath(Context ctx){
        String dirName= HYLSharePreferences.getDownloadDirName(ctx);
        if(dirName!=null){
            return ctx.getFilesDir().getPath()+"/"+dirName;
        }else{
            return null;
        }
    }
    public static String getStreamString(InputStream tInputStream){
        if (tInputStream != null){
            try{
                BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(tInputStream));
                StringBuffer tStringBuffer = new StringBuffer();
                String sTempOneLine = new String("");
                while ((sTempOneLine = tBufferedReader.readLine()) != null){
                    tStringBuffer.append(sTempOneLine);
                }
                return tStringBuffer.toString();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return null;
    }
    public static void loadAppTagJSON(Context ctx){

          new LoadAppTagJSOnTask(ctx).execute((String[]) null);
    }

    static class LoadAppTagJSOnTask extends  AsyncTask<String,String,String>{
        Context ctx;

        LoadAppTagJSOnTask(Context ctx){
            this.ctx=ctx;
        }
        @Override
        protected String doInBackground(String... params) {
            String appTagJSONPath="http://"+WSConnector.getInstance().getIP1()+"/public_cloud/upload/appTag.json";
            URL url= null;
            try {
                url = new URL(appTagJSONPath);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            URLConnection connection= null;
            try {
                connection = url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream  inputStream = null;
            try {
                inputStream = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(inputStream!=null){
                String tagAppString=getStreamString(inputStream);
                return tagAppString;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s!=null){
                HYLSharePreferences.cacheAppTagJson(ctx,s);
               // Toast.makeText(ctx,"缓存app tag json 成功",Toast.LENGTH_LONG).show();
            }else{
               // Toast.makeText(ctx,"数据获取失败",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }


    private static void downloadUIResources(Context ctx, String fileName,HYLResourceUtilsCallback block){
        String fileURL="http://"+WSConnector.getInstance().getIP1()+"/public_cloud/upload/"+fileName+".zip";
        String zipfileName=fileURL.substring(fileURL.lastIndexOf("/") + 1);
        File saveToFile=new File(ctx.getFilesDir(),zipfileName);
        UIDownloadTask newTask= new UIDownloadTask(fileURL,saveToFile,ctx);
        newTask.setBlock(block);
        newTask.execute((String[]) null);
    }



    static  class UIDownloadTask extends AsyncTask<String,String,String>{
        String filePath;
        File toSavePath;
        Context ctx;
        boolean isFinishDownload=false;

        public void setBlock(HYLResourceUtilsCallback block) {
            this.block = block;
        }

        HYLResourceUtilsCallback block;
        UIDownloadTask(String _filePath,File _toSavePath,Context _ctx){
            this.filePath=_filePath;
            this.toSavePath=_toSavePath;
            this.ctx=_ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            int bytesum = 0;
            int byteread = 0;
            URLConnection connection=null;
            FileOutputStream outputStream=null;
            InputStream inputStream=null;
            try {
                URL url=new URL(filePath);
                connection=url.openConnection();
                inputStream = connection.getInputStream();
                outputStream=new FileOutputStream(toSavePath);

                byte[] buffer = new byte[1204];
                while ((byteread = inputStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    outputStream.write(buffer, 0, byteread);
                }
                System.out.println("文件下载成功...");
                isFinishDownload=true;
                unzipdownloadfile(toSavePath);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {

            }finally {
                if(inputStream!=null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(outputStream!=null){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this.block.onFinishedDownload(isFinishDownload);
        }
    }



    private static void unzipdownloadfile(File innerZipfile) {
        int index=innerZipfile.getAbsolutePath().lastIndexOf(".zip");

        String unzipDir=innerZipfile.getAbsolutePath().substring(0,index);

        System.out.println("unzipDir..." + unzipDir);
        boolean isfinishUnZip=unZipFiles(innerZipfile,unzipDir);

        if(isfinishUnZip){
            if(innerZipfile.delete()){
                System.out.println(innerZipfile.getAbsolutePath() + "文件删除成功");
            }

            try {
                String srcConfigPath=unzipDir+"/config.json";
                String dstConfigPath=unzipDir+"/ui/config/config.json";

                String srcFieldPath=unzipDir+"/field.json";
                String dstFieldPath=unzipDir+"/ui/config/field.json";

                String srcLogoPath=unzipDir+"/launchLogo.png";
                String dstLogoPath=unzipDir+"/ui/img/launchLogo.png";
                movefile(srcConfigPath,dstConfigPath);
                movefile(srcFieldPath,dstFieldPath);
                movefile(srcLogoPath,dstLogoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void movefile(String srcPath,String dstPath) throws IOException {
        File srcFile=new File(srcPath);
        File dstFile=new File(dstPath);

        if(srcFile.exists()){
            FileInputStream inputStream=new FileInputStream(srcFile);
            FileOutputStream outputStream=new FileOutputStream(dstFile);
            byte[] bytes=new byte[1024];
            int reads=0;
            while((reads=inputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,reads);
            }
            srcFile.delete();
        }

    }

    private static boolean unZipFiles(java.io.File zipfile, String descDir) {
        boolean isunzipsuccessful=false;
        try {
            File descfile=new File(descDir);
            if(!descfile.exists()){
                descfile.mkdir();
            }

            ZipFile zf = new ZipFile(zipfile);
            for (Enumeration entries = zf.getEntries(); entries
                    .hasMoreElements();) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String zipEntryName = entry.getName();
                System.out.println("zipEntryName::" + zipEntryName);

                String[] childdirs=zipEntryName.split("/");
                String needCreateDir=descDir+"/";


                for (int i=0;i<childdirs.length;i++){
                    if(i==childdirs.length-1){
                        if(childdirs.length==1){
                            needCreateDir=childdirs[i];
                        }
                        File file=new File(needCreateDir);
                        if(!file.exists()){
                            file.mkdirs();
                        }

                    }else {
                        needCreateDir=needCreateDir+childdirs[i]+"/";
                    }
                }


                InputStream in = zf.getInputStream(entry);
                File file=new File(descDir +"/"+ zipEntryName);

                if(!file.exists()){
                    file.createNewFile();
                }

                if(!file.isDirectory())  {
                    OutputStream out = new FileOutputStream(file);
                    byte[] buf1 = new byte[1024];
                    int len;
                    while ((len = in.read(buf1)) > 0) {
                        out.write(buf1, 0, len);
                    }
                    in.close();
                    out.close();
                }

                if(zipEntryName.equals("ui.zip")){
                    boolean isUIUNZipSUC= unZipFiles(file,descDir+"/ui");
                    if(isUIUNZipSUC){
                        if(file.delete()){
                            System.out.println(file.getAbsolutePath() + "文件删除成功");
                        }

                    }

                }

            }
            System.out.println("解压缩完成.");
            isunzipsuccessful=true;
        } catch (IOException e) {
            e.printStackTrace();
            isunzipsuccessful=false;
        }
        return isunzipsuccessful;
    }

}
