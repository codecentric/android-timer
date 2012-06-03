package de.codecentric.android.timer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class PieChartView extends View {

	private static final String TAG = PieChartView.class.getName();

	private static final int RADIUS = 300;
	private static final int BACKGROUND_COLOR = 0x50ff0000;
	private static final int FOREGROUND_COLOR = 0xa0ff0000;

	private static final float STARTING_ANGLE = -90f;
	private static final float FULL_CIRCLE = 360f;

	private ShapeDrawable circle;
	private ShapeDrawable arc;

	private float lastFraction;

	public PieChartView(Context context) {
		super(context);
		this.init();
	}

	public PieChartView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.init();
	}

	private void init() {
		this.circle = new ShapeDrawable(new OvalShape());
		this.circle.getPaint().setColor(BACKGROUND_COLOR);
		this.circle.setBounds(0, 0, RADIUS, RADIUS);
		this.lastFraction = 0f;
		this.arc = new ShapeDrawable(new ArcShape(STARTING_ANGLE,
				this.lastFraction));
		this.arc.getPaint().setColor(FOREGROUND_COLOR);
		this.arc.setBounds(0, 0, RADIUS, RADIUS);
	}

	protected void onDraw(Canvas canvas) {
		Log.d(TAG, "onDraw()");
		this.circle.draw(canvas);
		this.arc.draw(canvas);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.setMeasuredDimension(RADIUS, RADIUS);
	}

	public void setFraction(float fraction) {
		Log.v(TAG, "setFraction(" + fraction + ")");
		if (fraction < 0f || fraction > 1.f) {
			throw new IllegalArgumentException("Fraction out of range: "
					+ fraction);
		}
		if (fraction != this.lastFraction) {
			float sweepingAngle = FULL_CIRCLE * fraction;
			this.arc.setShape(new ArcShape(STARTING_ANGLE, sweepingAngle));
			this.postInvalidate();
			this.lastFraction = fraction;
		} else {
			Log.v(TAG, "fraction has not changed, doing nothing");
		}
	}
}
