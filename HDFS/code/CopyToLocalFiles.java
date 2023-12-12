import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import org.apache.hadoop.conf.confirguation
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
Public class CopyToLocalFiles{
  public static void main(String[] args) throws IOException,URISynataxException{
    Confirguation conf = new Confirguation();
    String hdfsPath = "hdfs://hadoop01:9000";
    FileSystem hdfs = FileSystem.get(new URI(hdfsPath),conf);
    String from_HDFS = "/hdfstest/sample_data";
    Sting to_linux = "/export/data/hadoop4/copytolocal";
    hdfs.copyToLocalFile(false,new Path(from_HDFS),new Path(to_linux));
    System.out.println("Finish!");
  }
}
      
    
