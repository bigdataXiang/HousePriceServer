#数据处理记录
##8月2日
+ 数据噪音问题
> （1）在将安居客、房天下和我爱我家的数据导入mongodb的时候，发现unitprice的值
   还是存在很多不对的地方，有的是因为各个属性值的错位造成的，有的是本身就无数值只
>  有单位。

##8月3日
+ 数据查询问题
> (1)按月份调用mongodb中的数据的时候，发现数据只调用了一部分，或者根本不满足调用条件，
  调试之后才发现是月份的输入问题。
  当月份为两位数时：
  condition.put("month",i);应该改成condition.put("month","0"+i); 
>  这个问题让我考虑到很有可能里面的月份数据也不规则，有的是month=7，有些则是month=07，
>  考虑到以后数据库查询的方便，还是将数据全部变成不带0的比较好，统一时间描述标准。 
>  也可以进行两次查询，第一次查询condition.put("month",i)，第二次再查询condition.put("month","0"+i)
>  这样就不会落掉数据啦。数据计算存入本地后，再作数据对比与合并处理  
> (2)还有在算unitprice的时候，需要验证除数是否为0，当除数为0时，会抛出异常

## 8月4日
+ 网格编码问题
> 将房天下的数据传输到前端，发现位置还是对应不上。整体的网格位置相对实际位置是向东
> 偏移了的。现在的问题是要找出是上呢么原因导致的偏移。可能是编码的问题，也可能是
> 坐标本身解析的问题。先从编码问题开始排查。
> (1)经过排查发现，定位不准确是根本性的问题。通过试验很多次地名，发现调用地理编码解析出来的
>  坐标和地名实际的坐标并不一致。所以这才会导致网格编码的的错误，造成实际位置的偏移
> (2)检查网格编码的以及网格的生成情况
> var gridvalue=price.data[gridcode]应该改成gridvalue=price.data[gridcode-1]
> 因为gridcode是从1开始计数，而data数组是从0开始计数，即data[0]对应gridcode为1的网格值。
> (3)下午问了老师，才知道原来是百度坐标并不是真正的wgs84坐标，而解译得到的数据是wgs84坐标，
> 如果需要定位到百度地图上，还需要进行wgs84转百度坐标系。高德支持wgs84，可以用leaflet的高德
> 坐标。
> (4)在设计网格的时候发现了一个大问题，就是网格编码的不连续！在画网格的时候，是这犯的是数学错误啊啊啊。
> 没有120、180等这些编码的啊啊啊~经过检查才发现，最后一个列（60列）没有算上。本来应该写成"<61"的，结果
> 写成了"<60"

## 8月5号
+ 网格编码
> 今天又检查出了很多的小问题。
> (1)首先是数据库导入程序ExportToMongo，这个程序初衷是想将网格的code计算出来
> 并且导入进去的，但是我发现，最后的数据库里面的数据并没有code属性，因为程序没有把json数据中的code放到
> document中，导致虽然计算了网格但是并没有存放到数据库中去。
>(2)在比较SetPoiCode类和LngLatCode类的时候发现，SetPoiCode类在计算行列号的时候把间距值lenth和width刚好
> 搞反了，导致计算出来的gridcode的值都是错的。
> 这些小错误告诫我一定要检查调试程序啊，要不然功夫都白花费了！

## 8月10号
>晚上一直在纠结Math.ceil(rowmin/N)为什么返回的是一个不小于rowmin/N的整数，调试了很久，才明白
>因为我rowmin/N没有强制转换成double型！这个错误已经犯过多次了呀！一定要注意，一定要改！刚刚检查
>发现了codeMapping100toN00（）函数中的Math.ceil()的数是做过强制转换double处理的。但是当时没把
>提醒写上来。导致又犯错误啦。
>除法运算"/":如果分子和分母中有一个数是小数，则结果是小数，如果两个都是整数，则结果为整数！这点切记！
>小问题会酿造成大麻烦。

