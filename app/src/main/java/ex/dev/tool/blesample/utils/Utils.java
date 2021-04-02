package ex.dev.tool.blesample.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import ex.dev.tool.blesample.R;

public class Utils
{
    private static Utils utils;

    public static Utils getInstance()
    {
        if(utils == null)
            utils = new Utils();

        return utils;
    }

    public void showToast(final Activity activity, final String msg, final boolean isLong)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, msg, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
            }
        });
    }

    public void showDialog(Context context, String title, String msg)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
