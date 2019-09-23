package com.workfort.thinkndraw.app.data.local.constant

object Const {
    object QuestionType {
        const val TYPE_A = 1
        const val TYPE_B = 2
        const val TYPE_C = 3
        const val TYPE_D = 4
    }

    object Params {
        const val QUESTION = "question"
    }

    object Classes {
        val ICE_CREAM = Pair(0, "Ice-Cream")
        val SQUARE = Pair(1, "Square")
        val APPLE = Pair(2, "Apple")
        val CAR = Pair(3, "Car")
        val BANANA = Pair(4, "Banana")
    }

    object FcmMessaging {
        const val SERVER_KEY = "key=AAAAN1j5li0:APA91bFQ7Pzm-xlUMOjnK6RGFPuYyxVDB7LRH4xXS3sATqFlJrLJJaRAc7fpF7weStdi3c9fh_tB7zS7jXnyW0Tc7Zy0K-ceA1NbTAE2PyaoN5G-tTcLv6Av8Fg7Z0qSRzWSs5a_r3MH"
        const val CONTENT_TYPE = "application/json; charset=utf-8"
        const val PUSH_URL = "https://fcm.googleapis.com/fcm/"

        object Api {
            const val SEND_PUSH = "send"
        }

        object Topic {
            private const val TOPIC_PREFIX = "/topics/"

            const val GLOBAL = TOPIC_PREFIX + "global"
            const val MULTIPLAYER = TOPIC_PREFIX + "multiplayer"
        }

        object NotificationKey {
            const val TITLE = "title"
            const val BODY = "body" //text
            const val ANDROID_CHANNEL_ID = "android_channel_id"
            const val ICON = "icon"
            const val SOUND = "sound" //Sound file name in /res/raw/ directory
            /*
             * Identifier used to replace existing notifications in the notification drawer.
             * If not specified, each request creates a new notification.
             */
            const val TAG = "tag"
            const val COLOR = "color" //icon color in #rrggbb
            /*
             * If specified, an activity with a matching intent filter
             * is launched when a user clicks on the notification.
             */
            const val CLICK_ACTION = "click_action"
            /*
             * the key to the body string in the app's string resources
             * to use to localize
             */
            const val BODY_LOC_KEY = "body_loc_key"
            /*
             * Variable string values to be used in place of the format specifiers
             * in body_loc_key to use to localize the body text
             */
            const val BODY_LOC_ARGS = "body_loc_args"
            /*
             * the key to the title string in the app's string resources
             * to use to localize
             */
            const val TITLE_LOC_KEY = "title_loc_key"
            /*
             * Variable string values to be used in place of the format specifiers
             * in body_loc_key to use to localize the title text
             */
            const val TITLE_LOC_ARGS = "title_loc_args"
        }

        object DataKey {
            const val ACTION = "action"
            const val TYPE = "type"
            const val SENDER_ID = "sender_id"
            const val SENDER_NAME = "sender_name"
            const val SENDER_IMAGE_URL = "sender_image_url"
            const val SENDER_FCM_TOKEN = "sender_fcm_token"
            const val SENT_AT = "sent_at"
            const val MESSAGE_ID = "message_id"
            const val MESSAGE = "message"
            const val CHALLENGE_ID = "challenge_id"
            const val MATCH = "match"
        }

        object Action {
            object Multiplayer {
                const val INVITE = "com.workfort.thinkndraw.action.MULTIPLAYER.INVITE"
                const val ACCEPT = "com.workfort.thinkndraw.action.MULTIPLAYER.ACCEPT"
            }
        }

        object DataType {
            const val MESSAGE = "message"
        }
    }
}