package com.androidsx.announcement.model;

/**
 * Created by Androidsx on 18/12/15.
 *
 * JSON Structure
 {
 "version": "1.0",
 "pushes": [{
 "id": "push id",
 "enabled": true,
 "cancellable": true,
 "title": {
    "en": "Push title",
    "es": "Título del push"
 },
 "content": {
    "en": "Push content",
    "es": "Contenido del push"
 },
 "config": {
    "expanded": false,
    "large_icon_url": "empty -> default app icon; url -> override the default app icon",
    "content_image_url": "empty -> only show content text; url -> override the push content by this url image only if expanded mode",
    "open_url": "empty -> default open the app; url -> override the default opening by an url, normally for open the playstore",
    "non_installed_app_package": "empty -> for all users; package -> only for users that has not installed this package",
    "from_date": 1234567890,
    "to_date": 1234567890,
    "days_of_week": [1, 2, 3, 4, 5, 6, 7],
    "hour_of_day": 19,
    "minAppVersionCode": 0,
    "maxAppVersionCode": 9999
 }
 }],
 "dialogs": [{
 "id": "dialog id",
 "enabled": true,
 "cancellable": true,
 "title": {
    "en": "Dialog title",
    "es": "Título del diálogo"
 },
 "content": {
    "en": "Dialog content",
    "es": "Contenido del diálogo"
 },
 "config": {
    "content_image_url": "empty -> default app image; url -> override the default app image",
    "open_url": "empty -> default open the app; url -> override the default opening by an url, normally for open the playstore",
    "non_installed_app_package": "empty -> for all; package -> only for users that has not installed this package",
    "max_num_shows": "0 -> no limit; >0 the maximum number of times that the dialog will be show",
    "show_every_x_openings": "0 -> allways; >0 every number of openings",
    "stop_showing_after_ok": true,
    "from_date": 1234567890,
    "to_date": 1234567890,
    "days_of_week": [1, 2, 3, 4, 5, 6, 7],
    "minAppVersionCode": 0,
    "maxAppVersionCode": 9999
 }
 }]
 }
 */
public class AnnouncementParams {
    public static final String VERSION = "version";
    public static final String PUSHES = "pushes";
    public static final String DIALOGS = "dialogs";

    public class Data {
        public static final String ID = "id";
        public static final String ENABLED = "enabled";
        public static final String CANCELLABLE = "cancellable";
        public static final String TITLE = "title";
        public static final String CONTENT = "content";
        public static final String CONFIG = "config";
        public static final String CONTENT_IMAGE_URL = "content_image_url";
        public static final String OPEN_URL = "open_url";
        public static final String NON_INSTALLED_APP_PACKAGE = "non_installed_app_package";
        public static final String FROM_DATE = "from_date";
        public static final String TO_DATE = "to_date";
        public static final String DAYS_OF_WEEK = "days_of_week";
        public static final String MIN_APP_VERSION_CODE = "minAppVersionCode";
        public static final String MAX_APP_VERSION_CODE = "maxAppVersionCode";

        public class Push {
            public static final String EXPANDED = "expanded";
            public static final String LARGE_ICON_URL = "large_icon_url";
            public static final String HOUR_OF_DAY = "hour_of_day";
        }

        public class Dialog {
            public static final String MAX_NUM_SHOWS = "max_num_shows";
            public static final String SHOW_EVERY_X_OPENINGS = "show_every_x_openings";
            public static final String STOP_SHOWING_AFTER_OK = "stop_showing_after_ok";
        }
    }
}
