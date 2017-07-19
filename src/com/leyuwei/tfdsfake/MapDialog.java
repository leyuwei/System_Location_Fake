package com.leyuwei.tfdsfake;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;

public class MapDialog extends DialogFragment {
	View myView = null; 
	TextureMapView mMapView = null; 
    BaiduMap mBaiduMap = null;
    
    public interface onMapClickData{
    	void onMapClickComplete(LatLng ll);
    }
	
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState){  
    	getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);  
    	getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        View view = inflater.inflate(R.layout.dialog_interface, container); 
        mMapView = (TextureMapView) view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng arg0) {
				onMapClickData listener = (onMapClickData) getActivity();
				listener.onMapClickComplete(arg0);
				MapDialog.this.dismiss();
			}
		});
        new BitmapDescriptorFactory();
		OverlayOptions options = new MarkerOptions()
						        .position(new LatLng(32.0263185088,118.7876922371))  //设置marker的位置
						        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding))  //设置marker图标
						        .zIndex(9)  //设置marker所在层级
						        .draggable(false);  //设置手势拖拽
	    mBaiduMap.addOverlay(options);
	    LatLng ll = new LatLng(32.0263185088,118.7876922371);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(16.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        myView = view;
        return view;  
    }
    
    @Override
    public void onDismiss(DialogInterface dialog) {
    	// TODO Auto-generated method stub
    	super.onDismiss(dialog);
    	mMapView.onDestroy();  
    }

}
