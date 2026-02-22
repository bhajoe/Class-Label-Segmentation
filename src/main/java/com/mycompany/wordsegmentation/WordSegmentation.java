/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.wordsegmentation;

import java.io.IOException;

/**
 *
 * @author bayup
 */
public class WordSegmentation {

    public static void main(String[] args) throws IOException {
        //luceneTokenizer lt = new luceneTokenizer();
        //lt.Tokenizing("getaccount");
        //System.out.println(lt.getTokens());
        
        HybridTokenizer ht = new HybridTokenizer();
        ht.tokenize("usesocket");
        System.out.println(ht.getToken());
    }
}
