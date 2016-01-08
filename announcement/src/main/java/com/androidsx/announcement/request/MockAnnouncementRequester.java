package com.androidsx.announcement.request;

/**
 * Created by Androidsx on 19/12/15.
 */
public class MockAnnouncementRequester implements AnnouncementRequester {
    static volatile MockAnnouncementRequester singleton;

    public static MockAnnouncementRequester getInstance() {
        if (singleton == null) {
            synchronized (MockAnnouncementRequester.class) {
                if (singleton == null) {
                    singleton = new MockAnnouncementRequester();
                }
            }
        }
        return singleton;
    }

    @Override
    public String execute(boolean forceCache, boolean forceRequest) {
        return "     {\n" +
                "     \t\"version\": \"1.0\",\n" +
                "     \t\"pushes\": [{\n" +
                "     \t\t\"id\": \"Fake push\",\n" +
                "     \t\t\"enabled\": true,\n" +
                "     \t\t\"cancellable\": true,\n" +
                "     \t\t\"title\": {\n" +
                "     \t\t\t\"en\": \"Fake title\",\n" +
                "     \t\t\t\"es\": \"Título falso\"\n" +
                "     \t\t},\n" +
                "     \t\t\"content\": {\n" +
                "     \t\t\t\"en\": \"Fake content\",\n" +
                "     \t\t\t\"es\": \"Contenido falso\"\n" +
                "     \t\t},\n" +
                "     \t\t\"config\": {\n" +
                "     \t\t\t\"expanded\": false,\n" +
                "     \t\t\t\"large_icon_url\": \"http://www.techtronic.us/wp-content/uploads/2012/11/Notifications.png\",\n" +
                "     \t\t\t\"content_image_url\": \"\",\n" +
                "     \t\t\t\"open_url\": \"\",\n" +
                "     \t\t\t\"non_installed_app_package\": \"\",\n" +
                "     \t\t\t\"from_date\": \"\",\n" +
                "     \t\t\t\"to_date\": \"\",\n" +
                "     \t\t\t\"days_of_week\": [1, 2, 3, 4, 5, 6, 7],\n" +
                "     \t\t\t\"hour_of_day\": 20,\n" +
                "     \t\t\t\"minAppVersionCode\": 0,\n" +
                "     \t\t\t\"maxAppVersionCode\": 9999\n" +
                "     \t\t}\n" +
                "     \t}],\n" +
                "     \t\"dialogs\": [{\n" +
                "     \t\t\"id\": \"Fake dialog\",\n" +
                "     \t\t\"enabled\": true,\n" +
                "     \t\t\"cancellable\": true,\n" +
                "     \t\t\"title\": {\n" +
                "     \t\t\t\"en\": \"Dialog title\",\n" +
                "     \t\t\t\"es\": \"Título del diálogo\"\n" +
                "     \t\t},\n" +
                "     \t\t\"content\": {\n" +
                "     \t\t\t\"en\": \"Dialog content\",\n" +
                "     \t\t\t\"es\": \"Contenido del diálogo\"\n" +
                "     \t\t},\n" +
                "     \t\t\"config\": {\n" +
                "     \t\t\t\"content_image_url\": \"http://www.techtronic.us/wp-content/uploads/2012/11/Notifications.png\",\n" +
                "     \t\t\t\"open_url\": \"\",\n" +
                "     \t\t\t\"non_installed_app_package\": \"\",\n" +
                "     \t\t\t\"max_num_shows\": \"5\",\n" +
                "     \t\t\t\"show_every_x_openings\": \"2\",\n" +
                "     \t\t\t\"stop_showing_after_ok\": false,\n" +
                "     \t\t\t\"from_date\": \"\",\n" +
                "     \t\t\t\"to_date\": \"\",\n" +
                "     \t\t\t\"days_of_week\": [1, 2, 3, 4, 5, 6, 7],\n" +
                "     \t\t\t\"minAppVersionCode\": 0,\n" +
                "     \t\t\t\"maxAppVersionCode\": 9999\n" +
                "     \t\t}\n" +
                "     \t}]\n" +
                "     }";
    }
}
