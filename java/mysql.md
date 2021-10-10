#### Mysql

##### 安装

###### 单实例

1. 解压

   解压mysql到/usr/local目录

   `tar -zxvf mysql-5.7.9-linux-glibc2.5-x86_64.tar.gz` 

2. 具体安装

   安装需要的依赖

   `yum install -y libaio`

   ```shell
   shell> groupadd mysql
   shell> useradd -r -g mysql mysql
   shell> cd /usr/local
   shell> tar zxvf /path/to/mysql-VERSION-OS.tar.gz
   shell> ln -s full-path-to-mysql-VERSION-OS mysql
   shell> cd mysql
   shell> mkdir mysql-files
   shell> chmod 770 mysql-files
   shell> chown -R mysql .
   shell> chgrp -R mysql .
   shell> bin/mysqld --initialize --user=mysql     # MySQL 5.7.6 and up
   shell> bin/mysql_ssl_rsa_setup              # MySQL 5.7.6 and up
   shell> chown -R root .
   shell> chown -R mysql data mysql-files
   shell> bin/mysqld_safe --user=mysql &
   # Next command is optional
   shell> cp support-files/mysql.server /etc/init.d/mysql.server
   ```

3. 配置环境变量

   `export PATH=/usr/local/mysql/bin:$PATH`

4. 配置开启启动

   ```shell
   chkconfig mysql.server on
   chkconfig --list
   ```

5. 登陆，修改密码

   ```sql
   set password = 'root1234%'
   ```

6. 允许远程登陆

   ```sql
   GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'root1234%'
   GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'root1234%' WITH GRANT OPTION;
   flush privileges;
   ```

- 启动的时候可能会报错

  这是因为mysql启动的时候需要配置文件，而在安装centos的时候，哪怕是mini版本都会有个默认的配置在/etc目录中

  `/usr/local/mysql/bin/mysqld --verbose --help |grep -A 1 'Default options'`

  Default options are read from the following files in the given order:

  `/etc/my.cnf /etc/mysql/my.cnf /usr/local/mysql/etc/my.cnf ~/.my.cnf`

  Mysql启动的时候会以上面所述的顺序加载配置文件

  如果报错，先重命名my.cnf文件

###### 多实例

新建 /etc/my.cnf 配置如下

```
[mysqld]
sql_mode = "STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER"
[mysqld_multi]
mysqld = /usr/local/mysql/bin/mysqld_safe
mysqladmin = /usr/local/mysql/bin/mysqladmin
log = /var/log/mysqld_multi.log
[mysqld1] 
server-id = 11
socket = /tmp/mysql.sock1
port = 3307
datadir = /data1
user = mysql
performance_schema = off
innodb_buffer_pool_size = 32M
skip_name_resolve = 1
log_error = error.log
pid-file = /data1/mysql.pid1
[mysqld2]
server-id = 12
socket = /tmp/mysql.sock2
port = 3308
datadir = /data2
user = mysql
performance_schema = off
innodb_buffer_pool_size = 32M
skip_name_resolve = 1
log_error = error.log
pid-file = /data2/mysql.pid2
```

1. 创建2个数据目录

   ```shell
   mkdir /data1
   mkdir /data2
   
   chown mysql.mysql /data{1..2}
   
   mysqld --initialize --user=mysql --datadir=/data1
   mysqld --initialize --user=mysql --datadir=/data2
   
   cp /usr/local/mysql/support-files/mysqld_multi.server /etc/init.d/mysqld_multid
   ```

2. 配置开机启动

   `chkconfig mysqld_multid on`

3. 查看状态

   `mysqld_multi report`

   - 如发现还需要perl的环境，安装

     `yum -y install perl perl-devel`

4. 启动，分别修改密码，允许远程连接

   ```shell
   mysqld_multi start
   
   mysql -u root -S /tmp/mysql.sock1 -P 3307
   mysql -u root -S /tmp/mysql.sock1 -P 3308
   ```

   ```sql
   set password = 'root1234%';
   GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'root1234%';
   flush privileges;  
   ```

##### Mysql权限

###### 用户标识

在mysql中的权限不是单纯的赋予给用户的，而是赋予给”用户+IP”的

比如dev用户是否能登陆，用什么密码登陆，并且能访问什么数据库等都需要加上IP，这样才算一个完整的用户标识

###### 用户权限所涉及的表

- mysql.user

  一行记录代表一个用户标识

- mysql.db

  一行记录代表对数据库的权限

- mysql.table_priv

  一行记录代表对表的权限

- mysql_column_priv

  一行记录代表对某一列的权限

mysql其实权限并不事特别low，权限的粒度甚至到了某一列上

###### Mysql角色

先把check_proxy_users，mysql_native_password_proxy_users这两个变量设置成true,可写到配置文件里

```
set GLOBAL check_proxy_users =1;
set GLOBAL mysql_native_password_proxy_users = 1;
```

1. 创建角色

   `create USER 'dev_role'`

2. 创建开发用户

   ```sql
   create USER 'deer'
   create USER 'enjoy'
   ```

3. 将用户添加到组里

   ```sql
   grant proxy on 'dev_role' to 'deer'
   grant proxy on 'dev_role' to 'enjoy'
   ```

   如果你是远程链接，你可能会收获一个大大的错误，你没有权限做这一步，这个时候你需要再服务器上执行一条

    `GRANT PROXY ON ''@'' TO 'root'@'%' WITH GRANT OPTION;`

4. 给角色应该有的权限

   ```sql
   grant select(id,name) on mall.account to 'dev_role'
   ```

###### Mysql数据类型

