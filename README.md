这是系统的介绍部分，本系统是一个通过不完全文本聚类来进行实时热点的聚集（类似于微博热搜）。
核心思想是用K-MEANS聚类，核心创新点包括了用Word2Vec表示不基于全局的特征向量，以及对噪音数据和噪音簇的决策处理模块。文中图片的水印是我的博客地址。
欢迎各位同学的意见和指导。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101181523303.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQwODQzNjM5,size_16,color_FFFFFF,t_70)
***


## :sunglasses: 系统介绍图
### 系统流程图
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101162258640.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQwODQzNjM5,size_16,color_FFFFFF,t_70)
### 系统架构图
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101162047764.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQwODQzNjM5,size_16,color_FFFFFF,t_70)

## :alien: 核心算法图
#### 噪音数据和噪音数据簇的的决策处理模块：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101163517814.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQwODQzNjM5,size_16,color_FFFFFF,t_70)

#### word2Vec生成词向量和文档向量的模块：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101163944881.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQwODQzNjM5,size_16,color_FFFFFF,t_70)
#### 
***
上面这两个模块是本系统聚簇模块的核心模块，再得到文章向量之后，用传统的K-MEANS的聚类API来完成聚类即可以得到最终的聚类结果。
***

## :wrench: 系统使用说明
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101180725635.png)

预处理和工具类模块，调用Word2Vec生成词向量和通过TF-IDF权重生成文档向量
***
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019110118061541.png)
word2Vec的核心模块，两层神经网络的训练
***
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101180910902.png)
聚类核心模块，包括文本表示和对噪音簇的决策处理

***
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019110118100720.png)
图形化界面和词云生成模块
***

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101181131838.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101181113749.png)
系统执行的入口
***


### License

本项目仓库的内容，除了在对K-means和Word2Vec的核心实现上是调用了开源组件外，其他都是我的原创。在您引用本仓库内容或者对内容进行修改时，请署名并以相同方式共享，谢谢。
***






