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
public class Filter{  
    public static class Map extends Mapper<Object , Text , Text , NullWritable>{  
    private static Text newKey=new Text();  
    public void map(Object key,Text value,Context context) throws IOException, InterruptedException{  
    String line=value.toString();  
    System.out.println(line);  
    String arr[]=line.split("\t");  
    newKey.set(arr[1]);  
    context.write(newKey, NullWritable.get());  
    System.out.println(newKey);  
    }  
    }  
    public static class Reduce extends Reducer<Text, NullWritable, Text, NullWritable>{  
    public void reduce(Text key,Iterable<NullWritable> values,Context context) throws IOException, InterruptedException{  
        context.write(key,NullWritable.get());  
        }  
        }  
        public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException{  
        Configuration conf=new Configuration();  
        System.out.println("start");  
        Job job =new Job(conf,"filter");  
        job.setJarByClass(Filter.class);  
        job.setMapperClass(Map.class);  
        job.setReducerClass(Reduce.class);  
        job.setOutputKeyClass(Text.class);  
        job.setOutputValueClass(NullWritable.class);  
        job.setInputFormatClass(TextInputFormat.class);  
        job.setOutputFormatClass(TextOutputFormat.class);  
        Path in=new Path("hdfs://localhost:9000/mymapreduce2/in/buyer_favorite1");  
        Path out=new Path("hdfs://localhost:9000/mymapreduce2/out");  
        FileInputFormat.addInputPath(job,in);  
        FileOutputFormat.setOutputPath(job,out);  
        System.exit(job.waitForCompletion(true) ? 0 : 1);  
        }  
        }  
