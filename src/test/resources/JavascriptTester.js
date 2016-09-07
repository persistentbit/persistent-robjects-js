//var Platform = Java.type("be.schaubroeck.bevolking.JsTest");
//var Timer    = Java.type("java.util.Timer");

var rt= runtime;

function setInterval(func, milliseconds) {

    //print("setInterval " + func + ", " + milliseconds);
    // New timer, run as daemon so the application can quit
    var timer = rt.createTimer("setInterval");//new Timer("setInterval", true);
    timer.schedule(function() {rt.runLater(func)}, milliseconds, milliseconds);
    return timer;
}

function clearInterval(timer) {
    //print("clearInterval " + timer);
    //if(timer) { timer.cancel(); }
    rt.cancelTimer(timer);
}

function setTimeout(func, milliseconds) {

    //print("setTimeout " + func + ", " + milliseconds);
    // New timer, run as daemon so the application can quit
    var timer = rt.createTimer("setTimeout");
    timer.schedule(function() {rt.runLater(func)}, milliseconds);
    return timer;
}

function clearTimeout(timer) {

    //print("clearTimeout " + timer);
    rt.cancelTimer(timer);
}

function assert(value,message){
    rt.Assert(value,message);
}



