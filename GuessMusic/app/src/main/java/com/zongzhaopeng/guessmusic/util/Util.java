package com.zongzhaopeng.guessmusic.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zongzhaopeng.guessmusic.R;
import com.zongzhaopeng.guessmusic.model.IAlertDialogButtonListener;

/**
 * Created by zhaopengzong on 2017/1/8.
 */
public class Util
{
    public static View getView(Context context,int layoutId)
    {
        LayoutInflater inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(layoutId, null);
        return layout;
    }

    // interface change
    public static void startActivity(Context context, Class destination)
    {
        Intent intent = new Intent();
        intent.setClass(context, destination);
        context.startActivity(intent);

        // close current activity
        ((Activity)context).finish();
    }


    private static AlertDialog mAlertDialog;
    // show custom dialog
    public static void showDialog(final Context context, String message,
                                  final IAlertDialogButtonListener listener)
    {
        View dialogView = getView(context, R.layout.dialog_view);

        AlertDialog.Builder builder = new AlertDialog.Builder(context,
                R.style.Theme_Transparent);

        // image button
        ImageButton ibOK = (ImageButton) dialogView.findViewById(R.id.ib_dialog_ok);
        ImageButton ibCancel = (ImageButton) dialogView.findViewById(R.id.ib_dialog_cancel);

        // text message
        TextView txtMessage = (TextView) dialogView.findViewById(R.id.txt_dialog_message);
        txtMessage.setText(message);

        ibOK.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // close dialog
                if (mAlertDialog != null)
                {
                    mAlertDialog.cancel();
                }

                // event callback
                if (listener != null)
                {
                    listener.onClick();
                }

                // play sound
                MyPlayer.playSound(context, MyPlayer.INDEX_SOUND_ENTER);
            }
        });

        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // close dialog
                if (mAlertDialog != null)
                {
                    mAlertDialog.cancel();
                }

                // play sound
                MyPlayer.playSound(context, MyPlayer.INDEX_SOUND_CANCEL);
                //MyLog.d("myCancel",MyPlayer.INDEX_SOUND_CANCEL+"");
            }
        });

        // set view for dialog
        builder.setView(dialogView);
        mAlertDialog = builder.create();

        // show dialog
        mAlertDialog.show();
    }
}
