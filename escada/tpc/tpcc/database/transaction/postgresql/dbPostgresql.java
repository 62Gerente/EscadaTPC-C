package escada.tpc.tpcc.database.transaction.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;

import org.apache.log4j.Logger;

import escada.tpc.common.OutInfo;
import escada.tpc.tpcc.database.transaction.*;

/**
 * It is an interface to a postgreSQL, which based is based on the the
 * distributions of the TPC-C.
 */
public class dbPostgresql extends dbTPCCDatabase {

	private static Logger logger = Logger.getLogger(dbPostgresql.class);

	protected HashSet NewOrderDB(OutInfo obj, Connection con)
			throws java.sql.SQLException {

		boolean resubmit = Boolean.parseBoolean((String) obj
				.getInfo("resubmit"));
		HashSet dbtrace = new HashSet();

		while (true) {
			PreparedStatement statement = null;
			ResultSet rs = null;
			String cursor = null;

			try {

				Date NetStartTime = new java.util.Date();

				statement = con
						.prepareCall("select tpcc_neworder (?,?,?,?,?,?,?,?)");

				statement.setInt(1, Integer.parseInt((String) obj
						.getInfo("wid")));
				statement.setInt(2, Integer.parseInt((String) obj
						.getInfo("did")));
				statement.setInt(3, Integer.parseInt((String) obj
						.getInfo("cid")));
				statement.setInt(4, Integer.parseInt((String) obj
						.getInfo("qtd")));
				statement.setInt(5, Integer.parseInt((String) obj
						.getInfo("localwid")));

				int icont = 0;
				int qtd = Integer.parseInt((String) obj.getInfo("qtd"));
				StringBuffer iid = new StringBuffer();
				StringBuffer wid = new StringBuffer();
				StringBuffer qtdi = new StringBuffer();
				while (icont < qtd) {
					iid.append((String) obj.getInfo("iid" + icont));
					iid.append(",");
					wid.append((String) obj.getInfo("supwid" + icont));
					wid.append(",");
					qtdi.append((String) obj.getInfo("qtdi" + icont));
					qtdi.append(",");
					icont++;
				}
				statement.setString(6, iid.toString());
				statement.setString(7, wid.toString());
				statement.setString(8, qtdi.toString());

				rs = statement.executeQuery();

				if (rs.next()) {
					cursor = (String) rs.getString(1);
				}
				rs.close();
				rs = null;
				statement.close();
				statement = null;
				statement = con.prepareStatement("fetch all in \"" + cursor
						+ "\"");
				rs = statement.executeQuery();

				while (rs.next()) {
					dbtrace.add(rs.getString(1));
				}
				rs.close();
				rs = null;
				statement.close();
				statement = null;

				Date NetFinishTime = new java.util.Date();

				processLog(NetStartTime, NetFinishTime, "processing", "w",
						"tx neworder");

			} catch (java.sql.SQLException sqlex) {
				logger.warn("NewOrder - SQL Exception " + sqlex.getMessage());
				if ((sqlex.getMessage().indexOf("serialize") != -1)
						|| (sqlex.getMessage().indexOf("deadlock") != -1)) {
					RollbackTransaction(con, sqlex);
					if (resubmit) {
						InitTransaction(obj, con, "tx neworder");
						continue;
					} else {
						throw sqlex;
					}
				} else {
					RollbackTransaction(con, sqlex);
					throw sqlex;
				}
			} catch (java.lang.Exception ex) {
				logger.fatal("Unexpected error. Something bad happend");
				ex.printStackTrace(System.err);
				System.exit(-1);
			} finally {
				if (rs != null) {
					rs.close();
				}
				if (statement != null) {
					statement.close();
				}
			}
			break;
		}
		return (dbtrace);
	}

