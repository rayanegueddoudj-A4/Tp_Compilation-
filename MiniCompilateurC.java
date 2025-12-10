
package com.mycompany.projetcompi;

import com.mycompany.projetcompi.Token;
import java.util.Scanner;


public class MiniCompilateurC {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("   \n    ");
        System.out.println("   MINI-COMPILATEUR C - BOUCLE WHILE");
        System.out.println("  \n     ");
        System.out.println();
        
        System.out.println("Entrez votre code C (terminez par une ligne vide) :");
        System.out.println("Exemple : int i = 0;");
        System.out.println("          while (i < 10) {");
        System.out.println("              i++;");
        System.out.println("          }");
        System.out.println();
        
        // Lire le code ligne par ligne
        String code = "";
        String ligne;
        int lignesVides = 0;
        
        while (true) {
            ligne = scanner.nextLine();
            
            // Si ligne vide
            if (ligne.trim().isEmpty()) {
                lignesVides++;
                if (lignesVides >= 2) {
                    break; // 2 lignes vides = fin
                }
            } else {
                lignesVides = 0;
                code = code + ligne + "\n";
            }
        }
        
        // Verifier si code vide
        if (code.trim().isEmpty()) {
            System.out.println("Aucun code saisi.");
            scanner.close();
            return;
        }
        
        System.out.println("\n");
        System.out.println("   DEBUT DE L'ANALYSE");
        System.out.println(" \n ");
        
        // Etape 1 : Analyse lexicale
        System.out.println("--- ANALYSE LEXICALE ---");
        AnalyseurLexical lexer = new AnalyseurLexical(code);
        lexer.analyser();
        
        // Afficher les tokens
        System.out.println("\nTokens trouves : " + lexer.getTokens().size());
        for (int i = 0; i < lexer.getTokens().size(); i++) {
            Token t = lexer.getTokens().get(i);
            System.out.println("  " + (i+1) + ". " + t.type + " : '" + t.valeur + "' (ligne " + t.ligne + ")");
        }
        
        // Afficher erreurs lexicales
        if (lexer.getErreurs().size() > 0) {
            System.out.println("\nERREURS LEXICALES :");
            for (int i = 0; i < lexer.getErreurs().size(); i++) {
                System.out.println("  " + lexer.getErreurs().get(i));
            }
        } else {
            System.out.println("\nAucune erreur lexicale.");
        }
        
        // Etape 2 : Analyse syntaxique
        if (lexer.getErreurs().size() == 0) {
            System.out.println("\n--- ANALYSE SYNTAXIQUE ---");
            AnalyseurSyntaxique parser = new AnalyseurSyntaxique(lexer.getTokens());
            parser.analyser();
            
            // Afficher erreurs syntaxiques
            if (parser.getErreurs().size() > 0) {
                System.out.println("\nERREURS SYNTAXIQUES :");
                for (int i = 0; i < parser.getErreurs().size(); i++) {
                    System.out.println("  " + parser.getErreurs().get(i));
                }
            } else {
                System.out.println("\nAucune erreur syntaxique.");
            }
            
            // Resume final
            System.out.println("   \n   ");
            System.out.println("   RESUME");
            System.out.println("   \n   ");
            System.out.println("Tokens          : " + lexer.getTokens().size());
            System.out.println("Erreurs lexicales    : " + lexer.getErreurs().size());
            System.out.println("Erreurs syntaxiques  : " + parser.getErreurs().size());
            
            if (parser.getErreurs().size() == 0) {
                System.out.println("\n COMPILATION REUSSIE ");
            } else {
                System.out.println("\n  COMPILATION ECHOUEE ");
            }
        } else {
            System.out.println("\nAnalyse syntaxique non effectuee (erreurs lexicales).");
        }
        
        System.out.println("\n ___________");
        
        scanner.close();
    }
}