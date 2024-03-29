package com.revature.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.revature.annotations.Column;
import com.revature.annotations.Id;
import com.revature.connection.ConnectionUtil;

public class Persist {
	
	public String url;
	public String username;
	public String pwd;
	private static final Logger log = Logger.getLogger(Persist.class);
	
	
	public void createTables(@SuppressWarnings("rawtypes") List<Class> objects) {
		
		
		for (int i = 0; i < objects.size(); i++) {
		
			String tableName = "mikeop1." + objects.get(i).getSimpleName();
			Map<String, String> columns = new HashMap<String, String>();
			for (Field f : (objects.get(i)).getDeclaredFields()) {
				
				Id id = f.getAnnotation(Id.class);
				if (id != null) {
					columns.put(id.columnName(), "SERIAL PRIMARY KEY");
				} else if (f.getType().toString().equals("int") ) {
					columns.put(f.getName(), "INTEGER");
				} else if (f.getType().toString().equals("class java.lang.String")) {
					columns.put(f.getName(), "VARCHAR(50)");
				}
			}
//			System.out.println(columns);
			String sqlCol = "";
			for (Entry<String, String> j : columns.entrySet()) {
				
				sqlCol = sqlCol + (j.getKey() + " " + j.getValue() + ", ");
				
			}
			sqlCol = sqlCol.substring(0, sqlCol.length()-2);
//			System.out.println(sqlCol);
			
			try(Connection conn = ConnectionUtil.getConnection(this)) {
				
				String sql = "CREATE TABLE " + tableName + " (" + sqlCol + ")";
				
				// TODO: Create if statement to prompt user to either delete or not if table already exists
				PreparedStatement stmt = conn.prepareStatement(sql);
				
				stmt.execute();
				log.info("Table " + objects.get(i).getSimpleName() + " created.");

				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	public boolean addEntry(List<?> entries) {
		
		
		for (int i = 0; i < entries.size(); i++) {
			String sql = "INSERT INTO mikeop1." + entries.get(i).getClass().getSimpleName() + " (";
			for (Field f : entries.get(i).getClass().getDeclaredFields()) {
				Id id = f.getAnnotation(Id.class);
				if (id != null) {
					sql += id.columnName() + ", ";
				} else {
					sql += f.getName() + ", ";
				}
				
			}
			sql = sql.substring(0, sql.length()-2) + ") VALUES (";
//			System.out.println(entries.get(i).toString());
			String classString = entries.get(i).toString();
			int equalIndex = 0;
			int commaIndex = 0;
			for (int j = 0; j < entries.get(i).getClass().getDeclaredFields().length; j++) {
				
				
				equalIndex = classString.indexOf('=', equalIndex);
				commaIndex = classString.indexOf(',', commaIndex);
				if (commaIndex == -1)
					commaIndex = classString.length()-1;
				sql += (classString.substring(equalIndex + 1, commaIndex)) + ", ";
				equalIndex++;
				commaIndex++;
				
				
			}
			sql = sql.substring(0, sql.length()-2) + ")";
//			System.out.println(sql);
			
			
			try(Connection conn = ConnectionUtil.getConnection(this)) {
				

				
				// TODO: Create if statement to prompt user to either delete or not if table already exists
				PreparedStatement stmt = conn.prepareStatement(sql);
				
				stmt.execute();
				log.info("Entry " + entries.get(i).toString() + " added.");

				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return false;
	}
	
	public <T> List<Object> findAll(Class table) throws NoSuchMethodException, SecurityException {
		
		List<Object> allEntries = new ArrayList<>();
		
		
		try(Connection conn = ConnectionUtil.getConnection(this)) {
			
			String sql = "SELECT * FROM mikeop1." + table.getSimpleName();
			
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(sql); // ResultSet allows us to iterate over returned data
			
			while (rs.next()) {
				
				Constructor<?>[] co = table.getConstructors();
				Field[] fields = table.getDeclaredFields();

				Object ob = null;
				

				Object[] initArgs = new Object[fields.length];
				
				for (int i = 0; i < fields.length; i++) {
					Id id = fields[i].getAnnotation(Id.class);
					if (id != null) {
						initArgs[i] =  rs.getObject(id.columnName());
					} else {
						initArgs[i] = rs.getObject(fields[i].getName());
					}
				}

				try {
//					System.out.println(Arrays.toString(co[1].getParameters()));
					ob = co[1].newInstance(initArgs);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				allEntries.add(ob);
			}
			log.info("All entries found.");

			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return allEntries;
	}
	
	public Object findById(Class table, int idToFind) {
		
		Object ob = new Object();
		Field[] fields = table.getDeclaredFields();
		String idName = "";
		for (Field f : fields) {
			
			Id id = f.getAnnotation(Id.class);
			if (id != null) {
				idName = id.columnName();
			} 
			
		}
		
		try(Connection conn = ConnectionUtil.getConnection(this)) {
			
			
			String sql = "SELECT * FROM mikeop1." + table.getSimpleName() + " WHERE " + idName + " = " + idToFind;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				
				Constructor<?>[] co = table.getConstructors();
				

				Object[] initArgs = new Object[fields.length];
				
				for (int i = 0; i < fields.length; i++) {
					Id id = fields[i].getAnnotation(Id.class);
					if (id != null) {
						initArgs[i] =  rs.getObject(id.columnName());
					} else {
						initArgs[i] = rs.getObject(fields[i].getName());
					}
				}

				try {
					ob = co[1].newInstance(initArgs);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			log.info("Found " + ob.toString());

			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return ob;
		
	}
	
	public boolean update(Object ob) {
		
		int idNum = 0;
		String objectString = ob.toString().substring(ob.toString().indexOf('[')+1, ob.toString().indexOf(']'));
		idNum = Integer.parseInt(objectString.substring(objectString.indexOf('=')+1, objectString.indexOf(',')));
		objectString = objectString.substring(objectString.indexOf(' ')+1);
		System.out.println(objectString);
		
		String idName = "";
	
		for (Field f : ob.getClass().getDeclaredFields()) {
			Id id = f.getAnnotation(Id.class);
			if (id != null) {
				idName = id.columnName();
			} 
		}
		String sql = "UPDATE mikeop1." + ob.getClass().getSimpleName() + " SET " + objectString + "WHERE " + idName + " = " + idNum;
		
		System.out.println(sql);
		
		
		try(Connection conn = ConnectionUtil.getConnection(this)) {
			

			
			// TODO: Create if statement to prompt user to either delete or not if table already exists
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
			log.info("Entry " + ob.toString() + " updated.");
			return true;
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
		
	
		
		
		return false;
		
	}
	
	public boolean delete(Object ob) {
		
		String sql = "DELETE FROM mikeop1." + ob.getClass().getSimpleName() + " WHERE ";
		String idName = "";
		int idNum = Integer.parseInt(ob.toString().substring(ob.toString().indexOf('=')+1, ob.toString().indexOf(',')));
		for (Field f : ob.getClass().getDeclaredFields()) {
			Id id = f.getAnnotation(Id.class);
			if (id != null) {
				idName = id.columnName();
			} 
		}
		sql += idName + " = " + idNum;
		try(Connection conn = ConnectionUtil.getConnection(this)) {
			

			
			// TODO: Create if statement to prompt user to either delete or not if table already exists
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
			log.info("Entry " + ob.toString() + " deleted.");
			return true;

			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

}
