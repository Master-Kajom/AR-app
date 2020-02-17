package com.example.kajom.arapp;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.AugmentedFace;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.AugmentedFaceNode;

import java.util.Collection;
import java.util.function.Consumer;

public class ARCore extends AppCompatActivity {

    private ModelRenderable modelRenderable;
    private Texture texture;
    private boolean isAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.arcore);

        // Enable AR related functionality on ARCore supported devices only.
        maybeEnableArButton();


    }

    void maybeEnableArButton() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isTransient()) {
            // Re-query at 5Hz while compatibility is checked in the background.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    maybeEnableArButton();
                }
            }, 200);
        }
        if (availability.isSupported()) {
            //mArButton.setVisibility(View.VISIBLE);
            //mArButton.setEnabled(true);
            // indicator on the button.
            //handle AR here
            doaugment();
        } else { // Unsupported or unknown.
            //TODO
            //1. display toast then
            Toast.makeText(getApplicationContext(),"Device does not support AR Core Services",Toast.LENGTH_LONG).show();
            //2. return back to initial activity
            finish();
        }
    }

    public void doaugment() {

        //initialize
        CustomArFragment customArFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        ModelRenderable.builder()
                .setSource(this, R.raw.fox_face)
                .build()
                .thenAccept(new Consumer<ModelRenderable>() {
                    @Override
                    public void accept(ModelRenderable renderable) {
                        modelRenderable = renderable;

                        //hide casted shadows
                        modelRenderable.setShadowCaster(false);
                        modelRenderable.setShadowReceiver(false);
                    }
                });

        Texture.builder()
                .setSource(this,R.drawable.fox_face_mesh_texture)
                .build()
                .thenAccept(texture -> this.texture = texture);

        customArFragment.getArSceneView().setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);

        customArFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {

            if(modelRenderable == null || texture == null)
                return;

            Frame frame = customArFragment.getArSceneView().getArFrame();

            //track face and get it
            Collection<AugmentedFace> augmentedFaces = frame.getUpdatedTrackables(AugmentedFace.class);

            for(AugmentedFace augmentedFace : augmentedFaces){
                if(isAdded) return;

                AugmentedFaceNode augmentedFaceNode = new AugmentedFaceNode(augmentedFace);
                augmentedFaceNode.setParent(customArFragment.getArSceneView().getScene());
                augmentedFaceNode.setFaceRegionsRenderable(modelRenderable);
                augmentedFaceNode.setFaceMeshTexture(texture);

                isAdded = true;
            }

        });

    }
}
