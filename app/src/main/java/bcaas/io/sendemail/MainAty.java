package bcaas.io.sendemail;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.support.v4.content.FileProvider.getUriForFile;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/13
 */
public class MainAty extends Activity {

    private TextView textView;
    private Handler handler;

    private File file;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv_dot);
        handler = new Handler();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File[] files = getExternalFilesDirs(Environment.MEDIA_MOUNTED);
            for (File file : files) {
                Log.e("saveInfo", file + "");
            }
        }
        File rootFile = new File(getExternalFilesDir("bcaas").getAbsolutePath());
        if (!rootFile.exists()) {
            System.out.println("我创建了1");

            rootFile.mkdir();
        }
        file = new File(rootFile, "bcaas.txt");
        if (!file.exists()) {
            try {
                System.out.println("我创建了2");
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e("saveInfo", file + "");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyStoragePermissions(MainAty.this);

            }
        });
    }


    /*保存用户信息 */
    public boolean saveExternalInfo(String info, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(info.getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    private boolean saveInternalInfo(String info) {
        FileOutputStream fos = null;
        try {
            fos = this.openFileOutput("bcaas.txt", Context.MODE_APPEND);
            fos.write(info.getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (fos != null) {
                try {
                    fos.close();

                    File file = this.getFilesDir();  //getFilesDir()获取你app的内部存储空间，相当于你的应用在内部存储上的根目录
                    Log.d("path", file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    private void readLength() {
        File file = new File(this.getFilesDir() + "/bcaas.txt");
        System.out.println("readLength:" + file);

        if (file.exists()) {
            System.out.println(file.length());
        }
    }

    /*读取用户信息*/
    public String readExternalInfo(File file) {
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String info = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inStream));
            info = br.readLine();
            Log.d("SaveUserInfo====", info);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return info;
        }
    }

    /*读取用户信息*/
    public String readInternalInfo() {
        FileInputStream inStream = null;
        try {
            inStream = this.openFileInput("bcaas.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String info = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inStream));
            info = br.readLine();
            Log.d("SaveUserInfo====", info);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return info;
        }
    }

    private void sendToEmail() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        }
        Uri uri = getUriForFile(this, "bcaas.io.sendemail.fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Bcaas钱包文件");
        intent.putExtra(Intent.EXTRA_TEXT, "请妥善保存");
        //当无法确认发送类型的时候使用如下语句
        intent.setType("*/*");
        System.out.println(uri);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivityForResult(intent, 0);
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};


    public void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            } else {
                saveExternalInfo("this is a catherine", file);
                readExternalInfo(file);
                sendToEmail();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                    System.out.println("我已经获取权限了");
                    saveExternalInfo("this is a catherine", file);
                    readExternalInfo(file);
                    sendToEmail();

                } else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    System.out.println("我被拒绝获取权限了");

                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("onActivityResult" + requestCode);
        System.out.println("onActivityResult" + resultCode);
    }

    private int resetCount = 0;
    private Runnable buildSocket = new Runnable() {
        @Override
        public void run() {
            if (resetCount >= 4) {
                handler.postDelayed(this, 10000);
                System.out.println("on++++++++10000");
                resetCount = 0;

            } else {
                handler.postDelayed(this, 1000);
                System.out.println("on++++++++1000");

            }
            resetCount++;
        }
    };

}
