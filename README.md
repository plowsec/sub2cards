# sub2cards
Tool to generate vocabulary flashcards from a subtitle file

# Wanted features
* Extracts vocabulary from a subtitle file (done)
* Get the base form of the collected words (written -> write) (done for russian language)
* Get the translation for a target language (done). Powered by Yandex.
* Export the words into an Anki set (todo)
* Export the words to Quizlet (todo)
* Given a media file, download the subtitle file for a target language (todo)
* Given a video file and subtitle files, extract the sequences where a given subtitle line is said (todo), export to an anki/quizlet set or to an html page


# Instructions
* If you need the translation feature, a Yandex API key is required. You can get one in 2 seconds at https://tech.yandex.com/key/form.xml?service=trnsl
* Next, you need to create a file config.properties in the root directory of the project, with the following content :

```
#Fri Jan 17 22:37:45 MYT 2017
yandex=your-api-key
```
