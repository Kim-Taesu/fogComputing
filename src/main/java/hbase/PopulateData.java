package hbase;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

public class PopulateData {

    public static void main(String... args) throws Exception {

        Connection connection = ConnectionFactory.createConnection(HBaseConfiguration.create());

        TableName tableName = TableName.valueOf("customer");
        Table table = connection.getTable(tableName);

        Put p = new Put(Bytes.toBytes("row1"));
        //Customer table has personal and address column families. So insert data for 'name' column in 'personal' cf
        // and 'city' for 'address' cf
        p.addColumn(Bytes.toBytes("personal"), Bytes.toBytes("name"), Bytes.toBytes("bala"));
        p.addColumn(Bytes.toBytes("address"), Bytes.toBytes("city"), Bytes.toBytes("new york"));
        table.put(p);
        Get get = new Get(Bytes.toBytes("row1"));
        Result result = table.get(get);
        byte[] name = result.getValue(Bytes.toBytes("personal"), Bytes.toBytes("name"));
        byte[] city = result.getValue(Bytes.toBytes("address"), Bytes.toBytes("city"));
        System.out.println("Name: " + Bytes.toString(name) + " City: " + Bytes.toString(city));
        table.close();
        connection.close();
    }
}