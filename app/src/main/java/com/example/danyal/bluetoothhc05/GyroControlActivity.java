package com.example.danyal.bluetoothhc05;

        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.support.v4.app.NavUtils;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.WindowManager;
        import android.view.inputmethod.EditorInfo;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.ProgressBar;
        import android.widget.TextView;
        import java.lang.ref.WeakReference;

        import butterknife.Bind;
        import butterknife.ButterKnife;
        import butterknife.OnClick;

public class GyroControlActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_gyro_control);
        }
}
