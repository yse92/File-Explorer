package com.example.file_explorer.Fragments;


import android.Manifest;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.file_explorer.FileAdapter;
import com.example.file_explorer.FileOpener;
import com.example.file_explorer.OnFileSelectedListener;
import com.example.file_explorer.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InternalFragment extends Fragment implements OnFileSelectedListener {

    View view;
    File storage;
    String data;

    private RecyclerView recyclerView;
    private List<File> fileList;
    private ImageView img_back;
    private TextView tv_pathHolder;
    private FileAdapter fileAdapter;

    String [] items = {"Details", "Rename", "Delete", "Share"};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_internal, container, false);

        tv_pathHolder = view.findViewById(R.id.tv_pathHolder);
        img_back = view.findViewById(R.id.img_back);
        
        runTimePermission();

        String internalStorage = System.getenv("EXTERNAL_STORAGE");
        if ((null == internalStorage) || (internalStorage.length() == 0)) {
            storage = new File(internalStorage);
        }



        try {
            data = getArguments().getString("path");
            File file = new File(data);
            storage = file;

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        tv_pathHolder.setText(storage.getAbsolutePath());
        runTimePermission();

        return view;
    }

    private void runTimePermission() {
        Dexter.withContext(getContext()).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                displayFiles();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }
        }).check();
    }

    public ArrayList<File> findFiles(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        try {

            for(File singleFile : files) {
                if(singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.add(singleFile);
                }
            }
            for(File singleFile : files) {
                if (singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".jpg") ||
                        singleFile.getName().toLowerCase().endsWith(".png") || singleFile.getName().toLowerCase().endsWith(".mp3") ||
                        singleFile.getName().toLowerCase().endsWith(".wav") || singleFile.getName().toLowerCase().endsWith(".mp4") ||
                        singleFile.getName().toLowerCase().endsWith(".pdf") || singleFile.getName().toLowerCase().endsWith(".doc") ||
                        singleFile.getName().toLowerCase().endsWith(".apk")) {

                    arrayList.add(singleFile);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return arrayList;
    }

    private void displayFiles() {
        recyclerView = view.findViewById(R.id.recycler_internal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        fileList.addAll(findFiles(storage));
        fileAdapter = new FileAdapter(getContext(), fileList, this);
        recyclerView.setAdapter(fileAdapter);
    }

    @Override
    public void onFileClicked(File file) {
        if (file.isDirectory()) {
            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            InternalFragment internalFragment = new InternalFragment();
            internalFragment.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, internalFragment).addToBackStack(null).commit();

        } else {
            try {
                FileOpener.openFile(getContext(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFileLongClicked(File file) {
        final Dialog optionDialog = new Dialog(getContext());
        optionDialog.setContentView(R.layout.option_dialog);
        optionDialog.setTitle("Select Options...");
        ListView options = (ListView) optionDialog.findViewById(R.id.List);

    }
}
