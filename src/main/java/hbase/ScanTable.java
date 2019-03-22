package hbase;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class ScanTable {

    public static void main(String... args) {
        Connection connection = null;
        ResultScanner scanner = null;
        try {
            connection = ConnectionFactory.createConnection(HBaseConfiguration.create());
            TableName tableName = TableName.valueOf("customer");
            Table table = connection.getTable(tableName);
            Scan scan = new Scan();
            // Scanning the required columns
            scan.addColumn(Bytes.toBytes("personal"), Bytes.toBytes("name"));

            scanner = table.getScanner(scan);

            // Reading values from scan result
            for (Result result = scanner.next(); result != null; result = scanner.next())
                System.out.println("Found row : " + result);
            //closing the scanner
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) scanner.close();
            if (connection != null) try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}