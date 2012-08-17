package org.vamdc.tapservice.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.trans.SelectTranslator;
import org.apache.cayenne.query.SelectQuery;

/**
 * Helper class to get the count of rows returned by 
 * an arbitrary query.
 * Inspired by http://osdir.com/ml/user-cayenne-apache/2009-09/msg00012.html
 * 
 */
public class CountQuery {

	public static long count(DataContext context, SelectQuery query) {
		return count(context, query, context.getParentDataDomain().
				getDataNodes().iterator().next());
	}

	private static long count(DataContext context, SelectQuery query,
			DataNode node) {
		CountTranslator translator = new CountTranslator();

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
}