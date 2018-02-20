/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ecobici;
/**
 *
 * @author GerardoSteve
 */
public class EmptyCollectionException extends RuntimeException{
    public EmptyCollectionException(){
        super("Cola vacía");
    }
}
