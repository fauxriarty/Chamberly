from flask import Flask, request, jsonify
import firebase_admin
from firebase_admin import credentials, firestore, messaging

app = Flask(__name__)

# Initialize Firebase Admin SDK
cred = credentials.ApplicationDefault()
firebase_admin.initialize_app(cred)

@app.route('/sendNotification', methods=['POST'])
def send_notification():
    try:
        data = request.json

        # Extract data
        sender_id = data['senderId']
        recipient_token = data['recipientToken']
        message_text = data['message']

        # Create and send FCM message
        fcm_message = messaging.Message(
            token=recipient_token,
            notification=messaging.Notification(
                title=f"New message from {sender_id}",
                body=message_text,
            )
        )
        response = messaging.send(fcm_message)

        return jsonify({"success": True, "message": "Notification sent", "response": response}), 200
    except Exception as e:
        return jsonify({"success": False, "message": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)