1. INT类型

   | **类型**  |      | **字节** | **最小值**           | **最大值**           |
   | --------- | ---- | -------- | -------------------- | -------------------- |
   |           |      |          | (带符号的/无符号的)  | (带符号的/无符号的)  |
   | TINYINT   |      | 1        | -128                 | 127                  |
   |           |      |          | 0                    | 255                  |
   | SMALLINT  |      | 2        | -32768               | 32767                |
   |           |      |          | 0                    | 65535                |
   | MEDIUMINT |      | 3        | -8388608             | 8388607              |
   |           |      |          | 0                    | 16777215             |
   | INT       |      | 4        | -2147483648          | 2147483647           |
   |           |      |          | 0                    | 4294967295           |
   | BIGINT    |      | 8        | -9223372036854775808 | 9223372036854775807  |
   |           |      |          | 0                    | 18446744073709551615 |

   - 有无符号

     在项目中使用BIGINT，而且是有符号的。

     ```sql
     create table test_unsigned(a int unsigned, b int unsigned);
     insert into test_unsigned values(1, 2);
     select b - a from test_unsigned;
     select a - b from test_unsigned;  --运行出错
     ```

   - INT(N)

     ```sql
     create table test_int_n(a int(4) zerofill);
      insert into test_int_n values(1);
      insert into test_int_n values(123456);
     ```

     - int(N)中的 N 是显示宽度， 不表示 存储的数字的 长度 的上限。
     - zerofill 表示当存储的数字 长度 < N 时，用 数字0 填充左边，直至补满长度 N
     - 当存储数字的长度 超过N时 ，按照 实际存储 的数字显示

   - 自动增长的面试题

     这列语法有错误吗？

      create table test_auto_increment(a int auto_increment);  // 错误,自增必须使用组件

      create table test_auto_increment(a int auto_increment primary key);

      

     以下结果是什么？

     insert into test_auto_increment values(NULL);  //  自增数据

     insert into test_auto_increment values(0);   //  自增数据

     insert into test_auto_increment values(-1);   //  自增数据

     insert into test_auto_increment values(null),(100),(null),(10),(null)   //  自增数据

2. 字符类型

   | 类型          | 说明           | N的含义 | 是否有字符集 | 最大长度 |
   | ------------- | -------------- | ------- | ------------ | -------- |
   | CHAR(N)       | 定长字符       | 字符    | 是           | 255      |
   | VARCHAR(N)    | 变长字符       | 字符    | 是           | 16384    |
   | BINARY(N)     | 定长二进制字节 | 字节    | 否           | 255      |
   | VARBINARY(N)  | 变长二进制字节 | 字节    | 否           | 16384    |
   | TINYBLOB(N)   | 二进制大对象   | 字节    | 否           | 256      |
   | BLOB(N)       | 二进制大对象   | 字节    | 否           | 16K      |
   | MEDIUMBLOB(N) | 二进制大对象   | 字节    | 否           | 16M      |
   | LONGBLOB(N)   | 二进制大对象   | 字节    | 否           | 4G       |
   | TINYTEXT(N)   | 大对象         | 字节    | 是           | 256      |
   | TEXT(N)       | 大对象         | 字节    | 是           | 16K      |
   | MEDIUMTEXT(N) | 大对象         | 字节    | 是           | 16M      |
   | LONGTEXT(N)   | 大对象         | 字节    | 是           | 4G       |

   排序规则 （schema 建立时可以设置是否区分大小写）

   select 'a' = 'A';

   create table test_ci (a varchar(10), key(a));

   insert into test_ci values('a');

   insert into test_ci values('A');

    

   select * from test_ci where a = 'a';  --结果是什么？

   set names utf8mb4 collate utf8mb4_bin

3. 时间类型

   | 日期类型  | 占用空间 | 表示范围                                         |
   | --------- | -------- | ------------------------------------------------ |
   | DATETIME  | 8        | 1000-01-01 00:00:00 ~ 9999-12-31 23:59:59        |
   | DATE      | 3        | 1000-01-01 ~ 9999-12-31                          |
   | TIMESTAMP | 4        | 1970-01-01 00:00:00UTC ~ 2038-01-19  03:14:07UTC |
   | YEAR      | 1        | YEAR(2):1970-2070, YEAR(4):1901-2155             |
   | TIME      | 3        | -838:59:59 ~ 838:59:59                           |

   datatime与timestamp区别 : datatime有时区，timestamp 无时区

   create table test_time(a timestamp, b datetime);

   insert into test_time values (now(), now());

   select * from test_time;

   select @@time_zone;

   set time_zone='+00:00';

   select * from test_time;

4. JSON类型

   - 基本操作

     新建表

     ```sql
     create table json_user (
      uid int auto_increment,
      data json,
      primary key(uid)
      );
     ```

     插入数据

     ```json
     insert into json_user values (
     null, '{
        "name":"lison",
     "age":18,
     "address":"enjoy"
        }' );
     
      insert into json_user values (
      null,
      '{
       "name":"james",
       "age":28,
       "mail":"james@163.com"
      }');
     ```

   - JSON函数

     1. Json_extract 抽取

        ```sql
        select
         json_extract(data, '$.name'), 
         json_extract(data, '$.address') 
         from json_user;
        ```

     2. json_object 将对象转为json

        ```sql
        select json_object("name", "enjoy", "email", "enjoy.com", "age",35);
        ```

        ```sql
        insert into json_user values ( 
         null,
          json_object("name", "enjoy", "email", "enjoy.com", "age",35) );
        ```

     3. Json_insert 插入数据

        语法：`JSON_INSERT(json_doc, path, val[, path, val] ...)`

        ```sql
         set @json = '{ "a": 1, "b": [2, 3]}';
         select json_insert(@json, '$.a', 10, '$.c', '[true, false]');
         update json_user set data = json_insert(data, "$.address_2", "xiangxue") where uid = 1;
        ```

     4. json_merge 合并数据并返回

        ```sql
        select json_merge('{"name": "enjoy"}', '{"id": 47}');
        
         select
         json_merge(
         json_extract(data, '$.address'), 
         json_extract(data, '$.address_2')) 
         from json_user where uid = 1;
        ```

