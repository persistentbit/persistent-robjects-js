/**
 * Created by petermuys on 3/09/16.
 */


var Name = function(firstName, lastName){
    this._data = {
        _firstName : firstName,
        _lastName : lastName
    };
};
Name.fromJson = function(json){
    return new Name(json.firstName,json.lastName);
};

Name.prototype.getFirstName = function () {
    return this._data._firstName;
};
Name.prototype.getLastName = function(){
    return this._data._lastName;
};
Name.prototype.withFirstName = function (value){
    return new Name(value,this._data._lastName);
};
Name.prototype.withLastName = function(value){
    return new Name(this._data._firstName,value);
};
Name.prototype.json = function() {
    return {
        firstName : this._data.firstName,
        lastName : this._data.lastName
    }
};



var Person = function(id,name){
    this._data = {
        _id : id,
        _name : name
    };
};

Person.fromJson = function(json){
    return new Person(json.id,Name.fromJson(json.name));
}


Person.prototype.withName = function(name){
    return new Person(this._data._id,name);
};
Person.prototype.withId = function(id){
    return new Person(id,this._data._name);
};
Person.prototype.json = function() {
    return {
        id : this._data._id,
        name : this._data._name.json()
    };
};


var Tuple2 = function(_1,_2,toJson_1,toJson_2){
    this._data = {
        __1 : _1,
        __2 : _2,
        _toJson_1 : toJson_1,
        _toJson_2 : toJson_2
    };
};


Tuple2.fromJson = function(json,fromJson_1,fromJson_2){
    new Tuple2(fromJson(json._1),fromJson(json._2))
}

Tuple2.prototype.get_1 = function() {
    return this._data.__1;
}

Tuple2.prototype.get_2 = function() {
    return this._data.__2;
}


Tuple2.prototype.json = function() {
    return {
        _1 : this._data._toJson_1(this._data.__1),
        _2 : this._data._toJson_2(this._data.__2)
    };
};

Tuple2.prototype.with_1 = function (new_1) {
    return new Tuple2(new_1,this._data.__2);
};

Tuple2.prototype.with_2 = function(new_2){
    return new Tuple2(this._data.__1,new_2);
};

GenericsTest.fromJson = function(json,fromJsonGT){
    return new GenericsTest(
        fromJsonGT(json.gtValue),
        Tuple2.fromJson(
            json.gtValue,
            fromJsonGT,
            function(a){ return a;}
        )
    );
}