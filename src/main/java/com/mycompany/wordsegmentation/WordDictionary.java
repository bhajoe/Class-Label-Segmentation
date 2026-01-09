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
}
