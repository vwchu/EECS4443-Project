/*
 * ======================================================================
 * Copyright © 2014 Qualcomm Technologies, Inc. All Rights Reserved.
 * QTI Proprietary and Confidential.
 * =====================================================================
 * @file: DrawView.java
 */

package ca.yorku.eecs.cse13261.eecs4443project;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import com.qualcomm.snapdragon.sdk.face.*;

public class DrawView extends View {

    private final Paint arrowBrush     = new Paint();
    private final Paint leftEyeBrush   = new Paint();
    private final Paint rightEyeBrush  = new Paint();
    private final Paint eyeDistBrush   = new Paint();
    private final Paint zoomBrush      = new Paint();
    private final int   color          = Color.RED;

    public FaceData[] faces;
    public String zoom;
    public Point direction;
    public Path path = new Path();

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        arrowBrush.setColor(Color.argb(127, 255, 0, 0));
        leftEyeBrush.setColor(color);
        rightEyeBrush.setColor(color);
        eyeDistBrush.setColor(color);
        zoomBrush.setColor(color);
        eyeDistBrush.setStyle(Paint.Style.STROKE);
        zoomBrush.setTextSize(60);
    }

    public void updateView(FaceData[] faceArray, String zoomText, Point dirPoint) {
        faces = faceArray;
        zoom = zoomText;
        direction = dirPoint;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);

        if (direction != null) {
            if (direction.x < 0) {
                path.reset();
                path.setLastPoint(5, canvas.getHeight() / 2);
                path.lineTo(55, canvas.getHeight() / 2 + 50);
                path.lineTo(55, canvas.getHeight() / 2 - 50);
                path.close();
                canvas.drawPath(path, arrowBrush);
            } else if (direction.x > 0) {
                path.reset();
                path.setLastPoint(canvas.getWidth() - 5, canvas.getHeight() / 2);
                path.lineTo(canvas.getWidth() - 55, canvas.getHeight() / 2 + 50);
                path.lineTo(canvas.getWidth() - 55, canvas.getHeight() / 2 - 50);
                path.close();
                canvas.drawPath(path, arrowBrush);
            }
            if (direction.y < 0) {
                path.reset();
                path.setLastPoint(canvas.getWidth() / 2, canvas.getHeight() - 5);
                path.lineTo(canvas.getWidth() / 2 + 50, canvas.getHeight() - 55);
                path.lineTo(canvas.getWidth() / 2 - 50, canvas.getHeight() - 55);
                path.close();
                canvas.drawPath(path, arrowBrush);
            } else if (direction.y > 0) {
                path.reset();
                path.setLastPoint(canvas.getWidth() / 2, 5);
                path.lineTo(canvas.getWidth() / 2 + 50, 55);
                path.lineTo(canvas.getWidth() / 2 - 50, 55);
                path.close();
                canvas.drawPath(path, arrowBrush);
            }
        }

        if (faces != null) {
            for (FaceData face : faces) {
                if (face.leftEye != null) {
                    canvas.drawCircle(face.leftEye.x, face.leftEye.y, 10f, leftEyeBrush);
                    canvas.drawCircle(face.rightEye.x, face.rightEye.y, 10f, rightEyeBrush);
                    canvas.drawCircle((face.leftEye.x + face.rightEye.x) / 2f,
                                      (face.leftEye.y + face.rightEye.y) / 2f, 20f, leftEyeBrush);
                    canvas.drawLine(face.leftEye.x,  face.leftEye.y,
                                    face.rightEye.x, face.rightEye.y, eyeDistBrush);
                    if (zoom != null) {
                        canvas.drawText(zoom, (face.leftEye.x + face.rightEye.x) / 2f - 10,
                                              (face.leftEye.y + face.rightEye.y) / 2f - 60, zoomBrush);
                    }
                }
            } // for each face
        }
    } // onDraw
} // DrawView
