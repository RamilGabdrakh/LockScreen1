package ramil.lockscreen1;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

    HomeWatcher mHomeWatcher;
    DevicePolicyManager deviceManger;
    ComponentName compName;

    static final int RESULT_ENABLE = 1;

    public void lock() {

        mHomeWatcher.stopWatch();
        finish();
        startActivity(new Intent(MainActivity.this, MainActivity.class));

//        boolean active = deviceManger.isAdminActive(compName);
//        if (active) {
//            deviceManger.lockNow();
//        } else {
//            Intent intent = new Intent(DevicePolicyManager
//                    .ACTION_ADD_DEVICE_ADMIN);
//            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
//                    compName);
//            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
//                    "Additional text explaining why this needs to be added.");
//            startActivityForResult(intent, RESULT_ENABLE);
//        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.e("mytag", "Admin enabled!");
                    deviceManger.lockNow();
                } else {
                    Log.e("mytag", "Admin enable FAILED!");
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set up our Lockscreen
        makeFullScreen();
        startService(new Intent(this, LockScreenService.class));

        deviceManger = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, MyAdmin.class);

        setContentView(R.layout.activity_main);

        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                Log.e("mytag", "onHomePressed");
                lock();
            }

            @Override
            public void onHomeLongPressed() {
                Log.e("mytag", "onHomeLongPressed");
                lock();
            }
        });
        mHomeWatcher.startWatch();
    }

    /**
     * A simple method that sets the screen to fullscreen.  It removes the Notifications bar,
     * the Actionbar and the virtual keys (if they are on the phone)
     */
    public void makeFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT < 19) { //View.SYSTEM_UI_FLAG_IMMERSIVE is only on API 19+
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } else {
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    @Override
    public void onBackPressed() {
        return; //Do nothing!
    }

    public void unlockScreen(View view) {
        //Instead of using finish(), this totally destroys the process
        //android.os.Process.killProcess(android.os.Process.myPid());
        //finish();

        new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}