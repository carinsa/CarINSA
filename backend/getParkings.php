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
		
		
		$req="SELECT p.pkgid, p.name, p.lat, p.lng, ps.available, ps.last_update FROM parking p, parking_state ps WHERE p.pkgid=ps.pkgid AND ps.last_update IN (SELECT max(last_update) FROM parking_state WHERE pkgid=p.pkgid)";
		$stmt = $conn->query($req);
		
		$parkings=array();
		while($select = $stmt->fetch()){
			$obj=array();
			$obj['pkgid']=$select['pkgid'];
			$obj['name']=$select['name'];
			$obj['lat']=$select['lat'];
			$obj['lng']=$select['lng'];
			$obj['available']=$select['available'];
			$obj['last_update']=$select['last_update'];
			
			$req1="SELECT state, COUNT(*) AS num FROM rating WHERE pkgid='".$obj['pkgid']."' AND date>DATE_SUB(CURDATE(), INTERVAL 12 HOUR) GROUP BY state";
			$stmt1 = $conn->query($req1);
			
			$parking = array();
			$parking['full']=0;
			$parking['available']=0;
			$parking['close']=0;
			$parking['open']=0;
			while($select1 = $stmt1->fetch()){
				$s=$select1['state'];
				if($s==0){
					$parking['full']=0+$select1['num'];
				}
				elseif($s==1){
					$parking['available']=0+$select1['num'];
				}
				elseif($s==2){
					$parking['close']=0+$select1['num'];
				}
				elseif($s==3){
					$parking['open']=0+$select1['num'];
				}
			}
			
			
			$obj['ratings']=$parking;
			
			
			
			$req1="SELECT state, COUNT(*) AS num FROM rating WHERE pkgid='".$obj['pkgid']."' AND userid='$user' AND date>DATE_SUB(CURDATE(), INTERVAL 12 HOUR) GROUP BY state";
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
			
			$obj['my-ratings']=$contrib;
			$parkings[]=$obj;
		}
		
		
		
		
		$return = array();
		$return['parkings'] = $parkings;
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
