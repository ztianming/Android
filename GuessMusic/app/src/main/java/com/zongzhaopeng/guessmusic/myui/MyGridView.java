package com.zongzhaopeng.guessmusic.myui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.zongzhaopeng.guessmusic.R;
import com.zongzhaopeng.guessmusic.model.IWordButtonClickListener;
import com.zongzhaopeng.guessmusic.model.WordButton;
import com.zongzhaopeng.guessmusic.util.Util;

import java.util.ArrayList;

/**
 * Created by zhaopengzong on 2017/1/8.
 */
public class MyGridView extends GridView
{
    private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();

    private MyGridAdapter myGridAdapter;

    private Context mContext;

    public static final int COUNT_WORDS = 24;

    // animation
    private Animation mScaleAnimation;

    // listener
    private IWordButtonClickListener mWordButtonListener;

    public MyGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        myGridAdapter = new MyGridAdapter();
        this.setAdapter(myGridAdapter);
        mContext = context;
    }

    public void updateData(ArrayList<WordButton> list)
    {
        mArrayList = list;

        // set the data
        setAdapter(myGridAdapter);
    }

    // adapter
    class MyGridAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return mArrayList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return mArrayList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            final WordButton holder;

            if (view == null)
            {
                view = Util.getView(mContext, R.layout.ui_gridview_item);

                // load animation
                mScaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale);
                // set delay time
                mScaleAnimation.setStartOffset(position * 100);

                // set word button
                holder = mArrayList.get(position);
                holder.mIndex = position;
                holder.mViewButton = (Button) view.findViewById(R.id.item_btn);

                // click listener
                holder.mViewButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mWordButtonListener.onWordButtonClick(holder);
                    }
                });

                view.setTag(holder);
            }
            else
            {
                holder = (WordButton) view.getTag();
            }
            holder.mViewButton.setText(holder.mWordString);

            // play animation
            view.startAnimation(mScaleAnimation);
            return view;
        }
    }

    // register listener interface
    public void registerOnWordButtonClick(IWordButtonClickListener listener)
    {
        mWordButtonListener = listener;
    }
}
