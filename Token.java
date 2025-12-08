
package com.mycompany.projetcompi;

import java.util.ArrayList;
import java.util.List;
public class Token {
    
    String type;      // Type du token (MOT_CLE, IDENTIFIANT, etc.)
    String valeur;    // Valeur du token
    int ligne;        // Numéro de ligne
    
    public Token(String type, String valeur, int ligne) {
        this.type = type;
        this.valeur = valeur;
        this.ligne = ligne;
    }
    
    @Override
    public String toString() {
        return "Token[" + type + ", '" + valeur + "', ligne " + ligne + "]";
    }
}
    

