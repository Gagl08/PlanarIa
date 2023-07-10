package com.example.applimobileplanaire;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageIdentificationParam extends AppCompatActivity {

    private File imagePath;

    private String nomImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_identification);

        // Récupérer les données de l'intent
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("imagePath")) {
            String imagePathString = extras.getString("imagePath");
            if (imagePathString != null) {
                this.imagePath = new File(imagePathString);
            }
        }

        // Initialiser les vues
        ImageView imageView = findViewById(R.id.image_view);
        EditText jourEditText = findViewById(R.id.jour_popup);
        EditText replicatEditText = findViewById(R.id.replicat_popup);
        EditText jourPostExpoEditText = findViewById(R.id.jour_post_expo_popup);
        Spinner solutionSpinner = findViewById(R.id.solution_spinner);
        EditText numPlanaireEditText = findViewById(R.id.num_planaire_popup);
        Button sendButton = findViewById(R.id.envoye);
        Button backButton = findViewById(R.id.retour);

        View rootView = findViewById(android.R.id.content);
        rootView.setOnClickListener(v -> {
            closeNumericKeyboard(jourEditText);
            closeNumericKeyboard(replicatEditText);
            closeNumericKeyboard(jourPostExpoEditText);
            closeNumericKeyboard(numPlanaireEditText);
        });

        // Ajouter un écouteur de focus pour chaque champ de texte
        jourEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                closeNumericKeyboard(jourEditText);
            }
        });

        replicatEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                closeNumericKeyboard(replicatEditText);
            }
        });

        jourPostExpoEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                closeNumericKeyboard(jourPostExpoEditText);
            }
        });

        numPlanaireEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                closeNumericKeyboard(numPlanaireEditText);
            }
        });

        // Définir les valeurs des vues
        if (imagePath != null) {
            // Utilisez imageBitmap pour charger l'image dans ImageView
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath.getAbsolutePath());
            nomImage = imagePath.getName();
            imageView.setImageBitmap(bitmap);
        }

        // Définir les options de la liste déroulante
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.solution_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        solutionSpinner.setAdapter(adapter);

        // Définir les actions des boutons
        sendButton.setOnClickListener(v -> {
            // Récupérer les valeurs des champs
            String jour = jourEditText.getText().toString();
            String replicat = replicatEditText.getText().toString();
            String jourPostExpo = jourPostExpoEditText.getText().toString();
            String numPlanaire = numPlanaireEditText.getText().toString();
            String solution = solutionSpinner.getSelectedItem().toString();
            if (solution.equals("Controle")){
                solution="CTRL";
            }

            // Vérifier si les champs sont vides
            if (jour.isEmpty() || replicat.isEmpty() || jourPostExpo.isEmpty() || solution.isEmpty() || numPlanaire.isEmpty()) {
                // Construire le message indiquant les champs vides
                StringBuilder champsVides = new StringBuilder();
                if (jour.isEmpty()) {
                    champsVides.append("Le champ Jour est vide.\n");
                }
                if (replicat.isEmpty()) {
                    champsVides.append("Le champ Réplicat est vide.\n");
                }
                if (jourPostExpo.isEmpty()) {
                    champsVides.append("Le champ Jour Post-Expo est vide.\n");
                }
                if (solution.isEmpty()) {
                    champsVides.append("Le champ Solution est vide.\n");
                }
                if (numPlanaire.isEmpty()) {
                    champsVides.append("Le champ Num Planaire est vide.\n");
                }

                // Afficher la pop-up avec les champs vides
                AlertDialog.Builder builder = new AlertDialog.Builder(ImageIdentificationParam.this);
                builder.setTitle("Champs vides")
                        .setMessage(champsVides.toString())
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                // Déplacer l'image du dossier TempFolder vers le dossier "Images"
                // Obtenir le répertoire parent de TempFolder
                File parentDir = imagePath.getParentFile();
                // Créer un sous-dossier "Images" dans le répertoire parent
                assert parentDir != null;
                File destinationDir = new File(parentDir.getParentFile(), "Images");
                if (!destinationDir.exists()) {
                    destinationDir.mkdirs();
                }

                // Déplacer l'image du dossier TempFolder vers le dossier "Images"
                File imageFile = new File(parentDir, nomImage);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String nouveauNomImage = "S-"+timeStamp+"-T"+jourPostExpo+"-J"+jour+"-R"+replicat+"-"+solution+"-Q"+numPlanaire+".jpg";


                File imageFileRenamed = new File(parentDir, nouveauNomImage);
                imageFile.renameTo(imageFileRenamed);

                File destinationFile = new File(destinationDir, nouveauNomImage);
                System.out.println(destinationDir);
                boolean isMoved = imageFileRenamed.renameTo(destinationFile);
                if (isMoved) {
                    // Le déplacement de l'image a réussi
                    System.out.println("L'image a été déplacée avec succès vers le dossier Images.");
                } else {
                    // Le déplacement de l'image a échoué
                    System.out.println("Échec du déplacement de l'image vers le dossier Images.");
                }

                // Fermer l'activité et renvoyer les résultats à l'activité appelante
                setResult(RESULT_OK);
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fermer l'activité sans renvoyer de résultats
                // Supprimer l'image du dossier "TempFolder"
                File imageFile = new File(imagePath, imagePath.getName());
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
    // Méthode pour fermer le clavier numérique
    private void closeNumericKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}