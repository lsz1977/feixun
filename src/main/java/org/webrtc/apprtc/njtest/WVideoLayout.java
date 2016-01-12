package org.webrtc.apprtc.njtest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import org.webrtc.apprtc.media.WVideoView;

import java.util.HashMap;

/**
 * User: Villain
 * Date: 2014/9/12
 * Time: 13:10
 */
public class WVideoLayout extends LinearLayout {
    private HashMap<String, LinearLayout> mLayVideoMap = new HashMap<String, LinearLayout>();

    public WVideoLayout(Context context) {
        super(context);
    }

    public WVideoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WVideoView getVideoView(String id){
    	WVideoView videoView = null;
    	if(mLayVideoMap.containsKey(id)) {
    		LinearLayout lay = mLayVideoMap.get(id);
    		videoView = (WVideoView) lay.getChildAt(0);
    	}
    	return videoView;
    }
    
    public WVideoView addVideoView(String id, boolean bMirror) {
        WVideoView videoView = null;
        if (mLayVideoMap.containsKey(id)) {
            LinearLayout lay = mLayVideoMap.get(id);
            videoView = (WVideoView) lay.getChildAt(0);
            videoView.getVideoViewRender().setMirror(bMirror);
        } else {
        	videoView = null;
            if (id.equals("local")) {
            	videoView = new WVideoView(getContext());
                videoView.init("local",bMirror,true);
			} else {
				videoView = new WVideoView(getContext());
				videoView.init("remote",bMirror, true);
			}
            
            LayoutParams params = new LayoutParams(dip2px(getContext(), 160), dip2px(getContext(), 120));
            videoView.setLayoutParams(params);

            TextView textView = new TextView(getContext());
            textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setGravity(Gravity.CENTER);
            textView.setText(id);

            LinearLayout lay = new LinearLayout(getContext());
            lay.setOrientation(VERTICAL);
            lay.addView(videoView);
            lay.addView(textView);
            addView(lay);

            mLayVideoMap.put(id, lay);
        }
        return videoView;
    }

    public void removeVideoView(String id) {
        if (mLayVideoMap.containsKey(id)) {
            LinearLayout lay = mLayVideoMap.get(id);
            removeView(lay);
            mLayVideoMap.remove(id);
        }
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
