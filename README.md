# FunctionParamCreator

这是一个根据JSONObject格式的字符串,自动生成方法形参.本插件适用 Intellij IDEA 和 Android Studio 等工具

### 安装方法：

1、下载源码（git clone https://github.com/AndroidCoderPeng/FunctionParamCreator.git）,

2、然后编译插件，最后在build文件夹下找到distributions，里面的zip就是插件（不要解压！不要解压！不要解压！）。
如果不想自己编译的，下载我编译好了的zip也可以（[FunctionParamCreator-1.0.0.zip](plugins%2FFunctionParamCreator-1.0.0.zip)）

3、安装插件（Settings --> Plugins --> Installed --> Install Plugin form Disk…）

4、如果在Tools里面看不到如下图选项，重启IDEA就行了

![installed.png](images%2Finstalled.png)

5、如果安装成功，快捷键无法调出插件，那就是插件快捷键和IDEA现有设置过的快捷键出现了冲突，修改设置快捷键就行了

### 使用方法（以Android Studio为例）

1、在需要格式化大量参数的地方通过快捷键调出插件界面，如下图：

![initParam.png](images%2FinitParam.png)

2、把Http入参Json串填入左侧区域，勾选相应的配置，如下图：

![initJson.png](images%2FinitJson.png)

3、最后点击右上角"Generate"按钮即可，如下图：

![generateParam.png](images%2FgenerateParam.png)
