package uk.ac.aber.dcs.cs31620.anagram_solver_android

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class AnagramViewModel(application: Application) : AndroidViewModel(application) {

    var anagram by mutableStateOf("")
    var matches by mutableStateOf(0)
    var words = mutableStateListOf<String>()

    private var wordsCap = 1000

    //private var numbers = mutableListOf('1','2','3','4','5','6','7','8','9','0')
    //private var specialChars = mutableListOf('-','`','!','@','#','$','%','^','&','*','(',')','~','_','+','=','{','}','[',']','|',':',';','"','<','>',',','.','?')

    /**
     * Reads in the dictionary which is then used to
     * find the possible solutions of an anagram
     *
     * @return the words from the dictionary
     */
    fun readDictionary(): MutableList<String>{
        val dictionaryWords = mutableListOf<String>()
        var tempWord: String? = ""
        val inputStream: InputStream = getApplication<Application>().applicationContext.resources.openRawResource(R.raw.dictionary)

        val reader = BufferedReader(InputStreamReader(inputStream))
        while(true){
            try{
                if(reader.readLine().also {
                    tempWord = it
                    } == null) break
            }
            catch (e: IOException){
                Toast.makeText(getApplication<Application>().applicationContext, "Problem reading dictionary", Toast.LENGTH_LONG).show()
            }
            dictionaryWords.add(tempWord?.lowercase().toString())
        }
        inputStream.close()

        return dictionaryWords
    }

    /**
     * Determines which type of anagram the user has
     * entered and calls the corresponding method
     *
     * @param anagram as a string
     */
    fun readAnagram(anagram: String){
        val dictionaryWords = readDictionary()
        val filteredDictionary = filterDictionaryToLength(dictionaryWords, anagram)
        matches = if(anagram.contains('+')){
            clearList(words)
            solveAnagramMissingLetters(anagram, filteredDictionary)
            words.size
        }else if(anagram.contains('.')){
            clearList(words)
            solveAnagramCrossword(anagram, filteredDictionary)
            words.size
        }else{
            clearList(words)
            solveAnagramComplete(anagram, filteredDictionary)
            words.size
        }
    }

    /**
     * Clears the list before the next anagram is solved
     *
     * @param words as the list of words already found
     */
    private fun clearList(words: SnapshotStateList<String>){
        words.clear()
    }

    /**
     * Solves an anagram where all the letters are known
     *
     * @param anagram as a string
     * @param filteredDictionary as a list of strings
     *
     * @return the possible solutions to the anagram
     */
    fun solveAnagramComplete(anagram: String, filteredDictionary: List<String>){
        for(word in filteredDictionary){
            if(hasCorrectLettersComplete(anagram, word)){
                if(!words.contains(word) && words.size < wordsCap){
                    words.add(word)
                }
            }
        }
    }

    /**
     * Solves an anagram where not
     * all of the letters are known
     *
     * @param anagram as a string
     * @param filteredDictionary as a list of strings
     *
     * @return wordList
     */
    fun solveAnagramMissingLetters(anagram: String, filteredDictionary: List<String>){
        for(word in filteredDictionary){
            if(hasCorrectLettersMissing(anagram, word)){
                if(!words.contains(word) && words.size < wordsCap){
                    words.add(word)
                }
            }
        }
    }

    /**
     * Solves an anagram where some letters
     * and their positions are known but
     * some letters are missing
     *
     * @param anagram as a string
     * @param filteredDictionary as a list of strings
     *
     * @return wordList
     */
    fun solveAnagramCrossword(anagram: String, filteredDictionary: List<String>){
        for(word in filteredDictionary){
            if(hasCorrectLettersCrossword(anagram, word)){
                if(!words.contains(word) && words.size < wordsCap){
                    words.add(word)
                }
            }
        }
    }

    /**
     * Checks if the word contains the
     * correct quantities of each letter
     *
     * @param anagram as a string
     * @param word as a string
     *
     * @return whether the word is a match
     */
    private fun hasCorrectLettersComplete(anagram: String, word: String): Boolean {
        var lettersFoundCount = 0

        for (i in anagram.indices) {
            val anagramLetterCount = countLetterOccurrences(anagram, anagram[i])
            val wordLetterCount = countLetterOccurrences(word, anagram[i])

            if (anagramLetterCount == wordLetterCount) {
                lettersFoundCount += 1
            } else {
                break
            }
        }

        return lettersFoundCount == anagram.length
    }

    /**
     * Checks if the word contains the correct
     * quantities of each letter and is the correct length
     *
     * @param anagram as a string
     * @param word as a string
     *
     * @return whether the word is a match
     */
    private fun hasCorrectLettersMissing(anagram: String, word: String): Boolean{
        var wordFound = true

        for(letter in anagram){
            if(letter != '+'){
                if(!word.contains(letter)){
                    wordFound = false
                    break
                }
            }
        }

        return wordFound
    }

    /**
     * Checks if the word has the correct
     * letters in the correct position
     *
     * @param anagram as a string
     * @param word as a string
     *
     * @return whether the word is a match
     */
    private fun hasCorrectLettersCrossword(anagram: String, word: String): Boolean{
        var wordFound = true

        for(i in anagram.indices){
            if(anagram[i] != '.'){
                if(anagram[i] != word[i]){
                    wordFound = false
                    break
                }
            }
        }

        return wordFound
    }


    /**
     * Counts the occurrences of a letter within a string
     *
     * @param string as a string
     * @param char as a char
     *
     * @return the count of the letter occurrences
     */
    private fun countLetterOccurrences(string: String, char: Char): Int{
        return string.count { it == char }
    }

    private fun filterDictionaryToLength(dictionaryWords: List<String>, anagram: String): List<String>{
        return dictionaryWords.filter { it.length == anagram.length }
    }

}