### Redis 基础入门

#### redis 介绍

redis 是一种基于键值对（key-value）数据库，其中value 可以为string、hash、list、set、zset 等多种数据结构，可以满足很多应用场景。还提供了键过期，发布订阅，事务，流水线，等附加功能,

流水线: Redis 的流水线功能允许客户端一次将多个命令请求发送给服务器， 并将被执行的多个命令请求的结果在一个命令回复中全部返回给客户端，使用这个功能可以有效地减少客户端在执行多个命令时需要与服务器进行通信的次数。

##### 特性：

1. 速度快，数据放在内存中，官方给出的读写性能10 万/S，与机器性能也有关
   1. 数据放内存中是速度快的主要原因
   2. C 语言实现，与操作系统距离近
   3. 使用了单线程架构，预防多线程可能产生的竞争问题
2. 键值对的数据结构服务器
3. 丰富的功能：见上功能
4. 简单稳定：单线程
5. 持久化：发生断电或机器故障，数据可能会丢失，持久化到硬盘
6. 主从复制：实现多个相同数据的redis 副本
7. 高可用和分布式：哨兵机制实现高可用，保证redis 节点故障发现和自动转移
8. 客户端语言多：java php python c c++ nodejs 等

##### 使用场景

1. 缓存：合理使用缓存加快数据访问速度，降低后端数据源压力
2. 排行榜：按照热度排名，按照发布时间排行，主要用到列表和有序集合
3. 计数器应用：视频网站播放数，网站浏览数，使用redis 计数
4. 社交网络：赞、踩、粉丝、下拉刷新
5. 消息队列：发布和订阅

##### 正确安装与启动

linux 上安装，windows 也能装，但我们以linux 环境为主

| 可执行文件       | 作用                         |
| ---------------- | ---------------------------- |
| redis-server     | 启动redis                    |
| redis-cli redis  | 命令行客户端                 |
| redis-benchmark  | 基准测试工具                 |
| redis-check-aof  | AOF 持久化文件检测和修复工具 |
| redis-check-dump | RDB 持久化文件检测和修复工具 |
| redis-sentinel   | 启动哨兵                     |

###### redis-server 启动：

- 默认配置：redis-server, 日志输出版本信息，端口6379

- 运行启动：redis-server --port 6380 不建议

- 配置文件启动： redis-server /opt/redis/redis.conf，灵活，生产环境使用这种

- redis-cli 启动

  - 交互式：redis-cli -h {host} -p {prot}连接到redis 服务，没有h 默认连127.0

    redis-cli -h 127.0.0.1 -p 6379 //没有p 默认连6379

  - 命令式：redis-cli -h 127.0.0.1 -p 6379 get hello //取key=hello 的value

  - 停止redis 服务： redis-cli shutdown

    注意:

    1.  关闭时：断开连接，持久化文件生成，相对安全
    2. 还可以用kill 关闭，此方式不会做持久化，还会造成缓冲区非法关
       闭，可能会造成AOF 和丢失数据
    3. 关闭前生成持久化文件：
       使用redis-cli -a 123456 登录进去，再shutdown nosave|save

- 重大版本：
  1，版本号第二位为奇数，为非稳定版本（2.7、2.9、3.1）
  2，第二为偶数，为稳定版本（2.6、2.8、3.0）
  3，当前奇数版本是下一个稳定版本的开发版本，如2.9 是3.0 的开发版本

### 重要特性:

####   全局命令

1. 查看所有键：`keys * set school enjoy set hello world`

2. 键总数dbsize //2 个键，如果存在大量键，线上禁止使用此指令

3. 检查键是否存在：exists key //存在返回1，不存在返回0

4. 删除键：del key //del hello school, 返回删除键个数，删除不存在键返回0

5. 键过期：expire key seconds //set name test expire name 10 //10 秒过期

   ttl 查看剩余的过期时间

6. 键的数据结构类型：type key //type hello //返回string,键不存在返回none

#### 单线程架构

列举例子：三个客户端同时执行命令
  客户端1：set name test
  客户端2：incr num
  客户端3：incr num
  执行过程：发送指令－〉执行命令－〉返回结果
  执行命令：单线程执行，所有命令进入队列，按顺序执行，使用I/O 多路复用解决I/O 问题，后面有介绍(通过select/poll/epoll/kqueue 这些I/O 多路复用函数库，我们解决了一个线程处理多个连接的问题)
单线程快原因：纯内存访问， 非阻塞I/O（使用多路复用），单线程避免线程切换和竞争产生资源消耗
问题：如果某个命令执行，会造成其它命令的阻塞

#### 字符串<String>

##### 字符串类型：

字符串（包括XML JSON）

还有数字（整形浮点数）

二进制（图片音频视频），最大不能超过512MB

##### 设值命令：

set age 23 ex 10 //10 秒后过期
setnx name test //不存在键name 时，返回1 设置成功；存在的话失败0
set age 25 xx //存在键age 时，返回1 成功
场景：如果有多客户同时执行setnx,只有一个能设置成功，可做分布式锁

##### 获值命令：

get age //存在则返回value, 不存在返回nil
批量设值：mset country china city beijing
批量获取：mget country city address //返回china beigjin, address 为nil
若没有mget 命令，则要执行n 次get 命令
使用mget相当于1 次网络请求+redis 内部n 次查询

##### 计数：

incr age //必须为整数自加1，非整数返回错误，无age 键从0 自增返回1
decr age //整数age 减1
incrby age 2 //整数age+2
decrby age 2//整数age -2
incrbyfloat score 1.1 //浮点型score+1.1

##### append 追加指令：

set name hello; 

append name world //追加后成helloworld

##### 字符串长度：

set hello “世界”；strlen hello//结果6，每个中文占3 个字节

##### 截取字符串：

set name helloworld ; 

getrange name 2 4 // 返回llo

##### 内部编码：

- int:8 字节长整

  //set age 100; object encoding age //返回int

- embstr:小于等于39 字节串

  set name bejin; object encoding name   //embstr

- raw:大于39 字节的字符串

  set a fsdfwerwerweffffffffffdfs //返回raw

##### 应用场景：

1.   键值设计：业务名:对象名: id:[属性]
     数据库为order, 用户表user，对应的键可为order:user:1 或order:user:1:name

注意：redis 目前处于受保护模式，不允许非本地客户端链接，可以通过给redis设置密码，然后客户端链接的时候，写上密码就可以了
   127.0.0.1:6379> config set requirepass 123456 临时生效
   或者修改redis.conf requirepass 123456,启动时./redis-server redis.conf 指定conf
   ./redis-cli -p 6379 -a 12345678 //需要加入密码才能访问

#### 哈希hash：

是一个string 类型的field 和value 的映射表，hash 特适合用于存储
对象。

##### 命令

###### 设值：

hset user:1 name james //成功返回1，失败返回0

###### 取值：

hget user:1 name //返回james

###### 删值：

hdel user:1 age //返回删除的个数

###### 计算个数：

hset user:1 name james; 

hset user:1 age 23;
hlen user:1 //返回2，user:1 有两个属性值

###### 批量设值：

hmset user:2 name james age 23 sex boy //返回OK

###### 批量取值：

hmget user:2 name age sex //返回三行：james 23 boy

###### 判断field 是否存在：

hexists user:2 name //若存在返回1，不存在返回0

###### 获取所有field: 

hkeys user:2 // 返回name age sex 三个field

###### 获取所有value：

