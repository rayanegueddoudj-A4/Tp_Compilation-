
package com.mycompany.projetcompi;

import java.util.ArrayList;
import java.util.List;
public class AnalyseurLexical {
        private String code;           // Code source à analyser
    private int position;          // Position actuelle dans le code
    private int ligne;             // Ligne actuelle
    private List<Token> tokens;    // Liste des tokens trouvés
    private List<String> erreurs;  // Liste des erreurs
    
    // Mots-clés du langage C
    private String[] motsCles = {
        "int", "float", "char", "double", "void",
        "if", "else", "while", "do", "for",
        "return", "break", "continue"
    };
    
    // Mots-clés personnalisés 
    private String[] motsClesPerso = {
        "Rayane", "Gueddoudj"
    };
    
    public AnalyseurLexical(String code) {
        this.code = code;
        this.position = 0;
        this.ligne = 1;
        this.tokens = new ArrayList<>();
        this.erreurs = new ArrayList<>();
    }
    
    // Méthode principale d'analyse
    public void analyser() {
        while (position < code.length()) {
            char c = code.charAt(position);
            
            // Ignorer les espaces et tabulations
            if (c == ' ' || c == '\t' || c == '\r') {
                position++;
                continue;
            }
            
            // Gérer les retours à la ligne
            if (c == '\n') {
                ligne++;
                position++;
                continue;
            }
            
            // Ignorer les commentaires
            if (c == '/' && position + 1 < code.length()) {
                if (code.charAt(position + 1) == '/') {
                    // Commentaire sur une ligne
                    ignorerCommentaireLigne();
                    continue;
                } else if (code.charAt(position + 1) == '*') {
                    // Commentaire multi-lignes
                    ignorerCommentaireBloc();
                    continue;
                }
            }
            
            // Reconnaître les identifiants et mots-clés
            if (Character.isLetter(c) || c == '_') {
                lireIdentifiant();
                continue;
            }
            
            // Reconnaître les nombres
            if (Character.isDigit(c)) {
                lireNombre();
                continue;
            }
            
            // Reconnaître les opérateurs et symboles
            if (reconnaitreOperateur()) {
                continue;
            }
            
            // Caractère non reconnu = erreur lexicale
            erreurs.add("Erreur lexicale ligne " + ligne + " : caractère invalide '" + c + "'");
            position++;
        }
    }
    
    // Ignorer un commentaire sur une ligne (//)
    private void ignorerCommentaireLigne() {
        while (position < code.length() && code.charAt(position) != '\n') {
            position++;
        }
    }
    
    // Ignorer un commentaire multi-lignes (/* ... */)
    private void ignorerCommentaireBloc() {
        position += 2; // Sauter /*
        while (position < code.length() - 1) {
            if (code.charAt(position) == '\n') {
                ligne++;
            }
            if (code.charAt(position) == '*' && code.charAt(position + 1) == '/') {
                position += 2;
                return;
            }
            position++;
        }
        erreurs.add("Erreur lexicale ligne " + ligne + " : commentaire non fermé");
    }
    
    // Lire un identifiant ou mot-clé
    private void lireIdentifiant() {
        int debut = position;
        while (position < code.length() && 
               (Character.isLetterOrDigit(code.charAt(position)) || code.charAt(position) == '_')) {
            position++;
        }
        String mot = code.substring(debut, position);
        
        // Vérifier si c'est un mot-clé
        String type = "IDENTIFIANT";
        for (String mc : motsCles) {
            if (mot.equals(mc)) {
                type = "MOT_CLE";
                break;
            }
        }
        
        // Vérifier si c'est un mot-clé personnalisé
        for (String mcp : motsClesPerso) {
            if (mot.equals(mcp)) {
                type = "MOT_CLE_PERSO";
                break;
            }
        }
        
        tokens.add(new Token(type, mot, ligne));
    }
    
    // Lire un nombre
    private void lireNombre() {
        int debut = position;
        while (position < code.length() && Character.isDigit(code.charAt(position))) {
            position++;
        }
        
        // Gérer les nombres décimaux
        if (position < code.length() && code.charAt(position) == '.') {
            position++;
            while (position < code.length() && Character.isDigit(code.charAt(position))) {
                position++;
            }
        }
        
        String nombre = code.substring(debut, position);
        tokens.add(new Token("NOMBRE", nombre, ligne));
    }
    
