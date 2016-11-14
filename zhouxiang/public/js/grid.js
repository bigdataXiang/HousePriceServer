//creatGrid_investment_50：以50*50格网为基元，计算投资效益
function creatGrid_investment_50() {
    var bounds = map.getBounds();
    var west = bounds.getWest();
    var east = bounds.getEast();
    var south = bounds.getSouth();
    var north = bounds.getNorth();
    var zoom = map.getZoom();
    var x1 = document.getElementById("computation_invest").selectedIndex;
    var y1 = document.getElementById("computation_invest").options;
    var investment = y1[x1].text;
    var x2 = document.getElementById("gridTime_invest").selectedIndex;
    var y2 = document.getElementById("gridTime_invest").options;
    var time = y2[x2].text;
    var x3 = document.getElementById("source").selectedIndex;
    var y3 = document.getElementById("source").options;
    var source = y3[x3].text;
    var json = {
        "west": west,
        "east": east,
        "south": south,
        "north": north,
        "zoom": zoom,
        "gridTime": time,
        "source": source,
        "investment": investment
    };
    $.post("/investment", "" + JSON.stringify(json), function(result) {
        var price = JSON.parse(result);
        draw_rectangle_investment_50(price);
    });
}
//creatGrid：通过获取当前屏幕的经纬度坐标实现该范围内的网格可视化
function creatGrid() {
    var bounds = map.getBounds();
    console.log(bounds);
    var west = bounds.getWest();
    var east = bounds.getEast();
    var south = bounds.getSouth();
    var north = bounds.getNorth();
    var zoom = map.getZoom();
    console.log(zoom);
    var x = document.getElementById("gridValue").selectedIndex;
    var y = document.getElementById("gridValue").options;
    var gridValue = y[x].text;
    if (gridValue == "房价加速度") {
        x = document.getElementById("startTime").selectedIndex;
        y = document.getElementById("startTime").options;
        var startTime = y[x].text;
        x = document.getElementById("endTime").selectedIndex;
        y = document.getElementById("endTime").options;
        var endTime = y[x].text;
        x = document.getElementById("source").selectedIndex;
        y = document.getElementById("source").options;
        var source = y[x].text;
        x = document.getElementById("computation").selectedIndex;
        y = document.getElementById("computation").options;
        var computation = y[x].text;
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
        $.post("/gridacceleration", "" + JSON.stringify(json), function(result) {
            var price = JSON.parse(result);
            draw_rectangle_Acceleration(bounds, price);
        });
    } else if (gridValue == "房价") {
        x = document.getElementById("gridTime").selectedIndex;
        y = document.getElementById("gridTime").options;
        var gridTime = y[x].text;
        x = document.getElementById("source").selectedIndex;
        y = document.getElementById("source").options;
        var source = y[x].text;
        var json = {
            "west": west,
            "east": east,
            "south": south,
            "north": north,
            "zoom": zoom,
            "gridTime": gridTime,
            "source": source
        };
        $.post("/gridcolor", "" + JSON.stringify(json), function(result) {
            var price = JSON.parse(result);
            //console.log("每移动一次开始画网格");
            draw_rectangle_houseprice(price);
        });
    } else if (gridValue == "投资价格") {
        x = document.getElementById("computation_invest").selectedIndex;
        y = document.getElementById("computation_invest").options;
        var investment = y[x].text;
        x = document.getElementById("gridTime_invest").selectedIndex;
        y = document.getElementById("gridTime_invest").options;
        var time = y[x].text;
        x = document.getElementById("source").selectedIndex;
        y = document.getElementById("source").options;
        var source = y[x].text;
        var json = {
            "west": west,
            "east": east,
            "south": south,
            "north": north,
            "zoom": zoom,
            "gridTime": time,
            "source": source,
            "investment": investment
        };
        $.post("/investment", "" + JSON.stringify(json), function(result) {
            var price = JSON.parse(result);
            draw_rectangle_investment_50(price);
        });
    }
}
//creatGrid_50：通过获取当前屏幕的经纬度坐标实现该范围内的网格可视化(基础数据是50*50网格)
function creatGrid_50() {
    var bounds = map.getBounds();
    console.log(bounds);
    var west = bounds.getWest();
    var east = bounds.getEast();
    var south = bounds.getSouth();
    var north = bounds.getNorth();
    var zoom = map.getZoom();
    console.log(zoom);
    var x = document.getElementById("gridValue_50").selectedIndex;
    var y = document.getElementById("gridValue_50").options;
    var gridValue = y[x].text;
    if (gridValue == "房价加速度") {
        x = document.getElementById("startTime_50").selectedIndex;
        y = document.getElementById("startTime_50").options;
        var startTime = y[x].text;
        x = document.getElementById("endTime_50").selectedIndex;
        y = document.getElementById("endTime_50").options;
        var endTime = y[x].text;
        x = document.getElementById("source_50").selectedIndex;
        y = document.getElementById("source_50").options;
        var source = y[x].text;
        x = document.getElementById("computation_50").selectedIndex;
        y = document.getElementById("computation_50").options;
        var computation = y[x].text;
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
        x = document.getElementById("gridTime_50").selectedIndex;
        y = document.getElementById("gridTime_50").options;
        var gridTime = y[x].text;
        x = document.getElementById("source_50").selectedIndex;
        y = document.getElementById("source_50").options;
        var source = y[x].text;
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
            console.log("每移动一次开始画网格");
            draw_rectangle_houseprice_50(price); //1表示显示插值数据的曲线，2表示显示原始数据的曲线
        });
    }
}
//dragend：拖动地图时能实现不同网格的生成，主要用于同一zoom下的不同范围内的网格生成
map.on('dragend', function(e) {
    creatGrid();
    creatGrid_50();
});
//zoomend：缩放地图时能实现不同分辨率下网格的生成，主要用于不同zoom下当前屏幕范围内的网格生成
map.on('zoomend', function(e) {
    creatGrid();
    creatGrid_50();
});

