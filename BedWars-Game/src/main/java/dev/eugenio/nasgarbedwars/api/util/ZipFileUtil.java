package dev.eugenio.nasgarbedwars.api.util;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public final class ZipFileUtil {
    public static void zipDirectory(final File file, final File file2) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file2);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            zipSubDirectory("", file, zipOutputStream);
            zipOutputStream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static void zipSubDirectory(final String s, final File file, final ZipOutputStream zipOutputStream) {
        final byte[] array = new byte[4096];
        final File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return;
        }
        for (final File file2 : listFiles) {
            if (file2.isDirectory()) {
                try {
                    final String string = s + file2.getName() + "/";
                    zipOutputStream.putNextEntry(new ZipEntry(string));
                    zipSubDirectory(string, file2, zipOutputStream);
                    zipOutputStream.closeEntry();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else {
                try {
                    final FileInputStream fileInputStream = new FileInputStream(file2);
                    zipOutputStream.putNextEntry(new ZipEntry(s + file2.getName()));
                    int read;
                    while ((read = fileInputStream.read(array)) > 0) zipOutputStream.write(array, 0, read);
                    zipOutputStream.closeEntry();
                    fileInputStream.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public static void unzipFileIntoDirectory(final File file, final File file2) {
        try {
            if (!file.exists()) return;
            final ZipFile zipFile = new ZipFile(file);
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            FileOutputStream fileOutputStream = null;
            while (entries.hasMoreElements()) {
                try {
                    final ZipEntry zipEntry = entries.nextElement();
                    final InputStream inputStream = zipFile.getInputStream(zipEntry);
                    final byte[] array = new byte[1024];
                    final File file3 = new File(file2.getAbsolutePath(), zipEntry.getName());
                    if (zipEntry.isDirectory()) {
                        file3.mkdirs();
                    }
                    file3.getParentFile().mkdirs();
                    file3.createNewFile();
                    fileOutputStream = new FileOutputStream(file3);
                    int read;
                    while ((read = inputStream.read(array)) != -1) {
                        fileOutputStream.write(array, 0, read);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException ignored) {
                            // Ignored
                        }
                    }
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
