package oomprojects;

import java.io.IOException;

public class Oomprojects {
 
    public static void main ( String[] args){ 
        
        System.out.println("=======================================");
        System.out.println("     WELCOME TO MEDILINK SYSTEM        ");
        System.out.println("=======================================");
        
        try { 
            MediLinkSystem sys =  new MediLinkSystem();
            sys.run();
            
        }catch (IOException e){
            System.out.println("Failed to start system: " + e.getMessage());
        }
        
        System.out.println("\n======================================");
        System.out.println("     THANK YOU FOR USING MEDILINK      ");
        System.out.println("=======================================");
    }
    
}