hvals user:2 // 返回james 23 boy

###### 获取所有field 与value：

hgetall user:2 //name age sex james 23 boy 值

###### value增加：

hincrby user:2 age 1 //age+1
hincrbyfloat user:2 age 2 //浮点型加2

##### 内部编码：

###### ziplist<压缩列表>

当field 个数少且没有大的value 时，内部编码为ziplist
如：hmset user:3 name james age 24; object encoding user:3 //返回ziplist

###### hashtable<哈希表>

当value 大于64 字节，内部编码由ziplist 变成hashtable
如：hset user:4 address “fsgst64 字节”; object encoding user:3 //返回hashtable

##### 应用场景：

比如将关系型数据表转成redis 存储：
使用hash 后的存储方式为：
如果有值为NULL，那么如下：
HASH 类型是稀疏，每个键可以有不同的filed, 若用redis 模拟做关系复杂查询开发因难，维护成本高

#### 三种方案实现用户信息存储优缺点：

- 原生：set user:1:name james;
  set user:1:age 23;
  set user:1:sex boy;
  优点：简单直观，每个键对应一个值

  缺点：键数过多，占用内存多，用户信息过于分散，不用于生产环境

- 将对象序列化存入redis
  set user:1 serialize(userInfo);
  优点：编程简单，若使用序列化合理内存使用率高
  缺点：序列化与反序列化有一定开销，更新属性时需要把userInfo 全取出来
  进行反序列化，更新后再序列化到redis

- 使用hash 类型：
  hmset user:1 name james age 23 sex boy
  优点：简单直观，使用合理可减少内存空间消耗
  缺点：要控制ziplist 与hashtable 两种编码转换，且hashtable 会消耗更多内存

- 总结：对于更新不多的情况下，可以使用序列化，对于VALUE 值不大于64 字节可以使用hash 类型

#### 列表<list>

##### 特点：

用来存储多个有序的字符串，一个列表最多可存2 的32 次方减1 个元素。因为有序，可以通过索引下标获取元素或某个范围内元素列表，列表元素可以重复

##### 列表命令：

###### 添加命令：

###### 从右向左插入

rpush james c b a //从右向左插入cba, 返回值3

###### 从左向右插入

lpush key c b a //从左向右插入cba

lpush test b b b b b j x z //键test 放入z x j b b b b b

###### 从左到右获取列表所有元素

lrange james 0 -1 //从左到右获取列表所有元素返回c b a

lrange test 0 -1 //查询结果为z x j b b b b b

###### 某元素之前插入

linsert james before b teacher //在b 之前插入teacher, after 为之后，使
用lrange james 0 -1 查看：c teacher b a

##### 查找命令：

###### lrange key start end 

索引下标特点：从左到右为0 到N-1

###### 返回最右末尾第n个 

lindex james -1

###### 返回当前列表长度

llen james 

###### 删除最左

lpop james

###### 删除最右

rpop james 

###### 删除指定元素

lrem key count value

###### 删除指定个数的某个元素

lrem test 4 b //从左右开始删除b 的元素,删除4 个，
若lrem test 8 b, 删除8 个b, 但只有5 个全部删除

###### 删除所有指定元素

lrem test 0 b //检索所有b 全部删除j x z

###### 只保留某范围元素，其他删除

lrange user 0 -1 //查询结果为x j b, 其它已全被删掉

###### 替换某个元素

lset user01 2 java // 把第3 个元素z 替换成java

##### 应用场景设计



##### 列表内部编码

从redis 的官网查阅了相关资料，在3.2 版本以后，redis提供了quicklist 内部编码，它结合了ziplist 和linkedlist 两者的优势，之前的ziplist 是存在BUG的，使用quicklist 内部编码效率更高，所以我们现在3.2 以后看不到这两个编码，只看到**quicklist**（https://matt.sh/redis-quicklist ）

#### 集合<SET> 

用户标签，社交，查询有共同兴趣爱好的人,智能推荐
保存多元素，与列表不一样的是不允许有重复元素，且集合是无序，一个集合最多可存2 的32 次方减1 个元素，除了支持增删改查，还支持集合交集、并集、差集；

##### 命令：

###### 检查键值是否存在

exists user 

###### 插入n个元素

sadd user a b c //向user 插入3 个元素，返回3
sadd user a b //若再加入相同的元素，则重复无效，返回0

###### 获取的所有元素

smembers user //获取user 的所有元素,返回结果无序

###### 删除元素

srem user a //返回1，删除a 元素

###### 计算元素个数

scard user //返回2，计算元素个数

###### 判断元素是否在集合存在

sismember user a //判断元素是否在集合存在，存在返回1，不存在0

###### 随机返回n 个元素

srandmember user 2 //随机返回2 个元素，2 为元素个数

###### 随机移除并返回n 个元素

spop user 2 //随机返回2 个元素a b,并将a b 从集合中删除

###### 集合的交集：

sadd user:1 zhangsan 24 girl
sadd user:2 james 24 boy//初始化两个集合
sinter user:1 user:2 //求两集合交集， 此时返回24
sadd user:3 wang 24 girl //新增第三个元素
sinter user:1 user:2 user:3 //求三个集合的交集，此时返回24

###### 集合的并集（集合合并去重）：

sunion user:1 user:2 user:3 //三集合合并(并集)，去重24

###### 集合的差集

sdiff user:1 user:2//1 和2 差集,(zhangsan 24 girl)-(james 24 boy)=zhangsan girl

###### 将交集(jj)、并集(bj)、差集(cj)的结果保存：

sinterstore user_jj user:1 user:2 //将user:1 user:2 的交集保存到user_jj
sunionstore user_bj user:1 user:2 //将user:1 user:2 的(并)合集保存user_bj
sdiffstore user_cj user:1 user:2 //将user:1-user:2 的差集保存user_cj
smemebers user_cj // 返回zhangsan girl

##### 内部编码：

- intset 当元素个数小于512 个且都为整数，使用减少内存的使用
- hashtable当超过512 个或不为整数时，编码为

##### 使用场景：

标签，社交，查询有共同兴趣爱好的人,智能推荐
使用方式：
给用户添加标签：
sadd user:1:fav basball fball pq
sadd user:2:fav basball fball
............
或给标签添加用户
sadd basball:users user:1 user:3
sadd fball:users user:1 user:2 user:3
........
计算出共同感兴趣的人：
sinter user:1:fav user2:fav
规则：sadd (常用于标签) spop/srandmember(随机，比如抽奖)
sadd+sinter (用于社交，查询共同爱好的人，匹配)

#### 有序集合

常用于排行榜，如视频网站需要对用户上传视频做排行榜，或点赞数
与集合有联系，**不能有重复的成员**

##### 命令

**zadd key score member [score member......]**

###### 添加

zadd user:zan 200 james //james 的点赞数1, 返回操作成功的条数1
zadd user:zan 200 james 120 mike 100 lee// 返回3

zadd test:1 nx 100 james //键test:1 必须不存在，主用于添加
zadd test:1 xx incr 200 james //键test:1 必须存在，主用于修改,此时为300
zadd test:1 xx ch incr -299 james //返回操作结果1，300-299=1

###### 范围查看成员（分数）

zrange test:1 0 -1 withscores //查看点赞（分数）与成员名

###### 计算成员个数

zcard test:1 //计算成员个数， 返回1

###### 查点赞数

zadd test:2 nx 100 james //新增一个集合
zscore test:2 james //查看james 的点赞数（分数），返回100

