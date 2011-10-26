/*
*  Nokia Data Gathering
*
*  Copyright (C) 2011 Nokia Corporation
*
*  This program is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public
*  License as published by the Free Software Foundation; either
*  version 2.1 of the License, or (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public License
*  along with this program.  If not, see <http://www.gnu.org/licenses/
*/

package br.org.indt.ndg.mobile;

import br.org.indt.ndg.mobile.settings.LanguageHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

public class Localization {

    /**************************************************************************
     ****
             ****   Localization Support Begin
     ****
     **************************************************************************/



    /**
     * Error message used in the case there is any problem with initialization of
     * localization support. Please note, the error message should contain one
     * parameter sign - '{0}', which is used to fill in a reason of the failure.
     */
    private static final String _INIT_LOCALIZATION_ERROR_MSG = "Error when initializing localization support, reason: {0}";

    /**
     * Default String is returned from getMessage() methods when there is any problem
     * with finding the appropriate localized message or any part of it.
     */
    private static final String _DEFAULT_STRING = "???";

    /**
     * Initializes localization support based on currently set locale (obtained
     * from "microedition.locale" system property). The initialization method is called
     * automatically when a call to {@link #getMessage(java.lang.String)} method is attempted for the first time.
     *
     *
     * You can call this method explicitly to see whether there was any problem
     * with initialization of the localization support. Method returns a status
     * of the successfulness. If there was any problem with initialization, you can
     * get reason by using {@link #getErrorMessage()} method.
     * @return true if the intialization was succesfull, false if there was any problem.
     */
    public static boolean initLocalizationSupport() {
        LanguageHandler langHandler = new LanguageHandler();
        langHandler.getLanguage();
        messageTable = new Hashtable();
        return initLocalizationSupport(langHandler.getLanguage(), messageTable);     // NOI18N
    }

    private static boolean initDefaultLocalizationSupport(){
        String locale = AppMIDlet.getInstance().getSettings().getStructure().getDefaultLanguage().getLocale();
        defaultMessageTable = new Hashtable();
        return initLocalizationSupport(locale, defaultMessageTable);
    }

