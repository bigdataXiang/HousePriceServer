北京市时空房地产价格可视化
=============
#一、项目概况
#二、项目设计
##（一）格网系统设计
服务端数据的网格编码（code）基于[高斯平面直角坐标系](http://blog.csdn.net/mniwc/article/details/7351714)

其中X轴为南北方向，指向北方；Y轴为东西方向，指向东方。

坐标轴的原点是（115.417284,39.438283）。坐标轴的东北角是（117.500126,41.059244）。

每1000米的经度差：0.011785999999997188；纬度差：0.009003999999997347

基于高斯坐标系下的相对矩形范围：
X_MAX = 2.0542041271351546E7;
X_MIN = 2.036373920422157E7;
Y_MAX = 4547353.496401368;
Y_MIN = 4368434.982578722;

网格分辨率（index）：1000m*1000m

网格行数：rows = (int) Math.ceil((X_MAX - X_MIN) / index);

网格列数：cols = (int) Math.ceil((Y_MAX - Y_MIN) / index);

网格编码规则：从第一行第一列开始编码，第一行编码完再接着编码第二行，呈Z字形的编码方式。

某地理坐标点（经纬度坐标）的网格code计算方法：
1.将经纬度转换成高斯平面直角坐标系的坐标（X，Y）
2.通过平面坐标计算出该点所在的网格系统的行列号row（行），col(列)
row = (int) Math.ceil((X - X_MIN) / 1000); 其中：ceil()将小数部分一律向整数部分进位。 
col = (int) Math.ceil((Y - Y_MIN) / 1000);
3.通过网格系统的行列号计算出该网格的值code
code = (col + cols * (row - 1)); 
        
#三、项目功能
#四、总结



























//本机mongoDB
先用下面的命令打开一个mongo窗口
//mongod.exe --dbpath D:/ruanjian/MongoDB/bin

再在bin文件下cmd打开另外一个窗口，执行“mongo”命令即可查看操作数据库

//github 提交步骤
git add -A：提交所有
git commit -m "":提交说明
git pull:
git push：将代码同步到远程

WebStorm半年激活码 
一、下载安装篇 
前往：官网 https://www.jetbrains.com/ 下载您要的软件 
安装：请按提示完成安装。 
二、激活篇 
1、打开安装好的JetBrains软件 
2、在菜单栏－help－register打开的License Activation窗口中选择“activation code”单选按钮。 
3、用记事本把激活码文件打开，拷贝激活码到activation code 下方框中。 
注意 ：遗失不补发 
激活码https://yunpan.cn/cByyndrZxwuE4 （提取码：0277） 
4、点击OK
