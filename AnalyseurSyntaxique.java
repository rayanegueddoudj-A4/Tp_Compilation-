
package com.mycompany.projetcompi;

import java.util.List;

public class AnalyseurSyntaxique {
    

    private List<Token> tokens;    // Liste des tokens à analyser
    private int position;          // Position actuelle dans les tokens
    private List<String> erreurs;  // Liste des erreurs syntaxiques
    private Token tokenCourant;    // Token actuel
    
    public AnalyseurSyntaxique(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
        this.erreurs = new java.util.ArrayList<>();
        if (tokens.size() > 0) {
            this.tokenCourant = tokens.get(0);
        }
    }
    
    // Avancer au token suivant
    private void avancer() {
        position++;
        if (position < tokens.size()) {
            tokenCourant = tokens.get(position);
        } else {
            tokenCourant = null;
        }
    }
    
    // Méthode principale d'analyse - Programme
    public void analyser() {
        System.out.println("\n ANALYSE SYNTAXIQUE ");
        
        // Analyse du programme complet
        Programme();
        
        if (erreurs.isEmpty()) {
            System.out.println(" Analyse syntaxique réussie !");
        }
    }
    
    // Programme ::= Instruction*
    private void Programme() {
        while (tokenCourant != null) {
            if (!Instruction()) {
                // Si erreur, on essaye de continuer
                avancer();
            }
        }
    }
    
    // Instruction ::= Declaration | Affectation | BoucleWhile | AutresInstructions
    private boolean Instruction() {
        if (tokenCourant == null) return false;
        
        // Déclaration de variable (int, float, char, double)
        if (estTypeVariable()) {
            return Declaration();
        }
        
        // Affectation (identifiant = ...)
        if (tokenCourant.type.equals("IDENTIFIANT")) {
            return Affectation();
        }
        
        // Boucle while (instruction principale)
        if (tokenCourant.valeur.equals("while")) {
            return W(); // Méthode pour analyser while
        }
        
        // Autres instructions ignorées (for, if, do, else)
        if (tokenCourant.valeur.equals("for") || tokenCourant.valeur.equals("if") || 
            tokenCourant.valeur.equals("do") || tokenCourant.valeur.equals("else")) {
            System.out.println("Info : Instruction '" + tokenCourant.valeur + 
                             "' ignorée (ligne " + tokenCourant.ligne + ")");
            ignorerInstruction();
            return true;
        }
        
        // Accolade fermante
        if (tokenCourant.type.equals("ACCOLADE_FERM")) {
            avancer();
            return true;
        }
        
        return false;
    }
    
    // Declaration ::= Type Identifiant (= Expression)? ;
    private boolean Declaration() {
        Token debut = tokenCourant;
        
        // Type (int, float, char, double)
        if (!estTypeVariable()) {
            ajouterErreur("Type de variable attendu", debut);
            return false;
        }
        avancer();
        
        // Identifiant
        if (tokenCourant == null || !tokenCourant.type.equals("IDENTIFIANT")) {
            ajouterErreur("Nom de variable attendu après le type", tokenCourant);
            return false;
        }
        String nomVar = tokenCourant.valeur;
        avancer();
        
        // Affectation optionnelle (= Expression)
        if (tokenCourant != null && tokenCourant.type.equals("OP_AFFECTATION")) {
            avancer();
            if (!E()) { // Appel de E() pour analyser l'expression
                return false;
            }
        }
        
        // Point-virgule obligatoire
        if (tokenCourant == null || !tokenCourant.type.equals("POINT_VIRGULE")) {
            ajouterErreur("';' attendu après la déclaration", tokenCourant);
            return false;
        }
        avancer();
        
        System.out.println("✓ Déclaration valide : " + nomVar);
        return true;
    }
    
