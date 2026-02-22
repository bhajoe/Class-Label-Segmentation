/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.wordsegmentation;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 *
 * @author bhajoe
 */
public class luceneTokenizer {
    private ArrayList<String> tokens = new ArrayList<String>();
    private WordDictionary Dict;
    
    private Map<String, Long> freqDict;
    private int totalCount;
    private int maxWordLength;
    private double unknownPenalty;

    public luceneTokenizer() throws IOException {
        this.Dict = new WordDictionary();
        this.freqDict = FrequencyDictLoader.loadFrequencyDict("words.txt");
    }
    
    public ArrayList<String> getTokens()
    {
        return this.tokens;
    }
    
    public void Tokenizing(String Lbl) throws IOException
    {
        if (Lbl.matches(".*[A-Z].*"))
        {
            String[] lbls = Lbl.split("(?=\\p{Upper})");
            for (String wrd : lbls)
            {
                this.tokens.add(wrd);
            }
        } else
        {
            Reader reader = new StringReader(Lbl);      
            NGramTokenizer gramTokenizer = new NGramTokenizer(reader, 1, 10);
            //NGramTokenizer gramTokenizer = new NGramTokenizer(1, 10);

            CharTermAttribute charTermAttribute = gramTokenizer.addAttribute(CharTermAttribute.class);

            while (gramTokenizer.incrementToken()) 
            {
                String token = charTermAttribute.toString();
                if (this.Dict.getDict().contains(token.toLowerCase()))
                {
                    //System.out.println(token);        
                    this.tokens.add(token);
                }
            }
        }
    }
    
    public void Tokenizings(String Lbl) throws IOException {
    // 1. BAGIAN IF: Tetap dipertahankan untuk CamelCase
    if (Lbl.matches(".*[A-Z].*")) {
        String[] lbls = Lbl.split("(?=\\p{Upper})");
        for (String wrd : lbls) {
            // Opsional: di-lowercase agar seragam dengan output di bagian else
            this.tokens.add(wrd.toLowerCase()); 
        }
    } 
    // 2. BAGIAN ELSE: Diganti dengan Hybrid (Longest Match + Statistik)
    else {
        String text = Lbl.toLowerCase();
        int n = text.length();
        
        // Setup Array untuk Dynamic Programming
        double[] minCosts = new double[n + 1];
        int[] splitPositions = new int[n + 1];
        Arrays.fill(minCosts, Double.MAX_VALUE);
        minCosts[0] = 0.0;
        
        // Parameter penelitian jurnalmu (bisa diubah-ubah nilainya nanti)
        double alpha = 2.0;        // Bobot Longest Match (semakin besar, semakin suka kata panjang)
        double unkPenalty = 999.0; // Hukuman jika potongan kata tidak ada di kamus
        
        // Perulangan untuk mengecek semua kombinasi potongan kata
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                String word = text.substring(j, i);
                double cost;
                
                // --- DI SINI KITA TETAP MENGGUNAKAN KAMUSMU ---
                if (this.Dict.getDict().contains(word)) {
                    
                    // Karena ini metode hibrida dengan statistik, kita butuh nilai probabilitasnya.
                    // Asumsinya, kamu menambahkan method getProbability() di class Dict milikmu.
                    // Nilainya berkisar antara 0.0001 hingga 1.0
                    double prob = this.Dict.getProbability(word); 
                    
                    // Rumus Hibrida: Cost = -log(P) dikurangi bonus panjang kata (Longest Match)
                    cost = -Math.log(prob) - (alpha * word.length());
                    
                } else {
                    // Jika kata TIDAK ADA di kamus, berikan penalti besar
                    cost = unkPenalty * word.length();
                }
                
                // Jika jalur pemotongan ini menghasilkan Cost lebih kecil, simpan posisinya!
                if (minCosts[j] != Double.MAX_VALUE && minCosts[j] + cost < minCosts[i]) {
                    minCosts[i] = minCosts[j] + cost;
                    splitPositions[i] = j; // Ingat kita memotong di indeks ke-j
                }
            }
        }
        
        // 3. REKONSTRUKSI HASIL: Mengambil potongan kata dari belakang ke depan
        List<String> result = new ArrayList<>();
        int currentIndex = n;
        while (currentIndex > 0) {
            int prevIndex = splitPositions[currentIndex];
            result.add(text.substring(prevIndex, currentIndex));
            currentIndex = prevIndex;
        }
        
        // Karena diambil dari belakang, urutannya harus dibalik, lalu dimasukkan ke this.tokens
        Collections.reverse(result);
        for (String token : result) {
            this.tokens.add(token);
        }
    }
}
    
    public void DPSplitting(Map<String, Long> freqDict)
    {
        this.freqDict = new HashMap<>(freqDict);
        // hitung total frekuensi untuk normalisasi probabilitas
        int sum = 0;
        int maxLen = 1;
        for (Map.Entry<String, Long> e : freqDict.entrySet()) {
            sum += e.getValue();
            maxLen = Math.max(maxLen, e.getKey().length());
        }
        this.totalCount = sum;
        this.maxWordLength = maxLen;
        // penalti untuk kata yang tidak ada di kamus (angka lebih kecil = probabilitas lebih kecil)
        this.unknownPenalty = 10; // bisa disesuaikan
    }
    
    public List<String> segment(String input) {
        String s = input.toLowerCase();
        int n = s.length();
        // dp[i] = daftar token terbaik untuk substring s[0..i)
        @SuppressWarnings("unchecked")
        List<String>[] dp = new List[n + 1];
        double[] cost = new double[n + 1];
        Arrays.fill(cost, Double.POSITIVE_INFINITY);
        cost[0] = 0;
        dp[0] = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (dp[i] == null) continue;
            int maxj = Math.min(n, i + maxWordLength);
            for (int j = i + 1; j <= maxj; j++) {
                String word = s.substring(i, j);
                double prob = getProbability(word);
                double newCost = cost[i] - Math.log(prob);
                if (newCost < cost[j]) {
                    cost[j] = newCost;
                    dp[j] = new ArrayList<>(dp[i]);
                    dp[j].add(word);
                }
            }
        }
        return dp[n] != null ? dp[n] : Collections.singletonList(s);
    }
    
    private double getProbability(String word) {
        Long freq = freqDict.get(word);
        if (freq != null) {
            // Laplace smoothing: tambah 1 agar tidak nol
            return (freq + 1.0) / (totalCount + unknownPenalty);
        }
        // penalti untuk kata tidak dikenal; semakin besar unknownPenalty semakin tidak dipilih
        return 1.0 / (totalCount + unknownPenalty * totalCount);
    }
}