## 8月11号
>昨晚在导入数据的时候发现，房天下的二手房还是有很多不对的数据。
> {"longitude":"116.66601097828216","latitude":"40.15485512530272","region":"北京市,null,顺义区,null","title":"平谷乐园西小区2室1厅","time":"2014/12/9售价：155万（15980元/�）参考首付：46万参考月供：户型：2室1厅1卫建筑面积：97�楼层：低层（共6层）朝向：南小区：乐园西小区（平谷平谷）业主接电时间：09:00/22:00400/890/7943转219803业主张先生评估该房房贷计算器申请按揭房源点评房源图片客户看房记录小区成交记录街景地图小区简介搜房金融服务首页上一页共0页varallcount=0;varpageindex=1;varpageCount=0;if(pageindex!='1'){jQuery(\"#PageControl1_hlk_first\").css(\"display\",\"\");jQuery(\"#PageControl1_hlk_pre\").css(\"display\",\"\");if(pageindex==pageCount){jQuery(\"#PageControl1_hlk_next\").css(\"display\",\"none\");jQuery(\"#PageControl1_hlk_last\").css(\"display\",\"none\");}}if(allcount>0){jQuery(\"#agentInfo\").html(\"房源点评(\"+allcount+\"条)\");jQuery(\"#fanye\").find('a').click(function(){switch(jQuery(this).text()){case\"首页\":pageindex=1;break;case\"上一页\":pageindex=(pageindex/1)<0?0:(pageindex/1);break;case\"下一页\":pageindex=(pageindex+1)>=pageCount?pageCount:pageindex+1;break;case\"末页\":pageindex=pageCount;break;default:pageindex=parseInt($(this).text());break}varloadurl=\"/EsfHouse/Detail/Eb_AgentPingJia.aspx?houseid=236910&pageindex=\"+pageindex;jQuery(\"#agentGJ/pos\").load(loadurl,function(){init();varchangeno=newChangePhone400();changeno.Change(houseID,zhizunAgentid);});});}else{jQuery(\"#pingjiaagentinfo\").css(\"display\",\"none\");jQuery(\"#agentbottom\").css(\"display\",\"none\");jQuery(\"#fanye_allcount\").css(\"display\",\"none\");}jQuery(function(){varcmtCon=jQuery('.cmtC');for(vari=0;i<cmtCon.length;i++){isHide(cmtCon.eq(i));}});functionisHide(txt){varlineH=txt.innerHeight();varbtn=txt.next().children('.showBtn');if(lineH>144){txt.addClass('ht144');btn.toggle(function(){txt.removeClass('ht144');jQuery(this).text('');},function(){txt.addClass('ht144');jQuery(this).text('');});}else{btn.hide();}}街景地图地址：谷丰路与平谷南街交叉口东南角交通状况：平谷：h52路、h61路、郊100路、郊88路、平11路、平21路、平22路、平23路、平25路、平25支线、平26路、平28路、平29路、平30路、平35路、平40路、平41路、平42路、平43路、平44路、平45路、平47路、平49路、平50路南线、平52路小渔阳站周边配套街景地图小区简介楼盘名称：乐园西小区(平谷平谷)查看楼盘详情&gt;&gt;物业类型：住宅绿化率：30.00%物业费：0.40元/平米・月物业公司：暂无资料开发商：暂无资料乐园西小区本月均价：12621元/平方米查看本楼盘详情价格走势&gt;&gt;环比上月：↑0.02%同比去年：↓2.56%本楼盘价格走势本商圈价格走势本区县价格走势本市价格走势jQuery(document).ready(function(){varclickid='dsesfxq_';varjson={\"Code\":\"100\",\"Message\":\"获取数据成功\",\"List\":};try{varhtml='<divclass=\"comTitle\"><spanclass=\"ttc\">搜房金融服务房屋","price":null,"house_type":"2室1厅1卫","area":"97","unit_price":0,"built_year":null,"direction":"南","floor":"低层（共6层）","structure":null,"fitment":null,"property":null,"cmmunity":null,"facility":null,"address":null,"url":null,"crawldate":"2016-03-24","date":{"year":"2014","month":"12","day":"9售价：155万（15980元"},"layout":{"rooms":"2","halls":"1","bathrooms":"2室1厅1"},"storeys":{},"traffic":"","community":null}
> 类似与这种，无法处理的，就应该直接摒弃掉。这也说明在遇到地理编码错误的数据的时候，不一定是地址的问题
> 有可能就是数据本身就有问题。对于这种问题数据应该早些制止，一面造成清理后的数据的噪音问题。

## 8月12号
>今天解决了学区数据的可视化和兴趣区域的轮廓描述等问题。虽然实现之后发现是很简单的问题。但是实现的过程中由于
>没有仔细阅读api文档和查找资料，出现了很多的问题。首先是画圆（circle）的时候没有设置好圆的大小属性。但是好在
>一次性实现了。最要是后面画轮廓的时候，对照api文档去画，却是各种出错，最后仿照别人的程序才写对了。对比自己的
>程序和别人的程序，我发现我是没有用好“poly_line.addLatLng(latlng1);”和“map.addLayer(poly_line);”我直接
>仿照api的模版“var polyline = L.polyline(latlngs, {color: 'red'}).addTo(map);”去套却发现怎么也不能实现
>多边形的可见。最后用的“poly_line.addLatLng(latlng1);”和“map.addLayer(poly_line);”方法才得以实现。
>晚上一直在琢磨如何用L.marker来做出好看的标记。起初用Leaflet.awesome的时候，各种不行，然后用最原始的方法来实现吧
>也是各种图片加载不上。最后实在没办法，想到了先把图片传到网上，再利用网络地址来获取地理标记的图片的。但是为什么
>不能加载本地的图片，至今未知。另外我还发现了之前我把zoommin的值调得太小了，其实可以调大一点显示整个北京的区域就好了
>总之leaflet的api文档里有很多只是值得仔细去学习。不懂的地方要搜别人做过的例子，不能自己一味地闷着头做。

