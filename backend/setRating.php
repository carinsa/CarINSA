<?php
function conj($n){
	if($n==0){
		return 1;
	}
	elseif($n==1){
		return 0;
	}
	elseif($n==2){
		return 3;
	}
	elseif($n==3){
		return 2;
	}
}
header('Content-type: application/json');

$grandLyonUpdate=true;
include("GrandLyon.php");

$return = array();
if(isset($_GET['u'],$_GET['p'],$_GET['r'])){
	$user=$_GET['u'];
	$pkgid=$_GET['p'];
	$state=$_GET['r'];
	
	if(preg_match("/^[a-zA-Z0-9]+$/",$user) and preg_match("/^[0-9]+$/",$pkgid) and preg_match("/^[0-3]$/",$state)){
		$connectPdo=true;
		include('pdo.inc.php');
		
		
		$req1="SELECT state, COUNT(*) AS num FROM rating WHERE pkgid=$pkgid AND userid='$user' AND date>DATE_SUB(CURDATE(), INTERVAL 12 HOUR) GROUP BY state";
		$stmt1 = $conn->query($req1);
		
		$contrib=array();
		$contrib[0]=false;
		$contrib[1]=false;
		$contrib[2]=false;
		$contrib[3]=false;
		while($select1 = $stmt1->fetch()){
			$c=$select1['state'];
		
			if($c==0){
				$contrib[0]=true;
			}
			elseif($c==1){
				$contrib[1]=true;
			}
			elseif($c==2){
				$contrib[2]=true;
			}
			elseif($c==3){
				$contrib[3]=true;
			}
			
		}
		if($contrib[$state]){
			//nothing
			$req = "DELETE FROM rating WHERE state=$state AND userid='$user' AND pkgid='$pkgid' AND date>DATE_SUB(CURDATE(), INTERVAL 12 HOUR) LIMIT 1";
			$stmt = $conn->query($req);
			$return['erreur']="Avis supprimé";
			$return['code']="12";
		}
		elseif($contrib[conj($state)]){
			//update
			$cstate=conj($state);
			$req = "UPDATE rating SET state=$state, date=NOW() WHERE userid='$user' AND pkgid='$pkgid' AND state=$cstate AND date>DATE_SUB(CURDATE(), INTERVAL 12 HOUR)";
			$stmt = $conn->query($req);
			$return['execution']="Avis mise à jour";
			$return['code']="12";
		}
		else {
			//insert
			$req="INSERT INTO rating(pkgid,userid,state) VALUES ($pkgid,'$user',$state)";
			$stmt = $conn->query($req);
			$return['execution']="Avis enregistré";
			$return['code']="11";
		}
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
