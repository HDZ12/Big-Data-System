![Hadoop](https://github.com/HDZ12/Big-Data-System/assets/99587726/9c80843e-1bdc-4354-b7dc-b429cbf692b3)# Big-Data-System
个人的大数据体系
# [01.Haddop](https://github.com/HDZ12/Big-Data-System/blob/main/Hadoop/READEME.md#11%E7%AE%80%E4%BB%8B)
![Hadoop](https://github.com/HDZ12/Big-Data-System/assets/99587726/d0e93856-1ea1-4bf5-9c48-0f3c430e1d26)

Hadoop是一个由Apache基金会所开发的分布式系统基础架构。用户可以在不了解分布式底层细节的情况下，开发分布式程序。充分利用集群的威力进行高速运算和存储。

Hadoop实现了一个分布式文件系统，即Hadoop Distributed File System (HDFS)，它可以将用户的大规模数据集分布存储在集群的各个节点上，形成一个高容错性的并行系统。

Hadoop的核心设计就是：MapReduce，它是一个编程模型，用于处理和生成大数据集。用户可以编写MapReduce程序，这些程序会在被分割成一系列小数据块的数据集上并行运行。

Hadoop已经成为大数据和云计算领域不可或缺的一部分，它解决了海量数据存储和海量数据分析的问题，是当前处理大数据的重要工具之一。Hadoop的应用场景非常广泛，从互联网搜索和推荐系统，到生物信息学研究，再到数据仓库和报告系统，Hadoop都发挥着重要的作用。

总的来说，Hadoop是一个强大的开源平台，它能够存储和处理大数据，帮助企业和科研机构从海量数据中获取有价值的信息。
# [HDFS](https://github.com/HDZ12/Big-Data-System/blob/main/HDFS/HDFS.md)
Hadoop分布式文件系统（HDFS）是一个分布式文件系统，它被设计成适合运行在通用硬件上1。HDFS是一个高度容错性的系统，适合部署在廉价的机器上1。HDFS能提供高吞吐量的数据访问，非常适合大规模数据集上的应用1。HDFS放宽了一部分POSIX约束，来实现流式读取文件系统数据的目的1。HDFS在最开始是作为Apache Nutch搜索引擎项目的基础架构而开发的1。

HDFS的文件分布在集群机器上，同时提供副本进行容错及可靠性保证2。HDFS是以流式数据访问模式存储超大文件，将数据分块存储到一个商业硬件集群内的不同机器上3。HDFS的访问模式是：一次写入，多次读取，更加关注的是读取整个数据集的整体时间3。

HDFS采用了主从（Master/Slave）结构模型，一个HDFS集群是由一个NameNode和若干个DataNode组成的1。其中NameNode作为主服务器，管理文件系统的命名空间和客户端对文件的访问操作；集群中的DataNode管理存储的数据1。

HDFS是一个主从结构，一个HDFS集群是由一个名字节点，它是一个管理文件命名空间和调节客户端访问文件的主服务器，当然还有一些数据节点，通常是一个节点一个机器，它来管理对应节点的存储4。所有文件信息都保存在名字节点这里，名字节点毁坏后无法重建文件3。因此，必须高度重视namenode的容错性3。

总的来说，HDFS是一个强大的工具，用于处理大规模数据集，并且具有高度的容错性和可扩展性。132
# [Hbase](https://github.com/HDZ12/Big-Data-System/blob/main/Hbase/READEME.md)




