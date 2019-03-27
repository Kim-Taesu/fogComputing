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
        config.set("hbase.zookeeper.quorum", "hbase2");
        config.set("hbase.zookeeper.property.clientPort","2182");
//        config.set("hbase.master.port","60001");
//        config.set("hbase.master.info.port","60011");
//        config.set("hbase.regionserver.port","60021");
//        config.set("hbase.regionserver.info.port","60031");
        System.out.println(config);
        System.out.println(config.get("core-site.xml"));
        System.out.println(config.get("hbase-site.xml"));
        Connection connection = ConnectionFactory.createConnection(config);
        System.out.println(connection);

        Admin admin = connection.getAdmin();
        TableName tableName = TableName.valueOf("customer2");
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