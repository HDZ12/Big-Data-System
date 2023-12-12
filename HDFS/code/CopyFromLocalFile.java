import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import org.apache.hadoop.conf.confirguation
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
public class CopyFromLocalFile{
    public static void main(String[] args) throws IOException,URISyntaxException{
        confirguation conf = new Confirguation;
        String hdfsPath = "hdfs://hadoop01:9000";
        FileSystem hdfs = FileSystem.get(new URI(hdfsPath),conf);
        String from_linux = "/export/data/hadoop4/sample_data";
        String to_HDFS = "/hdfstest/";
        hdfs.copyFromLocalFile(new Path(from_linux),new Path(to_HDFS));
        System.out.println("Finish!");
    }
}
