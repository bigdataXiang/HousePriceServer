//把map定义成全局变量
var map;
var resize = function() {
	var $map = $('#map');
	$map.height($(window).height() - $('div.navbar').outerHeight());
	$(".sidebar").css("top", $('div.navbar').outerHeight()+2)
	if (map) {
		map.invalidateSize();
	}
};
$(window).on('resize', function() {
	resize();
});
resize();
var normalm = L.tileLayer.chinaProvider('GaoDe.Normal.Map', {
	maxZoom: 18,
	minZoom: 8
});
var imgm = L.tileLayer.chinaProvider('GaoDe.Satellite.Map', {
	maxZoom: 18,
	minZoom: 8
});
var imga = L.tileLayer.chinaProvider('GaoDe.Satellite.Annotion', {
	maxZoom: 18,
	minZoom: 8
});
var normal = L.layerGroup([normalm])
image = L.layerGroup([imgm, imga]);
var baseLayers = {
	"地图": normal,
	"影像": image,
};
var overlayLayers = {};
map = L.map("map", {
	zoomControl: true,
	center: [39.906985851984146, 116.39055488416854],
	zoom: 10
});
normal.addTo(map);
var layerControl = new L.Control.Layers(baseLayers);
layerControl.addTo(map);
var sidebar = L.control.sidebar('sidebar', {
	position: 'right'
}).addTo(map);