import org.apache.hadoop.conf.Configuration;  
import org.apache.hadoop.hbase.HBaseConfiguration;  
import org.apache.hadoop.hbase.KeyValue;  
import org.apache.hadoop.hbase.client.HTable;  
import org.apache.hadoop.hbase.client.Result;  
import org.apache.hadoop.hbase.client.ResultScanner;  
import org.apache.hadoop.hbase.client.Scan;  
import org.apache.hadoop.hbase.filter.PrefixFilter;  
import org.apache.hadoop.hbase.filter.Filter;  
import org.apache.hadoop.hbase.util.Bytes;
import java.io.IOException;  
public class Prffilre{
    public static void main(String[] args) throws IOException{
        Configuration conf = new HBaseConfiguration.create();
        HTable table = new HTable(conf,"order_items");
        Filter filter = new PrefixFilter(Bytes.toBytes("row"));
        Scan scan = new Scan();
        scan.setFilter(filter);
        ResultScanner scanner = table.getscanner(scan);
        System.out.println("Scanning table....");
        for(Result result:scanner){
            //System.out.println("getRow:"+Bytes.toString(result.getRow()));  
            for(KeyValue kv:result.raw()){
                //System.out.println("Family - "+Bytes.toString(kv.getFamily()));  
               //System.out.println("Qualifier - "+Bytes.toString(kv.getQualifier() ));
              System.out.println("kv:"+kv+",Key:"+Bytes.toString(kv.getRow())+",Value:"+Bytes.toString(kv.getValue()));
            }
        }
        scanner.close();
        System.out.println("Completed");
    }
}