###### 排名范围查询：

zadd user:3 200 james 120 mike 100 lee//先插入数据
zrange user:3 0 -1 withscores //查看分数与成员
zrank user:3 james //返回名次：第3 名返回2，从0 开始到2，共3 名
zrevrank user:3 james //返回0， 反排序，点赞数越高，排名越前

###### 删除成员：

zrem user:3 jame mike //返回成功删除2 个成员，还剩lee

###### 增加分数：

zincrby user:3 10 lee //成员lee 的分数加10
zadd user:3 xx incr 10 lee //和上面效果一样
返回指定排名范围的分数与成员
zadd user:4 200 james 120 mike 100 lee//先插入数据
zrange user:4 0 -1 withscores //返回结果
zrevrange user:4 0 -1 withscores //倒序

###### 返回指定分数范围的成员

zrangebyscore user:4 110 300 withscores //返回120 lee ,200 James, 由低到高
zrevrangebyscore user:4 300 110 withscores //返回200james 120lee,由高到低
zrangebyscore user:4 (110 +inf withscores//110 到无限大，120mike 200james
zrevrangebyscore user:4 (110 -inf withscores//无限小到110，返回100 lee

###### 返回指定分数范围的成员个数：

zcount user:4 110 300 //返回2，由mike120 和james200 两条数据

###### 删除指定排名内的升序元素：

zremrangebyrank user:4 0 1 //分数升序排列，删除第0 个与第1 个，只剩james

###### 删除指定分数范围的成员

zadd user:5 200 james 120 mike 100 lee//先插入测试数据
zremrangebyscore user:5 100 300 //删除分数在100 与300 范围的成员
zremrangebyscore user:5 (100 +inf //删除分数大于100(不包括100),还剩lee

###### 有序集合交集：

**格式：zinterstore destination numkeys key ... [WEIGHTS weight] [AGGREGATE SUM|MIN|MAX]**
destination:交集产生新的元素存储键名称
numkeys: 要做交集计算的键个数
key :元素键值
weights:每个被选中的键对应值乘weight, 默认为1
初始化数据：
zadd user:7 1 james 2 mike 4 jack 5 kate //初始化user:7 数据
zadd user:8 3 james 4 mike 4 lucy 2 lee 6 jim //初始化user:8 数据
交集例子：
zinterstore user_jj 2 user:7 user:8 aggregate sum //2 代表键合并个数，
//aggregate sum 可加也不可加上，因为默认是sum
//结果user_jj：4james(1+3), 6mike(2+4)
zinterstore user_jjmax 2 user:7 user:8 aggregate max 或min
//取交集最大的分数，返回结果3james 4mike, min 取最小
weights:
zinterstore user_jjweight 2 user:7 user:8 weights 8 4 aggregate max
//1,取两个成员相同的交集，user:7->1 james 2 mike; user:8->3 james 4 mike
//2,将user:7->james 1*8=8, user:7->mike 2*8 =16,
最后user:7 结果8 james 16 mike;
//3,将user:8-> james 3*4=12, user:8->mike 4*4=16
最后user:8 结果12 james 16 mike
//4,最终相乘后的结果，取最大值为12 james 16mike
//5, zrange user_jjweight 0 -1 withscores 查询结果为12 james 16mike
总结：将user:7 成员值乘8，将user:8 成员值乘4，取交集，取最大

###### 有序集合并集（合并去重）：

格式：zunionstore destination numkeys key ... [WEIGHTS weight] [AGGREGATE SUM|MIN|MAX]
destination:交集产生新的元素存储键名称
numkeys: 要做交集计算的键个数
key :元素键值
weights:每个被选中的键对应值乘weight, 默认为1
zunionstore user_jjweight2 2 user:7 user:8 weights 8 4 aggregate max
//与以上zinterstore 一样，只是取并集，指令一样

##### 内部编码

- 使用ziplist 编码，可有效减少内存的使用（当元素个数少（小于128 个），元素值小于64 字节时）

  ziplist: zadd user:9 20 james 30 mike 40 lee
  object encoding user:init

- skiplist 编码 （大于128 个元素或元素值大于64 字节时）

  kiplist: zadd user:10 20 james......

##### 使用场景

排行榜系统，如视频网站需要对用户上传的视频做排行榜
点赞数：zadd user:1:20180106 3 mike //mike 获得3 个赞
再获一赞：zincrby user:1:20180106 1 mike //在3 的基础上加1
用户作弊，将用户从排行榜删掉：zrem user:1:20180106 mike
展示赞数最多的5 个用户：
zadd user:4:20160101 9 jack 10 jj 11 dd 3 james 4 lee 6 mark 7 kate
zrevrangebylex user:4:20160101 + - limit 0 5
查看用户赞数与排名：
zscore user:1:20180106 mike zrank user:1:20180106 mike

### Redis 缓存雪崩与穿透

#### 雪崩

前提:为节约内存,Redis 一般会做定期清除操作
1)当查询key=james 的值,此时Redis 没有数据
2)如果有5000 个用户并发来查询key=james,全到Mysql 里去查, Mysql 会挂掉,导致雪崩;
解决方案如下：

1. 设置热点数据永远不过期
2. 加互斥锁

#### 穿透

前提:黑客模拟一个不存在的订单号xxxx
1)Redis 中无此值
2)Mysql 中也无此值, 但一直被查询
解决方案

对订单表所有数据查询出来放到布隆过滤器, 经过布隆过滤器处理的数据很小(只存0或1),每次查订单表前,先到过滤器里查询当前订单号状态是0 还是1, 0 的话代表数据库没有数据,直接拒绝查询

#### redis 持久化

redis 支持RDB 和AOF 两种持久化机制，持久化可以避免因进程退出而造成数据丢失

##### RDB 持久化

RDB 持久化把当前进程数据生成快照（.rdb）文件保存到硬盘的过程，有手动触发和自动触发,手动触发有**save**和**bgsave** 两命令

- save 命令：阻塞当前Redis，直到RDB 持久化过程完成为止，若内存实例比较大
  会造成长时间阻塞，线上环境不建议用它

- bgsave 命令：redis 进程执行fork 操作创建子线程，由子线程完成持久化，阻塞时
  间很短（微秒级），是save 的优化,在执行redis-cli shutdown 关闭redis 服务时，如果没有开
  启AOF 持久化，自动执行bgsave;显然bgsave 是对save 的优化。
  bgsave 运行流程
  
###### RDB 文件的操作

命令：config set dir /usr/local //设置rdb 文件保存路径
备份：bgsave //将dump.rdb 保存到usr/local 下
恢复：将dump.rdb 放到redis 安装目录与redis.conf 同级目录，重启redis 即可
优点：

1. 压缩后的二进制文，适用于备份、全量复制，用于灾难恢复
2. 加载RDB 恢复数据远快于AOF 方式

缺点：

1. 无法做到实时持久化，每次都要创建子进程，频繁操作成本过高
2. 保存后的二进制文件，存在老版本不兼容新版本rdb 文件的问 

##### AOF 持久化

针对RDB 不适合实时持久化，redis 提供了AOF 持久化方式来解决
开启：redis.conf 设置：appendonly yes (默认不开启，为no)
默认文件名：appendfilename "appendonly.aof"
流程说明：

1. 所有的写入命令(set hset)会append 追加到aof_buf 缓冲区中

2. AOF 缓冲区向硬盘做sync 同步

