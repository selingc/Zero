const functions = require('firebase-functions');
const admin = require('firebase-admin');
var request = require('request');
var API_KEY = "AAAAMyiwRVg:APA91bF0hCjJgtiBzw0CRyte0gJ3lEUJPYYGAfBnvluPFIThMNF5VoV2Tdz7tvDSYM20v9J0u9Arw7tLZLldScIgJvAtbozIk1CDYXfT_tbqpHig1B9nziXmr8b7WHZeA6XxKG3HsrS3"; // Your Firebase Cloud Messaging Server API key
require('@google-cloud/debug-agent').start({ allowExpressions: true });

admin.initializeApp(functions.config().firebase);

exports.sendNotificationToLocation = functions.database.ref("notificationRequests/{notificationID}").onWrite(event =>{
	if(event.data.val()){
		var location = event.data.val().location;
		var message = event.data.val().message;
		// console.log(location);
		// console.log(message);
		// request({
		// 	url: 'https://fcm.googleapis.com/fcm/send',
		// 	method: 'POST',
		// 	headers: {
		// 		'Content-Type' :' application/json',
		// 		'Authorization': 'key='+API_KEY
		// },
		// body: JSON.stringify({
		// 	notification: {
		// 		title: message
		// 	},
		// 	to : '/topics/'+location
		// })
		// }, function(error, response, body) {
		// 	if (error) { console.error(error); }
		// 	else if (response.statusCode >= 400) { 
		// 		console.error('HTTP Error: '+response.statusCode+' - '+response.statusMessage); 
		// 	}
		// 	else {
				
		// 	}
		// });

		const payload = {
			notification: {
				title: message
			}
		};

		admin.messaging().sendToTopic(location, payload)
			.then(function(response) {
				// See the MessagingDeviceGroupResponse reference documentation for
				// the contents of response.
				console.log("Successfully sent message:", response);
				event.data.ref.remove();
			})
			.catch(function(error) {
				console.log(error);
			});
		}
});