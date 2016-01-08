# simple-announcement

How to use
==========

The simplest integration:

Manifest:<br />
Change the receiver class by your own, if you want to track or make other things when announcements events occurs.
```java
<receiver android:name="com.androidsx.announcement.service.SimpleAnnouncementReceiver">
    <intent-filter>
        <action android:name="com.androidsx.announcement.event" />
    </intent-filter>
</receiver>
```

Application:
```java
AnnouncementManager.init(this, icon, appNumUses, JSON URL);
```

Activity:<br />
Where you want to show a dialog.
```java
AnnouncementManager.with(this).fetch().launchDialogAnnouncementIfApply(this, getFragmentManager());
```

License
=======

Licensed under the MIT License. See the [LICENSE.md](LICENSE.md) file for more details.
