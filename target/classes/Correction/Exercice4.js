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
        var c = 0
        var comp = true
        if (a <= 0){
            comp = false
        } else {
            for(i=1;i<a;i++){
                if(a%i===0){
                    c+=i
                }
            }
            if(c!=a){
                comp=false
            }
        }
        if (codyngame.parfait(a) === comp) {
            console.log("1");
            process.exit(0);
        } else {
            console.log("0");
            console.log(codyngame.parfait(a),a);
            console.log(comp,a);
            console.log("1");
            process.exit(0);
        }

        rl.close();
    }
});