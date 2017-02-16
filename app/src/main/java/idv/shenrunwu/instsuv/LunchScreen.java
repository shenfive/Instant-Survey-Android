package idv.shenrunwu.instsuv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class LunchScreen extends AppCompatActivity {
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch_screen);

        timer=new Timer();
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                LunchScreen.this.finish();
            }
        };
        timer.schedule(timerTask,5000L);
    }
}
