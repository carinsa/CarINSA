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
		
		$req="SELECT spotid, name, AVG(lat) AS lat, AVG(lng) AS lng, (select type from user_spots us2 where us1.spotid = us2.spotid group by type order by count(*) desc limit 1) as type, (select free from user_spots us2 where us1.spotid = us2.spotid group by free order by count(*) desc limit 1) as free, (select availableSpots from user_spots us2 where us1.spotid = us2.spotid group by availableSpots order by count(*) desc limit 1) as availableSpots, COUNT(*) AS nb FROM user_spots us1 GROUP BY spotid;";
		$stmt = $conn->query($req);
		$spots=array();
		while($select = $stmt->fetch()){
			$obj=array();
			$obj['spotid']=$select['spotid'];
			$obj['name']=$select['name'];
			$obj['lat']=$select['lat'];
			$obj['lng']=$select['lng'];
			$obj['type']=$select['type'];
			$obj['free']=boolval($select['free']);
			$obj['availableSpots']=$select['availableSpots'];
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


