# Hadoop体系
![Hadoop](https://github.com/HDZ12/Big-Data-System/assets/99587726/73ee3637-bc97-4b5a-b231-2213a1dc4d11)
# 1.1简介

### 1.1.1Hadoop是什么

1. Hadoop:是一个由Apache基金会所开发的分布式系统基础架构。
2. 主要解决，海量数据的存储和海量数据的分析计算问题。
3. 广义上来说，Hadoop通常是指一个更广泛的概念—Hadoop生态圈。

![b4a4f773236547f3bd835eef99f05f0c](https://github.com/HDZ12/Big-Data-System/assets/99587726/5af5107f-0328-4923-bd2c-737d21de284f)


## 1.1.2Hadoop简介

- Hadoop是基于Java语言开发的，具有很好的跨平台特性，并且可以部署在==廉价的计算机集群中==.
- Hadoop的核心是分布式文件系统HDFS（Hadoop Distributed File System）和分布式并行编程框架MapReduce.
- Hadoop被公认为行业大数据标准开源软件，在分布式环境下提供了海量数据的处理能力.
- 几乎所有主流厂商都围绕Hadoop提供开发工具、开源软件、商业化工具和技术服务，如谷歌、雅虎、微软、思科、淘宝等，都支持Hadoop.

## 1.1.3Hadoop优势

- 高可靠性：Hadoop底层护多个数据副本，所以即使Hadoop某个计算元
素或存储出现故障，也不会导致数据的丢失。

- 高扩展性：在集群间分配任务数据，可方便的扩展数以千计的节点。
- 高效性：在Mapreduce的思想下，Hadoop是并行工作的，以加快任务处理速度。
- 高容错性：自动将失败的任务重新分配
- 成本低
- 运行在Linux平台上
- 支持多种编程语言

## 1.1.4Hadoop的组成
![c039fdca03c3bd1d9f2698b89cb21194](https://github.com/HDZ12/Big-Data-System/assets/99587726/1ba2f5f9-dcfa-4ded-aeeb-d6ebd73e2b0f)\
![41eaf044529bc0b9c479b016906a2a6f](https://github.com/HDZ12/Big-Data-System/assets/99587726/8dbc67ef-1fd6-4365-8c2d-9e793425893e)
### Hadoop生态系统
![935ca57d1057b9a15b8220d78ce15a45](https://github.com/HDZ12/Big-Data-System/assets/99587726/c1bdccb7-3228-4661-87b0-4ab1c3ba1cd8)
![19d3020d8ee4febc31dc96ecb4f481c2](https://github.com/HDZ12/Big-Data-System/assets/99587726/0c57ad4d-9f27-4d4e-80fa-afcb89ce8369)
### 基于Hadoop的大数据处理框架
![b8a847e8dbcfd5925229bbc4f4eb85cc](https://github.com/HDZ12/Big-Data-System/assets/99587726/e4557fe4-4941-48b9-8c8d-9912eb516330)
- 平台管理层：确保整个数据处理平台平稳安全运行的保
障，包括配置管理、运行监控、故障管理、性能优化、
安全管理等在内的功能。
- 数据分析层：提供一些高级的分析工具给数据分析人员，
以提高他们的生产效率
- 编程模型层：为大规模数据处理提供一个抽象的并行计
算编程模型，以及为此模型提供可实施的编程环境和运
行环境。
- 数据存储层：提供分布式、可扩展的大量数据表的存储
和管理能力，强调的是在较低成本的条件下实现海量数
据表的管理能力。
- 文件存储层：利用分布式文件系统技术，将底层数量众
多且分布在不同位置的通过网络连接的各种存储设备组
织在一起，通过统一的接口向上层应用提供对象级文件
访问服务能力。
- 数据集成层：系统需要处理的数据来源，包括私有的应
用数据、数据库中的数据、被分析系统运行产生的日志
数据等，这些数据具有结构多样、类型多变的特点。

# 1.2.0Haddop集群的部署

- Hadoop框架中最核心的设计是为海量数据提供存储的HDFS和对数据进行计算的MapReduce。
- MapReduce的作业主要包括：==（1）从磁盘或从网络读取数据，即IO密集工作；（2）计算数据，即CPU密集工作==
- Hadoop集群的整体性能取决于CPU、内存、网络以及存储之间的性能平衡。因此运营团队在选择机器配置时要针对不同的工作节点选择合适硬件类型.
- 一个基本的Hadoop集群中的节点主要有:
  
        1,NameNode:负责协调集群中的数据存储
  
        2,DataNode:存储被拆分的数据块
  
        3,JobTracker:协调数据计算任务
  
        4,TaskTracker:负责执行JobTracker指派的任务
  
        5,SecondaryNameNode:帮助NameNode收集文件系统运行的状态信息

## 1.2.1集群硬件配置

在集群中，大部分的机器设备是作为Datanode和TaskTracker工作的Datanode/TaskTracker的硬件规格可以采用以下方案：

- 4个磁盘驱动器（单盘1-2T），支持JBOD(Just a Bunch Of Disks，磁盘簇)
- 2个4核CPU,至少2-2.5GHz
- 16-24GB内存
- 千兆以太网

NameNode提供整个HDFS文件系统的NameSpace(命名空间)管理、块管理等所有服务，因此需要更多的RAM，与集群中的数据块数量相对应，并且需要优化RAM的内存通道带宽，采用双通道或三通道以上内存。硬件规格可以采用以下方案：

- 8-12个磁盘驱动器（单盘1-2T）
- 2个4核/8核CPU
- 16-72GB内存
- 千兆/万兆以太网

SecondaryNameNode在小型集群中可以和NameNode共用一台机器，较大的群集可以采用与NameNode相同的硬件

## 1.2.2Hadoop集群规模

- Hadoop集群规模可大可小，初始时，可以从一个较小规模的集群开始，比如包含10个节点，然后，规模随着存储器和计算需求的扩大而扩大。
- 如果数据每周增大1TB，并且有三个HDFS副本，然后每周需要一个额外的3TB作为原始数据存储。要允许一些中间文件和日志（假定30%）的空间，由此，可以算出每周大约需要增加一台新机器。存储两年数据的集群，大约需要100台机器。
- 对于一个小的集群，名称节点（NameNode）和JobTracker运行在单个节点上，通常是可以接受的。但是，随着集群和存储在HDFS中的文件数量的增加，名称节点需要更多的主存，这时，名称节点和JobTracker就需要运行在不同的节点上。
- 第二名称节点（SecondaryNameNode）和名称节点可以运行在相同的机器上，但是，由于第二名称节点和名称节点几乎具有相同的主存需求，因此，二者最好运行在不同节点上。

## 1.2.3集群网络拓扑

- 普通的Hadoop集群结构由一个两阶网络构成。
- 每个机架（Rack）有30-40个服务器，配置一个1GB的交换机，并向上传输到一个核心交换机或者路由器（1GB或以上）。
- 在相同的机架中的节点间的带宽的总和，要大于不同机架间的节点间的带宽总和。
![83531e9d22fd9552a76e26200be16aae](https://github.com/HDZ12/Big-Data-System/assets/99587726/61af3658-1ad6-41f0-bf4a-7a80d80a4896)
# Hadoop相关资料
- [尚硅谷Hadoop笔记](https://github.com/HDZ12/Big-Data-System/blob/main/Hadoop/book/02_%E5%B0%9A%E7%A1%85%E8%B0%B7%E5%A4%A7%E6%95%B0%E6%8D%AE%E6%8A%80%E6%9C%AF%E4%B9%8BHadoop%EF%BC%88%E5%85%A5%E9%97%A8%EF%BC%89V3.3_20231029185952.pdf)
- [Hadoop开发者入门专刊](https://github.com/HDZ12/Big-Data-System/blob/main/Hadoop/book/Hadoop%E5%BC%80%E5%8F%91%E8%80%85%E5%85%A5%E9%97%A8%E4%B8%93%E5%88%8A.pdf)
- [Hadoop权威指南](https://github.com/HDZ12/Big-Data-System/blob/main/Hadoop/book/Hadoop%E6%9D%83%E5%A8%81%E6%8C%87%E5%8D%97(%E7%AC%AC2%E7%89%88).pdf)
- [维基百科](https://en.wikipedia.org/wiki/Apache_Hadoop)
- [Apache Hadoop](https://hadoop.apache.org/)

