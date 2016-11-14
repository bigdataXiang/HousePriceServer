//config：表格所需要的数据结构，设置为全局变量
var config = {
	type: 'line',
	data: {
		labels: [
			["10", "2015"], "11", "12", ["1", "2016"], "2", "3", "4", "5"
		],
		datasets: [{
			label: "房天下",
			data: [],
			fill: false
				//borderDash: [5, 5]  设置曲线的虚线间隔，若没有该属性则为实线
		}, {
			//hidden: true, hidden表示该曲线在可视化时被隐藏
			label: '我爱我家',
			data: [],
			fill: false
		}, {
			label: '融合数据',
			data: [],
			fill: false
				//borderDash: [5, 5]  设置曲线的虚线间隔，若没有该属性则为实线
		}]
	},
	options: {
		responsive: true,
		title: {
			display: true,
			text: "房价曲线图"
		},
		tooltips: {
			mode: 'label',
			callbacks: {}
		},
		hover: {
			mode: 'dataset'
		},
		scales: {
			xAxes: [{
				display: true,
				scaleLabel: {
					show: true,
					labelString: 'Month'
				}
			}],
			yAxes: [{
				display: true,
				scaleLabel: {
					show: true,
					labelString: 'Value'
				},
				ticks: {
					suggestedMin: 0,
					suggestedMax: 15,
				}
			}]
		}
	}
};
//对config里的数据进行修改
function dataStruct(parameter, code, max, min) {
	//如果这里不设置为空，则导致后面的网格的曲线和前面的网格曲线一样
	config.data.datasets[0].data = [];
	config.data.datasets[1].data = [];
	config.data.datasets[2].data = [];
	var text = "" + code;
	/*
	在全局的数据结构下已经设置好日期了
	//设置横轴的时间序列
	for(var i=0; i < parameter.data.length; i++){
	    config.data.labels.push(parameter.data[i].date);
	}*/
	//设置房天下数据的dataset
	if (parameter.fang.data.length != 0) {
		for (var i = 0; i < parameter.fang.data.length; i++) {
			var time_price = parameter.fang.data[i];
			var date = time_price.date;
			var average_price = time_price.average_price;
			var j = arrayAssignment(date);
			config.data.datasets[0].data[j] = average_price;
		}
	}
	//设置我爱我家数据的dataset
	if (parameter.woaiwojia.data.length != 0) {
		for (var i = 0; i < parameter.woaiwojia.data.length; i++) {
			var time_price = parameter.woaiwojia.data[i];
			var date = time_price.date;
			var average_price = time_price.average_price;
			var j = arrayAssignment(date);
			config.data.datasets[1].data[j] = average_price;
		}
	}
	//设置融合数据的dataset
	if (parameter.blend.data.length != 0) {
		for (var i = 0; i < parameter.blend.data.length; i++) {
			var time_price = parameter.blend.data[i];
			var date = time_price.date;
			var average_price = time_price.average_price;
			var j = arrayAssignment(date);
			config.data.datasets[2].data[j] = average_price;
		}
	}
	//设置曲线的最大和最小值
	config.options.scales.yAxes[0].ticks.suggestedMin = min;
	config.options.scales.yAxes[0].ticks.suggestedMax = max;
	//展示曲线对应的具体网格值
	config.options.title.text = "房价曲线图（:" + code + "）"; //给曲线随机赋颜色值
	for (var i = 0; i < config.data.datasets.length; i++) {
		/* 这是给每条曲线颜色随意赋值，之后有用
		config.data.datasets[i].borderColor = randomColor(1);
		 //config.data.datasets[i].backgroundColor = randomColor(0.3);
		 config.data.datasets[i].pointBorderColor = randomColor(0.7);
		 config.data.datasets[i].pointBackgroundColor = randomColor(0.5);
		 */
		config.data.datasets[i].pointBorderWidth = 1;
		if (i == 0) {
			config.data.datasets[i].borderColor = mycolor(0, 0, 255);
			config.data.datasets[i].pointBorderColor = mycolor(0, 0, 255);
			config.data.datasets[i].pointBackgroundColor = mycolor(0, 0, 255);
		}
		if (i == 1) {
			config.data.datasets[i].borderColor = mycolor(0, 255, 0);
			config.data.datasets[i].pointBorderColor = mycolor(0, 255, 0);
			config.data.datasets[i].pointBackgroundColor = mycolor(0, 255, 0);
		}
		if (i == 2) {
			config.data.datasets[i].borderColor = mycolor(255, 0, 0);
			config.data.datasets[i].pointBorderColor = mycolor(255, 0, 0);
			config.data.datasets[i].pointBackgroundColor = mycolor(255, 0, 0);
		}
	}
	return config;
}
var rects = [];
var pricedata;