3. 随着AOF 文件越来越大，需定期对AOF 文件rewrite 重写，达到压缩

4. 当redis 服务重启，可load 加载AOF 文件进行恢复

总结：

AOF 持久化流程：命令写入(append),文件同步(sync),文件重写(rewrite),重启加载(load)

###### redis 的AOF 配置详解：

- appendonly yes //启用aof 持久化方式
- appendfsync always //每收到写命令就立即强制写入磁盘，最慢的，但是保证完全的持久化，不推荐使用
- appendfsync everysec //每秒强制写入磁盘一次，性能和持久化方面做了折中，推荐
- appendfsync no //完全依赖os，性能最好,持久化没保证（操作系统自身的同步）
- no-appendfsync-on-rewrite yes //正在导出rdb 快照的过程中,要不要停止同步aof
- auto-aof-rewrite-percentage 100 //aof 文件大小比起上次重写时的大小 增长率100%时,重写
  auto-aof-rewrite-min-size 64mb //aof 文件,至少超过64M 时,重写

###### 如何从AOF 恢复

1. 设置appendonly yes；

2. 将appendonly.aof 放到dir 参数指定的目录；

3. 启动Redis，Redis 会自动加载appendonly.aof 文件。

   

###### redis 重启时恢复加载AOF 与RDB 顺序及流程：

1. 当AOF 和RDB 文件同时存在时，优先加载
2. 若关闭了AOF，加载RDB 文件
3. 加载AOF/RDB 成功，redis 重启成功
4. AOF/RDB 存在错误，redis 启动失败并打印错误信息

### Redis 慢查询

与mysql 一样:当执行时间超过极大值时，会将发生时间耗时命令记录.
通信流程如下:
Redis 的所有指令全部会存放到队列, 由单线程按顺序获取并执行指令, 如果某个指令执行很慢, 会出现阻塞, 

#### Redis 设慢查询阈值

1. 动态设置6379:> config set slowlog-log-slower-than 10000 //10 毫秒
   使用config set 完后,若想将配置持久化保存到redis.conf，要执行config rewrite
2. redis.conf 修改：找到slowlog-log-slower-than 10000 ，修改保存即可
   注意：slowlog-log-slower-than =0 记录所有命令 -1命令都不记录

#### Redis 慢查询原理

慢查询记录也是存在队列里的，slow-max-len 存放的记录最大条数，比如设置的slow-max-len＝10，当有第11 条慢查询命令插入时，队列的第一条命令就会出列，第11 条入列到慢查询队列中， 可以config set 动态设置，也可以修改redis.conf 完成配置

#### Redis 慢查询的命令

##### 获取队列里慢查 询的命令

slowlog get

##### 获取慢查询列表当前的长度

slowlog len //以上只有1 条慢查询，返回1；

##### 对慢查询列表清理（重置）

slowlog reset //再查slowlog len 此时返回0 清空；

##### 对于线上slow-max-len 配置的建议

线上可加大slow-max-len 的值，记录慢查询存长命令时redis 会做截断，不会占用大量内存，线上可设置1000 以上

##### 对于线上slowlog-log-slower-than 配置的建议

默认为10 毫秒，根据redis 并发量来调整，对于高并发比建议为1 毫秒
慢查询是先进先出的队列，访问日志记录出列丢失，需定期执行slowlog get,将结果存储到其它设备中（如mysql）

#### Redis 性能测试工具使用

- redis-benchmark -h 192.168.42.111 -p 6379 -c 100 -n 10000

  //100 个并发连接，10000 个请求，检测服务器性能

- redis-benchmark -h 192.168.42.111 -p 6379 -q -d 100

  //测试存取大小为100 字节的数据包的性能

- redis-benchmark -h 192.168.42.111 -p 6379 -t set,get -n 100000 -q

  //只测试set,lpush 操作的性能

- redis-benchmark -h 192.168.42.111 -p 6379 -n 100000 -q script load "redis.call('set','foo','bar')"

  //只测试某些数值存取的性能

#### Resp 协议

Redis 服务器与客户端通过RESP（Redis Serialization Protocol）协议通信。
主要以下特点：容易实现,解析快,人类可读.
RESP 底层采用的是TCP 的连接方式，通过tcp 进行数据传输，然后根据解析规则解析相
应信息，完成交互。
我们可以测试下，首先运行一个serverSocket 监听6379，来接收redis 客户端的请求信
息，实现如下
服务端程序如下:
客户端程序如下:
测试发现, 服务端打印的信息如下:
这就是Resp 协议的结构.

#### 将现有表数据快速存放到Redis

流程如下:

1. 使用用户名和密码登陆连接数据库

2. 登陆成功后执行order.sql 的select 语句得到查询结果集result

3. 使用密码登陆Redis

4. Redis 登陆成功后, 使用PIPE 管道将result 导入Redis.
   操作指令如下:

```shell
mysql -utest -ptest stress --default-character-set=utf8 --skip-column-names --raw < order.sql |
redis-cli -h 192.168.42.111 -p 6379 -a 12345678 --pipe
```

#### PIPELINE 操作流程

大多数情况下，我们都会通过请求-相应机制去操作redis。只用这种模式的一般的步骤是，先获得jedis 实例，然后通过jedis 的get/put 方法与redis 交互。由于redis 是单线程的，下一次请求必须等待上一次请求执行完成后才能继续执行。然而使用Pipeline 模式，客户端可以一次性的发送多个命令，无需等待服务端返回。这样就大大的减少了网络往返时间，提高了系统性能。
A>批量操作时使用如下代码网络开销非常大
每一次请求都会建立网络连接, 非常耗时, 特别是跨机房的场景下
B>使用PIPELINE 可以解决网络开销的问题,代码如下:
原理也非常简单,流程如下, 将多个指令打包后,一次性提交到Redis, 网络通信只有一次

#### Redis 弱事务

刚大家知道，pipeline 是多条命令的组合，为了保证它的原子性，redis 提供了简单的事务，什么是事务？事务是指一组动作的执行，这一组动作要么成功，要么失败。

1. redis 的简单事务，将一组需要一起执行的命令放到multi 和exec 两个命令之间，其中multi 代表事务开始，exec 代表事务结束
   注：在multi 前set user:age 4 //请提前初始化该值
2. 停止事务discard
3. 命令错误，语法不正确，导致事务不能正常结束
4. 运行错误，语法正确，但类型错误，事务可以正常结束可以看到redis不支持回滚功能
5. Watch 让事务失效，操作命令

#### redis 主要提供发布消息、订阅频道、取消订阅以及按照模式订阅和取消订阅

1. 发布消息

   publish channel:test "hello world"

2. 订阅消息

   subscrible channel:test
   此时另一个客户端发布一个消息:publish channel:test "james test"
   当前订阅者客户端会收到如下消息：
   和很多专业的消息队列（kafka rabbitmq）,redis 的发布订阅显得很lower, 比如无
   法实现消息规程和回溯， 但就是简单，如果能满足应用场景，用这个也可以

3. 查看订阅数

   pubsub numsub channel:test // 频道channel:test 的订阅数

4. 取消订阅

   unsubscribe channel:test

   客户端可以通过unsubscribe 命令取消对指定频道的订阅，取消后，不会再收到
   该频道的消息

5. 按模式订阅和取消订阅

   psubscribe ch* //订阅以ch 开头的所有频道
   punsubscribe ch* //取消以ch 开头的所有频道