5. JSON索引

   JSON 类型数据本身无法直接创建索引，需要将需要索引的 JSON数据重新生成虚拟列(Virtual Columns) 之后，对该列进行索引

   ```sql
    create table test_inex_1(
    data json,
    gen_col varchar(10) generated always as (json_extract(data, '$.name')), 
   index idx (gen_col) 
    );
   insert into test_inex_1(data) values ('{"name":"king", "age":18, "address":"cs"}');
   insert into test_inex_1(data) values ('{"name":"peter", "age":28, "address":"zz"}');
   ```

##### Mysql架构

######  连接层

当MySQL启动（MySQL服务器就是一个进程），等待客户端连接，每一个客户端连接请求，服务器都会新建一个线程处理（如果是线程池的话，则是分配一个空的线程），每个线程独立，拥有各自的内存处理空间

`show VARIABLES like '%max_connections%'`

连接到服务器，服务器需要对其进行验证，也就是用户名、IP、密码验证，一旦连接成功，还要验证是否具有执行某个特定查询的权限（例如，是否允许客户端对某个数据库某个表的某个操作）

###### SQL处理层

这一层主要功能有：SQL语句的解析、优化，缓存的查询，MySQL内置函数的实现，跨存储引擎功能（所谓跨存储引擎就是说每个引擎都需提供的功能（引擎需对外提供接口）），例如：存储过程、触发器、视图等。

1. 如果是查询语句（select语句），首先会查询**<u>缓存</u>**是否已有相应结果，有则返回结果，无则进行下一步（如果不是查询语句，同样调到下一步）
2. 解析查询，创建一个内部数据结构（解析树），这个解析树主要用来SQL语句的语义与语法解析
3. 优化：优化SQL语句，例如重写查询，决定表的读取顺序，以及选择需要的索引等。这一阶段用户是可以查询的，查询服务器优化器是如何进行优化的，便于用户重构查询和修改相关配置，达到最优化。这一阶段还涉及到存储引擎，优化器会询问存储引擎，比如某个操作的开销信息、是否对特定索引有查询优化等。

- 缓存

  `show variables like '%query_cache_type%'`  -- 默认不开启

  `show variables like '%query_cache_size%'` --默认值1M

  `SET GLOBAL query_cache_type = 1;` --会报错

  `query_cache_type`只能配置在my.cnf文件中，这大大限制了qc的作用

  在生产环境建议不开启，除非经常有sql完全一模一样的查询

  **QC严格要求2次SQL请求要完全一样，包括SQL语句，连接的数据库、协议版本、字符集等因素都会影响**

- 解析查询

  ```sql
  SELECT DISTINCT 
  <select_list> 
  FROM 
  <left_tabole> <join_type>
  JOIN 
  <right_table> ON <join_condition>
  WHERE
  <where_condition>
  GROUP BY
  <group_by_list>
  HAVING
  <having_condition>
  ORDER BY
  <order_by_condition> LIMIT <limit_number>
  ```

  解析后

  FROM(笛卡尔积)  > ON(主表保留) > JOIN(不符合ON也添加) , WHERE(非聚集，非SELECT别名) > GROUP BY(改变对表引用) > HAVING(只作用分组后) > SELECT(DISTINCT) > ORDER BY(可使用SELECT别名) > LIMIT(rows, offset)

- 优化

  一个sql并不一定会去查询物理数据，sql解析器会通过优化器来优化程序员写的sql

- 逻辑架构

  在mysql中其实还有个schema的概念，这概念没什么太多作用，只是为了兼容其他数据库，所以也提出了这个。

  在mysql中 database 和schema是等价的

###### 物理存储结构

1. 数据库的数据库（DataDir）

   mysql安装的时候都要指定datadir,其查看方式为：

   `show VARIABLES like 'datadir'`，其规定所有建立的数据库存放位置

2. 数据库

   创建了一个数据库后，会在上面的datadir目录新建一个子文件夹

3. 表文件

   用户建立的表都会在上面的目录中，它和具体的存储引擎相关，但有个共同的就是都有个frm文件，它存放的是表的数据格式。

   `mysqlfrm --diagnostic /usr/local/mysql/data/mall/account.frm` 

4. mysql utilities 安装

   ```shell
   tar -zxvf mysql-utilities-1.6.5.tar.gz 
   
   cd mysql-utilities-1.6.5
   
   python ./setup.py build
   
   python ./setup.py install
   ```

##### 存储引擎

```sql
#看你的mysql现在已提供什么存储引擎:

 mysql> show engines;

 #看你的mysql当前默认的存储引擎:

 mysql> show variables like '%storage_engine%';
```

###### MyISAM（非聚集索引）

MySql 5.5之前默认的存储引擎

MyISAM 存储引擎由MYD（data）和MYI（index）组成

由于现在innodb越来越强大，myisam已经停止维护**(绝大多数场景都不适合)**

```sql
create table testmysam (
 id int PRIMARY key
) ENGINE=myisam 
insert into testmysam VALUES(1),(2),(3)
```

1. 表压缩

   `myisampack -b -f /usr/local/mysql/data/mall/testmysam.MYI`

   压缩后再往表里面新增数据就新增不了

   压缩后，需要

   myisamchk -r -f /usr/local/mysql/data/mall/testmysam.MYI

2. 适用场景

   - 非事务型应用（数据仓库，报表，日志数据）
   - 只读类应用
   - 空间类应用（空间函数，坐标）

###### Innodb

目前默认是独立表空间 innodb_file_per_table = on; (off 为系统表空间)

