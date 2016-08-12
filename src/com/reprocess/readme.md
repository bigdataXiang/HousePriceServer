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


  
  