    /**
     * Explicit initialization of the localization support. This method is usually
     * called when a particular locale used in the application. E.g. the application
     * contains only french messages (no default messages, only <CODE>messages_fr.properties</CODE>
     * files is available), you should initialize the localization support (by calling
     * <CODE>initLocalizationSupport("fr");</CODE>) before using {@link #getMessage(java.lang.String)} method for the first
     * time.
     *
     * Method returns a status of the successfulness. If there was any problem with
     * the initialization, you can get explanation by using {@link #getErrorMessage()}
     * method.
     * @param locale locale which will be used to determine which message file from bundle will be used
     * @return true if the intialization was succesfull, false if there was any problem.
     */
    public static boolean initLocalizationSupport(String locale, Hashtable table) {
        // need access to a class object - cannot use Object.class, because of MIDP1 bug
        Class clazz = Runtime.getRuntime().getClass();

        boolean retVal = false;
        InputStream inputStream = null;
        FileConnection fileConnection = null;
        
        try {
            fileConnection = (FileConnection) Connector.open( Utils.prepereMessagesPath(locale) );
            if( fileConnection.exists() ) {
                inputStream = fileConnection.openInputStream();
            }else{
                 inputStream = clazz.getClass().getResourceAsStream(NdgConsts.DEFAULT_MESSAGES_FILE);
            }

            if(inputStream != null){
                loadMessages(inputStream, table);
                retVal = true;
            }
        }catch(IOException ex){
            retVal = false;
        }finally{
            try {
                if(inputStream != null){
                    inputStream.close();
                }
                if(fileConnection != null){
                    fileConnection.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return retVal;
    }

    /**
     * Returns an error message if there was any problem with accessing the localized
     * text. The message also possibly explainins a reason of the failure. The message
     * is taken from <CODE>_INIT_LOCALIZATION_ERROR_MSG</CODE>.
     * @return error message if there was any failure or null when everything is OK.
     */
    public static String getErrorMessage() {
        return _localizationErrorMessage;
    }

    /**
     * Finds a localized string in a message bundle.
     * @param key key of the localized string to look for
     * @return the localized string. If key is not found, then  <CODE>_DEFAULT_STRING</CODE> string
     * is returned
     */
    public static final String getMessage(String key) {
        return getMessage(key, null);
    }

    private static final String getFromLocale(String key){
        if (messageTable == null) {
            if (!initLocalizationSupport()) {
                return null;
            }
        }
        return (String) messageTable.get(key);
    }

    private static final String getFromDefault(String key){
        if (defaultMessageTable == null) {
            if (!initDefaultLocalizationSupport()) {
                return null;
            }
        }
        return (String) defaultMessageTable.get(key);
    }

    /**
     * Finds a localized string in a message bundle and formats the message by passing
     * requested parameters.
     * @param key key of the localized string to look for
     * @param args array of parameters to use for formatting the message
     * @return the localized string. If key is not found, then <CODE>_DEFAULT_STRING</CODE> string
     * is returned
     */
    public static final String getMessage(String key, Object[] args) {
        
        StringBuffer toAppendTo = new StringBuffer();
        String s = null;
        if((s = getFromLocale(key)) == null){
            if((s = getFromDefault(key)) == null){
                return _DEFAULT_STRING;
            }
        }
        
        int l = s.length();
        int n = 0, lidx = -1, lastidx = 0;
        for (int i = 0; i < l; i++) {
            if (s.charAt(i) == '{') {
                n++;
                if (n == 1) {
                    lidx = i;
                    toAppendTo.append(s.substring(lastidx, i));
                    lastidx = i;
                }
            }
            if (s.charAt(i) == '}') {
                if (n == 1) {
                    toAppendTo.append(_processPattern(s.substring(lidx + 1, i), args));
                    lidx = -1;
                    lastidx = i + 1;
                }
                n--;
            }
        }
        if (n > 0) {
            toAppendTo.append(_processPattern(s.substring(lidx + 1), args));
        } else {
            toAppendTo.append(s.substring(lastidx));
        }

        return toAppendTo.toString();
    }

    /* The rest is private to localization support. You shouldn't change anything
     * below this comment unless you really know what you are doing
     * Ideally, everyhthing below this should be collapsed.
     */

    /**
     * Characters separating keys and values
     */
    private static final String _KEY_VALUE_SEPARATORS = "=: \t\r\n\f";
    /**
     * Characters strictly separating keys and values
     */
    private static final String _STRICT_KEY_VALUE_SEPARTORS = "=:";
    /**
     * white space characters understood by the support (these can be in the message file)
     */
    private static final String _WHITESPACE_CHARS = " \t\r\n\f";
    /**
     * Contains the parsed message bundle.
     */
    private static Hashtable messageTable;

    /**
     * Contains the parsed message bundle form default localization file
     */
    private static Hashtable defaultMessageTable;

    /**
     * Contains an error message if there was any problem with localization support.
     * If everything is OK, this field is null.
     */
    private static String _localizationErrorMessage = null;

    /**
     * Loads messages from input stream to hash table.
     * @param inStream stream from which the messages are read
     * @throws IOException if there is any problem with reading the messages
     */
    private static synchronized void loadMessages(InputStream inStream, Hashtable table) throws IOException {

        InputStreamReader in = new InputStreamReader(inStream);
        while (true) {
            // Get next line
            String line = _readLine(in);
            if (line == null) {
                return;
            }

            if (line.length() > 0) {

                // Find start of key
                int len = line.length();
                int keyStart;
                for (keyStart = 0; keyStart < len; keyStart++) {
                    if (_WHITESPACE_CHARS.indexOf(line.charAt(keyStart)) == -1) {
                        break;
                    }
                }

                // Blank lines are ignored
                if (keyStart == len) {
                    continue;
                }

                // Continue lines that end in slashes if they are not comments
                char firstChar = line.charAt(keyStart);
                if ((firstChar != '#') && (firstChar != '!')) {
                    while (_continueLine(line)) {
                        String nextLine = _readLine(in);
                        if (nextLine == null) {
                            nextLine = "";
                        }
                        String loppedLine = line.substring(0, len - 1);
                        // Advance beyond whitespace on new line
                        int startIndex;
                        for (startIndex = 0; startIndex < nextLine.length(); startIndex++) {
                            if (_WHITESPACE_CHARS.indexOf(nextLine.charAt(startIndex)) == -1) {
                                break;
                            }
                        }
                        nextLine = nextLine.substring(startIndex, nextLine.length());
                        line = new String(loppedLine + nextLine);
                        len = line.length();
                    }

                    // Find separation between key and value
                    int separatorIndex;
                    for (separatorIndex = keyStart; separatorIndex < len; separatorIndex++) {
                        char currentChar = line.charAt(separatorIndex);
                        if (currentChar == '\\') {
                            separatorIndex++;
                        } else if (_KEY_VALUE_SEPARATORS.indexOf(currentChar) != -1) {
                            break;
                        }
                    }

                    // Skip over whitespace after key if any
                    int valueIndex;
                    for (valueIndex = separatorIndex; valueIndex < len; valueIndex++) {
                        if (_WHITESPACE_CHARS.indexOf(line.charAt(valueIndex)) == -1) {
                            break;
                        }
                    }

                    // Skip over one non whitespace key value separators if any
                    if (valueIndex < len) {
                        if (_STRICT_KEY_VALUE_SEPARTORS.indexOf(line.charAt(valueIndex)) != -1) {
                            valueIndex++;
                        }
                    }

                    // Skip over white space after other separators if any
                    while (valueIndex < len) {
                        if (_WHITESPACE_CHARS.indexOf(line.charAt(valueIndex)) == -1) {
                            break;
                        }
                        valueIndex++;
                    }
                    String key = line.substring(keyStart, separatorIndex);
                    String value = (separatorIndex < len) ? line.substring(valueIndex, len) : "";

                    // Convert then store key and value
                    key = _convertString(key);
                    value = _convertString(value);
                    table.put(key, value);
                }
            }
        }

    }

    /**
     * reads a single line from InputStreamReader
     * @param in InputStreamReader used to read the line
     * @throws IOException if there is any problem with reading
     * @return the read line
     */
    private static String _readLine(InputStreamReader in) throws IOException {
        StringBuffer strBuf = new StringBuffer("");
        int i;
        while ((i = in.read()) != -1) {
            if ((char) i == '\r' || (char) i == '\n') {
                return strBuf.toString();
            }
            strBuf.append((char) i);
        }
        return strBuf.length() > 0 ? strBuf.toString() : null;
    }

    /**
     * determines whether the line of the supplied string continues on the next line
     * @param line a line of String
     * @return true if the string contines on the next line, false otherwise
     */
    private static boolean _continueLine(String line) {
        int slashCount = 0;
        int index = line.length() - 1;
        while ((index >= 0) && (line.charAt(index--) == '\\')) {
            slashCount++;
        }
        return (slashCount % 2 == 1);
    }

    /**
     * Decodes a String which uses unicode characters in \\uXXXX format.
     * @param theString String with \\uXXXX characters
     * @return resolved string
     */
    private static String _convertString(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);

        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                // return DEFAULT STRING if there is any problem
                                return _DEFAULT_STRING;
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }

    /**
     * Extracts N-th from an array of argumens.
     * @param indexString a String number
     * @param args array of arguments
     * @return the indexString-th parameter from the array
     */
    private static String _processPattern(String indexString, Object[] args) {
        try {
            int index = Integer.parseInt(indexString);
            if ((args != null) && (index >= 0) && (index < args.length)) {
                if (args[index] != null) {
                    return args[index].toString();
                }
            }
        } catch (NumberFormatException nfe) {
            // NFE - nothing bad basically - the argument is not a number
            // swallow it for the time being and show default string
        }
        return _DEFAULT_STRING;
    }

    /**************************************************************************
     ****
     ****   Localization Support End
     ****
     **************************************************************************/
}
