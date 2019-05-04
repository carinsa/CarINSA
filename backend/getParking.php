<?php
header('Content-type: application/json');

$grandLyonUpdate=true;
include("GrandLyon.php");

$return = array();
if(isset($_GET['u'],$_GET['p'])){
	$user=$_GET['u'];
	$pkgid=$_GET['p'];
	
	if(preg_match("/^[a-zA-Z0-9]+$/",$user) and preg_match("/^[0-9]+$/",$pkgid)){
		$connectPdo=true;
		include('pdo.inc.php');
		
		
		$req="SELECT p.pkgid, p.name, p.lat, p.lng, ps.available, ps.last_update FROM parking p, parking_state ps WHERE p.pkgid=ps.pkgid AND ps.pkgid=$pkgid AND ps.last_update IN (SELECT max(last_update) FROM parking_state WHERE pkgid=p.pkgid)";
		$stmt = $conn->query($req);
		
		
		$select = $stmt->fetch();
		$parking=array();
		$parking['pkgid']=$select['pkgid'];
		$parking['name']=$select['name'];
		$parking['lat']=$select['lat'];
		$parking['lng']=$select['lng'];
		$parking['available']=$select['available'];
		$parking['last_update']=$select['last_update'];
			
		$req1="SELECT state, COUNT(*) AS num FROM rating WHERE pkgid=$pkgid AND date>DATE_SUB(CURDATE(), INTERVAL 12 HOUR) GROUP BY state";
		$stmt1 = $conn->query($req1);
			
		$contribs = array();
		$contribs['full']=0;
		$contribs['available']=0;
		$contribs['close']=0;
		$contribs['open']=0;
		while($select1 = $stmt1->fetch()){
			$s=$select1['state'];
			if($s==0){
				$contribs['full']=0+$select1['num'];
			}
			elseif($s==1){
				$contribs['available']=0+$select1['num'];
			}
			elseif($s==2){
				$contribs['close']=0+$select1['num'];
			}
			elseif($s==3){
				$contribs['open']=0+$select1['num'];
			}
		}
			
			
		$parking['ratings']=$contribs;
			
		$req1="SELECT state, COUNT(*) AS num FROM rating WHERE pkgid=$pkgid AND userid='$user' AND date>DATE_SUB(CURDATE(), INTERVAL 12 HOUR) GROUP BY state";
		$stmt1 = $conn->query($req1);
		
		$contrib=array();
		$contrib['full']=false;
		$contrib['available']=false;
		$contrib['close']=false;
		$contrib['open']=false;
		while($select1 = $stmt1->fetch()){
			$c=$select1['state'];
		
			if($c==0){
				$contrib['full']=true;
			}
			elseif($c==1){
				$contrib['available']=true;
			}
			elseif($c==2){
				$contrib['close']=true;
			}
			elseif($c==3){
				$contrib['open']=true;
			}
			
		}
			
		$parking['my-ratings']=$contrib;
		
		$return = array();
		$return['parking'] = $parking;
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
