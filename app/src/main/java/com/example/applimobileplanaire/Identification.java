package com.example.applimobileplanaire;

import static android.app.Activity.RESULT_CANCELED;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Identification extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 200;
    private static final int REQUEST_IMAGE_PICK = 300;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 400;

    private Intent mLastIntentWithData;
    public String NomImageActuelle;
    public File dossier;

    /**
     * classe principale de la création de view
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_identification, container, false);

        ImageView imgView = view.findViewById(R.id.imgView);
        imgView.setImageResource(R.drawable.logo_web);

        Button cameraButton = view.findViewById(R.id.prendre_photo);
        cameraButton.setOnClickListener(v -> launchCamera());

        Button galleryButton = view.findViewById(R.id.acceder_galerie);
        galleryButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });
        ImageButton boutonParam = view.findViewById(R.id.bouton_param);
        boutonParam.setOnClickListener(v -> afficherPopup());

        return view;
    }

    /**
     * Pop up des parametre
     */
    private void afficherPopup() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View popupView = inflater.inflate(R.layout.pop_up_param, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(popupView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Lancement de la caméra
     */
    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * Gestion des reponse de la camera et de la galerie
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLastIntentWithData = data;

        if (resultCode != RESULT_CANCELED) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                handleImageCapture(data);
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImageUri = data.getData();
                Bitmap selectedBitmap = decodeUri(selectedImageUri);

                if (selectedBitmap != null) {
                    saveImageToTempFolder(selectedBitmap);
                }
            }
        }
    }

    /**
     * création d'une image une foi capturée
     */
    private void handleImageCapture(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            saveImageToTempFolder(imageBitmap);
        }
    }

    /**
     * Gestion des permissions
     * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // La permission d'écrire sur le stockage externe a été accordée
                if (mLastIntentWithData != null) {
                    handleImageCapture(mLastIntentWithData);
                }
            }  // La permission d'écrire sur le stockage externe a été refusée
            // Gérer le cas où l'utilisateur a refusé la permission

        }
    }

    /**
     * utilisation des images issus de la galerie
     */
    private Bitmap decodeUri(Uri selectedImageUri) {
        try {
            // Chargement de l'image sans effectuer de redimensionnement ou de compression
            return BitmapFactory.decodeStream(requireActivity().getContentResolver().openInputStream(selectedImageUri), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * sauvegarde dans un dossier temporaire
     */
    private void saveImageToTempFolder(Bitmap imageBitmap) {
        File storageDir = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "TempFolder");
        dossier = storageDir;
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        String imageFileName = "IMG_en_cours.jpg";
        NomImageActuelle = imageFileName;
        File imageFile = new File(storageDir, imageFileName);
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            if (imageBitmap != null) {
                // Écriture des données de l'image sans compression
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.write(getBytesFromBitmap(imageBitmap));
                showImageDialog();
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * class de la pop up de validation
     */
    private void showImageDialog() {
        File imageFile = new File(dossier, NomImageActuelle);
        Intent intent = new Intent(getActivity(), ImageIdentificationParam.class);
        intent.putExtra("imagePath", imageFile.getAbsolutePath());
        startActivityForResult(intent, 1);
    }
}
