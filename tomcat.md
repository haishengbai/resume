## WEBSERVER-Tomcat

### Tomcat体系架构

#### Web服务器

##### Web服务器定义

Web服务器一般指网站服务器，是指驻留于因特网上某种类型计算机的程序，可以向浏览器等Web客户端提供文档，也可以放置网站文件，让全世界浏览；可以放置数据文件，让全世界下载

##### Web服务器介绍

1、服务器是一种被动程序：只有当Internet上运行其他计算机中的浏览器发出的请求时，服务器才会响应。

2、服务器一般使用HTTP（超文本传输协议）与客户机浏览器进行信息交流，这就是人们常把它们称为HTTP服务器的原因。

3、Web服务器不仅能够存储信息，还能在用户通过Web浏览器提供的信息的基础上运行脚本和程序。

#### Tomcat

Tomcat是一款开源轻量级Web应用服务器,是一款优秀的Servlet容器实现。

Servlet（Server Applet）是[Java](https://baike.baidu.com/item/Java/85979) Servlet的简称，称为小服务程序或服务连接器，用Java编写的[服务器](https://baike.baidu.com/item/服务器/100571)端程序，具有独立于平台和[协议](https://baike.baidu.com/item/协议/13020269)的特性，主要功能在于交互式地浏览和生成数据，生成动态[Web](https://baike.baidu.com/item/Web/150564)内容。

Servlet严格来讲是指Java语言实现的一个接口，一般情况下我们说的Servlet是指任何实现了这个Servlet接口的类。

- 实例化并调用init()方法初始化该 Servlet，一般 Servlet 只初始化一次(只有一个对象)
- service()（根据请求方法不同调用doGet() 或者 doPost()，此外还有doHead()、doPut()、doTrace()、doDelete()、doOptions()、destroy())。
- 当 Server 不再需要 Servlet 时（一般当 Server 关闭时），Server 调用 Servlet 的 destroy() 方法。

典型的Servlet的处理流程

1. 第一个到达服务器的 HTTP 请求被委派到 Servlet 容器。
2. Servlet 容器在调用 service() 方法之前加载 Servlet。
3. 然后 Servlet 容器处理由多个线程产生的多个请求，每个线程执行一个单一的 Servlet 实例的 service() 方法。

#### Tomcat版本

Servlet2.X：项目目录结构必须要有WEB-INF，web.xml等文件夹和文件，在web.xml中配置servlet,filter,listener，以web.xml为java web项目的统一入口

 servlet 3.x规范：项目中可以不需要WEB-INF，web.xml等文件夹和文件，在没有web.xml文件的情况下，通过注解实现servlet，filter, listener的声明，当使用注解时，容器自动进行扫描。

同时Tomcat8.5进行了大量的代码重构，对比与7.0的版本，也符合Tomcat未来的代码架构体系。但是Tomcat的核心和主体架构还是一直保持这样的。

##### 8.5版本特点

支持Servlet3.1

默认采用NIO，移除BIO

支持NIO2(AIO)

支持HTTP/2协议

默认采用异步日志处理

为什么要讲8.5的版本，首先这个版本比较新，因为太老的版本比如6.0的版本Servlet不支持3所以会导致部署SpringBoot等项目有问题，同时这个版本是在9.0出现以后发布的一个中间版本，主体架构延续8.0，同时又实现了部分9.0的新特性。

#### Tomcat启动

一般启动

`startup.bat/sh`

IDE中启动

嵌入式启动

Debug启动

在项目发布后，我们有时候需要对基于生产环境部署的应用进行调试，以解决在开发环境无法重现的BUG。这时我们就需要用到应用服务器的远程调试功能，这个主要是基于JDK提供的JPDA（Java Platform Debugger Architecture,Java平台调试体系结构）。不过一般情况下用不到，这里简单讲一讲。

使用DeBug启动可以对基于生产环境部署的应用进行调试，以解决在开发环境无法重现的BUG。

使用IDEA远程部署

1.在catalina.sh文件中加入以下的配置

```
CATALINA_OPTS="-Dcom.sun.management.jmxremote 
-Dcom.sun.management.jmxremote.port=1099 
-Dcom.sun.management.jmxremote.ssl=false 
-Dcom.sun.management.jmxremote.authenticate=false 
-Djava.rmi.server.hostname=192.168.19.200
-agentlib:jdwp=transport=dt_socket,address=15833,suspend=n,server=y"export CATALINA_OPTS
```

· 以上端口可以随意改动，但是必要的是后续的设置必须保持一致，并且务必保证端口没有被占用，这些设置的端口在防火墙中是开放状态；

· 其中1099的是tomcat远程部署连接端口；

· 15833 是远程调试的端口；

· 192.168.19.200是远程的服务器的Ip。

2.在Linux上启动tomcat,使用命令启动

```sh
./bin/catalina.sh run &
```

#### Tomcat部署及目录结构

