<?php
function updateGrandLyonData(){
	$username="";
	$password="";
	$park_url='https://download.data.grandlyon.com/wfs/rdata?SERVICE=WFS&VERSION=2.0.0&outputformat=GEOJSON&maxfeatures=1000&request=GetFeature&typename=pvo_patrimoine_voirie.pvoparkingtr&SRSNAME=urn:ogc:def:crs:EPSG::4171';
	$opts=array(
		'http'=>array(
			'method'=>"GET",
			'header' => "Authorization: Basic " . base64_encode("$username:$password")                 
		)
	);

	$context = stream_context_create($opts);

	// Open the file using the HTTP headers set above



	$parkings=array();

	$ret = json_decode(file_get_contents($park_url, false, $context));
	$connectPdo=true;
	include('pdo.inc.php');
	for($i=0;$i<count($ret->features);$i++){
		$name=$conn->quote($ret->features[$i]->properties->nom);
		$pkgid=$conn->quote($ret->features[$i]->properties->pkgid);
		$lat=$ret->features[$i]->geometry->coordinates[1];
		$lng=$ret->features[$i]->geometry->coordinates[0];
	
		$availableSpot=-1;
		if(preg_match("/^([0-9]+)/",$ret->features[$i]->properties->etat,$matches)){
			$availableSpot=$matches[1];
		}
		$last_update=$conn->quote($ret->features[$i]->properties->last_update);
	
		$req="INSERT IGNORE INTO parking(pkgid,name,lat,lng) VALUES ($pkgid,$name,$lat,$lng)";
		$stmt = $conn->query($req);
	
		$req="INSERT IGNORE INTO parking_state(pkgid,available,last_update) VALUES ($pkgid,$availableSpot,$last_update)";
		$stmt = $conn->query($req);
	}
}

if(isset($grandLyonUpdate)){
	$connectPdo=true;
	include('pdo.inc.php');
	$req="SELECT HOUR(TIMEDIFF(NOW(),ps.last_update)) AS diff FROM parking_state ps WHERE ps.last_update IN (SELECT max(last_update) FROM parking_state)";
	$stmt = $conn->query($req);
	$select = $stmt->fetch();
	if($select['diff']>0){
		updateGrandLyonData();
	}
}




