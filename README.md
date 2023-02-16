
<div style="display: flex;flex-direction: row;justify-content: center" width="100%">
      <img src="./img/logo.png"></img>
</div>

## AgentWeb 介绍

AgentWebX5 是一个基于 [AgentWeb](https://github.com/Justson/AgentWeb)  内部集成腾讯X5 ，极度容易使用以及功能强大的库，提供了 Android WebView 一系列的问题解决方案 ，并且轻量和极度灵活，详细使用请参照上面的 Sample 。

自行引入aar包	

基于support版本集成
implementation 'com.github.Justson.AgentWeb:agentweb-core:v5.0.0-alpha' // (必选)
implementation 'com.github.Justson.AgentWeb:agentweb-filechooser:v5.0.0-alpha' // (可选)

## 初始化
可查看demo
必须初始化x5
AgentWebCompat.initX5(this);





## 注意事项
* 支付宝使用需要引入支付宝SDK ，并在项目中依赖 ， 微信支付不需要做任何操作。
* AgentWeb 内部使用了 `AlertDialog` 需要依赖 `AppCompat` 主题 。 
* `setAgentWebParent` 不支持  `ConstraintLayout` 。
* `mAgentWeb.getWebLifeCycle().onPause();`会暂停应用内所有`WebView` 。
* `minSdkVersion` 低于等于16以下自定义`WebView`请注意与 `JS` 之间通信安全。



## 文档帮助
* [Wiki](https://github.com/Justson/AgentWeb/wiki)(不全)
* `Sample`(推荐，详细) 
* [更新日志](./releasenote.md)


