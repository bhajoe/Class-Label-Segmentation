/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wordsegmentation;

/**
 *
 * @author Bayu Priyambadha
 */
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class DomainVocabularyBuilder {

    // Map untuk menyimpan kata dan jumlah frekuensinya
    private Map<String, Integer> wordFrequencies = new HashMap<>();

    /**
     * Method untuk membaca dan memproses satu file Java.
     * (Bisa dipanggil berulang kali di dalam looping jika kamu punya banyak file)
     */
    public void processJavaFile(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            
            for (String line : lines) {
                // 1. Ambil hanya karakter alfabet (A-Z, a-z). 
                // Abaikan angka, tanda baca, kurung kurawal, dll.
                Pattern pattern = Pattern.compile("[a-zA-Z]+");
                Matcher matcher = pattern.matcher(line);

                while (matcher.find()) {
                    String extractedWord = matcher.group();

                    // 2. Pecah CamelCase (menggunakan logikamu sebelumnya!)
                    // Contoh: "getUserName" -> ["get", "User", "Name"]
                    String[] subWords = extractedWord.split("(?=\\p{Upper})");

                    // 3. Masukkan setiap sub-kata ke dalam perhitungan frekuensi
                    for (String word : subWords) {
                        // Abaikan string kosong akibat hasil split
                        if (!word.trim().isEmpty()) {
                            String cleanWord = word.toLowerCase();
                            
                            // Tambahkan frekuensi (+1) ke dalam Map
                            int count = wordFrequencies.getOrDefault(cleanWord, 0);
                            wordFrequencies.put(cleanWord, count + 1);
                        }
                    }
                }
            }
            System.out.println("Berhasil memproses: " + filePath);
            
        } catch (IOException e) {
            System.out.println("Gagal membaca file " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Method untuk menyimpan hasil perhitungan ke file CSV
     * File ini yang nantinya akan dibaca oleh method addWord() kamu!
     */
    public void saveToCSV(String outputFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            // Urutkan berdasarkan frekuensi terbanyak (opsional, tapi bagus untuk analisis)
            List<Map.Entry<String, Integer>> list = new ArrayList<>(wordFrequencies.entrySet());
            list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            // Tulis ke file (Format: kata,frekuensi)
            for (Map.Entry<String, Integer> entry : list) {
                // Filter: Hanya simpan kata yang panjangnya lebih dari 1 huruf 
                // untuk menghindari noise seperti huruf 'i', 'j' pada perulangan for
                if (entry.getKey().length() > 1) {
                    writer.write(entry.getKey() + "," + entry.getValue());
                    writer.newLine();
                }
            }
            System.out.println("Dataset Frekuensi berhasil disimpan ke: " + outputFilePath);
            System.out.println("Total kata unik: " + list.size());
            
        } catch (IOException e) {
            System.out.println("Gagal menyimpan CSV: " + e.getMessage());
        }
    }

    // --- CONTOH CARA PENGGUNAANNYA ---
    public static void main(String[] args) {
        DomainVocabularyBuilder builder = new DomainVocabularyBuilder();
        
        // Asumsikan kamu punya file source code Java untuk dianalisis
        // Jika kamu punya banyak file, kamu bisa menggunakan loop (Files.walk)
        //builder.processJavaFile("C:\\ProjectCode\\ContohClass.java");
        builder.processJavaFile("C:\\Users\\Bayu Priyambadha\\Documents\\NetBeansProjects\\Class-Label-Segmentation\\src\\main\\java\\com\\mycompany\\wordsegmentation\\luceneTokenizer.java");
        
        // Simpan hasilnya ke file CSV
        builder.saveToCSV("domain_knowledge_dataset.csv");
    }
}
