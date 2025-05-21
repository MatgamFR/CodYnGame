// Inclure le fichier contenant la fonction somme
const codyngame = require('./codyngame'); // Assurez-vous que codyngame.js contient la fonction somme

const readline = require('readline');

// Lire les entrées depuis la console
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

let inputs = [];
rl.on('line', (line) => {
    inputs.push(parseInt(line.trim()));
        const a = inputs[0];
        const b = inputs[1];

        if (codyngame.somme(a, b) === a + b) {
            console.log("1");
        } else {
            console.log("0");
            console.log(codyngame.somme(a, b));
            console.log(a + b);
            console.log("1");
        }

        rl.close();
    
});