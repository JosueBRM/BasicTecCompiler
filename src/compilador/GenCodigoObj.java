/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:        SEMESTRE: ______________            HORA: ______________ HRS
 *:                                   
 *:               
 *:    # Clase con la funcionalidad del Generador de COdigo Objeto
 *                 
 *:                           
 *: Archivo       : GenCodigoInt.java
 *: Autor         : Fernando Gil  
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   :  
 *:                  
 *:           	     
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 24/May/2023 F.Gil              -Generar la plantilla de programa Ensamblador
 *:-----------------------------------------------------------------------------
 */


package compilador;

import general.Linea_TS;
import general.Linea_BE;
        
public class GenCodigoObj {
 
    private Compilador cmp;

    
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
    public GenCodigoObj ( Compilador c ) {
        cmp = c;
    }
    // Fin del Constructor
    //--------------------------------------------------------------------------
	
    public void generar () {
        genEncabezadoASM ();
        genDeclaraVarsASM();
        genSegmentoCodigo();
        algoritmoGCO     ();
        genPieASM        ();
    }    

    //--------------------------------------------------------------------------
    // Genera las primeras lineas del programa Ensamblador hasta antes de la 
    // declaracion de variables.
    
    private void genEncabezadoASM () {
        cmp.iuListener.mostrarCodObj ( "TITLE CodigoObjeto ( codigoObjeto.asm )"  );
        cmp.iuListener.mostrarCodObj ( "; Descripción del programa: Automatas II" );
        cmp.iuListener.mostrarCodObj ( "; Fecha de creacion: Ene-Jun/2023"        );
        cmp.iuListener.mostrarCodObj ( "; Revisiones:" );
        cmp.iuListener.mostrarCodObj ( "; Fecha de ult. modificacion:" );
        cmp.iuListener.mostrarCodObj ( "" );
        cmp.iuListener.mostrarCodObj ( "; INCLUDE Irvine32.inc" );
        cmp.iuListener.mostrarCodObj ( "; (aqui se insertan las definiciones de simbolos)" );
        cmp.iuListener.mostrarCodObj ( "" );
        cmp.iuListener.mostrarCodObj ( ".data" );
        cmp.iuListener.mostrarCodObj ( "  ; (aqui se insertan las variables)" );        
    }
    
    //--------------------------------------------------------------------------
    // Genera las lineas en Ensamblador de Declaracion de variables.
    // Todas las variables serán DWORD ya que por simplificacion solo se genera
    // codigo objeto de programas fuente que usaran solo variables enteras.
    
    private void genDeclaraVarsASM () {
        for ( int i = 1; i < cmp.ts.getTamaño (); i++ ) {
            // Por cada entrada en la Tabla de Simbolos...
            Linea_TS elemento = cmp.ts.obt_elemento( i );
            String variable = elemento.getLexema();
            
            // Genera una declaracion de variable solo si se trata de un id
            if ( elemento.getComplex().equals ( "id" ) ) 
                cmp.iuListener.mostrarCodObj ( "  " + variable + " DWORD 0" );
        }
        cmp.iuListener.mostrarCodObj ( "" );
    }
    
    //--------------------------------------------------------------------------
    
    private void genSegmentoCodigo () {
        cmp.iuListener.mostrarCodObj ( ".code" );
        cmp.iuListener.mostrarCodObj ( "main PROC" );
        cmp.iuListener.mostrarCodObj ( "  ; (aqui se insertan las instrucciones ejecutables)" );
    }
    
    //--------------------------------------------------------------------------
    // Genera las lineas en Ensamblador de finalizacion del programa
    
    private void genPieASM () {
        cmp.iuListener.mostrarCodObj ( "  exit" );
        cmp.iuListener.mostrarCodObj ( "main ENDP" );
        cmp.iuListener.mostrarCodObj ( "" );
        cmp.iuListener.mostrarCodObj ( "; (aqui se insertan los procedimientos adicionales)" );
        cmp.iuListener.mostrarCodObj ( "END main" );
    }
    
    //--------------------------------------------------------------------------
    // Algoritmo de generacion de codigo en ensamblador
    
    private void algoritmoGCO () {
        for (int i = 0; i < cmp.be.getTamaño(); i++) {
            Linea_BE be = cmp.be.obtElemento(i);
            String lexema = be.getLexema();
            System.out.println(lexema);
            
        }
    }
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    
}