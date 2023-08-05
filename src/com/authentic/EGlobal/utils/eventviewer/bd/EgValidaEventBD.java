package com.authentic.EGlobal.utils.eventviewer.bd;

import com.alaric.jdbc.DBConnectionMgr;
import com.alaric.jdbc.DBException;
import com.authentic.EGlobal.loader.promoevo.dto.EgDTOBancoPromoEVO;
import com.authentic.EGlobal.loader.promoevo.dto.EgDTODatosPromoEVO;
import com.authentic.EGlobal.loader.promoevo.exception.EgExcepcionPromo;
import com.authentic.EGlobal.utils.eventviewer.dto.EgDTOEventViewer;
import com.sun.tools.javac.util.List;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class EgValidaEventBD {
  private static EgValidaEventBD instance = null;
  
  private Connection conn = null;
  
  public static EgValidaEventBD getInstance() {
    if (instance == null)
      instance = new EgValidaEventBD(); 
    return instance;
  }
  
  private EgValidaEventBD() {
    try {
      this.conn = obtenerConexion();
      this.conn.setAutoCommit(false);
    } catch (SQLException ex) {
      System.err.println("Error: No se realizo la instancia de la BD");
    }
  }
  
  private Connection obtenerConexion() {
    if (this.conn == null)
      try {
        this.conn = DBConnectionMgr.getInstance().getLongRunningConnection();
      } catch (DBException ex) {
        System.err.println("Error: NO se obtuvo la conexion, cerrando");
        cerrarConexion();
        System.exit(1);
      }  
    return this.conn;
  }
  
  public void commit() {
    try {
      this.conn.commit();
    } catch (SQLException e) {
      System.err.println("Error en commit");
      System.exit(1);
    } 
  }
  
  public void rollback() {
    try {
      this.conn.rollback();
    } catch (SQLException e) {
      System.err.println("Error en rollback");
      System.exit(1);
    } 
  }



  private void cerrarConexion() {
    try {
      if (this.conn != null && !this.conn.isClosed())
        this.conn.close(); 
    } catch (SQLException e) {
      System.err.println("Error: La conexion posiblemente quedabierta");
    } 
  }
  
  public ArrayList<EgDTOBancoPromoEVO> obtenerInfoBancos() throws EgExcepcionPromo {
    PreparedStatement psBanco = null;
    ResultSet rs = null;
    ArrayList<EgDTOBancoPromoEVO> info = new ArrayList<EgDTOBancoPromoEVO>();
    EgDTOBancoPromoEVO banco = null;
    try {
      psBanco = this.conn.prepareStatement("SELECT * FROM EGL_CONFIG_PROMOS_EVO");
      rs = psBanco.executeQuery();
      while (rs.next()) {
        banco = new EgDTOBancoPromoEVO();
        banco.setId_banco(rs.getString(1));
        banco.setNombre_banco(rs.getString(2));
        banco.setNombre_archivo_entrada(rs.getString(3));
        banco.setNombre_archivo_salida(rs.getString(4));
        banco.setDias_atraso(rs.getInt(5));
        banco.setUltima_carga(rs.getString(6));
        info.add(banco);
      } 
    } catch (SQLException e) {
      System.err.println("Error al obtener info de bancos");
      throw new EgExcepcionPromo("Error al obtener info de bancos");
    } finally {
      try {
        if (psBanco != null && !psBanco.isClosed())
          psBanco.close(); 
        if (rs != null && !rs.isClosed())
          rs.close(); 
      } catch (SQLException e) {
        System.err.println("No se ha cerrado cursor de info bancos");
      } 
    } 
    return info;
  }
  
  public ArrayList<EgDTOEventViewer> obtenerRegistrosEventViewer() {
    PreparedStatement stmt = null;
    ArrayList<EgDTOEventViewer> resultado = new ArrayList<EgDTOEventViewer>();
    ResultSet rs = null;
    try {
      stmt = conn.prepareStatement("SELECT E.EVE_EVD_ID, ED.EVD_CODE, ED.EVD_DESCRIPTION, E.EVE_IPS_NAME, COUNT(E.EVE_EVD_ID) AS CANTIDAD FROM BBVA_OWNER.EVENT E "
      		+ "INNER JOIN BBVA_OWNER.EVENT_DEFINITION ED ON E.EVE_EVD_ID = ED.EVD_ID "
      		+ "WHERE E.EVE_EVD_ID IN (959,956,955,954,957,958,1183,49,50,58,1195) "
      		+ "AND TRUNC(CAST(E.EVE_TIMESTAMP AS DATE)) = TRUNC(SYSDATE) "
      		+ "AND    TO_CHAR(E.EVE_TIMESTAMP, 'HH24:MI:SS') BETWEEN  "
      		+ "    TO_CHAR(SYSDATE - INTERVAL '3' HOUR, 'HH24:MI:SS') AND "
      		+ "    TO_CHAR(SYSDATE, 'HH24:MI:SS') "
      		+ "GROUP BY E.EVE_EVD_ID, ED.EVD_CODE, ED.EVD_DESCRIPTION, E.EVE_IPS_NAME");
      rs = stmt.executeQuery();
      while(rs.next()) {
    	EgDTOEventViewer aux = new EgDTOEventViewer();
    	aux.setId_evento(rs.getInt("EVE_EVD_ID"));
    	aux.setCodigo_evento(rs.getString("EVD_CODE"));
    	aux.setDescripcion_evento(rs.getString("EVD_DESCRIPTION"));
    	aux.setIps_nombre(rs.getString("EVE_IPS_NAME"));
    	aux.setCantidad_evento(rs.getInt("CANTIDAD"));
    	
    	resultado.add(aux);
      }
      
    } catch (SQLException e) {
      System.err.println("Error al obtener contador");
    } finally {
      try {
        if (stmt != null && !stmt.isClosed())
          stmt.close(); 
        if (rs != null && !rs.isClosed())
          rs.close(); 
      } catch (SQLException e) {
        System.err.println("Valió madres");
      } 
    } 
    return resultado;
  }
  
  public void cerrar() {
    try {
      if (this.conn != null && !this.conn.isClosed())
        this.conn.close(); 
    } catch (SQLException e) {
      System.err.println("Error: La conexion posiblemente quedabierta");
    } 
  }
}