6. 应用场景：
   1、今日头条订阅号、微信订阅公众号、新浪微博关注、邮件订阅系统
   2、即使通信系统
   3、群聊部落系统（微信群）

#### 键的迁移：把部分数据迁移到另一台redis 服务器

1. move key db;

   //reids 有16 个库， 编号为0－15
   set name james1; move name 5 //迁移到第6 个库
   select 5 ;//数据库切换到第6 个库， get name 可以取到james1
   这种模式不建议在生产环境使用，在同一个reids 里可以玩

2. dump key;

   restore key ttl value//实现不同redis 实例的键迁移，ttl=0 代表没有过期时间
   例子：在A 服务器上192.168.42.111
   set name james;
   dump name; // 得到"\x00\x05james\b\x001\x82;f\"DhJ"
   在B 服务器上：192.168.1.118
   restore name 0 "\x00\x05james\b\x001\x82;f\"DhJ"
   get name //返回james

3. migrate 用于在Redis 实例间进行数据迁移，实际上migrate 命令是将dump、restore、del 三个命令进行组合，从而简化了操作流程。
   migrate 命令具有原子性，从Redis 3.0.6 版本后已经支持迁移多个键的功能。
   migrate 命令的数据传输直接在源Redis 和目标Redis 上完成，目标Redis 完成
   restore 后会发送OK 给源Redis。
   migrate 实例操作：
   migrate 指令迁移到其它实例redis，在4222.111 服务器上将name 移到112
   指令如下：把111 上的name 键值迁移到112 上的redis
   192.168.42.111:6379> migrate 192.168.42.112 6379 name 0 1000 copy

  #### 键的遍历

redis 提供了两个命令来遍历所有的键

###### 键全量遍历

mset country china city bj name james //设置3 个字符串键值对
keys * //返回所有的键, *匹配任意字符多个字符
keys *y //以y结尾的键，
keys n*e //以n 开头以e 结尾，返回name
keys n?me // ?问号代表只匹配一个字符返回name,全局匹配
keys n?m* //返回name
keys [j,l]* //返回以j l 开头的所有键keys [j]ames 全量匹配james
考虑到是单线程， 在生产环境不建议使用，如果键多可能会阻塞
如果键少，可以

###### 渐进式遍历

初始化13 组KEY－VALUE
渐进式遍历
1.初始化数据
mset n1 1 n2 2 n3 3 n4 4 n5 5 n6 6 n7 7 n8 8 n9 9 n10 10 n11 11 n12 12 n13 13
2.遍历匹配:匹配以n 开头的键，最大是取5 条，第一次scan 0 开始
scan 0 match n* count 5
第二次从游标4096 开始取20 个以n 开头的键，相当于一页一页的取当最后返回0 时，键被取完,但count 不准,一般用来代替keys *操作，可避免阻塞
第二次从游标18 开始取20 个以n 开头的键，相当于一页一页的取当最后返回0 时，键被取完

###### 两种遍历对比

scan 相比keys 具备有以下特点:

1. 通过游标分布进行的，不会阻塞线程（可能这个特点我们靠谱点，下面了解即可）;

2. 提供limit 参数，可以控制每次返回结果的最大条数，limit 不准，返回的结果可多可少;

3. 同keys 一样，Scan 也提供模式匹配功能;

4. 服务器不需要为游标保存状态，游标的唯一状态就是scan 返回给客户端的游标整数;

5. scan 返回的结果可能会有重复，需要客户端去重复;

6. scan 遍历的过程中如果有数据修改，改动后的数据能不能遍历到是不确定的;

7. 单次返回的结果是空的并不意味着遍历结束，而要看返回的游标值是否为零;

注：可有效地解决keys 命令可能产生的阻塞问题，除scan 字符串外：还有以下

- SCAN 命令用于迭代当前数据库中的数据库键
- SSCAN 命令用于迭代集合键中的元素
- HSCAN 命令用于迭代哈希键中的键值对
-  ZSCAN 命令用于迭代有序集合中的元素（包括元素成员和分值）用法和scan 一样

 

### 集群

#### 主从复制

##### 配置主从复制配置

- 方式一、新增redis6380.conf, 加入slaveof 192.168.42.111 6379, 在6379 启动完
  后再启6380，完成配置；
- 方式二、redis-server --slaveof 192.168.42.111 6379 

##### 查看状态：

info replication

##### 断开主从复制

在slave 节点，执行6380:>slaveof no one

##### 断开后再变成主从复制

6380:> slaveof 192.168.42.111 6379

##### 数据较重要的节点，主从复制时使用密码验证

requirepass

##### 从节点建议用只读模式

slave-read-only=yes, 若从节点修改数据，主从数据不一致
传输延迟

##### repl-disable-tcp-nodelay

主从一般部署在不同机器上，复制时存在网络延时问题，redis 提供repl-disable-tcp-nodelay 参数决定是否关闭TCP_NODELAY,默认为关闭

参数关闭时：无论大小都会及时发布到从节点，占带宽，适用于主从网络好的场景
参数启用时：主节点合并所有数据成TCP 包节省带宽，默认为40 毫秒发一次，取决于内核，主从的同步延迟40 毫秒，适用于网络环境复杂或带宽紧张，如跨机房

#### 主从拓扑

支持单层或多层

- 一主一从：用于主节点故障转移从节点，当主节点的“写”命令并发高且需要持久化，可以只在从节点开启AOF（主节点不需要），这样即保证了数据的安全性，也避免持久化对主节点的影响
- B一主多从：针对“读”较多的场景，“读”由多个从节点来分担，但节点越多，主节点同步到多节点的次数也越多，影响带宽，也加重主节点的稳定
- 树状主从：一主多从的缺点（主节点推送次数多压力大）可用些方案解决，主节点只推送一次数据到从节点1，再由从节点2 推送到11，减轻主节点推送的压力。

复制原理
执行slave master port 后，与主节点连接，同步主节点的数据,6380:>info replication：查看主从及同步信息

master:6379，配置完slave of 127.0.0.1 6379，slave 6380 启动
1，保存主节点信息
2，主从建立socket 连接
3，发送ping 命令
4，权限验证
5，同步数据集
6，命令持续复制
3，数据同步：
redis 2.8 版本以上使用psync 命令完成同步，过程分“全量”与“部分”复制

-  全量复制：一般用于初次复制场景（第一次建立SLAVE 后全量）

- 部分复制：网络出现问题，从节占再次连主时，主节点补发缺少的数据，每次数据增加同步

心跳： 主从有长连接心跳， 主节点默认每10S 向从节点发ping 命令，
  repl-ping-slave-period 控制发送频率

#### 哨兵机制

为什么要用哨兵机制？

- 我们学习了redis 的主从复制，但如果说主节点出现问题不能提供服务，需要人工重新把从节点设为主节点，还要通知我们的应用程序更新了主节点的地址，这种处理方式不是科学的，耗时费事

- 主节点的写能力是单机的，能力能限

- 主节点是单机的，存储能力也有限

  

其中2，3 的问题在后面redis 集群课会讲，第1 个问题我们用***哨兵机制***来解决

##### 主从故障如何故障转移(不满足高可用)

1. 主节点(master)故障，从节点slave-1 端执行slaveof no one 后变成新主节点
2. 其它的节点成为新主节点的从节点，并从新节点复制数据

##### 哨兵机制(sentinel)的高可用

###### 原理

当主节点出现故障时，由redis sentinel 自动完成故障发现和转移，并通知应用方，实现高可用性。
主从复制与redis sentinel 拓扑结构图

