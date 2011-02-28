package cn.kyle.test.TarSystem;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;

public class Helper {
	public final static String SCRIPT_NAME = "surunner.sh";

	public static Process runSuCommandAsync(Context context, String command) throws IOException
	{
		DataOutputStream fout = new DataOutputStream(context.openFileOutput(SCRIPT_NAME, 0));
		fout.writeBytes(command);
		fout.close();
		
		String[] args = new String[] { "su", "-c", ". " + context.getFilesDir().getAbsolutePath() + "/" + SCRIPT_NAME };
		Process proc = Runtime.getRuntime().exec(args);
		return proc;
	}

	public static int runSuCommand(Context context, String command) throws IOException, InterruptedException
	{
		return runSuCommandAsync(context, command).waitFor();
	}
	
	public static int runSuCommandNoScriptWrapper(Context context, String command) throws IOException, InterruptedException
	{
		String[] args = new String[] { "su", "-c", command };
		Process proc = Runtime.getRuntime().exec(args);
		return proc.waitFor();
	}

	public static boolean hasBusyBox(Context context){
		return new File(context.getFilesDir().getAbsolutePath(),"busybox").exists();
	}
	
	public static String warpCmdWithBusybox(Context context, String cmd){
		String busybox = new File(context.getFilesDir().getAbsolutePath(),"busybox").getAbsolutePath();
		return busybox + " " + cmd;
	}
	
	public static void unpackFile(Context context, String filename, String chmod){
		if (chmod==null || chmod.trim().equals("")){
			chmod = "777";
		}
		String dir = "files/";
		File file = new File(context.getFilesDir().getAbsolutePath(),filename);
		int count=0;
		byte[] bs = new byte[10240];
		try {
			InputStream in = context.getResources().getAssets().open(dir+filename);
			FileOutputStream out = new FileOutputStream(file,false);
			while((count = in.read(bs))>=0){
				out.write(bs,0,count);
			}
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			runSuCommand(context,"chmod "+chmod+" "+file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