- Innodb是一种事务性存储引擎
- 完全支持事务的ACID特性
- RedoLog 和 UndoLog
- Innodb 支持行级锁

| 对比项 | MyISAM                                                 | InnoDB                                                       |
| ------ | ------------------------------------------------------ | ------------------------------------------------------------ |
| 主外键 | 不支持                                                 | 支持                                                         |
| 事务   | 不支持                                                 | 支持                                                         |
| 行表锁 | 表锁，即使操作一条记录也会锁住整个表，不适合高并发操作 | 行锁，操作时只锁某一行，不对其它行有影响，适合高并发的操作   |
| 缓存   | 只缓存索引，不缓存真实数据                             | 不仅缓存索引还要缓存真实数据，对内存要求较高，而且内存大小对性能决定性影响 |

`show VARIABLES like 'innodb_log_buffer_size'` 

###### CSV

- 以csv格式进行数据存储
- 所有列都不能为null的
- 不支持索引（不适合大表，不适合在线处理）
- 可以对数据文件直接编辑（保存文本文件内容）

```sql
create table mycsv(id int not null,c1 VARCHAR(10) not null,c2 char(10) not null) engine=csv;

insert into mycsv values(1,'aaa','bbb'),(2,'cccc','dddd');

vi /usr/local/mysql/data/mall/mycsv.CSV 修改文本数据 

flush TABLES;

select * from mycsv
create index idx_id on mycsv(id)
```

###### Archive

- 组成

  以zlib对表数据进行压缩，磁盘I/O更少

  数据存储在ARZ为后缀的文件中

- 特点

  只支持insert和select操作，只允许在自增ID列上加索引

```sql
create table myarchive(id int auto_increment not null,c1 VARCHAR(10),c2 char(10), key(id)) engine = archive;

create index idx_c1 on myarchive(c1)

INSERT into myarchive(c1,c2) value('aa','bb'),('cc','dd');

delete from myarchive where id = 1

update myarchive set c1='aaa' where id = 1
```

###### Memory

- 特点

  - 文件系统存储特点,也称HEAP存储引擎，所以数据保存在内存中
  - 支持HASH索引和BTree索引
  - 所有字段都是固定长度 varchar(10) = char(10)
  - 不支持Blog 和 Text 等大字段
  - Memory存储引擎使用表级锁
  - 最大大小由max_heap_table_size 参数决定

  ```sql
  show VARIABLES like 'max_heap_table_size'
  
  create table mymemory(id int,c1 varchar(10),c2 char(10),c3 text) engine = memory;
  create table mymemory(id int,c1 varchar(10),c2 char(10)) engine = memory;
  create index idx_c1 on mymemory(c1);
  create index idx_c2 using btree on mymemory(c2);
  show index from mymemory
  show TABLE status LIKE ‘mymemory’
  
  ```

- 与临时表的区别

  临时表在系统使用临时表时，超过限制使用Myisam临时表，未超过限制使用Memory表；在create temporary table 建立临时表

- 使用场景

  - hash索引用于查找或者是映射表（邮编和地区的对应表）
  - 用于保存数据分析中产生的中间表
  - 用于缓存周期性聚合数据的结果表

###### Ferderated

- 提供了访问远程MySQL服务器上表的方法
- 本地不存储数据，数据全部放到远程服务器上
- 本地需要保存表结构和远程服务器的连接信息

使用场景

偶尔的统计分析及手工查询（某些游戏行业）

默认禁止，启用需要再启动时增加federated参数

##### Mysql 中的锁

- 表级锁：开销小，加锁快；不会出现死锁；锁定粒度大，发生锁冲突的概率最高,并发度最低
- 行级锁：开销大，加锁慢；会出现死锁；锁定粒度最小，发生锁冲突的概率最低,并发度也最高
- 页面锁(gap锁,间隙锁)：开销和加锁时间界于表锁和行锁之间；会出现死锁；锁定粒度界于表锁和行锁之间，并发度一般

使用场景：

- 表级锁：以查询为主，只有少量按索引条件更新数据的应用，如OLAP系统
- 行级锁：适合于有大量按索引条件并发更新少量不同数据，同时又有并发查询的应用，如一些在线事务处理（OLTP）系统。

###### MyISAM锁

MySQL的表级锁有两种模式：

- 表共享读锁（Table Read Lock）
- 表独占写锁（Table Write Lock）

| 请求锁模式是否兼容当前锁模式 | None | 读锁 | 写锁 |
| ---------------------------- | ---- | ---- | ---- |
| 读锁                         | 是   | 是   | 否   |
| 写锁                         | 是   | 否   | 否   |

1. 共享读锁

   lock table table_name read

   - 对MyISAM表的读操作，不会阻塞其他用户对同一个表的读请求，但会阻塞对同一个表的写请求
   - 对MyISAM表的读操作，不会阻塞当前session对表读，当对表进行修改会报错
   - 一个session使用LOCK TABLE命令给表f叫了读锁，这个session可以查询锁定表中的记录，但更新或访问其他表都会提示错误

2. 独占写锁

   lock table table_name write

   - 对MyISAM表的写操作，则会阻塞其他用户对同一表的读和写操作；
   - 对MyISAM表的写操作，当前session可以对本表做CRUD，但对其他表进行操作会报错；

###### InooDB锁

在mysql的innoDB引擎支持行锁

- 共享锁（读锁）：当一个事务对某几行上读锁时，允许其它事务对这几行进行读操作，但不允许其进行写操作，也不允许其他事务给这几行上排他锁，但允许上读锁；
- 排它锁（写锁）：当一个事务对某几行上写锁时，不允许其他事务写，但允许读，更不允许其他事务给这几行上任何锁；

1. 语法

   上共享锁的写法：`lock in share mode`

   例如： select * from 表 where 条件 lock in share mode；

   上排它锁的写法：`for update`

   例如：select * from 表 where 条件 for update；

