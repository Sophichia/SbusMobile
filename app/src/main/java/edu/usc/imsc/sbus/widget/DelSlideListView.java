package edu.usc.imsc.sbus.widget;

/**
 * Created by Mengjia on 16/1/2.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import edu.usc.imsc.sbus.R;
import edu.usc.imsc.sbus.service.ListViewOnSingleTapUpListenner;
import edu.usc.imsc.sbus.service.OnDeleteListener;

public class DelSlideListView extends ListView implements
        GestureDetector.OnGestureListener, View.OnTouchListener{
    private GestureDetector mDectector;
    private OnDeleteListener mOnDeleteListener;
    private int position;
    private float velocityX, velocityY;
    private ListViewOnSingleTapUpListenner thisOnSingleTapUpListenner;

    private int standard_touch_target_size = 0;
    private float mLastMontionX;
    public boolean deleteView = false;
    private ScrollLinearLayout mSrollLinerLayout;
    private boolean scroll = false;
    private int pointToPostition;
    private boolean listViewMoving;
    private boolean delAll = false;
    public boolean isLongPress = false;

    public DelSlideListView(Context context){
        super(context);
        init(context);
    }

    public DelSlideListView(Context context, AttributeSet att){
        super(context,att);
        init(context);
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener){
        this.mOnDeleteListener = onDeleteListener;
    }

    public void setSingleTapUpListener(ListViewOnSingleTapUpListenner onSingleTapUpListenner){
        this.thisOnSingleTapUpListenner = onSingleTapUpListenner;
    }

    public boolean isDelAll(){
        return delAll;
    }

    public void setDelAll(boolean delAll){
        this.delAll = delAll;
    }

    private void init(Context context){
        mDectector = new GestureDetector(context,this);
        mDectector.setIsLongpressEnabled(false);
        standard_touch_target_size = (int) getResources().getDimension(R.dimen.delete_action_len);
        this.setOnTouchListener(this);
    }
    @Override
    public boolean onDown(MotionEvent e){
        if(thisOnSingleTapUpListenner != null){
            thisOnSingleTapUpListenner.onSingleTapUp();
        }
        mLastMontionX = e.getX();
        pointToPostition = this.pointToPosition((int)e.getX(),(int)e.getY());
        final int p = pointToPostition - this.getFirstVisiblePosition();
        if(mSrollLinerLayout != null) {
            mSrollLinerLayout.onDown();
            mSrollLinerLayout.setSingleTapUp(true);
        }
        if(deleteView && p!= position){
            deleteView = false;
            if(mSrollLinerLayout != null){
                mSrollLinerLayout.snapToScreen(0);
                mSrollLinerLayout.setSingleTapUp(false);
            }
            position = p;
            scroll = false;
            return true;
        }
        isLongPress = false;
        position = p;
        scroll = false;
        listViewMoving = false;
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e){

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY){
        if(listViewMoving && !scroll){
            if(mSrollLinerLayout != null)
                mSrollLinerLayout.snapToScreen(0);
            return false;
        }else if(scroll){
            if(mSrollLinerLayout != null){
                int deltaX = (int) (mLastMontionX - e2.getX());
                if(deleteView){
                    deltaX += standard_touch_target_size;
                }
                if(deltaX>=0 && deltaX <= standard_touch_target_size){
                    mSrollLinerLayout.scrollBy(deltaX-mSrollLinerLayout.getScrollX(),0);
                }
            }
        } else{
            if(Math.abs(distanceX)>Math.abs(distanceY)){
                final int pointToPosition1 = this.pointToPosition((int)e2.getX(),(int)e2.getY());
                final int p1 = pointToPosition1 - this.getFirstVisiblePosition();
                if(p1 == position && mOnDeleteListener.isCanDelete(p1)){
                    mSrollLinerLayout = (ScrollLinearLayout)this.getChildAt(p1);
                    if(mSrollLinerLayout != null){
                        int deltaX = (int) (mLastMontionX - e2.getX());
                        if(deleteView){
                            deltaX += standard_touch_target_size;
                        }
                        if(deltaX >= 0 && deltaX <= standard_touch_target_size && Math.abs(distanceY) <5){
                            isLongPress = true;
                            scroll = true;
                            listViewMoving = false;
                            mSrollLinerLayout.setSingleTapUp(false);
                            mSrollLinerLayout.scrollBy((int)(e1.getX() - e2.getX()), 0);
                        }
                    }
                }
            }
        }
        if(scroll){
            return true;
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e){

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e){
        if(deleteView){
            position = -1;
            deleteView = false;
            mSrollLinerLayout.snapToScreen(0);
            scroll = false;
            return true;
        }
        return false;
    }

    public void setScroll(boolean b){
        listViewMoving = b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        if(scroll || deleteView){
            return true;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        if(isDelAll()){
            return false;
        } else{
            if(event.getAction() == MotionEvent.ACTION_UP
                    ||event.getAction()==MotionEvent.ACTION_CANCEL){
                int deltaX2 = (int)(mLastMontionX - event.getX());
                if(scroll){
                    if(!deleteView&&deltaX2>=standard_touch_target_size/2){
                        position = pointToPostition - this.getFirstVisiblePosition();
                        deleteView = true;
                    } else {
                        position = -1;
                        deleteView = false;
                        mSrollLinerLayout.snapToScreen(0);
                    }
                    scroll = false;
                    return true;
                }
            }
            return mDectector.onTouchEvent(event);
        }
    }

    public void deleteItem(){
        position = -1;
        deleteView = false;
        scroll = false;
        if(mSrollLinerLayout != null){
            mSrollLinerLayout.snapToScreen(0);
        }
    }
}
