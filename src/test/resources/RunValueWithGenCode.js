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

var vc = new ValueWithCollections([1,2,3,4],[new Tuple2('A','String'), new Tuple2('A-v2','String-v2')]);
print(vc.json());
var vc2 = ValueWithCollections.fromJson(JSON.parse('{"listInt":[1,2,3,4],"listAString":[{"_1":"A","_2":"String"},{"_1":"A-v2","_2":"String-v2"}]}'),function(a){ return a;});;
print(vc2.json());
assert(vc.equals(vc2));
var vc3 = vc.withListInt([1,2,3,4,5]);
assert(vc.equals(vc3) === false);
var vc4 = vc.withListInt([1,2,3,0]);
assert(vc4.equals(vc) === false);
print(vc4.json());
assert(vc.listAString[0].equals(new Tuple2('A','String')));
assert(vc.listAString[1].equals(new Tuple2('A-v2','String-v2')));
assert(RObjectsUtils.objectsEqual(vc.listAString,vc2.listAString));
assert(RObjectsUtils.objectsEqual(vc.listInt,vc3.listInt) === false);