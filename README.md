# sub2cards
Tool to generate vocabulary flashcards from a subtitle file

[[https://raw.githubusercontent.com/plowsec/sub2cards/master/preview.png |alt=preview]]

# Features

* Extracts vocabulary from a subtitle file.
* Get the base form of the collected words (written -> write) (done for russian language)
* Get the translation for a target language. Powered by Yandex.
* Given a video file and subtitle files, extract the sequences where a given subtitle line is said.
* Export to an html page

# Wanted features

* Export the words into an Anki set (todo)
* Export the words to Quizlet (todo)
* Given a media file, download the subtitle file for a target language (todo)

# Instructions
* If you need the translation feature, a Yandex API key is required. You can get one in 2 seconds at https://tech.yandex.com/key/form.xml?service=trnsl
* Next, you need to create a file config.properties in the root directory of the project, with the following content :

```
#Fri Jan 17 22:37:45 MYT 2017
yandex=your-api-key
```
