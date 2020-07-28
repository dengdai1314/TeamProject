package com.example.vpnservicetest.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.example.vpnservicetest.MainActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public class MyService extends VpnService {
    public static final String VPN_TAG="vpn_tag";
    public static final int VPN_START=1;
    public static final int VPN_STOP=-1;


    private Thread mThread;
    private ParcelFileDescriptor mInterface;
    //a. Configure a builder for the interface.
    private Builder builder = new Builder();

    // Services interface
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int tag = intent.getIntExtra(VPN_TAG, VPN_STOP);
        Log.d("=======tag===" , tag+"");

        if (tag == VPN_START) {
            // Start a new session by creating a new thread.
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent intent = new Intent(MyService.this, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        //a. Configure the TUN and get the interface.
                        mInterface = builder.setSession("inm")
                                .addAddress("10.8.0.2", 32)
                                .addDnsServer("8.8.8.8")//向VPN连接添加一个DNS服务器。支持IPv4和IPv6地址。如果没有设置，默认网络的DNS服务器将被使用，添加一个服务器隐式地允许来自该地址族的流量。(如lP4或IPv6)将通过VPN路由
                                .addRoute("0.0.0.0", 0)//添加到vpn接口的网络路由
                                .setConfigureIntent(pendingIntent)
                                .establish();//设置vpn.builder.establish接口

                        //b. Packets to be sent are queued in this input stream.
                        FileInputStream in = new FileInputStream(
                                mInterface.getFileDescriptor());
                        //b. Packets received need to be written to this output stream.
                        FileOutputStream out = new FileOutputStream(
                                mInterface.getFileDescriptor());
                        //c. The UDP channel can be used to pass/get ip package to/from server
                        DatagramChannel tunnel = DatagramChannel.open();
                        // Connect to the server, localhost is used for demonstration only.
                        //                        tunnel.connect(new InetSocketAddress("vpn.inm.cc", 10443));
                        tunnel.connect(new InetSocketAddress("192.168.1.102", 557));
                        //d. Protect this socket, so package send by it will not be feedback to the vpn service.
                        protect(tunnel.socket());
                        //e. Use a loop to pass packets.

                        while (!Thread.interrupted()) {
                            // Allocate the buffer for a single packet.
                            //get packet with in
                            //put packet to tunnel
                            //get packet form tunnel
                            //return packet with out

                            //sleep is a must
                            Thread.sleep(100);
                        }
                    } catch (Exception e) {
                        // Catch any exception
                        e.printStackTrace();
                    } finally {
                        try {
                            if (mInterface != null) {
                                mInterface.close();
                                mInterface = null;
                            }
                        } catch (Exception e) {

                        }
                    }
                }

            }, "MyVpnRunnable");
            //start the service
            mThread.start();
        } else {
            try {
                if (mInterface != null) {
                    mInterface.close();
                    mInterface = null;
                }
            } catch (Exception e) {

            }
            onRevoke();
        }
        //return START_STICKY;
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("MyService","=====Service==destroy=====");
        // TODO Auto-generated method stub
        if (mThread != null) {
            mThread.interrupt();
        }
        super.onDestroy();
    }
}
