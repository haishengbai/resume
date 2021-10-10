### 简介

Linux 是一套免费使用和自由传播的类 Unix 操作系统，是一个基于 POSIX 和 UNIX 的多用户、多任务、支持多线程和多 CPU 的操作系统。l

#### Linux目录结构

- **/boot**：存放的启动Linux 时使用的内核文件，包括连接文件以及镜像文件。
-  **/etc**：**存放**所有**的系统需要的**配置文件**和**子目录列表，更改目录下的文件可能会导致系统不能启动。
-  **/lib**：存放基本代码库（比如c++库），其作用类似于Windows里的DLL文件。几乎所有的应用程序都需要用到这些共享库。
- **/sys**： 这是linux2.6内核的一个很大的变化。该目录下安装了2.6内核中新出现的一个文件系统 sysfs 。sysfs文件系统集成了下面3种文件系统的信息：针对进程信息的proc文件系统、针对设备的devfs文件系统以及针对伪终端的devpts文件系统。该文件系统是内核设备树的一个直观反映。当一个内核对象被创建的时候，对应的文件和目录也在内核对象子系统中
- 指令集合：
  -  **/bin**：存放着最常用的程序和指令
  - **/sbin**：只有系统管理员能使用的程序和指令
- 外部文件管理：
  -  **/dev** **：**Device(设备)的缩写, 存放的是Linux的外部设备。**注意：**在Linux中访问设备和访问文件的方式是相同的
  - **/media**：类windows的**其他设备，**例如U盘、光驱等等，识别后linux会把设备放到这个目录下
  - **/mnt**：临时挂载别的文件系统的，我们可以将光驱挂载在/mnt/上，然后进入该目录就可以查看光驱里的内容了
- 临时文件：
  -  **/run**：是一个临时文件系统，存储系统启动以来的信息。当系统重启时，这个目录下的文件应该被删掉或清除。如果你的系统上有 /var/run 目录，应该让它指向 run。
  -  **/lost+found**：一般情况下为空的，系统非法关机后，这里就存放一些文件
  - **/tmp**：这个目录是用来存放一些临时文件的
- 账户：
  - **/root**：系统管理员的用户主目录
  -  **/home**：用户的主目录，以用户的账号命名的
  -  **/usr**：用户的很多应用程序和文件都放在这个目录下，类似于windows下的program files目录
  - **/usr/bin**：系统用户使用的应用程序与指令
  - **/usr/sbin**：超级用户使用的比较高级的管理程序和系统守护程序
  - **/usr/src**：内核源代码默认的放置目录。运行过程中要用
  -  **/var**：存放经常修改的数据，比如程序运行的日志文件（/var/log 目录下）
  - **/proc**：管理**内存空间！**虚拟的目录，是系统内存的映射，我们可以直接访问这个目录来，获取系统信息。这个目录的内容不在硬盘上而是在内存里，我们也可以直接修改里面的某些文件来做修改
  - 扩展：
    - **/opt**：默认是空的，我们安装额外软件可以放在这个里面。
    - **/srv**：存放服务启动后需要提取的数据**（不用服务器就是空）**

#### Linux基础命令

shutdown 会给系统计划一个时间关机。它可以被用于停止、关机、重启机器。shutdown 会给系统计划一个时间关机。它可以被用于停止、关机、重启机器。

```sh
# shutdown -P now  ### 关闭机器
# shutdown -H now  ### 停止机器      
# shutdown -r09:35 ### 在 09:35am 重启机器
```

要取消即将进行的关机，只要输入下面的命令：

```sh
# shutdown -c
```



### 用户管理

Linux系统是一个多用户多任务的分时操作系统，任何一个要使用系统资源的用户，都必须首先向系统管理员申请一个账号，然后以这个账号的身份进入系统。

用户账号的管理工作主要涉及到用户账号的添加、修改和删除。

##### groupadd [参数] 用户组名

*创建用户组*

-g：指定新建用户组的gid；

-r：创建系统工作组，系统用户的组ID小于500；

-K：覆盖配置文件“/ect/login.defs”；

-o：允许添加组ID号不唯一的工作组。

##### useradd [参数] 用户名

*新建用户*

-g 属组

-u 设置uid

-m 创建家目录

-M 没有家目录

-G 指定属于多个组