	protected HashSet DeliveryDB(OutInfo obj, Connection con)
			throws java.sql.SQLException {

		boolean resubmit = Boolean.parseBoolean((String) obj
				.getInfo("resubmit"));
		HashSet dbtrace = new HashSet();

		while (true) {

			PreparedStatement statement = null;
			ResultSet rs = null;
			String cursor = null;

			try {
				Date NetStartTime = new java.util.Date();

				statement = con.prepareStatement("select tpcc_delivery(?,?)");

				statement.setInt(1, Integer.parseInt((String) obj
						.getInfo("wid")));
				statement.setInt(2, Integer.parseInt((String) obj
						.getInfo("crid")));
				rs = statement.executeQuery();

				if (rs.next()) {
					cursor = (String) rs.getString(1);
				}
				rs.close();
				rs = null;
				statement.close();
				statement = null;
				statement = con.prepareStatement("fetch all in \"" + cursor
						+ "\"");
				rs = statement.executeQuery();

				while (rs.next()) {
					dbtrace.add(rs.getString(1));
				}
				rs.close();
				rs = null;
				statement.close();
				statement = null;

				Date NetFinishTime = new java.util.Date();

				processLog(NetStartTime, NetFinishTime, "processing", "w",
						"tx delivery");

			} catch (java.sql.SQLException sqlex) {
				logger.warn("Delivery - SQL Exception " + sqlex.getMessage());
				if ((sqlex.getMessage().indexOf("serialize") != -1)
						|| (sqlex.getMessage().indexOf("deadlock") != -1)) {
					RollbackTransaction(con, sqlex);
					if (resubmit) {
						InitTransaction(obj, con, "tx delivery");
						continue;
					} else {
						throw sqlex;
					}
				} else {
					RollbackTransaction(con, sqlex);
					throw sqlex;
				}
			} catch (java.lang.Exception ex) {
				logger.fatal("Unexpected error. Something bad happend");
				ex.printStackTrace(System.err);
				System.exit(-1);
			} finally {
				if (rs != null) {
					rs.close();
				}
				if (statement != null) {
					statement.close();
				}
			}
			break;
		}
		return (dbtrace);
	}

	protected HashSet OrderStatusDB(OutInfo obj, Connection con)
			throws java.sql.SQLException {

		boolean resubmit = Boolean.parseBoolean((String) obj
				.getInfo("resubmit"));
		HashSet dbtrace = new HashSet();

		while (true) {
			PreparedStatement statement = null;
			ResultSet rs = null;
			String cursor = null;

			try {
				Date NetStartTime = new java.util.Date();

				statement = con
						.prepareStatement("select tpcc_orderstatus(?,?,?,?)");

				statement.setInt(1, Integer.parseInt((String) obj
						.getInfo("wid")));
				statement.setInt(2, Integer.parseInt((String) obj
						.getInfo("did")));
				statement.setInt(3, Integer.parseInt((String) obj
						.getInfo("cid")));
				statement.setString(4, (String) obj.getInfo("lastname"));
				rs = statement.executeQuery();

				if (rs.next()) {
					cursor = (String) rs.getString(1);
				}
				rs.close();
				rs = null;
				statement.close();
				statement = null;
				statement = con.prepareStatement("fetch all in \"" + cursor
						+ "\"");
				rs = statement.executeQuery();

				while (rs.next()) {
					dbtrace.add(rs.getString(1));
				}
				rs.close();
				rs = null;
				statement.close();
				statement = null;

				Date NetFinishTime = new java.util.Date();

				String str = (String) (obj).getInfo("cid");
				if (str.equals("0")) {
					processLog(NetStartTime, NetFinishTime, "processing", "w",
							"tx orderstatus 01");
				} else {
					processLog(NetStartTime, NetFinishTime, "processing", "w",
							"tx orderstatus 02");
				}

			} catch (java.sql.SQLException sqlex) {
				logger
						.warn("OrderStatus - SQL Exception "
								+ sqlex.getMessage());
				if ((sqlex.getMessage().indexOf("serialize") != -1)
						|| (sqlex.getMessage().indexOf("deadlock") != -1)) {
					RollbackTransaction(con, sqlex);

					if (resubmit) {
						String str = (String) (obj).getInfo("cid");
						if (str.equals("0")) {
							InitTransaction(obj, con, "tx orderstatus 01");
						} else {
							InitTransaction(obj, con, "tx orderstatus 02");
						}
						continue;
					} else {
						throw sqlex;
					}
				} else {
					RollbackTransaction(con, sqlex);
					throw sqlex;
				}
			} catch (java.lang.Exception ex) {
				logger.fatal("Unexpected error. Something bad happend");
				ex.printStackTrace(System.err);
				System.exit(-1);
			} finally {
				if (rs != null) {
					rs.close();
				}
				if (statement != null) {
					statement.close();
				}
			}
			break;
		}
		return (dbtrace);
	}