2. 注意

   1. 两个事务不能锁同一个索引
   2. insert，delete，update在事务中都会自动默认加上排他锁
   3. 行锁必须有索引才能实现，否则会自动锁全表，那么就不是行锁。             

3. 锁的等待问题

   查询线程id，kill 线程id

   5.7

   `select * from sys.innodb_lock_waits`

   kill thread_num

   5.6

   ```sql
   SELECT  r.trx_id waiting_trx_id,  r.trx_mysql_thread_id waiting_thread,  r.trx_query waiting_query,  b.trx_id blocking_trx_id,  b.trx_mysql_thread_id blocking_threadFROM  information_schema.innodb_lock_waits wINNER JOIN  information_schema.innodb_trx b ON b.trx_id = w.blocking_trx_idINNER JOIN  information_schema.innodb_trx r ON r.trx_id = w.requesting_trx_id;
   ```

   kill thread_num

##### 事务

现在的很多软件都是多用户，多程序，多线程的，对同一个表可能同时有很多人在用，为保持数据的一致性，所以提出了事务的概念。

###### 事务特性

事务应该具有4个属性：原子性、一致性、隔离性、持久性。这四个属性通常称为ACID特性。

- 原子性（atomicity）

  一个事务必须被视为一个不可分割的最小单元，整个事务中的所有操作要么全部提交成功，要么全部失败，对于一个事务来说，不可能只执行其中的一部分操作

- 一致性（consistency）

  一致性是指事务将数据库从一种一致性转换到另外一种一致性状态，在事务开始之前和事务结束之后数据库中数据的完整性没有被破坏

- 持久性（durability）

  一旦事务提交，则其所做的修改就会永久保存到数据库中。此时即使系统崩溃，已经提交的修改数据也不会丢失

- 隔离性（isolation）

  一个事务的执行不能被其他事务干扰。即一个事务内部的操作及使用的数据对并发的其他事务是隔离的，并发执行的各个事务之间不能互相干扰。

  （对数据库的并行执行，应该像串行执行一样）

  - 未提交读（READ UNCOMMITED）脏读
  - 已提交读 （READ COMMITED）不可重复读
  - 可重复读（REPEATABLE READ）
  - 可串行化（SERIALIZABLE）

  mysql默认的事务隔离级别为repeatable-read

  show variables like '%tx_isolation%'; 

###### 事务并发问题

- 脏读：事务A读取了事务B更新的数据，然后B回滚操作，那么A读取到的数据是脏数据
- 不可重复读：事务 A 多次读取同一数据，事务 B 在事务A多次读取的过程中，对数据作了更新并提交，导致事务A多次读取同一数据时，结果不一致
- 幻读：系统管理员A将数据库中所有学生的成绩从具体分数改为ABCDE等级，但是系统管理员B就在这个时候插入了一条具体分数的记录，当系统管理员A改结束后发现还有一条记录没有改过来，就好像发生了幻觉一样，这就叫幻读

不可重复读的和幻读很容易混淆，不可重复读侧重于修改，幻读侧重于新增或删除。解决不可重复读的问题只需锁住满足条件的行，解决幻读需要锁表

其实在mysql中，可重复读已经解决了幻读问题，借助的就是间隙锁

###### 事务语法

1. 开启事务

   1、begin 

   2、START TRANSACTION（推荐）

   3、begin work 

2. 事务回滚

   rollback

3. 事务提交

   commit

4. 还原点

   savepoint 

   show variables like '%autocommit%'; 自动提交事务是开启的

   set autocommit=0;

##### 业务设计

###### 逻辑设计

- 范式设计

  1. 数据库表中的所有字段都只具有单一属性

     单一属性的列是由基本数据类型所构成的

     设计出来的表都是简单的二维表

  2. 要求表中只具有一个业务主键，也就是说符合第二范式的表不能存在非主键列只对部分主键的依赖关系

- 反范式设计

  - 反范式化是针对范式化而言得，在前面介绍了数据库设计得范式
  - 所谓得反范式化就是为了性能和读取效率得考虑而适当得对数据库设计范式得要求进行违反
  - 允许存在少量得冗余，换句话来说反范式化就是使用空间来换取时间

###### 物理设计

- 命名规范

  使用大小写来格式化的库对象名字以获得良好的可读性

  1. 数据库，表，字段的命名要遵守可读性原则
  2. 数据库，表，字段的命名要遵守表意性原则
  3. 数据库，表，字段的命名要遵守长名原则

- 存储引擎选择

  | 对比项   | MyISAM                                                   | InnoDB                                                       |
  | -------- | -------------------------------------------------------- | ------------------------------------------------------------ |
  | 主外键   | 不支持                                                   | 支持                                                         |
  | 事务     | 不支持                                                   | 支持                                                         |
  | 行表锁   | 表锁，即使操作一条记录也会锁住整个表，不适合高并发的操作 | 行锁，操作时只锁某一行，不对其他行有影响，适合高并发的操作   |
  | 缓存     | 只缓存索引，不缓存真实数据                               | 不仅缓存索引还要缓存真实数据，对内存要求较高，而且内存大小对性能有决定性的影响 |
  | 表空间   | 小                                                       | 大                                                           |
  | 关注点   | 性能                                                     | 事务                                                         |
  | 默认安装 | Y                                                        | Y                                                            |

