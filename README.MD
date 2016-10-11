# SpanLayoutManager <img src="https://www.cleveroad.com/public/comercial/label-android.svg" height="19">
![Header image](/images/header.png)

## This is SpanLayoutManager - custom Recycler View's LayoutManager maintained by me for community

As an android developer i see that google isn't so polite to support all our wishes, and when i faced with such common task like create spannable list
but with support of CRUD animations i decided to make such stuff and share it.

<p align="center">
    <img src="/images/demo.gif" alt="Span layout manager demo">
</p>

## Setup and usage
### Installation

Import SpanLayoutManager module to your project. Maven support will be added soon

### Features
Layout manager represents spanned or "bubble" structure of list items and is a good replace for typical implementations of such issue based on spans in TextView.

* supports RecyclerView's features, such as default animations while CRUD items & custom ItemAnimators, ItemDecorators etc.
* vertical scrolling support. Horizontal scrolling are NOT supported
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
SpanLayoutManager spanLayoutManager = SpanLayoutManager.newBuilder()
        //set vertical gravity for all items in a row. Default = Gravity.CENTER_VERTICAL
        .setChildGravity(Gravity.TOP)
        //set gravity resolver where you can determite gravity for item in position. This method have priority over previous one
        .setGravityResolver(new IChildGravityResolver() {
            @Override
            public int getItemGravity(int position) {
                return Gravity.CENTER;
            }
        })
        .build();
rv.setLayoutManager(spanLayoutManager);
```
For example look at sample application.
<br />
## Support

If you have any other questions regarding the use of this library, please contact me on github or create an issue on the tracking board

### Contributors
All pull requests are welcome. 
I appreciate any help and contributors will be mentioned here. 

### Known limitations
 * wrap_content on RecyclerView height haven't supported yet
 * RecyclerView's padding haven't supported yet. Please use margin instead until new version will be released
 * Reconstruction of items when scrolled down and scrolled up again in case scrolling up drawing algorithm can't place some views in a places as scrolling down algorithm did
 http://stackoverflow.com/questions/37889636/recyclers-view-custom-layout-manager-animation
 This is connected with that items are recycled and theirs positions aren't remembered and there haven't any cache support yet.

This issues are going to be fixed in next versions soon.