function draw_circle_cityfocus(school) {
    var myIcon = L.icon({
        iconUrl: 'http://c.hiphotos.baidu.com/image/w%3D310/sign=913f487c4336acaf59e090fd4cd88d03/5fdf8db1cb13495453f3e3215e4e9258d0094a81.jpg',
        iconRetinaUrl: 'http://h.hiphotos.baidu.com/image/w%3D310/sign=705c47440255b3199cf9847473a88286/03087bf40ad162d92d4c695919dfa9ec8b13cdd1.jpg',
        iconSize: [50, 94],
        iconAnchor: [22, 94],
        popupAnchor: [-3, -76],
        shadowUrl: 'http://a.hiphotos.baidu.com/image/w%3D310/sign=99ae3420bb19ebc4c0787098b227cf79/7af40ad162d9f2d3b7b32faaa1ec8a136227ccd1.jpg',
        shadowRetinaUrl: 'http://d.hiphotos.baidu.com/image/w%3D310/sign=2dd97543a4af2eddd4f14fe8bd110102/8cb1cb13495409238684683b9a58d109b2de4981.jpg',
        shadowSize: [68, 95],
        shadowAnchor: [22, 94]
    });
    var size = school.data.length;
    var lng;
    var lat;
    var info;
    var title;
    var address;
    var location;
    var type;
    var content;
    var latlng;
    var popup;
    gradeOptions.radius = 10;
    gradeOptions.color = gradeOptions.fillColor = 'hsl(' + 359 + ',100%,70%)';
    for (var i = 0; i < size; i++) {
        info = school.data[i];
        lng = info.longitude;
        lat = info.latitude;
        /*var marker =L.marker([lat,lng,100],{icon: myIcon});*/
        var marker = L.mapMarker([lat, lng, 100], gradeOptions);
        map.addLayer(marker);
        title = info.school;
        location = info.region;
        address = info.address;
        type = info.type;
        content = "<DIV><span style=\"font-weight:bold;\"></span></DIV><HR/><DIV><span>" + title + "</span></DIV><BR/><DIV><span  style=\"font-weight:bold;\">区域:   </span> <span>" + location + "</span></DIV><DIV><span  style=\"font-weight:bold;\">地址:  </span> <span>" + address + "</span></DIV><DIV><span  style=\"font-weight:bold;\">类型:   </span><span>" + type + "<span></DIV>";
        latlng = L.latLng(lat, lng, 100);
        popup = L.popup()
            .setLatLng(latlng)
            .setContent(content);
        // .openOn(map);
        marker.bindPopup(popup);
        cityfocus.push(marker);
    }
}

