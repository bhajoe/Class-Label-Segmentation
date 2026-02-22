/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.wordsegmentation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author bhajoe
 */
public class WordDictionary {
    private String DictionaryFile = "words.txt";
    private Set<String> dict = new TreeSet<String>();
    private ArrayList<String> words = new ArrayList<String>();
    // HashMap untuk menyimpan [Kata, Jumlah Kemunculan]
    private Map<String, Integer> wordFrequencies;
    
    // Menyimpan total seluruh kata di dalam kamus untuk menghitung probabilitas
    private int totalWords;
        
    public WordDictionary() {
        init();
    }

    public Set<String> getDict() {
        return dict;
    }
    
    public ArrayList<String> getWords()
    {
        return this.words;
    }
    
    private void init() 
    {
        BufferedReader br = null;
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(DictionaryFile));
            while ((sCurrentLine = br.readLine()) != null) 
            {
                String[] words = sCurrentLine.split("\\|");
                for (String word : words)
                {
                    if (word.length()>2)
                        dict.add(word);    
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
	} finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
        }
	}
        
    }    
    
    /**
     * Method untuk melatih (train) kamus.
     * Kamu bisa memanggil ini saat membaca dataset source code.
     */
    public void addWord(String word, int frequency) {
        String lowerWord = word.toLowerCase(); // Pastikan kamus selalu huruf kecil
        
        // Jika kata sudah ada, tambahkan frekuensinya. Jika belum, masukkan baru.
        int currentFreq = this.wordFrequencies.getOrDefault(lowerWord, 0);
        this.wordFrequencies.put(lowerWord, currentFreq + frequency);
        
        // Update total kata keseluruhan
        this.totalWords += frequency;
    }
    
    /**
     * METHOD BARU UNTUK JURNAL: Menghitung nilai P(w)
     * Probabilitas = (Frekuensi Kata) / (Total Semua Kata)
     */
    public double getProbability(String word) {
        String lowerWord = word.toLowerCase();
        
        if (this.wordFrequencies.containsKey(lowerWord)) {
            // Rumus probabilitas dasar (Unigram)
            return (double) this.wordFrequencies.get(lowerWord) / this.totalWords;
        }
        
        // Jika kata tidak ditemukan, kembalikan probabilitas yang sangat kecil
        // (Praktik smoothing sederhana dalam NLP agar tidak terjadi error pembagian nol)
        return 0.0000001; 
    }
    
    // Opsional: Method untuk melihat total kata (berguna untuk analisis metrik di jurnal)
    public int getTotalWords() {
        return this.totalWords;
    }
}
