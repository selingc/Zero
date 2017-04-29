var functions = require('firebase-functions');

// // Start writing Firebase Functions
// // https://firebase.google.com/preview/functions/write-firebase-functions
// 
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// })
var admin = require('firebase-admin');
var request = require('request');

var API_KEY = "AAAAMyiwRVg:APA91bF0hCjJgtiBzw0CRyte0gJ3lEUJPYYGAfBnvluPFIThMNF5VoV2Tdz7tvDSYM20v9J0u9Arw7tLZLldScIgJvAtbozIk1CDYXfT_tbqpHig1B9nziXmr8b7WHZeA6XxKG3HsrS3"; // Your Firebase Cloud Messaging Server API key

// Fetch the service account key JSON file contents
var serviceAccount = require("./scripts/zero-sevice-account.json");

// Initialize the app with a service account, granting admin privileges
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://zero-5e2ff.firebaseio.com"
});
admin.initializeApp(functions.config().firebase);

ref = admin.database().ref();

function listenForNotificationRequests() {
	var requests = ref.child('notificationRequests');
	requests.on('child_added', function(requestSnapshot) {
		var request = requestSnapshot.val();
		sendNotificationToTopic(
			request.location, 
			request.message,
			function() {
				requestSnapshot.ref.remove();
			}
		);
	}, function(error) {
		console.error(error);
	});
};

function sendNotificationToTopic(location, message, onSuccess) {
	request({
		url: 'https://fcm.googleapis.com/fcm/send',
		method: 'POST',
		headers: {
			'Content-Type' :' application/json',
			'Authorization': 'key='+API_KEY
	},
	body: JSON.stringify({
		notification: {
			title: message
		},
		to : '/topics/'+location
	})
	}, function(error, response, body) {
		if (error) { console.error(error); }
		else if (response.statusCode >= 400) { 
			console.error('HTTP Error: '+response.statusCode+' - '+response.statusMessage); 
		}
		else {
		 	onSuccess();
		}
	});
}

// start listening
listenForNotificationRequests();