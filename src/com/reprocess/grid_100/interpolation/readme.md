1. 时空数据
2. 缺失值检测
（1）研究区域范围：
northeast
lat：39.96366837052331
lng：116.40074729919434
southwest
lat：39.934223203947056
lng：116.31834983825684

zoom=15，N=5
（2）确定要进行插值的网格
3. 时空分区
（1）选取用于插值的网格
  首先在空间维度选取缺失数据周围空间的ｎ个相关性最大的采样数据，本文选择相关系数
Ｒ 描述站点之间的相关性，具体计算为：对于一个缺失数据，计算其周围空间点的数据序列和缺失
数据点的数据序列的相关系数 Ｒ（ｙｉ，ｙ０）。其中，ｙｉ表示缺失数据周围空间点的数据序列，ｙ
０表示缺失数据点的数据序列。当某一序列中存在其他缺失数据时则去除另一序列中的相应数据继续进
行计算。
   同样对于时间维度，在同一子区域内选取缺失数据前后时间的ｍ个相关性最大的采样数据，其中的相关性
计算为：对于一个缺失数据，计算缺失数据周围时间切片的空间点序列和缺失数据所在时间切片的空间点序列
的相关系数 Ｒ（ｔｊ ，ｔ０）。
   其中，ｔｊ表示缺失数据周围时间切片的空间点序列 ，ｔ０表示缺失数据所在时间切片的空间点序列。
   最后，分别在空间维度选择ｎ个、时间维度选择ｍ 个相关系数最大的周围数据进行缺失点的插值计算。
   
   注意，如果两空间点在时间序列上的数值不多（比如只有三个月的数据），即使相关性很高也不要用来做插值点。
   
   （2）计算协方差的时候，是利用公式cov(xy)=E(xy)-E(x)*E(y)=(1/n)*(x1*y1+...+xn*yn)-(1/(n*n))(x1+...+xn)(y1+...+yn)
   这里取的是n而不是n-1，如果效果不好，则再用n-1来计算
   注意：如果是对总体的计算一般使用n，如果是对样本的计算一般使用n-1，这样是对总体的无偏估计
4. 空间维度估值
5. 时间维度估值
6. 时空数据融合
7. 缺失数据估计值
