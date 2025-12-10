import java.util.ArrayList;


public class AnalyseurSyntaxique {
    private ArrayList<Token> tokens;
    private int position;
    private Token tokenActuel;
    private ArrayList<String> erreurs;
    
    public AnalyseurSyntaxique(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
        this.erreurs = new ArrayList<String>();
        if (tokens.size() > 0) {
            this.tokenActuel = tokens.get(0);
        }
    }
    
    // Avancer au token suivant
    private void avancer() {
        position++;
        if (position < tokens.size()) {
            tokenActuel = tokens.get(position);
        } else {
            tokenActuel = null;
        }
    }
    
    // Verifier le type du token
    private boolean verifierType(String type) {
        return tokenActuel != null && tokenActuel.type.equals(type);
    }
    
    // Verifier la valeur du token
    private boolean verifierValeur(String valeur) {
        return tokenActuel != null && tokenActuel.valeur.equals(valeur);
    }
    
    // Methode principale
    public void analyser() {
        while (tokenActuel != null) {
            analyserInstruction();
        }
        
        if (erreurs.size() == 0) {
            System.out.println("\nAnalyse syntaxique reussie !");
        }
    }
    
    // Analyser une instruction
    private void analyserInstruction() {
        if (tokenActuel == null) return;
        
        // Declaration (int, float, char, double)
        if (verifierValeur("int") || verifierValeur("float") || 
            verifierValeur("char") || verifierValeur("double")) {
            analyserDeclaration();
            return;
        }
        
        // Boucle while
        if (verifierValeur("while")) {
            analyserWhile();
            return;
        }
        
        // Affectation (x = 5; ou x++;)
        if (verifierType("IDENTIFIANT")) {
            analyserAffectation();
            return;
        }
        
        // Autres instructions (for, if, do) - on les ignore
        if (verifierValeur("for") || verifierValeur("if") || 
            verifierValeur("do") || verifierValeur("else")) {
            System.out.println("Info : '" + tokenActuel.valeur + "' ignore (ligne " + tokenActuel.ligne + ")");
            ignorerInstruction();
            return;
        }
        
        // Accolade fermante
        if (verifierType("ACCOLADE_FERM")) {
            avancer();
            return;
        }
        
        avancer();
    }
    
    // Declaration : int x = 5;
    private void analyserDeclaration() {
        avancer(); // type
        
        if (!verifierType("IDENTIFIANT")) {
            ajouterErreur("Nom de variable attendu");
            return;
        }
        String nom = tokenActuel.valeur;
        avancer();
        
        // = Expression (optionnel)
        if (verifierType("OP_AFFECTATION")) {
            avancer();
            analyserExpression();
        }
        
        // ;
        if (!verifierType("POINT_VIRGULE")) {
            ajouterErreur("';' attendu");
            return;
        }
        avancer();
        
        System.out.println("Declaration valide : " + nom);
    }
    
    // Affectation : x = 5; ou x++;
    private void analyserAffectation() {
        String nom = tokenActuel.valeur;
        avancer();
        
        // x++ ou x--
        if (verifierType("OP_INCREMENT")) {
            avancer();
            if (verifierType("POINT_VIRGULE")) {
                avancer();
                System.out.println("Incrementation valide : " + nom);
            }
            return;
        }
        
        if (verifierType("OP_DECREMENT")) {
            avancer();
            if (verifierType("POINT_VIRGULE")) {
                avancer();
                System.out.println("Decrementation valide : " + nom);
            }
            return;
        }
        
        // x = expression
        if (!verifierType("OP_AFFECTATION")) {
            ajouterErreur("'=' attendu");
            return;
        }
        avancer();
        
        analyserExpression();
        
        if (!verifierType("POINT_VIRGULE")) {
            ajouterErreur("';' attendu");
            return;
        }
        avancer();
        
        System.out.println("Affectation valide : " + nom);
    }
    
