> 鉴于我们后端项目配置特别麻烦，每次都会遇到一大堆问题，所以我想要不就把es+mongodb+redis全扔上去云吧，只要本地的jdk1.8+nodejs14.x版本ok那就能跑了

云服务器的IP地址：`121.36.201.185`

## 配置ES



> 写在前面，我发现es7.6.x还不支持将es注册成开机自动启动的服务（不支持使用root来启动），所以还需要开机后手动启动es

#### ubuntu下启动es（相信你一定有了一些linux基础吧，那我写简单一点）

1. 开机（去华为云上选中`sqrrow_new`这台服务器启动）

2. 使用dqx用户进行登录（一定是这个，不能使用root，es不支持使用root用户启动），密码是`dqxlz0821`

3. 进入es的bin：

   ```js
   cd /home/dqx/elasticsearch-7.6.2/bin
   ```

4. 启动es

   ```
   ./elasticsearch
   ```

   如果希望他挂起运行可以使用

   ```
   ./elasticsearch -d
   ```

   > ps:用完记得关机



1. 测试云上的ES是否开启

   浏览器输入：

   ```
   121.36.201.185:9200
   ```

   出现这个Json就是ok

   ```
   JSON
       namenode-1
       cluster_nameelasticsearch
       cluster_uuidSrEs4yUzQqOK6WVbp5Lshg
       version
       number7.6.2
       build_flavordefault
       build_typetar
       build_hashef48eb35cf30adf4db14086e8aabd07ef6fb113f
       build_date2020-03-26T06:34:37.794943Z
       build_snapshotfalse
       lucene_version8.4.0
       minimum_wire_compatibility_version6.8.0
       minimum_index_compatibility_version6.0.0-beta1
       taglineYou Know, for Search
   ```

   

2. 更改本地的head配置

   修改`_site`目录下的`app.js`

   ```js
   this.base_uri = this.config.base_uri || this.prefs.get("app-base_uri") || "http://localhost:9200";
   #将原来的localhost改为服务器ip
   ```

   

3. 配置本地的kibana

   修改/config/kibana.yml

   ```js
   elasticsearch.hosts: ["http://服务器的地址:9200"]
   ```

4. 都能打开，那么ok



mongodb与redis的配置就和localhost的一样，只需要将地址改成我们的服务器的地址就好，我就不写了（这两个我改成了开机开启服务，所以不用管他）

