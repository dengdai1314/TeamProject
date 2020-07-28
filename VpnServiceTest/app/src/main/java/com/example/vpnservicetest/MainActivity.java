package com.example.vpnservicetest;

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vpnservicetest.services.MyService;
import com.example.vpnservicetest.utils.IpUtil;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * vpn连接测试
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTvText;
    private Button mBtnConfirm;
    private Button mBtnVpnState;
    private Button mBtnDisconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
        mTvText = findViewById(R.id.tv_text);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnVpnState = findViewById(R.id.btn_vpnstate);
        mBtnDisconnect = findViewById(R.id.btn_disconnect);
        mBtnConfirm.setOnClickListener(this);
        mBtnVpnState.setOnClickListener(this);
        mBtnDisconnect.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                Intent intent = VpnService.prepare(MainActivity.this);
                if (intent != null) {
                    startActivityForResult(intent, 0);
                } else {
                    onActivityResult(0, RESULT_OK, null);
                }
                break;
            case R.id.btn_vpnstate:
                boolean isConnected=isVpnUsed();
                String ip= IpUtil.getIPAddress(MainActivity.this);
                mTvText.setText("vpn连接状态: "+isConnected+"  连接ip: "+ip);
                break;
            case R.id.btn_disconnect:
                //关闭vpn连接
                Intent intentStop = new Intent(this, MyService.class);
                intentStop.putExtra(MyService.VPN_TAG, MyService.VPN_STOP);
                stopService(intentStop);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent intentStart = new Intent(this, MyService.class);
            intentStart.putExtra(MyService.VPN_TAG, MyService.VPN_START);
            startService(intentStart);
        }
    }

    /**VPN是否已经连接**/
    private boolean isVpnUsed() {
        try {
            Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
            if (niList != null) {
                for (NetworkInterface intf : Collections.list(niList)) {
                    if (!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    Log.d("isVpnUsed() NetworkInterface Name: " , intf.getName());
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
                        return true; // The VPN is up
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
}