/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perfilador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author LabLogistica
 */
public class Perfilador {

    /**
     * @param args the command line arguments
     */
    static int estaciones,parámetros;
    static double repeticiones;
    static double[][] resultados;
    static double vacío,lleno,uet,ued,tet,ted;
    static double balance,inef;
    static double espera,tiempo;
    static String[] perfiles;
    static int a,b,bb,e,ee;
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        estaciones=452;
        repeticiones=15;
        parámetros=11;
        
        perfiles=new String[estaciones];
        a=0;b=0;bb=0;e=0;ee=0;
        
        for (int i = 0; i < estaciones; i++) {
            vacío=0;lleno=0;uet=0;ued=0;tet=0;ted=0;
            balance =0;inef=0;
            espera=0;tiempo=0;
            BufferedReader br = null;
            String s="C:\\Users\\lablogistica\\Desktop\\ecobici\\Est"+(i+1)+
                    ".csv";
            resultados=new double[parámetros][(int)repeticiones];
            try {
                br =new BufferedReader(new FileReader(s));
                for (int j = 0; j < parámetros; j++) {
                    String line = br.readLine();
                    String [] fields = line.split(",");
                    for (int k = 0; k < repeticiones; k++) {
                        resultados[j][k]=Double.valueOf(fields[k+1]);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error! "+e.getMessage());
            } finally {
                if (null!=br){
                    try {
                        br.close();
                    } catch (IOException e) {
                        System.err.println("Error closing file !! "+e.getMessage());
                    }
               }
            }
            for (int j = 0; j < repeticiones; j++) {
                balance+=resultados[10][j];
                uet+=resultados[2][j];
                ued+=resultados[7][j];
//                vacío+=resultados[4][j];
//                lleno+=resultados[9][j];
                tet+=resultados[3][j];
                ted+=resultados[8][j];
            }
            balance=balance/repeticiones;
//            inef=(lleno-vacío)/repeticiones;
            espera=(ued-uet)/repeticiones;
            tiempo=(ted-tet)/repeticiones;
            
//            System.out.println("Estación "+(i+1)+": espera tomar="+uet+
//                    ", espera dejar="+ued+", tiempo tomar="+tet+
//                    ", tiempo dejar="+ted);
                
                
            
            if(Math.abs(espera)<.01||Math.abs(tiempo)<0.5){
                a++;
                perfiles[i]="A";
//                System.out.println("Balance: "+balance);
            }
            else if(espera<=-.15||tiempo<=-10){
                perfiles[i]="B";
                bb++;
            }
            else if((-0.15<espera&&espera<=-.01)||(-10<tiempo&&tiempo<=0.5)){
                perfiles[i]="b";
                b++;
            }
            else if(espera>=.15||tiempo>=10){
                perfiles[i]="E";
                ee++;
            }
            else{
//                System.out.println("Estación "+(i+1)
//                    +" espera="+espera+", tiempo="+tiempo);
                perfiles[i]="e";
                e++;
            }
        }
        String c="C:\\Users\\lablogistica\\Desktop\\ecobici\\Perfiles.csv";
        FileWriter w=null;
        try{
            w = new FileWriter(c);
            for (int m = 0; m < estaciones; m++){
                w.append((m+1)+","+perfiles[m]+"\n");
            }
            
        } catch (Exception e) {
           System.err.println("Error! "+e.getMessage());
        } finally {
           if (null!=w){
              try {
                 w.flush();
              } catch (IOException e) {
                 System.err.println("Error flushing file !! "+e.getMessage());
              }
              try {
                 w.close();
              } catch (IOException e) {
                 System.err.println("Error closing file !! "+e.getMessage());
              }
           }
        }
        System.out.println("Autobalanceables: "+a);
        System.out.println("Demandantes de bicicletas: "+b);
        System.out.println("Muy demandantes de bicicletas: "+bb);
        System.out.println("Demandantes de espacios: "+e);
        System.out.println("Muy demandantes de espacios: "+ee);
        System.out.println("Total: "+(a+b+bb+e+ee));
    }
    
}
