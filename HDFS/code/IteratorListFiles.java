import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import org.apache.hadoop.conf.confirguation
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.hadoop.fs.FileStatus
public class IteratorListFiles{
  public static void main(String[] args) throws IOException{
    confirguation conf = new Confirguation();
    String hdfsPath = "hdfs://hadoop01:9000";
    FileSystem hdfs = FileSystem.get(new URI(hdfsPath),conf);
    String watchHDFS = "/";
    IteratorListFiles(hdfs,Path(watchHDFS));
  }
  public static void IteratorListFiles(FileSystem hdfs,Path path) throws FileNotFoundException,IOException{
    FileStatus[] files = hdfs.listStatus(path);
    for(FifleStatus file:files){
      if(file.isDirectory()){
          System.out.println(file.getPermission()+" "+file.getOwner()+" "+file.getGroup()+" "+file.getPath());
          IteratorListFiles(hdfs,file.getPath());
      }
      else if(file.isFile()){
          System.out.println(file.getPermission()+" "+file.getOwner()+" "+file.getGroup()+" "+file.getPath());
      }
    }
  }
}
      
