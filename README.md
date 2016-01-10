# simple-announcement

How to use
==========

The simplest integration:

Manifest:<br />
Change the receiver class by your own, extending [AnnouncementReceiver.java](announcement/src/main/java/com/androidsx/announcement/service/AnnouncementReceiver.java), if you want to track or make other things when announcements events occurs.
```java
<receiver android:name="com.androidsx.announcement.service.SimpleAnnouncementReceiver">
    <intent-filter>
        <action android:name="com.androidsx.announcement.event" />
    </intent-filter>
</receiver>
```
Note: Take care to call `super` into the methods `onPushOpen` and `onDialogOpen` of your own Receiver class.
<br />
See the basic receiver implementation:<br />
[SimpleAnnouncementReceiver.java](announcement/src/main/java/com/androidsx/announcement/service/SimpleAnnouncementReceiver.java)

Application:
```java
AnnouncementManager.init(this, iconResId, appNumUses, JSON URL);
```

Activity:<br />
Where you want to show a dialog.
```java
AnnouncementManager.with(this).fetch().launchDialogAnnouncementIfApply(this, getFragmentManager());
```
<br />
Note: The pushes will be launched by the internal service called by the scheduled alarm at the hour inidicated in the push parameter.

License
=======

Licensed under the MIT License. See the [LICENSE.md](LICENSE.md) file for more details.
