package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.zip.ZipFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;

import java.net.URL;

import static java.lang.Integer.parseInt;

public class Obec {
    public static void unzip(String zipFilePath, String destDir) { //unzip souboru
        File dir = new File(destDir);
        
        if (!dir.exists())
            dir.mkdirs();
        FileInputStream fis;
        
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to " + newFile.getAbsolutePath());
               
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void obec() {
        // login do databaze
       

        String jdbcUrl = "jdbc:mysql://localhost:3306/ukol";
        String username = "root";
        String password = "";

        try (BufferedInputStream in = new BufferedInputStream(new URL("https://vdp.cuzk.cz/vymenny_format/soucasna/20211031_OB_573060_UZSZ.xml.zip").openStream());
            FileOutputStream fileOutputStream = new FileOutputStream("data.zip")) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // handle exception
        }
        // adresáře souborů 
        String zipFilePath = "data.zip";
        String destDir = "demo/src";
        unzip(zipFilePath, destDir);
        String filePath = "demo/src/20211031_OB_573060_UZSZ.xml";

        int batchSize = 20;
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(jdbcUrl, username, password); // připojení do databaze
            connection.setAutoCommit(false);

            File file = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(filePath);

            String sql = "insert into obec(Kod,Nazev) values(?,?)";

            PreparedStatement statement = connection.prepareStatement(sql);

            NodeList nList = doc.getElementsByTagName("vf:Obce");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                System.out.println("Node name: " + nNode.getNodeName() + " " + (i + 1));
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("Kod: " + eElement.getElementsByTagName("obi:Kod").item(0).getTextContent());
                    System.out.println("Nazev: " + eElement.getElementsByTagName("obi:Nazev").item(0).getTextContent());

                    String Kod = eElement.getElementsByTagName("obi:Kod").item(0).getTextContent();
                    String Nazev = eElement.getElementsByTagName("obi:Nazev").item(0).getTextContent();

                    statement.setInt(1, parseInt(Kod)); // vloženi do relační tabulky
                    statement.setString(2, Nazev);
                    int count = 0;
                    statement.addBatch();
                    if (count % batchSize == 0) {
                        statement.executeBatch();
                    }
                }
            }

            statement.executeBatch();
            connection.commit(); // ukončení pripojení
            connection.close();
            System.out.println("\n Data úspěšně vložena do databáze");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