    // While : while (condition) { ... }
    private void analyserWhile() {
        System.out.println("\nAnalyse de while (ligne " + tokenActuel.ligne + ")");
        avancer(); // while
        
        // (
        if (!verifierType("PAREN_OUV")) {
            ajouterErreur("'(' attendu apres while");
            return;
        }
        avancer();
        
        // condition
        analyserCondition();
        
        // )
        if (!verifierType("PAREN_FERM")) {
            ajouterErreur("')' attendu");
            return;
        }
        avancer();
        
        // bloc { }
        analyserBloc();
        
        System.out.println("Boucle while valide");
    }
    
    // Condition : x < 10 ou x > 0 && y < 100
    private void analyserCondition() {
        // ! (negation)
        if (verifierType("OP_LOGIQUE") && verifierValeur("!")) {
            avancer();
        }
        
        // expression
        analyserExpression();
        
        // operateur de comparaison
        if (verifierType("OP_COMPARAISON")) {
            avancer();
            analyserExpression();
        }
        
        // && ou ||
        while (verifierType("OP_LOGIQUE")) {
            avancer();
            
            // negation
            if (verifierType("OP_LOGIQUE") && verifierValeur("!")) {
                avancer();
            }
            
            analyserExpression();
            
            if (verifierType("OP_COMPARAISON")) {
                avancer();
                analyserExpression();
            }
        }
    }
    
    // Expression : 5 + 3 * 2
    private void analyserExpression() {
        analyserTerme();
        
        // + ou -
        while (verifierType("OP_ARITHMETIQUE") && 
               (verifierValeur("+") || verifierValeur("-"))) {
            avancer();
            analyserTerme();
        }
    }
    
    // Terme : 3 * 2 ou 10 / 5
    private void analyserTerme() {
        analyserFacteur();
        
        // * ou / ou %
        while (verifierType("OP_ARITHMETIQUE") && 
               (verifierValeur("*") || verifierValeur("/") || verifierValeur("%"))) {
            avancer();
            analyserFacteur();
        }
    }
    
    // Facteur : nombre ou variable ou (expression)
    private void analyserFacteur() {
        if (tokenActuel == null) {
            ajouterErreur("Expression attendue");
            return;
        }
        
        // nombre
        if (verifierType("NOMBRE")) {
            avancer();
            return;
        }
        
        // variable
        if (verifierType("IDENTIFIANT")) {
            avancer();
            return;
        }
        
        // (expression)
        if (verifierType("PAREN_OUV")) {
            avancer();
            analyserExpression();
            if (verifierType("PAREN_FERM")) {
                avancer();
            } else {
                ajouterErreur("')' attendu");
            }
            return;
        }
        
        ajouterErreur("Expression invalide");
    }
    
    // Bloc : { instructions }
    private void analyserBloc() {
        // { instructions }
        if (verifierType("ACCOLADE_OUV")) {
            avancer();
            while (!verifierType("ACCOLADE_FERM") && tokenActuel != null) {
                analyserInstruction();
            }
            if (verifierType("ACCOLADE_FERM")) {
                avancer();
            }
            return;
        }
        
        // instruction simple
        analyserInstruction();
    }
    
    // Ignorer une instruction (for, if, etc.)
    private void ignorerInstruction() {
        int accolades = 0;
        
        while (tokenActuel != null) {
            if (verifierType("ACCOLADE_OUV")) {
                accolades++;
                avancer();
            } else if (verifierType("ACCOLADE_FERM")) {
                if (accolades > 0) {
                    accolades--;
                    avancer();
                    if (accolades == 0) break;
                } else {
                    break;
                }
            } else if (verifierType("POINT_VIRGULE") && accolades == 0) {
                avancer();
                break;
            } else {
                avancer();
            }
        }
    }
    
    // Ajouter une erreur
    private void ajouterErreur(String message) {
        String erreur = "Erreur syntaxique";
        if (tokenActuel != null) {
            erreur += " ligne " + tokenActuel.ligne + " : " + message;
        } else {
            erreur += " : " + message;
        }
        erreurs.add(erreur);
        System.out.println(erreur);
    }
    
    // Obtenir les erreurs
    public ArrayList<String> getErreurs() {
        return erreurs;
    }
}