# ChipsLayoutManager <img alt="ChipsLayoutManager is an android library" src="https://www.cleveroad.com/public/comercial/label-android.svg" height="19"> [![minSdk for ChipsLayoutManager](https://img.shields.io/badge/minSdk-15-green.svg)](#) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ChipsLayoutManager-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/4568#)
![ChipsLayoutManager for RecyclerView. Custom layout manager for RecyclerView](/images/header.png)

## This is ChipsLayoutManager - custom Recycler View's LayoutManager which moves item to the next line when no space left on the current 
This layout manager could represent [Google Chips material design pattern](https://material.google.com/components/chips.html#)

As an android developer i see that google isn't so polite to support all our wishes, and when i faced with such common task like create spannable list
but with support of CRUD animations i decided to make such stuff and share it.

<p align="center">
    <img src="/images/demo.gif" alt="Chips layout manager gif with scrolling">
</p>

## Setup and usage
### Installation [ ![Download](https://api.bintray.com/packages/beloo/widget/ChipsLayoutManager/images/download.svg) ](https://bintray.com/beloo/widget/ChipsLayoutManager/_latestVersion)

with gradle
```GRADLE 
compile 'com.beloo.widget:ChipsLayoutManager:0.3.0@aar'
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
Layout manager represents chips or spanned or "bubble" structure of list items and is a good replace for typical implementations of such issue based on spans in `TextView` 
or custom `FlowLayout`s.

* supports `RecyclerView`'s features, such as default animations while CRUD items & custom `ItemAnimator`s, `ItemDecorator`s, scrolling etc. `wrap_content` of height is supported
* vertical scrolling is supported. Horizontal scrolling isn't supported. It is possible to disable vertical scrolling as well. Supported padding/margin, clipToPadding parameter of `RecyclerView`
* supports different height and width of items
* recycling items support (another advantage over `TextView` approach)
* items gravity inside row support. For all items or only for required ones
* LTR and RTL support
* activity orientation change support
* predictive animations supported (animate view movement outside of visible bounds)
* setting max count of view in row
* you are able to break row due to your conditions

| Insert/delete | Move |
|---|---|
|![Insert/delete animations with ChipsLayoutManager](/images/insert_delete_animations.gif) |![Move animations with ChipsLayoutManager](/images/move_animations.gif)|

###Layout Directions
<center>

|  | Left to Right  | Right to Left  |
|---|---|---|
|  <b>Horizontal</b> |![Left to Right Rows implementation of ChipsLayoutManager](/images/ltr_horizontal.png) |![Right to Left implementation of ChipsLayoutManager](/images/rtl_horizontal.png)|
|  <b>Vertical</b>   |![Left to Right Columns implementation of ChipsLayoutManager](/images/ltr_vertical.png) ||

</center>

### Usage

```JAVA 
ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder()
        //set vertical gravity for all items in a row. Default = Gravity.CENTER_VERTICAL
        .setChildGravity(Gravity.TOP)
        //whether RecyclerView can scroll. TRUE by default
        .setScrollingEnabled(true)
        //set maximum views count in a particular row
        .setMaxViewsInRow(2)
        //set gravity resolver where you can determine gravity for item in position. 
        //This method have priority over previous one
        .setGravityResolver(new IChildGravityResolver() {
            @Override
            public int getItemGravity(int position) {
                return Gravity.CENTER;
            }
        })
        //you are able to break row due to your conditions. Row breaker should return true for that views
        .setRowBreaker(new IRowBreaker() {
            @Override
            public boolean isItemBreakRow(@IntRange(from = 0) int position) {
                return position == 6 || position == 11 || position == 2;
            }
        })
        //an layoutOrientation of layout manager, could be VERTICAL OR HORIZONTAL. HORIZONTAL by default
        .setOrientation(ChipsLayoutManager.HORIZONTAL)
        .build();
rv.setLayoutManager(chipsLayoutManager);
```

Notice that if you changed gravity resolver or row breaker conditions in runtime - it won't affect already drew items and cached item positions. 
You have to call `notifyItemChanged` on `ChipsLayoutManager` otherwise layout won't be predictable. 

**Device current direction** have been chosen by default.
You can manipulate with layout direction (**LTR** or **RTL**)<br/>
_On Api >= 17:_
```XML
<android.support.v7.widget.RecyclerView
       android:layout_width="match_parent"
       android:layout_height="match_parent"
       android:layoutDirection="rtl"
       />
```
or programmatically (_on all supported Api:_):
```JAVA
ViewCompat.setLayoutDirection(recyclerView, ViewCompat.LAYOUT_DIRECTION_RTL);
```

For example look at sample application.
<br />
### Bonus
You may need an `RecyclerView.ItemDecoration` to work with `ChipsLayoutManager` for fast setup as showed in sample. <br/>
It contained in same package and named `SpacingItemDecoration`.

## Support
BETA is going.
I respond to your founded issues. Just create a ticket if you found something goes wrong.

### Contributors
All pull requests are welcome. I've tried to make code clean & readable, so the next is up to you. 
I appreciate any help and contributors will be mentioned here. 

### Known issues
I create issues in tracking board of known problems.
These issues are going to be fixed in next versions.

### License [![license for ChipsLayoutManager](https://img.shields.io/crates/l/rustc-serialize.svg)](#)

    Copyright 2016 Beloy Oleg, Ukraine.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
