# 分布式并行编程
![1701778506383](https://github.com/HDZ12/Big-Data-System/assets/99587726/7765f3a0-083f-47db-b75c-1a15676058fb)
- MapReduce将复杂的、运行于大规模集群上的并行计算过程高度地抽象到了两个函数：Map和Reduce
- 编程容易，不需要掌握分布式并行编程细节，也可以很容易把自己的程序运行在分布式系统上，完成海量数据的计算
- MapReduce采用“分而治之”策略，一个存储在分布式文件系统中的大规模数据集，会被切分成许多独立的分片（split），这些分片可以被多个Map任务并行处理
- MapReduce设计的一个理念就是“计算向数据靠拢”，而不是“数据向计算靠拢”，因为，移动数据需要大量的网络传输开销
- MapReduce框架采用了Master/Slave架构，包括一个Master和若干个Slave。Master上运行JobTracker，Slave上运行TaskTracker 
- Hadoop框架是用Java实现的，但是，MapReduce应用程序则不一定要用Java来写
# Map和Reduce函数
![1701778672799](https://github.com/HDZ12/Big-Data-System/assets/99587726/cb91cebc-c98d-43a7-96af-3d167a763718)
MapReduce主要有以下4个部分组成：\
1）Client\
用户编写的MapReduce程序通过Client提交到JobTracker端\
用户可通过Client提供的一些接口查看作业运行状态\
2）JobTracker\
运行在主节点的守护进程就是JobTracker\
JobTracker负责资源监控和作业调度\
JobTracker 监控所有TaskTracker与Job的健康状况，一旦发现失败，就将相应的任务转移到其他节点\
JobTracker 会跟踪任务的执行进度、资源使用量等信息，并将这些信息告诉任务调度器（TaskScheduler），而调度器会在资源出现空闲时，选择合适的任务去使用这些资源
![1701778748798](https://github.com/HDZ12/Big-Data-System/assets/99587726/85ce3cc4-db47-4cad-9666-f3536f6dd95b)
3）TaskTracker
运行在各个从节点的守护进程就是TaskTracker\
TaskTracker 会周期性地通过“心跳”将本节点上资源的使用情况和任务的运行进度汇报给JobTracker，同时接收JobTracker 发送过来的命令并执行相应的操作（如启动新任务、杀死任务等）\
TaskTracker 使用“slot”等量划分本节点上的资源量（CPU、内存等）。一个Task 获取到slot 后才有机会运行，而Hadoop调度器的作用就是将各个TaskTracker上的空闲slot分配给Task使用。slot 分为Map slot 和Reduce slot 两种，分别供MapTask 和Reduce Task 使用\
4）Task\
Task 分为Map Task 和Reduce Task 两种，均由TaskTracker 启动 
# MapReduce工作流程
![1701778868630](https://github.com/HDZ12/Big-Data-System/assets/99587726/5554d2ee-6ccc-4223-b213-e5bf7baa4888)
- 不同的Map任务之间不会进行通信
- 不同的Reduce任务之间也不会发生任何信息交换
- 用户不能显式地从一台机器向另一台机器发送消息
- 所有的数据交换都是通过MapReduce框架自身去实现的
![1701778987487](https://github.com/HDZ12/Big-Data-System/assets/99587726/cfe86b99-c860-4fca-8dc8-b00f33050d77)
## 关于split(分片)
HDFS 以固定大小的block 为基本单位存储数据，而对于MapReduce 而言，其处理单位是split。split 是一个逻辑概念，它只包含一些元数据信息，比如数据起始位置、数据长度、数据所在节点等。它的划分方法完全由用户自己决定。
- Map任务的数量
Hadoop为每个split创建一个Map任务，split 的多少决定了Map任务的数目。大多数情况下，理想的分片大小是一个HDFS块
- Reduce任务的数量
最优的Reduce任务个数取决于集群中可用的reduce任务槽(slot)的数目
通常设置比reduce任务槽数目稍微小一些的Reduce任务个数（这样可以预留一些系统资源处理可能发生的错误）
## Shuffle过程
![Pasted image 20231205202519](https://github.com/HDZ12/Big-Data-System/assets/99587726/781fc56b-afe2-49a0-a759-2c78ec5ca46d)
### Map端的Shuffle过程
![1701779184741](https://github.com/HDZ12/Big-Data-System/assets/99587726/e227562c-0e24-48f7-8ddb-5f2c2d9e6c15)
- 每个Map任务分配一个缓存
- MapReduce默认100MB缓存
- 设置溢写比例0.8
- 分区默认采用哈希函数
- 排序是默认的操作
- 排序后可以合并（Combine）
- 合并不能改变最终结果
- 在Map任务全部结束之前进行归并
- 归并得到一个大的文件，放在本地磁盘
- 文件归并时，如果溢写文件数量大于预定值（默认是3）则可以再次启动Combiner，少于3不需要
- JobTracker会一直监测Map任务的执行，并通知Reduce任务来领取数据\
**合并（Combine）和归并（Merge）的区别：\
两个键值对<“a”,1>和<“a”,1>，如果合并，会得到<“a”,2>，如果归并，会得到<“a”,<1,1>>**
### Reduce端的Shuffle过程
- Reduce任务通过RPC向JobTracker询问Map任务是否已经完成，若完成，则领取数据
- Reduce领取数据先放入缓存，来自不同Map机器，先归并，再合并，写入磁盘
- 多个溢写文件归并成一个或多个大文件，文件中的键值对是排序的
- 当数据很少时，不需要溢写到磁盘，直接在缓存中归并，然后输出给Reduce
## Mapreduce细节实现
### Mapreduce生命周期
> ** 步骤1：作业提交与初始化**
> 用户提交作业后，首先由JobClient实例将作业相关信息，比如程序jar包、作业配置文件、分片元信息文件等上传到分布式文件系统（一般为HDFS）上，其中分片原信息文件记录了每个输入分片的逻辑位置信息。然后JobClient通过RPC通知JobTracker。JobTracker收到新作业提交请求后，由作业调度模块对作业进行初始化：为作业创建JobInProgress对象以跟踪作业运行情况，而JobInProgress则会为每个Task创建一个TaskInProgress对象以跟踪每个任务的运行状态，TaskInProgress可能需要管理多个“Task运行尝试”（Task Attempt）。
***
> **步骤2：任务调度与监控**
> TaskTracker周期性地通过Heartbeat向JobTracker汇报本节点的资源使用情况，一旦出现空闲资源，JobTracker会按照一定的策略选择一个合适的任务使用该空闲资源，这由任务调度器完成。任务调度器是一个可插拔的独立模块，且为双层架构，即首先选择作业，然后从该作业中选择任务，其中选择任务时需要重点考虑数据本地性。此外，JobTracker跟踪作业的整个运行过程，并为作业的成功运行提供全方位的保障。首先，当TaskTracker或者Task失败时，转移计算任务；其次，当某个Task执行进度远远落后于同一作业的其他Task时，为之启动一个相同Task，并选取计算快的Task结果作为最终结果
***
> **步骤3：任务运行环境准备**
> 运行环境包括JVM启动和资源隔离，均由TaskTracker实现。TaskTracker为每个Task启动一个独立的JVM以避免不同Task在运行过程中相互影响；同时，TaskTracker使用了操作系统进程实现资源隔离以防止Task滥用资源
***
> **步骤4：任务执行**
> TaskTracker为Task准备好运行环境后，便会启动Task。在运行过程中，每个Task的最新进度首先由Task通过RPC汇报给TaskTracker，再由TaskTracker汇报给JobTracker。
***
> **步骤5：作业完成**
> 待所有Task执行完毕后，整个作业执行成功。
### 作业控制过程
_JobTracker是整个MapReduce计算框架中的主服务器，相当于集群计算的“管理者”，负责整个集群的作业控制和资源管理。作业控制的主要作用有两个：容错和为任务调度提供决策依据。_
**容错**：通过状态监控，JobTracker能够及时发现存在异常或出现故障的TaskTracker、作业或者任务，从而启动相应的容错机制进行处理；\
**任务调度**：JobTracker保存了作业和任务的近似实时运行信息，这些可用于任务调度时进行任务选择的依据。\
**资源管理**：通过一定的策略将各个节点上的计算资源分配给集群中的任务。\
**JobTracker在其内部以“三层多叉树”的方式描述和跟踪每个作业的运行状态，作业被抽象成三层，从上往下依次为：作业监控层、任务监控层、任务执行层。**

- 作业监控层中，每个作业由一个JobInProgress（JIP）对象描述和跟踪其整体运行状态以及每个任务的运行情况，该对象存在于作业的整个运行过程中：它在作业提交时创建，在作业运行完成时销毁；
- 任务监控层中，每个任务有一个TaskInProgress（TIP）对象描述和跟踪其运行状态；
- 任务执行层中，考虑到任务在执行过程中可能会因为软件Bug、硬件故障等原因运行失败，因而每个任务可能尝试执行多次，直到成功或者超过尝试次数而失败。每次尝试运行一次任务，称为“任务运行尝试”，对应的实例称为Task Attempt（TA）。
**任何一个TA运行成功，上层对应的TIP会标注该任务运行成功；当所有的TIP运行成功之后，JIP会标注整个作业运行成功。**\

![image](https://github.com/HDZ12/Big-Data-System/assets/99587726/6b3216a0-df56-4bb3-9574-91ddd5ca06ef)
> **三层多叉树**\
> 为了区分各个作业，JobTracker为每个作业赋予一个唯一的ID：\
JobID=”作业前缀字符串“_“JobTracker启动时间”_“作业提交顺序”\
比如：job_201208071706_0009\
每个任务ID继承了作业的ID，并在此基础上进行了扩展：\
TaskID=jobID(前缀字符改成“task”)_”任务类型“_”任务编号”\
比如：task_201208071706_0009_m_000000\
每个Task Attempt的ID继承了任务ID：\
AttemptID=TaskID(前缀字符改成“attempt”)_“运行尝试次数”\
比如：attempt_201208071706_0009_m_000000_0\
![image](https://github.com/HDZ12/Big-Data-System/assets/99587726/46c856d9-ae28-460a-a9e3-bea7bead687a)

**JobInProgress**类主要用于监控和跟踪作业运行状态，并为调度器提供最底层的调度接口，其中主要维护了两种作业信息：一种是静态信息，这些信息是作业提交只是就已经确定好的；另一种是动态信息，这些信息随着作业的运行而动态变化。\
**（1）作业静态信息**\
作业静态信息是指作业提交之时就已经确定好的属性，主要包括以下几项：
1. Map Task个数
2. ReduceTask个数
3. 每个MapTask需要的内存量
4. 每个ReduceTask需要的内存量
5. 每个MapTask需要的slot个数
6. 每个ReduceTask需要的slot个数
7. 允许每个TaskTracker上失败的Task个数，默认是4.当该作业在某个TaskTracker上失败的个数超过该值时，会将该节点添加到该作业的黑名单中，调度器便不再为该节点分配该作业的任务。
8. 允许的Map Task失败比例上限。
9. 允许的Reduce Task失败比例上限。
10. 作业优先级\
**(2)作业动态信息**
作业动态信息是指作业运行过程中会动态更新的信息。这些信息对于发现TaskTracker/Job/Task故障非常有用，也可以为调度器进行任务调度提供决策依据。
1. 正在运行的MapTask数目
2. 正在运行的ReduceTask数目
3. 运行完成的MapTask数目
4. 运行完成的ReduceTask数目
5. 失败的MapTask Attempt数目
6. 失败的ReduceTask Attempt
7. 正在运行的备份任务（Map）数目
8. 正在运行的备份任务（Reduce）数目
9. 节点与TaskInProgress的映射关系，即TaskInProgress输入数据位置与节点对应关系
10. 按照失败次数进行排序的TIP集合
11. 作业提交时间
12. 作业开始执行时间
13. 作业完成时间\
**TaskInProgress**类维护了一个Task运行过程中的全部信息。在Hadoop中，由于一个任务可能会重新执行，所以会存在多个Task Attempt，且同一时刻，可能有多个Task Attempt同时在执行，而这些任务被同一个TaskInProgress对象管理和跟踪，只要任何一个任务尝试运行成功，TaskInProgress 就会标注该任务执行成功。
1. Task要处理的Split信息
2. MapTask数目（只对Reduce Task有用）
3. 该Task在Task列表中的索引
4. TaskID
5. 该TaskInProgress所在的JobInProgress
6. 运行该Task需要的slot数目
7. Task Attemp失败次数
8. 任务运行进度
9. 运行状态
10. TaskInProgress对象创建时间
11. 第一个Task Attempt开始运行时间
12. 最后一个运行成功的Task Attempt完成时间
13. Task Attempt运行成功数目，实际只有两个值：0和1
14. 该TaskInProgress的下一个可用Task Attempt ID
15. 使得该TaskInProgress运行成功的Task Attempt ID\
### 资源管理过程
**JobTracker不断接收各个TaskTracker周期性发送过来的资源量和任务状态等信息，并综合考虑TaskTracker（所在DataNode）的数据分布、资源剩余量、作业优先级、作业提交时间等因素，为TaskTracker分配最合适的任务。**\
**心跳机制**
JobTracker与TaskTracker之间采用了pull通信模型，即JobTracker从不会主动与TaskTracker通信，而总是被动等待TaskTracker汇报信息并领取命令。JobTracker只能通过心跳应答的形式为各个TaskTracker分配任务。\
_心跳机制主要有三个作用：_
> 判断Task是否活着。\
> 及时让JobTracker获取各个节点上的资源使用情况和任务运行状态。\
> 为TaskTracker分配任务。
## 任务调度框架
任务调度是一个可插拔的模块。任务调度器和JobTracker之间存在函数相互调用的关系，它们彼此都拥有对方需要的信息或者功能。对于JobTracker而言，它需要调用任务调度器中的assignTasks函数为TaskTracker分配新的任务，同时，JobTracker 内部保存了整个集群中的节点、作业和任务的运行时状态信息，这信息是任务调度器进行调度决策时需要用到的。

**JobTracker与调度器之间的函数调用关系主要有以下几点:**
1)任务调度器需要通过一个或者多个JobInProgressListener 对象从JobTracker 端监听作业状态的变化，包括作业添加、作业更新和作业删除等。\
2)任务调度器包括两个主要功能:作业初始化和任务调度。其中，作业初始化发生在JobInProgressListener#jobAdded(JobInProgress)之后，TaskScheduler#assignTasks(TaskTracker)之前，通过调用函数JobInProgress.initJob(JobInProgress)完成。\
3)任务调度器中最重要的对外函数是assignTasks.JobTracker收到能够接收新任务的TaskTracker后，会调用该函数为它分配新任务。它的输入参数是一个 TaskTracker对象，输出参数是为该TaskTracker分配的任务列表。\
*任务选择策略**\
任务选择发生在调度器选定一个作业之后， 目的是从该作业中选择一个最合适的任务。选择Map Task时需考虑的最重要的因素是数据本地性，也就是尽量将任务调度到数据所在节点。除了数据本地性之外，还需考虑失败任务、备份任务的调度顺序等。然而，由于Reduce Task没有数据本地性可言，因此选择Reduce Task时通常只需考虑未运行任务和备份任务的调度顺序。\
_任务选择策略分析_\
**(1)数据本地性**：在分布式环境中，为了减少任务执行过程中的网络传输开销，通常将任务调度到输入数据所在的计算节点，也就是让数据在本地进行计算，而MapReduce正是以“尽力而为”的策略保证数据本地性的。
为了实现数据本地性，需要管理员提供集群的网络拓扑结构。Hadoop集群采用了三层网络拓扑结构，其中，根节点表示整个集群，第一层代表数据中心，第二层代表机架或者交换机，第三层代表实际用于计算和存储的物理节点。\
![image](https://github.com/HDZ12/Big-Data-System/assets/99587726/738e50bd-2b5f-42ae-bff9-f00b249a9e69)
**(2)Map Task选择策略**：当需要从作业中选择一个 Map Task时，调度器会直接调用JobInProgress中的obtain-NewMapTask方法。该方法封装了所有调度器公用的任务选择策略实现。其主要思想是优先选择运行失败的任务，以让其快速获取重新运行的机会，其次是按照数据本地性策略选择尚未运行的任务，最后是查找正在运行的任务，尝试为“拖后腿”任务启动备份任务。
















