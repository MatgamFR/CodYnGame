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
        var c = 1
        var comp = true
        if (a <= 1){
            comp = false
        } else {
            for(i=2;i<a;i++){
                c=a%i
                if(c===0){
                    comp = false
                }
            }
        }
        if (codyngame.premier(a) === comp) {
            console.log("1");
            process.exit(0);
        } else {
            console.log("0");
            console.log(codyngame.premier(a),a);
            console.log(comp,a);
            console.log("1");
            process.exit(0);
        }

        rl.close();
    }
});