    // Affectation ::= Identifiant = Expression ; | Identifiant ++ ; | Identifiant -- ;
    private boolean Affectation() {
        Token debut = tokenCourant;
        String nomVar = debut.valeur;
        avancer();
        
        // Incrément (++)
        if (tokenCourant != null && tokenCourant.type.equals("OP_INCREMENT")) {
            avancer();
            if (tokenCourant == null || !tokenCourant.type.equals("POINT_VIRGULE")) {
                ajouterErreur("';' attendu après ++", tokenCourant);
                return false;
            }
            avancer();
            System.out.println("✓ Incrémentation valide : " + nomVar);
            return true;
        }
        
        // Décrément (--)
        if (tokenCourant != null && tokenCourant.type.equals("OP_DECREMENT")) {
            avancer();
            if (tokenCourant == null || !tokenCourant.type.equals("POINT_VIRGULE")) {
                ajouterErreur("';' attendu après --", tokenCourant);
                return false;
            }
            avancer();
            System.out.println("✓ Décrémentation valide : " + nomVar);
            return true;
        }
        
        // Affectation (=)
        if (tokenCourant == null || !tokenCourant.type.equals("OP_AFFECTATION")) {
            ajouterErreur("'=' attendu pour l'affectation", tokenCourant);
            return false;
        }
        avancer();
        
        // Expression
        if (!E()) {
            return false;
        }
        
        // Point-virgule
        if (tokenCourant == null || !tokenCourant.type.equals("POINT_VIRGULE")) {
            ajouterErreur("';' attendu après l'affectation", tokenCourant);
            return false;
        }
        avancer();
        
        System.out.println("✓ Affectation valide : " + nomVar);
        return true;
    }
    
    // W() - Analyse de la boucle While
    // BoucleWhile ::= while ( Condition ) BlocOuInstruction
    private boolean W() {
        Token debut = tokenCourant;
        System.out.println("\n→ Analyse de la boucle while (ligne " + debut.ligne + ")");
        
        // Mot-clé 'while'
        if (!tokenCourant.valeur.equals("while")) {
            ajouterErreur("'while' attendu", debut);
            return false;
        }
        avancer();
        
        // Parenthèse ouvrante '('
        if (tokenCourant == null || !tokenCourant.type.equals("PAREN_OUV")) {
            ajouterErreur("'(' attendu après 'while'", tokenCourant);
            return false;
        }
        avancer();
        
        // Condition - Appel de C()
        if (!C()) {
            ajouterErreur("Condition invalide dans le while", tokenCourant);
            return false;
        }
        
        // Parenthèse fermante ')'
        if (tokenCourant == null || !tokenCourant.type.equals("PAREN_FERM")) {
            ajouterErreur("')' attendu après la condition", tokenCourant);
            return false;
        }
        avancer();
        
        // Corps de la boucle (bloc ou instruction simple)
        if (!B()) { // Appel de B() pour le bloc
            return false;
        }
        
        System.out.println("✓ Boucle while valide");
        return true;
    }
    
    // C() - Analyse de Condition
    // Condition ::= ExpressionLogique
    private boolean C() {
        return L(); // Appel de L() pour expression logique
    }
    
    // L() - Analyse d'Expression Logique
    // ExpressionLogique ::= (!? Comparaison) ((&& | ||) Comparaison)*
    private boolean L() {
        // Négation optionnelle (!)
        if (tokenCourant != null && tokenCourant.type.equals("OP_LOGIQUE") && 
            tokenCourant.valeur.equals("!")) {
            avancer();
        }
        
        // Première comparaison
        if (!R()) { // Appel de R() pour comparaison
            return false;
        }
        
        // Opérateurs logiques (&&, ||)
        while (tokenCourant != null && tokenCourant.type.equals("OP_LOGIQUE") && 
               (tokenCourant.valeur.equals("&&") || tokenCourant.valeur.equals("||"))) {
            avancer();
            if (!R()) {
                return false;
            }
        }
        
        return true;
    }
    
    // R() - Analyse de Comparaison
    // Comparaison ::= Expression (OpComparaison Expression)?
    private boolean R() {
        // Première expression
        if (!E()) {
            return false;
        }
        
        // Opérateur de comparaison optionnel
        if (tokenCourant != null && tokenCourant.type.equals("OP_COMPARAISON")) {
            avancer();
            if (!E()) {
                return false;
            }
        }
        
        return true;
    }
    
    // E() - Analyse d'Expression Arithmétique
    // Expression ::= Terme ((+ | -) Terme)*
    private boolean E() {
        // Premier terme
        if (!T()) {
            return false;
        }
        
        // Opérateurs + et -
        while (tokenCourant != null && tokenCourant.type.equals("OP_ARITHMETIQUE") && 
               (tokenCourant.valeur.equals("+") || tokenCourant.valeur.equals("-"))) {
            avancer();
            if (!T()) {
                return false;
            }
        }
        
        return true;
    }
    
