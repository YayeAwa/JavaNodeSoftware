package org.vamdc.tapservice.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.trans.SelectTranslator;
import org.apache.cayenne.query.SelectQuery;

/**

 * 
 */
public class QueryUtil {

	/**
	 * Get the count of rows returned by an arbitrary query.
	 * Inspired by http://osdir.com/ml/user-cayenne-apache/2009-09/msg00012.html
	 * @param context Cayenne DataContext
	 * @param query Cayenne SelectQuery to transform into count()
	 * @return number of rows normally returned by query
	 */
	public static long countQuery(DataContext context, SelectQuery query) {
		return count(
				context, 
				query, 
				context.getParentDataDomain().getDataNodes().iterator().next(),
				new CountTranslator());
	}
	
	/**
	 * Get the last timestamp value from the specified field of a table, for a specific query
	 * @param context Cayenne DataContext
	 * @param query Cayenne SelectQuery on a table
	 * @param dateField name of a database field containing last modification/creation date
	 * @return java.util.Date representing the newest(maximum) modification/creation date from a field
	 */
	public static Date lastTimestampQuery(DataContext context, SelectQuery query,String dateField){
		Long timestamp = count(
				context,
				query,
				context.getParentDataDomain().getDataNodes().iterator().next(),
				new LastDateTranslator(dateField));
		return new Date(timestamp*1000L);
	}

	private static long count(DataContext context, SelectQuery query,DataNode node,SelectTranslator translator) {
		translator.setQuery(query);
		translator.setAdapter(node.getAdapter());
		translator.setEntityResolver(context.getEntityResolver());

		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = node.getDataSource().getConnection();
			translator.setConnection(con);

			stmt = translator.createStatement();

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Query exception",e);	
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Size estimation exception",e);
		}
		finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			}
			catch (Exception ex) {
			}
		}
		return 0;
	}

	static class CountTranslator extends SelectTranslator {
		@Override
		public String createSqlString() throws Exception {
			String sql = super.createSqlString();
			int index = sql.indexOf(" FROM ");

			return "SELECT COUNT(*)" + sql.substring(index);
		}
	}
	
	static class LastDateTranslator extends SelectTranslator {
		private String field;
		public LastDateTranslator(String fieldName){
			this.field = fieldName;
		}
		
		@Override
		public String createSqlString() throws Exception {
			String sql = super.createSqlString();
			int index = sql.indexOf(" FROM ");

			return "SELECT UNIX_TIMESTAMP(MAX("+field+"))" + sql.substring(index);
		}
		
	}
}