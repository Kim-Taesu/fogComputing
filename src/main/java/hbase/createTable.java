package hbase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

public class createTable {

    public static void main(String... args) throws Exception {
        System.out.println("Creating Htable starts");
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "192.168.99.100");
        config.set("hbase.zookeeper.property.clientPort","2181");
        Connection connection = ConnectionFactory.createConnection(config);
        Admin admin = connection.getAdmin();
        TableName tableName = TableName.valueOf("customer");
        if (!admin.tableExists(tableName)) {
            HTableDescriptor htable = new HTableDescriptor(tableName);
            htable.addFamily(new HColumnDescriptor("personal"));
            htable.addFamily(new HColumnDescriptor("address"));
            admin.createTable(htable);
        } else {
            System.out.println("customer Htable is exists");
        }
        admin.close();
        connection.close();
        System.out.println("Creating Htable Done");
    }
}