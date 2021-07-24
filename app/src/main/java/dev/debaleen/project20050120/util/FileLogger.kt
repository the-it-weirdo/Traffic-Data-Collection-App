package dev.debaleen.project20050120.util

import android.os.Environment
import java.io.File


/**
 * A class to handle File creation and write operations.
 * */
class FileLogger(filename: String, isDir: Boolean = false) {

    /**
     * Parent directory for the app to create data recording files.
     * */
    private var parentF: File = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        Constants.BASE_DIR
    )

    var file: File

    init {
        if (!parentF.exists()) {
            parentF.mkdirs()
        }
        // Create a file reference to the given filename with respect to parent directory
        file = File(parentF, filename)
        // if the file doesn't exist create the file
        if (!file.exists()) {
            // if the requested file is a directory, make the directory else create the file
            if (isDir) file.mkdirs() else file.createNewFile()
        }
    }

    /**
     * Method to get a reference to output file for recording data.
     * @return A [File] reference to the [FileLogger]
     * */
    fun getOutputFile(): File {
        return file
    }

    /**
     * Method to write a data to a given file. The data will be appended to the file.
     * @param text The data as [String]
     * */
    fun writeToFile(text: String) {
        file.appendText(text)
    }
}