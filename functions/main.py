import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from firebase_admin import messaging

cred = credentials.Certificate('path/to/serviceAccountKey.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

def send_fcm_message(sender_id, recipient_token, message):
    # Create a FCM message
    fcm_message = messaging.Message(
        token=recipient_token,
        notification=messaging.Notification(
            title=f"New message from {sender_id}",
            body=message,
        )
    )

    # Send the message
    response = messaging.send(fcm_message)
    return response

# Cloud Function to listen to 'notifications' collection
def notification_listener(event, context):
    notification_data = event.to_dict()
    sender_id = notification_data['senderId']
    recipient_token = notification_data['recipientToken']
    message = notification_data['message']

    # Send FCM message
    send_fcm_message(sender_id, recipient_token, message)
