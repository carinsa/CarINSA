<?php
header('Content-type: application/json');

$grandLyonUpdate=true;
include("GrandLyon.php");

$return = array();
if(isset($_GET['u'],$_GET['lat'],$_GET['lng'],$_GET['type'],$_GET['free'],$_GET['available'])){
	$user=$_GET['u'];
	$lat=$_GET['lat'];
	$lng=$_GET['lng'];
	$type=$_GET['type'];
	$free=$_GET['free'];
	$available=$_GET['available'];
	if(preg_match("/^[a-zA-Z0-9]+$/",$user) and preg_match("/^[0-9]+\.[0-9]+$/",$lat) and preg_match("/^[0-9]+\.[0-9]+$/",$lng) and preg_match("/^[0-3]$/",$type) and preg_match("/^[0-1]$/",$free) and preg_match("/^[0-9]+$/",$available)){
		$opts = array('http'=>array('header'=>"User-Agent: CarINSA v1.0b\r\n"));
		$context = stream_context_create($opts);
		$ret=json_decode(file_get_contents("https://nominatim.openstreetmap.org/reverse.php?format=jsonv2&lat=$lat&lon=$lng&zoom=16",false,$context));
		$name=$conn->quote($ret->name);
		$spotid=$conn->quote(md5($ret->display_name));
		
		$connectPdo=true;
		include('pdo.inc.php');
		
		
		$req="INSERT IGNORE INTO user_spots(userid,name,spotid,lat,lng,type,free,availableSpots) VALUES ('$user',$name,$spotid,$lat,$lng,$type,$free,$available)";
		$stmt = $conn->query($req);
		$return['execution']="Spot ajouté";
		$return['code']="11";
	}	
	else {
		$return['erreur']="Requete invalide";
		$return['code']="22";
	}
}
else {
	$return['erreur']="Requete invalide";
	$return['code']="21";
}
echo json_encode($return);


