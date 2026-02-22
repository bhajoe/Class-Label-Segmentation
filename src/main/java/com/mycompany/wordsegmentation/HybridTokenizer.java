/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wordsegmentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Bayu Priyambadha
 */
public class HybridTokenizer {
    // Menyimpan kamus beserta probabilitas atau frekuensinya (Log Cost)
    // Semakin sering muncul, cost-nya semakin mendekati 0. Semakin jarang, cost-nya tinggi.
    private Map<String, Double> wordCosts = new HashMap<>();
    private List<String> tokens = new ArrayList<>();
    private double maxCost = 99999.0; // Penalti untuk kata yang tidak ada di kamus

    // Method untuk inisialisasi awal (Contoh sederhana)
    public void buildDictionaryCost(Map<String, Integer> wordFrequencies, int totalWords) {
        for (Map.Entry<String, Integer> entry : wordFrequencies.entrySet()) {
            String word = entry.getKey();
            double probability = (double) entry.getValue() / totalWords;
            // Menghitung -log(P)
            wordCosts.put(word, -Math.log(probability));
        }
    }

    public void tokenize(String label) {
        String text = label.toLowerCase();
        int n = text.length();
        
        // Array untuk menyimpan cost minimum hingga indeks ke-i
        double[] bestCosts = new double[n + 1];
        Arrays.fill(bestCosts, maxCost);
        bestCosts[0] = 0.0; // Cost awal adalah 0
        
        // Array untuk melacak di mana kita harus memotong string
        int[] splitPositions = new int[n + 1];
        
        // Algoritma Dynamic Programming
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                String substring = text.substring(j, i);
                
                // Cek apakah kata ada di kamus, jika tidak berikan penalti maksimal
                double wordCost = wordCosts.getOrDefault(substring, maxCost);
                
                // Jika cost kata valid (atau terpaksa diambil), hitung total cost sejauh ini
                if (bestCosts[j] + wordCost < bestCosts[i]) {
                    bestCosts[i] = bestCosts[j] + wordCost;
                    splitPositions[i] = j; // Ingat dari mana kita memotong
                }
            }
        }
        
        // Rekonstruksi kata dari belakang ke depan berdasarkan splitPositions
        List<String> result = new ArrayList<>();
        int currentIndex = n;
        while (currentIndex > 0) {
            int previousIndex = splitPositions[currentIndex];
            result.add(text.substring(previousIndex, currentIndex));
            currentIndex = previousIndex;
        }
        
        // Karena direkonstruksi dari belakang, kita harus membalik urutannya
        Collections.reverse(result);
        this.tokens.addAll(result);
    }
    
    public List<String> getToken()
    {
        return this.tokens;
    }
}