![image-20210826183225423](.\imgs\redis_主从.png)<img src=".\imgs\redis_sentinel.png" alt="image-20210826183255498"  />

其实整个过程只需要一个哨兵节点来完成，首先使用Raft 算法（感兴趣的同学可以查一下，其实就是个选举算法）实现选举机制，选出一个哨兵节点来完成转移和通知哨兵有三个定时监控任务完成对各节点的发现和监控：

1. 任务1，每个哨兵节点每10 秒会向主节点和从节点发送info 命令获取最拓扑结构图，哨兵配置时只要配置对主节点的监控即可，通过向主节点发送info，获取从节点的信息，并当有新的从节点加入时可以马上感知到
2. 任务2，每个哨兵节点每隔2 秒会向redis 数据节点的指定频道上发送该哨兵节点对于主节点的判断以及当前哨兵节点的信息，同时每个哨兵节点也会订阅该频道，来了解其它哨兵节点的信息及对主节点的判断，其实就是通过消息publish 和subscribe 来完成的
3. 任务3，每隔1 秒每个哨兵会向主节点、从节点及其余哨兵节点发送一次ping 命令做一次心跳检测，这个也是哨兵用来判断节点是否正常的重要依据

###### 主观下线和客观下线：

主观下线：刚我知道知道哨兵节点每隔1 秒对主节点和从节点、其它哨兵节点发送ping做心跳检测，当这些心跳检测时间超过down-after-milliseconds 时，哨兵节点则认为该节点错误或下线，这叫主观下线；这可能会存在错误的判断。
客观下线：当主观下线的节点是主节点时，此时该哨兵3 节点会通过指令sentinel is-masterdown-by-addr 寻求其它哨兵节点对主节点的判断，当超过quorum（法定人数）个数，此时哨兵节点则认为该主节点确实有问题，这样就客观下线了，大部分哨兵节点都同意下线操作，也就说是客观下线领导者哨兵选举流程：

1. 每个在线的哨兵节点都可以成为领导者，当它确认（比如哨兵3）主节点下线时，会向其它哨兵发is-master-down-by-addr 命令，征求判断并要求将自己设置为领导者，由领导者处理故障转移；
2. 当其它哨兵收到此命令时，可以同意或者拒绝它成为领导者；
3. 如果哨兵3 发现自己在选举的票数大于等于num(sentinels)/2+1 时，将成为领导者，如果没有超过，继续选举…………

###### 故障转移机制

由Sentinel 节点定期监控发现主节点是否出现了故障sentinel 会向master 发送心跳PING 来确认master 是否存活，如果master 在“一定
时间范围”内不回应PING 或者是回复了一个错误消息，那么这个sentinel 会主观地(单方面地)认为这个master 已经不可用了。当主节点出现故障，此时3 个Sentinel 节点共同选举了Sentinel3 节点为领导，负载处理主节点的故障转移,由Sentinel3 领导者节点执行故障转移，过程和主从复制一样，但是自动执行
流程： 

1. 将slave-1 脱离原从节点，升级主节点
2. 将从节点slave-2 指向新的主节点
3. 通知客户端主节点已更换
4. 将原主节点（oldMaster）变成从节点，指向新的主节点

5，哨兵机制－故障转移详细流程

1. 过滤掉不健康的（下线或断线），没有回复过哨兵ping 响应的从节点
2. 选择slave-priority 从节点优先级最高（redis.conf）
3. 选择复制偏移量最大，指复制最完整的从节点

###### 安装和部署Reids Sentinel

我们以3 个Sentinel 节点、2 个从节点、1 个主节点为例进行安装部署

1.  前提：先搭好一主两从redis 的主从复制，和之前复制搭建一样，搭建方式如下：

   A 主节点6379 节点（/usr/local/bin/conf/redis6379.conf）：
   修改requirepass 12345678，注释掉#bind 127.0.0.1
   B 从节点redis6380.conf 和redis6381.conf:
   修改requirepass 12345678 ,注释掉#bind 127.0.0.1,
   加上masterauth 12345678 ,加上slaveof 127.0.0.1 6379
   注意：当主从起来后，主节点可读写，从节点只可读不可写

2. edis sentinel 哨兵机制核心配置(也是3 个节点)：

   /usr/local/bin/conf/sentinel_26379.conf
   /usr/local/bin/conf/sentinel_26380.conf
   /usr/local/bin/conf/sentinel_26381.conf
   将三个文件的端口改成: 26379 26380 26381
   然后：

   sentinel monitor mymaster 190.168.1.111 6379 2 //监听主节点6379
   sentinel auth-pass mymaster 12345678 //连接主节点时的密码
   三个配置除端口外，其它一样。

3. 哨兵其它的配置：只要修改每个sentinel.conf 的这段配置即可

   sentinel monitor mymaster 192.168.1.10 6379 2
   //监控主节点的IP 地址端口，sentinel 监控的master 的名字叫做mymaster
   2 代表，当集群中有2 个sentinel 认为master 死了时，才能真正认为该master已经不可用了
   sentinel auth-pass mymaster 12345678 //sentinel 连主节点的密码
   sentinel config-epoch mymaster 2 //故障转移时最多可以有2 从节点同时对新主节点进行数据同步
   sentinel leader-epoch mymaster 2
   sentinel failover-timeout mymasterA 180000 //故障转移超时时间180s，
   a,如果转移超时失败，下次转移时时间为之前的2 倍；
   b,从节点变主节点时，从节点执行slaveof no one 命令一直失败的话，当时间超过180S 时，则故障转移失败
   c,从节点复制新主节点时间超过180S 转移失败
   sentinel down-after-milliseconds mymasterA 300000//sentinel 节点定期向主节点
   ping 命令，当超过了300S 时间后没有回复，可能就认定为此主节点出现故障了……
   sentinel parallel-syncs mymasterA 1 //故障转移后，1 代表每个从节点按顺序排队一个一个复制主节点数据，如果为3，指3 个从节点同时并发复制主节点数据，不会影响阻塞，但存在网络和IO 开销

4. 启动sentinel 服务

   ./redis-sentinel conf/sentinel_26379.conf &
   ./redis-sentinel conf/sentinel_26380.conf &
   ./redis-sentinel conf/sentinel_26381.conf &
   关闭：./redis-cli -h 192.168.42.111 -p 26379 shutdown

5. 测试：kill -9 6379 杀掉6379 的redis 服务

   看日志是分配6380 还是6381 做为主节点，当6379 服务再启动时，已变成从节点
   假设6380 升级为主节点:进入6380>info replication 可以看到role:master
   打开sentinel_26379.conf 等三个配置，sentinel monitor mymaster 127.0.0.1 6380 2
   打开redis6379.conf 等三个配置, slaveof 192.168.42.111 6380,也变成了6380
   注意：生产环境建议让redis Sentinel 部署到不同的物理机上。
   重要： sentinel monitor mymaster 192.168.42.111 6379 2 // 切记将IP 不要写成127.0.0.1
   不然使用JedisSentinelPool 取jedis 连接的时候会变成取127.0.0.1 6379 的错误地址
   注：我们稍后要启动四个redis 实例，其中端口为6379 的redis 设为master，其他两个设为slave 。所以mymaster 后跟的是master 的ip 和端口，最后一个’2’代表只要有2 个sentinel 认为master 下线，就认为该master 客观下线，选举产生新的master。
   通常最后一个参数不能多于启动的sentinel 实例数。哨兵sentinel 个数为奇数，选举嘛，奇数哨兵个才能选举成功，一般建议3 个

