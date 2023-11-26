# Hbase简介

HBase是一个高可靠、高性能、面向列、可伸缩的分布式数据库，是谷歌BigTable的开源实现，主要用来存储非结构化和半结构化的松散数据。HBase的目标是处理非常庞大的表，可以通过水平扩展的方式，利用廉价计算机集群处理由超过10亿行数据和数百万列元素组成的数据表 

==关系数据库已经流行很多年，并且Hadoop已经有了HDFS和MapReduce，为什么需要HBase?==

- Hadoop可以很好地解决大规模数据的离线批量处理问题，但是，受限于Hadoop MapReduce编程框架的高延迟数据处理机制，使得Hadoop无法满足大规模数据实时处理应用的需求
- HDFS面向批量访问模式，不是随机访问模式
- 传统的通用关系型数据库无法应对在数据规模剧增时导致的系统扩展性和性能问题（分库分表也不能很好解决）
- 传统关系数据库在数据结构变化时一般需要停机维护；空列浪费存储空间
- 因此，业界出现了一类面向半结构化数据存储和处理的高可扩展、低写入/查询延迟的系统，例如，键值数据库、文档数据库和列族数据库（如BigTable和HBase等）
- HBase已经成功应用于互联网服务领域和传统行业的众多在线式数据分析处理系统中

# HBase与传统关系数据库的对比分析

HBase与传统的关系数据库的区别主要体现在以下几个方面：

- 数据类型：关系数据库采用关系模型，具有丰富的数据类型和存储方式，HBase则采用了更加简单的数据模型，它把数据存储为未经解释的字符串
- 数据操作：关系数据库中包含了丰富的操作，其中会涉及复杂的多表连接。HBase操作则不存在复杂的表与表之间的关系，只有简单的插入、查询、删除、清空等，因为HBase在设计上就避免了复杂的表和表之间的关系、
- 存储模式：关系数据库是基于行模式存储的。HBase是基于列存储的，每个列族都由几个文件保存，不同列族的文件是分离的
- 数据索引：关系数据库通常可以针对不同列构建复杂的多个索引，以提高数据访问性能。HBase只有一个索引——行键，通过巧妙的设计，HBase中的所有访问方法，或者通过行键访问，或者通过行键扫描，从而使得整个系统不会慢下来
- 数据维护：在关系数据库中，更新操作会用最新的当前值去替换记录中原来的旧值，旧值被覆盖后就不会存在。而在HBase中执行更新操作时，并不会删除数据旧的版本，而是生成一个新的版本，旧有的版本仍然保留
- 可伸缩性：关系数据库很难实现横向扩展，纵向扩展的空间也比较有限。相反，HBase和BigTable这些分布式数据库就是为了实现灵活的水平扩展而开发的，能够轻易地通过在集群中增加或者减少硬件数量来实现性能的伸缩

# HBase数据模型

## 数据模型概述

- HBase是一个稀疏、多维度、排序的映射表，这张表的索引是==行键、列族、列限定符和时间戳==
- 每个值是一个未经解释的字符串，没有数据类型
- 用户在表中存储数据，每一行都有一个可排序的行键和任意多的列
- 表在水平方向由一个或者多个列族组成，一个列族中可以包含任意多个列，同一个列族里面的数据存储在一起
- 列族支持动态扩展，可以很轻松地添加一个列族或列，无需预先定义列的数量以及类型，所有列均以字符串形式存储，用户需要自行进行数据类型转换
- HBase中执行更新操作时，并不会删除数据旧的版本，而是生成一个新的版本，旧有的版本仍然保留（这是和HDFS只允许追加不允许修改的特性相关的）

## 数据模型相关概念

