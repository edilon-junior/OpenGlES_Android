package com.example.openglexemple;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MySurfaceView extends GLSurfaceView {
        private MyRenderer mRenderer;

        // Offsets for touch events
        private float mPreviousX;
        private float mPreviousY;

        private float mDensity;

	public MySurfaceView(Context context)
        {
            super(context);
        }

	public MySurfaceView(Context context, AttributeSet attrs)
        {
            super(context, attrs);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            if (event != null)
            {
                float x = event.getX();
                float y = event.getY();

                if (event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    if (mRenderer != null)
                    {
                        float mdeltaX = (x - mPreviousX) / mDensity / 2f;
                        float mdeltaY = (y - mPreviousY) / mDensity / 2f;

                        mRenderer.deltaX += mdeltaX;
                        mRenderer.deltaY += mdeltaY;
                    }
                }

                mPreviousX = x;
                mPreviousY = y;

                return true;
            }
            else
            {
                return super.onTouchEvent(event);
            }
        }

        // Hides superclass method.
        public void setRenderer(MyRenderer renderer, float density)
        {
            mRenderer = renderer;
            mDensity = density;
            super.setRenderer(renderer);
        }
}

