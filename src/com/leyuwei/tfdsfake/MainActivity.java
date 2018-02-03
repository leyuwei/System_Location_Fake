package com.leyuwei.tfdsfake;

import java.io.File;
import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.leyuwei.tfdsfake.MapDialog.onMapClickData;

public class MainActivity extends Activity implements OnClickListener,onMapClickData{
	private Button btn;
	private Button btn2;
	private Button btn3,btn4;
	private TextView tv,tv3;
	public boolean isEnabledFake,isBoss;
	private double lat = 32.0263185088f;
	private double lon = 118.7876922371f;
	public String picPath2 = "";
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private static final int PHOTO_REQUEST_GALLERY = 1;// 从相册中选择
	//记录用户首次点击返回键的时间
    private long firstTime=0;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		setTitle("随意停");
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		
		
		btn = (Button) findViewById(R.id.button1);
		btn2 = (Button) findViewById(R.id.button2);
		btn3 = (Button) findViewById(R.id.button3);
		btn4 = (Button) findViewById(R.id.button4);
		btn.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		tv = (TextView) findViewById(R.id.textView2);
		tv3 = (TextView) findViewById(R.id.textView3);
		tv3.setOnClickListener(this);
		isEnabledFake = true;
		isBoss = false;
		
		sp = getSharedPreferences("Global", Activity.MODE_WORLD_READABLE);
		editor = sp.edit();
		editor.putBoolean("isON", true);
		editor.putBoolean("isBoss", false);
		editor.putString("ll", "32.0263185088+118.7876922371");
		editor.commit();
		FileUtils.makeRootDirectory(FileUtils.SDPATH); //1.1版本修复问题
		if(sp.getBoolean("isFirst3.0", true)){
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle("新版本说明")
				   .setMessage("V3.0版本更新内容：\n1.针对物业管家新版本混淆进行了代码优化\n2.优化了GPS数据劫持稳定性")
				   .setPositiveButton("进入应用", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							editor.putBoolean("isFirst3.0", false);
							editor.commit();
						}
				   })
				   .show();
		}

		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		addLog("设备imei:"+imei);
		addLog("开始注入虚拟位置...");
	}
	
	public static void T(Activity a, String x){
		Toast.makeText(a, x, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button1:
			isEnabledFake = !isEnabledFake;
			editor.putBoolean("isON", isEnabledFake);
			editor.commit();
			if(isEnabledFake){
				btn.setText("暂停虚拟");
				addLog("已经开始注入虚拟位置...");
			}else{
				btn.setText("开启虚拟");
				addLog("已经停止注入虚拟位置...");
			}
			break;
		case R.id.button2:
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle("提示")
				   .setMessage("图片选取后可能会出现短时间的APP假死，请耐心等待，不要强行退出后台，假死是由于图片质量较高引发的长时间压缩耗时，敬请谅解！")
				   .setPositiveButton("好的", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							openGallery();
						}
				   })
				   .setNegativeButton("取消操作", null)
				   .show();
			break;
		case R.id.button3:
			FileUtils.deleteDir();
			addLog("全部缓存已删除，不留痕迹！");
			break;
		case R.id.textView3:
			MapDialog dialog = new MapDialog(); 
			dialog.setCancelable(true);
	        dialog.show(getFragmentManager(), "mapDialog"); 
			break;
		case R.id.button4:
			isBoss = !isBoss;
			editor.putBoolean("isBoss", isBoss);
			editor.commit();
			if(isBoss){
				btn4.setText("我是老板");
				addLog("已经进入老板模式...");
			}else{
				btn4.setText("我是员工");
				addLog("已经进入员工模式...");
			}
			addLog("请您务必重新启动应用以适应更改！");
			break;
		}
		
	}
	
	@Override
	protected void onDestroy() {
		isEnabledFake = false;
		editor.putBoolean("isON", isEnabledFake);
		editor.commit();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){
            if (System.currentTimeMillis()-firstTime>2000){
                Toast.makeText(MainActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                firstTime=System.currentTimeMillis();
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

	private void openGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}

	public void addLog(String x){
		tv.append(x+"\n");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTO_REQUEST_GALLERY) {
			// 从相册返回的数据
			if (data != null) {
				Uri originalUri = data.getData();
				String[] proj = { MediaStore.Images.Media.DATA };
	            // 好像是android多媒体数据库的封装接口，具体的看Android文档  
	            Cursor cursor = managedQuery(originalUri, proj, null, null, null);  
				// 按我个人理解 这个是获得用户选择的图片的索引值  
	            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
	            // 将光标移至开头 ，这个很重要，不小心很容易引起越界  
	            cursor.moveToFirst();  
	            // 最后根据索引值获取图片路径  
	            picPath2 = cursor.getString(column_index);
	            addLog("用户选定图片："+picPath2);
	            BitmapUtil.saveImgToDisk("tempfile"+File.separator+"tfdsTEMP.jpg", BitmapCompressor.compressBitmap(BitmapUtil.getDiskBitmap(picPath2), 600));
	            picPath2 = BitmapUtil.getSdCardPath() + File.separator + "tempfile" + File.separator + "tfdsTEMP.jpg";
	            addLog("已压缩照片至："+picPath2);
	            addLog("已成功注入，请切回应用并拍照");
			}
		}
	}

	@Override
	public void onMapClickComplete(LatLng ll) {
		editor.putBoolean("isON", false); //1.1版本修正
		editor.commit();
		lat = ll.latitude;
		lon = ll.longitude;
		DecimalFormat decimalFormat=new DecimalFormat(".0000");//构造方法的字符格式这里如果小数不足2位,会以0补足.
		String s = "点选虚拟坐标："+decimalFormat.format(lat)+", "+decimalFormat.format(lon);
		tv3.setText(s);
		editor.putString("ll", String.valueOf(lat)+"+"+String.valueOf(lon));
		editor.commit();
		addLog(s);
		addLog("虚拟坐标已更改完成");
		editor.putBoolean("isON", true);
		editor.commit();
		
		//1.1版本优化
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("更改位置说明")
			   .setMessage("您刚刚成功更改了虚拟坐标，为了保证在其他APP中能及时响应，可能需要您在任务管理中结束其所有后台之后重新启动，以免其不能正确响应新位置")
			   .setPositiveButton("OK", null)
			   .show();
	}
}
