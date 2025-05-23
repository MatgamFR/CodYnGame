<?php

include 'codyngame.php'; // Inclure le fichier contenant la fonction somme

$a = intval(trim(fgets(STDIN))); // Lire la première entrée
$f = 1;

if($a<0){
    $f=0;
} else {
    $f=1;
    for($i=1;$i<=$a;$i++){
        $f *= $i;
    }
}       
if (factorielle($a) == $comp) {
    echo "1\n";
} else {
    echo "0\n";
    echo factorielle($a)." " . $a . "\n";
    echo ($comp)." " . $a . "\n";
    echo "1\n";
}
?>