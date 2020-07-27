package com.zongzhaopeng.guessmusic.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.zongzhaopeng.guessmusic.R;

/**
 * Created by zhaopengzong on 2017/2/18.
 */
public class AllPassView extends Activity
{
    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.all_pass);

        // hide the coin button
        FrameLayout view = (FrameLayout) findViewById(R.id.layout_bar_coin);
        view.setVisibility(View.INVISIBLE);
    }
}
