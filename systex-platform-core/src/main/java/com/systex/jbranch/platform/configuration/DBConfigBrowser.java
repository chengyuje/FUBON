package com.systex.jbranch.platform.configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.sql.DataSource;

public class DBConfigBrowser implements IConfigBrowser
{
	DataSource dataSource;
	String tableName;
	String defaultType;
	String defaultName;
	String typeColumn;
	String nameColumn;
	String keyColumn;
	String valueColumn;
	Comparator priorityComparator;

	public DBConfigBrowser(){}
	
	public DBConfigBrowser(
			DataSource dataSource, 
			String tableName,
			String typeColumn,
			String nameColumn, 
			String keyColumn, 
			String valueColumn,
			String defaultType, 
			String defaultName, 
			Comparator priorityComparator) {

		this.dataSource = dataSource;
		this.tableName = tableName;
		this.defaultType = defaultType;
		this.defaultName = defaultName;
		this.typeColumn = typeColumn;
		this.nameColumn = nameColumn;
		this.keyColumn = keyColumn;
		this.valueColumn = valueColumn;
		this.priorityComparator = priorityComparator;
	}
	
	
	private void fillConfigMap(PreparedStatement pstmt,String type,String name,final Map configMap) throws SQLException
	{
		pstmt.setString(1, type);
		pstmt.setString(2, name);
		ResultSet rs = pstmt.executeQuery();
        
        while (rs.next())
        {
        	String key=rs.getString(keyColumn);
        	String value=rs.getString(valueColumn);
        	configMap.put(key, value);
        }
	}
	
	public Map getConfigMap(Map<String,String> filter) throws ConfigurationException
	{
		Connection conn=null;
		PreparedStatement pstmt=null;
		try
		{
			conn=dataSource.getConnection();
			StringBuilder query=new StringBuilder();
			query.append("SELECT * FROM ").append(tableName).append(" WHERE ");
			query.append(typeColumn).append("=?").append(" AND ");
			query.append(nameColumn).append("=?");
			
			pstmt=conn.prepareStatement(query.toString());

			Map<String,String> filterMap=new TreeMap<String,String>(priorityComparator);
			filterMap.put(defaultType, defaultName);
			
			if(filter!=null)
			{
				filterMap.putAll(filter);
			}
			
			Map configMap=new HashMap();
			
			for(Map.Entry<String, String> entry:filterMap.entrySet())
			{
				fillConfigMap(pstmt,entry.getKey(),entry.getValue(),configMap);
			}
			return configMap;	
		}
		catch (SQLException e) 
		{
			throw new ConfigurationException(e);
		}
		finally
		{
			close(conn,pstmt);
		}
	}


	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}


	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}


	public void setTypeColumn(String typeColumn) {
		this.typeColumn = typeColumn;
	}


	public void setNameColumn(String nameColumn) {
		this.nameColumn = nameColumn;
	}


	public void setKeyColumn(String keyColumn) {
		this.keyColumn = keyColumn;
	}


	public void setValueColumn(String valueColumn) {
		this.valueColumn = valueColumn;
	}


	public void setPriorityComparator(Comparator priorityComparator) {
		this.priorityComparator = priorityComparator;
	}
	
    private static void close(Connection conn, Statement stmt)
    {
        try
        {
            if (stmt != null)
            {
                stmt.close();
            }
        }
        catch (SQLException e)
        {}

        try
        {
            if (conn != null)
            {
                conn.close();
            }
        }
        catch (SQLException e)
        {}
    }
}