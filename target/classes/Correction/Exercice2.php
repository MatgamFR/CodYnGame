<?php

include 'codyngame.php'; // Inclure le fichier contenant la fonction somme

$a = intval(trim(fgets(STDIN))); // Lire la première entrée
$b = intval(trim(fgets(STDIN))); // Lire la deuxième entrée

if (codyngame_somme($a, $b) == $a + $b) {
    echo "1\n";
} else {
    echo "0\n";
    echo codyngame_somme($a, $b) . "\n";
    echo ($a + $b) . "\n";
    echo "1\n";
}
?>