function draw_circle_regionfocus(school) {
    var myIcon = L.icon({
        iconUrl: 'http://c.hiphotos.baidu.com/image/w%3D310/sign=913f487c4336acaf59e090fd4cd88d03/5fdf8db1cb13495453f3e3215e4e9258d0094a81.jpg',
        iconRetinaUrl: 'http://h.hiphotos.baidu.com/image/w%3D310/sign=705c47440255b3199cf9847473a88286/03087bf40ad162d92d4c695919dfa9ec8b13cdd1.jpg',
        iconSize: [50, 94],
        iconAnchor: [22, 94],
        popupAnchor: [-3, -76],
        shadowUrl: 'http://a.hiphotos.baidu.com/image/w%3D310/sign=99ae3420bb19ebc4c0787098b227cf79/7af40ad162d9f2d3b7b32faaa1ec8a136227ccd1.jpg',
        shadowRetinaUrl: 'http://d.hiphotos.baidu.com/image/w%3D310/sign=2dd97543a4af2eddd4f14fe8bd110102/8cb1cb13495409238684683b9a58d109b2de4981.jpg',
        shadowSize: [68, 95],
        shadowAnchor: [22, 94]
    });
    var size = school.data.length;
    var lng;
    var lat;
    var info;
    var title;
    var address;
    var location;
    var type;
    var content;
    var latlng;
    var popup;
    gradeOptions.radius = 10;
    gradeOptions.color = gradeOptions.fillColor = 'hsl(' + 285 + ',100%,70%)';
    for (var i = 0; i < size; i++) {
        info = school.data[i];
        lng = info.longitude;
        lat = info.latitude;
        /*var marker =L.marker([lat,lng,100],{icon: myIcon});*/
        var marker = L.mapMarker([lat, lng, 100], gradeOptions);
        map.addLayer(marker);
        title = info.school;
        location = info.region;
        address = info.address;
        type = info.type;
        content = "<DIV><span style=\"font-weight:bold;\"></span></DIV><HR/><DIV><span>" + title + "</span></DIV><BR/><DIV><span  style=\"font-weight:bold;\">区域:   </span> <span>" + location + "</span></DIV><DIV><span  style=\"font-weight:bold;\">地址:  </span> <span>" + address + "</span></DIV><DIV><span  style=\"font-weight:bold;\">类型:   </span><span>" + type + "<span></DIV>";
        latlng = L.latLng(lat, lng, 100);
        popup = L.popup()
            .setLatLng(latlng)
            .setContent(content);
        //.openOn(map);
        marker.bindPopup(popup);
        regionfocus.push(marker);
    }
}

function draw_circle_ordinary(school) {
    var myIcon = L.icon({
        iconUrl: 'http://c.hiphotos.baidu.com/image/w%3D310/sign=913f487c4336acaf59e090fd4cd88d03/5fdf8db1cb13495453f3e3215e4e9258d0094a81.jpg',
        iconRetinaUrl: 'http://h.hiphotos.baidu.com/image/w%3D310/sign=705c47440255b3199cf9847473a88286/03087bf40ad162d92d4c695919dfa9ec8b13cdd1.jpg',
        iconSize: [50, 94],
        iconAnchor: [22, 94],
        popupAnchor: [-3, -76],
        shadowUrl: 'http://a.hiphotos.baidu.com/image/w%3D310/sign=99ae3420bb19ebc4c0787098b227cf79/7af40ad162d9f2d3b7b32faaa1ec8a136227ccd1.jpg',
        shadowRetinaUrl: 'http://d.hiphotos.baidu.com/image/w%3D310/sign=2dd97543a4af2eddd4f14fe8bd110102/8cb1cb13495409238684683b9a58d109b2de4981.jpg',
        shadowSize: [68, 95],
        shadowAnchor: [22, 94]
    });
    var size = school.data.length;
    var lng;
    var lat;
    var info;
    var title;
    var address;
    var location;
    var type;
    var content;
    var latlng;
    var popup;
    gradeOptions.radius = 10;
    gradeOptions.color = gradeOptions.fillColor = 'hsl(' + 240 + ',100%,70%)';
    for (var i = 0; i < size; i++) {
        info = school.data[i];
        lng = info.longitude;
        lat = info.latitude;
        /*var marker =L.marker([lat,lng,100],{icon: myIcon});*/
        var marker = L.mapMarker([lat, lng, 100], gradeOptions);
        map.addLayer(marker);
        title = info.school;
        location = info.region;
        address = info.address;
        type = info.type;
        content = "<DIV><span style=\"font-weight:bold;\"></span></DIV><HR/><DIV><span>" + title + "</span></DIV><BR/><DIV><span  style=\"font-weight:bold;\">区域:   </span> <span>" + location + "</span></DIV><DIV><span  style=\"font-weight:bold;\">地址:  </span> <span>" + address + "</span></DIV><DIV><span  style=\"font-weight:bold;\">类型:   </span><span>" + type + "<span></DIV>";
        latlng = L.latLng(lat, lng, 100);
        popup = L.popup()
            .setLatLng(latlng)
            .setContent(content);
        // .openOn(map);
        marker.bindPopup(popup);
        ordinary.push(marker);
    }
}

function draw_Polygon(contour, value, retain) {
    if (retain == 1) {
        //将polygon全部删除
        if (polygon_array.length != 0) {
            for (var i = 0; i < polygon_array.length; i++) {
                map = map.removeLayer(polygon_array[i]);
            }
            polygon_array = [];
        }
    } else if (retain == 0) {
        //不删除之前的多线图层
    }
    var color;
    color = colorSet(value);
    console.log(color);
    //color=randomColor(1);
    for (var tag = 0; tag < contour.length; tag++) {
        var coordinates;
        if (contour[tag].type == "Polygon") {
            var independent_polygon = contour[tag].coordinates;
            draw_independent_polygon(independent_polygon, color, tag);
        } else if (contour[tag].type == "MultiPolygon") {
            var independent_polygons = contour[tag].coordinates;
            var length = independent_polygons.length;
            console.log(length);
            for (var len = 0; len < length; len++) {
                var coors = independent_polygons[len];
                draw_independent_polygon(coors, color, tag);
            }
        }
    }
}

