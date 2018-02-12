/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package población;

/**
 *
 * @author LabLogistica
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.math3.distribution.UniformRealDistribution;

public class Población {

    static int[] espacios;
    static char[] perfil;
    static int[][] bicicletas;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        int estaciones=452;
        int corridas=10;
        espacios = new int[estaciones];
        perfil = new char[estaciones];
        bicicletas=new int[estaciones][corridas];
        BufferedReader br = null;
        try {
            br =new BufferedReader(new FileReader("C:\\Users\\lablogistica\\"
                    + "Desktop\\ecobici\\Perfiles.csv"));
            for (int i = 0; i < estaciones; i++) {
                String line = br.readLine();
                String [] fields = line.split(",");
                espacios[i]=Integer.valueOf(fields[0]);
                perfil[i]=fields[1].charAt(0);
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
        UniformRealDistribution E= new UniformRealDistribution(0,0.3);
        UniformRealDistribution e= new UniformRealDistribution(0.2,0.5);
        UniformRealDistribution A= new UniformRealDistribution(0.3,0.7);
        UniformRealDistribution b= new UniformRealDistribution(0.5,0.8);
        UniformRealDistribution B= new UniformRealDistribution(0.7,1);
        
        for (int j = 0; j < corridas; j++)
            for (int i = 0; i < estaciones; i++)
                if(perfil[i]=='A')
                    bicicletas[i][j]=(int)Math.round(A.sample()*espacios[i]);
                else if(perfil[i]=='b')
                    bicicletas[i][j]=(int)Math.round(b.sample()*espacios[i]);
                else if(perfil[i]=='B')
                    bicicletas[i][j]=(int)Math.round(B.sample()*espacios[i]);
                else if(perfil[i]=='e')
                    bicicletas[i][j]=(int)Math.round(e.sample()*espacios[i]);
                else
                    bicicletas[i][j]=(int)Math.round(E.sample()*espacios[i]);
        
        String c="C:\\Users\\lablogistica\\Desktop\\ecobici\\Población.csv";
        FileWriter w=null;
        try{
            w = new FileWriter(c);
            for (int m = 0; m < estaciones; m++){
                w.append(espacios[m]+","+perfil[m]+",");
                for (int i = 0; i < corridas; i++)
                    w.append(bicicletas[m][i]+",");
                w.append("\n");
            }
        } catch (Exception ex) {
           System.err.println("Error! "+ex.getMessage());
        } finally {
           if (null!=w){
              try {
                 w.flush();
              } catch (IOException ex) {
                 System.err.println("Error flushing file !! "+ex.getMessage());
              }
              try {
                 w.close();
              } catch (IOException ex) {
                 System.err.println("Error closing file !! "+ex.getMessage());
              }
           }
        }
    }
    
}
