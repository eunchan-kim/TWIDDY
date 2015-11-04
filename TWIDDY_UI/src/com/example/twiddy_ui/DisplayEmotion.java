package com.example.twiddy_ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class DisplayEmotion  extends Activity implements OnClickListener{
	
	Emotion emotion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
		
		Button btn_smile = (Button)findViewById(R.id.btn_smile);
		Button btn_angry = (Button)findViewById(R.id.btn_angry);
		btn_smile.setOnClickListener(this);
		btn_angry.setOnClickListener(this);
		
		emotion = new Emotion(this);		
		FrameLayout frame = (FrameLayout)findViewById(R.id.layout_display);
		frame.addView(emotion, 0);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_smile:
			emotion.changeEmotion(EnumEmotion.Happy);
			break;
		case R.id.btn_angry:
			emotion.changeEmotion(EnumEmotion.Angry);
			break;
		}
	}
	
	
	protected class Emotion extends View{
		Thread animator = null;
		Paint pnt;
		DisplayMetrics displayMetrics;
		int deviceWidth, deviceHeight;
		int smileStart, angle, angryStart;
		int radius, eyeRadius;
		RectF rect;
		EnumEmotion currentEmotion;
		
		public Emotion(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			pnt = new Paint();
			displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);			 
            deviceWidth = displayMetrics.widthPixels;
            deviceHeight = displayMetrics.heightPixels;
            radius = deviceWidth/4;
            eyeRadius = radius/6;
            rect = new RectF();
            currentEmotion = EnumEmotion.Normal;
            smileStart = 30;
            angryStart = 210;
            angle = 120;    
            postInvalidate();
		}
		
		public void changeEmotion(EnumEmotion e)
		{
			currentEmotion = e;
			postInvalidate();
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);
            switch(currentEmotion)
            {
            case Normal:
            	canvas.drawColor(Color.WHITE);
            	break;
            case Happy:
            	canvas.drawColor(Color.GREEN);
                break;
            case Angry:
            	canvas.drawColor(Color.RED);
                break;
            }     
			
        	pnt.setStyle(Style.FILL);
            pnt.setColor(Color.YELLOW);
            canvas.drawCircle(deviceWidth/2, deviceHeight/5*2, radius, pnt);
            pnt.setColor(Color.BLACK);
            canvas.drawCircle(deviceWidth/2-eyeRadius*2, deviceHeight/5*2-eyeRadius, eyeRadius, pnt);
            canvas.drawCircle(deviceWidth/2+eyeRadius*2, deviceHeight/5*2-eyeRadius, eyeRadius, pnt);
            pnt.setStrokeWidth(10);
            pnt.setStyle(Style.STROKE);
            
            switch(currentEmotion)
            {
            case Normal:
            	canvas.drawLine(deviceWidth/2-eyeRadius*3, deviceHeight/5*2+eyeRadius*3, 
            			deviceWidth/2+eyeRadius*3, deviceHeight/5*2+eyeRadius*3, pnt);
            	break;
            case Happy:
            	rect.set(deviceWidth/2-eyeRadius*3,deviceHeight/5*2,deviceWidth/2+eyeRadius*3,deviceHeight/5*2+eyeRadius*3);
                canvas.drawArc(rect, smileStart, angle, false, pnt);
                break;
            case Angry:
            	rect.set(deviceWidth/2-eyeRadius*3,deviceHeight/5*2+eyeRadius*3,deviceWidth/2+eyeRadius*3,deviceHeight/5*2+eyeRadius*6);
                canvas.drawArc(rect, angryStart, angle, false, pnt);
                break;
            }            
		}	
	}
}