function colorSet(value) {
    var color;
    if (value == 2) {
        color = "#077BEA";
    } else if (value == 3) {
        color = "#03EAE4";
    } else if (value == 4) {
        color = "#06E884";
    } else if (value == 5) {
        color = "#B8E705";
    } else if (value == 6) {
        color = "#E9E507";
    } else if (value == 7) {
        color = "#E9A708";
    } else if (value == 8) {
        color = "#EA4706";
    } else if (value == 9) {
        color = "#C70305";
    } else if (value == 10) {
        color = "#C70305";
    } else if (value == 11) {
        color = "#C70305";
    }
    return color;
}
//画一个单独的多边形，该多边形里面可能含有内洞
function draw_independent_polygon(independent_polygon, color, k) {
    var latlngss = [];
    var latlngs = [];
    var latlng;
    //计算一共有多少个封闭的多边形
    var len = independent_polygon.length;
    console.log(len);
    for (var mm = 0; mm < len; mm++) {
        var coors = independent_polygon[mm];
        //分别画每一个多边形，获取每一个多边形里面的坐标点，存放在latlngs中
        for (var i = 0; i < coors.length; i++) {
            var coordinate = coors[i];
            var lng = coordinate[0];
            var lat = coordinate[1];
            latlng = L.latLng(lat, lng);
            latlngs.push(latlng);
        }
        latlngss.push(latlngs);
        latlngs = [];
    }
    var poly = new L.Polygon(latlngss, {
        color: color,
        fillColor: color,
        stroke: true,
        opacity: 0.8, //不透明度
        fillOpacity: 0.8,
        weight: 5
    });
    map.addLayer(poly);
    polygon_array.push(poly);
    var content = "<DIV><span style=\"font-weight:bold;\"></span></DIV><HR/><DIV><span>" + "第" + k + "个polygon" + "</span></DIV>";
    var popup = L.popup()
        .setLatLng(latlngs[0])
        .setContent(content);
    //.openOn(map);
    poly.bindPopup(popup);
}

