# HookPlugin
插件化

# 使用
#### 1.宿主和插件需要依赖jplugin <br/>
#### 2.插件中的Activity需要继承jplugin中的BasePluginActivity <br/>
#### 3.宿主中初始化和加载插件 <br/>
##### &emsp;&emsp;1).初始化 <br/>
&emsp;&emsp;  PluginManager.getInstance().init(this); <br/>
&emsp;&emsp;  PluginManager.getInstance().hookActivity(ProxyActivity.class); <br/>
##### &emsp;&emsp;2).加载插件 <br/>
&emsp;&emsp;  PluginManager.getInstance().loadApk(extractFile.getPath()); <br/>
##### &emsp;&emsp;3).启动插件 <br/>
&emsp;&emsp;  PluginManager.getInstance().startLauncherActivity("com.cj.oneplugin"); <br/>
