<?php

include 'codyngame.php'; // Inclure le fichier contenant la fonction somme

$a = intval(trim(fgets(STDIN))); // Lire la première entrée
$c = 1;
$comp=true;

if($a<=1){
    $comp=false;
} else {
    for($i=2;$i<$a;$i++){
        $c=$a%$i;
        if($c==0){
            $comp=false;
        }
    }
}       
if (premier($a) == $comp) {
    echo "1\n";
} else {
    echo "0\n";
    echo premier($a)." " . $a . "\n";
    echo ($comp)." " . $a . "\n";
    echo "1\n";
}
?>