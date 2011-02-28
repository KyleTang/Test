package cn.kyle.test.TarSystem;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TarSystem extends Activity {
    /** Called when the activity is first created. */
	Handler h = new Handler();

	private ProgressDialog p  = null ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (!Helper.hasBusyBox(this)){
        	Helper.unpackFile(this, "busybox", "777");
        }
        Button btnTarSystem = (Button)findViewById(R.id.btnTarSystem);
        btnTarSystem.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				p = new ProgressDialog(TarSystem.this);
				p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				p.setTitle("正在打包");
				p.setMessage("请稍候，会持续几分钟时间...");
				p.show();
				Thread t = new Thread(){
					public void run(){
						try {
							Helper.runSuCommand(TarSystem.this,
									Helper.warpCmdWithBusybox(TarSystem.this,
											"tar zcvf /sdcard/tarsystem-"+Build.MODEL+"-"+System.currentTimeMillis()+".tgz /system"));
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						postFinished();
					}
				};
				t.start();
			}
        });
    }

    public void postFinished(){
    	h.post(new Runnable() {
			public void run() {
				if (p!=null){
		    		p.dismiss();
		    	}
			}
		});
    }
}