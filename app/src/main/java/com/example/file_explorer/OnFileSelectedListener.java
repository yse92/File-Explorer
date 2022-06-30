package com.example.file_explorer;

import java.io.File;

public interface OnFileSelectedListener {
    void onFileClicked(File file);
    void onFileLongClicked(File file);

}
