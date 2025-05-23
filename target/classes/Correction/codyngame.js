function fibonacci(n) {
    let talm = [];
    let x = 0, y = 0;
    
    while (x <= n) {
        talm.push(x); 
        [x, y] = [y, x + y]; 
    }
    
    return talm; 
}
module.exports = { fibonacci };