- 数据类型选择

  当一个列可以选择多种数据类型时

  - 优先考虑数字类型
  - 其次是日期，时间类型
  - 最后是字符类型
  - 对于相同级别的数据类型，应该优先选择占用空间小的数据类型

  浮点类型

  | 列类型  | 存储空间                            | 是否精确类型 |
  | ------- | ----------------------------------- | ------------ |
  | FLOAT   | 4个字节                             | 否           |
  | DOUBLE  | 8个字节                             | 否           |
  | DECIMAL | 每4个字节存9个数字，小数点占1个字节 | 是           |

  日期类型

  | 类型      | 大小（字节） | 范围                                    | 格式                | 用途                     |
  | --------- | ------------ | --------------------------------------- | ------------------- | ------------------------ |
  | DATETIME  | 8            | 1000-01-01 00:00:00/9999-12-31 23:59:59 | YYYY-MM-DD HH:MM:SS | 混合日期和时间值         |
  | TIMESTAMP | 4            | 1970-01-01 00:00:00/2037 年某时         | YYYYMMDDHHMMSS      | 混合日期和时间值，时间戳 |

  **timestamp** **和时区有关，而datetime和时区无关**

##### 慢查询

慢查询日志，顾名思义，就是查询慢的日志，是指mysql记录所有执行超过long_query_time参数设定的时间阈值的SQL语句的日志。该日志能为SQL语句的优化带来很好的帮助。默认情况下，慢查询日志是关闭的，要使用慢查询日志功能，首先要开启慢查询日志功能。

###### 慢查询配置

- slow_query_log 启动停止技术慢查询日志
- slow_query_log_file 指定慢查询日志得存储路径及文件（默认和数据文件放一起）
- ong_query_time 指定记录慢查询日志SQL执行时间得伐值（单位：秒，默认10秒）
- log_queries_not_using_indexes 是否记录未使用索引的SQL
- log_output 日志存放的地方【TABLE】【FILE】【FILE,TABLE】

配置了慢查询后，它会记录符合条件的SQL 

`cat /usr/local/mysql/data/mysql-slow.log` 

包括：

- 查询语句
- 数据修改语句
- 已经回滚得SQL 

通过如下命令可查看相关配置

```sql
show VARIABLES like '%slow_query_log%'

show VARIABLES like '%slow_query_log_file%'

show VARIABLES like '%long_query_time%'

show VARIABLES like '%log_queries_not_using_indexes%'

show VARIABLES like 'log_output'
set global long_query_time=0;   ---默认10秒，这里为了演示方便设置为0 

set GLOBAL  slow_query_log = 1; --开启慢查询日志

set global log_output='FILE,TABLE'  --项目开发中日志只能记录在日志文件中，不能记表中
```

###### 慢查询解读

第一行：用户名 、用户的IP信息、线程ID号

第二行：执行花费的时间【单位：毫秒】

第三行：执行获得锁的时间

第四行：获得的结果行数

第五行：扫描的数据行数

第六行：这SQL执行的具体时间

第七行：具体的SQL语句

慢查询分析

慢查询的日志记录非常多，要从里面找寻一条查询慢的日志并不是很容易的事情，一般来说都需要一些工具辅助才能快速定位到需要优化的SQL语句，下面介绍两个慢查询辅助工具

- Mysqldumpslow

  常用的慢查询日志分析工具，汇总除查询条件外其他完全相同的SQL，并将分析结果按照参数中所指定的顺序输出

  **语法：**

  mysqldumpslow -s r -t 10 slow-mysql.log

  -s order (c,t,l,r,at,al,ar) 

  ​     c:总次数

  ​     t:总时间

  ​     l:锁的时间

  ​     r:总数据行

  ​     at,al,ar :t,l,r平均数 【例如：at = 总时间/总次数】

   -t top  指定取前面几天作为结果输出

  mysqldumpslow -s t -t 10 

  /usr/local/mysql/data/mysql-slow.log 

