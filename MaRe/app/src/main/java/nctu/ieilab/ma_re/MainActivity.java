/**
 * File: MainActivity.java
 *
 * An Android app of the client of MaRe.
 *
 * Author: Gary Huang <gh.nctu+code@gmail.com>
 * License: 3-clause BSD License
 */
package nctu.ieilab.ma_re;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final Pattern PARTIAl_IP_ADDRESS = Pattern.compile("^((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])\\.){0,3}((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])){0,1}$");

    private TextView tv_message;
    private EditText et_user_addr, et_user_port;
    private Button bt_connect;
    private Button bt_forward;
    private Button bt_backward;
    private boolean first_click;

    private String addr;
    private int port;

    private Socket socket_stream;
    private Socket socket_control;

    private PrintWriter out;

    private enum JpegState { INI, SOI, TEM, RST, EOI }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createWidget();
        first_click = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (first_click) {
            bt_connect.callOnClick();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            socket_stream.close();
        } catch (IOException | NullPointerException e) {
            // do nothing
        }
        try {
            socket_control.close();
        } catch (IOException | NullPointerException e) {
            // do nothing
        }
        tv_message.setText("Disconnected");
    }

    private void createWidget() {
        tv_message = (TextView) findViewById(R.id.message);
        et_user_addr = (EditText) findViewById(R.id.user_addr);
        et_user_addr.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void beforeTextChanged(CharSequence s,int start,int count,int after) {}

            private String mPreviousText = "";
            @Override
            public void afterTextChanged(Editable s) {
                if(PARTIAl_IP_ADDRESS.matcher(s).matches()) {
                    mPreviousText = s.toString();
                } else {
                    s.replace(0, s.length(), mPreviousText);
                }
            }
        });
        et_user_port = (EditText) findViewById(R.id.user_port);
        bt_connect = (Button) findViewById(R.id.connect);
        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!handleUserInputs()) {
                    return;
                }
                first_click = true;
                new StreamTask().execute();
            }
        });
        bt_forward = (Button) findViewById(R.id.forward);
        bt_forward.setOnTouchListener(new RepeatListener(100, 100, new OnClickListener() {
            @Override
            public void onClick(View v) {
                out.println("forward");
            }
        }));
        bt_backward = (Button) findViewById(R.id.backward);
        bt_backward.setOnTouchListener(new RepeatListener(100, 100, new OnClickListener() {
            @Override
            public void onClick(View v) {
                out.println("backward");
            }
        }));
    }

    private boolean handleUserInputs() {

        String s_addr, s_port;

        s_addr = et_user_addr.getText().toString();
        s_port = et_user_port.getText().toString();

        if (s_port.isEmpty()) {
            tv_message.setText("Please fill port number.");
            return false;
        }

        if (s_addr.isEmpty()) {
            tv_message.setText("Please fill the IP address.");
            return false;
        }

        addr = s_addr;
        port = Integer.parseInt(s_port);

        return true;
    }

    private class StreamTask extends AsyncTask<String,String,String> {

        private Bitmap bitmap;

        @Override
        protected String doInBackground(String... params) {
            String message = "Connecting to " + addr + ":" + port + ", 8889 ...";
            publishProgress("message", message);
            try {
                socket_stream = new Socket(addr, port);
                socket_control = new Socket(addr, 8889);

                if (!socket_stream.isConnected()) {
                    message += "\nFailed to connect socket!";
                    publishProgress("message", message);
                    return null;
                }

                if (!socket_control.isConnected()) {
                    message += "\nFailed to connect socket!";
                    socket_stream.close();
                    publishProgress("message", message);
                    return null;
                }

                message += "\nConnected!";
                publishProgress("message", message);
                publishProgress("button_disable");

                // get display dimension information
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int screenWidth = size.x;

                InputStream is = socket_stream.getInputStream();
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket_control.getOutputStream())), true);

                while (socket_stream.isConnected()) {
                    ByteArrayOutputStream accumulate = new ByteArrayOutputStream();

                    // handle JFIF streaming
                    int byteRead;
                    byte[] buffer = new byte[1024];
                    JpegState state = JpegState.INI;
                    while ((byteRead = is.read(buffer)) != -1) {
                        accumulate.write(buffer, 0, byteRead);

                        // handle JFIF marker
                        byte[] ba_accumulate = accumulate.toByteArray();
                        int i;
                        // Find the beginning of the JFIF
                        if (state == JpegState.INI) {
                            for (i = 0; i < ba_accumulate.length - 2; i++) {
                                if ((ba_accumulate[i]   & 0xFF) == 0xFF &&
                                        (ba_accumulate[i+1] & 0xFF) == 0xD8) {
                                    accumulate.reset();
                                    accumulate.write(ba_accumulate, i, ba_accumulate.length - i);
                                    ba_accumulate = accumulate.toByteArray();
                                    state = JpegState.SOI;
                                    break;
                                }
                            }
                            if (state == JpegState.SOI) {
                                continue;
                            }
                        }
                        if (state == JpegState.SOI) {
                            // JFIF is ready, then find the ending
                            for (i = ba_accumulate.length - 2; i > 0; i--) {
                                if ((ba_accumulate[i]   & 0xFF) == 0xFF &&
                                        (ba_accumulate[i+1] & 0xFF) == 0xD9) {
                                    state = JpegState.EOI;
                                    break;
                                }
                            }
                            if (i > 0) {
                                break;
                            }
                        }
                    }
                    accumulate.flush();

                    // from byte array to bitmap
                    byte[] imageByte = accumulate.toByteArray();
                    ByteArrayInputStream bais = new ByteArrayInputStream(imageByte);
                    bitmap = BitmapFactory.decodeStream(bais);

                    // scaling and display
                    double scale = screenWidth / bitmap.getWidth();
                    bitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, (int) (scale * (double) bitmap.getHeight()), true);
                    publishProgress("image");

                    accumulate.close();
                }
            } catch (UnknownHostException e) {
                message += "\nUnknownHostException: " + e.getMessage();
                publishProgress("message", message);
            } catch (IOException e) {
                message += "\nIOException: " + e.getMessage();
                publishProgress("message", message);
            } catch (NullPointerException e) {
                message += "\nNullPointerException: " + e.getMessage();
                publishProgress("message", message);
            } catch (Exception e) {
                message += "\nException: " + e.getMessage();
                publishProgress("message", message);
            }
            publishProgress("button_enable");
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            switch (values[0]) {
                case "image":
                    ImageView iv = (ImageView) findViewById(R.id.imageView);
                    iv.setImageBitmap(bitmap);
                    break;
                case "message":
                    TextView tv_message = (TextView) findViewById(R.id.message);
                    tv_message.setText(values[1]);
                    break;
                case "button_enable": {
                    Button bt_connect = (Button) findViewById(R.id.connect);
                    bt_connect.setEnabled(true);
                    break;
                }
                case "button_disable": {
                    Button bt_connect = (Button) findViewById(R.id.connect);
                    bt_connect.setEnabled(false);
                    break;
                }
            }
        }
    }

    /**
     * A class, that can be used as a TouchListener on any view (e.g. a Button).
     * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
     * click is fired immediately, next one after the initialInterval, and subsequent
     * ones after the normalInterval.
     *
     * <p>Interval is scheduled after the onClick completes, so it has to run fast.
     * If it runs slow, it does not generate skipped onClicks. Can be rewritten to
     * achieve this.
     *
     * From: http://stackoverflow.com/questions/4284224/android-hold-button-to-repeat-action
     */
    public class RepeatListener implements OnTouchListener {

        private Handler handler = new Handler();

        private int initialInterval;
        private final int normalInterval;
        private final OnClickListener clickListener;

        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, normalInterval);
                clickListener.onClick(downView);
            }
        };

        private View downView;

        /**
         * @param initialInterval The interval after first click event
         * @param normalInterval The interval after second and subsequent click
         *       events
         * @param clickListener The OnClickListener, that will be called
         *       periodically
         */
        public RepeatListener(int initialInterval, int normalInterval,
                              OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handler.removeCallbacks(handlerRunnable);
                    handler.postDelayed(handlerRunnable, initialInterval);
                    downView = view;
                    downView.setPressed(true);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handler.removeCallbacks(handlerRunnable);
                    downView.setPressed(false);
                    downView = null;
                    return true;
            }

            return false;
        }

    }
}
