

var App = function(caller,remoteObjectDef){
    this._caller = caller;

    this._callStack = remoteObjectDef.callStack;
    for (var i=0; i < remoteObjectDef.remoteMethods.length; i++) {
        var methodDef = remoteObjectDef.remoteMethods[i];
        var methodName = methodDef.methodName;
        this["md_"+methodName] = methodDef;
    }
    for(i=0; i< remoteObjectDef.remoteCache.length; i++){
        var methodDef = remoteObjectDef.remoteCache[i].key;
        var value = remoteObjectDef.remoteCache[i].value;
        var methodName = methodDef.methodName;
        this[methodName] = function() { return Promise.resolve(value); }
    }
};

//App.prototype.getVersion = function() {
//
//};
//RCallResult(MethodDefinition theCall,Object value, RemoteObjectDefinition robject, Exception exception)

App.prototype.createLoginToken = function(un,pw){
    var methodCall = new RMethodCall(this.md_createLoginToken,[toJson(un),toJson(pw)]);
    var rcall = new RCall(this._callStack,methodCall);
    return this._caller.call(rcall).then(function(rCallResult){
        if(rCallResult.exception){
            throw exception;
        }
        return rCallResult.value; //fromJson(rCallResult.value)
    });
};

App.prototype.getUserSession = function(loginToken){
    var methodCall = new RMethodCall(this.md_getUserSession,[toJson(loginToken)]);
    var rcall = new RCall(this._callStack,methodCall);
    return this._caller.call(rcall).then(function(rCallResult){
        if(rCallResult.exception){
            throw exception;
        }
        return new UserSession(caller,rCallResult.robject);
    });
};


//UserData    getDetails();
UserSession.prototype.getDetails = function() {
    var methodCall = new RMethodCall(this.md_getDetails,[]);
    var rcall = new RCall(this._callStack,methodCall);
    return this._caller.call(rcall).then(function(rCallResult){
        if(rCallResult.exception){
            throw exception;
        }
        return UserData.fromJson(rCallResult.value);
    });
};
