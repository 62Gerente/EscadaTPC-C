package escada.tpc.tpcc.database.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

import escada.tpc.common.OutInfo;
import escada.tpc.tpcc.database.dbTPCCDatabase;
import escada.tpc.tpcc.trace.dbLog;

/** It is an interface to a postgreSQL, which based is based on the
 * the distributions of the TPC-C.
 **/
public class dbMySql	
extends dbTPCCDatabase {

    public static int NewOrderTransNum = 0;
    public static int PaymentTransNum = 0;
    public static int OrderStatusTransNum = 0;
    public static int StockLevelTransNum = 0;
    public static int DeliveryTransNum = 0;

    public static String pErro = "";
    
    public Object TraceNewOrderDB(OutInfo obj,String hid) throws java.sql.
    SQLException {
        
        Connection con = null;
        HashSet dbtrace = null;
        
        try {
            con = getConnection();

            System.out.println("Beginning transaction new order (thread(" + Thread.currentThread().getName() + "))");

            dbLog.log("Beginning transaction new order (thread(" + Thread.currentThread().getName() + "))");

	    InitTransaction(obj, con, "tx neworder");

            java.util.Date lDateBegin1 = new java.util.Date();

            dbtrace = NewOrderDB(obj, con);

            java.util.Date lDateEnd1 = new java.util.Date();

            CommitTransaction(con);

            dbLog.log("\tFinishing transaction new order (thread(" + Thread.currentThread().getName() + ")) T= "
                +(lDateEnd1.getTime()-lDateBegin1.getTime())
                +"\tB1 = "+lDateBegin1.getTime()
                +"\tE1 = "+lDateEnd1.getTime());
            
            System.out.println("Finishing transaction new order (thread(" + Thread.currentThread().getName() + "))");
        }
        catch (java.lang.Exception ex) {
            System.err.println(Thread.currentThread().getName() + " NewOrder - General Main Exception " + ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            returnConnection(con);
        }
        return (dbtrace);
    }
    
    public HashSet NewOrderDB(OutInfo obj, Connection con) throws java.sql.
    SQLException {
        while (true) {
            PreparedStatement statement = null, statement02 = null;
            ResultSet rs= null, rs02 = null;
            String cursor = null;
           
            java.util.Date startTime = null;
            java.util.Date endTime = null;
            startTime = new java.util.Date();

            HashSet dbtrace = new HashSet();
            
            try {
                int _w_id = Integer.parseInt( (String) obj.getInfo("wid"));
                int _d_id = Integer.parseInt( (String) obj.getInfo("did"));
                int _c_id = Integer.parseInt( (String) obj.getInfo("cid"));
                int _o_ol_cnt = Integer.parseInt( (String) obj.getInfo("qtd"));
                int _o_all_local = Integer.parseInt( (String) obj.getInfo("localwid"));
                
                double _w_tax;
                double _d_tax;
                String _c_last;
                String _c_credit;;
                double _c_discount;
                double _i_price;
                String _i_name;
                String _i_data;
                java.sql.Date _o_entry_d = new java.sql.Date(2000,01,25);
                int _remote_flag;
                int _s_quantity;
                String _s_data;
                String _s_dist;
                int _li_no;
                int _o_id;
                int _commit_flag;
                int _li_id;
                int _li_s_w_id;
                int _li_qty;
                int _ol_number;
                int _c_id_local;
                int _tempqtd;
                int _s_remote_cnt;
                
                statement = con.prepareStatement("update district set d_next_o_id = d_next_o_id + 1 where d_w_id = ? and d_id = ?");
                statement.setInt(1,_w_id);
                statement.setInt(2,_d_id);
                statement.executeUpdate();
                statement.close();
                
                statement = con.prepareStatement("select d_tax, d_next_o_id from district where d_w_id = ? and d_id = ?");
                statement.setInt(1,_w_id);
                statement.setInt(2,_d_id);
                rs = statement.executeQuery();
                rs.next();
                _d_tax = rs.getDouble("d_tax");
                _o_id = rs.getInt("d_next_o_id");
                rs.close();
                statement.close();
                
                _li_no = 0;
                _commit_flag = 0;
                
                while (_li_no < _o_ol_cnt) {
                    _li_id = Integer.parseInt( (String) obj.getInfo("iid" + _li_no));
                    _li_s_w_id =  Integer.parseInt( (String) obj.getInfo("supwid" + _li_no));
                    _li_qty = Integer.parseInt( (String) obj.getInfo("qtdi" + _li_no));
                    
                    statement = con.prepareStatement("select i_price, i_name, i_data from item where i_id = ?");
                    statement.setInt(1,_li_id);
                    rs = statement.executeQuery();
                    rs.next();
                    _i_price = rs.getDouble("i_price");
                    _i_name = rs.getString("i_name");
                    _i_data = rs.getString("i_data");
                    rs.close();
                    statement.close();
                    
                    statement = con.prepareStatement("update stock set s_ytd = s_ytd + " + _li_qty + ", s_order_cnt = s_order_cnt + 1 where s_i_id = ? and s_w_id = ?");
                    statement.setInt(1,_li_id);
                    statement.setInt(2,_li_s_w_id);
                    statement.executeUpdate();
                    statement.close();
                    
                    statement = con.prepareStatement("select * from stock where s_i_id = ? and s_w_id = ?");
                    statement.setInt(1,_li_id);
                    statement.setInt(2,_li_s_w_id);
                    rs = statement.executeQuery();
                    rs.next();
                    _s_quantity = rs.getInt("s_quantity");
                    _s_remote_cnt = rs.getInt("s_remote_cnt");
                    _s_data = rs.getString("s_data");
                    
                    if ((_s_quantity - _li_qty) < 10) {
                        _tempqtd = 91;
                    }
                    else {
                        _tempqtd = 0;
                    }
                    _s_quantity = _s_quantity - _li_qty + _tempqtd;
                    
                    if (_li_s_w_id == _w_id ) {
                        _tempqtd = 0;
                    }
                    else {
                        _tempqtd = 1;
                    }
                    _s_remote_cnt = _s_remote_cnt + _tempqtd;
                    
                    _s_dist = "";
                    if (_d_id == 1) {
                        _s_dist = rs.getString("s_dist_01");
                    }
                    else if (_d_id == 2) {
                        _s_dist = rs.getString("s_dist_02");
                    }
                    else if (_d_id == 3) {
                        _s_dist = rs.getString("s_dist_03");
                    }
                    else if (_d_id == 4) {
                        _s_dist = rs.getString("s_dist_04");
                    }
                    else if (_d_id == 5) {
                        _s_dist = rs.getString("s_dist_05");
                    }
                    else if (_d_id == 6) {
                        _s_dist = rs.getString("s_dist_06");
                    }
                    else if (_d_id == 7) {
                        _s_dist = rs.getString("s_dist_07");
                    }
                    else if (_d_id == 8) {
                        _s_dist = rs.getString("s_dist_08");
                    }
                    else if (_d_id == 9) {
                        _s_dist = rs.getString("s_dist_09");
                    }
                    else if (_d_id == 10) {
                        _s_dist = rs.getString("s_dist_10");
                    }
                    rs.close();
                    statement.close();
                    
                    statement = con.prepareStatement("update stock set s_quantity = ?, s_remote_cnt = ? where s_i_id = ? and s_w_id = ?");
                    statement.setInt(1,_s_quantity);
                    statement.setInt(2,_s_remote_cnt);
                    statement.setInt(3,_li_id);
                    statement.setInt(4,_li_s_w_id);
                    int found = statement.executeUpdate();
                    statement.close();
                    if (found > 0) {
                        statement = con.prepareStatement("insert into order_line " +
                        " (ol_o_id, ol_d_id, ol_w_id, ol_number, ol_i_id, ol_supply_w_id, ol_delivery_d, ol_quantity, ol_amount, ol_dist_info) " +
                        " values (?,?,?,?,?,?,?,?,?,?)");
                        statement.setInt(1,_o_id);
                        statement.setInt(2,_d_id);
                        statement.setInt(3,_w_id);
                        statement.setInt(4,_li_no);
                        statement.setInt(5,_li_id);
                        statement.setInt(6,_li_s_w_id);
                        statement.setString(7, "dec 31, 1899");
                        statement.setInt(8,_li_qty);
                        statement.setDouble(9,_i_price * _li_qty);
                        statement.setString(10,_s_dist);
                        statement.executeUpdate();
                        statement.close();
                        _commit_flag = 1;
                    }
                    else {
                        _commit_flag = 0;
                    }
                    
                    _li_no++;
                }
                statement = con.prepareStatement("select c_last, c_discount, c_credit, c_id from customer where c_id = ? and c_w_id = ? and c_d_id = ?");
                statement.setInt(1,_c_id);
                statement.setInt(2,_w_id);
                statement.setInt(3,_d_id);
                rs = statement.executeQuery();
                rs.next();
                _c_last = rs.getString("c_last");
                _c_discount = rs.getDouble("c_discount");
                _c_credit = rs.getString("c_credit");
                _c_id_local = rs.getInt("c_id");
                rs.close();
                statement.close();
                
                statement = con.prepareStatement("insert into orders (o_id,o_d_id,o_w_id,o_c_id,o_entry_d,o_carrier_id,o_ol_cnt,o_all_local) " +
                " values  (?,?,?,?,?, 0,?,?) ");
                statement.setInt(1,_o_id);
                statement.setInt(2,_d_id);
                statement.setInt(3,_w_id);
                statement.setInt(4, _c_id_local);
                statement.setDate(5,_o_entry_d);
                statement.setInt(6,_o_ol_cnt);
                statement.setInt(7,_o_all_local);
                statement.executeUpdate();
                statement.close();
                
                statement = con.prepareStatement("insert into new_order (no_o_id,no_d_id,no_w_id) " +
                "values (?, ?, ?)");
                statement.setInt(1,_o_id);
                statement.setInt(2,_d_id);
                statement.setInt(3,_w_id);
                statement.executeUpdate();
                statement.close();
                
                statement = con.prepareStatement("select w_tax from warehouse where w_id = ?");
                statement.setInt(1,_w_id);
                rs = statement.executeQuery();
                rs.next();
                _w_tax = rs.getDouble("w_tax");
                rs.close();
                statement.close();
                
                if (_commit_flag == 0) {
                    throw new Exception("TPC-C Generated Abort - Item not Found.");
                }
                
                rs = null;
                statement = null;

                endTime = new java.util.Date();
                dbLog.logToFile(dbLog.NO,"NEW_ORDER_EXECUTION_TIME = " +dbMySql.NewOrderTransNum++
                                        +" T= "+(endTime.getTime()-startTime.getTime())
                                        +" E= "+pErro+" T1= "+startTime.getTime()
                                        +" T2= "+endTime.getTime()
                                        +" Client= "+Thread.currentThread().getName());
            }
            catch (java.sql.SQLException sqlex) {
                System.err.println(Thread.currentThread().getName() + " NewOrder - SQL Exception " + sqlex.getMessage());
                if (sqlex.getMessage().indexOf("deadlock") != -1) continue;
                throw sqlex;
            }
            catch (java.lang.Exception ex) {
                System.err.println(Thread.currentThread().getName() + " NewOrder - General Exception " + ex.getMessage());
                throw new java.sql.SQLException(ex.getMessage());
            }
            finally {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            return (dbtrace);
        }
    }
    
    public Object TraceDeliveryDB(OutInfo obj,String hid) throws java.sql.
    SQLException {
        
        Connection con = null;
        HashSet dbtrace = null;
        
        try {
            con = getConnection();
            
            System.out.println("Beginning transaction new order (thread(" + Thread.currentThread().getName() + "))");

            dbLog.log("Beginning trasaction delivery (thread(" + Thread.currentThread().getName() + "))");

            java.util.Date lDateBegin1 = new java.util.Date();

            InitTransaction(obj, con, "tx delivery");
            
            dbtrace = DeliveryDB(obj, con);

            CommitTransaction(con);

	    java.util.Date lDateEnd1 = new java.util.Date();

            dbLog.log("\tFinishing trasaction delivery (thread(" + Thread.currentThread().getName()
                    + "))\tB1 = "+lDateBegin1.getTime()
                    +"\tE1 = "+lDateEnd1.getTime());
            
            System.out.println("Finishing trasaction delivery (thread(" + Thread.currentThread().getName() + "))");
            
        }
        catch (java.lang.Exception ex) {
            System.err.println(Thread.currentThread().getName() + " Delivery - General Main Exception " + ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            returnConnection(con);
        }
        return (dbtrace);
    }
    
    public HashSet DeliveryDB(OutInfo obj, Connection con) throws java.sql.
    SQLException {
        while (true) {
            PreparedStatement statement = null;
            ResultSet rs = null;
            String cursor = null;

            java.util.Date startTime = null;
            java.util.Date endTime = null;
            startTime = new java.util.Date();

            HashSet dbtrace = new HashSet();
            
            try {
                int _w_id =  Integer.parseInt( (String) obj.getInfo("wid"));
                int _o_carrier_id = Integer.parseInt( (String) obj.getInfo("crid"));
                
                int _d_id;
                int _o_id;
                int _c_id;
                double _total;
                int _oid1;
                int _oid2;
                int _oid3;
                int _oid4;
                int _oid5;
                int _oid6;
                int _oid7;
                int _oid8;
                int _oid9;
                int _oid10;
                
                _d_id = 0;
                
                while (_d_id < 10) {
                    _d_id = _d_id + 1;
                    _total = 0;
                    _o_id = 0;
                    
                    statement = con.prepareStatement("select no_o_id from new_order where no_w_id = ? and no_d_id = ? " +
                    "order by no_o_id asc limit 1");
                    statement.setInt(1,_w_id);
                    statement.setInt(2,_d_id);
                    rs = statement.executeQuery();
                    
                    rs.next();
                    _o_id = rs.getInt("no_o_id");
                    rs.close();
                    statement.close();
                    
                    if (_o_id != 0) {
                        statement = con.prepareStatement("delete from new_order where no_w_id = ? and no_d_id = ? and no_o_id = ?");
                        statement.setInt(1,_w_id);
                        statement.setInt(2,_d_id);
                        statement.setInt(3,_o_id);
                        statement.executeUpdate();
                        statement.close();
                        
                        statement = con.prepareStatement("update orders set o_carrier_id = ? where o_w_id = ? and " +
                        "o_d_id = ? and o_id = ?");
                        statement.setInt(1,_o_carrier_id);
                        statement.setInt(2,_w_id);
                        statement.setInt(3,_d_id);
                        statement.setInt(4,_o_id);
                        statement.executeUpdate();
                        statement.close();
                        
                        statement = con.prepareStatement("select o_c_id from orders where o_w_id = ? and o_d_id = ? and o_id = ?");
                        statement.setInt(1,_w_id);
                        statement.setInt(2,_d_id);
                        statement.setInt(3,_o_id);
                        rs = statement.executeQuery();
                        rs.next();
                        _c_id = rs.getInt("o_c_id");
                        rs.close();
                        statement.close();
                        
                        statement = con.prepareStatement("update order_line set ol_delivery_d = CURRENT_TIMESTAMP where ol_w_id = ? " +
                        "and ol_d_id = ? and ol_o_id = ?");
                        statement.setInt(1,_w_id);
                        statement.setInt(2,_d_id);
                        statement.setInt(3,_o_id);
                        statement.executeUpdate();
                        statement.close();
                        
                        statement = con.prepareStatement(" select ol_amount from order_line where ol_w_id = ? and  ol_d_id = ? " +
                        " and ol_o_id = ?");
                        statement.setInt(1,_w_id);
                        statement.setInt(2,_d_id);
                        statement.setInt(3,_o_id);
                        rs = statement.executeQuery();
                        rs.next();
                        _total = _total + rs.getInt("ol_amount");
                        rs.close();
                        statement.close();
                        
                        statement = con.prepareStatement("update customer set c_balance = c_balance + " + _total + "," +
                        " c_delivery_cnt = c_delivery_cnt + 1 " +
                        "  where c_w_id = ? and c_d_id = ? and  c_id = ?");
                        statement.setInt(1,_w_id);
                        statement.setInt(2,_d_id);
                        statement.setInt(3,_c_id);
                        statement.executeUpdate();
                        statement.close();
                    }
                    
                    if (_d_id == 1) {
                        _oid1 = _o_id;
                    }
                    else if (_d_id == 2) {
                        _oid2 = _o_id;
                    }
                    else if (_d_id == 3) {
                        _oid3 = _o_id;
                    }
                    else if (_d_id == 4) {
                        _oid4 = _o_id;
                    }
                    else if (_d_id == 5) {
                        _oid5 = _o_id;
                    }
                    else if (_d_id == 6) {
                        _oid6 = _o_id;
                    }
                    else if (_d_id == 7) {
                        _oid7 = _o_id;
                    }
                    else if (_d_id == 8) {
                        _oid8 = _o_id;
                    }
                    else if (_d_id == 9) {
                        _oid9 =_o_id;
                    }
                    else if (_d_id == 10) {
                        _oid10 = _o_id;
                    }
                }
                
                rs = null;
                statement = null;

		endTime = new java.util.Date();
                dbLog.logToFile(dbLog.DL,"DELIVERY_EXECUTION_TIME = "+dbMySql.DeliveryTransNum++
                                        +" T= "+(endTime.getTime()-startTime.getTime())
                                        +" E= "+pErro+" T1= "+startTime.getTime()
                                        +" T2= "+endTime.getTime()
                                        +" Client= "+Thread.currentThread().getName());

            }
            catch (java.sql.SQLException sqlex) {
                System.err.println(Thread.currentThread().getName() + " Delivery - SQL Exception " + sqlex.getMessage());
                if (sqlex.getMessage().indexOf("deadlock") != -1) continue;
                throw sqlex;
            }
            catch (java.lang.Exception ex) {
                System.err.println(Thread.currentThread().getName() + " Delivery - General Exception " + ex.getMessage());
                throw new java.sql.SQLException(ex.getMessage());
            }
            finally {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            return (dbtrace);
        }
    }
    
    public Object TraceOrderStatusDB(OutInfo obj,String hid) throws java.sql.
    SQLException {
        
        Connection con = null;
        HashSet dbtrace = null;
        
        try {
            con = getConnection();
            
            System.out.println("Beginning transaction order status (thread(" + Thread.currentThread().getName() + "))");

	    dbLog.log("Beginning transaction order status (thread(" + Thread.currentThread().getName() + "))");

            java.util.Date lDateBegin1 = new java.util.Date();

            InitTransaction(obj, con, "tx orderstatus");

            dbtrace = OrderStatusDB(obj, con);

            CommitTransaction(con);

            java.util.Date lDateEnd1 = new java.util.Date();

            dbLog.log("\tFinishing transaction order status (thread(" + Thread.currentThread().getName()
                    + "))\tB1 = "+lDateBegin1.getTime()
                    + "\tE1 = "+lDateEnd1.getTime());
            
            
            System.out.println("Finishing transaction order status (thread(" + Thread.currentThread().getName() + "))");
        }
        catch (java.lang.Exception ex) {
            System.err.println(Thread.currentThread().getName() + " OrderStatus - General Main Exception " + ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            returnConnection(con);
        }
        return (dbtrace);
    }
    
    
    public HashSet OrderStatusDB(OutInfo obj, Connection con) throws java.
    sql.SQLException {
        while (true) {
            PreparedStatement statement = null;
            ResultSet rs = null;
            String cursor = null;

            java.util.Date startTime = null;
	    java.util.Date endTime = null;
            startTime = new java.util.Date();
 
            HashSet dbtrace = new HashSet();
            
            try {
                int __w_id = Integer.parseInt( (String) obj.getInfo("wid"));
                int __d_id = Integer.parseInt( (String) obj.getInfo("did"));
                int __c_id = Integer.parseInt( (String) obj.getInfo("cid"));
                String __c_last = (String) obj.getInfo("lastname");
                
                double _c_balance;
                String _c_first;
                String _c_middle;
                int _o_id;
                java.sql.Date _o_entry_d;
                int _o_carrier_id;
                int _cnt;
                int _c_id = __c_id;
                String _c_last;
                
                if  (__c_id == 0) {
                    statement = con.prepareStatement("select c_id, c_last, c_balance, c_first, c_middle " +
                    " from customer where c_last = ? and " +
                    " c_w_id = ? and c_d_id = ? " +
                    " order by c_w_id, c_d_id, c_last, c_first limit 1 ");
                    statement.setString(1,__c_last);
                    statement.setInt(2,__w_id);
                    statement.setInt(3,__d_id);
                    rs = statement.executeQuery();
                    if (rs.next()) {
                        _c_id  = rs.getInt("c_id");
                        _c_last = rs.getString("c_last");
                        _c_balance = rs.getDouble("c_balance");
                        _c_first = rs.getString("c_first");
                        _c_middle = rs.getString("c_middle");
                        _cnt = 1;
                    }
                    else _cnt = 0;
                    rs.close();
                    statement.close();
                }
                else {
                    statement = con.prepareStatement("select c_id, c_last, c_balance, c_first, c_middle " +
                    " from customer where c_id = ? and c_d_id = ? and c_w_id = ?");
                    statement.setString(1,__c_last);
                    statement.setInt(2,__w_id);
                    statement.setInt(3,__d_id);
                    rs = statement.executeQuery();
                    
                    if (rs.next()) {
                        _c_id  = rs.getInt("c_id");
                        _c_last = rs.getString("c_last");
                        _c_balance = rs.getDouble("c_balance");
                        _c_first = rs.getString("c_first");
                        _c_middle = rs.getString("c_middle");
                        _cnt = 1;
                    }
                    else _cnt = 0;
                    rs.close();
                    statement.close();
                }
                
                if (_cnt == 0) {
                    throw new SQLException("TPC-C Generated Abort - Customer Not Found.");
                }
                else {
                    statement = con.prepareStatement("select o_id, o_entry_d, o_carrier_id from orders where o_c_id = ? " +
                    " and o_d_id = ? and o_w_id = ? order by o_id asc limit 1");
                    statement.setInt(1,_c_id);
                    statement.setInt(2,__d_id);
                    statement.setInt(3,__w_id);
                    rs = statement.executeQuery();
                    rs.next();
                    _o_id = rs.getInt("o_id");
                    _o_entry_d = rs.getDate("o_entry_d");
                    _o_carrier_id = rs.getInt("o_carrier_id");
                    rs.close();
                    statement.close();
                    
                    statement = con.prepareStatement(" select * from order_line where ol_o_id = ? and  ol_d_id = ? " +
                    " and ol_w_id = ?");
                    statement.setInt(1,_o_id);
                    statement.setInt(2,__d_id);
                    statement.setInt(3,__w_id);
                    rs = statement.executeQuery();
                    
                    while (rs.next()) {
                        /*DO NOTHING*/
                    }
                    
                    rs.close();
                    statement.close();
                }
                rs = null;
                statement = null;

                endTime = new java.util.Date();
                dbLog.logToFile(dbLog.OS,"ORDER_STATUS_EXECUTION_TIME = "+dbMySql.OrderStatusTransNum++
                                        +" T= "+(endTime.getTime()-startTime.getTime())
                                        +" E= "+pErro+" T1= "+startTime.getTime()
                                        +" T2= "+endTime.getTime()
                                        +" Client= "+Thread.currentThread().getName());
            }
            catch (java.sql.SQLException sqlex) {
                System.err.println(Thread.currentThread().getName() + " OrderStatus - SQL Exception " + sqlex.getMessage());
                if (sqlex.getMessage().indexOf("deadlock") != -1) continue;
                throw sqlex;
            }
            catch (java.lang.Exception ex) {
                System.err.println(Thread.currentThread().getName() + " OrderStatus - General Exception " + ex.getMessage());
                ex.printStackTrace();
		throw new java.sql.SQLException(ex.getMessage());
            }
            finally {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            return (dbtrace);
        }
    }
    
    public Object TracePaymentDB(OutInfo obj,String hid) throws java.sql.SQLException {
        
        Connection con = null;
        HashSet dbtrace = null;
        
        try {
            con = getConnection();
            
            System.out.println("Beginning transaction payment (thread(" + Thread.currentThread().getName() + "))");

            dbLog.log("Beginning transaction payment (thread(" + Thread.currentThread().getName() + "))");

            java.util.Date lDateBegin1 = new java.util.Date();

            InitTransaction(obj, con, "tx payment");
            
            dbtrace = PaymentDB(obj, con);

            CommitTransaction(con);

            java.util.Date lDateEnd1 = new java.util.Date();

            dbLog.log("\tFinishing transaction payment (thread(" + Thread.currentThread().getName()
                    + "))\tB1 = "+lDateBegin1.getTime()
                    + "\tE1 = "+lDateEnd1.getTime());
            
            System.out.println("Finishing transaction payment (thread(" + Thread.currentThread().getName() + "))");
            
        }
        catch (java.lang.Exception ex) {
            System.err.println(Thread.currentThread().getName() + " Payment - General Main Exception " + ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            returnConnection(con);
        }
        return (dbtrace);
    }
    
    public HashSet PaymentDB(OutInfo obj, Connection con) throws java.sql.
    SQLException {
        while (true) {
            PreparedStatement statement = null;
            ResultSet rs = null;
            String cursor = null;

            java.util.Date startTime = null;
            java.util.Date endTime = null;
	    startTime = new java.util.Date();

            HashSet dbtrace = new HashSet();
            
            try {
                int __w_id = Integer.parseInt( (String) obj.getInfo("wid"));
                int __c_w_id = Integer.parseInt( (String) obj.getInfo("cwid"));
                float __h_amount = Float.parseFloat( (String) obj.getInfo("hamount"));
                int __d_id = Integer.parseInt( (String) obj.getInfo("did"));
                int __c_d_id = Integer.parseInt( (String) obj.getInfo("cdid"));
                int __c_id = Integer.parseInt( (String) obj.getInfo("cid"));
                String __c_last = (String) obj.getInfo("lastname");
                
                String _w_street_1;
                String _w_street_2;
                String _w_city;
                String _w_state;
                String _w_zip;
                String _w_name;
                String _d_street_1;
                String _d_street_2;
                String _d_city;
                String _d_state;
                String _d_zip;
                String _d_name;
                String _c_first;
                String _c_middle;
                String _c_street_1;
                String _c_street_2;
                String _c_city;
                String _c_state;
                String _c_zip;
                String _c_phone;
                java.sql.Date _c_since;
                String _c_credit;
                float _c_credit_lim;
                float _c_balance;
                float _c_discount;
                String _data;
                String _c_data;
                java.sql.Date _datetime = new java.sql.Date(2000,1,25);
                float _w_ytd;
                float _d_ytd;
                int _cnt;
                int _val;
                String _screen_data;
                int _d_id_local;
                int _w_id_local;
                int _c_id_local;
                int _c_id = __c_id;
                String _c_last;
                
                if (__c_id == 0) {
                    statement = con.prepareStatement("select c_id from customer where c_last = ? and " +
                    " c_w_id = ? and c_d_id = ? order by c_w_id, c_d_id, c_last, c_first limit 1");
                    statement.setString(1,__c_last);
                    statement.setInt(2,__c_w_id);
                    statement.setInt(3,__c_d_id);
                    rs = statement.executeQuery();
                    
                    if (rs.next()) {
                        _cnt = 1;
                        _c_id = rs.getInt("c_id");
                    }
                    else {
                        _cnt = 0;
                    }
                    rs.close();
                    statement.close();
                }
                else {
                    _c_id = __c_id;
                }
                
                statement = con.prepareStatement("update customer set c_balance = c_balance - " + __h_amount + "," +
                                                    " c_payment_cnt = c_payment_cnt + 1, c_ytd_payment = c_ytd_payment + " + __h_amount +
                                                    "  where c_id = ? and c_w_id = ? and  c_d_id = ?");
                statement.setInt(1,_c_id);
                statement.setInt(2,__c_w_id);
                statement.setInt(3,__c_d_id);
                statement.executeUpdate();
                statement.close();
                
                statement = con.prepareStatement("select *  from customer  where c_id = ? and  c_w_id = ? and  c_d_id = ?");
                statement.setInt(1,_c_id);
                statement.setInt(2,__c_w_id);
                statement.setInt(3,__c_d_id);
                rs = statement.executeQuery();
                //
                rs.next();
                    _c_balance = rs.getFloat("c_balance");
                    _c_first = rs.getString("c_first");
                    _c_middle = rs.getString("c_middle");
                    _c_last = rs.getString("c_last");
                    _c_street_1 = rs.getString("c_street_1");
                    _c_street_2 = rs.getString("c_street_2");
                    _c_city = rs.getString("c_city");
                    _c_state = rs.getString("c_state");
                    _c_zip = rs.getString("c_zip");
                    _c_phone = rs.getString("c_phone");
                    _c_credit = rs.getString("c_credit");
                    _c_credit_lim = rs.getFloat("c_credit_lim");
                    _c_discount = rs.getFloat("c_discount");
                    _c_since = rs.getDate("c_since");
                    _data = rs.getString("c_data");
                    _c_id_local = rs.getInt("c_id");
                    
                    if (_c_credit.equals("BC")) {
                        _c_data = "" + _c_id;
                        
                        statement = con.prepareStatement("update customer set c_data = ? where c_id = ? and " +
                        " c_w_id = ? and c_d_id = ?");
                        statement.setString(1,_c_data);
                        statement.setInt(2,_c_id);
                        statement.setInt(3,__c_w_id);
                        statement.setInt(4,__c_d_id);
                        statement.executeUpdate();
                        statement.close();
                    }
                
                statement = con.prepareStatement("update district set d_ytd = d_ytd + " + __h_amount +
                " where d_w_id = ? and d_id = ? ");
                statement.setInt(1,__w_id);
                statement.setInt(2,__d_id);
                statement.executeUpdate();
                statement.close();
                
                statement = con.prepareStatement("select *  from district where d_w_id = ? and d_id = ? ");
                statement.setInt(1,__w_id);
                statement.setInt(2,__d_id);
                rs = statement.executeQuery();
                rs.next();
                _d_street_1 = rs.getString("d_street_1");
                _d_street_2 = rs.getString("d_street_2");
                _d_city = rs.getString("d_city");
                _d_state = rs.getString("d_state");
                _d_zip = rs.getString("d_zip");
                _d_name = rs.getString("d_name");
                _d_id_local = rs.getInt("d_id");
                rs.close();
                statement.close();
                
                statement = con.prepareStatement("update warehouse  set w_ytd = w_ytd + " + __h_amount +
                "  where w_id = ?" );
                statement.setInt(1,__w_id);
                statement.executeUpdate();
                statement.close();
                
                statement = con.prepareStatement("select * from warehouse  where w_id = ?");
                statement.setInt(1,__w_id);
                rs = statement.executeQuery();
                rs.next();
                
                _w_street_1 = rs.getString("w_street_1");
                _w_street_2 = rs.getString("w_street_2");
                _w_city = rs.getString("w_city");
                _w_state = rs.getString("w_state");
                _w_zip = rs.getString("w_zip");
                _w_name = rs.getString("w_name");
                _w_id_local = rs.getInt("w_id");
                rs.close();
                statement.close();
                
                statement = con.prepareStatement("insert into history (h_c_id, h_c_d_id, h_c_w_id, h_d_id, h_w_id, h_date, h_amount, h_data ) " +
                " values ( ?, ?, ?, ?, ?, ?, ?, ?)");
                statement.setInt(1,_c_id_local);
                statement.setInt(2,__c_d_id);
                statement.setInt(3,__c_w_id);
                statement.setInt(4,_d_id_local);
                statement.setInt(5,_w_id_local);
                statement.setDate(6,_datetime);
                statement.setFloat(7,__h_amount);
                statement.setString(8,_w_name + " " + _d_name);
                statement.executeUpdate();
                statement.close();
                
                rs = null;
                statement = null;

                endTime = new java.util.Date();
                dbLog.logToFile(dbLog.PA,"PAYMENT_EXECUTION_TIME = "+dbMySql.PaymentTransNum++
                                        +" T= "+(endTime.getTime()-startTime.getTime())
                                        +" E= "+pErro+" T1= "+startTime.getTime()
                                        +" T2= "+endTime.getTime() +" Client= "
                                        +Thread.currentThread().getName());
            }
            catch (java.sql.SQLException sqlex) {
                System.err.println(Thread.currentThread().getName() + " Payment - SQL Exception " + sqlex.getMessage());
                if (sqlex.getMessage().indexOf("deadlock") != -1) continue;
                throw sqlex;
            }
            catch (java.lang.Exception ex) {
                System.err.println(Thread.currentThread().getName() + " Payment - General Exception " + ex.getMessage());
                throw new java.sql.SQLException(ex.getMessage());
            }
            finally {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            return (dbtrace);
        }
    }
    
    public Object TraceStockLevelDB(OutInfo obj,String hid) throws java.sql.SQLException {
        
        Connection con = null;
        HashSet dbtrace = null;
        
        try {
            con = getConnection();
            
            System.out.println("Beginning transaction stock level (thread(" + Thread.currentThread().getName() + "))");

            dbLog.log("Beginning transaction stock level (thread(" + Thread.currentThread().getName() + "))");

            java.util.Date lDateBegin1 = new java.util.Date();

            InitTransaction(obj, con, "tx stocklevel");

            dbtrace = StockLevelDB(obj, con);

            CommitTransaction(con);

            java.util.Date lDateEnd1 = new java.util.Date();

            dbLog.log("\tFinishing transaction stock level (thread(" + Thread.currentThread().getName()
                    + "))\tB1 = "+lDateBegin1.getTime()
                    + "\tE1 = "+lDateEnd1.getTime());
            
            System.out.println("Finishing transaction stock level (thread(" + Thread.currentThread().getName() + "))");
        }
        catch (java.lang.Exception ex) {
            System.err.println(Thread.currentThread().getName() + " StockLevel - General Main Exception " + ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            returnConnection(con);
        }
        return (dbtrace);
    }
    
    public HashSet StockLevelDB(OutInfo obj, Connection con) throws java.
    sql.SQLException {
        while (true) {
            PreparedStatement statement = null;
            ResultSet rs = null;
            String cursor = null;

            java.util.Date startTime = null;
            java.util.Date endTime = null;
            startTime = new java.util.Date();

            HashSet dbtrace = new HashSet();
            
            try {
                int __w_id = Integer.parseInt( (String) obj.getInfo("wid"));
                int __d_id = Integer.parseInt( (String) obj.getInfo("did"));
                int __threshhold = Integer.parseInt( (String) obj.getInfo("threshhold"));
                
                int _o_id_low;
                int _o_id_high;
                
                statement = con.prepareStatement("select d_next_o_id  from district where d_w_id = ? and  d_id = ?");
                statement.setInt(1,__w_id);
                statement.setInt(2,__d_id);
                rs = statement.executeQuery();
                rs.next();
                _o_id_low = rs.getInt("d_next_o_id") - 20;
                _o_id_high = rs.getInt("d_next_o_id") - 1;
                rs.close();
                statement.close();
                
                statement = con.prepareStatement("select count(distinct(s_i_id)) from stock, order_line where ol_w_id = ? and " +
                " ol_d_id = ? and ol_o_id >= ? and ol_o_id <= ? and s_w_id = ol_w_id and s_i_id = ol_i_id and s_quantity < ? ");
                statement.setInt(1,__w_id);
                statement.setInt(2,__d_id);
                statement.setInt(3,_o_id_low);
                statement.setInt(4,_o_id_high);
                statement.setInt(5,__threshhold);
                rs = statement.executeQuery();
                while (rs.next()) {
                    /*DO NOTHING*/
                }
                rs.close();
                statement.close();
                
                rs = null;
                statement = null;

                endTime = new java.util.Date();
                dbLog.logToFile(dbLog.SL,"STOCK_LEVEL_EXECUTION_TIME = "+dbMySql.StockLevelTransNum++
                                        +" T= "+(endTime.getTime()-startTime.getTime())
                                        +" E= "+pErro+" T1= "+startTime.getTime()
                                        +" T2= "+endTime.getTime()
                                        +" Client= "+Thread.currentThread().getName());

            }
            catch (java.sql.SQLException sqlex) {
                System.err.println(Thread.currentThread().getName() + " StockLevel - SQL Exception " + sqlex.getMessage());
                if (sqlex.getMessage().indexOf("deadlock") != -1) continue;
                throw sqlex;
            }
            catch (java.lang.Exception ex) {
                System.err.println(Thread.currentThread().getName() + " StockLevel - General Exception " + ex.getMessage());
                throw new java.sql.SQLException(ex.getMessage());
            }
            finally {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            return (dbtrace);
        }
    }
    
  public static void InitTransaction(OutInfo obj, Connection con,
                                     String transaction) throws
      java.sql.SQLException {
    Statement statement = null;
    try {
	
      statement = con.createStatement();
      /*statement.execute("start transaction");
      if (trace(obj) || traceString(obj)) {
        statement.execute("SET TRANSACTION ISOLATION LEVEL READ COMMITTED");
      }
      else {
        statement.execute("SET TRANSACTION ISOLATION LEVEL SERIALIZABLE");
      }*/
      statement.execute("select '" + transaction + "'");
    }
    catch (java.lang.Exception ex) {
      if (con != null) {
        RollbackTransaction(con, ex);
      }
      throw new java.sql.SQLException();
    }
    finally {
      if (statement != null) {
        statement.close();
      }
    }
  }

  public static void CommitTransaction(Connection con) throws java.sql.
      SQLException {
    {
      Statement statement = null;
      try {
        /*statement = con.createStatement();
        statement.execute("commit");*/
      }
      catch (java.lang.Exception ex) {
        if (con != null) {
          RollbackTransaction(con, ex);
        }
        throw new java.sql.SQLException();
      }
      finally {
        if (statement != null) {
          statement.close();
        }
      }
    }
  }

  public static void RollbackTransaction(Connection con, Exception dump) throws
      java.sql.SQLException {
    Statement statement = null;
    try {
      /*statement = con.createStatement();
      statement.execute("rollback");*/
    }
    catch (java.lang.Exception ex) {
      ex.printStackTrace();
    }
    finally {
      if (statement != null) {
        statement.close();
      }
    }
  }

  public static boolean trace(OutInfo obj) {
    return ( ( (String) obj.getInfo("trace")).equalsIgnoreCase("TRACE"));
  }

  public static boolean traceString(OutInfo obj) {
    return ( ( (String) obj.getInfo("trace")).equalsIgnoreCase(
        "TRACESTRING"));
  }

}
// arch-tag: 8aaab911-6c39-40cf-9c0a-d14dc6e7c471
