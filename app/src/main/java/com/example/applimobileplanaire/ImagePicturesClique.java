package com.example.applimobileplanaire;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImagePicturesClique extends AppCompatActivity {

    private static final int REQUEST_FULLSCREEN_IMAGE = 1;
    private String imagePath;
    private String nomImageTemps;
    private String nomImageJour;
    private String nomImageReplicat ;
    private String nomImageJour_post_expo;
    private String nomImageSolution ;
    private String nomImageNum_planaire ;
    private String descriptionImage;
    private String filePath;
    private boolean descriptionAChange;
    private boolean nomImageAChange;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item);

        descriptionAChange=false;
        nomImageAChange=false;

        // Récupérer le chemin de l'image depuis l'intent
        imagePath = getIntent().getStringExtra("imagePath");

        // Initialiser les variables filesDir et filePath ici
        // Chemin absolu du répertoire "files"
        String filesDir = getExternalFilesDir(null).getAbsolutePath();
        filePath = filesDir + File.separator + "descriptionImage.txt";

        if (imagePath != null) {

            gestionImage();

            Button supprimerButton = findViewById(R.id.supprime);
            supprimerButton.setOnClickListener(v -> showConfirmationDialog());

            TextView jour = findViewById(R.id.jour);
            TextView replicat = findViewById(R.id.replicat);
            TextView jour_post_expo = findViewById(R.id.jour_post_expo);
            TextView solution = findViewById(R.id.solution);
            TextView num_planaire = findViewById(R.id.num_planaire);
            TextView description = findViewById(R.id.description);

            String imageName = getImageNameFromPath(imagePath);

            //Recupération des informations contenu dans le nom de l'image
            final String SEPARATEUR = "-";
            String[] elementNom = imageName.split(SEPARATEUR);
            nomImageTemps=elementNom[1];

            nomImageJour=elementNom[3].substring(1);
            jour.setText(nomImageJour);
            nomImageJour_post_expo=elementNom[2].substring(1);
            jour_post_expo.setText(nomImageJour_post_expo);
            nomImageReplicat=elementNom[4].substring(1);
            replicat.setText(nomImageReplicat);
            nomImageSolution=elementNom[5];
            if (elementNom[5].equals("CTRL")){
                solution.setText("Controle");
            } else {
                solution.setText(nomImageSolution);
            }
            nomImageNum_planaire=elementNom[6].substring(1);
            num_planaire.setText(nomImageNum_planaire);

            System.out.println("1");
            //Recupération de la description dans le fichier "descriptionImage"
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                System.out.println("2");
                String line;
                System.out.println("3");
                while ((line = reader.readLine()) != null) {
                    System.out.println("4");
                    System.out.println(imagePath);
                    if (line.startsWith(imagePath + "@@@")) {
                        System.out.println("5");
                        // Faites quelque chose avec la ligne correspondante

                        final String SEPARATEUR_DESCRIPTION = "@@@";
                        String[] elementDescriptionImage = line.split(SEPARATEUR_DESCRIPTION);
                        description.setText(elementDescriptionImage[1]);
                        System.out.println("Ligne correspondante : " + line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de la lecture du fichier.");
            }

            //Gestion des modifications de l'utilisateur

            //Modification de Jour
            final EditText editJourEditText = findViewById(R.id.edit_jour);
            jour.setOnClickListener(v -> {
                jour.setVisibility(View.GONE);
                editJourEditText.setVisibility(View.VISIBLE);
                editJourEditText.requestFocus(); // Définir le focus sur editJourEditText
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editJourEditText, InputMethodManager.SHOW_IMPLICIT); // Afficher le clavier
            });

            editJourEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String userInput = editJourEditText.getText().toString();
                    jour.setText(userInput);
                    jour.setVisibility(View.VISIBLE);
                    editJourEditText.setVisibility(View.GONE);
                    nomImageJour = userInput;
                    nomImageAChange=true;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editJourEditText.getWindowToken(), 0); // Cacher le clavier
                    return true;
                }
                return false;
            });

            //Modification du replicat
            final EditText editReplicatEditText = findViewById(R.id.edit_replicat);
            replicat.setOnClickListener(v -> {
                replicat.setVisibility(View.GONE);
                editReplicatEditText.setVisibility(View.VISIBLE);
                editReplicatEditText.requestFocus(); // Définir le focus sur editJourEditText
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editReplicatEditText, InputMethodManager.SHOW_IMPLICIT); // Afficher le clavier
            });

            editReplicatEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String userInput = editReplicatEditText.getText().toString();
                    replicat.setText(userInput);
                    replicat.setVisibility(View.VISIBLE);
                    editReplicatEditText.setVisibility(View.GONE);
                    nomImageReplicat = userInput;
                    nomImageAChange=true;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editReplicatEditText.getWindowToken(), 0); // Cacher le clavier
                    return true;
                }
                return false;
            });

            //Modification du jour post exposition
            final EditText editJourPostExpoEditText = findViewById(R.id.edit_jour_post_expo);
            jour_post_expo.setOnClickListener(v -> {
                jour_post_expo.setVisibility(View.GONE);
                editJourPostExpoEditText.setVisibility(View.VISIBLE);
                editJourPostExpoEditText.requestFocus(); // Définir le focus sur editJourEditText
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editJourPostExpoEditText, InputMethodManager.SHOW_IMPLICIT); // Afficher le clavier
            });

            editJourPostExpoEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String userInput = editJourPostExpoEditText.getText().toString();
                    jour_post_expo.setText(userInput);
                    jour_post_expo.setVisibility(View.VISIBLE);
                    editJourPostExpoEditText.setVisibility(View.GONE);
                    nomImageJour_post_expo = userInput;
                    nomImageAChange=true;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editJourPostExpoEditText.getWindowToken(), 0); // Cacher le clavier
                    return true;
                }
                return false;
            });

            //Modification du numéro de planaire
            final EditText editNumPlanaireEditText = findViewById(R.id.edit_num_planaire);
            num_planaire.setOnClickListener(v -> {
                num_planaire.setVisibility(View.GONE);
                editNumPlanaireEditText.setVisibility(View.VISIBLE);
                editNumPlanaireEditText.requestFocus(); // Définir le focus sur editJourEditText
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editNumPlanaireEditText, InputMethodManager.SHOW_IMPLICIT); // Afficher le clavier
            });

            editNumPlanaireEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String userInput = editNumPlanaireEditText.getText().toString();
                    num_planaire.setText(userInput);
                    num_planaire.setVisibility(View.VISIBLE);
                    editNumPlanaireEditText.setVisibility(View.GONE);
                    nomImageNum_planaire = userInput;
                    nomImageAChange=true;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editNumPlanaireEditText.getWindowToken(), 0); // Cacher le clavier
                    return true;
                }
                return false;
            });

            //Modification de la solution
            final Spinner editSolutionSpinner = findViewById(R.id.edit_solution_spinner);

            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.solution_options, android.R.layout.simple_spinner_item);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            editSolutionSpinner.setAdapter(spinnerAdapter);

            solution.setOnClickListener(v -> {
                solution.setVisibility(View.GONE);
                editSolutionSpinner.setVisibility(View.VISIBLE);
            });

            editSolutionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedSolution = parent.getItemAtPosition(position).toString();
                    solution.setText(selectedSolution);
                    solution.setVisibility(View.VISIBLE);
                    editSolutionSpinner.setVisibility(View.GONE);
                    nomImageSolution=selectedSolution;
                    nomImageAChange=true;
                    if (solution.getText().equals("Controle")){
                        nomImageSolution="CTRL";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Aucune action nécessaire lorsque rien n'est sélectionné dans le Spinner
                }
            });

            //Modification de la description
            final EditText editDescriptionEditText = findViewById(R.id.edit_description);
            description.setOnClickListener(v -> {
                description.setVisibility(View.GONE);
                editDescriptionEditText.setVisibility(View.VISIBLE);
                editDescriptionEditText.requestFocus(); // Définir le focus sur editJourEditText
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editDescriptionEditText, InputMethodManager.SHOW_IMPLICIT); // Afficher le clavier
            });

            editDescriptionEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String userInput = editDescriptionEditText.getText().toString();
                    description.setText(userInput);
                    description.setVisibility(View.VISIBLE);
                    editDescriptionEditText.setVisibility(View.GONE);
                    descriptionImage=userInput;
                    descriptionAChange=true;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editDescriptionEditText.getWindowToken(), 0); // Cacher le clavier
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * Sauvegarde de la description entré par l'utilisateur
     * Recupere toutes les lignes du fichier descriptionImage
     * Si une ligne commence par le nom de l'image, la remplace par la nouvelle description
     * Sinon creer la ligne avec la description
     * Réécrit les lignes dans le documents
     */
    private void saveDescription() {
        List<String> lignes = new ArrayList<>();
        boolean foundImageLine = false;

        // Charger le contenu du fichier existant dans une liste
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(imagePath)) {
                    // Remplacer la ligne correspondante
                    lignes.add(imagePath + "@@@" + descriptionImage);
                    foundImageLine = true;
                } else {
                    lignes.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Si aucune ligne correspondante n'a été trouvée, ajouter une nouvelle ligne à la fin
        if (!foundImageLine) {
            lignes.add(imagePath + "@@@" + descriptionImage);
        }

        // Écrire le contenu mis à jour dans le fichier
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lignes) {
                System.out.println(line);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recupère le fichier image avec son chemin
     * @param imagePath chemin de l'image
     * @return fileName le nom de l'image
     */
    private String getImageNameFromPath(String imagePath) {
        File file = new File(imagePath);
        String fileName = file.getName();
        // Remove the file extension if present
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1) {
            fileName = fileName.substring(0, dotIndex);
        }
        return fileName;
    }

    /**
     * Pop up de suppression de l image
     */
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation de suppression")
                .setMessage("Êtes-vous sûr de vouloir supprimer cette image ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    // Supprimer l'image du répertoire
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                    // Envoyer le résultat à Pictures
                    setResult(RESULT_OK);
                    // Terminer l'activité
                    finish();
                })
        .setNegativeButton("Non", (dialog, which) -> {
            // Ne rien faire, simplement fermer la boîte de dialogue
        })
        .show();
    }

    /**
     * afficher une image en pleins écran
     */
    private void showFullScreenImage() {
        Intent intent = new Intent(this, ImageFullScreen.class);
        intent.putExtra("imagePath", imagePath);
        startActivityForResult(intent, REQUEST_FULLSCREEN_IMAGE);
    }

    private void renameImageFile() {
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            String parentDirectory = imageFile.getParent();
            // Nouveau nom de fichier souhaité
            String nouveauNomImage = "S-"+nomImageTemps+"-T"+nomImageJour_post_expo+"-J"+nomImageJour+"-R"+nomImageReplicat+"-"+nomImageSolution+"-Q"+nomImageNum_planaire+".jpg";

            if (parentDirectory != null) {
                File newImageFile = new File(parentDirectory, nouveauNomImage);
                boolean renamed = imageFile.renameTo(newImageFile);

                if (renamed) {
                    // Le fichier a été renommé avec succès
                    imagePath = newImageFile.getAbsolutePath();
                }
            }
        }
    }

    /**
     * Gère les évenement lié a la fermeture de ImageFullScreen
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FULLSCREEN_IMAGE) {
            // Vérifiez le code de demande correspondant à l'activité ImageFullScreen
            if (resultCode == RESULT_OK) {
                // Le résultat est OK, effectuez l'action d'actualisation ici
                gestionImage();
            }
        }
    }

    /**
     * Creation de la view image
     */
    private void gestionImage() {
        Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);

        ImageView imageView = findViewById(R.id.imgView);
        imageView.setImageBitmap(imageBitmap);

        // Ajouter un écouteur de clic à l'ImageView
        imageView.setOnClickListener(v -> {
            // Afficher l'image en plein écran
            showFullScreenImage();
        });

        Button buttonRetour = findViewById(R.id.retour);
        buttonRetour.setOnClickListener(v -> {
            // Renommer l'image
            if(nomImageAChange){
                renameImageFile();
            }
            // Enregistré la descritpion
            if (descriptionAChange){
                saveDescription();
            }
            // Terminer l'activité ImagePicturesClique et revenir à l'activité précédente (Pictures)
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}