function draw_multiPolyline(contour, value, retain) {
    if (retain == 1) {
        //将之前的多线图层删除
        if (polyline_array.length != 0) {
            for (var i = 0; i < polyline_array.length; i++) {
                map = map.removeLayer(polyline_array[i]);
            }
            polyline_array = [];
        }
    } else if (retain == 0) {
        //不删除之前的多线图层
    }
    var color; //randomColor(0.4)
    color = colorSet(value); //根据value值设置色彩阶梯
    color = randomColor(1); //设置随机色
    var poly_line = new L.Polyline([], {
        color: color,
        fillColor: color,
        stroke: true,
        weight: 3,
        smoothFactor: 5,
        fillOpacity: 1,
        opacity: 1
    }); //折线
    for (var tag = 0; tag < contour.length; tag++) {
        var poly_points = [];
        var coordinates;
        var latlng;
        if (contour[tag].type == "Polygon") {
            //计算一共有多少个封闭的多边形
            var len = contour[tag].coordinates.length;
            console.log(len);
            for (var mm = 0; mm < len; mm++) {
                var coors = contour[tag].coordinates[mm];
                //分别画每一个多边形，获取每一个多边形里面的坐标点，存放在poly_points中
                for (var i = 0; i < coors.length; i++) {
                    var coordinate = coors[i];
                    var lng = coordinate[0];
                    var lat = coordinate[1];
                    latlng = L.latLng(lat, lng);
                    poly_points.push(latlng);
                    poly_line.addLatLng(latlng);
                }
                map.addLayer(poly_line);
                polyline_array.push(poly_line);
                color = randomColor(1);
                poly_points = [];
                poly_line = new L.Polyline([], {
                    color: color,
                    fillColor: color,
                    stroke: true,
                    weight: 3,
                    smoothFactor: 5,
                    fillOpacity: 1,
                    opacity: 1
                }); //折线
            }
        } else if (contour[tag].type == "MultiPolygon") {
            var lenen = contour[tag].coordinates.length;
            console.log(lenen);
            for (var m = 0; m < lenen; m++) {
                var len1 = contour[tag].coordinates[m].length;
                console.log(len1);
                for (var j = 0; j < len1; j++) {
                    coordinates = contour[tag].coordinates[m][j];
                    var len2 = coordinates.length;
                    console.log(len2);
                    for (var i = 0; i < len2; i++) {
                        var coordinate = coordinates[i];
                        var lng = coordinate[0];
                        var lat = coordinate[1];
                        latlng = L.latLng(lat, lng);
                        poly_points.push(latlng);
                        poly_line.addLatLng(latlng);
                    }
                    map.addLayer(poly_line);
                    polyline_array.push(poly_line);
                    //color=randomColor(1);
                    poly_points = [];
                    poly_line = new L.Polyline([], {
                        color: color,
                        fillColor: color,
                        stroke: true,
                        weight: 3,
                        smoothFactor: 5,
                        fillOpacity: 1,
                        opacity: 1
                    }); //折线
                }
            }
        }
    }
    alert("总共画了" + contour.length + "个多边形");
}
//将所有的网格全部删除
function delect_all_rectangle() {
    //将之前的图层删除
    if (rects.length != 0) {
        for (var i = 0; i < rects.length; i++) {
            map = map.removeLayer(rects[i]);
        }
        rects = [];
    }
}
//原始数据网格绘制
function draw_rectangle_houseprice(price) {
    var N = price.N;
    var width = 0.0011785999999997187 * N; //每N00m的经度差
    var length = 9.003999999997348E-4 * N; //每N00m的纬度差
    var r_min = price.r_min;
    var r_max = price.r_max;
    var c_min = price.c_min;
    var c_max = price.c_max;
    var row;
    var col;
    //将之前的图层删除
    if (rects.length != 0) {
        for (var i = 0; i < rects.length; i++) {
            map = map.removeLayer(rects[i]);
        }
        rects = [];
    }
    var count = 0;
    for (row = r_min; row <= r_max; row++) {
        for (col = c_min; col <= c_max; col++) {
            //parseInt:取整数部分
            //Math.ceil():只要有小数就加1
            var cols = parseInt(2000 / N);
            var gridcode_js = col + cols * (row - 1);
            //网格是从左至右一行一行地填充，故对于某一固定的行，每个网格距离初始网格的经度跨度一直在变，而纬度跨度不变
            var dist_wid = (col - 1) * width;
            var dist_len = (row - 1) * length;
            var bounds = [
                [39.438283 + dist_len, (115.417284 + dist_wid)],
                [(39.438283 + length) + dist_len, ((115.417284 + width) + dist_wid)]
            ]; //(左下角，右上角)
            var rect;
            var gridvalue = price.data[count];
            var gridcode_java = gridvalue.code;
            var gridcolor = gridvalue.color;
            var gridrow = gridvalue.row;
            var gridcol = gridvalue.col;
            var averageprice = gridvalue.average_price;
            if (gridcode_js == gridcode_java) {
                if (averageprice != 0) {
                    //console.log(count);
                    rect = L.rectangle(bounds, {
                        color: mycolor(255, 255, 255),
                        opacity: 0.9,
                        fillColor: gridcolor,
                        weight: 1,
                        fillOpacity: 0.6,
                        className: "" + gridcode_java
                    }); //,className:gridcode_java
                    rect.index = count;
                    rect.data = price.data[rect.index];
                    rect.on('click', function(e) {
                        var json = {
                            "row": this.data.row,
                            "col": this.data.col,
                            "code": this.data.code,
                            "N": N
                        };
                        $.post("/price", JSON.stringify(json),
                            function(data) {
                                var ctx = document.getElementById("myChart").getContext("2d");
                                pricedata = JSON.parse(data);
                                var max = pricedata.suggestedMax;
                                var min = pricedata.suggestedMin;
                                var grid = JSON.stringify(json);
                                var gridinfo = JSON.parse(grid);
                                var info = "编码：" + gridinfo.code + "," + gridinfo.row + "行" + gridinfo.col + "列";
                                window.myLine = new Chart(ctx, dataStruct(pricedata, info, max, min));
                                $('#myModal').modal();
                            }
                        )
                    });
                    rect.addTo(map);
                    rects.push(rect);
                }
                count++;
            }
            //count++;
        }
    }
}

