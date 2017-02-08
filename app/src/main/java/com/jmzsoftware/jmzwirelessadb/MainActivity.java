/**
 * Jmz Wireless ADB
 *
 * Copyright 2016 by Jmz Software <support@jmzsoftware.com>
 *
 *
 * Some open source application is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * Some open source application is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this code.  If not, see https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 */

package com.jmzsoftware.jmzwirelessadb;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {

    Context context;
    TextView textView;
    Button adb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        adb = (Button) findViewById(R.id.button);
        adb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adb.getText() == getString(R.string.disable_adb)) {
                    disableAdb();
                    textView.setText("");
                    adb.setText(R.string.enable_adb);
                } else {
                    enableAdb();
                    textView.setText(String.format("Run 'adb connect %s:5555' in terminal", getIP()));
                    adb.setText(R.string.disable_adb);
                }
            }
        });

        textView = (TextView) findViewById(R.id.textView);
    }

    public void enableAdb () {
        final String[] commands = { "setprop service.adb.tcp.port 5555", "stop adbd", "start adbd" };
        Thread runSu = new Thread(new Runnable() {
            @Override
            public void run() {
                Shell.SU.run(commands);
            }
        });
        runSu.start();
    }

    public void disableAdb () {
        final String[] commands = { "stop adbd" };
        Thread runSu = new Thread(new Runnable() {
            @Override
            public void run() {
                Shell.SU.run(commands);
            }
        });
        runSu.start();
    }

    public String getIP () {
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ip = mWifiManager.getConnectionInfo().getIpAddress();
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 24) & 0xFF);

    }
}
