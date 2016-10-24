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

or just import ChipsLayoutManager module to your project.

### Features
Layout manager represents chips or spanned or "bubble" structure of list items and is a good replace for typical implementations of such issue based on spans in TextView.

* supports RecyclerView's features, such as default animations while CRUD items & custom ItemAnimators, ItemDecorators etc. wrap_content of height is supported
* vertical scrolling support. Horizontal scrolling are NOT supported. It is possible to disable vertical scrolling as well.
* supports views with different height and width
* recycling items support (another advantage over TextView approach)
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
For example look at sample application.
<br />
## Support

If you have any other questions regarding the use of this library, please contact me on github or create an issue on the tracking board

### Contributors
All pull requests are welcome. 
I appreciate any help and contributors will be mentioned here. 

### Known limitations
 * RecyclerView's padding haven't supported yet. Please use margin instead until new version will be released

This issues are going to be fixed in next versions soon.
