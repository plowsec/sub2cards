# sub2cards
Tool to generate vocabulary flashcards from a subtitle file

![test](https://raw.githubusercontent.com/plowsec/sub2cards/master/preview.png)

# Features

* Extracts vocabulary from a subtitle file.
* Get the base form of the collected words (written -> write) (done for russian language)
* Get the translation for a target language. Powered by Yandex.
* Given a video file and subtitle files, extract the sequences where a given subtitle line is said.
* Export to an html page
* Export the words into an Anki set

# To do
* Parse commandline arguments because not everyone wants to edit the Main.java
* Export the words to Quizlet
* Given a media file, download the subtitle file for a target language
* Provide a GUI

# Instructions
* If you need the translation feature, a Yandex API key is required. You can get one in 2 seconds at https://tech.yandex.com/key/form.xml?service=trnsl
* Next, you need to create a file config.properties in the root directory of the project, with the following content :

```
#Fri Jan 17 22:37:45 MYT 2017
yandex=your-api-key
```
* sqlite dependancy : add the jar sqlite-jdbc-3.8.11.2.jar to your project or to your classpath 
