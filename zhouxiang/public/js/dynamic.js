function timedMsg() {
    //alert("执行定时程序");
    var t = setTimeout("setPriceGrid(\"房价\",\"\",\"\",\"我爱我家\",\"\",\"2015年10月\")", 1);
    console.log("10月");
    t = setTimeout("setPriceGrid(\"房价\",\"\",\"\",\"我爱我家\",\"\",\"2015年11月\")", 1000);
    console.log("11月");
    t = setTimeout("setPriceGrid(\"房价\",\"\",\"\",\"我爱我家\",\"\",\"2015年12月\")", 1000);
    console.log("12月");
    t = setTimeout("setPriceGrid(\"房价\",\"\",\"\",\"我爱我家\",\"\",\"2016年01月\")", 1000);
    console.log("1月");
    t = setTimeout("setPriceGrid(\"房价\",\"\",\"\",\"我爱我家\",\"\",\"2016年02月\")", 1000);
    console.log("2月");
    t = setTimeout("setPriceGrid(\"房价\",\"\",\"\",\"我爱我家\",\"\",\"2016年03月\")", 1000);
    console.log("3月");
    t = setTimeout("setPriceGrid(\"房价\",\"\",\"\",\"我爱我家\",\"\",\"2016年04月\")", 1000);
    console.log("4月");
    t = setTimeout("setPriceGrid(\"房价\",\"\",\"\",\"我爱我家\",\"\",\"2016年05月\")", 1000);
    console.log("5月");
}

function setPriceGrid(gridValue, startTime, endTime, source, computation, gridTime) {
    console.log(map);
    var bounds = map.getBounds();
    var west = bounds.getWest();
    var east = bounds.getEast();
    var south = bounds.getSouth();
    var north = bounds.getNorth();
    var zoom = map.getZoom();
    if (gridValue == "房价加速度") {
        var json = {
            "west": west,
            "east": east,
            "south": south,
            "north": north,
            "zoom": zoom,
            "starttime": startTime,
            "endtime": endTime,
            "source": source,
            "computation": computation
        };
        $.post("/gridacceleration_50", "" + JSON.stringify(json), function(result) {
            var price = JSON.parse(result);
            draw_rectangle_Acceleration_50(bounds, price);
        });
    } else if (gridValue == "房价") {
        var json = {
            "west": west,
            "east": east,
            "south": south,
            "north": north,
            "zoom": zoom,
            "gridTime": gridTime,
            "source": source
        };
        $.post("/gridcolor_50", "" + JSON.stringify(json), function(result) {
            var price = JSON.parse(result);
            //console.log("每移动一次开始画网格");
            draw_rectangle_houseprice_50(price); //1表示显示插值数据的曲线，2表示显示原始数据的曲线
        });
    }
}

function loopTimedMsg() {
    var int = self.setInterval("timedMsg()", 5000)
}