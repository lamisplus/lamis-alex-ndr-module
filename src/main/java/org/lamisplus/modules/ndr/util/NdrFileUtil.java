package org.lamisplus.modules.ndr.util;

import lombok.var;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Component
public class NdrFileUtil {

    public void makeDir(String directory) {
        File destination = new File(directory);
        if (!destination.exists()) {
            destination.mkdirs();
        }
    }

    public void deleteFile(File file) {
        if (file.isDirectory()) {
            //directory is empty, then delete it
            if (file.list().length == 0) {

                file.delete();
                System.out.println("Directory is deleted : "
                        + file.getAbsolutePath());

            } else {
                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    deleteFile(fileDelete);
                }

                //check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    file.delete();
                    System.out.println("Directory is deleted : "
                            + file.getAbsolutePath());
                }
            }
        } else {
            //if file, then delete it
            file.delete();
            System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }

    public void deleteFileWithExtension(String sourceFolder, String deleteExtension) {
        FileFilter fileFilter = new FileFilter(deleteExtension);
        File directory = new File(sourceFolder);

        // Put the names of all files ending with .txt in a String array
        String[] listOfTextFiles = directory.list(fileFilter);
        if (listOfTextFiles.length == 0) {
            System.out.println("There are no text files in this directory!");
            return;
        }

        File fileToDelete;
        for (String file : listOfTextFiles) {
            //construct the absolute file paths...
            String absoluteFilePath = new StringBuffer(sourceFolder).append(File.separator).append(file).toString();

            //open the files using the absolute file path, and then delete them...
            fileToDelete = new File(absoluteFilePath);
            boolean isdeleted = fileToDelete.delete();
            System.out.println("File : " + absoluteFilePath + " was deleted : " + isdeleted);
        }
    }

    //https://www.baeldung.com/java-compress-and-uncompress
    public void zipFile(String sourceFile, String outputZipFile) {
        try {
            FileOutputStream fos = new FileOutputStream(outputZipFile);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(sourceFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            zipOut.close();
            fis.close();
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void zipDirectory(String sourceDir, String outputZipFile)  {
        try {
            Set<String> srcFiles = listFiles(sourceDir);
            FileOutputStream fos = new FileOutputStream(outputZipFile);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            for (String srcFile : srcFiles) {
                System.out.println("File name: "+srcFile);
                File fileToZip = new File(sourceDir.concat(srcFile));
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();
            }
            zipOut.close();
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    //https://www.baeldung.com/java-compress-and-uncompress
    public void unzip(String zipFile, String folder) throws IOException {
        int BUFFER_SIZE = 8192;
        File extractTo = new File(folder);
        ZipFile archive = new ZipFile(zipFile);
        Enumeration e = archive.entries();
        while (e.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) e.nextElement();
            File file = new File(extractTo, entry.getName());
            if (entry.isDirectory() && !file.exists()) {
                file.mkdirs();
            } else {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                InputStream in = archive.getInputStream(entry);
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                byte[] buffer = new byte[BUFFER_SIZE];
                int readByte;
                while ((readByte = in.read(buffer)) != -1) {  //read and write until last byte is encountered
                    out.write(buffer, 0, readByte);
                }
                in.close();
                out.close();
            }
        }
    }

    //https://www.baeldung.com/java-list-directory-files
    public Set<String> listFiles(String dir)  {
        Set<String> fileList = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileList.add(path.getFileName()
                            .toString());
                }
            }
        }
        catch (IOException e) {

        }
        return fileList;
    }

    public static boolean isBinaryFile(String filename) throws FileNotFoundException, IOException {
        /**
         * Guess whether given file is binary. Just checks for anything under
         * 0x09.
         *
         * If the file consists of the bytes 0x09 (tab), 0x0A (line feed), 0x0C
         * (form feed), 0x0D (carriage return), or 0x20 through 0x7E, then it's
         * probably ASCII text.
         *
         * If the file contains any other ASCII control character, 0x00 through
         * 0x1F excluding the three above, then it's probably binary data.
         */

        InputStream in = new FileInputStream(filename);
        int size = in.available();
        if (size > 1024) {
            size = 1024;
        }
        byte[] data = new byte[size];
        in.read(data);
        in.close();

        int ascii = 0;
        int other = 0;

        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            if (b < 0x09) {
                return true;
            }

            if (b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D) {
                ascii++;
            } else if (b >= 0x20 && b <= 0x7E) {
                ascii++;
            } else {
                other++;
            }
        }

        if (other == 0) {
            return false;
        }
        return 100 * other / (ascii + other) > 95; //(other / size * 100 > 95);
    }

    public void lockFolder(String folder) {
        //To lock a folder create a file named lock.ser
        String fileName = folder + "lock.ser";
        try {
            File file = new File(fileName);
            FileOutputStream stream = new FileOutputStream(file);
            stream.close();
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public void unlockFolder(String folder) {
        //To unlock a folder delete the file lock.ser
        String fileName = folder + "lock.ser";
        try {
            File file = new File(fileName);
            if(file.exists()) {
                file.delete();
            }
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public boolean isLocked(String folder) {
        // If file lock.ser if found in the folder, that folder is locked and cannot be accessed by the client to write data into
        //if the file is not present the folder is unlock and data can be written into it
        if(new File(folder + "lock.ser").exists()) {
            return true;
        }
        else {
            return false;
        }

    }


    public class FileFilter implements FilenameFilter {

        private String fileExtension;

        public FileFilter(String fileExtension) {
            this.fileExtension = fileExtension;
        }

        @Override
        public boolean accept(File directory, String fileName) {
            return (fileName.endsWith(this.fileExtension));
        }
    }

}
