# To get error listener by reflection the field must be preserved.
-keepclassmembers class * extends com.android.volley.Request {
    private ** mErrorListener;
}