package com.tapisdev.mysteam.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Get_koordinat_awal_akhir extends Activity{

	// DB
	Cursor cursor;	
	
	int fix_simpul_awal = 0;
	String explode_lat_only = "";	
	Location posisiUser = new Location("");
	ArrayList<String> a_tmp_graph = new ArrayList<String>();
	String[] exp_nodes = new String[100];
	
	// return JSON
	JSONObject jadi_json = new JSONObject();
	
	// AMBIL NODE DARI FIELD SIMPUL_AWAL DAN TUJUAN DI TABEL GRAPH, TRS DIGABUNG; contoh 1,0 DAN MASUKKAN KE ARRAY
	List<String> barisDobel = new ArrayList<String>();
	List<String> indexBarisYgDikerjakan = new ArrayList<String>();
	

	public JSONObject Get_simpul(double latx, double lngx, Context context) throws JSONException {
		// TODO Auto-generated constructor stub		
		
		// your coordinate
		posisiUser.setLatitude(latx);
		posisiUser.setLongitude(lngx);		

		// TAMPUNG NODE DARI FIELD SIMPUL_AWAL DAN TUJUAN DI TABEL GRAPH, TRS DIGABUNG; contoh 1,0 DAN MASUKKAN KE ARRAY
		List<String> barisDobel = new ArrayList<String>();
		List<String> indexBarisYgDikerjakan = new ArrayList<String>();


		exp_nodes[0] = "0";
		exp_nodes[1] = "1";
		// list simpul yg dikerjakan
		StringBuilder indexBarisYgDikerjakan1 = new StringBuilder();		
		for(int j = 0; j < indexBarisYgDikerjakan.size(); j++){
			
			if(indexBarisYgDikerjakan1.length() == 0){
				
				// field id lagi pada tabel graph (khusus utk stringbuilder)
				indexBarisYgDikerjakan1.append(indexBarisYgDikerjakan.get(j));
			}else{
				indexBarisYgDikerjakan1.append(","+indexBarisYgDikerjakan.get(j)); // untuk where in ('0,1')
			}
		}

		JSONObject obj = new JSONObject();

		
		double x = 0;
		double y = 0;
		int rowId_json = 0;
		int indexCoordinate_json = 0;
		int countCoordinate_json = 0;
		String nodes_json = "";

		// cari bobot terkecil dari JSON
		for(int s = 0; s < obj.length(); s++){
			
			if(s == 0){
				// first
				JSONArray a = obj.getJSONArray("0");			
				JSONObject b = a.getJSONObject(0);
				x = Double.parseDouble(b.getString("bobot"));
				
				// ==========
				// row id field
				rowId_json = Integer.parseInt(b.getString("row_id"));
				// index coordinate sekitar simpul
				indexCoordinate_json = Integer.parseInt(b.getString("index"));
				// jumlah coordinate
				countCoordinate_json = Integer.parseInt(b.getString("count_koordinat"));
				// nodes
				nodes_json = b.getString("nodes").toString();
				// ==========
				
			}else{
				// second, dst
				JSONArray c = obj.getJSONArray("" + s);			
				JSONObject d = c.getJSONObject(0);
				y = Double.parseDouble(d.getString("bobot"));
				
				// dapatkan value terkecil (bobot)
				if(y <= x){
					// bobot
					x = y;

					// ==========
					// row id field
					rowId_json = Integer.parseInt(d.getString("row_id"));
					// index coordinate sekitar simpul
					indexCoordinate_json = Integer.parseInt(d.getString("index"));
					// jumlah coordinate
					countCoordinate_json = Integer.parseInt(d.getString("count_koordinat"));
					// nodes
					nodes_json = d.getString("nodes").toString();
					// ==========
					
				}		
			}
			
		}		
		
		// nodes : 0-1
		//String[] exp_nodes = nodes_json.split("-");

		
		int field_simpul_awal = Integer.parseInt(exp_nodes[0]);
		int field_simpul_tujuan = Integer.parseInt(exp_nodes[1]);

		// Koordinat yg didapat di awal atau diakhir, maka gak perlu nambah simpul
		if(indexCoordinate_json == 0 || indexCoordinate_json == countCoordinate_json){
			
			//tentukan simpul awal atau akhir yg dekat dgn posisi user
			if(indexCoordinate_json == 0){
				
				// nodes di field simpul_awal
				fix_simpul_awal = field_simpul_awal; 
			}else if(indexCoordinate_json == countCoordinate_json){
				
				// nodes di field simpul_akhir
				fix_simpul_awal = field_simpul_tujuan;
			}
			
			jadi_json.put("status", "jalur_none");
			
		}
		//Koordinat yang didapat berada ditengah2 simpul 0 - 1 (misal)
		else{
			
			int dobel = cursor.getCount();
			
			//ada simpul yg dobel (1,0) dan (0,1)
			if(dobel == 1){

				jadi_json.put("status", "jalur_double");

			}
			//gak dobel, hanya (1,0)
			else if(dobel == 0){
				
				jadi_json.put("status", "jalur_single");

			}
		}
		
		
		// JSON
		jadi_json.put("node_simpul_awal0", field_simpul_awal);
		jadi_json.put("node_simpul_awal1", field_simpul_tujuan);				
		jadi_json.put("index_coordinate_json", indexCoordinate_json);
		//jadi_json.put("destination_simpul", judulTabel_simpulTujuan);
		jadi_json.put("explode_lat_only", explode_lat_only);
				
		return jadi_json;
		
	}//public

}