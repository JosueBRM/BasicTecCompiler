/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:        SEMESTRE: ENE-JUN/2023            HORA: 18-19 HRS
 *:                                   
 *:               
 *:    # Clase con la funcionalidad del Generador de COdigo Objeto
 *                 
 *:                           
 *: Archivo       : GenCodigoInt.java
 *: Autor         : Josue Benjamin Rangel Montiel 19130963
                    Carlos Castorena Lugo 19130899
 *: Fecha         : 23/05/23
 *: Compilador    : Java JDK 19
 *: Descripción   : Dentro de esta clase se encuentran los metodos para 
 *:                 la generacion del codigo intermedio
 *:                  
 *:           	     
 *: Ult.Modif.    : 23/05/23
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
    23/05/23    Josue Rangel    Se agregaron los metodos para la generacion de 
                                codigo intermedio.

 *:-----------------------------------------------------------------------------
 */


package compilador;

import general.Linea_BE;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GenCodigoInt {
     //--------------------------------------------------------------------------
    private Compilador cmp;
    private String     preAnalisis;
    
    //Codigo Intermedio Variables ----------------------------------------------
    public static final int NIL = 0;
    private int consecutivoTemp = 1;
    private int consecutivoEtiq = 1;
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
	public GenCodigoInt ( Compilador c ) {
        cmp = c;
    }
    // Fin del Constructor
    public void generar () {
        consecutivoTemp = 1;
        preAnalisis = cmp.be.preAnalisis.complex;
        
        programa();

    }   
    
    //--------------------------------------------------------------------------
    
    private void emite ( String c3d ) {
        cmp.iuListener.mostrarCodInt( c3d );
    }
    
    //--------------------------------------------------------------------------

    private String tempnuevo (){
        return  "t" + consecutivoTemp++;
    }
    
    //--------------------------------------------------------------------------
    
    private String etiqnueva (){
        return  "etiq" + consecutivoEtiq++;
    }
    
    //--------------------------------------------------------------------------
    
    public static int precedence(String operador) {
        switch (operador) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            default:
                return 0;
        }
    }
    
    public static String infixToPrefix(String infix){
        String inverted = new StringBuilder(infix).reverse().toString();
        String [] inverted2 = inverted.split("(\\s)");
        for (String string : inverted2) {
            System.out.println(string);
        }
        
        // Create a stack to store operators
        Stack<String> operators = new Stack<>();
        
        // Create a StringBuilder to store the prefix expression
        StringBuilder prefix = new StringBuilder();

        for(String s : inverted2){
                
                if (s.equals(" ")) {
                    continue; // Ignore spaces
                }
                if (isLetterOrDigit(s)) {
                    prefix.append(s).append(" "); // Append operand and space
                } else if (")".equals(s)) {
                    operators.push(s);
                } else if ("(".equals(s)) {
                    while (!operators.isEmpty() && 
                            !operators.peek().equals(")")) {
                        prefix.append(operators.pop()).append(" "); // Append operator and space
                    }
                    operators.pop(); // Pop the closing bracket
                } else {
                    while (!operators.isEmpty() 
                            && precedence(operators.peek()) > 
                                precedence(s)) {
                        prefix.append(operators.pop()).append(" "); // Append operator and space
                    }
                    operators.push(s);
                }
        }

        while (!operators.isEmpty()) {
            prefix.append(operators.pop()).append(" "); // Append operator and space
        }

        // Reverse the prefix expression and return
        return prefix.reverse().toString().trim();

    }
    
    public static boolean isLetterOrDigit(String s){
        Pattern pattern = Pattern.compile("([aA-zZ0-9])\\w*");
        Matcher matcher = pattern.matcher(s);
        
        return matcher.find();
    }
    
    public String prefixTo3D(String prefix){
        
        emite(prefix);
        String[] c3d = prefix.split("\\s+");
         for(int i=0; c3d[i].equals(("t"+consecutivoTemp)) || i < c3d.length-1  ;i++)
         {
             if(c3d[i].matches("([\\+\\-\\*\\/])\\w*") && c3d[i+1]
                            .matches("([A-Za-z1-9])\\w*")&& c3d[i+2]
                                .matches("([A-Za-z1-9])\\w*") )
             {
                 //emite(tempnuevo() + " := " + op2 + op1 +  op3);
                 emite(tempnuevo() + " := " + c3d[i+1] + c3d[i] +  c3d[i+2]);
                  c3d[i]="t"+(consecutivoTemp-1);            
                 for (int j = i+1; j < c3d.length-2; j++) {
                    
                         c3d[j]=c3d[j+2];              
                     }
                  i=0;
            }
         }
         emite(tempnuevo() + " := " + c3d[1] + c3d[0] +  c3d[2]);         
         return "t" + consecutivoTemp + "";
     }
        
    // Funciones  D -> R
    
    public String getDomain(String t) {
        String[] partes = t.split("->");
        return partes[0];
        
    }
    
    public String getRange(String t) {
        String[] partes = t.split("->");
        return partes[1];
    }

    //--------------------------------------------------------------------------
    // Metodo que inicia la ejecucion del analisis sintactico predictivo.
    // analizarSemantica : true = realiza el analisis semantico a la par del sintactico
    //                     false= realiza solo el analisis sintactico sin comprobacion semantica
    //--------------------------------------------------------------------------

    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
            preAnalisis = cmp.be.preAnalisis.complex;            
        } else {
            errorEmparejar( t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------
 
    private void errorEmparejar(String _token, String _lexema, int numLinea ) {
        String msjError = "";

        if (_token.equals("id")) {
            msjError += "Se esperaba un identificador";
        } else if (_token.equals("num")) {
            msjError += "Se esperaba una constante entera";
        } else if (_token.equals("num.num")) {
            msjError += "Se esperaba una constante real";
        } else if (_token.equals("literal")) {
            msjError += "Se esperaba una literal";
        } else if (_token.equals("oparit")) {
            msjError += "Se esperaba un operador aritmetico";
        } else if (_token.equals("oprel")) {
            msjError += "Se esperaba un operador relacional";
        } else if (_token.equals("opasig")) {
            msjError += "Se esperaba operador de asignacion";
        } else {
            msjError += "Se esperaba " + _token;
        }
        msjError += " se encontró " + ( _lexema.equals ( "$" )? "fin de archivo" : _lexema ) + 
                    ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

        cmp.me.error(Compilador.ERR_SINTACTICO, msjError);
    }

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico

    private void error(String _descripError) {
        cmp.me.error(cmp.ERR_SINTACTICO, _descripError);
    }

    // Fin de error
    //--------------------------------------------------------------------------
    //  *  *   *   *    PEGAR AQUI EL CODIGO DE LOS PROCEDURES  *  *  *  *
    //--------------------------------------------------------------------------
    
    //Metodo para saber si es palabra reservada
    private boolean isReservedWord(String preanalisis){
           switch(preanalisis){
               case "dim":
               case "function":
               case "sub":
               case "id":
               case "if":
               case "call":
               case "do":
               case "end":
                   return true;
           }
           return false;
    }
    
    private void programa() {
        
        Atributo declaraciones = new Atributo();
        Atributo declaraciones_subprogramas = new Atributo();
        Atributo proposiciones_optativas = new Atributo();
        
        if ( isReservedWord(preAnalisis) ) {
            
            // programa -> declaraciones declaraciones_subprogramas proposiciones_optativas end {1}
            declaraciones ( declaraciones );
            declaracionesSubprogramas ( declaraciones_subprogramas );
            proposicionesOptativas ( proposiciones_optativas );
            emparejar ( "end" );
            

            // -----------------------------------------------------------------            
        } else {
            error ( "[programa]: Error iniciando el programa." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }

//------------------------------------------------------------------------------
   
    private void declaraciones ( Atributo declaraciones ) {
        
        Atributo lista_declaraciones = new Atributo();
        Atributo declaraciones_2     = new Atributo();     
        
        if ( "dim".equals(preAnalisis) ) {
            // declaraciones -> dim lista_declaraciones declaraciones_2 {2}
            emparejar ( "dim" );
            listaDeclaraciones ( lista_declaraciones );
            declaraciones ( declaraciones_2 );
            
        } 
        else {
            // declaraciones -> empty 
        }
    }
//------------------------------------------------------------------------------
    
    private void listaDeclaraciones ( Atributo lista_dec ) {
        
        Linea_BE id                         = new Linea_BE   ();
        Atributo tipo                       = new Atributo ();
        Atributo listaDeclaracionesPrima    = new Atributo ();
        
        if ( preAnalisis.equals ( "id" ) ) {
            
            // Se salvan los atributos de id
            id = cmp.be.preAnalisis;
            
            // lista_declaraciones -> id as tipo lista_declaraciones_prima {4}
            emparejar ( "id" );
            emparejar ( "as" );
            tipo ( tipo );
            listaDeclaracionesPrima ( listaDeclaracionesPrima );
            
            
        } else {
            error ( "[lista_declaraciones]: Se esperaba una declaración." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
//------------------------------------------------------------------------------
    
    
    private void listaDeclaracionesPrima ( Atributo listaDeclaracionesPrima ) {
        
        Atributo listaDeclaraciones = new Atributo ();
        
        if ( ",".equals(preAnalisis) ) {
            
            emparejar ( "," );
            listaDeclaraciones ( listaDeclaraciones );
        }else{
            //lista_declaraciones_prima -> empty 
        }
    }  
//------------------------------------------------------------------------------
    private void tipo ( Atributo tipo ) {
        if ( "integer".equals(preAnalisis) ) {
            emparejar ( "integer" );
           
            
        } else if ( "single".equals(preAnalisis) ) {
            emparejar ( "single" );
            
        } else if ( "string".equals(preAnalisis) ) {
            emparejar ( "string" );

        } else {
            error ( "[tipo]: Tipo de dato erroneo." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
//------------------------------------------------------------------------------
    
    private void declaracionesSubprogramas ( Atributo declaracionesSubprogramas ) {
        
        Atributo declaracion_subprograma = new Atributo ();
        Atributo declaraciones_subprogramas_1 = new Atributo ();
        
        if ( preAnalisis.equals ( "function" ) ||
                preAnalisis.equals ( "sub" ) ) {
            declaracionSubprograma ( declaracion_subprograma );
            declaracionesSubprogramas ( declaraciones_subprogramas_1 );
            
        }else{
            // declaraciones_subprogramas -> empty 
        }
    }
    
    //--------------------------------------------------------------------------
    private void declaracionSubprograma ( Atributo declaracionSubprograma ) {
        
        Atributo declaracionFuncion = new Atributo ();
        Atributo declaracionSubrutina = new Atributo ();
        
        if ( "function".equals(preAnalisis) ) {
            // declaracion_subprograma -> declaracion_funcion 
            declaracionFuncion ( declaracionFuncion );
            
            
        } else if ( "sub".equals(preAnalisis) ) {
            // declaracion_subprograma -> declaracion_subrutina 
            declaracionSubrutina ( declaracionSubrutina );
            
            
        } else {
            error ( "[declaracion_subprograma]: Error de función." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
//------------------------------------------------------------------------------
    
   
    private void declaracionFuncion ( Atributo df ) {
        
        Linea_BE id = new Linea_BE ();
        Atributo argumentos = new Atributo ();
        Atributo tipo = new Atributo ();
        Atributo proposicionesOpt = new Atributo ();
        
        if ( "function".equals(preAnalisis) ) {
            emparejar ( "function" );
            
            // atributos de id
            id = cmp.be.preAnalisis;
            
            emparejar ( "id" );
            argumentos ( argumentos );
            emparejar ( "as" );
            tipo ( tipo );
            proposicionesOptativas ( proposicionesOpt );            

            emparejar ( "end" );
            emparejar ( "function" );
            
        } else {
            error ( "[declaracionFuncion]: Error en la declaracion de funcion" +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    
    
    private void declaracionSubrutina ( Atributo ds ) {
        
        Linea_BE id = new Linea_BE ();
        Atributo argumentos = new Atributo ();
        Atributo proposicionesOpt = new Atributo ();
        
        if ( "sub".equals(preAnalisis) ) {
            emparejar ( "sub" );
            
            id = cmp.be.preAnalisis;
            
            emparejar ( "id" );
            argumentos ( argumentos );
            proposicionesOptativas ( proposicionesOpt );
            
            
            emparejar ( "end" );
            emparejar ( "sub" );
        } else {
            error ( "[declaracion_subrutina]: Error de subrutina" +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
//------------------------------------------------------------------------------
    
    private void argumentos ( Atributo argumentos ) {
        
        Atributo ls = new Atributo ();
        
        if ( "(".equals(preAnalisis) ) {
            // argumentos -> ( lista_declaraciones ) {16}
            emparejar ( "(" );
            listaDeclaraciones ( ls );
            emparejar ( ")" );
               
        }else{
            //argumentos -> empty
        }
            
    }
//------------------------------------------------------------------------------
    
    private void proposicionesOptativas ( Atributo propOpt ) {
        
        Atributo proposicion = new Atributo ();
        Atributo proposiciones_optativas1 = new Atributo ();
        
        if ( preAnalisis.equals ( "id" ) || 
                preAnalisis.equals ( "call" ) ||
                    preAnalisis.equals ( "if" ) ||
                        preAnalisis.equals ( "do" ) ) {
            
            // proposiciones_optativas -> proposicion proposiciones_optativas {18}
            proposicion ( proposicion );
            proposicionesOptativas ( proposiciones_optativas1 );
            
            if ( !propOpt.siguiente.equals("") )
                emite( "goto " + propOpt.siguiente );
            
        } else {
            // proposiciones_optativas -> empty {19}
            
        }
    }
    
//------------------------------------------------------------------------------
    
    private void proposicion ( Atributo proposicion ) {
        
        Atributo expresion = new Atributo ();
        Atributo propPrima = new Atributo ();
        Atributo condicion = new Atributo ();
        Atributo proposicionesOptativas_1 = new Atributo ();
        Atributo proposicionesOptativas_2 = new Atributo ();
        Atributo condicion_1 = new Atributo ();
        Atributo proposicionesOptativas_3 = new Atributo ();
        Linea_BE id = new Linea_BE ();
        
        if ( preAnalisis.equals ( "id" ) ) {
            // proposicion -> id opasig expresion {20}
            
            id = cmp.be.preAnalisis;
            
            emparejar ( "id" );
            emparejar ( "opasig" );
            expresion ( expresion );
            
            //============================ ACCIÓN SEMANTICA 2 ==============================
            String temporal;
            try {
                temporal = this.prefixTo3D( infixToPrefix(expresion.valor) ); 
            } catch ( Exception ex ) {
                temporal = "";
            }

            if ( !temporal.equals(""))
                emite( id.lexema + " := " + temporal );
            else 
                emite( id.lexema + " := " + expresion.valor );
            
            
        } else if ( "call".equals(preAnalisis) ) {
            // proposicion -> call id proposicion_prima {21}
            
            emparejar ( "call" );
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            proposicionPrima( propPrima );
            
            
        } else if ( "if".equals(preAnalisis) ) {
            // proposicion -> if condicion then proposiciones_optativas 
            //      else proposiciones_optativas {22} end if
            emparejar ( "if" );
            
            proposicion.siguiente = etiqnueva();
            condicion.verdadera = etiqnueva();
            condicion.falsa = etiqnueva();
            proposicionesOptativas_1.siguiente = proposicion.siguiente;
            
            condicion ( condicion );
            emparejar ( "then" );
            proposicionesOptativas ( proposicionesOptativas_1 );
            
            emite( condicion.falsa + ":" );
            
            emparejar ( "else" );
            proposicionesOptativas ( proposicionesOptativas_2 );
            
            emite( proposicion.siguiente + ":" );
            
            emparejar ( "end" );
            emparejar ( "if" );
        } else if ( "do".equals(preAnalisis) ) {
            // proposicion -> do while condicion proposiciones_optativas {23} loop
            emparejar ( "do" );
            emparejar ( "while" );
            
            //============================ ACCIÓN SEMANTICA 6 ==============================
            
            proposicion.comienzo = etiqnueva();
            proposicion.siguiente = etiqnueva();
            condicion_1.verdadera = etiqnueva();
            condicion_1.falsa = proposicion.siguiente;
            proposicionesOptativas_3.siguiente = proposicion.comienzo;
            emite( proposicion.comienzo +  ":" );
            
            //===============================================================================
            
            condicion ( condicion_1 );
            proposicionesOptativas ( proposicionesOptativas_3 );
            
            
            //============================ ACCIÓN SEMANTICA 7 ==============================
            
            emite( "goto " + proposicion.comienzo );
            emite( condicion_1.falsa + ":" );
            
            //===============================================================================
            
            emparejar ( "loop" );
        } else {
            error ( "[proposicion]: Error" +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    
    
    private void proposicionPrima ( Atributo propPrim ) {
        
        Atributo le = new Atributo ();
        
        if ( preAnalisis.equals( "(" ) ) {
            // proposicion_prima -> ( lista_expresiones ) {24}
            emparejar ( "(" );
            listaExpresiones ( le );
            emparejar ( ")" );
            
            
        } else{
            // proposicion_prima -> empty 
        }
    }
    
    //------------------------------------------------------------------------------------
    
    private void listaExpresiones ( Atributo le ) {
        
        Atributo expresion = new Atributo ();
        Atributo lista_expresiones_prima = new Atributo ();
        
        
        if ( preAnalisis.equals ( "id" ) || 
                preAnalisis.equals ( "num" ) ||  
                    preAnalisis.equals ( "num.num" ) || 
                        preAnalisis.equals ( "(" ) ||
                            preAnalisis.equals ( "literal" )) {
            // lista_expresiones -> expresion lista_expresiones_prima {26}
            expresion ( expresion );
            listaExpresionesPrima ( lista_expresiones_prima );
            
            
        } else {
            // lista_expresiones -> empty {26}
        }
    }
//------------------------------------------------------------------------------    
    
    private void listaExpresionesPrima ( Atributo listaEP ) {
        
        Atributo expresion = new Atributo ();
        Atributo lista_expresiones_prima1 = new Atributo ();
        
        if ( ",".equals(preAnalisis) ) {
            // lista_expresiones_prima -> , expresion lista_expresiones_prima {27}
            emparejar ( "," );
            expresion ( expresion );
            listaExpresionesPrima ( lista_expresiones_prima1 );
            
        } else {
            // lista_expresiones_prima -> empty {28}
            
        }
    }    
//------------------------------------------------------------------------------
    
    
    private void condicion ( Atributo condicion ) {
        
        Atributo expr1 = new Atributo ();
        Atributo expr2 = new Atributo ();
        Linea_BE oprel = new Linea_BE ();
        
        if ( preAnalisis.equals ( "id" )        ||
                preAnalisis.equals ( "num" )       ||
                    preAnalisis.equals ( "num.num" )   ||
                        preAnalisis.equals ( "(" ) ||
                            preAnalisis.equals ( "literal" )) {
            // condicion -> expresion oprel expresion {29}
            expresion ( expr1 );
            oprel = cmp.be.preAnalisis;
            emparejar ( "oprel" );
            expresion ( expr2 );
            
            //============================ ACCIÓN SEMANTICA 8 ==============================
            
            String tempExpr1 = this.prefixTo3D( infixToPrefix(expr1.valor) );
            String tempExpr2 = this.prefixTo3D( infixToPrefix(expr2.valor) );

            
            emite ( "if " + ( !tempExpr1.equals ("" ) ? tempExpr1 + " " : expr1.valor ) + oprel.lexema + " " + 
                  ( !tempExpr2.equals ( "" ) ? tempExpr2 + " " : expr2.valor ) + "goto " + condicion.verdadera  );
            emite ( "goto " + condicion.falsa );
            emite ( condicion.verdadera + ":" );
            
            //===============================================================================

            
        } else {
            error ( "[condicion]: Error de condición." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    
    
    private void expresion ( Atributo expresion ) {
        
        Atributo termino = new Atributo ();
        Atributo exprPrima = new Atributo ();
        Linea_BE literal = new Linea_BE ();
        
        if ( preAnalisis.equals ( "id" )        ||
                preAnalisis.equals ( "num" )       ||
                    preAnalisis.equals ( "num.num" )   ||
                        preAnalisis.equals ( "(" ) ) {
            // expresion -> termino {30} expresion_prima {31}
            termino ( termino );
            expresionPrima ( exprPrima );
            
            //============================ ACCIÓN SEMANTICA 9 ==============================
            expresion.valor = termino.valor + exprPrima.valor ;
            //===============================================================================
          
        } else if ( preAnalisis.equals ( "literal" ) ) {
            
            // expresion -> literal {10}
            literal = cmp.be.preAnalisis;
            emparejar ( "literal" );
            
            //============================ ACCIÓN SEMANTICA 10 ==============================
            
            expresion.valor = literal.lexema;
            
            //===============================================================================    
        } else {  
            error ( "[expresion]: Expresión no valida." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
//------------------------------------------------------------------------------
    
    
    private void expresionPrima ( Atributo expresion_prima ) {
        
        Atributo termino = new Atributo ();
        Atributo ePrim_1 = new Atributo ();
        
        if ( preAnalisis.equals ( "opsuma" ) ) {
            // expresion_prima -> opsuma termino {33} expresion_prima {34}
            emparejar ( "opsuma" );
            termino ( termino );
            expresionPrima ( ePrim_1 ); 
            
            //============================ ACCIÓN SEMANTICA 11 ==============================
            
            expresion_prima.valor = "+ " + termino.valor + ePrim_1.valor;
            
            //===============================================================================
            
        } else {
            // expresion_prima -> empty {35}            
            //============================ ACCIÓN SEMANTICA 12 ==============================
            
            expresion_prima.valor = "";
            
            //===============================================================================
        }
    }
    
    //--------------------------------------------------------------------------
    
    
    private void termino ( Atributo termino ) {
        
        Atributo factor = new Atributo ();
        Atributo termino_prima = new Atributo ();
        
        if ( preAnalisis.equals ( "id" )        ||
                preAnalisis.equals ( "num" )       ||
                    preAnalisis.equals ( "num.num" )   ||
                        preAnalisis.equals ( "(" ) ) {
            factor ( factor );
            terminoPrimo ( termino_prima );
            
            //============================ ACCIÓN SEMANTICA 13 ==============================
            
            termino.valor = factor.valor + termino_prima.valor;
            
            //===============================================================================
            
        } else {
            error ( "[termino]: Error de término." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    
    private void terminoPrimo ( Atributo terminoPrimo ) {
        
        Atributo factor = new Atributo ();
        Atributo tp_1 = new Atributo ();
        
        if ( "opmult".equals(preAnalisis) ) {
            emparejar ( "opmult" );
            factor ( factor );
            terminoPrimo ( tp_1 );            
            
        } else {
            // termino -> empty {40}
            //============================ ACCIÓN SEMANTICA 15 ==============================
            
            tp_1.valor = "";
            
            //===============================================================================
            
        }
    }
//------------------------------------------------------------------------------
    
    private void factor ( Atributo factor ) {
        
        Linea_BE id = new Linea_BE ();
        Atributo fp = new Atributo ();
        Atributo expresion = new Atributo ();
        
        id = cmp.be.preAnalisis;
        
        if ( "id".equals(preAnalisis) ) {
            // factor -> id factor_prima {41}
            
            emparejar ( "id" );
            //============================ ACCIÓN SEMANTICA 16 ==============================
            
            factor.valor = id.lexema + " ";
            
            //===============================================================================
            factorPrimo ( fp );
            
            
        } else if ( "num".equals(preAnalisis)) {
            // factor -> num {42}
            emparejar ( "num" );
            
// -----------------------------------------------------------------------------
            
        } else if ( "num.num".equals(preAnalisis) ) {
            // factor -> num.num {43}
            emparejar ( "num.num" );
            
            //============================ ACCIÓN SEMANTICA 18 ==============================
            
            factor.valor = id.lexema + " ";
            
            //===============================================================================
            
        } else if ( preAnalisis.equals ( "(" ) ) {
            // factor -> ( expresion ) {44}
            emparejar ( "(" );
            expresion ( expresion );
            emparejar ( ")" );
            
            //============================ ACCIÓN SEMANTICA 19 ==============================
            
            factor.valor = "( " + expresion.valor + ") ";
            
            //===============================================================================

            
        } else {
            error ( "[factor]: Error de variables." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    
    
    private void factorPrimo ( Atributo factorPrimo ) {
        
        Atributo le = new Atributo ();
        
        if ( preAnalisis.equals( "(" ) ) {
            // factor_prima -> ( lista_expresiones ) {45}
            emparejar ( "(" );
            listaExpresiones ( le );
            emparejar ( ")" );
            
        } else {
            // factor_prima -> empty {46}
        }
    }
    
public String getArgumentos(String tipo){
        return tipo.toString();
}

}