function draw_rectangle_Acceleration(bounds, price) {
    var west = bounds.getWest();
    var east = bounds.getEast();
    var south = bounds.getSouth();
    var north = bounds.getNorth();
    var N = price.N;
    var width = 0.0011785999999997187 * N; //每N00m的经度差
    var length = 9.003999999997348E-4 * N; //每N00m的纬度差
    var r_min = price.r_min;
    var r_max = price.r_max;
    var c_min = price.c_min;
    var c_max = price.c_max;
    var row;
    var col;
    //将之前的图层删除
    if (rects.length != 0) {
        for (var i = 0; i < rects.length; i++) {
            map = map.removeLayer(rects[i]);
        }
        rects = [];
    }
    var count = 0;
    for (row = r_min; row <= r_max; row++) {
        for (col = c_min; col <= c_max; col++) {
            //网格是从左至右一行一行地填充，故对于某一固定的行，每个网格距离初始网格的经度跨度一直在变，而纬度跨度不变
            var dist_wid = (col - 1) * width;
            var dist_len = (row - 1) * length;
            var bounds = [
                [39.438283 + dist_len, (115.417284 + dist_wid)],
                [(39.438283 + length) + dist_len, ((115.417284 + width) + dist_wid)]
            ]; //(左下角，右上角)
            var rect;
            var gridvalue = price.data[count];
            var gridcode_java = gridvalue.code;
            var gridcode_js = col + (row - 1) * (2000 / N);
            var gridcolor = gridvalue.color;
            var gridrow = gridvalue.row;
            var gridcol = gridvalue.col;
            var acceleration = gridvalue.acceleration;
            if (acceleration != 0) {
                rect = L.rectangle(bounds, {
                    color: mycolor(255, 255, 255),
                    opacity: 0.9,
                    fillColor: gridcolor,
                    weight: 1,
                    fillOpacity: 0.6,
                    className: "" + gridcode_js + "," + row + "," + col
                }); //,className:gridcode_java
                rect.index = count;
                rect.data = price.data[rect.index];
                rect.on('click', function(e) {
                    var json = {
                        "row": this.data.row,
                        "col": this.data.col,
                        "code": this.data.code,
                        "N": N
                    };
                    $.post("/price", JSON.stringify(json),
                        function(data) {
                            var ctx = document.getElementById("myChart").getContext("2d");
                            pricedata = JSON.parse(data);
                            var max = pricedata.suggestedMax;
                            var min = pricedata.suggestedMin;
                            var grid = JSON.stringify(json);
                            var gridinfo = JSON.parse(grid);
                            var info = "编码：" + gridinfo.code + "," + gridinfo.row + "行" + gridinfo.col + "列";
                            window.myLine = new Chart(ctx, dataStruct(pricedata, info, max, min));
                            $('#myModal').modal();
                        }
                    )
                });
                rect.addTo(map);
                rects.push(rect);
            }
            count++;
        }
    }
}

