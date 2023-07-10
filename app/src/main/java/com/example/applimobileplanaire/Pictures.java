package com.example.applimobileplanaire;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Pictures extends Fragment {
    private static final int REQUEST_OPEN_IMAGE = 1 ;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pictures, container, false);

        ImageView imgLogo = view.findViewById(R.id.imageLogo);
        imgLogo.setImageResource(R.drawable.logo_haut);

        setImage(view);

        return view;
    }

    private void setImage(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        List<String> imagePathList = getImagePathsFromDirectory();
        ImageAdapter imageAdapter = new ImageAdapter(imagePathList);
        recyclerView.setAdapter(imageAdapter);
    }

    private List<String> getImagePathsFromDirectory() {
        List<String> imagePaths = new ArrayList<>();

        File storageDir = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Images");
        if (storageDir.exists()) {
            File[] files = storageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        imagePaths.add(file.getAbsolutePath());
                    }
                }
            }
        }

        return imagePaths;
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
        private final List<String> imagePathList;

        public ImageAdapter(List<String> imagePathList) {
            this.imagePathList = imagePathList;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            String imagePath = imagePathList.get(position);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            holder.imageView.setImageBitmap(bitmap);
        }

        @Override
        public int getItemCount() {
            return imagePathList.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView imageView;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String imagePath = imagePathList.get(position);
                    openImageDetailActivity(imagePath);
                }
            }
        }
    }
    private void openImageDetailActivity(String imagePath) {
        Intent intent = new Intent(getActivity(), ImagePicturesClique.class);
        intent.putExtra("imagePath", imagePath);
        startActivityForResult(intent, REQUEST_OPEN_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_IMAGE && resultCode == RESULT_OK) {
            // Mettre à jour les images
            setImage(view);
        }
    }
}