<?php

include 'codyngame.php'; // Inclure le fichier contenant la fonction somme

$a = intval(trim(fgets(STDIN))); // Lire la première entrée
$c = 0;
$comp=true;

if($a<=0){
    $comp=false;
} else {
    for($i=1;$i<$a;$i++){
        if($a%$i==0){
            $c+=$i;
        }
    }
    if($c!=$a){
        $comp=false;
    }
}       
if (parfait($a) == $comp) {
    echo "1\n";
} else {
    echo "0\n";
    echo parfait($a)." " . $a . "\n";
    echo ($comp)." " . $a . "\n";
    echo "1\n";
}
?>