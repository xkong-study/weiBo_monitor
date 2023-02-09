# 微博舆论监控系统
 
## 项目介绍
这个系统目前采用IDEA开发软件，使用Springboot开发微博舆论监控系统，使其对微博进行一定的有效管理。网站主要设计和实现对微博热点新闻进行获取和展示，采用短文本情感分类技术对微博评论进行一定的训练使其能够通过算法进行自动化的分类，并以图表的形式展示评论的情感分类。对于愤怒值过高的新闻将进行告警服务，并且可以展示告警日志。除此之外，还采用随机森林回归模型对用户画像的特征重要性进行分析，并进行Chart图展示。


Web界面：
首页————热搜评论文本情感分类算法：
![image](https://i.postimg.cc/GtzLJxyt/Screen-Shot-2023-02-08-at-16-16-14.png)

词云展示————评论热词展示：
![image](https://i.postimg.cc/GtzLJxyt/Screen-Shot-2023-02-08-at-16-16-14.png)

用户画像————展示微博用户基本数据：
![image](https://i.postimg.cc/hjDTSpcP/Screen-Shot-2023-02-08-at-16-28-12.png)

。。。。。。。。

更多功能已部署在线上：3.252.123.47:8080

## 项目结构介绍

### util包
`HttpUtil`类负责发送http请求。`RedisUtil`类负责使用`Jedis`连接Redis服务器并返回Jedis实例读写数据到Redis。`SegmentUtil`类重写了分词方法。

### entity包
`Hot`类、`Weibo`类和`Comment`类分别表示热搜、微博和评论的实体类，它们存储了基本的元数据和进行情感分析后的加工数据。

### crawler包
`CrawlTask`类继承了`TimerTask`能够周期性的运行，对微博热搜榜进爬取，进行情感分析，然后将数据存储到Redis。具体的爬取任务由`WeiboParser`类负责，由于微博页面基本都是动态的，请求微博相应的接口返回`Json`数据，然后使用`jackson`框架解析微博元数据。

### classifier包
`Train`类读取语料文件，存储到内存里，调用`HanLP`对文本进行分词处理，并计算每个词的`卡方值`，得到显著的词作为特征。最后生成`Model`类的模型。`Model`使用朴素贝叶斯算法进行文本分类。

`MyClassifier`类组合了`Train`和`Model`类，优先从指定的路径加载序列化的模型文件，如果未获取到模型就使用`Train`重新根据语料计算模型，然后将计算得到的模型序列化到指定路径。`MyClassifier`类的`getScore`方法调用模型对文本进行情感分析，所以实际使用中只会用到`MyClassifier`。

> 其实`HanLP`已经实现了一个分本算法，参考`HanLPClassifer`类。

### web包
Web服务由`Spring Boot`支持，后端从Redis读取数据（其实数据可以直接保存在内存的数据结构里，因为前期设计时考虑对多次爬取的数据进行横向分析才存使用Redis管理数据，为了方便后续扩展还是继续使用Redis），将数据渲染到`Thyemeleaf`模版中然后返回html。

模版存储在`src/main/resources/templates`目录下，前端使用`Bootstrap`编写， 图表使用`chart.js`渲染。

## 运行环境
* Java 8以上，本项目使用了一些Java 8的特性；
* Maven，下载项目依赖的包；
* Redis，某些数据存储在Redis中；

## 运行准备

### 1.设置Redis服务器地址
redis线上服务器ip：3.252.123.47:6379

### 2.设置HTTP请求参数
由于微博的限制，未登录的账户只能获取到第一页微博评论。为了爬取更多评论你需要在浏览器中登录`m.weibo.cn`，浏览微博评论列表，然后打开浏览器的开发者工具查看你发送的`hotflow?id=...`请求（这是请求评论request）里的Cookie，将其复制到`HttpUtil`类里的`header("cookie", "")`的第二个参数里。

你还可以将`user-agent`设置成自己浏览器的user-agent标识。

### 3.下载分词所需的数据
本项目使用的分词工具为[HanLP](https://github.com/hankcs/HanLP/tree/1.x)，为了分词更准确它需要一些额外的数据。


### 4.设置模型及语料的路径
我提供了一些基本的语料作为参考，在`src/main/resources/train`目录下。

复制模型`weibo-model`的绝对路径到`MyClassifier`类的`MODEL_PATH`（**注意一定要用绝对路径，后续路径也是，因为项目打包成jar后运行的classpath路径不确定，相对路径可能会失效**），运行时如果模型文件存在就不用重新训练模型。

复制停用词表`cn_stopwords.txt`、否定词表`cn_nonwords.txt`和自定义词库`customwords.txt`的路径到`SegmentUtil`类的相应变量。

> 如果想要使用其他的语料训练模型，可以复制积极情绪语料`pos.txt`和消极情绪语料`neg.txt`的绝对路径到`Train`类相应的路径。


