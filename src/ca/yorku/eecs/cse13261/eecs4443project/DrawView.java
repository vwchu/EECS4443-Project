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
import android.graphics.PorterDuff.Mode;
import android.util.*;
import android.view.*;
import com.qualcomm.snapdragon.sdk.face.*;

public class DrawView extends View {

    private final Paint leftEyeBrush   = new Paint();
    private final Paint rightEyeBrush  = new Paint();
    private final Paint mouthBrush     = new Paint();
    private final Paint rectBrush      = new Paint();
    private final Paint faceBrush      = new Paint();
    private final Paint eyeDistBrush   = new Paint();
    private final Paint zoomBrush      = new Paint();
    private final Paint countDownBrush = new Paint();
    
    public FaceData[] faces;
    public String zoom;
    public String countdown;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        leftEyeBrush.setColor(Color.RED);
        rightEyeBrush.setColor(Color.RED); // GREEN
        mouthBrush.setColor(Color.WHITE);
        faceBrush.setColor(Color.CYAN);
        rectBrush.setColor(Color.YELLOW);
        eyeDistBrush.setColor(Color.RED);
        zoomBrush.setColor(Color.RED);
        countDownBrush.setColor(Color.argb(127, 255, 0, 0));
        
        rectBrush.setStyle(Paint.Style.STROKE);
        eyeDistBrush.setStyle(Paint.Style.STROKE);
        zoomBrush.setTextSize(40);
        countDownBrush.setTextSize(150);
    }

    public void updateView(FaceData[] faceArray, String zoomText, String countdownText) {
        faces = faceArray;
        zoom = zoomText;
        countdown = countdownText;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.drawColor(0, Mode.CLEAR);
        if (countdown != null) {
            canvas.drawText(countdown, 
                    getWidth() / 2f - 150 / 2, 
                    getHeight() / 2f - 150 / 2, countDownBrush);
        }
        if (faces != null) {
            for (FaceData face : faces) {
                if (face.leftEye != null) {
                    
                    canvas.drawCircle(face.leftEye.x, face.leftEye.y, 10f, leftEyeBrush);
                    canvas.drawCircle(face.rightEye.x, face.rightEye.y, 10f, rightEyeBrush);
                    //canvas.drawCircle(face.mouth.x, face.mouth.y, 5f, mouthBrush);

                    canvas.drawCircle((face.leftEye.x + face.rightEye.x) / 2f,
                                      (face.leftEye.y + face.rightEye.y) / 2f, 20f, leftEyeBrush);
                    canvas.drawLine(face.leftEye.x,  face.leftEye.y,
                                    face.rightEye.x, face.rightEye.y, eyeDistBrush);

                    if (zoom != null) {
                        canvas.drawText(zoom, (face.leftEye.x + face.rightEye.x) / 2f - 10,
                                              (face.leftEye.y + face.rightEye.y) / 2f - 40, zoomBrush);
                    }
                    
                }
                //if (face.leftEyeObj != null) {
                //    canvas.drawCircle(face.mouthObj.left.x, face.mouthObj.left.y, 5f, faceBrush);
                //    canvas.drawCircle(face.mouthObj.right.x, face.mouthObj.right.y, 5f, faceBrush);
                //    canvas.drawCircle(face.mouthObj.upperLipTop.x, face.mouthObj.upperLipTop.y, 5f, faceBrush);
                //    canvas.drawCircle(face.mouthObj.upperLipBottom.x, face.mouthObj.upperLipBottom.y, 5f, faceBrush);
                //    canvas.drawCircle(face.mouthObj.lowerLipTop.x, face.mouthObj.lowerLipTop.y, 5f, faceBrush);
                //    canvas.drawCircle(face.mouthObj.lowerLipBottom.x, face.mouthObj.lowerLipBottom.y, 5f, faceBrush);
                //
                //    canvas.drawCircle(face.leftEyebrow.left.x, face.leftEyebrow.left.y, 5f, faceBrush);
                //    canvas.drawCircle(face.leftEyebrow.right.x, face.leftEyebrow.right.y, 5f, faceBrush);
                //    canvas.drawCircle(face.leftEyebrow.top.x, face.leftEyebrow.top.y, 5f, faceBrush);
                //    canvas.drawCircle(face.leftEyebrow.bottom.x, face.leftEyebrow.bottom.y, 5f, faceBrush);
                //
                //    canvas.drawCircle(face.rightEyebrow.left.x, face.rightEyebrow.left.y, 5f, faceBrush);
                //    canvas.drawCircle(face.rightEyebrow.right.x, face.rightEyebrow.right.y, 5f, faceBrush);
                //    canvas.drawCircle(face.rightEyebrow.top.x, face.rightEyebrow.top.y, 5f, faceBrush);
                //    canvas.drawCircle(face.rightEyebrow.bottom.x, face.rightEyebrow.bottom.y, 5f, faceBrush);
                //
                //    canvas.drawCircle(face.leftEar.top.x, face.leftEar.top.y, 5f, faceBrush);
                //    canvas.drawCircle(face.leftEar.bottom.x, face.leftEar.bottom.y, 5f, faceBrush);
                //    canvas.drawCircle(face.rightEar.top.x, face.rightEar.top.y, 5f,mouthBrush);
                //    canvas.drawCircle(face.rightEar.bottom.x, face.rightEar.bottom.y, 5f, faceBrush);
                //
                //    canvas.drawCircle(face.leftEyeObj.left.x, face.leftEyeObj.left.y, 5f, faceBrush);
                //    canvas.drawCircle(face.leftEyeObj.right.x, face.leftEyeObj.right.y, 5f, faceBrush);
                //    canvas.drawCircle(face.leftEyeObj.top.x, face.leftEyeObj.top.y,5f, faceBrush);
                //    canvas.drawCircle(face.leftEyeObj.bottom.x, face.leftEyeObj.bottom.y, 5f, faceBrush);
                //    canvas.drawCircle(face.leftEyeObj.centerPupil.x, face.leftEyeObj.centerPupil.y, 5f, faceBrush);
                //
                //    canvas.drawCircle(face.rightEyeObj.left.x, face.rightEyeObj.left.y, 5f, faceBrush);
                //    canvas.drawCircle(face.rightEyeObj.right.x, face.rightEyeObj.right.y, 5f, faceBrush);
                //    canvas.drawCircle(face.rightEyeObj.top.x, face.rightEyeObj.top.y, 5f, faceBrush);
                //    canvas.drawCircle(face.rightEyeObj.bottom.x, face.rightEyeObj.bottom.y, 5f, faceBrush);
                //    canvas.drawCircle(face.rightEyeObj.centerPupil.x,face.rightEyeObj.centerPupil.y, 5f, faceBrush);
                //
                //    canvas.drawCircle(face.chin.left.x, face.chin.left.y, 5f, faceBrush);
                //    canvas.drawCircle(face.chin.right.x, face.chin.right.y, 5f, faceBrush);
                //    canvas.drawCircle(face.chin.center.x, face.chin.center.y, 5f, faceBrush);
                //
                //    canvas.drawCircle(face.nose.noseBridge.x, face.nose.noseBridge.y, 5f, faceBrush);
                //    canvas.drawCircle(face.nose.noseCenter.x, face.nose.noseCenter.y, 5f, faceBrush);
                //    canvas.drawCircle(face.nose.noseTip.x, face.nose.noseTip.y, 5f, faceBrush);
                //    canvas.drawCircle(face.nose.noseLowerLeft.x, face.nose.noseLowerLeft.y, 5f, faceBrush);
                //    canvas.drawCircle(face.nose.noseLowerRight.x, face.nose.noseLowerRight.y, 5f, faceBrush);
                //    canvas.drawCircle(face.nose.noseMiddleLeft.x, face.nose.noseMiddleLeft.y, 5f, faceBrush);
                //    canvas.drawCircle(face.nose.noseMiddleRight.x, face.nose.noseMiddleRight.y, 5f, faceBrush);
                //    canvas.drawCircle(face.nose.noseUpperLeft.x, face.nose.noseUpperLeft.y, 5f, faceBrush);
                //    canvas.drawCircle(face.nose.noseUpperRight.x, face.nose.noseUpperRight.y, 5f, faceBrush);
                //}
                //canvas.drawRect(face.rect.left, face.rect.top, face.rect.right, face.rect.bottom, rectBrush);
            }
        }
    }
}
