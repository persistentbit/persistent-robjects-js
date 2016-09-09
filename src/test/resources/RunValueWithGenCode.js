print("JS got Json " + jsonNameString);
var json = JSON.parse(jsonNameString);

var valueTest = ValueWithGen.fromJson(json,Name.fromJson,function(f){return f;});
assert(valueTest.valueA.equals(new Name("fn","ln")),"Name not equal");
assert(valueTest.valueA.equals(new Name("fn","wrong")) == false,"Name equal");
assert(valueTest.equals(valueTest),"valuetest is not equal");

var  aVal = new Name("fn","ln");
//ValueWithGen<Name,String> val = new ValueWithGen<>(aVal,new Tuple2<>(aVal,"txt"),new Tuple2<>(1234,new Tuple2<>(aVal,"BVal")),"JustAString");

var valueTest2 = new ValueWithGen(aVal,new Tuple2(aVal,"txt"), new Tuple2(1234,new Tuple2(aVal,"BVal")),"JustAString");
assert(valueTest.equals(valueTest2),"valueTest2 is not equal");
var valueTest2Json = valueTest2.jsonData();
print("------");
print("JSON valuetest2Json = " + JSON.stringify(valueTest2Json));
print("JSON json = " + JSON.stringify(json));
assert(JSON.stringify(valueTest2Json) === JSON.stringify(json),"json is not equal");
var me = new Name("Peter","Muys");
var valueTest3 = valueTest2.withValueA(me);
assert(valueTest2.equals(valueTest3) === false, "with changed name is eqeal");
assert(valueTest3.valueA.equals(me),"i am not equal");
