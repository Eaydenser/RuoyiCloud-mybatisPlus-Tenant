# 平台简介
## 代码并不完善，但是可以看看具体思路，两个月前搞得，现在没有时间来运营    

## 1、重写了MybatisPlus的sql生成逻辑，支持sql拦截后进行多表查询
- 因为若依的数据过滤是通过拼接用户表和部门表进行的数据过滤，而mybatisPlus是单表查询，在进行sql拦截后会出现多表列名相同结果集不明确的问题，通过修改mybatisPlus的sql生成来达到添加若依数据过滤的目的
- 若依的数据过滤是通过参数的方式在进行xml解析时插入到sql中参与条件过滤，但用户表部门表的是自己手写join进行的过滤前置条件，这里在原来的数据过滤注解中添加的自动join用户表和部门表的逻辑，并可以通过注解内属性的方法插入join table on 后面的操作sq
- 最终让myabtisPlus在执行代码的时候会动态拼接用户表和部门表，并在查询条件的最后插入若依自带的数据过滤条件。

## 2、使用了Mybatis-plus3最新引入了多租户的配置实现方式，会在mybatisPlus的sql中动态拼接tanent_id=的操作
- 首先这是一个基于泛域名的多租户实现方式，通过Nginx拆分url讲泛域名部分插入请求头，再通过sql拦截器获取
- 如果获取的是数字，说明是前端插入的请求头tenant_id，如果是字符串判断为是租户表内的自定义域名，根据两种场景动态区分租户
- 在上面的数据过滤动态拼接后属于MyabtisPlus插入多租户过滤后的过程，所以在数据过滤层面注解实现层面也添加了自动过滤租户的代码

### 源码

- [Github](https://github.com/Eaydenser/RuoyiCloud-mybatisPlus-Tenant)
- [Gitee](https://gitee.com/eaydesner/ruo-yi-cloud-pro-max-plus)

#### 友情链接 [若依/RuoYi-Cloud](https://gitee.com/y_project/RuoYi-Cloud) Element UI版本。
## RuoYi Cloud交流群

QQ群：  [![加入QQ群](https://img.shields.io/badge/755109875-blue.svg)](https://jq.qq.com/?_wv=1027&k=5JGXHPD)  点击按钮入群。

### 鸣谢
- [若依](https://gitee.com/y_project/RuoYi) 开源框架，以及演示服务器提供
