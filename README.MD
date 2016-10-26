# ChipsLayoutManager <img src="https://www.cleveroad.com/public/comercial/label-android.svg" height="19">
![Header image](/images/header.png)

## This is ChipsLayoutManager - custom Recycler View's LayoutManager which move item to the next line when no space left on the current 
This layout manager could represent [Google Chips material design pattern](https://material.google.com/components/chips.html#)

As an android developer i see that google isn't so polite to support all our wishes, and when i faced with such common task like create spannable list
but with support of CRUD animations i decided to make such stuff and share it.

<p align="center">
    <img src="/images/demo.gif" alt="Span layout manager demo">
</p>

## Setup and usage
### Installation

```GRADLE 
compile 'com.beloo.widget:ChipsLayoutManager:0.0.1-beta'
```

Assuming you have installed jCenter provider:
```GRADLE
allprojects {
    repositories {
        jcenter()
    }
}
```

or just import `ChipsLayoutManager` module to your project.

### Features
Layout manager represents chips or spanned or "bubble" structure of list items and is a good replace for typical implementations of such issue based on spans in TextView.

* supports `RecyclerView`'s features, such as default animations while CRUD items & custom `ItemAnimator`s, `ItemDecorator`s etc. `wrap_content` of height is supported
* vertical scrolling support. Horizontal scrolling are NOT supported. It is possible to disable vertical scrolling as well. Supported padding/margin, clipToPadding parameter of `RecyclerView`
* supports views with different height and width
* recycling items support (another advantage over `TextView` approach)
* items gravity inside row support. For all items or only for required ones
* LTR and RTL support
* activity orientation change support

<p align="center">
    <img src="/images/animations.gif" alt="Span layout manager animations">
</p>

### Usage

```JAVA 
ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder()
        //set vertical gravity for all items in a row. Default = Gravity.CENTER_VERTICAL
        .setChildGravity(Gravity.TOP)
        //whether RecyclerView can scroll
        .setScrollingEnabled(true)
        //set maximum views count in a particular row
        .setMaxViewsInRow(2)
        //set gravity resolver where you can determine gravity for item in position. This method have priority over previous one
        .setGravityResolver(new IChildGravityResolver() {
            @Override
            public int getItemGravity(int position) {
                return Gravity.CENTER;
            }
        })
        .build();
rv.setLayoutManager(chipsLayoutManager);
```

**Left to Right** direction have chosen by default.
You can manipulate with layout direction (**LTR** or **RTL**)<br/>
_On Api >= 17:_
```XML
<android.support.v7.widget.RecyclerView
       android:layout_width="match_parent"
       android:layout_height="match_parent"
       android:layoutDirection="rtl"
       />
```
or programmatically:
```JAVA
recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
```

_On Api < 17:_
```JAVA
ViewCompat.setLayoutDirection(recyclerView, ViewCompat.LAYOUT_DIRECTION_RTL);
```

For example look at sample application.
<br />
## Support
BETA is going.
I respond to your founded issues. Just create a ticket if you found something goes wrong.

### Contributors
All pull requests are welcome. I've tried to make code clean & readable, so the next is up to you. 
I appreciate any help and contributors will be mentioned here. 

### Known issues
* Cache of view positions is cleared on orientation change. But it wouldn't be so critical, because views are going to be properly repositioned 
when user will scroll to start again.

This issues are going to be fixed in next versions.