import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import org.apache.hadoop.conf.confirguation
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.hadoop.fs.FileStatus
public class ListFiles{
  public static void main(String[] args) throws IOException,URISyntaxException{
    Confirguation conf = new Confirguation();
    String hdfsPath = "hdfs://hadoop01:9000";
    FileSystem hdfs = FileSystem.get(new URI(hdfsPath),conf);
    String watchHDFS = "/hdfstest";
    FileStatus[] files = hdfs.listStatus(new Path(watchHDFS));
    for(FileStatus file:files){
      System.out.println(file.getPermission()+" "+file.getOwner()+" "+file.getGroup()+" "+file.getPath());
    }
  }
}
