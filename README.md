# SOFTENG 206 Assignment 3 
This is a prototype for VARpedia. A Visual Audio Reading wikipedia search.
This application should be tested in the SOFTENG 206 updated VirtualBox image.

# Libraries
The following libraries must be imported for the source code to work.

JavaFX13

flickr4java-3.0.1

scribejava-apis-6.6.3

scribejava-core-6.2.0

slf4j-api-1.7.25

slf4j-nop-1.7.26

These libraries are included in the libs folder, excluding JavaFX13. 
Along with some other libraries from the 206_FlickrExample ACP project.

# Flickr API Keys
The flickr API keys are located in flickr-api-keys.txt, in the same folder as the jar file.

# Runnable jar
This application should be run on the SOFTENG 206 Virtual Image.
You can run the jar file with the included run.sh script, make sure its in the same folder as the jar file.
Or from terminal
>PATH=/home/student/Downloads/openjdk-13_linux-x64_bin/jdk-13/bin:$PATH
>
>java --module-path /home/student/Downloads/openjfx-13-rc+2_linux-x64_bin-sdk/javafx-sdk-13/lib --add-modules javafx.base,javafx.controls,javafx.media,javafx.graphics,javafx.fxml -jar 206Assignment3.jar
