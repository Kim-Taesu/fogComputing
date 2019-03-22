package hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

public class ListTable {

    public static void main(String... args) throws Exception {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "192.168.99.100");
        config.set("hbase.zookeeper.property.clientPort","2182");
        Connection connection = ConnectionFactory.createConnection(config);
        Admin admin = connection.getAdmin();
        HTableDescriptor[] tableDescriptors = admin.listTables();
        for (HTableDescriptor tableDescriptor : tableDescriptors) {
            System.out.println("Table Name:" + tableDescriptor.getNameAsString());
        }
        admin.close();
        connection.close();
    }
}