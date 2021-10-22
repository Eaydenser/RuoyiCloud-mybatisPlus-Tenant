# 平台简介
## 代码并不完善，但是可以看看具体思路，两个月前搞得，现在没有时间来运营    

## 1、若以的权限过滤设计是通过拼接用户表和部门表进行数据隔离，但是mybatis-plus只支持单表查询，不符合使用逻辑
####   解决思路：1、创建一个注解，决定这个方法内的sql是否插入用户表，插入后on的逻辑，是否插入部门表，插入后on的逻辑

## 2、重写mybatis-plus的sql构造器，带入tbale表名，让我在进行sql拦截时不会出现sql分析不了表列的错误

## 3、使用了Mybatis-plus3最新的tenant_id多租户sql拼接，实测是会对上面部门表和用户表也同时进行租户数据隔离
####    这是一个基于泛域名的多租户实现方式，通过Nginx拆分url讲泛域名部分插入请求头，再通过sql拦截器获取
####    如果获取的是数字，说明是前端插入的请求头tenant_id，如果是字符串判断为是租户表内的自定义域名，根据两种场景动态区分租户

### 源码

#### 友情链接 [若依/RuoYi-Cloud](https://gitee.com/y_project/RuoYi-Cloud) Element UI版本。
## RuoYi Cloud交流群

QQ群：  [![加入QQ群](https://img.shields.io/badge/755109875-blue.svg)](https://jq.qq.com/?_wv=1027&k=5JGXHPD)  点击按钮入群。

### 鸣谢
- [若依](https://gitee.com/y_project/RuoYi) 开源框架，以及演示服务器提供