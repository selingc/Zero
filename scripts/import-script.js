var admin =  require('firebase-admin');
var json = require('./'+process.argv[3]);

var serviceAccount = require("./zero-service-account.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://zero-5e2ff.firebaseio.com"
});

var dbRef = admin.database().ref(process.argv[2]);

for(var i = 0; i < json.length; i++){
	var key = dbRef.push().key;
	dbRef.child(key).set(json[i]);
}