	protected HashSet PaymentDB(OutInfo obj, Connection con)
			throws java.sql.SQLException {

		boolean resubmit = Boolean.parseBoolean((String) obj
				.getInfo("resubmit"));
		HashSet dbtrace = new HashSet();

		while (true) {
			PreparedStatement statement = null;
			ResultSet rs = null;
			String cursor = null;

			try {
				Date NetStartTime = new java.util.Date();

				statement = con
						.prepareStatement("select tpcc_payment(?,?,cast(? as numeric(6,2)),?,?,?,cast(? as char(16)))");

				statement.setInt(1, Integer.parseInt((String) obj
						.getInfo("wid")));
				statement.setInt(2, Integer.parseInt((String) obj
						.getInfo("cwid")));
				statement.setFloat(3, Float.parseFloat((String) obj
						.getInfo("hamount")));
				statement.setInt(4, Integer.parseInt((String) obj
						.getInfo("did")));
				statement.setInt(5, Integer.parseInt((String) obj
						.getInfo("cdid")));
				statement.setInt(6, Integer.parseInt((String) obj
						.getInfo("cid")));
				statement.setString(7, (String) obj.getInfo("lastname"));

				rs = statement.executeQuery();

				if (rs.next()) {
					cursor = (String) rs.getString(1);
				}
				rs.close();
				rs = null;
				statement.close();
				statement = null;
				statement = con.prepareStatement("fetch all in \"" + cursor
						+ "\"");
				rs = statement.executeQuery();

				while (rs.next()) {
					dbtrace.add(rs.getString(1));
				}
				rs.close();
				rs = null;
				statement.close();
				statement = null;

				Date NetFinishTime = new java.util.Date();

				String str = (String) (obj).getInfo("cid");
				if (str.equals("0")) {
					processLog(NetStartTime, NetFinishTime, "processing", "w",
							"tx payment 01");
				} else {
					processLog(NetStartTime, NetFinishTime, "processing", "w",
							"tx payment 02");
				}
			} catch (java.sql.SQLException sqlex) {
				logger.warn("Payment - SQL Exception " + sqlex.getMessage());
				if ((sqlex.getMessage().indexOf("serialize") != -1)
						|| (sqlex.getMessage().indexOf("deadlock") != -1)) {
					RollbackTransaction(con, sqlex);
					if (resubmit) {
						String str = (String) (obj).getInfo("cid");
						if (str.equals("0")) {
							InitTransaction(obj, con, "tx payment 01");
						} else {
							InitTransaction(obj, con, "tx payment 02");
						}
						continue;
					} else {
						throw sqlex;
					}
				} else {
					RollbackTransaction(con, sqlex);
					throw sqlex;
				}
			} catch (java.lang.Exception ex) {
				logger.fatal("Unexpected error. Something bad happend");
				ex.printStackTrace(System.err);
				System.exit(-1);
			} finally {
				if (rs != null) {
					rs.close();
				}
				if (statement != null) {
					statement.close();
				}
			}
			break;
		}
		return (dbtrace);
	}