##### 项目部署

###### 隐式部署

直接丢文件夹、war、jar到webapps目录，tomcat会根据文件夹名称自动生成虚拟路径，简单，但是需要重启Tomcat服务器，包括要修改端口和访问路径的也需要重启。

###### 显式部署

- 添加context元素

  server.xml中的Host加入一个Context（指定路径和文件地址），例如：

  <Host name="localhost">

  <Context path="/comet" docBase="D:\work_tomcat\ref-comet.war" />

  即/comet 这个虚拟路径映射到了D:\work_tomcat\ref-comet目录下(war会解压成文件)，修改完servler.xml需要重启tomcat 服务器。

- 创建xml文件

  在conf/Catalina/localhost中创建xml文件，访问路径为文件名，例如：

  在localhost目录下新建demo.xml，内容为：

  <Context docBase="D:\work_tomcat\ref-comet" />

  不需要写path，虚拟目录就是文件名demo，path默认为/demo，添加demo.xml不需要重启 tomcat服务器。

###### 三种方式比较

隐式部署：可以很快部署,需要人手动移动Web应用到webapps下，在实际操作中不是很人性化

添加context元素 : 配置速度快,需要配置两个路径，如果path为空字符串，则为缺省配置,每次修改server.xml文件后都要重新启动Tomcat服务器，重新部署.

创建xml文件:服务器后台会自动部署，修改一次后台部署一次，不用重复启动Tomcat服务器,该方式显得更为智能化。

##### bin执行脚本目录

- startup文件，主要是检查catalina.bat/sh 执行所需环境，并调用catalina.bat 批处理文件。启动tomcat。

- catalina文件，真正启动Tomcat文件，可以在里面设置jvm参数。后面性能调优会重点讲

- shutdown文件，关闭Tomcat

- 脚本version.sh、startup.sh、shutdown.sh、configtest.sh都是对catalina.sh的包装，内容大同小异，差异在于功能介绍和调用catalina.sh时的参数不同。

- Version：查看当前tomcat的版本号，

- Configtest：校验tomcat配置文件server.xml的格式、内容等是否合法、正确。

- Service：安装tomcat服务，可用net start tomcat 启动

- web.xml

  Tomcat中所有应用默认的部署描述文件，主要定义了基础的Servlet和MIME映射(mime-mapping 文件类型，其实就是Tomcat处理的文件类型),如果部署的应用中不包含Web.xml，那么Tomcat将使用此文件初始化部署描述，反之，Tomcat会在启动时将默认描述与定义描述配置进行合并。

  加载一些tomcat内置的servlet

  DefaultServlet默认的,加载静态文件 html,js,jpg等静态文件。

  JspServlet专门处理jsp。

- context.xml

  用于自定义所有Web应用均需要加载的Context配置，如果Web应用指定了自己的context.xml，那么该文件的配置将被覆盖。

  context.xml与server.xml中配置context的区别

  server.xml是不可动态重加载的资源，服务器一旦启动了以后，要修改这个文件，就得重启服务器才能重新加载。而context.xml文件则不然，tomcat服务器会定时去扫描这个文件。一旦发现文件被修改（时间戳改变了），就会自动重新加载这个文件，而不需要重启服务器。

- catalina.policy

  权限相关 Permission ，Tomcat是跑在jvm上的，所以有些默认的权限

- Tomcat-users.xml

  配置Tomcat的server的manager信息

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <tomcat-users xmlns="http://tomcat.apache.org/xml"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://tomcat.apache.org/xml tomcat-users.xsd"
                version="1.0">
  <role rolename="manager-gui"/>
  <user username="manager" password="manager" roles="manager-gui"/>
  </tomcat-users>
  ```

- logging.properties

  设置tomcat日志

  控制输出不输出内容到文件，不能阻止生成文件，阻止声文件可用注释掉

##### webapps目录

存放web项目的目录，其中每个文件夹都是一个项目；如果这个目录下已经存在了目录，那么都是tomcat自带的。项目。其中ROOT是一个特殊的项目，在地址栏中没有给出项目目录时，对应的就是ROOT项目。http://localhost:8080/examples，进入示例项目。其中examples就是项目名，即文件夹的名字。

##### lib目录

Tomcat的类库，里面是一大堆jar文件。如果需要添加Tomcat依赖的jar文件，可以把它放到这个目录中，当然也可以把应用依赖的jar文件放到这个目录中，这个目录中的jar所有项目都可以共享之，但这样你的应用放到其他Tomcat下时就不能再共享这个目录下的Jar包了，所以建议只把Tomcat需要的Jar包放到这个目录下；

##### temp目录

运行时生成的文件，最终运行的文件都在这里。通过webapps中的项目生成的！可以把这个目录下的内容删除，再次运行时会生再次生成work目录。当客户端用户访问一个JSP文件时，Tomcat会通过JSP生成Java文件，然后再编译Java文件生成class文件，生成的java和class文件都会存放到这个目录下。

##### logs目录

这个目录中都是日志文件，记录了Tomcat启动和关闭的信息，如果启动Tomcat时有错误，那么异常也会记录在日志文件中

localhost-xxx.log  Web应用的内部程序日志，建议保留

catalina-xxx.log   控制台日志

host-manager.xxx.log  Tomcat管理页面中的host-manager的操作日志，建议关闭

localhost_access_log_xxx.log 用户请求Tomcat的访问日志（这个文件在conf/server.xml里配置），建议关闭

##### conf配置文件目录

###### server.xml

####  

```xml
<?xml version="1.0" encoding="UTF-8"?>
 
