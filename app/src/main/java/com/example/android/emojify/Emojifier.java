/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import timber.log.Timber;

class Emojifier {
    private static final String LOG_TAG = Emojifier.class.getSimpleName();
    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;

    static void detectFaces(Context context, Bitmap picture){
        // create Face detector, disable tracking and enable classification
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        // build the frame
        Frame frame = new Frame.Builder().setBitmap(picture).build();

        // detect the faces
        SparseArray<Face> faces = detector.detect(frame);

        // log the number of faces
        Log.d(LOG_TAG, "detectFaces: number of faces = " + faces.size());

        // If no faces detected, show a toast message
        if(faces.size()==0){
            Toast.makeText(context, R.string.no_faces_message, Toast.LENGTH_SHORT).show();
        }

        // release the detector
        detector.release();

    }

    /**
     * Method for logging the classification probabilities
     *
     * @param face the face to for which you pick an emoji
     * */
    private static void whichEmoji(Face face){
        Log.d(LOG_TAG, "whichEmoji: similingProb = "+ face.getIsSmilingProbability());
        Log.d(LOG_TAG, "whichEmoji: leftEyeOpenProb ="+face.getIsLeftEyeOpenProbability());
        Log.d(LOG_TAG, "whichEmoji: rightEyeOpenProb = "+ face.getIsRightEyeOpenProbability());

        // track the state of facial expression
        boolean smiling = face.getIsSmilingProbability() > SMILING_PROB_THRESHOLD;
        boolean leftEyeClosed = face.getIsLeftEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;
        boolean rightEyeClosed = face.getIsRightEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;

        // determine and log the appropriate emoji
        Emoji emoji ;

        if (smiling){
            if (leftEyeClosed && !rightEyeClosed){
                emoji = Emoji.LEFT_WINK;
            } else if (rightEyeClosed && ! leftEyeClosed) {
                emoji = Emoji.RIGHT_WINK;
            } else if (leftEyeClosed){
                emoji = Emoji.CLOSED_EYE_SMILE;
            } else {emoji = Emoji.SMILE;}
        } else {
            if (leftEyeClosed && !rightEyeClosed){
                emoji = Emoji.LEFT_WINK_FROWN;
            } else if (rightEyeClosed && ! leftEyeClosed) {
                emoji = Emoji.RIGHT_WINK_FROWN;
            } else if (leftEyeClosed){
                emoji = Emoji.CLOSED_EYE_FROWN;
            } else {emoji = Emoji.FROWN;}
        }

        // log the chosen Emoji
        Log.d(LOG_TAG, "whichEmoji: "+ emoji.name());

    }

    // Enum for all possible Emojis
    private enum Emoji{
        SMILE,
        FROWN,
        LEFT_WINK,
        RIGHT_WINK,
        LEFT_WINK_FROWN,
        RIGHT_WINK_FROWN,
        CLOSED_EYE_SMILE,
        CLOSED_EYE_FROWN
    }
}
