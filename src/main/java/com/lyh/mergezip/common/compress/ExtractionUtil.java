package com.lyh.mergezip.common.compress;

import com.lyh.mergezip.pojo.OneArchive;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ExtractionUtil {
    static int bufSize = 1024;
    public void extractionGz(String archive, String outPath) throws IOException {
        File file = new File(archive);
        extractionGz(file,outPath,false);
    }

    public void extractionGz(File file, String outPath,boolean useStream) throws IOException {
        BufferedInputStream bis =  new BufferedInputStream(new FileInputStream(file));
        String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
        String finalName = file.getParent() + File.separator + fileName;
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(finalName));
        GzipCompressorInputStream gcis = new GzipCompressorInputStream(bis);

        byte[] buffer = new byte[1024];
        int read = -1;
        while ((read = gcis.read(buffer)) != -1) {
            bos.write(buffer, 0, read);
        }
        gcis.close();
        bos.close();

        if (useStream) {
            InputStream inputStream = Files.newInputStream(Paths.get(finalName));
            extractionTar(inputStream, outPath);
        }else {
            extractionTar(finalName, outPath,true);
        }
    }
    public void extractionGz(InputStream in, String outPath) throws IOException {
        BufferedInputStream bis =  new BufferedInputStream(in);
        String finalName = outPath + File.separator + "temp.tar";
        File file = new File(finalName);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(finalName);
        BufferedOutputStream bos = new BufferedOutputStream(out);
        GzipCompressorInputStream gcis = new GzipCompressorInputStream(bis);

        byte[] buffer = new byte[1024];
        int read = -1;
        while ((read = gcis.read(buffer)) != -1) {
            bos.write(buffer, 0, read);
        }
        gcis.close();
        bos.close();

        InputStream inputStream = Files.newInputStream(Paths.get(finalName));
        extractionTar(inputStream, outPath);
    }

    public static void extractionTar(String finalName, String outPath, boolean deleteFile) throws IOException {
        File file = new File(finalName);
        extractionTar(file, outPath,deleteFile);
    }

    public static void extractionTar(File file, String outPath, boolean deleteFile) throws IOException {
        TarArchiveInputStream tais = new TarArchiveInputStream(new FileInputStream(file));
        TarArchiveEntry tarArchiveEntry = null;
        while ((tarArchiveEntry = tais.getNextTarEntry()) != null) {
            String name = tarArchiveEntry.getName();
            File tarFile = new File(outPath, name);
            if(tarArchiveEntry.isDirectory()){
                if(!tarFile.exists()){
                    tarFile.mkdirs();
                }
                continue;
            }else {
                if (!tarFile.getParentFile().exists()) {
                    tarFile.getParentFile().mkdirs();
                }
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tarFile));
            int read = -1;
            byte[] buffer = new byte[bufSize];
            while ((read = tais.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            bos.close();
        }
        tais.close();
        if (deleteFile) {
            file.delete();//删除tar文件
        }
    }

    public void extractionTar(InputStream in, String outPath) throws IOException {
        ArrayList<OneArchive> zipArchives = new ArrayList<>();
        ArrayList<OneArchive> tarArchives = new ArrayList<>();
        TarArchiveInputStream tais = new TarArchiveInputStream(in);
        TarArchiveEntry entry = null;
        while ((entry = tais.getNextTarEntry()) != null ){
            String name = entry.getName();
            if(name.endsWith(".zip")){
                byte[] bytes = toCache(entry, tais);
                zipArchives.add(new OneArchive(outPath+File.separator+name.substring(0,name.indexOf("/")),bytes));
            }else if(name.endsWith(".tar.gz")){
                byte[] bytes = toCache(entry, tais);
                tarArchives.add(new OneArchive(outPath+File.separator+name.substring(0,name.indexOf("/")),bytes));
            }else {
                toFile(outPath+File.separator+name,entry,tais);
            }
        }
        for (OneArchive zipArchive : zipArchives) {
            byte[] bytes = zipArchive.getBytes();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            this.extractionZip(byteArrayInputStream,zipArchive.getPath());
        }

        for (OneArchive tarArchive : tarArchives) {
            byte[] bytes = tarArchive.getBytes();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            this.extractionTar(byteArrayInputStream,tarArchive.getPath());
        }


        tais.close();
    }

    public void extractionZip(InputStream in, String outPath) throws IOException {
        /*ZipFile zipFile = new ZipFile(file);
        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();*/
        ArrayList<OneArchive> zipArchives = new ArrayList<>();
        ArrayList<OneArchive> tarArchives = new ArrayList<>();
        ZipArchiveInputStream zipArchiveInputStream = new ZipArchiveInputStream(in);
        ArchiveEntry entry= null;
        while ((entry = zipArchiveInputStream.getNextEntry()) != null ){
//            InputStream inputStream = zipFile.getInputStream(entry);
            String name = entry.getName();
            if(name.endsWith(".zip")){
                byte[] bytes = toCache(entry, zipArchiveInputStream);
                zipArchives.add(new OneArchive(outPath+File.separator+name.substring(0,name.indexOf("/")),bytes));
            }else if(name.endsWith(".tar.gz")){
                byte[] bytes = toCache(entry, zipArchiveInputStream);
                tarArchives.add(new OneArchive(outPath+File.separator+name.substring(0,name.indexOf("/")),bytes));
            }else {
                toFile(outPath+File.separator+name,entry,zipArchiveInputStream);
            }
        }
        for (OneArchive zipArchive : zipArchives) {
            byte[] bytes = zipArchive.getBytes();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            this.extractionZip(byteArrayInputStream,zipArchive.getPath());
        }

        for (OneArchive tarArchive : tarArchives) {
            byte[] bytes = tarArchive.getBytes();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            this.extractionTar(byteArrayInputStream,tarArchive.getPath());
        }
        zipArchiveInputStream.close();
    }

    private static void toFile(String outPath, ArchiveEntry entry, InputStream inputStream) throws IOException {
        File outFile = new File(outPath);
        System.out.println("outFile = " + outFile);
        if(entry.isDirectory()){
            if(!outFile.exists()){
                outFile.mkdirs();
            }
            return;
        }else {
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
        }
        try (OutputStream o = Files.newOutputStream(outFile.toPath())) {
            IOUtils.copy(inputStream, o);
        }
    }

    private static byte[] toCache(ArchiveEntry entry, InputStream inputStream) throws IOException {
        byte[] content = new byte[(int) entry.getSize()];
        int off=0;
        int index;
        while ((index= inputStream.read(content,off,content.length-off))>0){
            off += index;
        }
        return content;
    }
}
