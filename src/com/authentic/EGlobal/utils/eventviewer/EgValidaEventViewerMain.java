package com.authentic.EGlobal.utils.eventviewer;

import java.util.ArrayList;

import com.authentic.EGlobal.utils.eventviewer.dto.EgDTOEventViewer;
import com.authentic.EGlobal.utils.eventviewer.bd.EgValidaEventBD;

public class EgValidaEventViewerMain {
  
  public static void main(String[] args) {

	    if (args.length != 2) {
	        System.err.println(String.valueOf(args.length) + " Faltan argumentos.");
	        System.exit(1);
	      } 
	  
    try {
    	EgValidaEventBD egValidaEventBD = EgValidaEventBD.getInstance();
    	ArrayList<EgDTOEventViewer> resultado = egValidaEventBD.obtenerRegistrosEventViewer();
    	for(EgDTOEventViewer aux : resultado) {
    		System.out.println(aux.toString());
    	}
      
    } catch (Exception e) {
      System.err.println("Error en Main");
      System.exit(1);
    } 
  }
  
}