6. RedisSentinel 如何监控2 个redis 主节点呢

    sentinel monitor mymasterB 192.168.1.20 6379 2
       ……与上面一样…………。

7. 部署建议：

   - sentinel 节点应部署在多台物理机（线上环境）
   
   - 至少三个且奇数个sentinel 节点

   - 通过以上我们知道，3 个sentinel 可同时监控一个主节点或多个主节点 监听N 个主节点较多时，如果sentinel 出现异常，会对3个主节点有影响，同时还会造成sentinel 节点产生过多的网络连接， 一般线上建议还是， 3 个sentinel 监听一个主节点
   

###### sentinel 哨兵的API

命令：redis-cli -p 26379 //进入哨兵的命令模式，使用redis-cli 进入

- 查看redis 主节点相关信息

  26379>sentinel masters 或sentinel master mymaster 

- 查看从节点状态与相关信息

   26379>sentinel slaves mymaster

- 查sentinel 节点集合信息(不包括当前26379)

  26379>sentinel sentinels mymaster

- 对主节点强制故障转移，没和其它节点协商

  26379>sentinel failover mymaster

###### 客户端连接（redis-sentinel 例子工程）

 远程客户端连接时，要打开protected-mode no
 ./redis-cli -p 26380 shutdown //关闭
在使用工程redis-sentinel，调用jedis 查询的流程如下：

1. 将三个sentinel 的IP 和地址加入JedisSentinelPool
2. 根据IP 和地址创建JedisSentinelPool 池对象
3. 在这个对象创建完后，此时该对象已把redis 的主节点（ 此时sentinel monitor mymaster 必须写成192.168.42.111 6379 2 ， 不能为127.0.0.1，不然查询出来的主节点的IP 在客户端就变成了127.0.0.1，拿不到连接了）查询出来了，当客户准备发起查询请求时，调用pool.getResource()借用一个jedis 对象，内容包括主节点的IP 和端口；
4. 将得到jedis 对象后，可执行jedis.get(“age”)指令了……。

#### redis 集群

RedisCluster 是redis 的分布式解决方案，在3.0 版本后推出的方案，有效地解决了Redis 分布式的需求，当遇到单机内存、并发等瓶颈时，可使用此方案来解决这些问题

##### 分布式数据库概念

分布式数据库把整个数据按分区规则映射到多个节点，即把数据划分到多个节点上，每个节点负责整体数据的一个子集，比如我们库有900 条用户数据，有3 个redis 节点，将900 条分成3 份，分别存入到3 个redis 节点

###### 分区规则

常见的分区规则哈希分区和顺序分区，redis 集群使用了哈希分区，顺序分区暂用不到，不做具体说明；
rediscluster 采用了哈希分区的“虚拟槽分区”方式

- 哈希分区分节点取余
- 一致性哈希分区
- 虚拟槽分区

其它两种也不做介绍，有兴趣可以百度了解一下。

###### 虚拟槽分区(槽：slot)

RedisCluster 采用此分区，所有的键根据哈希函数(CRC16[key]&16383)映射到0－16383 槽内，共16384 个槽位，每个节点维护部分槽及槽所映射的键值数据
哈希函数: Hash()=CRC16[key]&16383 按位与槽与节点的关系如下
redis 用虚拟槽分区原因：

解耦数据与节点关系，节点自身维护槽映射关系，分布式存储
redisCluster 的缺陷：

1. 键的批量操作支持有限，比如mset, mget，如果多个键映射在不同的槽， 就不支持了
2. 键事务支持有限，当多个key 分布在不同节点时无法使用事务，同一节点是支持事务
3. 键是数据分区的最小粒度，不能将一个很大的键值对映射到不同的节点
4. 不支持多数据库，只有0，select 0
5. 复制结构只支持单层结构，不支持树型结构。

##### 集群环境搭建：

###### 手动安装

1. 在/usr/local/bin/clusterconf 目录,
   6389 为6379 的从节点，6390 为6380 的从节点，6391 为6381 的从节点

2. 分别修改6379、6380、7381、6389、6390、6391 配置文件
   port 6379 //节点端口
   cluster-enabled yes //开启集群模式
   cluster-node-timeout 15000 //节点超时时间（接收pong 消息回复的时间）
   cluster-config-file /usrlocalbin/cluster/data/nodes-6379.conf 集群内部配置文件
   其它节点的配置和这个一致，改端口即可

3. 配置完后，启动6 个redis 服务

###### 自动安装模式

在/usr/local 新建目录：ruby
下载链接：https://pan.baidu.com/s/1kWsf3Rh 密码：n3pc
从这个链接下载ruby-2.3.1.tar.gz 和redis-3.3.0.gem
tar -zxvf ruby-2.3.1.tar.gz

1. cd ruby-2.3.1
2. ./configure -prefix=/usr/local/ruby
3.  make && make install //过程会有点慢，大概5－10 分钟
4. 然后gem install -l redis-3.3.0.gem //没有gem 需要安装yum install gem
5. 准备好6 个节点，（ 注意不要设置requirepass ） , 将/usr/local/bin/clusterconf/data 的config-file 删除； 依次启动6 个节点：./redis-server clusterconf/redis6379.conf   如果之前redis 有数据存在，flushall 清空；(坑:不需要cluster meet ..)
6. 进入cd /usr/local/bin, 执行以下：1 代表从节点的个数
   ./redis-trib.rb create --replicas 1 192.168.0.111:6379 192.168.0.111:6380
   192.168.0.111:6381 192.168.0.111:6389 192.168.0.111:6390 192.168.0.111:6391
   主从分配，6379 是6389 的从节点
   貌似只有主节点可读写，从节点不可以
   主节点死后，从节点变成主节点
7. 集群健康检测：
   redis-trib.rb check 192.168.42.111:6379 (注：redis 先去注释掉requirepass，不然连不上)
   如此出现了这个问题，6379 的5798 槽位号被打开了
   解决如下：
   6379，6380，6381 的有部分槽位被打开了，分别进入这几个节点，执行
   6380:>cluster setslot 1180 stable
   cluster setslot 2998 stable
   cluster setslot 11212 stable
   其它也一样，分别执行修复完后：
   此时修复后的健康正常；
   当停掉6379 后，过会6389 变成主节点
   注意：使用客户端工具查询时要加-c
   ./redis-cli -h 192.168.42.111 -p 6379 -c
   mset aa bb cc dd,批设置对应在不同的solt 上，缺点

集群正常启动后，在每个redis.conf 里加上
masterauth “12345678”
requiredpass “12345678”
当主节点下线时，从节点会变成主节点，用户和密码是很有必要的，设置成一致
这上面是一主一从，那能不能一主多从呢？
./redis-trib.rb create --replicas 2
192.168.42.111:6379 192.168.42.111:6380 192.168.42.111:6381
192.168.42.111:6479 192.168.42.111:6480 192.168.42.111:6481
92.168.42.111:6579 192.168.42.111:6580 192.168.42.111:6581

##### 节点之间的通信

节点之间采用Gossip 协议进行通信，Gossip 协议就是指节点彼此之间不断通信交换信息
当主从角色变化或新增节点，彼此通过ping/pong 进行通信知道全部节点的最新状态并达到集群同步

###### Gossip 协议

