$("#schoolType").change(function() {
    var x = document.getElementById("schoolType").selectedIndex;
    var y = document.getElementById("schoolType").options;
    var schoolType = y[x].text;
    if (schoolType == "市重点小学") {
        for (var i = 0; i < cityfocus.length; i++) {
            map.removeLayer(cityfocus[i]);
        }
        for (var i = 0; i < regionfocus.length; i++) {
            map.removeLayer(regionfocus[i]);
        }
        for (var i = 0; i < ordinary.length; i++) {
            map.removeLayer(ordinary[i]);
        }
        //市重点
        $.post("/static/city_focused", "" + map.getZoom(), function(result) {
            var school = JSON.parse(result);
            draw_circle_cityfocus(school);
        });
    } else if (schoolType == "区重点小学") {
        for (var i = 0; i < cityfocus.length; i++) {
            map.removeLayer(cityfocus[i]);
        }
        for (var i = 0; i < regionfocus.length; i++) {
            map.removeLayer(regionfocus[i]);
        }
        for (var i = 0; i < ordinary.length; i++) {
            map.removeLayer(ordinary[i]);
        }
        //区重点
        $.post("/static/region_focused", "" + map.getZoom(), function(result) {
            var school = JSON.parse(result);
            draw_circle_regionfocus(school);
        });
    } else if (schoolType == "普通小学") {
        for (var i = 0; i < cityfocus.length; i++) {
            map.removeLayer(cityfocus[i]);
        }
        for (var i = 0; i < regionfocus.length; i++) {
            map.removeLayer(regionfocus[i]);
        }
        for (var i = 0; i < ordinary.length; i++) {
            map.removeLayer(ordinary[i]);
        }
        //普通小学
        $.post("/static/ordinary_primary_school", "" + map.getZoom(), function(result) {
            var school = JSON.parse(result);
            draw_circle_ordinary(school);
        });
    }
});
$("#chazhi").change(function() {
    var x = document.getElementById("chazhi").selectedIndex;
    var y = document.getElementById("chazhi").options;
    var chazhi = y[x].text;
    if (chazhi == "是") {
        alert("画插值后的网格图");
        console.log("删除之前的网格~");
        var json = {
            "gridTime": "2015年10月"
        };
        $.post("/gridcolor_interpolation", "" + JSON.stringify(json), function(result) {
            var price = JSON.parse(result);
            console.log("开始画网格");
            draw_rectangle_interpolation(price);
        });
    } else {
        alert("请到原始数据栏目中选择条件");
        creatGrid();
    }
});
$("#contourline").change(function() {
    var x = document.getElementById("contourline").selectedIndex;
    var y = document.getElementById("contourline").options;
    var contourline = y[x].text;
    if (contourline == "是") {
        /*$.post("/static/contourline/polygon_2", function (result) {
            var contour = JSON.parse(result);
            console.log("开始画等值线");
            draw_multiPolyline(contour,2);
        });*/
        $("#contourvalue").change(function() {
            var x = document.getElementById("contourvalue").selectedIndex;
            var y = document.getElementById("contourvalue").options;
            var contourvalue = y[x].text;
            /*如果选择全部画，那就需要删除之前的等值线了，故在删除等值线之前要做一个判断，1：表示删除之前的等值线，0表示不删除*/
            if (contourvalue == "2万——3万") {
                $.post("/static/contourline/polygon_2", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 2, 1);
                });
            } else if (contourvalue == "3万——4万") {
                $.post("/static/contourline/polygon_3", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 3, 1);
                });
            } else if (contourvalue == "4万——5万") {
                $.post("/static/contourline/polygon_4", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 4, 1);
                });
            } else if (contourvalue == "5万——6万") {
                $.post("/static/contourline/polygon_5", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 5, 1);
                });
            } else if (contourvalue == "6万——7万") {
                $.post("/static/contourline/polygon_6", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 6, 1);
                });
            } else if (contourvalue == "7万——8万") {
                $.post("/static/contourline/polygon_7", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 7, 1);
                });
            } else if (contourvalue == "8万——9万") {
                $.post("/static/contourline/polygon_8", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 8, 1);
                });
            } else if (contourvalue == "9万——10万") {
                $.post("/static/contourline/polygon_9", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 9, 1);
                });
            } else if (contourvalue == "10万——11万") {
                $.post("/static/contourline/polygon_10", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 10, 1);
                });
            } else if (contourvalue == "11万——12万") {
                $.post("/static/contourline/polygon_11", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 11, 1);
                });
            } else if (contourvalue == "全部") {
                $.post("/static/contourline/polygon_2", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 2, 0);
                });
                $.post("/static/contourline/polygon_3", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 3, 0);
                });
                $.post("/static/contourline/polygon_4", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 4, 0);
                });
                $.post("/static/contourline/polygon_5", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 5, 0);
                });
                $.post("/static/contourline/polygon_6", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 6, 0);
                });
                $.post("/static/contourline/polygon_7", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 7, 0);
                });
                $.post("/static/contourline/polygon_8", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 8, 0);
                });
                $.post("/static/contourline/polygon_9", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 9, 0);
                });
                $.post("/static/contourline/polygon_10", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 10, 0);
                });
                $.post("/static/contourline/polygon_11", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 11, 0);
                });
            }
        });
        //这是汉青的数据
        $("#contourvalue_hq").change(function() {
            var x = document.getElementById("contourvalue_hq").selectedIndex;
            var y = document.getElementById("contourvalue_hq").options;
            var contourvalue_hq = y[x].text;
            /*如果选择全部画，那就需要删除之前的等值线了，故在删除等值线之前要做一个判断，1：表示删除之前的等值线，0表示不删除*/
            if (contourvalue_hq == "2万——3万") {
                $.post("/static/contourline/hq/polygon_2", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 2, 1);
                });
            } else if (contourvalue_hq == "3万——4万") {
                $.post("/static/contourline/hq/polygon_3", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 3, 1);
                });
            } else if (contourvalue_hq == "4万——5万") {
                $.post("/static/contourline/hq/polygon_4", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 4, 1);
                });
            } else if (contourvalue_hq == "5万——6万") {
                $.post("/static/contourline/hq/polygon_5", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 5, 1);
                });
            } else if (contourvalue_hq == "6万——7万") {
                $.post("/static/contourline/hq/polygon_6", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 6, 1);
                });
            } else if (contourvalue_hq == "7万——8万") {
                $.post("/static/contourline/hq/polygon_7", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 7, 1);
                });
            } else if (contourvalue_hq == "8万——9万") {
                $.post("/static/contourline/hq/polygon_8", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 8, 1);
                });
            } else if (contourvalue_hq == "9万——10万") {
                $.post("/static/contourline/hq/polygon_9", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 9, 1);
                });
            } else if (contourvalue_hq == "10万——11万") {
                $.post("/static/contourline/hq/polygon_10", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 10, 1);
                });
            } else if (contourvalue_hq == "11万——12万") {
                $.post("/static/contourline/hq/polygon_11", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 11, 1);
                });
            } else if (contourvalue_hq == "全部") {
                $.post("/static/contourline/hq/polygon_2", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 2, 0);
                });
                $.post("/static/contourline/hq/polygon_3", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 3, 0);
                });
                $.post("/static/contourline/hq/polygon_4", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 4, 0);
                });
                $.post("/static/contourline/hq/polygon_5", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 5, 0);
                });
                $.post("/static/contourline/hq/polygon_6", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 6, 0);
                });
                $.post("/static/contourline/hq/polygon_7", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 7, 0);
                });
                $.post("/static/contourline/hq/polygon_8", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 8, 0);
                });
                $.post("/static/contourline/hq/polygon_9", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 9, 0);
                });
                $.post("/static/contourline/hq/polygon_10", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 10, 0);
                });
                $.post("/static/contourline/hq/polygon_11", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值线");
                    draw_multiPolyline(contour, 11, 0);
                });
            }
        });
    } else {
        //将之前的多线图层删除
        if (polyline_array.length != 0) {
            for (var i = 0; i < polyline_array.length; i++) {
                map = map.removeLayer(polyline_array[i]);
            }
            polyline_array = [];
        }
    }
});
$("#polygon").change(function() {
    var x = document.getElementById("polygon").selectedIndex;
    var y = document.getElementById("polygon").options;
    var polygon = y[x].text;
    if (polygon == "是") {
        $("#polyvalue").change(function() {
            var x = document.getElementById("polyvalue").selectedIndex;
            var y = document.getElementById("polyvalue").options;
            var polyvalue = y[x].text;
            if (polyvalue == "2万——3万") {
                $.post("/static/polygon/polygon_2", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 2, 1);
                });
            } else if (polyvalue == "3万——4万") {
                $.post("/static/polygon/polygon_3", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 3, 1);
                });
            } else if (polyvalue == "4万——5万") {
                $.post("/static/polygon/polygon_4", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 4, 1);
                });
            } else if (polyvalue == "5万——6万") {
                $.post("/static/polygon/polygon_5", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 5, 1);
                });
            } else if (polyvalue == "6万——7万") {
                $.post("/static/polygon/polygon_6", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 6, 1);
                });
            } else if (polyvalue == "7万——8万") {
                $.post("/static/polygon/polygon_7", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 7, 1);
                });
            } else if (polyvalue == "8万——9万") {
                $.post("/static/polygon/polygon_8", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 8, 1);
                });
            } else if (polyvalue == "9万——10万") {
                $.post("/static/polygon/polygon_9", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 9, 1);
                });
            } else if (polyvalue == "10万——11万") {
                $.post("/static/polygon/polygon_10", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 10, 1);
                });
            } else if (polyvalue == "11万——12万") {
                $.post("/static/polygon/polygon_11", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 11, 1);
                });
            } else if (polyvalue == "全部") {
                $.post("/static/polygon/polygon_2", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 2, 0);
                });
                $.post("/static/polygon/polygon_3", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 3, 0);
                });
                $.post("/static/polygon/polygon_4", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 4, 0);
                });
                $.post("/static/polygon/polygon_5", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 5, 0);
                });
                $.post("/static/polygon/polygon_6", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 6, 0);
                });
                $.post("/static/polygon/polygon_7", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 7, 0);
                });
                $.post("/static/polygon/polygon_8", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 8, 0);
                });
                $.post("/static/polygon/polygon_9", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 9, 0);
                });
                $.post("/static/polygon/polygon_10", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 10, 0);
                });
                $.post("/static/polygon/polygon_11", function(result) {
                    var contour = JSON.parse(result);
                    console.log("开始画等值面");
                    draw_Polygon(contour, 11, 0);
                });
            }
        });
    } else if (polygon == "否") {
        //将polygon全部删除
        if (polygon_array.length != 0) {
            for (var i = 0; i < polygon_array.length; i++) {
                map = map.removeLayer(polygon_array[i]);
            }
            polygon_array = [];
        }
    }
});
//弹出曲线框的时候触发的事件
$("#select_source").change(function() {
    var x = document.getElementById("select_source").selectedIndex;
    var y = document.getElementById("select_source").options;
    var select_source = y[x].text;
    if (select_source == "我爱我家") {} else if (select_source == "房天下") {} else if (select_source == "安居客") {} else if (select_source == "链家") {} else if (select_source == "全部") {}
    $('#changeDataObject').click(function() {
        config.data = {
            labels: ["July", "August", "September", "October", "November", "December"],
            datasets: [{
                label: "My First dataset",
                data: [randomScalingFactor(), randomScalingFactor(), randomScalingFactor(), randomScalingFactor(), randomScalingFactor(), randomScalingFactor(), randomScalingFactor(), randomScalingFactor(), randomScalingFactor(), randomScalingFactor(), randomScalingFactor()],
                fill: false,
            }, {
                label: "My Second dataset",
                fill: false,
                data: [randomScalingFactor(), randomScalingFactor(), randomScalingFactor(), randomScalingFactor(), randomScalingFactor(), randomScalingFactor(), randomScalingFactor(), randomScalingFactor(), randomScalingFactor(), randomScalingFactor(), randomScalingFactor()],
            }]
        };
        $.each(config.data.datasets, function(i, dataset) {
            dataset.borderColor = randomColor(0.4);
            dataset.backgroundColor = randomColor(0.5);
            dataset.pointBorderColor = randomColor(0.7);
            dataset.pointBackgroundColor = randomColor(0.5);
            dataset.pointBorderWidth = 1;
        });
        // Update the chart
        window.myLine.update();
    });
    $('#addDataset').click(function() {
        var newDataset = {
            label: 'Dataset ' + config.data.datasets.length,
            borderColor: randomColor(0.4),
            backgroundColor: randomColor(0.5),
            pointBorderColor: randomColor(0.7),
            pointBackgroundColor: randomColor(0.5),
            pointBorderWidth: 1,
            data: [],
        };
        for (var index = 0; index < config.data.labels.length; ++index) {
            newDataset.data.push(randomScalingFactor());
        }
        config.data.datasets.push(newDataset);
        window.myLine.update();
    });
    $('#addData').click(function() {
        if (config.data.datasets.length > 0) {
            var month = MONTHS[config.data.labels.length % MONTHS.length];
            config.data.labels.push(month);
            $.each(config.data.datasets, function(i, dataset) {
                dataset.data.push(randomScalingFactor());
            });
            window.myLine.update();
        }
    });
    $('#removeDataset').click(function() {
        config.data.datasets.splice(0, 1);
        window.myLine.update();
    });
    $('#removeData').click(function() {
        config.data.labels.splice(-1, 1); // remove the label first
        config.data.datasets.forEach(function(dataset, datasetIndex) {
            dataset.data.pop();
        });
        window.myLine.update();
    });
});
var x_source;
var y_source;
var source;
$("#source").change(function() {
    x_source = document.getElementById("source").selectedIndex;
    y_source = document.getElementById("source").options;
    source = y_source[x_source].text;
});
var x_type;
var y_type;
var type;
$("#type").change(function() {
    x_type = document.getElementById("type").selectedIndex;
    y_type = document.getElementById("type").options;
    type = y_type[x_type].text;
});
var gridValue;
var x_gridValue;
var y_gridValue;
$("#gridValue").change(function() {
    x_gridValue = document.getElementById("gridValue").selectedIndex;
    y_gridValue = document.getElementById("gridValue").options;
    gridValue = y_gridValue[x_gridValue].text;
});
var polygon_array = [];
var polyline_array = [];
var cityfocus = [];
var regionfocus = [];
var ordinary = [];
//原始数据的触发
$("#finish").change(function() {
    var x = document.getElementById("finish").selectedIndex;
    var y = document.getElementById("finish").options;
    var finish = y[x].text;
    if (finish == "是") {
        creatGrid();
        creatGrid_investment_50();
    } else if (finish == "否") {
        alert("请再次选择需要显示的数据");
    }
});
//插值数据的触发
$("#finish_50").change(function() {
    var x = document.getElementById("finish_50").selectedIndex;
    var y = document.getElementById("finish_50").options;
    var finish = y[x].text;
    if (finish == "是") {
        creatGrid_50();
    } else if (finish == "否") {
        alert("请再次选择需要显示的数据");
    }
});
//清除当前所有的网格
$("#delect").change(function() {
    delect_all_rectangle();
});