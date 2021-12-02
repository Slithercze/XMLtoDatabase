package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class CreateDB {
    public static void createDB() {
        
        String url = "jdbc:mysql://localhost:3306";

       
        String username = "root";
        String password = "";

        
        String sql = "CREATE DATABASE IF NOT EXISTS ukol;";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
            System.out.println("DATABASE CREATED SUCCESFULY");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String sql2 = "CREATE TABLE `ukol`.`cast_obce` ( `Kod` INT NOT NULL AUTO_INCREMENT , `Nazev` VARCHAR(30) NOT NULL , `Kod_obce` INT NOT NULL , PRIMARY KEY (`Kod`)) ENGINE = InnoDB;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql2)) {
            stmt.execute();
            System.out.println("DATABASE CREATED SUCCESFULY");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String sql3 = "CREATE TABLE `ukol`.`obec` ( `Kod` INT NOT NULL AUTO_INCREMENT , `Nazev` VARCHAR(30) NOT NULL , PRIMARY KEY (`Kod`)) ENGINE = InnoDB;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql3)) {
            stmt.execute();
            System.out.println("DATABASE CREATED SUCCESFULY");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String sql4 = "ALTER TABLE `cast_obce` ADD CONSTRAINT `Test` FOREIGN KEY (`Kod_obce`) REFERENCES `ukol`.`obec`(`Kod`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql4)) {
            stmt.execute();
            System.out.println("DATABASE CREATED SUCCESFULY");
        } catch (Exception e) {
          
        }
    }
}