    // Reconnaître les opérateurs et symboles
    private boolean reconnaitreOperateur() {
        char c = code.charAt(position);
        
        // Opérateurs à deux caractères
        if (position + 1 < code.length()) {
            String deuxChar = "" + c + code.charAt(position + 1);
            switch (deuxChar) {
                case "==":
                    tokens.add(new Token("OP_COMPARAISON", "==", ligne));
                    position += 2;
                    return true;
                case "!=":
                    tokens.add(new Token("OP_COMPARAISON", "!=", ligne));
                    position += 2;
                    return true;
                case "<=":
                    tokens.add(new Token("OP_COMPARAISON", "<=", ligne));
                    position += 2;
                    return true;
                case ">=":
                    tokens.add(new Token("OP_COMPARAISON", ">=", ligne));
                    position += 2;
                    return true;
                case "&&":
                    tokens.add(new Token("OP_LOGIQUE", "&&", ligne));
                    position += 2;
                    return true;
                case "||":
                    tokens.add(new Token("OP_LOGIQUE", "||", ligne));
                    position += 2;
                    return true;
                case "++":
                    tokens.add(new Token("OP_INCREMENT", "++", ligne));
                    position += 2;
                    return true;
                case "--":
                    tokens.add(new Token("OP_DECREMENT", "--", ligne));
                    position += 2;
                    return true;
            }
        }
        
        // Opérateurs à un caractère
        switch (c) {
            case '+':
                tokens.add(new Token("OP_ARITHMETIQUE", "+", ligne));
                position++;
                return true;
            case '-':
                tokens.add(new Token("OP_ARITHMETIQUE", "-", ligne));
                position++;
                return true;
            case '*':
                tokens.add(new Token("OP_ARITHMETIQUE", "*", ligne));
                position++;
                return true;
            case '/':
                tokens.add(new Token("OP_ARITHMETIQUE", "/", ligne));
                position++;
                return true;
            case '%':
                tokens.add(new Token("OP_ARITHMETIQUE", "%", ligne));
                position++;
                return true;
            case '=':
                tokens.add(new Token("OP_AFFECTATION", "=", ligne));
                position++;
                return true;
            case '<':
                tokens.add(new Token("OP_COMPARAISON", "<", ligne));
                position++;
                return true;
            case '>':
                tokens.add(new Token("OP_COMPARAISON", ">", ligne));
                position++;
                return true;
            case '!':
                tokens.add(new Token("OP_LOGIQUE", "!", ligne));
                position++;
                return true;
            case '(':
                tokens.add(new Token("PAREN_OUV", "(", ligne));
                position++;
                return true;
            case ')':
                tokens.add(new Token("PAREN_FERM", ")", ligne));
                position++;
                return true;
            case '{':
                tokens.add(new Token("ACCOLADE_OUV", "{", ligne));
                position++;
                return true;
            case '}':
                tokens.add(new Token("ACCOLADE_FERM", "}", ligne));
                position++;
                return true;
            case ';':
                tokens.add(new Token("POINT_VIRGULE", ";", ligne));
                position++;
                return true;
            case ',':
                tokens.add(new Token("VIRGULE", ",", ligne));
                position++;
                return true;
        }
        
        return false;
    }
    
    // Obtenir les tokens
    public List<Token> getTokens() {
        return tokens;
    }
    
    // Obtenir les erreurs
    public List<String> getErreurs() {
        return erreurs;
    }
    
    // Afficher les tokens
    public void afficherTokens() {
        System.out.println("\n=== ANALYSE LEXICALE ===");
        System.out.println("Nombre de tokens trouvés : " + tokens.size());
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
    
    // Afficher les erreurs
    public void afficherErreurs() {
        if (!erreurs.isEmpty()) {
            System.out.println("\n=== ERREURS LEXICALES ===");
            for (String erreur : erreurs) {
                System.out.println(erreur);
            }
        }
    }
}
    

