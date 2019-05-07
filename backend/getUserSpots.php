<?php
header('Content-type: application/json');

$grandLyonUpdate=true;
include("GrandLyon.php");

$return = array();
if(isset($_GET['u'])){
	$user=$_GET['u'];
	if(preg_match("/^[a-zA-Z0-9]+$/",$user)){
		$connectPdo=true;
		include('pdo.inc.php');
		
		$req="SELECT spotid, name, AVG(lat) AS lat, AVG(lng) AS lng, COUNT(*) AS nb FROM user_spots GROUP BY spotid";
		$stmt = $conn->query($req);
		$spots=array();
		while($select = $stmt->fetch()){
			$obj=array();
			$obj['spotid']=$select['spotid'];
			$obj['name']=$select['name'];
			$obj['lat']=$select['lat'];
			$obj['lng']=$select['lng'];
			$obj['nb']=$select['nb'];
			$spots[]=$obj;
		}
		$return['spots'] = $spots;
		$return['code']="10";
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


