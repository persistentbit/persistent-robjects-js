print("JS got Json " + jsonNameString);
var json = JSON.parse(jsonNameString);

var valueTest = ValueWithGen.fromJson(json,Name.fromJson,function(f){return f;});
assert(valueTest.valueA === new Name("fn","ln"));

