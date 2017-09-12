# sub2cards
Tool to generate vocabulary flashcards from a subtitle file

![test](https://raw.githubusercontent.com/plowsec/sub2cards/master/preview.png)

# Disclaimer
This software is currently in development and is not ready for everyday use. However, if you know a little programming, you can tweak some things to make it generate the data that you need, as all the important functions are implemented.

# Features

* Extracts vocabulary from a subtitle file.
* Get the base form of the collected words (written -> write) (done for russian language)
* Get the translation for a target language. Powered by Yandex.
* Given a video file and subtitle files, extract the sequences where a given subtitle line is said.
* Export to an html page
* Export the words into an Anki set

# Examples
Here are examples of data produced by this software and the command used to do so :

1000 russian/english words extracted from Game of Thrones S04E01, aggregated by base form (written -> write) and sorted by occurences :
```
java -jar sub2cards.jar -s tests/got.srt -e text -l ru-en -w words
```

~Anki deck of 400 sentences extracted from Game of Thrones S04E01, with sounds, thumbnails from video and translation (Yandex) :

```
java -jar sub2cards.jar -s test/got.srt -e anki -m tests/video.avi -w lines
```


# To do (small contributions)

* Cache the results of the requests made to Yandex/Starling to speed up everything
* Allow to specify output destination
* Allow to name the anki deck generated
* Take into account option -t (target subtitle file), so the user doesn't need to use Yandex
* Allow the user to skip the "simplification" of words (words like 'written' and 'write' will not be aggregated for example)
* Implement option -e text, to generate a file
* Add csv export mode ?
* Implement -w mixed (lines with audio, thumbnails, translation and all the words of the sentence translated independantly below the sentences. This requires to modify the anki format in Constant.java)
* Adapt the code to take into account all the arguments given to options -t and -s
* Adapt the code so that each Word instance remembers the context where it was found, to give an example sentence. Maybe propose a new export mode to generate an anki deck that has the word and the context on the verso, and the translation on the back.
* Adapt the projet to use Maven to handle dependencies
* Print the progress, so the user has an idea of the amount of time he has to wait to generate a deck
* Export modes with no image | sound | translation (or combination of previous items)

# To do ('big' work)
* Parse commandline arguments because not everyone wants to edit the Main.java
* Export the words to Quizlet
* Given a media file, download the subtitle file for a target language
* Provide a GUI
* Allow the user to give the location of a folder were multiple subtitle files are stored, in 2 different languages at most


# Instructions
* If you need the translation feature, a Yandex API key is required. You can get one in 2 seconds at https://tech.yandex.com/key/form.xml?service=trnsl
* Next, you need to create a file config.properties in the root directory of the project, with the following content :

```
#Fri Jan 17 22:37:45 MYT 2017
yandex=your-api-key
```
* sqlite dependancy : add the jar sqlite-jdbc-3.8.11.2.jar to your project or to your classpath 