##8月27日
+ 时间比较的问题
>GregorianCalendar和Calendar的区别
Calendar是父类
GregorianCalendar是子类
一般使用的时候可以使用
Calendar c=new GregorianCalendar();//直接创建
Calendar ca = new GregorianCalendar()//默认当前的时刻。
Calendar ca = new GregorianCanlendar(int year,int month,int dayOfMonth)//初始具有指定年月日的公历类对象。
Calendar ca = new GregorianCanlendar(int year,int month,int dayOfMonth,int hourOfDay,int minute)初始具有指定年月日的公历类对象。
Calendar ca = new GregorianCanlendar(int year,int month,int dayOfMonth,int hourOfDay,int minute,int second)//初始具有指定年月日的公历类对象。

使用ca.get(Calendar.YEAR)、ca.get(Calendar.MONTH)、ca.get(Calendar.DAY)方式获得年月日

## 8月28日
+ 加速度网格一直不显示的问题
>加速度程序写好之后一直前端发送请求时一直不能返回结果，试了好多办法还是不行。后面在调试的时候发现
是因为Calendar的问题。因为Calendar的月份是从0到11的。当实际月份为12月份的时候，Calendar显示的是
第二年的0月份。这样导致了每当选取月份为12月份的时候，自动变成0月份。这样的数据在数据库中是不存在的。
所以会导致数据调用不了，不能返回给前端数据，前端自然显示不了加速度网格

+ 拖动地图网格的颜色值会变化的问题
>在浏览地图的网格的时候发现，在拖动地图的时候，同一个网格会随着拖动改变颜色。严格意义上讲同一个网格的颜色
是不应该变的。具体原因还需要排查

## 8月30日
+ 解决了拖动地图网格的颜色值会变化的问题
>1.这个问题的罪魁祸首就是没有及时把timeprice_map清零。导致下一个网格中含有上一个网格的价格数据。
对，就是这句话 timeprice_map.clear();导致整个网格的显示都非常不正常。通过数据的一一对照和调试
最后才发现一个code的每个月时间点的价格list是叠加了上一个code的list的。原因就在与timeprice_map
是以时间为key的。而每一个code的时间点就那么几个，所以越到后面的code，叠加前面的数据就越多，也很好
地解释了为什么每拖动一次网格就变一次。
>除了上面那个问题，还有一个细节也是不能忽略的。
>rowmin=(r_min-1)*N+1;
         rowmax=r_max*N;
         colmin=(c_min-1)*N+1;
         colmax=c_max*N;
>2.对于屏幕边界的网格，不能只调用在屏幕范围内的小网格的数值，而是需要调用边界的大网格（不一定完全在屏幕内显示）
>所包含的全部数据。这个问题在网格值为月份价格数据的时候也忽略了，需要改正。
>也是因为这个问题，导致服务端传送过去的数据的code与网格code并没有一一对应上。所以这也是导致网格值变化的原因之一。
## 9月6日
+ 皮尔逊相关系数
>1.在求皮尔逊相关系数的时候发现，当N=5的时候，code为44556的网格与周边网格的相关系数都为0.不可思议。但是通过
排查，数据本身确实没问题啊，那问题出在哪里呢？
>2. 皮尔逊相关系数
通常情况下通过以下取值范围判断变量的相关强度：
相关系数     0.8-1.0     极强相关
                 0.6-0.8     强相关
                 0.4-0.6     中等程度相关
                 0.2-0.4     弱相关
                 0.0-0.2     极弱相关或无相关
>3. 协方差
    两组数[x1,x2...xn]和[y1,y2...yn]其协方差为
    cov(xy)=E(xy)-E(x)*E(y)=(1/n)*(x1*y1+...+xn*yn)-(1/(n*n))(x1+...+xn)(y1+...+yn)
## 09月07日
+ 数据问题
>44565:{"2015-11":8.843007,"2015-10":7.9607162,"2016-3":8.221647,"2016-5":8.401514,"2016-4":8.694678}
     []
 44953:{"2015-11":8.843007,"2015-10":7.9607162,"2016-3":8.221647,"2016-5":8.401514,"2016-4":8.694678}
     []
 44955:{"2015-11":8.843007,"2015-10":7.9607162,"2016-3":8.221647,"2016-5":8.401514,"2016-4":8.694678}
     []
 45356:{"2015-11":8.843007,"2015-10":7.9607162,"2016-3":8.221647,"2016-5":8.401514,"2016-4":8.694678}
 
 为什么这三个网格里面的值都是一样的？需要检查一下数据。
 
 





  
  