-s 指定登录[shell](https://www.linuxcool.com/)

-d 指定家目录

-c 注释

-D 改变它默认的属性

-e 指定的日期是帐号失效的日期,

##### passwd [参数] 用户名

*使用 passwd [命令](https://www.linuxcool.com/)为新建用户设置密码和修改用户密码*

-l：锁定已经命名的账户名称

-u：解开账户锁定状态

-x, --maximum=DAYS：密码使用最大时间（天）

-n, --minimum=DAYS：密码使用最小时间（天）

-d：删除使用者的密码

-S：检查指定使用者的密码认证种类

--stdin：非交互式修改/设置密码，弊端是操作日志能查密码，用history -c 干掉

#### 与用户账号有关的系统文件

与用户和用户组相关的信息都存放在一些系统文件中，这些文件包括/etc/passwd, /etc/shadow, /etc/group等

##### /etc/passwd文件

Linux系统中的每个用户都在/etc/passwd文件中有一个对应的记录行，它记录了这个用户的一些基本属性。

例子：

```
＃ cat /etc/passwd
 
root:x:0:0:Superuser:/:
daemon:x:1:1:System daemons:/etc:
bin:x:2:2:Owner of system commands:/bin:
sys:x:3:3:Owner of system files:/usr/sys:
adm:x:4:4:System accounting:/usr/adm:
uucp:x:5:5:UUCP administrator:/usr/lib/uucp:
auth:x:7:21:Authentication administrator:/tcb/files/auth:
cron:x:9:16:Cron daemon:/usr/spool/cron:
listen:x:37:4:Network daemon:/usr/net/nls:
lp:x:71:18:Printer administrator:/usr/spool/lp:
```

每一行记录对应着一个用户（其中bin/sys/adm/uucp/lp/nobody是伪用户），每行记录又被冒号(:)分隔为7个字段，其格式和具体含义如下：

```
用户名:口令:用户标识号:组标识号:注释性描述:主目录:登录Shell
```

##### /etc/group文件

用户组的所有信息都存放在/etc/group文件中。

例子：

```
root::0:root
bin::2:root,bin
sys::3:root,uucp
adm::4:root,adm
daemon::5:root,daemon
lp::7:root,lp
users::20:root,sam
```

此文件的格式类似于/etc/passwd文件，由冒号(:)隔开若干个字段，这些字段有：

```
组名:口令:组标识号:组内用户列表
```

 



### 文件系统

```
第一个字符：代表这个文件的类型，是目录、文件，还是一个链接等等
[ d ] 目录
[ - ] 文件
[ l  ] 链接文档(link file)
[ b ] 可供储存的接口设备(可随机存取装置)
[ c ] 串行端口设备，例如键盘、鼠标(一次性读取装置)
接下来的字符：以三个一组分成三组，用 r、w、x 三个参数的组合表示，位置不会改变
[ r ] 代表可读(read)
[ w ] 代表可写(write)
[ x ] 代表可执行(execute)
[ - ] 没有权限
```

#### 文件的属主与属组

每一个文件，它都有一个特定的所有者，也就是对该文件具有所有权的用户。

同时，在Linux系统中，用户是按组分类的，一个用户属于一个或多个组。

文件所有者以外的用户又可以分为文件所有者的同组用户和其他用户。

因此，Linux系统按文件所有者、文件所有者同组用户和其他用户来规定了不同的文件访问权限。

##### chgrp：更改文件属组

```
chgrp [-R] 属组名 文件名
参数选项
-R：递归更改文件属组，就是在更改某个目录文件的属组时，如果加上-R的参数，那么该目录下的所有文件的属组都会更改。
```

##### chown：更改文件属主，也可以同时更改文件属组

```
chown [–R] 属主名 文件名
chown [-R] 属主名：属组名 文件名
进入 /root 目录（~）将install.log的拥有者改为bin这个账号：
```

例：

```
chown root:root aa              ##更改aa的属主与属组
```

##### chmod：更改文件9个属性

文件的权限字符为：『-rwxrwxrwx』， 这九个权限是三个三个一组的！其中，我们可以使用数字来代表各个权限，各权限的分数对照表如下：

r:4 w:2  x:1

每种身份(owner/group/others)各自的三个权限(r/w/x)分数是需要累加的，例如当权限为： [-rwxrwx---] 分数则是：

owner = rwx = 4+2+1 = 7

group = rwx = 4+2+1 = 7

others= --- = 0+0+0 = 0

所以等一下我们设定权限的变更时，该文件的权限数字就是770啦！

变更权限的指令chmod的语法是这样的：

```sh
chmod [-R] xyz 文件或目录
选项与参数：
xyz : 就是刚刚提到的数字类型的权限属性，为 rwx 属性数值的相加。
-R : 进行递归(recursive)的持续变更，亦即连同次目录下的所有文件都会变更
```

举例来说，如果要将.bashrc这个文件所有的权限都设定启用，那么命令如下：

```
chmod +x aa                     ##更改aa的属性,加x权限
chmod 777 aa                    ##更改aa的属性，三组权限都设置成7
```

#### 文件与目录管理命令

##### man [**命令**]

使用 *man [**命令**]* 来查看各个命令的使用文档，如 ：man cp

```sh
ls: 列出目录 ---ll
cd：切换目录
pwd：显示目前的目录
mkdir：创建一个新的目录
rmdir：删除一个空的目录
cp: 复制文件或目录  -----scp网络复制
rm: 移除文件或目录
mv: 移动文件与目录，或修改文件与目录的名称
```

#### 硬链接与软连接

##### 硬链接

硬连接指通过索引节点来进行连接。在 Linux 的文件系统中，保存在磁盘分区中的文件不管是什么类型都给它分配一个编号，称为索引节点号(Inode Index)。在 Linux 中，多个文件名指向同一索引节点是存在的。比如：A 是 B 的硬链接（A 和 B 都是文件名），则 A 的目录项中的 inode 节点号与 B 的目录项中的 inode 节点号相同，即一个 inode 节点对应两个不同的文件名，两个文件名指向同一个文件，A 和 B 对文件系统来说是完全平等的。删除其中任何一个都不会影响另外一个的访问。

硬连接的作用是允许一个文件拥有多个有效路径名，这样用户就可以建立硬连接到重要文件，以防止“误删”的功能。其原因如上所述，因为对应该目录的索引节点有一个以上的连接。只删除一个连接并不影响索引节点本身和其它的连接，只有当最后一个连接被删除后，文件的数据块及目录的连接才会被释放。也就是说，**文件真正删除的条件是与之相关的所有硬连接文件均被删除**

##### 软连接

另外一种连接称之为符号连接（Symbolic Link），也叫软连接。软链接文件有类似于 Windows 的快捷方式。它实际上是一个特殊的文件。在符号连接中，文件实际上是一个文本文件，其中包含的有另一文件的位置信息。比如：A 是 B 的软链接（A 和 B 都是文件名），A 的目录项中的 inode 节点号与 B 的目录项中的 inode 节点号不相同，A 和 B 指向的是两个不同的 inode，继而指向两块不同的数据块。但是 A 的数据块中存放的只是 B 的路径名（可以根据这个找到 B 的目录项）。A 和 B 之间是“主从”关系，如果 B 被删除了，A 仍然存在（因为两个是不同的文件），但指向的是一个无效的链接。

例：

```sh
$touch a          #创建一个测试文件a
$ ln a b          #创建a的一个硬连接文件b    --echo 123 > a ##a,b的内容同时变为123
$ ln -s a c       #创建a的一个符号连接文件c
$ ls -li            # -i参数显示文件的inode节点信息
```

#### find [选项] [参数]

*查找文件和文件夹*

`查找目录：find /（查找范围） -name '查找关键字' -type d`
`查找文件：find /（查找范围） -name 查找关键字 -print`

-amin<分钟>：查找在指定时间曾被存取过的文件或目录，单位以分钟计算；
-anewer<参考文件或目录>：查找其存取时间较指定文件或目录的存取时间更接近现在的文件或目录；
-atime<24小时数>：查找在指定时间曾被存取过的文件或目录，单位以24小时计算；
-cmin<分钟>：查找在指定时间之时被更改过的文件或目录；
-cnewer<参考文件或目录>查找其更改时间较指定文件或目录的更改时间更接近现在的文件或目录；
-ctime<24小时数>：查找在指定时间之时被更改的文件或目录，单位以24小时计算；
-daystart：从本日开始计算时间；
-depth：从指定目录下最深层的子目录开始查找；
-expty：寻找文件大小为0 Byte的文件，或目录下没有任何子目录或文件的空目录；
-exec<执行指令>：假设find指令的回传值为True，就执行该指令；
-false：将find指令的回传值皆设为False；
-fls<列表文件>：此参数的效果和指定“-ls”参数类似，但会把结果保存为指定的列表文件；
-follow：排除符号连接；
-fprint<列表文件>：此参数的效果和指定“-print”参数类似，但会把结果保存成指定的列表文件；
-fprint0<列表文件>：此参数的效果和指定“-print0”参数类似，但会把结果保存成指定的列表文件；
-fprintf<列表文件><输出格式>：此参数的效果和指定“-printf”参数类似，但会把结果保存成指定的列表文件；
-fstype<文件系统类型>：只寻找该文件系统类型下的文件或目录；
-gid<群组识别码>：查找符合指定之群组识别码的文件或目录；
-group<群组名称>：查找符合指定之群组名称的文件或目录；
-help或——help：在线帮助；
-ilname<范本样式>：此参数的效果和指定“-lname”参数类似，但忽略字符大小写的差别；
-iname<范本样式>：此参数的效果和指定“-name”参数类似，但忽略字符大小写的差别；
-inum<inode编号>：查找符合指定的inode编号的文件或目录；
-ipath<范本样式>：此参数的效果和指定“-path”参数类似，但忽略字符大小写的差别；
-iregex<范本样式>：此参数的效果和指定“-regexe”参数类似，但忽略字符大小写的差别；
-links<连接数目>：查找符合指定的硬连接数目的文件或目录；
-iname<范本样式>：指定字符串作为寻找符号连接的范本样式；
-ls：假设find指令的回传值为Ture，就将文件或目录名称列出到标准输出；
-maxdepth<目录层级>：设置最大目录层级；
-mindepth<目录层级>：设置最小目录层级；
-mmin<分钟>：查找在指定时间曾被更改过的文件或目录，单位以分钟计算；
-mount：此参数的效果和指定“-xdev”相同；
-mtime<24小时数>：查找在指定时间曾被更改过的文件或目录，单位以24小时计算；
-name<范本样式>：指定字符串作为寻找文件或目录的范本样式；
-newer<参考文件或目录>：查找其更改时间较指定文件或目录的更改时间更接近现在的文件或目录；
-nogroup：找出不属于本地主机群组识别码的文件或目录；
-noleaf：不去考虑目录至少需拥有两个硬连接存在；
-nouser：找出不属于本地主机用户识别码的文件或目录；
-ok<执行指令>：此参数的效果和指定“-exec”类似，但在执行指令之前会先询问用户，若回答“y”或“Y”，则放弃执行命令；
-path<范本样式>：指定字符串作为寻找目录的范本样式；
-perm<权限数值>：查找符合指定的权限数值的文件或目录；
-print：假设find指令的回传值为Ture，就将文件或目录名称列出到标准输出。格式为每列一个名称，每个名称前皆有“./”字符串；
-print0：假设find指令的回传值为Ture，就将文件或目录名称列出到标准输出。格式为全部的名称皆在同一行；
-printf<输出格式>：假设find指令的回传值为Ture，就将文件或目录名称列出到标准输出。格式可以自行指定；
-prune：不寻找字符串作为寻找文件或目录的范本样式;
-regex<范本样式>：指定字符串作为寻找文件或目录的范本样式；
-size<文件大小>：查找符合指定的文件大小的文件；
-true：将find指令的回传值皆设为True；
-typ<文件类型>：只寻找符合指定的文件类型的文件；
-uid<用户识别码>：查找符合指定的用户识别码的文件或目录；
-used<日数>：查找文件或目录被更改之后在指定时间曾被存取过的文件或目录，单位以日计算；
-user<拥有者名称>：查找符和指定的拥有者名称的文件或目录；
-version或——version：显示版本信息；
-xdev：将范围局限在先行的文件系统中；
-xtype<文件类型>：此参数的效果和指定“-type”参数类似，差别在于它针对符号连接检查



### 安装程序 rpm/yum

#### rpm方式

需要去下载相应的rpm格式的安装包xxxx.rpm

rpm [参数] rpm包

-i 安装

-U 更新（很少用）

-e 卸载

-v 显示安装信息

-h 显示安装进度

例：

```sh
rpm -ivh jdk-8u172-linux-x64.rpm 安装jdk8并显示安装进度和安装信息

rpm -qa|grep jdk查看jdk的安装包

rpm -e jdk1.8-1.8.0_221-fcs.x86_64 卸载jdk8 ，只适用于rpm包安装的软件
```



#### yum方式

yum（ Yellow dog Updater, Modified）是一个在Fedora和RedHat以及SUSE中的Shell前端软件包管理器。

基於RPM包管理，能够从指定的服务器自动下载RPM包并且安装，可以自动处理依赖性关系，并且一次安装所有依赖的软体包，无须繁琐地一次次下载、安装。

yum提供了查找、安装、删除某一个、一组甚至全部软件包的命令，而且命令简洁而又好记。

##### yum 语法

```
yum [options] [command] [package ...]
options：可选，选项包括-h（帮助），-y（当安装过程提示选择全部为"yes"），-q（不显示安装的过程）等等。
command：要进行的操作。
package操作的对象。
例：
yum search jdk       搜索jdk安装包
yum install -y java-1.8.0-openjdk.x86_64          安装openjdk，-y表示安装过程中的询问自动选y
yum list installed ｜grep jdk                   列出安装的jdk软件包
yum remove java-1.8.0-openjdk.x86_64  java-1.8.0-openjdk-headless.x86_64         卸载jdk
```

##### yum常用命令

```
1.列出所有可更新的软件清单命令：yum check-update
2.更新所有软件命令：yum update
3.仅安装指定的软件命令：yum install <package_name>
4.仅更新指定的软件命令：yum update <package_name>
5.列出所有可安裝的软件清单命令：yum list
6.删除软件包命令：yum remove <package_name>
7.查找软件包 命令：yum search <keyword>
8.清除缓存命令:
yum clean packages: 清除缓存目录下的软件包
yum clean headers: 清除缓存目录下的 headers
yum clean oldheaders: 清除缓存目录下旧的 headers
yum clean, yum clean all (= yum clean packages; yum clean oldheaders) :清除缓存目录下的软件包及旧的headers
 
```

##### yum源

官方的yum源在国内访问效果不佳。需要改为国内比较好的阿里云或者网易的yum源

在/etc/yum..repos.d/下进行如下操作(请做好相应备份)：

```
wget http://mirrors.163.com/.help/CentOS7-Base-163.repo
mv CentOS7-Base-163.repo  CentOS-Base.repo
```

重建缓存：

```
yum clean all
yum makecache
```

#### 配置环境变量

系统环境变量文件在/etc下的profile文件，我们可以用vi profile命令来编辑该文件，将变量添加进去

一般需要将安装文件的bin目录加入path中，可在profile中加入下面一行代码，如：

vim /etc/profile 

\# 末尾追加以下内容

```sh
export JAVA_HOME=/usr/java/default

export PATH=$JAVA_HOME/bin:$PATH

export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
```

#### 环境变量生效

```sh
source /etc/profile
```



### shell脚本

shell语言是一门linux系统下的工具语言，主要用于写一些linux系统下的操作命令，实际上Shell是一个命令解释器，它解释由用户输入的命令并且把它们送到内核。或者直接理解为shell命令是可以执行多个linux命令的脚本。Shell 种类众多，有以下种类（一般使用的 Bash，就是 Bourne Again Shell，它是大多数Linux 系统默认的 Shell）：

```
l   Bourne Shell（/usr/bin/sh或/bin/sh）
l   Bourne Again Shell（/bin/bash）    
l   C Shell（/usr/bin/csh）
l   K Shell（/usr/bin/ksh）
l   Shell for Root（/sbin/sh）
```

​    shell并不复杂，有编程基础的话，简单入门两三个小时就可以入门，主要是把一些重复操作的linux命令写成shell脚本来执行一下。以下列出shell常规的一些要素：  

#### 解释器与执行shell

解释器

```
#!/bin/sh   Bourne shell版本
#!/bin/bash Bourne Again Shell 版本
```

执行shell

```
chomd +x ./test.sh #使脚本具有执行权限
./test.sh #执行脚本 “./”表示当前目录下
```

 

#### 演示

```
#!/bin/bash        ##声明bash脚本
 
##demo            ##注释
echo $PATH         ##打印PATH环境变量
name="Peter"       ##定义变量name
echo $name         ##打印name值
echo "I am ${name}'s friend"    ##字符串拼接
echo ""                                              
 
name2="I am ${name}'s good friend"          ##字符串拼接
echo $name2
echo ""
 
names=("Peter" "james" "deer")              ##定义数组
echo ${names[@]}                          ##遍历数组
echo "I am ${names[1]}'s friend" ##第二个元素
echo "I have ${#names[@]} friends"          ##数组长度
echo ""
 
for var in ${names[@]};                                ##循环数组
do 
        if test $var = 'Peter'               ##字符串相等
        then
                echo "I am Peter"
        else
                echo "I am ${var}'s friend"
        fi
done
 
echo ""
if [ $(ps -ef | grep -c "ssh") -gt 1 ]; ##查找是否有ssh服务
then echo "ssh service open"; 
fi
 
echo ""
echo "sh arg: $0 $1"                                  ##sh的传参
 
 
重定向：
1、test 'aa' -eq "bb" > out   ##命令输出到 out文件，报错信息并不会进入out
2、test 'aa' -eq "bb" > out 2>&1  ##将stderr合并到stdout，则报错信息进入了out
```



### 常用命令

#### 最最常用的是cd 命令

cd 进入用户主目录；

cd ~ 进入用户主目录；

cd - 返回进入此目录之前所在的目录；

cd .. 返回上级目录（若当前目录为“/“，则执行完后还在“/"；".."为上级目录的意思）；

cd ../.. 返回上两级目录；

**ls**，**ll**，**wget**，**curl**，**history**

#### 新建文件夹和文件：mkdir touch

mkdir 创建文件夹

mkdir dirname 直接跟文件夹名，可在当前目录下创建文件夹

mkdir /opt/lamp/dirname 可跟路径

mkdir -p /opt/lam/dirname 假如lam不存在，需要用-p才可以创建该文件夹

 

touch 新建文件

touch dilename 可直接跟文件名在当前目录下创建新的文件

#### cat/less/more/tail等文件查看命令

-n或-number：有1开始对所有输出的行数编号；

-b或--number-nonblank：和-n相似，只不过对于空白行不编号；

-s或--squeeze-blank：当遇到有连续两行以上的空白行，就代换为一行的空白行；

-A：显示不可打印字符，行尾显示“$”；

-e：等价于"-vE"选项；

-t：等价于"-vT"选项；

-e：文件内容显示完毕后，自动退出；

-f：强制显示文件；

-g：不加亮显示搜索到的所有关键词，仅显示当前显示的关键字，以提高显示速度；

-l：搜索时忽略大小写的差异；

-N：每一行行首显示行号；

-s：将连续多个空行压缩成一行显示；

-S：在单行显示较长的内容，而不换行显示；

-x<数字>：将TAB字符显示为指定个数的空格字符。

-<数字>：指定每屏显示的行数；

-d：显示“[press space to continue,'q' to quit.]”和“[Press 'h' for instructions]”；

-c：不进行滚屏操作。每次刷新这个屏幕；

-s：将多个空行压缩成一行显示；

-u：禁止下划线； +<数字>：从指定数字的行开始显示。



按Space键：显示文本的下一屏内容。

按Enier键：只显示文本的下一行内容。

按斜线符|：接着输入一个模式，可以在文本中寻找下一个相匹配的模式。

按H键：显示帮助屏，该屏上有相关的帮助信息。

按B键：显示上一屏内容。

按Q键：退出rnore命令。

--retry：即是在tail命令启动时，文件不可访问或者文件稍后变得不可访问，都始终尝试打开文件。使用此选项时需要与选项“——follow=name”连用；

-c或——bytes=：输出文件尾部的N（N为整数）个字节内容；

-f或；--follow：显示文件最新追加的内容。“name”表示以文件名的方式监视文件的变化。“-f”与“-fdescriptor”等效；

-F：与选项“-follow=name”和“--retry"连用时功能相同；

-n或——line=：输出文件的尾部N（N位数字）行内容。

--pid=<进程号>：与“-f”选项连用，当指定的进程号的进程终止后，自动退出tail命令；

-q或——quiet或——silent：当有多个文件参数时，不输出各个文件名；

-s<秒数>或——sleep-interal=<秒数>：与“-f”选项连用，指定监视文件变化时间隔的秒数；

-v或——verbose：当有多个文件参数时，总是输出各个文件名；

 

一般tail命令最常用的-n和-f，例：

tail filename 读取filename最后10行内容

tail -f filename 实时动态读取filename最后10行内容

tail -20f filename 实时动态读取filename最后20行内容

 

cat和less及more指令相似，cat是一次性读取所有内容，文件内容较多时速度较慢。less与more的区别在于：less可以前后翻页查看，more只能向前翻页查看。

tail则用于实时获取log信息，从后向前读取内容

上述四种命令均可跟grep搭配使用cat/less/tail |grep mysql

#### 查看文件大小的命令 du/df

du -sh * 显示当前目录下所有文件的大小

du -sh filename 　　显示该文件大小

du -sh 　　　　显示当前目录所占空间大小

-s或 仅显示总计，只列出最后加总的值。

-h或 以K，M，G为单位，提高信息的可读性。

df 显示磁盘占用信息

直接df默认一k为单位

df -lh 显示本地系统的占用信息，以K，M，G为单位

#### 大重点--文本编辑器 vi

vi命令是UNIX操作系统和类UNIX操作系统中最通用的全屏幕纯文本编辑器。

Linux中的vi编辑器叫vim，它是vi的增强版（vi Improved），与vi编辑器完全兼容，而且实现了很多增强功能。 

vi编辑器支持编辑模式和命令模式，编辑模式下可以完成文本的编辑功能，命令模式下可以完成对文件的操作命令，要正确使用vi编辑器就必须熟练掌握着两种模式的切换。

默认情况下，打开vi编辑器后自动进入命令模式。从编辑模式切换到命令模式使用“esc”键，从命令模式切换到编辑模式使用“A”、“a”、“O”、“o”、“I”、“i”键。 

vi编辑器提供了丰富的内置命令，有些内置命令使用键盘组合键即可完成，有些内置命令则需要以冒号“：”开头输入。常用内置命令如下：

1 Ctrl+u：向文件首翻半屏；

2 Ctrl+d：向文件尾翻半屏；

3 Ctrl+f：向文件尾翻一屏；

4 Ctrl+b：向文件首翻一屏；

5 Esc：从编辑模式切换到命令模式；

6 ZZ：命令模式下保存当前文件所做的修改后退出vi；

7 :行号：光标跳转到指定行的行首；

8 :$：光标跳转到最后一行的行首；

9 x或X：删除一个字符，x删除光标后的，而X删除光标前的；

10 D：删除从当前光标到光标所在行尾的全部字符；

11 dd：删除光标行正行内容；

12 ndd：删除当前行及其后n-1行；

13 nyy：将当前行及其下n行的内容保存到寄存器?中，其中？为一个字母，n为一个数字；

14 p：粘贴文本操作，用于将缓存区的内容粘贴到当前光标所在位置的下方；

15 P：粘贴文本操作，用于将缓存区的内容粘贴到当前光标所在位置的上方；

16 /字符串：文本查找操作，用于从当前光标所在位置开始向文件尾部查找指定字符串的内容，查找的字符串会被加亮显示；

17 ？name：文本查找操作，用于从当前光标所在位置开始向文件头部查找指定字符串的内容，查找的字符串会被加亮显示；

18 a，bs/F/T：替换文本操作，用于在第a行到第b行之间，将F字符串换成T字符串。其中，“s/”表示进行替换操作；

19 a：在当前字符后添加文本；

20 A：在行末添加文本；

21 i：在当前字符前插入文本；

22 I：在行首插入文本；

23 o：在当前行后面插入一空行；

24 O：在当前行前面插入一空行；

25 :wq：在命令模式下，执行存盘退出操作；

26 :w：在命令模式下，执行存盘操作；

27 :w！：在命令模式下，执行强制存盘操作；

28 :q：在命令模式下，执行退出vi操作；

29 :q！：在命令模式下，执行强制退出vi操作；

30 :e文件名：在命令模式下，打开并编辑指定名称的文件；

31 :n：在命令模式下，如果同时打开多个文件，则继续编辑下一个文件；

32 :f：在命令模式下，用于显示当前的文件名、光标所在行的行号以及显示比例；

33 :set nu：在命令模式下，用于在最左端显示行号；

34 :set nonu：在命令模式下，用于在最左端不显示行号；

35 :1,3y 复制第一行到第三行

36 :1,3d 删除第一行到第三行

37 :1,3s/str/str_new/g 替换第一行到第三行中的字符串

38 :1,3s/str/str_new 替换第一行到第三行中的字符串第一个字符

39 :1,3 g/str /d 删除第一行到第三行中含有这个字符串的行

#### 重定向 >

##### 输出重定向

who > bbb.txt 将aaa的内容写入bbb中，覆盖写入

cat bbb.txt >> ccc.txt 讲aaa的内容追加写入bbb中，不覆盖原来内容

\> bbb.txt 将bbb清空

##### 输入重定向

grep 05:37:43.730 < web.2019-07-22.0.log

 

大多数 UNIX 系统命令从你的终端接受输入并将所产生的输出发送回到您的终端。

一个命令通常从一个叫标准输入的地方读取输入，默认情况下，这恰好是你的终端。

同样，一个命令通常将其输出写入到标准输出，默认情况下，这也是你的终端。

##### 重定向深入讲解

一般情况下，每个 Unix/Linux 命令运行时都会打开三个文件：

- 标准输入文件(stdin)：stdin的文件描述符为0，Unix程序默认从stdin读取数据
- 标准输出文件(stdout)：stdout 的文件描述符为1，Unix程序默认向stdout输出数据
- 标准错误文件(stderr)：stderr的文件描述符为2，Unix程序会向stderr流中写入错误信息

默认情况下，command > file 将 stdout 重定向到 file，command < file 将stdin 重定向到 file。

#### 查看服务

netstat -nlpt|grep 80 查看该端口号是否被占用

free -m //查看LINUX内存剩余容量

ps可以查看具体的进程信息，一般与管道符连接其他命令使用，如：grep

ps常用参数-ef/-aux，一般最常用还是-ef，例：ps -ef|grep mysql 查询mysql进程

top也可查看进程信息，而且是动态显示

whoami 查看当前登陆用户

who 查看多少用户在使用系统

date查看系统时间，可跟时间格式使用

cal查看日历，可跟年份，查看指定的年份

chkconfig --list #查看系统服务启动

chkconfig iptables on #开机启动该服务

chkconfig iptables off #开机不启动该服务

service iptables start #启动该服务

service iptables restart #重启启该服务

ps -ef|grep mysql|grep -v grep|awk '{print $2}'ps -ef|grep mysql 是查询mysql服务的进程

|后的grep -v grep 是匹配不包含grep的行

awk是取查询结果的第几列，awk '{print $2}'则是取第二列的值

grep 无参数则显示匹配的行

-c 显示匹配的行数

-v 显示不匹配的行

#### 杀掉进程 kill 命令

kill最常用的参数是-9，用法：kill -9 进程号 即可强制杀掉该进程

#### **统计命令** **wc**

常用的参数是 -l 用法：wc -l ，例：

ps -ef|grep mysql|wc -l 统计查询出的mysql进程的行数

#### 查找命令 find/locate/whereis/which

find -name 后跟文件名，可查看文件所在目录，可跟user，查看属于user的文件

find -name filename 查找filename所在目录

find -name name* 查找开头为name的文件所在目录

find -name *name 查找结尾为name的文件所在目录

find -name *name* 查找包含name字符串的文件所在目录

find -user faith 查看用户faith的文件

locate用法与find基本相似，只是locate搜索速度较快些，locate一般系统不会自带，需要安装，可用yum安装

whereis只能搜索程序名

which则是只查询path中的环境变量

 

#### 压缩和解压命令 gzip/guzip  zip/unzip  tar

gzip和gunzip一般可用参数是-r，例:

gzip test.txt　　压缩文件

gzip -r test 　　压缩所有test下的子文件

gunzip test.gz 　　解压文件

zip和unzip可用参数较多，例：

zip test 不跟参数直接使用

zip -r test 递归压缩test下所有文件

unzip test 不跟参数直接使用

unzip -n 解压时不覆盖已存在的文件

unzip -o 解压时覆盖已存在的文件

unzip -d 将文件解压到目录中去

tar使用的较为多些，用法也多，最常用的是zxcvf几个参数，例：

-c 创建新文档，就是代表压缩的意思

-x 解压文档

-f 使用归档文件

-z 使用gzip解压

-v 详细输出模式

最为常用的使用方法：

tar -zcvf test.tar test 将test压缩为test.tar并输出详细信息

tar -zxvf test.tar 将test.tar解压缩，并输出详细信息

#### 定时任务 crontab

crontab [-u user] 文件

crontab [-u user] {-r -e -l}

不加-u的话默认当前用户

－e：执行文字编辑器来设定时程表，内定的文字编辑器是vi。

－r：删除目前的时程表。

－l：列出目前的时程表。

crontab -e 就可以打开一个文件进行编辑

 crontab文件的格式为“M H D m d cmd”，M为分钟1-59，H为小时1-24，D为天1-31，m为月1-12，d为周0-6（0为周日）。cmd代表要执行的程序，*代表每分钟都执行

\* * * * * sh /opt/lampp/test.sh 表示每分钟执行一次test.sh这个脚本

*/5 * * * * sh /opt/lampp/test.sh 表示每5分钟执行一次test.sh这个脚本

30 21 * * * /usr/local/apache/bin/apachectl restart 表示每晚的21:30重启apache



### python脚本

#### http文件传输

```python
#!/usr/bin/env python
#coding=utf-8
# modifyDate: 20120808 ~ 20120810
# 原作者为：bones7456, http://li2z.cn/
# 修改者为：decli@qq.com
# v1.2，changeLog：
# +: 文件日期/时间/颜色显示、多线程支持、主页跳转
# -: 解决不同浏览器下上传文件名乱码问题：仅IE，其它浏览器暂时没处理。
# -: 一些路径显示的bug，主要是 cgi.escape() 转义问题
# ?: notepad++ 下直接编译的server路径问题
 
"""
    简介：这是一个 python 写的轻量级的文件共享服务器（基于内置的SimpleHTTPServer模块），
    支持文件上传下载，只要你安装了python（建议版本2.6~2.7，不支持3.x），
    然后去到想要共享的目录下，执行：
        python SimpleHTTPServerWithUpload.py 1234       
    其中1234为你指定的端口号，如不写，默认为 8000
    然后访问 http://localhost:1234 即可，localhost 或者 1234 请酌情替换。
"""
 
"""Simple HTTP Server With Upload.
 
This module builds on BaseHTTPServer by implementing the standard GET
and HEAD requests in a fairly straightforward manner.
 
"""
 
 
__version__ = "0.1"
__all__ = ["SimpleHTTPRequestHandler"]
__author__ = "bones7456"
__home_page__ = ""
 
import os, sys, platform
import posixpath
import BaseHTTPServer
from SocketServer import ThreadingMixIn
import threading
import urllib
import cgi
import shutil
import mimetypes
import re
import time
 
 
try:
    from cStringIO import StringIO
except ImportError:
    from StringIO import StringIO
     
 
print ""
print '----------------------------------------------------------------------->> '
try:
   port = int(sys.argv[1])
except Exception, e:
   print '-------->> Warning: Port is not given, will use deafult port: 8000 '
   print '-------->> if you want to use other port, please execute: '
   print '-------->> python SimpleHTTPServerWithUpload.py port '
   print "-------->> port is a integer and it's range: 1024 < port < 65535 "
   port = 8915
    
if not 1024 < port < 65535:  port = 8090
serveraddr = ('', port)
print '-------->> Now, listening at port ' + str(port) + ' ...'
print '-------->> You can visit the URL:   http://localhost:' + str(port)
print '----------------------------------------------------------------------->> '
print ""
     
 
def sizeof_fmt(num):
    for x in ['bytes','KB','MB','GB']:
        if num < 1024.0:
            return "%3.1f%s" % (num, x)
        num /= 1024.0
    return "%3.1f%s" % (num, 'TB')
 
def modification_date(filename):
    # t = os.path.getmtime(filename)
    # return datetime.datetime.fromtimestamp(t)
    return time.strftime("%Y-%m-%d %H:%M:%S",time.localtime(os.path.getmtime(filename)))
 
class SimpleHTTPRequestHandler(BaseHTTPServer.BaseHTTPRequestHandler):
 
    """Simple HTTP request handler with GET/HEAD/POST commands.
 
    This serves files from the current directory and any of its
    subdirectories.  The MIME type for files is determined by
    calling the .guess_type() method. And can reveive file uploaded
    by client.
 
    The GET/HEAD/POST requests are identical except that the HEAD
    request omits the actual contents of the file.
 
    """
 
    server_version = "SimpleHTTPWithUpload/" + __version__
 
    def do_GET(self):
        """Serve a GET request."""
        # print "....................", threading.currentThread().getName()
        f = self.send_head()
        if f:
            self.copyfile(f, self.wfile)
            f.close()
 
    def do_HEAD(self):
        """Serve a HEAD request."""
        f = self.send_head()
        if f:
            f.close()
 
    def do_POST(self):
        """Serve a POST request."""
        r, info = self.deal_post_data()
        #print r, info, "by: ", self.client_address
        f = StringIO()
        f.write('<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">')
        f.write("<html>\n<title>Upload Result Page</title>\n")
        f.write("<body>\n<h2>Upload Result Page</h2>\n")
        f.write("<hr>\n")
        if r:
            f.write("<strong>Success:</strong>")
        else:
            f.write("<strong>Failed:</strong>")
        f.write(info)
        f.write("<br><a href=\"%s\">back</a>" % self.headers['referer'])
        f.write("<hr><small>Powered By: bones7456, check new version at ")
        f.write("<a href=\"http://li2z.cn/?s=SimpleHTTPServerWithUpload\">")
        f.write("here</a>.</small></body>\n</html>\n")
        length = f.tell()
        f.seek(0)
        self.send_response(200)
        self.send_header("Content-type", "text/html")
        self.send_header("Content-Length", str(length))
        self.end_headers()
        if f:
            self.copyfile(f, self.wfile)
            f.close()
         
    def deal_post_data(self):
        boundary = self.headers.plisttext.split("=")[1]
        remainbytes = int(self.headers['content-length'])
        line = self.rfile.readline()
        remainbytes -= len(line)
        if not boundary in line:
            return (False, "Content NOT begin with boundary")
        line = self.rfile.readline()
        remainbytes -= len(line)
        fn = re.findall(r'Content-Disposition.*name="file"; filename="(.*)"', line)
        if not fn:
            return (False, "Can't find out file name...")
        path = self.translate_path(self.path)
        osType = platform.system()
        try:
            if osType == "Linux":
                fn = os.path.join(path, fn[0].decode('gbk').encode('utf-8'))
            else:
                fn = os.path.join(path, fn[0])
        except Exception, e:
            return (False, "文件名请不要用中文，或者使用IE上传中文名的文件。")
        while os.path.exists(fn):
            fn += "_"
        line = self.rfile.readline()
        remainbytes -= len(line)
        line = self.rfile.readline()
        remainbytes -= len(line)
        try:
            out = open(fn, 'wb')
        except IOError:
            return (False, "Can't create file to write, do you have permission to write?")
                 
        preline = self.rfile.readline()
        remainbytes -= len(preline)
        while remainbytes > 0:
            line = self.rfile.readline()
            remainbytes -= len(line)
            if boundary in line:
                preline = preline[0:-1]
                if preline.endswith('\r'):
                    preline = preline[0:-1]
                out.write(preline)
                out.close()
                return (True, "File '%s' upload success!" % fn)
            else:
                out.write(preline)
                preline = line
        return (False, "Unexpect Ends of data.")
 
    def send_head(self):
        """Common code for GET and HEAD commands.
 
        This sends the response code and MIME headers.
 
        Return value is either a file object (which has to be copied
        to the outputfile by the caller unless the command was HEAD,
        and must be closed by the caller under all circumstances), or
        None, in which case the caller has nothing further to do.
 
        """
        path = self.translate_path(self.path)
        f = None
        if os.path.isdir(path):
            if not self.path.endswith('/'):
                # redirect browser - doing basically what apache does
                self.send_response(301)
                self.send_header("Location", self.path + "/")
                self.end_headers()
                return None
            for index in "index.html", "index.htm":
                index = os.path.join(path, index)
                if os.path.exists(index):
                    path = index
                    break
            else:
                return self.list_directory(path)
        ctype = self.guess_type(path)
        try:
            # Always read in binary mode. Opening files in text mode may cause
            # newline translations, making the actual size of the content
            # transmitted *less* than the content-length!
            f = open(path, 'rb')
        except IOError:
            self.send_error(404, "File not found")
            return None
        self.send_response(200)
        self.send_header("Content-type", ctype)
        fs = os.fstat(f.fileno())
        self.send_header("Content-Length", str(fs[6]))
        self.send_header("Last-Modified", self.date_time_string(fs.st_mtime))
        self.end_headers()
        return f
 
    def list_directory(self, path):
        """Helper to produce a directory listing (absent index.html).
 
        Return value is either a file object, or None (indicating an
        error).  In either case, the headers are sent, making the
        interface the same as for send_head().
 
        """
        try:
            list = os.listdir(path)
        except os.error:
            self.send_error(404, "No permission to list directory")
            return None
        list.sort(key=lambda a: a.lower())
        f = StringIO()
        displaypath = cgi.escape(urllib.unquote(self.path))
        f.write('<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">')
        f.write("<html>\n<title>Directory listing for %s</title>\n" % displaypath)
        f.write("<body>\n<h2>Directory listing for %s</h2>\n" % displaypath)
        f.write("<hr>\n")
        f.write("<form ENCTYPE=\"multipart/form-data\" method=\"post\">")
        f.write("<input name=\"file\" type=\"file\"/>")
        f.write("<input type=\"submit\" value=\"upload\"/>")
        f.write("&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp")
        f.write("<input type=\"button\" value=\"HomePage\" onClick=\"location='/'\">")
        f.write("</form>\n")
        f.write("<hr>\n<ul>\n")
        for name in list:
            fullname = os.path.join(path, name)
            colorName = displayname = linkname = name
            # Append / for directories or @ for symbolic links
            if os.path.isdir(fullname):
                colorName = '<span style="background-color: #CEFFCE;">' + name + '/</span>'
                displayname = name
                linkname = name + "/"
            if os.path.islink(fullname):
                colorName = '<span style="background-color: #FFBFFF;">' + name + '@</span>'
                displayname = name
                # Note: a link to a directory displays with @ and links with /
            filename = os.getcwd() + '/' + displaypath + displayname
            f.write('<table><tr><td width="60%%"><a href="%s">%s</a></td><td width="20%%">%s</td><td width="20%%">%s</td></tr>\n'
                    % (urllib.quote(linkname), colorName,
                        sizeof_fmt(os.path.getsize(filename)), modification_date(filename)))
        f.write("</table>\n<hr>\n</body>\n</html>\n")
        length = f.tell()
        f.seek(0)
        self.send_response(200)
        self.send_header("Content-type", "text/html")
        self.send_header("Content-Length", str(length))
        self.end_headers()
        return f
 
    def translate_path(self, path):
        """Translate a /-separated PATH to the local filename syntax.
 
        Components that mean special things to the local file system
        (e.g. drive or directory names) are ignored.  (XXX They should
        probably be diagnosed.)
 
        """
        # abandon query parameters
        path = path.split('?',1)[0]
        path = path.split('#',1)[0]
        path = posixpath.normpath(urllib.unquote(path))
        words = path.split('/')
        words = filter(None, words)
        path = os.getcwd()
        for word in words:
            drive, word = os.path.splitdrive(word)
            head, word = os.path.split(word)
            if word in (os.curdir, os.pardir): continue
            path = os.path.join(path, word)
        return path
 
    def copyfile(self, source, outputfile):
        """Copy all data between two file objects.
 
        The SOURCE argument is a file object open for reading
        (or anything with a read() method) and the DESTINATION
        argument is a file object open for writing (or
        anything with a write() method).
 
        The only reason for overriding this would be to change
        the block size or perhaps to replace newlines by CRLF
        -- note however that this the default server uses this
        to copy binary data as well.
 
        """
        shutil.copyfileobj(source, outputfile)
 
    def guess_type(self, path):
        """Guess the type of a file.
 
        Argument is a PATH (a filename).
 
        Return value is a string of the form type/subtype,
        usable for a MIME Content-type header.
 
        The default implementation looks the file's extension
        up in the table self.extensions_map, using application/octet-stream
        as a default; however it would be permissible (if
        slow) to look inside the data to make a better guess.
 
        """
 
        base, ext = posixpath.splitext(path)
        if ext in self.extensions_map:
            return self.extensions_map[ext]
        ext = ext.lower()
        if ext in self.extensions_map:
            return self.extensions_map[ext]
        else:
            return self.extensions_map['']
 
    if not mimetypes.inited:
        mimetypes.init() # try to read system mime.types
        extensions_map = mimetypes.types_map.copy()
        extensions_map.update({
        '': 'application/octet-stream', # Default
        '.py': 'text/plain',
        '.c': 'text/plain',
        '.h': 'text/plain',
        })
 
class ThreadingServer(ThreadingMixIn, BaseHTTPServer.HTTPServer):
    pass
     
def test(HandlerClass = SimpleHTTPRequestHandler,
       ServerClass = BaseHTTPServer.HTTPServer):
    BaseHTTPServer.test(HandlerClass, ServerClass)
 
if __name__ == '__main__':
    # test()
     
    #单线程
    # srvr = BaseHTTPServer.HTTPServer(serveraddr, SimpleHTTPRequestHandler)
     
    #多线程
    srvr = ThreadingServer(serveraddr, SimpleHTTPRequestHandler)
    srvr.serve_forever() 


```

#### 

