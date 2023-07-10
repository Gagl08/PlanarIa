package com.example.applimobileplanaire;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;

public class ImageFullScreen extends AppCompatActivity {

    Intent resultIntent = new Intent();
    private String imagePath;
    private Bitmap imageBitmap;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_full_screen);

        // Récupérer le chemin de l'image depuis l'intent
        imagePath = getIntent().getStringExtra("imagePath");

        if (imagePath != null) {
            imageBitmap = BitmapFactory.decodeFile(imagePath);

            imageView = findViewById(R.id.image_full);
            imageView.setImageBitmap(imageBitmap);
        }

        ImageButton rotationGaucheButton = findViewById(R.id.rotation_gauche);
        rotationGaucheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage(-90);
            }
        });

        ImageButton rotationDroitButton = findViewById(R.id.rotation_droit);
        rotationDroitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage(90);
            }
        });

        Button validerButton = findViewById(R.id.valider_rota);
        validerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRotatedImage();
            }
        });
    }

    private void rotateImage(float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
        imageView.setImageBitmap(imageBitmap);
    }

    private void saveRotatedImage() {
        // Enregistrer l'image avec les modifications de rotation ici
        try {
            FileOutputStream outputStream = new FileOutputStream(imagePath);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Vous pouvez également envoyer un résultat à une autre activité si nécessaire
        setResult(RESULT_OK,resultIntent);
        // Terminer l'activité
        finish();
    }
}