Gossip 协议的主要职责就是信息交换，信息交换的载体就是节点之间彼此发送的Gossip 消息，常用的Gossip 消息有ping 消息、pong 消息、meet 消息、fail 消息meet 消息：用于通知新节点加入，消息发送者通知接收者加入到当前集群，meet 消息通信完后，接收节点会加入到集群中，并进行周期性ping pong 交换

ping 消息：集群内交换最频繁的消息，集群内每个节点每秒向其它节点发ping 消息，用于检测节点是在在线和状态信息，ping 消息发送封装自身节点和其他节点的状态
数据；pong 消息，当接收到ping meet 消息时，作为响应消息返回给发送方，用来确认正常通信，pong 消息也封闭了自身状态数据；
fail 消息：当节点判定集群内的另一节点下线时，会向集群内广播一个fail 消息，


###### 消息解析流程

所有消息格式为：消息头、消息体，消息头包含发送节点自身状态数据（比如节点ID、槽映射、节点角色、是否下线等），接收节点根据消息头可以获取到发送节点的相关数据。
消息解析流程：
选择节点并发送ping 消息：
Gossip 协议信息的交换机制具有天然的分布式特性，但ping pong 发送的频率很高，可以实时得到其它节点的状态数据，但频率高会加重带宽和计算能力，因此每次都会有目的性地选择一些节点； 但是节点选择过少又会影响故障判断的速度，redis 集群的Gossip 协议兼顾了这两者的优缺点，看下图：不难看出：节点选择的流程可以看出消息交换成本主要体现在发送消息的节点数量和每个消息携带的数据量

###### 流程说明：

1. 选择发送消息的节点数量：集群内每个节点维护定时任务默认为每秒执行10  次，每秒会随机选取5 个节点，找出最久没有通信的节点发送ping 消息，用来保证信息交换的随机性，每100 毫秒都会扫描本地节点列表，如果发现节点最近 一次接受pong 消息的时间大于cluster-node-timeout/2 则立刻发送ping 消息，这样做目的是防止该节点信息太长时间没更新，当我们宽带资源紧张时，在可 redis.conf 将cluster-node-timeout 15000 改成30 秒，但不能过度加大
2. 消息数据：节点自身信息和其他节点信息

##### 集群扩容

这也是分布式存储最常见的需求，当我们存储不够用时，要考虑扩容

扩容步骤如下：

1. 准备好新节点

2. 加入集群,迁移槽和数据

     1. 同目录下新增redis6382.conf、redis6392.conf 两
     
        启动两个新redis 节点
        ./redis-server clusterconf/redis6382.conf & （新主节点）
        ./redis-server clusterconf/redis6392.conf & （新从节点）
     
     2. 新增主节点
        ./redis-trib.rb add-node 192.168.42.111:6382 192.168.42.111:6379
        6379 是原存在的主节点，6382 是新的主节点
     
     3. 添加从节点
        redis-trib.rb add-node --slave --master-id
        03ccad2ba5dd1e062464bc7590400441fafb63f2 192.168.42.111:6392 192.168.42.111:6379
        --slave，表示添加的是从节点
        --master-id 03ccad2ba5dd1e062464bc7590400441fafb63f2 表示主节点6382 的
        master_id
        192.168.42.111:6392,新从节点
        192.168.42.111:6379 集群原存在的旧节点
     
     4. redis-trib.rb reshard 192.168.42.111:6382 //为新主节点重新分配solt
        How many slots do you want to move (from 1 to 16384)? 1000 //设置slot 数1000
        What is the receiving node ID? 464bc7590400441fafb63f2 //新节点node id
        Source node #1:all //表示全部节点重新洗牌
        新增完毕！

##### 集群减缩节点
1. 集群同时也支持节点下线掉
   下线的流程如下：

   1. 确定下线节点是否存在槽slot,如果有，需要先把槽迁移到其他节点，保证整个
           集群槽节点映射的完整性；=

   2. 当下线的节点没有槽或本身是从节点时，就可以通知集群内其它节点（或者叫忘记节点），当下线节点被忘记后正常关闭。
      删除节点也分两种：
      一种是主节点6382，一种是从节点6392。
      在从节点6392 中，没有分配哈希槽，执行
       ./redis-trib.rb del-node 192.168.42.111:6392 7668541151b4c37d2d9 有两个参数ip：port和节点的id。从节点6392 从集群中删除了。
      主节点6382 删除步骤：

      1. ./redis-trib.rb reshard 192.168.42.111:6382
         问我们有多少个哈希槽要移走，因为我们这个节点上刚分配了1000 个所以我们这里输入1000

      2. 最后
         ./redis-trib.rb del-node 192.168.42.111:6382 3e50c6398c75e0088a41f908071c2c2eda1dc900
         此时节点下线完成……

         

   请求路由重定向
   我们知道，在redis 集群模式下，redis 接收的任何键相关命令首先是计算这个键CRC值，通过CRC 找到对应的槽位，再根据槽找到所对应的redis 节点，如果该节点是本身，则直接处理键命令；如果不是，则回复键重定向到其它节点，这个过程叫做
   MOVED 重定向

   
   
   
   
##### 故障转移

   redis 集群实现了高可用，当集群内少量节点出现故障时，通过故障转移可以保证集群正常对外提供服务。当集群里某个节点出现了问题，redis 集群内的节点通过ping pong 消息发现节点是否健康，是否有故障，其实主要环节也包括了主观下线和客观下线；
   主观下线：指某个节点认为另一个节点不可用，即下线状态，当然这个状态不是最终的
   故障判定，只能代表这个节点自身的意见，也有可能存在误判；
   下线流程：

   1. 节点a 发送ping 消息给节点b ,如果通信正常将接收到pong 消息，节点a 更新最近一次与节点b 的通信时间；
   2. 如果节点a 与节点b 通信出现问题则断开连接，下次会进行重连，如果一直通信失败，则它们的最后通信时间将无法更新；
   3. 节点a 内的定时任务检测到与节点b 最后通信时间超过cluster_note-timeout 时，更新本地对节点b 的状态为主观下线（pfail）
      客观下线：指真正的下线，集群内多个节点都认为该节点不可用，达成共识，将它下线，如果下线的节点为主节点，还要对它进行故障转移假如节点a 标记节点b 为主观下线，一段时间后节点a 通过消息把节点b 的状态发到其它节点，当节点c 接受到消息并解析出消息体时，会发现节点b 的pfail 状态时，会触发客观下线流程；当下线为主节点时，此时redis 集群为统计持有槽的主节点投票数是否达到一半，当下线报告统计数大于一半时，被标记为客观下线状态。

​     

##### 故障恢复

故障主节点下线后，如果下线节点的是主节点，则需要在它的从节点中选一个替换它，保证集群的高可用；转移过程如下：

1. 资格检查：检查该从节点是否有资格替换故障主节点，如果此从节点与主节点 断开过通信，那么当前从节点不具体故障转移；
2. 准备选举时间：当从节点符合故障转移资格后，更新触发故障选举时间，只有到达该时间后才能执行后续流程；
3. 发起选举：当到达故障选举时间时，进行选举；
4. 选举投票：只有持有槽的主节点才有票，会处理故障选举消息，投票过程其实是一个领导者选举（选举从节点为领导者）的过程，每个主节点只能投一张票给从节点，当从节点收集到足够的选票（大于N/2+1）后，触发替换主节点操作，撤销原故障主节点的槽，委派给自己，并广播自己的委派消息，通知集群内所有节点。