<!-- Server代表一个 Tomcat 实例。可以包含一个或多个 Services，其中每个Service都有自己的Engines和Connectors。
       port="8005"指定一个端口，这个端口负责监听关闭tomcat的请求
  -->
<Server port="8005" shutdown="SHUTDOWN">
<!-- 监听器 -->
<Listener className="org.apache.catalina.startup.VersionLoggerListener" />
<Listener className="org.apache.catalina.core.AprLifecycleListener" SSLEngine="on" />
<Listener className="org.apache.catalina.core.JreMemoryLeakPreventionListener" />
<Listener className="org.apache.catalina.mbeans.GlobalResourcesLifecycleListener" />
<Listener className="org.apache.catalina.core.ThreadLocalLeakPreventionListener" />
<!-- 全局命名资源，定义了UserDatabase的一个JNDI(java命名和目录接口)，通过pathname的文件得到一个用户授权的内存数据库 -->
<GlobalNamingResources>
<Resource name="UserDatabase" auth="Container"
               type="org.apache.catalina.UserDatabase"
               description="User database that can be updated and saved"
               factory="org.apache.catalina.users.MemoryUserDatabaseFactory"
               pathname="conf/tomcat-users.xml" />
</GlobalNamingResources>
<!-- Service它包含一个<Engine>元素,以及一个或多个<Connector>,这些Connector元素共享用同一个Engine元素 -->
<Service name="Catalina">
<!-- 
         每个Service可以有一个或多个连接器<Connector>元素，
         第一个Connector元素定义了一个HTTP Connector,它通过8080端口接收HTTP请求;第二个Connector元素定
         义了一个JD Connector,它通过8009端口接收由其它服务器转发过来的请求.
     -->
<Connector port="8080" protocol="HTTP/1.1"
                connectionTimeout="20000"
                redirectPort="8443" />
<Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />
<!-- 每个Service只能有一个<Engine>元素 -->
<Engine name="Catalina" defaultHost="localhost">
<Realm className="org.apache.catalina.realm.LockOutRealm">
<Realm className="org.apache.catalina.realm.UserDatabaseRealm"
                resourceName="UserDatabase"/>
</Realm>
<!-- 默认host配置，有几个域名就配置几个Host，但是这种只能是同一个端口号 -->
<Host name="localhost"  appBase="webapps"
             unpackWARs="true" autoDeploy="true">
     　　<!-- Tomcat的访问日志，默认可以关闭掉它，它会在logs文件里生成localhost_access_log的访问日志 -->
<Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
                prefix="localhost_access_log" suffix=".txt"
                pattern="%h %l %u %t "%r" %s %b" />
</Host>
<Host name="www.hzg.com"  appBase="webapps"
             unpackWARs="true" autoDeploy="true">
<Context path="" docBase="/myweb1" />
<Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
                prefix="hzg_access_log" suffix=".txt"
                pattern="%h %l %u %t "%r" %s %b" />
