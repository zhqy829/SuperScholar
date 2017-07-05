package com.superscholar.android.activity;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.entity.Goods;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class GoodsUpdateActivity extends BaseActivity {

    private static  final int REQUEST_CHOOSE_PHOTO=0;

    private ImageView imageView;
    private TextView nameText;
    private TextView priceText;
    private TextView detailText;

    private boolean isImageChoose=false;  //是否选择图片
    private Bitmap image;  //选择的图片生成的bitmap图像

    private Goods goods;
    private int position;

    //变量初始化
    private void initVariable(){
        goods=getIntent().getParcelableExtra("goods");
        position=getIntent().getIntExtra("position",0);

        imageView=(ImageView)findViewById(R.id.goods_update_imageView);
        nameText=(TextView)findViewById(R.id.goods_update_nameText);
        priceText=(TextView)findViewById(R.id.goods_update_priceText);
        detailText=(TextView)findViewById(R.id.goods_update_detailText);

        nameText.setText(goods.getName());
        priceText.setText(String.valueOf(goods.getPrice()));
        detailText.setText(goods.getDetail());
    }

    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.goods_update_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //imageView初始化
    private void initImageView(){
        String imagePath="/sdcard/photos/SuperScholar/"+goods.getUuid().toString()+".jpg";
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            image= BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(image);
            isImageChoose=true;
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CHOOSE_PHOTO);
            }
        });
    }

    //按钮初始化
    private void initButton(){
        Button button=(Button)findViewById(R.id.goods_update_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();
                if (name.equals("")) {
                    Toast.makeText(GoodsUpdateActivity.this, "请输入商品名", Toast.LENGTH_SHORT).show();
                    return;
                }

                String priceString = priceText.getText().toString();
                if (priceString.equals("")) {
                    Toast.makeText(GoodsUpdateActivity.this, "请输入商品价格", Toast.LENGTH_SHORT).show();
                    return;
                }

                int price = Integer.parseInt(priceString);
                if (price == 0) {
                    Toast.makeText(GoodsUpdateActivity.this, "商品价格不能为0", Toast.LENGTH_SHORT).show();
                    return;
                }

                String detail = detailText.getText().toString();
                if (detail.equals("")) {
                    Toast.makeText(GoodsUpdateActivity.this, "请输入商品描述", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isImageChoose) {
                    Toast.makeText(GoodsUpdateActivity.this, "请选择一张图片", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    goods.setName(name);
                    goods.setPrice(price);
                    goods.setDetail(detail);

                    String imagePath="/sdcard/photos/SuperScholar/"+goods.getUuid().toString()+".jpg";
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }

                    String imgName = createPhotoFileName(goods.getUuid());
                    savePhotoToSDCard("/sdcard/photos/SuperScholar", imgName, image);
                    Intent intent=new Intent();
                    intent.putExtra("goods",goods);
                    intent.putExtra("position",position);
                    setResult(RESULT_OK,intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //根据uuid生成文件名
    private String createPhotoFileName(UUID uuid) {
        String fileName = "";
        fileName =uuid.toString()+ ".jpg";
        return fileName;
    }

    //将图片存储至sd卡中
    private void savePhotoToSDCard(String path, String photoName, Bitmap photoBitmap) {
        FileOutputStream fileOutputStream = null;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File photoFile = new File(path, photoName);
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //图片压缩
    private Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    //获取选定的图片路径
    private String getImagePath(Uri uri, String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return  path;
    }

    //显示图片
    private void displayImage(String imagePath){
        if(imagePath!=null){
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
            isImageChoose=true;
            Bitmap smallBitmap = zoomBitmap(bitmap, bitmap.getWidth() / 5,
                    bitmap.getHeight() / 5);
            imageView.setImageBitmap(smallBitmap);
            image=smallBitmap;
        }else{
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    //处理选中的图片，KitKat版本以后调用
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }
        displayImage(imagePath);
    }

    //处理选中的图片，KitKat版本以前调用
    private void handleImageBeforeKitKat(Intent data){
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_update);

        initVariable();
        initToolbar();
        initImageView();
        initButton();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST_CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }
}
