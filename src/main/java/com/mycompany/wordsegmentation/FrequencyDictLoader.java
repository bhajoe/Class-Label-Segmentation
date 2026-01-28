/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wordsegmentation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author bayup
 */

public class FrequencyDictLoader {

    public static Map<String, Long> loadFrequencyDict(String filePath) throws IOException {
        Map<String, Long> freqMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                // skip blank lines and comments
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }
                String[] parts = line.split("\\s+");
                if (parts.length < 2) {
                    // ignore lines that don't contain at least a word and a count
                    continue;
                }
                String word = parts[0].toLowerCase();
                long count;
                try {
                    count = Long.parseLong(parts[1]);
                } catch (NumberFormatException e) {
                    // skip lines with invalid number format
                    continue;
                }
                freqMap.put(word, count);
            }
        }
        return freqMap;
    }
}

