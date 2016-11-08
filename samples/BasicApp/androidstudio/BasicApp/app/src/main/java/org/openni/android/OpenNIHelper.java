package org.openni.android;

/**
 * Created by marcel on 8/11/16.
 */

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;




public class OpenNIHelper {



    private Context mAndroidContext;
    private String mActionUsbPermission;
    private DeviceOpenListener mDeviceOpenListener;

    private String mUri;
    private UsbManager mUsbManager;


    private static final String TAG = "OpenNIHelper";


    /**
     * Used for receiving the result of .
     */
    public static interface DeviceOpenListener {
        /**
         * Called when permission to access the device is granted.
         *  The device for which permission was granted.
         */
        public abstract void onDeviceOpened(int fd);

        /**
         * Called when permission is access the device is denied.
         * @param uri The device for which permission was denied.
         */
        public abstract void onDeviceOpenFailed(String uri);
    }


    /**
     * Constructs an OpenNIHelper object. The constructor also extracts all files saved in {@code assets/openni},
     * to make them accessible to OpenNI.
     * @param context a Context object used to access application assets.
     */
    public OpenNIHelper(Context context) throws IOException {
        mAndroidContext = context;

        Log.d(TAG, "HELPER INIT ");
		/*
		 * The configuration files are saved as assets. To make them readable by
		 * the OpenNI native library, we need to write them to the application
		 * files directory
		 */

//        String[] list = (mAndroidContext.getAssets().list(OPENNI_ASSETS_DIR));
//        try {
//            for (String fileName : mAndroidContext.getAssets().list(OPENNI_ASSETS_DIR)) {
//                extractOpenNIAsset(fileName);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        mActionUsbPermission = context.getPackageName() + ".USB_PERMISSION";

        IntentFilter filter = new IntentFilter(mActionUsbPermission);
        mAndroidContext.registerReceiver(mUsbReceiver, filter);




    }


    public void requestDeviceOpen(String uri, DeviceOpenListener listener) {
        // TODO: Attach listener to this specific request rather than keep a "global" listener.
        // Theoretically, the client may call this method more than once with different listeners.
        mDeviceOpenListener = listener;

        mUsbManager         = (UsbManager) mAndroidContext.getSystemService( Context.USB_SERVICE );
        HashMap< String, UsbDevice > stringDeviceMap    =       mUsbManager.getDeviceList();
        Collection< UsbDevice > usbDevices              = stringDeviceMap.values();

        PendingIntent permissionIntent = PendingIntent.getBroadcast(mAndroidContext, 0, new Intent(
                mActionUsbPermission), 0);



        Iterator< UsbDevice > usbDeviceIter             = usbDevices.iterator();
        while( usbDeviceIter.hasNext() )
        {
            UsbDevice usbDevice = usbDeviceIter.next();

            Log.d(TAG, "HELPER INIT USBDEVICE VENDOR " + usbDevice.getVendorId() );
            if (usbDevice.getVendorId() == 11205) // Orbbec vendor ID
            {
                Log.d(TAG, "HELPER INIT MATCH VENDOR " + usbDevice.getVendorId() );


                Log.d(TAG, "HELPER INIT DEvice Name " + usbDevice.getDeviceName() );


                mUsbManager.requestPermission( usbDevice, permissionIntent );
            }
        }




    }


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "HELPER ONReceive  ");
            if (mActionUsbPermission.equals(action)) {
                synchronized (this) {
                    if (mDeviceOpenListener == null) {
                        Log.d(TAG, "HELPER ONReceive mDeviceOpenListener is null ");
                        return;
                    }
                    Log.d(TAG, "HELPER ONReceive Get device from intent ");
                    UsbDevice device = (UsbDevice) intent
                            .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device == null) {
                        Log.e(TAG, "HELPER BroadcastReceiver Device NULL ");
                        return;
                    }

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        // permission granted. open the device
                        try {
                            Log.e(TAG, "HELPER open device  permission was granted ");
//
                            UsbManager mUsbManager = (UsbManager) mAndroidContext.getSystemService(Context.USB_SERVICE);
                            boolean hasPermision = mUsbManager.hasPermission(device);


                            if(hasPermision)
                            {
                                Log.d(TAG, "HELPER ONRECEIVE DEvice Name " + device.getDeviceName() );

                                UsbInterface intf = device.getInterface(0);
                                UsbEndpoint endpoint = intf.getEndpoint(0);

                                Log.d(TAG, "HELPER ONRECEIVE ENDPOINT " + endpoint.toString() );
                                UsbDeviceConnection deviceConnection    = mUsbManager.openDevice( device );

                                if(deviceConnection == null)
                                {
                                    Log.d(TAG, "HELPER USB CONNECTION NULL ");
                                }
                                else
                                {
                                    int fd = deviceConnection.getFileDescriptor();
                                    Log.d( TAG, "HELPER USB File Descriptor " + fd);


                                    //OpenNI.initialize();

//                                    List<DeviceInfo> devices = OpenNI.enumerateDevices();
//                                    if (!devices.isEmpty())
//                                    {
//                                        String uri = devices.get(devices.size() - 1).getUri();
//                                        Log.d(TAG, "NIVIEWER DEVICE " + uri);
//                                        mUri = uri;
//                                    }
//
//                                    openDevice(mUri, fd);


                                }


                            }else
                            {
                                Log.d(TAG, "HELPER USB hasPermission False ");
                            }

                        } catch (Exception ex) {
                            Log.e(TAG, "HELPER Can't open device though permission was granted: " + ex);
                            //mDeviceOpenListener.onDeviceOpenFailed(mUri);
                        }
                    } else {
                        Log.e(TAG, "HELPER Permission denied for device");
                        //mDeviceOpenListener.onDeviceOpenFailed(mUri);
                    }
                }
            }
        }
    };





}
