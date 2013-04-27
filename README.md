WoWHead - jModelViewer
=========

jModelViewer is a java based World of Warcraft modelviewer. The models are grabbed from the popular online world of warcraft database http://www.wowhead.com/ so no local client is needed.

The most work was done by the http://www.wowhead.com/ guys they provide the models and they wrote the jogl code I simply reversed the applet code, cleaned the code up to work on desktop, added some pretty cool new features (caching, removed all the static bullshit, enhanced code usability, ...)

### Dependencies
1. Vecmath - https://java.net/projects/vecmath
2. gluegen-rt - http://download.java.net/media/jogl/www/
3. jogl.all - http://download.java.net/media/jogl/www/
4. ApacheIO - http://commons.apache.org/proper/commons-io/
5. jogl natives - http://download.java.net/media/jogl/www/

### Setup
1. Download the source
2. Go to the download section and download the libary bundle which contains all the libarys this project depends on.
3. Create a project, include libaries and set the run parameter `-Djava.library.path=$REPLACE_ME_DIR_TO_NATIVES$`. For example if you put the native libaries into the `native` directory the parameter would be `-Djava.library.path=native`
4. Done.

### Usage
To get started and test is everything is set up correctly you can use the premade standalone JFrame with the rendering context `Util.showModelViewer(null)` or simply use `ModelViewerFrame.main(String[] args)`.