    // T() - Analyse de Terme
    // Terme ::= Facteur ((* | / | %) Facteur)*
    private boolean T() {
        // Premier facteur
        if (!F()) {
            return false;
        }
        
        // Opérateurs *, /, %
        while (tokenCourant != null && tokenCourant.type.equals("OP_ARITHMETIQUE") && 
               (tokenCourant.valeur.equals("*") || tokenCourant.valeur.equals("/") || 
                tokenCourant.valeur.equals("%"))) {
            avancer();
            if (!F()) {
                return false;
            }
        }
        
        return true;
    }
    
    // F() - Analyse de Facteur
    // Facteur ::= Nombre | Identifiant | ( Expression )
    private boolean F() {
        if (tokenCourant == null) {
            ajouterErreur("Expression attendue", null);
            return false;
        }
        
        // Nombre
        if (tokenCourant.type.equals("NOMBRE")) {
            avancer();
            return true;
        }
        
        // Identifiant
        if (tokenCourant.type.equals("IDENTIFIANT")) {
            avancer();
            return true;
        }
        
        // Expression entre parenthèses ( Expression )
        if (tokenCourant.type.equals("PAREN_OUV")) {
            avancer();
            if (!E()) {
                return false;
            }
            if (tokenCourant == null || !tokenCourant.type.equals("PAREN_FERM")) {
                ajouterErreur("')' attendu", tokenCourant);
                return false;
            }
            avancer();
            return true;
        }
        
        ajouterErreur("Expression invalide", tokenCourant);
        return false;
    }
    
    // B() - Analyse de Bloc ou Instruction
    // BlocOuInstruction ::= { Instruction* } | Instruction
    private boolean B() {
        // Bloc avec accolades { }
        if (tokenCourant != null && tokenCourant.type.equals("ACCOLADE_OUV")) {
            avancer();
            
            // Instructions dans le bloc
            while (tokenCourant != null && !tokenCourant.type.equals("ACCOLADE_FERM")) {
                Instruction();
            }
            
            // Accolade fermante
            if (tokenCourant == null || !tokenCourant.type.equals("ACCOLADE_FERM")) {
                ajouterErreur("'}' attendu pour fermer le bloc", tokenCourant);
                return false;
            }
            avancer();
            return true;
        }
        
        // Instruction simple (sans accolades)
        return Instruction();
    }
    
    // Vérifier si c'est un type de variable
    private boolean estTypeVariable() {
        if (tokenCourant == null) return false;
        String val = tokenCourant.valeur;
        return val.equals("int") || val.equals("float") || 
               val.equals("char") || val.equals("double");
    }
    
    // Ignorer une instruction non gérée (for, if, etc.)
    private void ignorerInstruction() {
        int niveauAccolades = 0;
        
        while (tokenCourant != null) {
            if (tokenCourant.type.equals("ACCOLADE_OUV")) {
                niveauAccolades++;
                avancer();
            } else if (tokenCourant.type.equals("ACCOLADE_FERM")) {
                if (niveauAccolades > 0) {
                    niveauAccolades--;
                    avancer();
                    if (niveauAccolades == 0) break;
                } else {
                    break;
                }
            } else if (tokenCourant.type.equals("POINT_VIRGULE") && niveauAccolades == 0) {
                avancer();
                break;
            } else {
                avancer();
            }
        }
    }
    
    // Ajouter une erreur
    private void ajouterErreur(String message, Token token) {
        String erreur = "Erreur syntaxique";
        if (token != null) {
            erreur += " ligne " + token.ligne + " : " + message + 
                     " (trouvé : '" + token.valeur + "')";
        } else {
            erreur += " : " + message + " (fin de fichier atteinte)";
        }
        erreurs.add(erreur);
        System.out.println("✗ " + erreur);
    }
    
    // Obtenir les erreurs
    public List<String> getErreurs() {
        return erreurs;
    }
    
    // Vérifier si l'analyse a réussi
    public boolean aReussi() {
        return erreurs.isEmpty();
    }
}