	protected HashSet StockLevelDB(OutInfo obj, Connection con)
			throws java.sql.SQLException {

		boolean resubmit = Boolean.parseBoolean((String) obj
				.getInfo("resubmit"));
		HashSet dbtrace = new HashSet();

		while (true) {
			PreparedStatement statement = null;
			ResultSet rs = null;
			String cursor = null;

			try {
				Date NetStartTime = new java.util.Date();

				statement = con
						.prepareStatement("select tpcc_stocklevel(?,?,?)");

				statement.setInt(1, Integer.parseInt((String) obj
						.getInfo("wid")));
				statement.setInt(2, Integer.parseInt((String) obj
						.getInfo("did")));
				statement.setInt(3, Integer.parseInt((String) obj
						.getInfo("threshhold")));
				rs = statement.executeQuery();

				if (rs.next()) {
					cursor = (String) rs.getString(1);
				}
				rs.close();
				rs = null;
				statement.close();
				statement = null;
				statement = con.prepareStatement("fetch all in \"" + cursor
						+ "\"");
				rs = statement.executeQuery();

				while (rs.next()) {
					dbtrace.add(rs.getString(1));
				}
				rs.close();
				rs = null;
				statement.close();
				statement = null;

				Date NetFinishTime = new java.util.Date();

				processLog(NetStartTime, NetFinishTime, "processing", "w",
						"tx stocklevel");

			} catch (java.sql.SQLException sqlex) {
				logger.warn("StockLevel - SQL Exception " + sqlex.getMessage());
				if ((sqlex.getMessage().indexOf("serialize") != -1)
						|| (sqlex.getMessage().indexOf("deadlock") != -1)) {
					RollbackTransaction(con, sqlex);
					if (resubmit) {
						InitTransaction(obj, con, "tx stocklevel");
						continue;
					} else {
						throw sqlex;
					}
				} else {
					RollbackTransaction(con, sqlex);
					throw sqlex;
				}
			} catch (java.lang.Exception ex) {
				logger.fatal("Unexpected error. Something bad happend");
				ex.printStackTrace(System.err);
				System.exit(-1);
			} finally {
				if (rs != null) {
					rs.close();
				}
				if (statement != null) {
					statement.close();
				}
			}
			break;
		}
		return (dbtrace);
	}

	protected void InitTransaction(OutInfo obj, Connection con,
			String transaction) throws java.sql.SQLException {
		Statement statement = null;
		try {
			statement = con.createStatement();
			statement.execute("begin transaction");
			statement.execute("SET TRANSACTION ISOLATION LEVEL SERIALIZABLE");
			statement.execute("select '" + transaction + "'");
		} catch (java.lang.Exception ex) {
			logger.fatal("Unexpected error. Something bad happend");
			ex.printStackTrace(System.err);
			System.exit(-1);
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	protected void CommitTransaction(Connection con)
			throws java.sql.SQLException {
		{
			Statement statement = null;
			try {
				statement = con.createStatement();
				statement.execute("commit transaction");
			} catch (java.sql.SQLException sqlex) {
				if (con != null) {
					RollbackTransaction(con, sqlex);
				}
				throw sqlex;
			} catch (java.lang.Exception ex) {
				logger.fatal("Unexpected error. Something bad happend");
				ex.printStackTrace(System.err);
				System.exit(-1);
			} finally {
				if (statement != null) {
					statement.close();
				}
			}
		}
	}

	protected void RollbackTransaction(Connection con, Exception dump)
			throws java.sql.SQLException {
		Statement statement = null;
		try {
			statement = con.createStatement();
			statement.execute("rollback transaction");
		} catch (java.lang.Exception ex) {
			logger.fatal("Unexpected error. Something bad happend");
			ex.printStackTrace(System.err);
			System.exit(-1);
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}
}
// arch-tag: 5e93fc99-eedb-49eb-af2a-bbdb57146184