function draw_rectangle_interpolation(price) {
    var N = price.N;
    var width = 0.0011785999999997187 * N; //每N00m的经度差
    var length = 9.003999999997348E-4 * N; //每N00m的纬度差
    var r_min = price.r_min;
    var r_max = price.r_max;
    var c_min = price.c_min;
    var c_max = price.c_max;
    var row;
    var col;
    //将之前的图层删除
    if (rects.length != 0) {
        for (var i = 0; i < rects.length; i++) {
            map = map.removeLayer(rects[i]);
        }
        rects = [];
    }
    var count = 0;
    for (row = r_min; row <= r_max; row++) {
        for (col = c_min; col <= c_max; col++) {
            var gridcode_js = col + (2000 / N) * (row - 1);
            //网格是从左至右一行一行地填充，故对于某一固定的行，每个网格距离初始网格的经度跨度一直在变，而纬度跨度不变
            var dist_wid = (col - 1) * width;
            var dist_len = (row - 1) * length;
            var bounds = [
                [39.438283 + dist_len, (115.417284 + dist_wid)],
                [(39.438283 + length) + dist_len, ((115.417284 + width) + dist_wid)]
            ]; //(左下角，右上角)
            var rect;
            var gridvalue = price.data[count];
            var gridcode_java = gridvalue.code;
            var gridcolor = gridvalue.color;
            var gridrow = gridvalue.row;
            var gridcol = gridvalue.col;
            var averageprice = gridvalue.average_price;
            if (gridcode_js == gridcode_java) {
                if (averageprice != 0) {
                    //console.log(count);
                    rect = L.rectangle(bounds, {
                        color: mycolor(255, 255, 255),
                        opacity: 0.9,
                        fillColor: gridcolor,
                        weight: 1,
                        fillOpacity: 0.6,
                        className: "" + gridcode_java
                    }); //,className:gridcode_java
                    rect.index = count;
                    rect.data = price.data[rect.index];
                    rect.on('click', function(e) {
                        var json = {
                            "row": this.data.row,
                            "col": this.data.col,
                            "code": this.data.code,
                            "N": N
                        };
                        $.post("/price", JSON.stringify(json),
                            function(data) {
                                var ctx = document.getElementById("myChart").getContext("2d");
                                pricedata = JSON.parse(data);
                                var max = pricedata.suggestedMax;
                                var min = pricedata.suggestedMin;
                                var grid = JSON.stringify(json);
                                var gridinfo = JSON.parse(grid);
                                var info = "编码：" + gridinfo.code + "," + gridinfo.row + "行" + gridinfo.col + "列";
                                window.myLine = new Chart(ctx, dataStruct(pricedata, info, max, min));
                                $('#myModal').modal();
                            }
                        )
                    });
                    rect.addTo(map);
                    rects.push(rect);
                }
                count++;
            }
            //count++;
        }
    }
}
//插值数据网格绘制
function draw_rectangle_houseprice_50(price) {
    var N = price.N;
    var width = 5.892999999998593E-4 * N; //每N50m的经度差
    var length = 4.501999999998674E-4 * N; //每N50m的纬度差
    var r_min = price.r_min;
    var r_max = price.r_max;
    var c_min = price.c_min;
    var c_max = price.c_max;
    var row;
    var col;
    //将之前的图层删除
    if (rects.length != 0) {
        for (var i = 0; i < rects.length; i++) {
            map = map.removeLayer(rects[i]);
        }
        rects = [];
    }
    var count = 0;
    for (row = r_min; row <= r_max; row++) {
        for (col = c_min; col <= c_max; col++) {
            //parseInt:取整数部分
            //Math.ceil():只要有小数就加1
            var cols = parseInt(4000 / N);
            var gridcode_js = col + cols * (row - 1);
            //网格是从左至右一行一行地填充，故对于某一固定的行，每个网格距离初始网格的经度跨度一直在变，而纬度跨度不变
            var dist_wid = (col - 1) * width;
            var dist_len = (row - 1) * length;
            var bounds = [
                [39.438283 + dist_len, (115.417284 + dist_wid)],
                [(39.438283 + length) + dist_len, ((115.417284 + width) + dist_wid)]
            ]; //(左下角，右上角)
            var rect;
            var gridvalue = price.data[count];
            var gridcode_java = gridvalue.code;
            var gridcolor = gridvalue.color;
            var gridrow = gridvalue.row;
            var gridcol = gridvalue.col;
            var averageprice = gridvalue.average_price;
            if (gridcode_js == gridcode_java) {
                if (averageprice != 0) {
                    //console.log(count);
                    rect = L.rectangle(bounds, {
                        color: mycolor(255, 255, 255),
                        opacity: 0.9,
                        fillColor: gridcolor,
                        weight: 1,
                        fillOpacity: 0.6,
                        className: "" + gridcode_java
                    }); //,className:gridcode_java
                    rect.index = count;
                    rect.data = price.data[rect.index];
                    rect.on('click', function(e) {
                        var json = {
                            "row": this.data.row,
                            "col": this.data.col,
                            "code": this.data.code,
                            "N": N
                        };
                        $.post("/pricecurve_50", JSON.stringify(json),
                            function(data) {
                                var ctx = document.getElementById("myChart").getContext("2d");
                                pricedata = JSON.parse(data);
                                var max = pricedata.suggestedMax;
                                var min = pricedata.suggestedMin;
                                var grid = JSON.stringify(json);
                                var gridinfo = JSON.parse(grid);
                                var info = "编码：" + gridinfo.code + "," + gridinfo.row + "行" + gridinfo.col + "列";
                                window.myLine = new Chart(ctx, dataStruct(pricedata, info, max, min));
                                $('#myModal').modal();
                            }
                        )
                    });
                    rect.addTo(map);
                    rects.push(rect);
                }
                count++;
            }
        }
    }
}

function draw_rectangle_Acceleration_50(bounds, price) {
    var N = price.N;
    var width = 5.892999999998593E-4 * N; //每N50m的经度差
    var length = 4.501999999998674E-4 * N; //每N50m的纬度差
    var r_min = price.r_min;
    var r_max = price.r_max;
    var c_min = price.c_min;
    var c_max = price.c_max;
    var row;
    var col;
    //将之前的图层删除
    if (rects.length != 0) {
        for (var i = 0; i < rects.length; i++) {
            map = map.removeLayer(rects[i]);
        }
        rects = [];
    }
    var count = 0;
    for (row = r_min; row <= r_max; row++) {
        for (col = c_min; col <= c_max; col++) {
            //网格是从左至右一行一行地填充，故对于某一固定的行，每个网格距离初始网格的经度跨度一直在变，而纬度跨度不变
            var dist_wid = (col - 1) * width;
            var dist_len = (row - 1) * length;
            var bounds = [
                [39.438283 + dist_len, (115.417284 + dist_wid)],
                [(39.438283 + length) + dist_len, ((115.417284 + width) + dist_wid)]
            ]; //(左下角，右上角)
            var rect;
            var gridvalue = price.data[count];
            var gridcode_java = gridvalue.code;
            var gridcode_js = col + (row - 1) * (4000 / N);
            var gridcolor = gridvalue.color;
            var gridrow = gridvalue.row;
            var gridcol = gridvalue.col;
            var acceleration = gridvalue.acceleration;
            if (acceleration != 0) {
                rect = L.rectangle(bounds, {
                    color: mycolor(255, 255, 255),
                    opacity: 0.9,
                    fillColor: gridcolor,
                    weight: 1,
                    fillOpacity: 0.6,
                    className: "" + gridcode_js + "," + row + "," + col
                }); //,className:gridcode_java
                rect.index = count;
                rect.data = price.data[rect.index];
                rect.on('click', function(e) {
                    var json = {
                        "row": this.data.row,
                        "col": this.data.col,
                        "code": this.data.code,
                        "N": N
                    };
                    $.post("/pricecurve_50", JSON.stringify(json),
                        function(data) {
                            var ctx = document.getElementById("myChart").getContext("2d");
                            pricedata = JSON.parse(data);
                            var max = pricedata.suggestedMax;
                            var min = pricedata.suggestedMin;
                            var grid = JSON.stringify(json);
                            var gridinfo = JSON.parse(grid);
                            var info = "编码：" + gridinfo.code + "," + gridinfo.row + "行" + gridinfo.col + "列";
                            window.myLine = new Chart(ctx, dataStruct(pricedata, info, max, min));
                            $('#myModal').modal();
                        }
                    )
                });
                rect.addTo(map);
                rects.push(rect);
            }
            count++;
        }
    }
}

