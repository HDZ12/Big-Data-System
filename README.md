# Big-Data-System
个人的大数据体系
1. [Haddop](https://github.com/HDZ12/Big-Data-System/blob/main/Hadoop/READEME.md#11%E7%AE%80%E4%BB%8B)
2. [HDFS](https://github.com/HDZ12/Big-Data-System/blob/main/HDFS/HDFS.md)
   1. [CopyFromLocalFile.java](https://github.com/HDZ12/Big-Data-System/blob/main/HDFS/code/CopyFromLocalFile.java)
   2. [CopyToLocalFiles.java](https://github.com/HDZ12/Big-Data-System/blob/main/HDFS/code/CopyToLocalFiles.java)
   3. [ListFiles.java](https://github.com/HDZ12/Big-Data-System/blob/main/HDFS/code/ListFiles.java)
   4. [IteratorListFiles.java](https://github.com/HDZ12/Big-Data-System/blob/main/HDFS/code/IteratorListFiles.java)
3. [Hbase](https://github.com/HDZ12/Big-Data-System/blob/main/Hbase/READEME.md)
   1. [HBase.java](https://github.com/HDZ12/Big-Data-System/blob/main/Hbase/code/%E8%BF%87%E6%BB%A4%E5%99%A8/HBase.java  )
   2. [Prffilter.java](https://github.com/HDZ12/Big-Data-System/blob/main/Hbase/code/%E8%BF%87%E6%BB%A4%E5%99%A8/Prffilter.java)
4. [NO SQL](https://github.com/HDZ12/Big-Data-System/blob/main/No%20SQL/READEME.md)
5. [Mapreduce](https://github.com/HDZ12/Big-Data-System/blob/main/Mapreduce/READEME.md)
   1. [WordCount.java](https://github.com/HDZ12/Big-Data-System/blob/main/Mapreduce/code/WordCount.java)
   2. [MyAverage.java](https://github.com/HDZ12/Big-Data-System/blob/main/Mapreduce/code/MyAverage.java)
   3. [Filter.java](https://github.com/HDZ12/Big-Data-System/blob/main/Mapreduce/code/Filter.java)
# [Haddop](https://github.com/HDZ12/Big-Data-System/blob/main/Hadoop/READEME.md#11%E7%AE%80%E4%BB%8B)
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
![Hbase](https://github.com/HDZ12/Big-Data-System/assets/99587726/01161b9e-daef-4afe-be1a-8f49ef8d6a18)

HBase是一个开源的、非关系型、分布式数据库，它是Apache软件基金会的一个项目。HBase的设计目标是为了在普通的硬件集群上提供大规模结构化存储服务。它的主要特性包括：
- 线性和模块化的可扩展性：HBase可以通过添加更多的机器来扩展，以便处理更多的数据。
- 严格一致性的读写：HBase提供了严格的读写一致性，这意味着在任何给定的时间，无论数据被读取还是写入，都会返回最新的值。
- 自动和可配置的分片：HBase表会自动分割成区域，并在集群中分布。用户也可以配置分片的方式。
- 自动故障恢复：HBase可以自动处理服务器故障，无需人工干预。
HBase是Google BigTable的开源实现，它运行在Hadoop HDFS文件系统之上，并且支持Hadoop/MapReduce计算框架。HBase的主要应用场景包括大规模数据的实时查询和处理。

HBase的数据模型是一个由行和列组成的稀疏、分布式、持久化的多维排序映射。这个模型非常适合于非常大的表（数十亿行，数百万列）的存储和随机实时访问。

HBase的主要组件包括HMaster、RegionServer和ZooKeeper。HMaster负责协调和管理RegionServer，而RegionServer则负责处理对数据的读写请求。ZooKeeper是一个分布式协调服务，它用于维护和监控HBase集群的状态。

总的来说，HBase是一个强大的工具，能够处理PB级别的大数据，并提供快速、随机的读写能力。它是大数据处理和分析的重要工具之一。
# [NO SQL](https://github.com/HDZ12/Big-Data-System/blob/main/No%20SQL/READEME.md)
NoSQL（Not Only SQL）是一类用于存储和检索大量非结构化或半结构化数据的数据库管理系统的术语。与传统的关系型数据库管理系统（RDBMS）不同，NoSQL数据库的设计目标是解决大规模数据处理和分布式计算环境下的性能和可扩展性问题。以下是NoSQL的一些主要特点和分类：

1. **灵活的数据模型：** NoSQL数据库通常采用灵活的数据模型，允许存储半结构化或非结构化数据，与传统的关系型数据库相比更具弹性。

2. **分布式架构：** NoSQL数据库被设计为能够轻松扩展到多台机器上，以支持大规模数据存储和处理。这使得它们在处理大量数据和高并发请求时表现更为优越。

3. **高性能：** NoSQL数据库通常优化了读写性能，并且在处理大量数据时表现出色。这得益于其灵活的数据模型和分布式架构。

4. **CAP原理：** NoSQL数据库的设计通常遵循CAP原理，即一致性（Consistency）、可用性（Availability）和分区容错性（Partition Tolerance）。在CAP原理中，任何分布式系统只能同时满足其中的两个。

5. **多样的类型：** NoSQL数据库被分为多个类型，包括文档型数据库（如MongoDB）、键值存储（如Redis）、列族存储（如Apache Cassandra）、图形数据库（如Neo4j）等。

主要的NoSQL数据库有：

- **文档型数据库：** MongoDB、CouchDB等。这些数据库以文档的形式存储数据，文档可以是JSON、XML等格式。

- **键值存储：** Redis、DynamoDB等。这类数据库使用键值对存储数据，适用于快速的数据检索。

- **列族存储：** Apache Cassandra、HBase等。这些数据库按列而不是行来存储数据，适用于需要横向扩展的场景。

- **图形数据库：** Neo4j、ArangoDB等。这些数据库专注于处理图形数据结构，适用于关系较为复杂的数据。

总体而言，NoSQL数据库在处理大规模和复杂数据时提供了更灵活、可扩展和高性能的解决方案，但也需要根据具体的应用场景来选择合适的类型和实现。
# [Mapreduce](https://github.com/HDZ12/Big-Data-System/blob/main/Mapreduce/READEME.md)
MapReduce 是一种分布式计算编程模型，旨在处理大规模数据集。它最初由Google提出，并在Apache Hadoop等开源项目中得到广泛应用。以下是关于 MapReduce 的简介：

MapReduce简介：

MapReduce 是一种用于大规模数据处理的编程模型和处理框架。它允许开发人员编写能够在分布式计算环境中运行的并行化程序，用于处理大规模数据集。MapReduce 的设计理念是将计算任务分解为两个主要阶段：Map 阶段和 Reduce 阶段。

Map阶段： 在这个阶段，原始数据集被划分成若干个小块，每个小块通过一个称为“Mapper”的函数进行处理。Mapper 函数生成一系列键值对（key-value pairs），其中键用于分组数据，值用于存储中间结果。Map 阶段的目标是将原始数据集分解成更小的部分，以便并行处理。

Shuffle and Sort阶段： 在 Map 阶段之后，MapReduce 框架会对生成的键值对进行排序和分组操作。这个过程称为 Shuffle and Sort 阶段，其目的是将所有具有相同键的数据汇总在一起，以便传递给 Reduce 阶段的 Reduce 函数。

Reduce阶段： 在 Reduce 阶段，数据按照键值对的键被分组，然后传递给称为“Reducer”的函数进行处理。Reducer 函数处理这些组并生成最终的输出结果。Reduce 阶段的目标是将中间结果整合成最终的结果。

MapReduce 具有很高的可扩展性，可以在大规模的分布式计算集群上运行。它被广泛应用于处理海量数据，例如在搜索引擎索引构建、日志分析、机器学习等领域。

Apache Hadoop 是一个开源的 MapReduce 实现，它提供了一个分布式存储系统（Hadoop Distributed File System，HDFS）以及一个用于执行 MapReduce 任务的框架。 MapReduce 模型的成功启发了许多其他分布式计算框架的发展，如 Apache Spark、Apache Flink 等。






