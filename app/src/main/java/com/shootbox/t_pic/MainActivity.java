/*
 * Copyright (c) 2015-2015 by Shanghai shootbox Information Technology Co., Ltd.
 * Create: 2015/11/21 4:26:57
 * Project: T_pic
 * File: MainActivity.java
 * Class: MainActivity
 * Module: app
 * Author: yangyankai
 * Version: 1.0
 */

package com.shootbox.t_pic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

	private Button buttonCamera = null;
	private ImageView imageView = null;
	private TextView textView = null;
	private File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY   = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT       = 3;// 结果

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	private void init()
	{
		// TODO Auto-generated method stub

		buttonCamera = (Button) findViewById(R.id.button1);
		imageView = (ImageView) findViewById(R.id.imageView);
		buttonCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{

				Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 指定调用相机拍照后照片的储存路径
				cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
				startActivityForResult(cameraintent, PHOTO_REQUEST_TAKEPHOTO);
			}
		});
		textView = (TextView) findViewById(R.id.text);


	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case PHOTO_REQUEST_TAKEPHOTO:// 当选择拍照时调用
				startPhotoZoom(Uri.fromFile(tempFile));
				break;
			case PHOTO_REQUEST_GALLERY:// 当选择从本地获取图片时
				// 做非空判断，当我们觉得不满意想重新剪裁的时候便不会报异常，下同
				if (data != null)
					startPhotoZoom(data.getData());
				break;
			case PHOTO_REQUEST_CUT:// 返回的结果
				if (data != null)
					// setPicToView(data);
					sentPicToNext(data);
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		//		getMenuInflater().inflate(R.menu.activity_crama, menu);
		return true;
	}


	private void startPhotoZoom(Uri uri)
	{
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	// 将进行剪裁后的图片传递到下一个界面上
	private void sentPicToNext(Intent picdata)
	{
		Bundle bundle = picdata.getExtras();
		if (bundle != null)
		{
			Bitmap photo = bundle.getParcelable("data");
			if (photo == null)
			{
				imageView.setImageResource(R.mipmap.ic_launcher);
			}
			else
			{
				imageView.setImageBitmap(photo);
				//                设置文本内容为    图片绝对路径和名字
				textView.setText(tempFile.getAbsolutePath());
			}

			ByteArrayOutputStream baos = null;
			try
			{
				baos = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				byte[] photodata = baos.toByteArray();
				System.out.println(photodata.toString());
				// Intent intent = new Intent();
				// intent.setClass(RegisterActivity.this, ShowActivity.class);
				// intent.putExtra("photo", photodata);
				// startActivity(intent);
				// finish();
			} catch (Exception e)
			{
				e.getStackTrace();
			} finally
			{
				if (baos != null)
				{
					try
					{
						baos.close();
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	// 使用系统当前日期加以调整作为照片的名称
	private String getPhotoFileName()
	{
		Date             date       = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}
}
