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
public class Usuario {
    private final int id;
    private final int Ti; // tiempo de inicio de espera
    int Tf; // tiempo de fin de espera
    final int tipo; // si espera para tomar bici o dejarla
    final int est; //  estación de espera
    
    public Usuario(int n, int ti, int t, int e){
        id = n;
        Ti = ti;
        Tf = 0;
        tipo=t; // 1= espera tomar bici, 2= espera dejar bici
        est=e;
    }
    public String toString(){
        String st;
        int t=Tf-Ti;
        if(tipo==1)
            st="usuario "+id+" esperó tomar bici por "+t+
                    " minutos en la estación "+est;
        else
            st="usuario "+id+" esperó dejar bici por "+t+
                    " minutos en la estación "+est;
        return st;
    }
}