function arrayAssignment(date) {
	var i;
	if (date == "2015-10-01") {
		i = 0;
	} else if (date == "2015-11-01") {
		i = 1;
	} else if (date == "2015-12-01") {
		i = 2;
	} else if (date == "2016-1-01") {
		i = 3;
	} else if (date == "2016-2-01") {
		i = 4;
	} else if (date == "2016-3-01") {
		i = 5;
	} else if (date == "2016-4-01") {
		i = 6;
	} else if (date == "2016-5-01") {
		i = 7;
	}
	return i;
}

function mycolor(r, g, b) {
	var color = "rgb(" + r + "," + g + "," + b + ")";
	return color;
}

function poly_test() {
	latlngs = [];
	var latlng1 = L.latLng(39.906985851984146, 116.39055488416854);
	var latlng2 = L.latLng(39.905779, 116.319274);
	var latlng3 = L.latLng(39.88311876796353, 116.30383565962735);
	var latlng4 = L.latLng(39.872028939237744, 116.36803340806715);
	var latlng6 = L.latLng(39.906985851984146, 116.39055488416854);
	latlngs.push(latlng1);
	latlngs.push(latlng2);
	latlngs.push(latlng3);
	latlngs.push(latlng4);
	latlngs.push(latlng6);
	var poly = new L.Polygon(latlngs, {
		color: mycolor(0, 0, 205),
		fillColor: mycolor(0, 0, 205),
		stroke: true,
		weight: 1
	});
	map.addLayer(poly);
	polygon_array.push(poly);
	var content = "<DIV><span style=\"font-weight:bold;\"></span></DIV><HR/><DIV><span>" + "天安门" + "</span></DIV><BR/><DIV><span  style=\"font-weight:bold;\">位置:	</span> <span>" + "北京天安门" + "</span></DIV><DIV><span  style=\"font-weight:bold;\">类型:	</span><span>" + "文明古迹" + "<span></DIV>";
	var popup = L.popup()
		.setLatLng(latlng1)
		.setContent(content)
		.openOn(map);
	poly.bindPopup(popup);
}
var legend = L.control({
	position: 'bottomright'
});
legend.onAdd = function(map) {
	var div = L.DomUtil.create('div', 'info legend'),
		color = ['#C70305', '#EA4706', '#E97A04', '#E9A708', '#E6CC05', '#E9E507', '#D8EB00', '#B8E705', '#04E738', '#06E884', '#08E9C7', '#03EAE4', '#09BAEC', '#077BEA', '#1411D2'],
		labels = [],
		grades = ["[8,+∞]", "[8,9]", "[7.5,8]", "[7,7.5]", "[6.5,7]", "[6,6.5]", "[5.5,6]", "[5,5.5]", "[4.5,5]", "[4,4.5]", "[3.5,4]", "[3,3.5]", "[2.5,3]", "[2,2.5]", "[0,2]"],
		from, to;
	for (var i = 0; i < color.length; i++) {
		labels.push(
			'<i style=\"background:' + color[i] + '\"></i> ' + grades[i]);
	}
	div.innerHTML = labels.join('<br>');
	return div;
};
legend.addTo(map);
var gradeOptions = {
	weight: 1,
	fillOpacity: 0.7,
	rotation: 0.0,
	position: {
		x: 0,
		y: 0
	},
	offset: 0,
	innerRadius: 0,
};
//})
//增删表格的行数
var id = 1;

function add(text1, text2, table_id) {
	id++;
	var theTable = document.getElementById(table_id); //table的id
	var rowCount = theTable.rows.length; //获得当前表格的行数
	var row = theTable.insertRow(rowCount); //在tale里动态的增加tr
	row.id = id;
	$("#" + id).attr("onmouseover", "this.style.backgroundColor=\'#ffff66\';");
	$("#" + id).attr("onmouseout", "this.style.backgroundColor=\'#C6E2FF\';");
	var cell1 = row.insertCell(0); //在tr中动态的增加td
	var cell2 = row.insertCell(0); //在tr中动态的增加td
	cell1.innerText = text1;
	cell1.style.cssText = "text-align:center"; //tr中内容居中显示
	cell2.innerText = text2;
	cell2.style.cssText = "text-align:center"; //tr中内容居中显示
}

function moveTr(tr_id, table_id) {
	var tb = document.getElementById(table_id); //获取table
	var tr = document.getElementById(tr_id); //根据id获取具体的tr
	tb.deleteRow(tr.rowIndex); //删除行
	unit(); //调用此方法，对界面有些地方的value进行更新
}