- pt_query_digest

  是用于分析mysql慢查询的一个工具，与mysqldumpshow工具相比，py-query_digest 工具的分析结果更具体，更完善。

  有时因为某些原因如权限不足等，无法在服务器上记录查询。这样的限制我们也常常碰到。

  yum -y install 'perl(Data::Dumper)';

  yum -y install perl-Digest-MD5

  yum -y install perl-DBI

  yum -y install perl-DBD-MySQL

  perl ./pt-query-digest --explain h=127.0.0.1,u=root,p=root1234% /usr/local/mysql/data/mysql-slow.log

  汇总的信息【总的查询时间】、【总的锁定时间】、【总的获取数据量】、【扫描的数据量】、【查询大小】

  Response: 总的响应时间。

  time: 该查询在本次分析中总的时间占比。

  calls: 执行次数，即本次分析总共有多少条这种类型的查询语句。

  R/Call: 平均每次执行的响应时间。

  Item : 查询对象

  **语法及重要选项**

  pt-query-digest [OPTIONS] [FILES] [DSN]

  - --create-review-table 当使用--review参数把分析结果输出到表中时，如果没有表就自动创建。
  - --create-history-table 当使用--history参数把分析结果输出到表中时，如果没有表就自动创建。
  - --filter 对输入的慢查询按指定的[字符串](http://www.php.cn/wiki/57.html)进行匹配过滤后再进行分析
  - --limit 限制输出结果百分比或数量，默认值是20,即将最慢的20条语句输出，如果是50%则按总响应时间占比从大到小排序，输出到总和达到50%位置截止
  - --host mysql服务器地址
  - --user mysql用户名
  - --password mysql用户密码
  - --history 将分析结果保存到表中，分析结果比较详细，下次再使用--history时，如果存在相同的语句，且查询所在的时间区间和历史表中的不同，则会记录到数据表中，可以通过查询同一CHECKSUM来比较某类型查询的历史变化
  - --review 将分析结果保存到表中，这个分析只是对查询条件进行参数化，一个类型的查询一条记录，比较简单。当下次使用--review时，如果存在相同的语句分析，就不会记录到数据表中
  - --output 分析结果输出类型，值可以是report(标准分析报告)、slowlog(Mysql slow log)、[json](http://www.php.cn/wiki/1488.html)、json-anon，一般使用report，以便于阅读
  - --since 从什么时间开始分析，值为字符串，可以是指定的某个”yyyy-mm-dd [hh:mm:ss]”格式的时间点，也可以是简单的一个时间值：s(秒)、h(小时)、m(分钟)、d(天)，如12h就表示从12小时前开始统计
  - --until 截止时间，配合—since可以分析一段时间内的慢查询

  **分析pt-query-digest输出结果**

  1. 总体统计结果

     - Overall：总共有多少条查询
     - Time range：查询执行的时间范围
     - unique：唯一查询数量，即对查询条件进行参数化以后，总共有多少个不同的查询
     - total：总计 min：最小 max：最大 avg：平均
     - 95%：把所有值从小到大排列，位置位于95%的那个数，这个数一般最具有参考价值
     - median：中位数，把所有值从小到大排列，位置位于中间那个数

  2. 查询分组统计结果

     - Rank：所有语句的排名，默认按查询时间降序排列，通过--order-by指定
     - Query ID：语句的ID，（去掉多余空格和文本字符，计算[hash](http://www.php.cn/wiki/762.html)值）
     - Response：总的响应时间
     - time：该查询在本次分析中总的时间占比
     - calls：执行次数，即本次分析总共有多少条这种类型的查询语句
     - R/Call：平均每次执行的响应时间
     - V/M：响应时间Variance-to-mean的比率
     - Item：查询对象

  3. 每一种查询的详细统计结果

     由下面查询的详细统计结果，最上面的[表格](http://www.php.cn/code/5947.html)列出了执行次数、最大、最小、平均、95%等各项目的统计

     - ID：查询的ID号，和上图的Query ID对应
     - Databases：数据库名
     - Users：各个用户执行的次数（占比）
     - Query_time distribution ：查询时间分布, 长短体现区间占比，本例中1s-10s之间查询数量是10s以上的两倍
     - Tables：查询中涉及到的表
     - Explain：SQL语句

##### 索引

  MySQL官方对索引的定义为：索引（Index）是帮助MySQL高效获取数据的数据结构。

  可以得到索引的本质：**索引是数据结构**。

######   **索引的分类**

  - **普通索引：**即一个索引只包含单个列，一个表可以有多个单列索引
  - **唯一索引：**索引列的值必须唯一，但允许有空值
  - **复合索引：**即一个索引包含多个列
  - **聚簇索引(聚集索引)**：并不是一种单独的索引类型，而是一种数据存储方式。具体细节取决于不同的实现，InnoDB的聚簇索引其实就是在同一个结构中保存了B-Tree索引(技术上来说是B+Tree)和数据行。
  - **非聚簇索引：**不是聚簇索引，就是非聚簇索引

######   **基础语法**

  - **查看索引**

    SHOW INDEX FROM table_name\G

  - **创建索引**

    CREATE  [UNIQUE ] INDEX indexName ON mytable(columnname(length));

    ALTER TABLE 表名 ADD [UNIQUE ] INDEX [indexName] ON (columnname(length))

  - **删除索引**

    DROP INDEX [indexName] ON mytable;

###### 执行计划

使用EXPLAIN关键字可以模拟优化器执行SQL查询语句，从而知道MySQL是如何处理你的SQL语句的。分析你的查询语句或是表结构的性能瓶颈

###### 执行计划的作用

- 表的读取顺序
- 数据读取操作的操作类型
- 哪些索引可以使用
- 哪些索引被实际使用
- 表之间的引用
- 每张表有多少行被优化器查询

###### 执行计划的语法

`EXPLAIN select * from table1`

重点的就是EXPLAIN后面你要分析的SQL语句

###### 执行计划详解

| id   | select_type | table | type | possible_keys | key  | key_len | ref  | rows | Extra |
| ---- | ----------- | ----- | ---- | ------------- | ---- | ------- | ---- | ---- | ----- |

- ID

  描述select查询的序列号,包含一组数字，表示查询中执行select子句或操作表的顺序,根据ID的数值结果可以分成一下三种情况:

  - id相同：执行顺序由上至下
  - id不同：如果是子查询，id的序号会递增，id值越大优先级越高，越先被执行
  - id相同不同：同时存在

- select_type

  要是用于区别:普通查询、联合查询、子查询等的复杂查询

  | 类型         | 描述                                                         | 例子                                                         | 描述                                                         |
  | ------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
  | SIMPLE       | 简单的select查询，查询中不包含子查询或者UNION                | EXPLAIN select * from t1                                     | 简单的 select 查询,查询中不包含子查询或者UNION               |
  | PRIMARY      | 查询中若包含任何复杂的子部分，最外层查询则被标记为           | select t1.*,(select t2.id from t2 where t2.id = 1 ) from t1  | 查询中若包含任何复杂的子部分，最外层查询则被标记为           |
  | SUBQUERY     | 在select或where列表中包含了子查询                            | select t1.*,(select t2.id from t2 where t2.id = 1 ) from t1  | 在SELECT或WHERE列表中包含了子查询                            |
  | DERIVED      | 在from列表中包含的子查询被标记为derived(衍生)，Mysql会递归执行这些子查询，把结果放在临时表里。 | select t1.* from t1 ,(select t2.* from t2 where t2.id = 1 ) s2 where t1.id = s2.id | 在FROM列表中包含的子查询被标记为DERIVED(衍生)；MySQL会递归执行这些子查询, 把结果放在临时表里。 |
  | UNION        | 若第二个SELECT出现在UNION之后，则被标记为UNION；若UNION包含在FROM子句的子查询中，外层SELECT将被标记为：DERIVED | EXPLAIN select * from t2 UNION select * from t2              | 若第二个SELECT出现在UNION之后，则被标记为UNION；             |
  | UNION RESULT | 从UNION表获取结果的SELECT                                    | EXPLAIN select * from t2 UNION select * from t2              | 从UNION表获取结果的SELECT                                    |

- table

  显示这一行的数据是关于哪张表的

- type

  type显示的是访问类型，是较为重要的一个指标，结果值从最好到最坏依次是：

  system > const > eq_ref > ref > fulltext > ref_or_null > index_merge > unique_subquery > index_subquery > range > index > ALL 

  需要记忆的

  system>const>eq_ref>ref>range>index>ALL

  一般来说，得保证查询至少达到range级别，最好能达到ref。

  - System与const

    System：表只有一行记录（等于系统表），这是const类型的特列，平时不会出现，这个也可以忽略不计

    Const：表示通过索引一次就找到了

    const用于比较primary key或者unique索引。因为只匹配一行数据，所以很快

    如将主键置于where列表中，MySQL就能将该查询转换为一个常量

  - eq_ref

    唯一性索引扫描，对于每个索引键，表中只有一条记录与之匹配。常见于主键或唯一索引扫描

  - ref

     非唯一性索引扫描，返回匹配某个单独值的所有行.

    本质上也是一种索引访问，它返回所有匹配某个单独值的行，然而，它可能会找到多个符合条件的行，所以他应该属于查找和扫描的混合体

  - range

    只检索给定范围的行,使用一个索引来选择行。key 列显示使用了哪个索引

    一般就是在你的where语句中出现了between、<、>、in等的查询

    这种范围扫描索引扫描比全表扫描要好，因为它只需要开始于索引的某一点，而结束语另一点，不用扫描全部索引。

  - index

    当查询的结果全为索引列的时候，虽然也是全部扫描，但是只查询的索引库，而没有去查询

    数据。

  - all 

    Full Table Scan，将遍历全表以找到匹配的行

- possible_keys / key

  possible_keys:可能使用的key

  Key:实际使用的索引。如果为NULL，则没有使用索引

  查询中若使用了**覆盖索引**，则该索引和查询的select字段重叠

- key_len

  Key_len表示索引中使用的字节数，可通过该列计算查询中使用的索引的长度。在不损失精确性的情况下，长度越短越好

  key_len显示的值为索引字段的最大可能长度，并非实际使用长度，即key_len是根据表定义计算而得，不是通过表内检索出的

  - key_len表示索引使用的字节数
  - 根据这个值，就可以判断索引使用情况，特别是在组合索引的时候，判断所有的索引字段是否都被查询用到
  - char和varchar跟字符编码也有密切的联系
  - latin1占用1个字节，gbk占用2个字节，utf8占用3个字节。（不同字符编码占用的存储空间不同）

- ref

  显示索引的哪一列被使用了，如果可能的话，是一个常数。

- rows

  根据表统计信息及索引选用情况，大致估算出找到所需的记录所需要读取的行数

- Extra

  包含不适合在其他列中显示但十分重要的额外信息。

  | 值                      | 描述                                                         | 说明                                                         |
  | ----------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
  | Using filesort          | 说明mysql会对数据使用一个外部的索引排序，而不是按照表内的索引顺序进行读取。Mysql中无法利用索引完成的排序操作称为‘文件排序’ | 当发现有Using filesort 后，实际上就是发现了可以优化的地方    |
  | Using timporary         | 使用了临时表保存中间结果，Mysql在对查询结果排序时使用临时表。常见于排序order by 和分组查询group by | 尤其发现在执行计划里面有using filesort而且还有Using temporary的时候，特别需要注意 |
  | Useing index            | 是否使用了覆盖索引                                           | 如果同时出现using where，表明索引被用来执行索引键值的查找;如果没有同时出现using where，表明索引用来读取数据而非执行查找动作 |
  | Using where             | 表明使用了where过滤                                          |                                                              |
  | Using join buffer       | 使用了连接缓存                                               |                                                              |
  | impossible        where | where子句的值总是false，不能用来采取任何元组                 |                                                              |

  ***覆盖索引：***

  就是select的数据列只用从索引中就能够取得，不必读取数据行，MySQL可以利用索引返回select列表中的字段，而不必根据索引再次读取数据文件,换句话说查询列要被所建的索引覆盖。

  索引是高效找到行的一个方法，但是一般数据库也能使用索引找到一个列的数据，因此它不必读取整个行。毕竟索引叶子节点存储了它们索引的数据;当能通过读取索引就可以得到想要的数据，那就不需要读取行了。一个索引包含了(或覆盖了)满足查询结果的数据就叫做覆盖索引

  注意：

  如果要使用覆盖索引，一定要注意select列表中只取出需要的列，不可select *，

  因为如果将所有字段一起做索引会导致索引文件过大，查询性能下降。

  所以，千万不能为了查询而在所有列上都建立索引，会严重影响修改维护的性能。

#####  SQL优化

###### 尽量全值匹配

###### 最佳左前缀法则

###### 不在索引上做任何操作

###### 范围条件放最后

###### 覆盖索引尽量用

###### 不等于要慎用

###### Null/Not 有影响

###### Like查询要用心

###### 字符类型加引号

###### OR改UNION效率高

#### LOAD DATA INFLIE

LOAD DATA INFLIE；

**使用LOAD DATA INFLIE ,比一般的insert语句快20倍**

 

select * into OUTFILE 'D:\\product.txt' from product_info

load data INFILE 'D:\\product.txt' into table product_info

 

load data INFILE '/soft/product3.txt' into table product_info

 

show VARIABLES like 'secure_file_priv'

- secure_file_priv 为 NULL 时，表示限制mysqld不允许导入或导出。

- secure_file_priv 为 /tmp 时，表示限制mysqld只能在/tmp目录中执行导入导出，其他目录不能执行。

- secure_file_priv 没有值时，表示不限制mysqld在任意目录的导入导出。

  

·     

 



