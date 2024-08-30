package com.systex.jbranch.platform.configuration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

public class DBConfigPriorityComparator implements Comparator
{
	private Map<Object,Integer> priorityMap=new HashMap<Object,Integer>(); 
	
	public DBConfigPriorityComparator(DataSource dataSource,String tableName,String typeColumn,String priorityColumn) throws ConfigurationException
	{
		Connection conn=null;
		PreparedStatement pstmt=null;
		try
		{
			conn=dataSource.getConnection();
			pstmt=conn.prepareStatement("select * from "+tableName);
			
			ResultSet rs=pstmt.executeQuery();
			while(rs.next())
			{
				priorityMap.put(rs.getString(typeColumn),rs.getInt(priorityColumn));
			}
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
	
	public int compare(Object o1, Object o2) 
	{
		int p1=priorityMap.get(o1);
		int p2=priorityMap.get(o2);
		
		return p1-p2;
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
