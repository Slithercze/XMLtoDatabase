package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import static java.lang.Integer.parseInt;

public class CastObce {
    public static void castObce() {
        // login do databaze
        String jdbcUrl = "jdbc:mysql://localhost:3306/ukol";
        String username = "root";
        String password = "";
        
        // adresáře souborů
        String filePath = "C:\\Users\\Slith\\Desktop\\data.xml";

        int batchSize = 20;
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(jdbcUrl, username, password); // připojení do databaze
            connection.setAutoCommit(false);

            File file = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(filePath);

            String sql = "insert into cast_obce(Kod,Nazev,Kod_obce) values(?,?,?)";

            PreparedStatement statement = connection.prepareStatement(sql);

            NodeList nList = doc.getElementsByTagName("vf:CastObce");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                System.out.println("Node name: " + nNode.getNodeName() + " " + (i + 1));
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String Kod = eElement.getElementsByTagName("coi:Kod").item(0).getTextContent();
                    String Nazev = eElement.getElementsByTagName("coi:Nazev").item(0).getTextContent();
                    String Kod_obce = eElement.getElementsByTagName("obi:Kod").item(0).getTextContent();
                    statement.setInt(1, parseInt(Kod));
                    statement.setString(2, Nazev);
                    statement.setInt(3, parseInt(Kod_obce));
                    statement.addBatch();
                    int count = 0;
                    if (count % batchSize == 0) {
                        statement.executeBatch();
                    }
                    System.out.println("Kod: " + eElement.getElementsByTagName("coi:Kod").item(0).getTextContent());
                    System.out.println("Nazev: " + eElement.getElementsByTagName("coi:Nazev").item(0).getTextContent());
                    System.out
                            .println("Kod obce: " + eElement.getElementsByTagName("obi:Kod").item(0).getTextContent());

                }
            }

            // vloženi do relační tabulky

            statement.executeBatch();
            connection.commit(); // ukončení pripojení
            connection.close();
            System.out.println("\n Data úspěšně vložena do databáze");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
