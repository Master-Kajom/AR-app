package com.example.kajom.arapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import com.example.kajom.arapp.camera.GraphicOverlay;
import com.google.android.gms.vision.face.Contour;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;


/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;

    Bitmap nose;
    Bitmap ears;

    FaceGraphic(GraphicOverlay overlay, Bitmap nose, Bitmap ears) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
        this.nose = nose;
        this.ears = ears;
        //lip = BitmapFactory.decodeResource(getResources(), R.drawable.lip);
    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }


    //TODO
    //1. load the image asset put in the resource folder ie the lip and the ears and nose one
    //2. Draw them on the graphic overlay and it should work

    //start with the mouth ..... then after you've understood put the ears and nose

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {

        Face face = mFace;
        if (face == null) {
            return;
        }

        float face_height = face.getHeight();
        float face_width = face.getWidth();

        //get the height and width of the canvas
        int canvas_width = canvas.getWidth();
        int canvas_height = canvas.getHeight();

        int idealWidth = (int) (face_width/2.2);
        int idealHeight = (int) (face_height/2.2);
        Bitmap  noseImage = Bitmap.createScaledBitmap(this.nose, idealWidth, idealHeight, false);
        int ear_width = this.ears.getWidth();
        int ear_height = this.ears.getHeight();
        Bitmap  earsImage = Bitmap.createScaledBitmap(this.ears,(int) (idealWidth*2), (int)((idealHeight*2)/2.4), false);



        float x =0;
        float y =0;

        float left_eye_x =0;
        float right_eye_x = 0;
        float left_eye_y=0;
        float right_eye_y=0;
        float nose_base_y = 0;
        float bottom_mouth_y = 0;

        List<Landmark> landmarkList = face.getLandmarks();
        for(Landmark landmark:landmarkList){
            x = translateX(landmark.getPosition().x);
            y = translateY(landmark.getPosition().y);
            if(landmark.getType() == Landmark.LEFT_EYE){
                left_eye_x = x;
                left_eye_y = y;
                //draw the circle here
                //canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
            }
            if(landmark.getType() == Landmark.RIGHT_EYE){
                right_eye_x = x;
                right_eye_y = y;
                //draw the circle here
                //canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
            }
            if(landmark.getType() == Landmark.NOSE_BASE){
                //draw the circle here
                nose_base_y = y;
                //canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
                canvas.drawBitmap(noseImage,(int)(x-(noseImage.getWidth()/2.2)),(int) (y-(noseImage.getHeight()/1.4)),null);
            }
            if(landmark.getType() == Landmark.LEFT_EAR_TIP){
                //draw the circle here
                //canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
            }
            if(landmark.getType() == Landmark.RIGHT_EAR_TIP){
                //draw the circle here
                //canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
            }
            if(landmark.getType() == Landmark.BOTTOM_MOUTH){
                bottom_mouth_y = y;
                //draw the circle here
                //canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
            }

        }

        //draw circle at the centre of your ears
        float middle_eye_y = (right_eye_y +((left_eye_y - right_eye_y)/2));
        float forehead_y = (float) (middle_eye_y + ((middle_eye_y - nose_base_y)*1.25));
        float middle_eye_x = right_eye_x +((left_eye_x-right_eye_x)/2);
        //canvas.drawCircle(middle_eye_x, forehead_y, FACE_POSITION_RADIUS, mFacePositionPaint);
        canvas.drawBitmap(earsImage,(int)(middle_eye_x-(earsImage.getWidth()/2)),(int) (forehead_y-(earsImage.getHeight())),null);
    }
}