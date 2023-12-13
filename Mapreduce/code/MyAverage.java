import java.io.IOException;  
import org.apache.hadoop.conf.Configuration;  
import org.apache.hadoop.fs.Path;  
import org.apache.hadoop.io.IntWritable;  
import org.apache.hadoop.io.NullWritable;  
import org.apache.hadoop.io.Text;  
import org.apache.hadoop.mapreduce.Job;  
import org.apache.hadoop.mapreduce.Mapper;  
import org.apache.hadoop.mapreduce.Reducer;  
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;  
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;  
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;  
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;  
public class MyAverage{
  public static class Map extends Mapper<Object, Text, Text,IntWritable>{
    private static Text newKey = new Text();
    public void map(Object key,Text value,Context context)throws IOException,InterruptedException{
        String line = value.toString();
        System.out.println(line);
        String arr[] = line.split("\t");
        newKey.set(arr[0]);
        int click = Integer.parseInt(arr[1]);
        context.write(newKey,new IntWritable(click));
    }
  }
  public static class Reduce extends Reducer<Text,IntWritable,Text,IntWritable>{
    public void reduce(Text key,Iterable<Intwritable> values,Context context) throws IOException,InterruptedException{
      int num=0;
      int count=0;
      for(IntWritable val:values){
        num+=val.get();
        count++;
      }
      int avg = num/count;
      context.write(key, new IntWritable(avg));
    }
  }
  pubic static void main(String[] args) throws IOException,ClassNotFoundException,InterruptedException{
    Configuration conf = new Configuration();
    System.out.println("start");
    Job job = new Job(conf,"MyAverage");
    job.setJarByClass(Myaverage.class);
    job.setMapperClass(Map.class);
    job.setReducerClass(Reduce.class);  
    job.setReducerClass(Reduce.class);  
    job.setOutputKeyClass(Text.class);  
    job.setOutputValueClass(IntWritable.class);  
    job.setInputFormatClass(TextInputFormat.class);  
    job.setOutputFormatClass(TextOutputFormat.class);  
    Path in=new Path("hdfs://localhost:9000/mymapreduce4/in/goods_click");  
    Path out=new Path("hdfs://localhost:9000/mymapreduce4/out");  
    FileInputFormat.addInputPath(job,in);  
    FileOutputFormat.setOutputPath(job,out);  
    System.exit(job.waitForCompletion(true) ? 0 : 1);  
  }
}
