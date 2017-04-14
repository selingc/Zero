var admin =  require('firebase-admin');
var json = require('/' + process.argv[2]);

var serviceAccount = require("./zero-service-account.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://zero-5e2ff.firebaseio.com"
});

admin.database().ref("test").child("testing").set(true);

for(var i=0; i<json.length; i++){
	var key = admin.database().ref("alerts").push().key;
	admin.database().ref("alerts").child(key).set(json[i]);
}