</Host>
</Engine>
</Service>
</Server>
```

server.xml中日志的patter解释

有效的日志格式模式可以参见下面内容，如下字符串，其对应的信息由指定的响应内容取代：
 ％a - 远程IP地址
 ％A - 本地IP地址
 ％b - 发送的字节数，不包括HTTP头，或“ - ”如果没有发送字节
 ％B - 发送的字节数，不包括HTTP头
 ％h - 远程主机名
 ％H - 请求协议
 ％l (小写的L)- 远程逻辑从identd的用户名（总是返回' - '）
 ％m - 请求方法
 ％p - 本地端口
 ％q - 查询字符串（在前面加上一个“？”如果它存在，否则是一个空字符串
 ％r - 第一行的要求
 ％s - 响应的HTTP状态代码
 ％S - 用户会话ID
 ％t - 日期和时间，在通用日志格式
 ％u - 远程用户身份验证
 ％U - 请求的URL路径
 ％v - 本地服务器名
 ％D - 处理请求的时间（以毫秒为单位）
 ％T - 处理请求的时间（以秒为单位）
 ％I （大写的i） - 当前请求的线程名称

##### Tomcat组件及架构

###### server

Server是最顶级的组件，它代表Tomcat的运行实例，它掌管着整个Tomcat的生死大权；

Ø 提供了监听器机制，用于在Tomcat整个生命周期中对不同时间进行处理

Ø 提供Tomcat容器全局的命名资源实现，JNDI

Ø 监听某个端口以接受SHUTDOWN命令，用于关闭Tomcat

###### service

一个概念，一个Service维护多个Connector和一个Container

###### connector组件

链接器：监听转换Socket请求，将请求交给Container处理，支持不同协议以及不同的I/O方式

###### container

表示能够执行客户端请求并返回响应的一类对象，其中有不同级别的容器：Engine、Host、Context、Wrapper

###### engine

整个Servler引擎，最高级的容器对象

###### host

表示Servlet引擎中的虚拟机，主要与域名有关，一个服务器有多个域名是可以使用多个Host

###### context

用于表示ServletContext,一个ServletContext表示一个独立的Web应用

###### wrapper

用于表示Web应用中定义的Servlet

###### executor

Tomcat组件间可以共享的线程池

#### Tomcat的核心组件

解耦：网络协议与容器的解耦。

Connector链接器封装了底层的网络请求(Socket请求及相应处理),提供了统一的接口，使Container容器与具体的请求协议以及I/O方式解耦。

Connector将Socket输入转换成Request对象，交给Container容器进行处理，处理请求后，Container通过Connector提供的Response对象将结果写入输出流。

因为无论是Request对象还是Response对象都没有实现Servlet规范对应的接口，Container会将它们进一步分装成ServletRequest和ServletResponse.

#### Tomcat的链接器

AJP主要是用于Web服务器与Tomcat服务器集成，AJP采用二进制传输可读性文本，使用保持持久性的TCP链接，使得AJP占用更少的带宽，并且链接开销要小得多，但是由于AJP采用持久化链接，因此有效的连接数较HTTP要更多。

HTTP2.0目前市场不成熟，这个技术点后续我们的三期、四期如果市面上协议很普遍了会考虑加入。

对于I/0选择，要根据业务场景来定，一般高并发场景下，APR和NIO2的性能要优于NIO和BIO，（linux操作系统支持的NIO2由于是一个假的，并没有真正实现AIO，所以一般linux上推荐使用NIO，如果是APR的话，需要安装APR库，而Windows上默认安装了），所以在8.5的版本中默认是NIO。

### Tomcat源码分析

#### tomcat源码目录

- catalina目录

  catalina包含所有的Servlet容器实现，以及涉及到安全、会话、集群、部署管理Servlet容器的各个方面，同时，它还包含了启动入口。

- coyote目录

  coyote是Tomcat链接器框架的名称，是Tomcat服务器提供的客户端访问的外部接口，客户端通过Coyote与服务器建立链接、发送请求并接收响应。

- EL目录

  提供java表达式语言

- Jasper

  提供JSP引擎

- Naming

  提供JNDI的服务

- Juli

  提供服务器日志的服务

- tomcat

  提供外部调用的接口api

#### tomcat启动流程分析

\1.   启动流程解析：注意是标准的启动，也就是从bin目录下的启动文件中启动Tomcat

，Tomcat的启动非常的标准，除去Boostrap和Catalin，我们可以对照一下Server.xml的配置文件。Server,service等等这些组件都是一一对照，同时又有先后顺序。

基本的顺序是先init方法，然后再start方法。

\2.   加入调试信息()：注意是标准的启动，也就是从bin目录下的启动文件中启动Tomcat可以看到，在源码中加入调试的信息和流程图是一致的。

我们可以看到，除了Bootstrap和catalina类，其他的Server,service等等之类的都只是一个接口，实现类均为StandardXXX类。

我们来看下StandardServer类，

问题来了，我们发现StandardServer类中没有init方法，只有一个类似于init的initInternal方法，这个是为什么？

带着问题我们进入下面的内容。

#### 分析tomcat请求过程

解耦：网络协议与容器的解耦。

Connector链接器封装了底层的网络请求(Socket请求及相应处理),提供了统一的接口，使Container容器与具体的请求协议以及I/O方式解耦。

Connector将Socket输入转换成Request对象，交给Container容器进行处理，处理请求后，Container通过Connector提供的Response对象将结果写入输出流。

因为无论是Request对象还是Response对象都没有实现Servlet规范对应的接口，Container会将它们进一步分装成ServletRequest和ServletResponse.

问题来了，在Engine容器中，有四个级别的容器，他们的标准实现分别是StandardEngine、StandardHost、StandardContext、StandardWrapper。

#### 组件的生命周期管理

###### 各种组件如果统一管理

Tomcat的架构设计是清晰的、模块化、它拥有很多组件，加入在启动Tomcat时一个一个组件启动，很容易遗漏组件，同时还会对后面的动态组件拓展带来麻烦。如果采用我们传统的方式的话，组件在启动过程中如果发生异常，会很难管理，比如你的下一个组件调用了start方法，但是如果它的上级组件还没有start甚至还没有init的话，Tomcat的启动会非常难管理，因此，Tomcat的设计者提出一个解决方案：用Lifecycle管理启动，停止、关闭。

###### 生命周期统一接口-Lifecycle

omcat内部架构中各个核心组件有包含与被包含关系，例如：Server包含了Service.Service又包含了Container和Connector,这个结构有一点像数据结构中的树，树的根结点没有父节点，其他节点有且仅有一个父节点，每一个父节点有0至多个子节点。所以，我们可以通过父容器启动它的子容器，这样只要启动根容器，就可以把其他所有的容器都启动，从而达到了统一的启动，停止、关闭的效果。

所有所有组件有一个统一的接口——Lifecycle,把所有的启动、停止、关闭、生命周期相关的方法都组织到一起，就可以很方便管理Tomcat各个容器组件的生命周期。

Lifecycle其实就是定义了一些状态常量和几个方法，主要方法是init,start,stop三个方法。

例如：Tomcat的Server组件的init负责遍历调用其包含所有的Service组件的init方法。

注意：Server只是一个接口，实现类为StandardServer,有意思的是，StandardServer没有init方法，init方法是在哪里，其实是在它的父类LifecycleBase中，这个类就是统一的生命周期管理。

所以StandardServer最终只会调用到initInternal方法，这个方法会初始化子容器Service的init方法

为什么LifecycleBase这么玩，其实很多架构源码都是这么玩的，包括JDK的容器源码都是这么玩的，一个类，有一个接口，同时抽象一个抽象骨架类，把通用的实现放在抽象骨架类中，这样设计就方便组件的管理，使用LifecycleBase骨架抽象类，在抽象方法中就可以进行统一的处理，具体的内容见下面。

抽象类LifecycleBase统一管理组件生命周期

#### 分析tomcat请求过程

##### host设计的目的

Tomcat诞生时，服务器资源很贵，所以一般一台服务器其实可以有多个域名映射，满了满足这种需求，比如，我的这台电脑，有一个localhost域名，同时在我的hosts文件中配置两个域名，一个www.a.com 一个localhost。

##### context设计的目的

container从上一个组件connector手上接过解析好的内部request，根据request来进行一系列的逻辑操作，直到调用到请求的servlet，然后组装好response，返回给connecotr

先来看看container的分类吧：

1. Engine
2. Host
3. Context
4. Wrapper

 它们各自的实现类分别是StandardEngine, StandardHost, StandardContext, and StandardWrapper，他们都在tomcat的org.apache.catalina.core包下。

它们之间的关系，可以查看tomcat的server.xml也能明白（根据节点父子关系），这么比喻吧：除了Wrapper最小，不能包含其他container外，Context内可以有零或多个Wrapper，Host可以拥有零或多个Context，Engine可以有零到多个Host。

Standard 的 container都是直接继承抽象类：org.apache.catalina.core.ContainerBase：

##### tomcat处理一个HTTP请求过程

用户点击网页内容，请求被发送到本机端口8080，被在那里监听的Coyote HTTP/1.1 Connector获得。

Connector把该请求交给它所在的Service的Engine来处理，并等待Engine的回应。

Engine获得请求localhost/test/index.jsp，匹配所有的虚拟主机Host。

Engine匹配到名为localhost的Host（即使匹配不到也把请求交给该Host处理，因为该Host被定义为该Engine的默认主机），名为localhost的Host获得请求/test/index.jsp，匹配它所拥有的所有的Context。Host匹配到路径为/test的Context（如果匹配不到就把该请求交给路径名为“ ”的Context去处理）。

path=“/test”的Context获得请求/index.jsp，在它的mapping table中寻找出对应的Servlet。Context匹配到URL PATTERN为*.jsp的Servlet,对应于JspServlet类。

构造HttpServletRequest对象和HttpServletResponse对象，作为参数调用JspServlet的doGet（）或doPost（）.执行业务逻辑、数据存储等程序。

Context把执行完之后的HttpServletResponse对象返回给Host。

Host把HttpServletResponse对象返回给Engine。

Engine把HttpServletResponse对象返回Connector。

Connector把HttpServletResponse对象返回给客户Browser。

#### 管道模式

###### 管道与阀门

在管道中连接一个或者多个阀门，每一个阀门负责一部分逻辑处理，数据按照规定的顺序往下流。此种模式分解了逻辑处理任务，可方便对某个任务单元进行安装、拆卸，提高流程的可拓展性，可重用性，机动性，灵活性。

###### 源码分析



###### Tomcat中定制阀门

管道机制给我们带来了更好的拓展性，例如，你要添加一个额外的逻辑处理阀门是很容易的。

1. 自定义个阀门PrintIPValve，只要继承ValveBase并重写invoke方法即可。注意在invoke方法中一定要执行调用下一个阀门的操作，否则会出现异常。

   ```java
   public class PrintIPValve extends ValveBase{
   
     @Override
   
     public void invoke(Request request, Response response) throws IOException, ServletException {
   
      System.out.println("------自定义阀门PrintIPValve:"+request.getRemoteAddr());
   
      getNext().invoke(request,response);
   
     }
   
   }
   ```

   

2. 配置Tomcat的核心配置文件server.xml,这里把阀门配置到Engine容器下，作用范围就是整个引擎，也可以根据作用范围配置在Host或者是Context下

   ```xml
   <**Valve** **className****="org.apache.catalina.valves.PrintIPValve"**/>
   ```

   

3. 源码中是直接可以有效果，但是如果是运行版本，则可以将这个类导出成一个Jar包放入Tomcat/lib目录下，也可以直接将.class文件打包进catalina.jar包中。

######  Tomcat中提供常用的阀门

AccessLogValve，请求访问日志阀门，通过此阀门可以记录所有客户端的访问日志，包括远程主机IP，远程主机名，请求方法，请求协议，会话ID，请求时间，处理时长，数据包大小等。它提供任意参数化的配置，可以通过任意组合来定制访问日志的格式。

- JDBCAccessLogValve，同样是记录访问日志的阀门，但是它有助于将访问日志通过JDBC持久化到数据库中。
- ErrorReportValve，这是一个讲错误以HTML格式输出的阀门
- PersistentValve，这是对每一个请求的会话实现持久化的阀门
- RemoteAddrValve，访问控制阀门。可以通过配置决定哪些IP可以访问WEB应用
- RemoteHostValve，访问控制阀门，通过配置觉得哪些主机名可以访问WEB应用
- RemoteIpValve，针对代理或者负载均衡处理的一个阀门，一般经过代理或者负载均衡转发的请求都将自己的IP添加到请求头”X-Forwarded-For”中，此时，通过阀门可以获取访问者真实的IP。
- SemaphoreValve，这个是一个控制容器并发访问的阀门，可以作用在不同容器上。

### Tomcat类加载

#### Tomcat的挑战

- Tomcat上可以部署多个项目
- Tomcat运行时需要加载的类
- Tomcat中的多个项目可能存在相同的类

#### 类加载和类加载器

##### 类加载

主要是将.class文件中的二进制字节读入到JVM中

我们可以看到因为这个定义，所以并没有规定一定是要磁盘加载文件，可以通过网络，内存什么的加载。只要是二进制流字节数据，JVM就认。

类加载过程：

1. 通过类的全限定名获取该类的二进制字节流；
2. 将字节流所代表的静态结构转化为方法区的运行时数据结构
3. 在内存中生成一个该类的java.lang.Class对象，作为方法区这个类的各种数据的访问入口

##### 类加载器

**定义：**JVM设计者把“类加载”这个动作放到java虚拟机外部去实现，以便让应用程序决定如何获取所需要的类。实现这个动作的代码模块成为“类加载器”

类与类加载器

对于任何一个类，都需要由加载它的类加载器和这个类来确定其在JVM中的唯一性。也就是说，两个类来源于同一个Class文件，并且被同一个类加载器加载，这两个类才相等。

注意：这里所谓的“相等”，一般使用instanceof关键字做判断。

#### 类加载器和双亲委派模型

##### 类加载器

启动类加载器：该加载器使用C++语言实现，属于虚拟机自身的一部分。

启动类加载器（Bootstrap ClassLoader）:负责加载JAVA_HOME\lib目录中并且能被虚拟机识别的类库加载到JVM内存中，如果名称不符合的类库即使在lib目录中也不会被加载。该类加载器无法被java程序直接引用

拓展类加载器与应用程序类加载器：另一部分就是所有其它的类加载器，这些类加载器是由Java语言实现，独立于JVM外部，并且全部继承抽象类java.lang.ClassLoader。

扩展类加载器(Extension ClassLoader):该加载器主要负责加载JAVA_HOME\lib\ext目录中的类库，开发者可以使用扩展加载器。

应用程序类加载器（Application ClassLoader）:该列加载器也称为系统加载器，它负责加载用户类路径(Classpath)上所指定的类库，开发者可以直接使用该类加载器，如果应用程序中没有自定义过自己的类加载器，一般情况下这个就是程序中默认的类加载器

##### 双亲委派模型

**定义：**双亲委派模型的工作过程为：如果一个类加载器收到了类请求，它首先不会自己去尝试加载这个类，而是把这个请求委派给父加载器去完成，每一层都是如此，因此所有类加载的请求都会传到启动类加载器，只有当父加载器无法完成该请求时，子加载器才去自己加载。

**实现方式：**该模型要求除了顶层的启动类加载器外，其余的类加载器都应当有自己的父类加载器。子类加载器不是以继承的关系来实现，而是通过组合关系来复用父加载器的代码。

**意义：**好处双亲委派模型的好处就是java类随着它的类加载器一起具备了一种带有优先级的层次关系。例如：Object,无论那个类加载器去加载该类，最终都是由启动类加载器进行加载的，因此Object类在程序的各种类加载环境中都是一个类。如果不用改模型，那么java.lang.Object类存放在classpath中，那么系统中就会出现多个Object类，程序变得很混乱。

#### JVM中的类加载器源码分析

#### Tomcat中的类加载解决方案

##### Tomcat类加载的考虑

###### 隔离型

Web应用类库相互隔离，避免依赖库或者应用包相互影响，比如有两个Web应用，一个采用了Spring 4,一个采用了Spring 5,而如果如果采用同一个类加载器，那么Web应用将会由于jar包覆盖而无法启动成功。

###### 灵活性

因为隔离性，所以Web应用之间的类加载器相互独立，如果一个Web应用重新部署时，该应用的类加载器重新加载，同时不会影响其他web应用。

比如：不需要重启Tomcat的创建xml文件的类加载，

还有context元素中的reloadable字段：如果设置为true的话，Tomcat将检测该项目是否变更，当检测到变更时，自动重新加载Web应用。

###### 性能

由于每一个Web应用都有一个类加载器，所以在Web应用在加载类时，不会搜索其他Web应用包含的Jar包，性能自然高于只有一个类加载的情况。

###### Tomcat中的类加载器

Tomcat提供3个基础类加载器（common、catalina、shared）和Web应用类加载器。

#### Tomcat中的类加载器源码分析

##### 三个基础类加载器

3个基础类加载器的加载路径在catalina.properties配置，默认情况下，3个基础类加载器的实例都是一个。

createClassLoader调用ClassLoaderFactory属于一种工厂模式，并且都是使用URLClassLoader

默认情况三个是一个实例，但是可以通过修改配置创建3个不同的类加载机制，使它们各司其职。

举个例子：如果我们不想实现自己的会话存储方案，并且这个方案依赖了一些第三方包，我们不希望这些包对Web应用可见，因此我们可以配置server.loader,创建独立的Catalina类加载器。

共享性：

Tomcat通过Common类加载器实现了Jar包在应用服务器与Web应用之间的共享，

通过Shared类加载器实现了Jar包在Web应用之间的共享

通过Catalina类加载器加载服务器依赖的类。

##### 使用类加载工厂的好处：

1. ClassLoadFactory有一个内部Repository，它就是表示资源的类，资源的类型用一个RepositoryType的枚举表示。
2. 同时我们看到，如果在检查jar包的时候，如果有检查的URL地址的如果检查有异常就忽略掉，可以确保部分类加载正确。

尽早设置线程上下文类加载器

每一个运行线程中有一个成员ContextClassLoader,用于在运行时动态载入其他类，当程序中没有显示声明由哪个类加载器去加载哪个类，将默认由当前线程类加载器加载，所以一般系统默认的ContextClassLoad是系统类加载器。

一般在实际的系统上，使用线程上下文类加载器，可以设置不同的加载方式，这个也是Java灵活的类加载方式的体现，也可以很轻松的打破双亲委派模式，同时也会避免类加载的异常。

Webapp类加载器

每个web应用会对一个Context节点，在JVM中就会对应一个org.apache.catalina.core.StandardContext对象，而每一个StandardContext对象内部都一个加载器实例loader实例变量。这个loader实际上是WebappLoader对象。而每一个 WebappLoader 对象内部关联了一个 classLoader 变量（就这这个类的定义中，可以看到该变量的类型是org.apache.catalina.loader.WebappClassLoader）。

所以，这里一个web应用会对应一个StandardContext 一个WebappLoader 一个WebappClassLoader 。

一个web应用对应着一个StandardContext实例，每个web应用都拥有独立web应用类加载器(WebappClassLoader)，这个类加载器在StandardContext.startInternal()中被构造了出来。	

### Tomcat性能优化

#### 去除value访问tomcat记录

#### 关闭自动重载，热部署方式

#### 去掉不必要的servlet

#### JspServlet优化

### Tomcat常见面试题

#### 1.Tomcat有哪几种部署方式？

##### 隐式部署

直接丢文件夹、war、jar到webapps目录，tomcat会根据文件夹名称自动生成虚拟路径，简单，但是需要重启Tomcat服务器，包括要修改端口和访问路径的也需要重启。

##### 显式部署

###### 添加context元素

server.xml中的Host加入一个Context（指定路径和文件地址），例如：

<Host name="localhost">

<Context path="/comet" docBase="D:\work_tomcat\ref-comet.war" />

即/comet 这个虚拟路径映射到了D:\work_tomcat\ref-comet目录下(war会解压成文件)，修改完servler.xml需要重启tomcat 服务器。

###### 创建xml文件

在conf/Catalina/localhost中创建xml文件，访问路径为文件名，例如：

在localhost目录下新建demo.xml，内容为：

<Context docBase="D:\work_tomcat\ref-comet" />

不需要写path，虚拟目录就是文件名demo，path默认为/demo，添加demo.xml不需要重启 tomcat服务器。

**三种方式比较：**

隐式部署：可以很快部署,需要人手动移动Web应用到webapps下，在实际操作中不是很人性化

添加context元素 : 配置速度快,需要配置两个路径，如果path为空字符串，则为缺省配置,每次修改server.xml文件后都要重新启动Tomcat服务器，重新部署.

创建xml文件:服务器后台会自动部署，修改一次后台部署一次，不用重复启动Tomcat服务器,该方式显得更为智能化。

#### 2. Tomcat核心组件是哪些？

Tomcat的核心组件是链接器(connector)和容器(Container),

链接器(connector)封装了底层的网络请求(Socket请求及相应处理),提供了统一的接口.

容器(Container)则专注处理Servlet，Tomcat本质上就是Servlet容器。

#### 3. 什么是Tomcat的Valve

在一个大的组件中直接处理这些繁杂的逻辑处理,使用管道（pipeline）可以把把多个对象连接起来，而Valve(阀门)整体看起来就像若干个阀门嵌套在管道中，而处理逻辑放在阀门上。 

管道(Pipeline)就像一个工厂中的生产线，负责调配工人（valve）的位置，valve则是生产线上负责不同操作的工人。

#### 4. Tomcat 有哪几种Connector 运行模式(优化)？

1. bio(blocking I/O) 同步阻塞I/O （tomcat8.5版本已经移除）
2. nio(non-blocking I/O) 同步非阻塞I/O
3. Nio2/AIO  异步非阻塞I/0
4. apr(Apache Portable Runtime/Apache可移植运行库)

对于I/0选择，要根据业务场景来定，一般高并发场景下，APR和NIO2的性能要优于NIO和BIO，（linux操作系统支持的NIO2由于是一个假的，并没有真正实现AIO，所以一般linux上推荐使用NIO，如果是APR的话，需要安装APR库，而Windows上默认安装了），所以在8.5的版本中默认是NIO。

#### 5. tomcat容器是如何创建servlet类实例？用到了什么原理？

当容器启动时，会读取在webapps目录下所有的web应用中的web.xml文件，然后对xml文件进行解析，并读取servlet注册信息。然后，将每个应用中注册的servlet类都进行加载，并通过反射的方式实例化（也有可能是在第一次请求时实例化）。

#### 6. tomcat 如何优化？

#### 7. tomcat 中JVM如何调优

一般我们优化启动时的堆内存设置,Windows下,在文件{tomcat_home}/bin/catalina.bat，Unix下，在文件$CATALINA_HOME/bin/catalina.sh的前面，增加如下设置：

JAVA_OPTS=”‘$JAVA_OPTS” -Xms[初始化内存大小] -Xmx[可以使用的最大内存]

一般说来，你应该使用物理内存的 80% 作为堆大小。建议设置为70％；建议设置[[初始化内存大小]等于[可以使用的最大内存]，这样可以减少平凡分配堆而降低性能。

#### 8. 说出Tomcat中用到的任意一种设计模式？

#### 9. Tomcat中类加载的顺序

当应用需要到某个类时，则会按照下面的顺序进行类加载：

1 使用bootstrap引导类加载器加载

2 使用system系统类加载器加载

3 使用应用类加载器在WEB-INF/classes中加载

4 使用应用类加载器在WEB-INF/lib中加载

5 使用common类加载器在CATALINA_HOME/lib中加载

#### 10.      什么是双亲委派模型？

**定义：**双亲委派模型的工作过程为：如果一个类加载器收到了类请求，它首先不会自己去尝试加载这个类，而是把这个请求委派给父加载器去完成，每一层都是如此，因此所有类加载的请求都会传到启动类加载器，只有当父加载器无法完成该请求时，子加载器才去自己加载。

**实现方式：**该模型要求除了顶层的启动类加载器外，其余的类加载器都应当有自己的父类加载器。子类加载器不是以继承的关系来实现，而是通过组合关系来复用父加载器的代码。

**意义：**好处双亲委派模型的好处就是java类随着它的类加载器一起具备了一种带有优先级的层次关系。例如：Object,无论那个类加载器去加载该类，最终都是由启动类加载器进行加载的，因此Object类在程序的各种类加载环境中都是一个类。如果不用改模型，那么java.lang.Object类存放在classpath中，那么系统中就会出现多个Object类，程序变得很混乱。

#### 11.      既然 Tomcat 不遵循双亲委派机制，那么如果我自己定义一个恶意的HashMap，会不会有风险呢？

不会有风险，如果有，Tomcat都运行这么多年了，那群Tomcat大神能不改进吗？ tomcat不遵循双亲委派机制，只是自定义的webAppclassLoader不遵循，但上层的类加载器还是遵守双亲委派的，

### 手写Tomcat