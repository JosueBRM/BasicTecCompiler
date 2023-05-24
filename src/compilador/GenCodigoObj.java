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
 *:                 la generacion del codigo objeto
 *:                  
 *:           	     
 *: Ult.Modif.    : 23/05/23
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
    23/05/23    Josue Rangel    Se agregaron los metodos para la generacion de 
                                codigo objeto.

 *:-----------------------------------------------------------------------------
 */


package compilador;


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
    }    
}
