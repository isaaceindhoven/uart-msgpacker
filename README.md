# UART-msgpacker

This UART-msgpacker library will help with (de)serialization of requests and responses to MsgPack format. 
See https://msgpack.org/ for more details regarding the Msgpack specifications.

# Installation

Currently, this library is not downloadable from any of the Repository Managers (Maven, Gradle, etc.). 
You can build an .aar file in which can be included in the libs folder of your project. 
Make sure to add the following lines to your `build.gradle` file (assuming you call the residing folder 'libs'):

`implementation fileTree(dir: 'libs', include: ['*.aar'])`

as well as the necessary dependencies for the library (.aar does not include transitive dependencies)

`implementation 'org.msgpack:msgpack-core:0.8.22'` // Older version can give compatibility issues. 

`implementation 'com.google.code.gson:gson:2.8.6'` // Older versions of Gson are probably compatible too.

The other option is to include this repository as a git submodule in the project.
If you plan on pushing code to the origin, make sure to use SSH when cloning to prevent issues now the GitHub no longer allows credential auth for this.