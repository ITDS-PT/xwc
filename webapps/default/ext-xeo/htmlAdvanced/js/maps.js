function createMarker(point,iTitle,html){
	var marker = new GMarker(point, {title: iTitle});
	GEvent.addListener(marker, "click", function() {
		marker.openInfoWindowHtml('<span style="font-family:arial;font-size:12pt;font-weight:bold;">'+html+'</span>');
	});
	return marker;
}
function initializeMap(mapdivId,iLat,iLong,iMapDesc){
	var map=new GMap2(document.getElementById(mapdivId)); 
	map.setCenter(new GLatLng(iLat,iLong),4);
	map.addMapType(G_PHYSICAL_MAP);
	map.addMapType(G_SATELLITE_3D_MAP);
	overviewmap = new GOverviewMapControl();
	map.addControl(overviewmap, new GControlPosition(G_ANCHOR_BOTTOM_RIGHT));
	map.addControl(new GLargeMapControl());
	map.addControl(new GMapTypeControl());
	map.setMapType(G_NORMAL_MAP);	
	var point = new GLatLng(iLat, iLong);
	map.addOverlay(createMarker(point, iMapDesc, iMapDesc));
}