/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecobici;

/**
 *
 * @author GerardoSteve
 */
public class Bicicleta {

    private final int Id;
    private final boolean Funcionando;
    
    public Bicicleta (int id, boolean x){
        Id = id;
        Funcionando = x;
    }
    
    public Bicicleta (int id){
        Id=id;
        Funcionando=true;
    }
}
