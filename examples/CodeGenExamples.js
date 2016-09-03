/**
 * Created by petermuys on 3/09/16.
 */


var Tuple2 = function(jsonData,genT1,genT2){
    this._jsonData = data;
    this._genT1 = genT1;
    this._genT2 = genT2;
}

var createTuple2 = function(__1,__2,_genT1,_genT2){
    var json = {
        _1: __1.getJSON(),
        _2: __2.getJSON()
    }
    return new Tuple2(json,_genT1,_genT2);
}

Tuple2.prototype.get_1 = function() {
    return genT1(this._jsonData._1);
}

Tuple2.prototype.get_2 = function() {
    return genT2(this._jsonData._2);
}


Tuple2.prototype.getJSON = function() {
    return JSON.parse(JSON.stringify(this._jsonData)); //This makes a deep copy...
    /*return {
        _1: this._jsonData._1,
        _2: this._jsonData._2
    };*/
}

Tuple2.prototype.with_1 = function (new_1) {
    var _newData = this.getJSON();
    _newData._1 = new_1.getJSON();
    return new Tuple2(_newData,this._genT1,this._genT2);
}

Tuple2.prototype.with_2 = function(new_2){
    var _newData = this.getJSON();
    _newData._2 = new_2.getJSON();
    return new Tuple2(_newData,this._genT1,this.get_2());
}