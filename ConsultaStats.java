package com.calacom.stats.data;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.bind.DatatypeConverter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.calacom.stats.dto.GolsDTO;
import com.calacom.stats.dto.LineUpsDTO;
import com.calacom.stats.dto.MatchDTO;
import com.calacom.stats.dto.SustitutionsDTO;
import com.calacom.stats.dto.TeamDTO;
import com.calacom.stats.logic.GetXML;
import com.calacom.stats.util.FechasSoporte;
import com.sun.xml.internal.ws.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ConsultaStats {
	
	public String SENDER_ID = "290811386777";
	public static String API_KEY = "ry8F0aMgecvNJtvdRJJ7dmQikcqpPvxs";
	public static String API_USR = "adminstats";
	public static String API_PAS = "adminadmin";
	public static String URL_ACS = "https://api.cloud.appcelerator.com/v1/";
		
	
	public static void main(String[] args){
		
		try {
			ConsultaStats cons=new ConsultaStats();
			
			cons.tipoMensaje(0, "America");
				
			
			
		} catch (Exception e) {
			System.out.println("IOException: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void tipoMensaje(int num,String equipo) throws Exception{
		ConsultaStats cons=new ConsultaStats();
		String session=cons.obtieneIdSession();
		
		String nombre=null;
		String titulo=null;
		if(num==0){
			nombre=equipo+" starts in 30 min";
			titulo="Game";
		}else if(num==1){
			nombre=equipo+" Game Starts";
			titulo="Game";
		}else if(num==2){
			nombre="Goal of "+equipo;
			titulo="Game";
		}else if(num==3){
			nombre="End of part";
			titulo="Half Time";
		}else if(num==4){
			nombre="Yellow Card from "+equipo;
			titulo="Yellowcard";
		}else if(num==4){
			nombre="Red Card from "+equipo;
			titulo="Redcard";
		}else if(num==4){
			nombre=equipo+" wins";
			titulo="End of Match";
		}
		cons.enviaMensaje(nombre, titulo, session);
	}
	
	public String obtieneIdSession() throws Exception{
		URL url = null;
		URLConnection uc = null;
		String idSession=null;
		try {
             url = new URL(URL_ACS+"users/login.json?key="+API_KEY+"&login="+API_USR+"&password="+API_PAS+"");
			 uc = url.openConnection();
             HttpURLConnection conn = (HttpURLConnection) url.openConnection();
             conn.setRequestMethod("POST");
             if (conn.getResponseCode() != 200) {
               throw new Exception(conn.getResponseMessage());
             }
             InputStream is = conn.getInputStream();
             BufferedReader rd = new BufferedReader(
                 new InputStreamReader(is));
             StringBuilder sb = new StringBuilder();
             String line;
             while ((line = rd.readLine()) != null) {
               sb.append(line);
             }
             rd.close();
             conn.disconnect();

            String respuesta=sb.toString();
            
            if (conn.getResponseCode() != 200) {
                throw new Exception(conn.getResponseMessage());
            }else {
             	int number=sb.indexOf("session_id");
             	String meta=sb.substring(number+14, number+60);
             	int fin=meta.indexOf("\"");
             	idSession=meta.substring(0, fin);
            }
		} catch (Exception e) {
			throw new Exception("Problema de session "+e);
		}
		return idSession;
	}
	
	public String enviaMensaje(String nombreAlert, String titulo,String session_id) throws Exception{
		URL url = null;
		HttpURLConnection uc = null;
		String idSession=null;
		try {
			String ruta=URL_ACS+"push_notification/notify.json?key="+API_KEY+"";

			System.out.println(ruta);
             
			 url=new URL(ruta);
             uc = (HttpURLConnection) url.openConnection();

             uc.setDoInput(true);
             uc.setDoOutput(true);
             uc.setRequestProperty("Content-Type", "application/json");
             uc.setRequestProperty("Accept", "application/json");
             uc.setRequestProperty("Cookie","_session_id="+session_id);

             JSONObject cred = new JSONObject();
             JSONObject push = new JSONObject();
             
             cred.put("alert",nombreAlert);
             cred.put("title",titulo);
             cred.put("icon","icon_notifi");
             cred.put("badge","+2");
             cred.put("vibrate",true);
             cred.put("sound","default");
             
             push.put("payload",cred);
             push.put("channel","All users");
             
             System.out.println(push.toString());
             
             String respuestaJSON="{payload:{\"icon\":\"icon_notifi\",\"title\":\"Game\",\"sound\":\"default\",\"alert\":\"Club America starts in 30 min\",\"vibrate\":true,\"badge\":\"+2\"},channel:'All users'}";//push.toString();
             
             System.out.println(respuestaJSON);
             
             OutputStreamWriter wr= new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
             wr.write(respuestaJSON);
             
             if (uc.getResponseCode() != 200) {
               throw new Exception(uc.getResponseMessage());
             }
             InputStream is = uc.getInputStream();
             BufferedReader rd = new BufferedReader(
                 new InputStreamReader(is));
             StringBuilder sb = new StringBuilder();
             String line;
             while ((line = rd.readLine()) != null) {
               sb.append(line);
             }
             rd.close();
             uc.disconnect();
			 
             System.out.println("The content was :: " + sb.toString());
		
		} catch (Exception e) {
			throw new Exception("Problema de mensaje "+e);
		}
		return idSession;
	}
	
}
