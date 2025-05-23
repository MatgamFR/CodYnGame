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
    if (inputs.length >= 1) { 
        const a = inputs[0]
        let tab = [];
        let x = 0, y = 1;
    
        while (x <= a) {
            tab.push(x); 
            [x, y] = [y, x + y]; 
        }
        if (codyngame.fibonacci(a) == tab ){
            console.log("1");
            process.exit(0);
        } else {
            console.log("0");
            console.log(codyngame.fibonacci(a));
            console.log(tab);
            console.log("1");
            process.exit(0);
        }

        rl.close();
    }
});