function draw_rectangle_investment_50(price) {
    var N = price.N;
    var width = 5.892999999998593E-4 * N; //每N50m的经度差
    var length = 4.501999999998674E-4 * N; //每N50m的纬度差
    var r_min = price.r_min;
    var r_max = price.r_max;
    var c_min = price.c_min;
    var c_max = price.c_max;
    var row;
    var col;
    //将之前的图层删除
    if (rects.length != 0) {
        for (var i = 0; i < rects.length; i++) {
            map = map.removeLayer(rects[i]);
        }
        rects = [];
    }
    var count = 0;
    for (row = r_min; row <= r_max; row++) {
        for (col = c_min; col <= c_max; col++) {
            //parseInt:取整数部分
            //Math.ceil():只要有小数就加1
            var cols = parseInt(4000 / N);
            var gridcode_js = col + cols * (row - 1);
            //网格是从左至右一行一行地填充，故对于某一固定的行，每个网格距离初始网格的经度跨度一直在变，而纬度跨度不变
            var dist_wid = (col - 1) * width;
            var dist_len = (row - 1) * length;
            var bounds = [
                [39.438283 + dist_len, (115.417284 + dist_wid)],
                [(39.438283 + length) + dist_len, ((115.417284 + width) + dist_wid)]
            ]; //(左下角，右上角)
            var rect;
            var gridvalue = price.data[count];
            var gridcode_java = gridvalue.code;
            var gridcolor = gridvalue.color;
            var gridrow = gridvalue.row;
            var gridcol = gridvalue.col;
            var averageprice = gridvalue.average_price;
            if (gridcode_js == gridcode_java) {
                if (averageprice != 0) {
                    //console.log(count);
                    rect = L.rectangle(bounds, {
                        color: mycolor(255, 255, 255),
                        opacity: 0.9,
                        fillColor: gridcolor,
                        weight: 1,
                        fillOpacity: 0.6,
                        className: "" + gridcode_java
                    }); //,className:gridcode_java
                    rect.index = count;
                    rect.data = price.data[rect.index];
                    rect.on('click', function(e) {
                        var json = {
                            "row": this.data.row,
                            "col": this.data.col,
                            "code": this.data.code,
                            "N": N
                        };
                        $.post("/gridinfo", JSON.stringify(json), function(data) {
                                var information = JSON.parse(data);
                                var gridinfo = information.info;
                                $('#infoModal').modal();
                                $("#text1").html(gridinfo);
                            })
                            /*$("#text1").html("SSS");
                            $('#infoModal').modal();*/
                    });
                    rect.addTo(map);
                    rects.push(rect);
                }
                count++;
            }
        }
    }
}

function changeText() {
    document.getElementById('text2').innerHTML = 'Fred Flinstone Fred Flinstone Fred Flinstone Fred Flinstone';
    document.getElementById('text3').innerHTML = 'Fred Flinstone Fred FlinstoneFred FlinstoneFred FlinstoneFred FlinstoneFred Flinstone';
}
resize();
//生成随机颜色和尺度
var randomScalingFactor = function() {
    return Math.round(Math.random() * 100);
    //return 0;
};
var randomColorFactor = function() {
    return Math.round(Math.random() * 255);
};
var randomColor = function(opacity) {
    return 'rgba(' + randomColorFactor() + ',' + randomColorFactor() + ',' + randomColorFactor() + ',' + (opacity || '.3') + ')';
};
var MONTHS = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];