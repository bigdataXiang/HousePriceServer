#空间插值步骤
+ step_1:先生成整个北京区域内的每个网格的时序数据，存放刚到jsonArray_map中,
  使得全局变量jsonArray_map有值。此时所有的数据都从“GridData_Resold_50”数据
  库中获得。
  
+ step_2:返回有缺失值的网格的编码，并且初始化数据集dataset。
  返回有缺失的网格编码：如果不足八个月的数据，就要在后面进行插值。
  "所有不足八个月的数据lack_value_grids的网格数目有"+lack_value_grids.size()
  
+ step_3:计算有缺失数据的网格与全部网格的相关系数 r ,并且返回相关性最强的20个
  如果待插值的网格本身数据稀疏，则不参与插值，而是存在全局变量sparse_data中，
  因为这样的插值效果不太好。findRelatedCode方法中还实现将与所有网格的相关系数
  为0的网格code存放在pearson_is_0中。
  
+ step_4:计算所有有缺失数据的网格的插值结果，并且将最终的结果存一份存到
  interpolation_result中。
  
+ step_5:统计插值情况，检查是否有遗漏的点。

+ step_6:计算interpolation_result中每个网格插值前后的mse的值，并且将mse的
  值较大的挑选出来,存在failed_interpolation_codes中。
  
+ step_7:比较mse的值较大的code的真实值和插值，并且将其打印出来。

+ step_8:将插值结果符合(即mse小于0.25、或者mae小于1)的网格进行插值操作

+ step_9:将插值后的结果转换成网格的形式存储于MongoDB
  (GridData_Resold_50_Interpolation表)中。
  
  
#插值数据检查统计
+ 总共有数据的网格有：jsonArray_map_size:7446
  
+ 所有不足八个月的网格数目有:lack_value_grids.size():5183

+ 待插值的网格本身数据稀疏，则不参与插值，而是存在全局变量sparse_data中，
  数目有：sparse_data.size():1060
  
+ 插值成功的网格有：spatial_size:3624
  其中：
  mae>1：248
  mae<1：3376

+ 相关系数为0的网格有：pearson_is_0_size:499

+ total=spatial_size+sparse_data_size+pearson_is_0_size
       =3624+499+1060=5183=所有不足八个月的网格数目
    
+ 数据满格的网格有full_value_grids.size()：2263

+ 总共有数据的网格=数据满格的网格+所有不足八个月的网格数目
  jsonArray_map_size=full_value_grids.size()+lack_value_grids.size()
  7446=2263+5183
  
# 邻近插值
+ 网格本身数据稀疏sparse_data.size():1060
+ mae>1，failed_interpolation_codes.size()：248
+ pearson_is_0_size:499
以上这几类数据均采用邻近插值法
  