- 表：HBase采用表来组织数据，表由行和列组成，列划分为若干个列族
- 行：每个HBase表都由若干行组成，每个行由行键（row key）来标识。
- 列族：一个HBase表被分组成许多“列族”（Column Family）的集合，它是基本的访问控制单元
- 列限定符：列族里的数据通过列限定符（或列）来定位
- 单元格：在HBase表中，通过行、列族和列限定符确定一个“单元格”（cell），单元格中存储的数据没有数据类型，总被视为字节数组byte
- 时间戳：每个单元格都保存着同一份数据的多个版本，这些版本采用时间戳进行索引
![e94cc2b4dadbe0108e3cf1613a5e004c](https://github.com/HDZ12/Big-Data-System/assets/99587726/cff90fe3-dce9-4a12-b1cd-b61049bf3085)
# HBase的实现原理

## HBase功能组件

HBase的实现包括三个主要的功能组件：

1. 库函数：链接到每个客户端
2. 一个Master主服务器
3. 许多个Region服务器

- 主服务器Master负责管理和维护HBase表的分区信息，维护Region服务器列表，分配Region，负载均衡
- Region服务器负责存储和维护分配给自己的Region，处理来自客户端的读写请求
- 客户端并不是直接从Master主服务器上读取数据，而是在获得Region的存储位置信息后，直接从Region服务器上读取数据
- 客户端并不依赖Master，而是通过Zookeeper来获得Region位置信息，大多数客户端甚至从来不和Master通信，这种设计方式使得Master负载很小

## 表和Region

- Region：对于每个HBase表而言，表中的行是根据行键的字典排序进行维护的，表中包含的行数量可能非常庞大，无法存储在一台机器上，需要分布存储到多台机器上。因此，需要根据行键的值对表中的行进行分区，每个行区间叫“Region”。
- 开始只有一个Region，后来不断分裂
- Region拆分操作非常快，接近瞬间，因为拆分之后的Region读取的仍然是原存储文件，直到“合并”过程把存储文件异步地写到独立的文件之后，才会读取新文件
- 每个Region默认大小是100MB到200MB（2006年以前的硬件配置）
- 每个Region的最佳大小取决于单台服务器的有效处理能力
- 目前每个Region最佳大小建议1GB-2GB（2013年以后的硬件配置）
- 同一个Region不会被分拆到多个Region服务器
- 每个Region服务器存储10-1000个Region

## Region的定位

- 每个Region都有一个RegionID来标识它的唯一性，这样一个Region标识符就可以标识成“表名+开始主键+RegionID”。每个Region标识符唯一标识一个Region。
- 为了定位每个Region的位置，构建Region和服务器之间的映射表，映射表中每行（条目）包含两项内容，分别为Region标识符和服务器标识。这个映射表被称为“元数据表”，又名“.META.表”
- 当HBase表很大时， .META.表也会被分裂成多个Region
- 根数据表，又名-ROOT-表，记录所有元数据的具体位置
- -ROOT-表只有唯一一个Region，名字是在程序中被写死的
- Zookeeper文件记录了-ROOT-表的位置
|层次|名称|作用|
|--|--|--|
|第一层|Zookeeper文件|记录了-ROOT-表的位置信息|
|第二层|-ROOT-表|记录了.META.表的Region位置信息 -ROOT-表只能有一个Region。通过-ROOT-表，就可以访问.META.表中的数据|
|第三层|.META.表|记录了用户数据表的Region位置信息，.META.表可以有多个Region，保存了HBase中所有用户数据表的Region位置信息|
- 为了加快访问速度，.META.表的全部Region都会被保存在内存中
- 假设.META.表的每行（一个映射条目）在内存中大约占用1KB，并且每个Region限制为128MB，那么，上面的三层结构可以保存的用户数据表的Region数目的计算方法是：
  
  （-ROOT-表能够寻址的.META.表的Region个数）×（每个.META.表的 Region可以寻址的用户数据表的Region个数）
  
  一个-ROOT-表最多只能有一个Region，也就是最多只能有128MB，按照每行（一个映射条目）占用1KB内存计算，128MB空间可以容纳128MB/1KB=217行，也就是说，一个-ROOT-表可以寻址217个.META.表的Region。
  
  同理，每个.META.表的 Region可以寻址的用户数据表的Region个数是128MB/1KB=217。
  
  最终，三层结构可以保存的Region数目是(128MB/1KB) × (128MB/1KB) = $2^{34}$个Region

客户端访问数据时的“三级寻址”

- 为了加速寻址，客户端会缓存位置信息，同时，需要解决缓存失效问题
- 寻址过程客户端只需要询问Zookeeper服务器，不需要连接Master服务器
## HBase运行机制
![b19c489ac99ba8d610933501fa7c466b](https://github.com/HDZ12/Big-Data-System/assets/99587726/ce9ebd4a-8910-4e08-b051-c53a7da8547b)
# HBase系统架构
1. 客户端
客户端包含访问HBase的接口，同时在缓存中维护着已经访问过的Region位置信息，用来加快后续数据访问过程
2. Zookeeper服务器
Zookeeper可以帮助选举出一个Master作为集群的总管，并保证在任何时刻总有唯一一个Master在运行，这就避免了Master的“单点失效”问题
3. Zookeeper是一个很好的集群管理工具，被大量用于分布式计算，提供配置维护、域名服务、分布式同步、组服务等
### Master

主服务器Master主要负责表和Region的管理工作，仅仅维护着表和Region的 元数据 信息：

- 管理用户对表的增加、删除、修改、查询等操作
- 实现不同Region服务器之间的负载均衡
- 在Region分裂或合并后，负责重新调整Region的分布
- 对发生故障失效的Region服务器上的Region进行迁移
- 清理过期日志及文件，Master会隔一段时间检查HDFS中Hlog是否过期、Hfile是否已经被删除，并在过期之后将其删除。

### Region服务器

Region服务器是HBase中最核心的模块，负责维护分配给自己的Region，并响应用户的读写请求，由Hlog、BlockCache以及多个Region组成。其中HLog用来保证数据写入的可靠性；Blockcache可以将数据块缓存在内存中以提升数据读取性能；Region是Hbase中数据表的一个数据分片，一个RegionServer上通常会负责多个Region的数据读写。一个Region由多个Store组成，每个Store包含一个memStore和多个StoreFile（以Hfile的形式存储在HDFS中），用户写入数据时会将对应列族数据写入相应的MemStore中，一旦写入数据的内存大小超过阈值，系统会将MemStore中的数据落盘形成Hfile。

## Store工作原理

- Store是Region服务器的核心
- Store的个数取决于列族的个数，多少个列族就有多少个Store
- 多个StoreFile合并成一个
- 单个StoreFile过大时，又触发分裂操作，1个父Region被分裂成两个子Region
  ![ff7592c34dd34c8bb02ff878b6ddd5a8](https://github.com/HDZ12/Big-Data-System/assets/99587726/5ac1abce-1da8-41f0-9960-f1e93f6d8997)
### StoreFile
![a51fbe47cb2d1a792fe563cb6bf49862](https://github.com/HDZ12/Big-Data-System/assets/99587726/3f18b139-c7db-4bff-8737-6dce3148c7a3)
- Trailer: HFile的基本信息、各个部分的偏移，一个固定长度，记录了值和寻址信息。
![edb9105f1eb4af3bfb5a3b3a645304c8](https://github.com/HDZ12/Big-Data-System/assets/99587726/c45132c2-9a98-4fbf-add6-fb059152dd9e)
DataBlocks 段–保存表中的数据

Key-value实例不会跨块拆分，一个block默认是64kb，假设一个key-value为2M，这个键值也将作为一个块被读入。

File Info 段–记录了文件的一些 Meta 信息，例如：AVG_KEY_LEN, AVG_VALUE_LEN, FIRST_KEY, LAST_KEY, COMPARATOR, MAX_SEQ_ID_KEY 等。

==Root Index Block段–HFile为DataBlocks创建了一个数据索引树，Root Index Block标识索引树根节点。==

IndexEntry表示具体的索引对象，每个索引对象由3个字段组成：Blockoffset表示指向DataBlock的偏移量，BlockDataSize表示索引指向DataBlock在磁盘上的大小，Blockkey表示索引指向DataBlock的第一个key。
![e7cd60ba20b90c070b892fc6811d2438](https://github.com/HDZ12/Big-Data-System/assets/99587726/99a6d077-db4d-4d61-9e3e-446640c1eeee)
![aab22c3bd255a9fbbd345936fb9c484a](https://github.com/HDZ12/Big-Data-System/assets/99587726/6df596e8-dc04-43b5-8209-b7754525eec9)
# HLog工作原理

- 分布式环境必须要考虑系统出错。HBase采用HLog保证系统恢复
- HBase系统为每个Region服务器配置了一个HLog文件，它是一种预写式日志（Write Ahead Log）
- 用户更新数据必须首先写入日志后，才能写入MemStore缓存，并且，直到MemStore缓存内容对应的日志已经写入磁盘，该缓存内容才能被刷写到磁盘
- Zookeeper会实时监测每个Region服务器的状态，当某个Region服务器发生故障时，Zookeeper会通知Master
- Master首先会处理该故障Region服务器上面遗留的HLog文件，这个遗留的HLog文件中包含了来自多个Region对象的日志记录
系统会根据每条日志记录所属的Region对象对HLog数据进行拆分，分别放到相应Region对象的目录下，然后，再将失效的Region重新分配到可用的Region服务器中，并把与该Region对象相关的HLog日志记录也发送给相应的Region服务器
- Region服务器领取到分配给自己的Region对象以及与之相关的HLog日志记录以后，会重新做一遍日志记录中的各种操作，把日志记录中的数据写入到MemStore缓存中，然后，刷新到磁盘的StoreFile文件中，完成数据恢复
- ==共用日志优点：提高对表的写操作性能；缺点：恢复时需要分拆日志==

# BlockCache工作原理

- 为了提升读取性能，Hbase实现了一种读缓存结构——BlockCache。
- 客户端读取某个Block，首先会检查该Block是否存在于BlockCache，如果存在就直接加载出来，如果不存在则去Hfile文件中读取，加载之后放到BlockCache中，后续同一请求或者临近数据查找请求可以直接从内存中获取，以避免昂贵的IO操作。
- 一个RegionServer只有一个BlockCache，在RegionServer启动时完成BlockCache初始化工作。

# Hbase数据读写流程

- 需要说明的是，Hbase服务端没有提供update、delete借口，Hbase中对数据的更新、删除操作在服务器端也认为是写入操作，更新操作会写入一个新版本数据，删除操作会写入一条标记为deleted的KV数据。

写入流程分三步：

1. 客户端处理阶段：客户端将用户写入请求进行预处理，并根据集群元数据定位写入数据所在的RegionServer，将请求发送给对应的RegionServer。
2. Region写入阶段：RegionServer接收到写入请求之后将数据解析出来，首先写入Hlog，再写入对应Region列族的MemStore。
3. MemStore Flush阶段：当Region中MemStore容量超过一定阈值，系统会异步执行flush操作，将内存中的数据写入文件，形成Hfile。
注意：用户写入请求在完成Region MemStore的写入之后就会返回成功。MemStore Flush是一个异步执行的过程。

客户端处理阶段：

1. 客户端根据写入的表以及rowkey在元数
据缓存中查找，如果能够查找出该rowkey所在的RegionServer以及Region，就可以直接发送给写入请求（携带Region信息）到目标RegionServer。
2. .如果客户端缓存中没有查到对应的rowkey信息，需要首先到Zookeeper上查找Root表的地址信息，然后从root表中查找Hbase元数据表所在的RegionServer，向元数据表所在的RegionServer发送请求，在元数据表中查询rowkey所在的RegionServer以及Region信息，并返回给客户端，客户端将结果缓存到本地，以备下次使用。
3. 客户端将写入请求发送给目标RegionServer，RegionServer接收到请求之后会解析出具体的Region信息，查到对应的Region对象，并将数据写入目标Region的Memstore中。

- region写入阶段：服务器端RegionServer收到客户端写入请求后，首先会执行各种检查操作，比如检查Region是否只读、MemStore大小是否超过blockingMemostoresize等。检查完成之后，执行一系列核心操作

1. Acquire locks：Hbase中使用行锁保证对同一行数据的更新都是互斥操作，用以保证更新的原子性，要么成功，要么失败。
2. Update LATEST_TIMESTAMP timestamps：更新所有待写入KeyValue的时间戳为当前系统时间。
3. Build WAL edit：HBase使用WAL（Write Ahead Log）机制保证数据可靠性，即首先写入日志再写缓存，即使发生宕机，也可以通过恢复Hlog还原出原始数据。该步骤在内存中构建WALEdit对象，为了保证Region级别失误的写入原子性，一次写入操作中所有的KeyValue会构建成一条WALEdit记录
4. Append WALEdit to WAL：将步骤3中构造在内存中的WALEdit记录顺序写入Hlog中。
5. Write back to MemStore：将数据写入MemStore。
6. Release row locks：释放行锁。
7. Sync Wal：Hlog真正sync到HDFS，在释放行锁之后执行sync操作是为了尽量减少持锁时间，提升写性能。如果Sync失败，执行回滚操作将MemStore中已经写入的数据移除
8. 结束写事务：此时更新操作对其他读写请求可见，更新生效

缓存的刷新：

- 系统会周期性地把MemStore缓存里的内容刷写到磁盘的StoreFile文件中，清空缓存，并在Hlog里面写入一个标记
- 每次刷写都生成一个新的StoreFile文件，因此，每个Store包含多个StoreFile文件
- 每个Region服务器都有一个或多个自己的HLog 文件，每次启动都检查该文件，确认最近一次执行缓存刷新操作之后是否发生新的写入操作；如果发现更新，则先写入MemStore，再刷写到StoreFile，最后删除旧的Hlog文件，开始为用户提供服务

缓存刷新触发条件：

1. MemStore级别限制：当Region中任意一个MemStore的大小达到了上限（hbase.hregion.memstore.flush.size），会触发Memstore的刷新。
2. Region级别限制：当Region中所有MemStore的大小总和达到了上限会触发MemStore刷新。
3. RegionServer级别限制：当RegionServer中MemStore的大小综合超过低水位阈值，RegionServer强制执行flush，先flushMemStore最大的Region，再flush次大的，依次执行。如果此时写入吞吐量依然很高，导致总MemStore大小超过高水位阈值，RegionServer会阻塞更新并强制执行flush，直至总MemStore大小下降到低水位阈值。
4. 当一个RegionServer中Hlog数量达到上限（通过参数hbase.regionserver.maxlogs配置）时，系统会选取最早的Hlog对应的一个或多个Region进行flush。
5. Hbase定期刷新MemStore：默认周期为1小时，确保MemStore不会长时间没有持久化。为避免所有的MemStore在同一时间都进行flush而导致的问题，定期的flush操作有一定时间的随机延时。
6. 手动执行flush：用户可以通过Shell命令flush ‘tablename’或者flush ‘regionname’分别对一个表或者一个Region进行flush。
   
   用到Memstore最主要的原因是：存储在HDFS上的数据需要按照row key 排序。而HDFS本身被设计为顺序读写(sequential reads/writes)，不允许修改。这样的话，HBase就不能够高效的写数据，因为要写入到HBase的数据不会被排序，这也就意味着没有为将来的检索优化。为了解决这个问题，HBase将最近接收到的数据缓存在内存中(in Memstore)，在持久化到HDFS之前完成排序，然后再快速的顺序写入HDFS。

用户读数据过程：

- Client-server读取交互逻辑；
  
  Client-server读取交互逻辑同写过程一样，获取数据所在的目标Regionserver和Region。
  
  Hbase数据读取可以分为get和scan两类，get请求是指给定rowkey查找一行记录，scan指给定startkey和stopkey查找多行满足条件的记录。所有读操作都可以认为是一次scan操作。一次大规模的scan操作很有可能就是一次全表扫描，扫描结果非常大，因此Hbase根据设置条件将一次scan操作分为多个RPC请求，每次请求只返回规定数量的结果。
- Server端Scan框架体系；
  
  Server端Scan框架体系：
一次scan可能会同时扫描一张表的多个Region，对于这种扫描，客户端会根据.Meta元数据将扫描的起始区间[startkey,endkey)进行切分，切分成多个相互独立的查询子区间，每个子区间对应一个Region。客户端将每个子区间请求分别发送给对应的Region进行处理。
  
  RegionServer接收到请求之后首先构建scan框架体系，Scanner的核心体系包括三层Scanner：RegionScanner，StoreScanner，MemstoreScanner和StoreFileScanner。三者是层级关系：
一个RegionScanner由多个StoreScanner构成。一张表由多少个列族组成，就有多少个StoreScanner，每个StoreScanner负责对应Store的数据查找。
一个StoreScanner由MemstoreScanner和StoreFileScanner构成。每个Store的数据由Memstore和StoreFile文件组成。相对应的， StoreScanner会为当前该Store中每个Hfile构造一个StoreFileScanner，为Memstore构造一个MemstoreScanner。
  
  **RegionScanner和StoreScanner并不负责实际的数据查找操作，它们更多地承担组织调度任务，负责KeyValue查找操作的是MemstoreScanner和StoreFileScanner。
  ![278b2c910b9bb2b3faf7c68ff5d98607](https://github.com/HDZ12/Big-Data-System/assets/99587726/469e90fa-40f0-4f1b-8f45-2fb76c579ebf)
- 过滤淘汰不符合查询条件的Hfile：
并不是每个Hfile都包含用户想要查找的KeyValue，可以通过一些查询条件过滤掉很多肯定不存在待查找KeyValue的Hfile。主要过滤手段有：根据KeyRange过滤，根据TimeRange过滤。
1）根据KeyRange过滤：如果待检索row范围[startrow，stoprow]与文件起始key范围[firstkey，lastkey]没有交集，就可以过滤掉该StoreFile。
2）根据TimeRange过滤：HFile中元数据有关于该HFile的TimeRange属性，如果待检测的TimeRange与该文件时间范围没有交集，就可以过滤掉该StoreFile；另外，如果该文件所有数据已过期，也可以过滤淘汰。
- 从Hfile中读取待查找Key
![71eb20e7c7cccfa710266bd70d9b1521](https://github.com/HDZ12/Big-Data-System/assets/99587726/c7ddcf5c-b973-444a-8329-d756e57e9e10)
1. 根据Hfile索引树定位目标Block
RegionServer打开Hfile时会将Trailer部分和Load-on-open部分加载到内存，Load-on-open部分有一个非常重要的Block——Root Index Block，即索引树的根节点。
2. BlockCache中检索目标Block
Block缓存到Block Cache之后会构建一个Map，Map的key是BlockKey（包含Hfile名称及Block在Hfile中的偏移量），value是Block在内存中的地址。根据BlockKey获取Block在内存中的地址后，加载出该Block对象，如果在BlockCache中没有找到待查Block，就需要在HDFS中查找。
3. HDFS中检索目标Block
Hbase为Hfile创建一个从HDFS读取数据的输入流——FSDataInputStream。
首先与NameNode通信，NameNode会做两件事：
找到属于这个Hfile的所有HDFSBlock列表，确认待查找数据在哪个HDFSBlock上。
确认该HDFSBlock在哪些DataNode上，选择最优的Datanode返回给Hbase。
4. HDFS中检索目标Block
Hbase与DataNode通信，首先找到HDFSBlock，seek到指定偏移量，从磁盘中读取HBaseBlock大小（64k）的数据返回。
5. 从Block中读取待查找KeyValue
遍历扫描该Block，并对符合KeyValue条件的数据进行条件过滤：
检查该KeyValue的KeyType是否是Deleted/ DeletedColumn/ DeletedFamily.
检查该KeyValue的TimeStamp是否在用户设定的TimeStamp Range范围。
检查该KeyValue是否满足用户设置的各种filter过滤器。

# Region拆分过程
![c6a258c52f2043d675d19c1934754fd3](https://github.com/HDZ12/Big-Data-System/assets/99587726/bf670dc8-8542-499a-a4e9-18c3717ffe87)
一个Region中最大StoreFile大小大于设置阈值就会触发切分

1. 选取切分点（splitpoint）：整个region中最大store中的最大文件中最中心的一个block的首个rowkey。如果定位到的rowkey是整个文件的首个rowkey或者最后一个rowkey的话，就认为没有切分点。（==什么情况下会出现没有切分点的场景呢？最常见的就是一个文件只有一个block，执行split的时候就会发现无法切分==。）
2. prepare阶段：在内存中初始化两个子region，具体是生成两个HRegionInfo对象，代表分裂后产生的两个dautghter region，包含tableName、regionName、startkey、endkey等信息。同时会生成一个transaction journal，这个对象用来记录切分的进展，为rollback阶段做准备。
3. execute阶段：切分的核心操作
   
   （1）在zookeeper的/hbase/region-in-transition/region-name下创建一个znode，并设为SPLITTING状态。
   
   （2）Master通过对region-in-transition的watch监测到刚刚创建的Region状态的改变，修改内存中Region的状态，防止master对其进行move等操作。
   
   （3）在hdfs上为这个父region的split过程创建临时工作目录/hbase/data/namespace/tableName/region-name/.split，保存split后的daughter region信息
   
   （4）关闭parent region：parent region关闭数据写入并触发flush操作，将写入region的数据全部持久化到磁盘。此后短时间内客户端落在父region上的写请求都会抛出异常NotServingRegionException。
   
   （5）RegionServer在.split目录下为daughter regionA和B创建目录和相关的数据结构。然后RegionServer分割store文件，这种分割是指，为daughter region的每个storefile文件创建Reference文件。这些Reference文件将指向父region中的文件。文件内容主要有两部分构成：其一是切分点splitkey，其二是一个boolean类型的变量（true或者false），true表示该reference文件引用的是父文件的上半部分（top），而false表示引用的是下半部分 （bottom）。
   
   （6）RegionServer在HDFS中创建实际的daughter region目录，并移动每个daughter region的Reference文件。
   
   （7）RegionServer向.META.表发送Put请求，并在.META.中将父region改为下线状态，添加子region的信息。下线后父Region在 meta列表中的信息不会马上删除，而是将split列和offline列标注为true。
   
   （8）RegionServer打开子region，并行地接受写请求。
   
   （9）RegionServer将子region A和B的相关信息写入.META.。此后，Client便可以扫描到新的region，并且可以向其发送请求。Client会在本地缓存.META.的条目，但当它们向RegionServer或.META.发送请求时，这些缓存便无效了，他们就重新学习.META.中新region的信息。
   
   （10）RegionServer将zookeeper中的znode（ /hbase/region-in-transition/region-name）更改为SPLIT状态，以便Master可以监测到
   
