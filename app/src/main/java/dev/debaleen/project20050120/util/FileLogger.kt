package dev.debaleen.project20050120.util

import android.os.Environment
import java.io.File


/**
 * A class to handle File creation and write operations.
 * */
class FileLogger {

    /**
     * Parent directory for the app to create data recording files.
     * */
    private var parentF: File = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        Constants.BASE_DIR
    )

    init {
        if (!parentF.exists()) {
            parentF.mkdirs()
        }
    }

    /**
     * Method to get a reference to output file for recording data.
     * @param filename A [String] denoting the name of the output file.
     * @param isDir A [Boolean] denoting whether the [filename] is a directory
     * @return A [File] reference to the [filename]
     * */
    fun getOutputFile(filename: String, isDir: Boolean): File {
        // Create a file reference to the given filename with respect to parent directory
        val file = File(parentF, filename)
        // if the file doesn't exist create the file
        if (!file.exists()) {
            // if the requested file is a directory, make the directory else create the file
            if (isDir) file.mkdirs() else file.createNewFile()
        }
        return file
    }

    /**
     * Method to write a data to a given file. The data will be appended to the file.
     * @param file A [File] to which the data will be written.
     * @param text The data as [String]
     * */
    fun writeToFile(file: File, text: String) {
        file.appendText(text)
    }
}