package org.libcinder.sample.basicapp;

import org.libcinder.app.CinderNativeActivity;

public class BasicAppActivity extends CinderNativeActivity {
    static final String TAG = "BasicAppActivity";
    
    
	static {
		System.loadLibrary("usb");
		System.loadLibrary("OpenNI2");
	}

}
