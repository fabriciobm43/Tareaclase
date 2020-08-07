package com.example.tareaclase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    TextView txt;
    ImageView imageView;
    Button camara;
    String uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<String> permisos = new ArrayList<String>();
        permisos.add(Manifest.permission.CAMERA);
        permisos.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permisos.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permisos.add(Manifest.permission.WRITE_CALENDAR);


        getPermission(permisos);
        camara=findViewById(R.id.btn_camara);
        imageView=findViewById(R.id.imageView);
        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarcamara();
            }
        });

    }
    public void getPermission(ArrayList<String> permisosSolicitados){

        ArrayList<String> listPermisosNOAprob = getPermisosNoAprobados(permisosSolicitados);
        if (listPermisosNOAprob.size()>0)
            if (Build.VERSION.SDK_INT >= 23)
                requestPermissions(listPermisosNOAprob.toArray(new String[listPermisosNOAprob.size()]), 1);

    }
    public ArrayList<String> getPermisosNoAprobados(ArrayList<String>  listaPermisos) {
        ArrayList<String> list = new ArrayList<String>();
        for(String permiso: listaPermisos) {
            if (Build.VERSION.SDK_INT >= 23)
                if(checkSelfPermission(permiso) != PackageManager.PERMISSION_GRANTED)
                    list.add(permiso);

        }
        return list;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String s="";
        if(requestCode==1)    {
            for(int i =0; i<permissions.length;i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    s=s + "OK " + permissions[i] + "\n";
                else
                    s=s + "NO  " + permissions[i] + "\n";
            }
            Toast.makeText(this.getApplicationContext(), s, Toast.LENGTH_LONG).show();
        }
    }
    public void BajarDoc(View view){

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, 10);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK){
            Bundle extras=data.getExtras();
            Bitmap bitmap=(Bitmap) extras.get("data");
            imageView.setImageBitmap(bitmap);
        }
       if (resultCode == RESULT_CANCELED) {
        }
        if ((resultCode == RESULT_OK) && (requestCode == 10)) {

            uri = data.getData().getPath();
            String url = "https://auto.suzuki.es/catalogos/pdf/CATALOGO_VITARA_WEB.pdf";
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("PDF");
            request.setTitle("Pdf");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            request.setDestinationInExternalPublicDir(uri, "filedownload.pdf");
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            try {
                manager.enqueue(request);
            } catch (Exception e) {
                Toast.makeText(this.getApplicationContext(),"Error: "  + e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }


    }
    private void getSDCardAccess(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(intent, 1);
    }
    public void cargarcamara(){
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent, 1);
        }
    }

}