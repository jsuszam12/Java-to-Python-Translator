package application;
import java.util.*;
import java.io.*;

/*
    Java tokens to be stored in the constructor of the lexical analyzer class or whatever class
    does the lexical analysis of the Java code. I've created a custom Lexer to illustrate the concept
    of how identifiers and literals will be stored during analysis, and will see about having this solution
    adapted (however much of it necessary) to James R.'s Lexer.
 */

public class LexicalAnalyzer {

    /*
        Tokens are classified as keywords, identifiers,
        literals, constants, comments, operators,
        and separators.
    */
    static protected Map<String,String> keyWord = new HashMap<>();

    static protected Map<Character, String> stringLiteral = new HashMap<>();
    static protected Map<String, String> integerLiteral = new HashMap<>();
    static protected Map<String, String> doubleLiteral = new HashMap<>();
    static protected Map<String, String> booleanLiteral = new HashMap<>();
    static protected Map<String, String> nullLiteral = new HashMap<>();

    static protected Map<String,String> constant = new HashMap<>();
    static protected Map<String,String> comment = new HashMap<>();

    static protected Map<String,String> opArithmetic = new HashMap<>();
    static protected Map<String,String> opUnary = new HashMap<>();
    static protected Map<String,String> opAssignment = new HashMap<>();
    static protected Map<String,String> opRelational = new HashMap<>();
    static protected Map<String,String> opLogical = new HashMap<>();

    static protected Map<String,String> separator = new HashMap<>();

    static protected Map<String,String> mainIdent = new HashMap<>();
    static protected Map<String,String> stringIdent = new HashMap<>();
    static protected Map<String,String> argsIdent = new HashMap<>();

    static String[] checkLongSpecificationStatement = new String[4];

    static ArrayList<TokenData> arrayOfTokens = new ArrayList<>();
    static ArrayList<String> listOfVariables = new ArrayList<>();

    //static ArrayList<String> listOfVariables = new ArrayList<>();

    static boolean insideOfNonMainMethod = false;
    static boolean insideOfMainMethod = false;
    static boolean insideOfClass = false;
    static boolean publicBool = false;
    static boolean classBool = false;

    static int indentionLevelCount = 0;
    
    static String prevTokenTwo = "";

    // Store tokens
    public LexicalAnalyzer() {

        booleanLiteral.put("TRUE", "boolean literal");
        booleanLiteral.put("FALSE", "boolean literal");

        comment.put("//", "comment");
        comment.put("/*", "comment");
        comment.put("*/", "comment");

        keyWord.put("ABSTRACT", "keyword");
        keyWord.put("BOOLEAN", "T_BOOL");
        keyWord.put("BREAK", "keyword");
        keyWord.put("BYTE", "T_BYTE");
        keyWord.put("CASE", "keyword");
        keyWord.put("CATCH", "keyword");
        keyWord.put("CHAR", "T_CHAR");
        keyWord.put("CLASS", "T_CLASS");
        keyWord.put("CONTINUE", "keyword");
        keyWord.put("DEFAULT", "keyword");
        keyWord.put("DO", "T_DO");
        keyWord.put("DOUBLE", "T_DOUBLE");
        keyWord.put("ELSE", "T_ELSE");
        keyWord.put("ENUM", "keyword");
        keyWord.put("EXTENDS", "keyword");
        keyWord.put("FINAL", "keyword");
        keyWord.put("FINALLY", "keyword");
        keyWord.put("FLOAT", "T_FLOAT");
        keyWord.put("FOR", "T_FOR");
        keyWord.put("IF", "T_IF");
        keyWord.put("IMPORT", "keyword");
        keyWord.put("INT", "T_INT");
        keyWord.put("LONG", "T_LONG");
        keyWord.put("NEW", "T_NEW");
        keyWord.put("NULL", "keyword");
        keyWord.put("PACKAGE", "keyword");
        keyWord.put("PRIVATE", "T_PRIVATE");
        keyWord.put("PROTECTED", "T_PROTECTED");
        keyWord.put("PUBLIC", "T_PUBLIC");
        keyWord.put("RETURN", "keyword");
        keyWord.put("SHORT", "T_SHORT");
        keyWord.put("STATIC", "T_STATIC");
        keyWord.put("SUPER", "keyword");
        keyWord.put("SWITCH", "keyword");
        keyWord.put("SYNCHRONIZED", "keyword");
        keyWord.put("THROW", "keyword");
        keyWord.put("THROWS", "keyword");
        keyWord.put("TRY", "keyword");
        keyWord.put("VOID", "T_VOID");
        keyWord.put("WHILE", "T_WHILE");

        nullLiteral.put("null", "null literal");

        opArithmetic.put("+", "arithmetic operator");
        opArithmetic.put("-", "arithmetic operator");
        opArithmetic.put("/", "arithmetic operator");
        opArithmetic.put("*", "arithmetic operator");
        opArithmetic.put("%", "arithmetic operator");

        opUnary.put("++", "unary operator");
        opUnary.put("--", "unary operator");

        opAssignment.put("=", "assignment operator");
        opAssignment.put("+=", "assignment operator");
        opAssignment.put("-=", "assignment operator");
        opAssignment.put("*=", "assignment operator");
        opAssignment.put("/=", "assignment operator");
        opAssignment.put("%=", "assignment operator");

        opRelational.put("==", "relational operator");
        opRelational.put("!=", "relational operator");
        opRelational.put("<", "relational operator");
        opRelational.put(">", "relational operator");
        opRelational.put("<=", "relational operator");
        opRelational.put(">=", "relational operator");

        opLogical.put("&", "logical operator");
        opLogical.put("|", "logical operator");
        opLogical.put("!", "logical operator");

        stringLiteral.put('"', "string literal");

        separator.put("[", "T_LBRACKET");
        separator.put("]", "T_RBRACKET");
        separator.put("(", "T_LPARENTH");
        separator.put(")", "T_RPARENTH");
        separator.put("{", "T_LBRACE");
        separator.put("}", "T_RBRACE");
        separator.put(",", "T_COMMA");
        separator.put("=", "T_ASSIGN");
        separator.put(";", "T_SEMICOLON");
        separator.put(".", "T_DOT");

        mainIdent.put("MAIN", "MAIN_IDENTIFIER");
        stringIdent.put("STRING", "STRING_IDENTIFIER");
        argsIdent.put("ARGS", "ARGS_IDENTIFIER");

        integerLiteral.put("0.0", "integer literal");
    }

    public static List<Object> streamTokenizer(Reader r) throws IOException
    {
        StreamTokenizer st = new StreamTokenizer(r);
        List<Object> tokens = new ArrayList<Object>();

        //Set the quotations character to be counted as a word character to keep it attached
        //to the string it is encasing
        st.wordChars('"',' ');

        //Read tokens until there is none and add to the token list
        //TT_EOF = end of file, TT_Word = string value, tt_number = a number value
        int currToken = st.nextToken();
        while (currToken != st.TT_EOF)
        {
            if (st.ttype == st.TT_NUMBER) {

                tokens.add(st.nval);

            } else if (st.ttype == st.TT_WORD || st.ttype == '\'' || st.ttype == '"') {

                if (st.ttype == '"') {
                    tokens.add('"' + st.sval + '"');
                } else if (st.ttype == '\'') {
                    tokens.add("'" + st.sval + "'");
                } else {
                    tokens.add(st.sval);
                }

            } else {
                tokens.add((char) currToken);
            }

            currToken = st.nextToken();
        }
        return tokens;
    }

    public static boolean isNumber(Object potentialNumber) {
        return potentialNumber instanceof Number;
    }

    static void printTranslator() {
        PythonConverter.translateDriver(arrayOfTokens);
    }

    static void printLookAheadFunction() {
        PythonConverter.lookAheadFunction(arrayOfTokens);
    }

    static void getDeletedPyStr() {
        PythonConverter.deletePyStr(arrayOfTokens);
    }

    static String getPythonStr() { return PythonConverter.resultPythonStr; }

    public static void directNumber(Object testObject) {

        String strObject = testObject.toString();

        arrayOfTokens.add(new TokenData("NUMBER", strObject));
        System.out.println("NUMBER" + " - " + strObject);

        checkLongSpecificationStatement[3] = "NUMBER";

    }

    public static void translateToPython(Object testObject) {

        // I expect that the "instanceof" functionality (see isNumber()) may be necessary for the checks.
        // Secondary object is call the Objects toString() method and store/manipulate that way.

        // Keyword check - May require further steps depending on the keyword
        // Loop/Call a method that loops through the list of keywords that, as of writing this, doesn't exist. Check against all known keywords.
        // Replace where necessary. Methods should be created for most of the keywords to ensure they are converted correctly.
        // Part of this should include catching the label that follows the keyword, where applicable (such as a class or variable name)
        //------------------------------------------------------
        // If it's a Keyword:


        //------------------------------------------------------

        // String check - Catches anything starting or ending with '"', makes no change to the rest
        //------------------------------------------------------
        // If it's a String:


        //------------------------------------------------------

        // Separator check - May need two separator checks, one for open and the other for close.
        //------------------------------------------------------
        // If it's a Separator:


        //------------------------------------------------------

        // Operator check - Should be fairly straight forward, no change really needs to happen to it.
        //If we stick to manipulating a list, we may not even need this check - we could just do nothing, make no change, if it's an operator
        //------------------------------------------------------
        // If it's an Operator:


        //------------------------------------------------------

        // Input check - I call this input, but I wasn't sure how to handle the "System.out.println()" content in general. May be a better name for this.
        //------------------------------------------------------
        // If it's an Input:


        //------------------------------------------------------

        String strObject = testObject.toString();

        //System.out.println("                                             " + strObject);

        String prevToken = "";

        if(keyWord.containsKey(strObject.toUpperCase())) {

            if(strObject.equals("public")) {
                publicBool = true;
            }

            if(strObject.equals("class")) {
                classBool = true;
            }

            arrayOfTokens.add(new TokenData(keyWord.get(strObject.toUpperCase()), strObject));
            System.out.println(keyWord.get(strObject.toUpperCase()) + " - " + strObject);

            checkLongSpecificationStatement[0] = keyWord.get(strObject.toUpperCase());

        } else if(separator.containsKey(strObject.toUpperCase())) {

            if(strObject.equals("{")) {

                if(insideOfClass) {
                    insideOfNonMainMethod = false;
                    insideOfClass = false;
                    indentionLevelCount++;
                } else {
                    insideOfNonMainMethod = true;
                    indentionLevelCount++;
                }

            }


            if(strObject.equals("}") && indentionLevelCount >= 3) {
                insideOfNonMainMethod = true;
                insideOfMainMethod = false;
                indentionLevelCount--;
            } else if(strObject.equals("}") && indentionLevelCount < 3) {
                insideOfNonMainMethod = false;
                insideOfMainMethod = false;
                indentionLevelCount--;
            }

            arrayOfTokens.add(new TokenData(separator.get(strObject.toUpperCase()), strObject));
            System.out.println(separator.get(strObject.toUpperCase()) + " - " + strObject);

            checkLongSpecificationStatement[2] = separator.get(strObject.toUpperCase());

        } else if(strObject.charAt(0) == '"' && strObject.charAt(strObject.length()-1) == '"') {

            arrayOfTokens.add(new TokenData(stringLiteral.get('"'), strObject));
            System.out.println(stringLiteral.get('"') + " - " + strObject);

        } else if(strObject.charAt(0) == '\'' && strObject.charAt(strObject.length()-1) == '\'' && strObject.length() == 3) {

            arrayOfTokens.add(new TokenData("character literal", strObject));
            System.out.println("character literal" + " - " + strObject);

        } else if(mainIdent.containsKey(strObject.toUpperCase())) {

            insideOfMainMethod = true;

            arrayOfTokens.add(new TokenData(mainIdent.get(strObject.toUpperCase()), strObject));
            System.out.println(mainIdent.get(strObject.toUpperCase()) + " - " + strObject);

        } else if(stringIdent.containsKey(strObject.toUpperCase())) {

            arrayOfTokens.add(new TokenData(stringIdent.get(strObject.toUpperCase()), strObject));
            System.out.println(stringIdent.get(strObject.toUpperCase()) + " - " + strObject);

        } else if(argsIdent.containsKey(strObject.toUpperCase())) {

            arrayOfTokens.add(new TokenData(argsIdent.get(strObject.toUpperCase()), strObject));
            System.out.println(argsIdent.get(strObject.toUpperCase()) + " - " + strObject);

        } else if(opLogical.containsKey(strObject.toUpperCase())) {

            arrayOfTokens.add(new TokenData(opLogical.get(strObject.toUpperCase()), strObject));
            System.out.println(opLogical.get(strObject.toUpperCase()) + " - " + strObject);

        } else if(opUnary.containsKey(strObject.toUpperCase())) {

            arrayOfTokens.add(new TokenData(opUnary.get(strObject.toUpperCase()), strObject));
            System.out.println(opUnary.get(strObject.toUpperCase()) + " - " + strObject);

        } else if(opRelational.containsKey(strObject.toUpperCase())) {

            arrayOfTokens.add(new TokenData(opRelational.get(strObject.toUpperCase()), strObject));
            System.out.println(opRelational.get(strObject.toUpperCase()) + " - " + strObject);

        } else if(booleanLiteral.containsKey(strObject.toUpperCase())) {

            arrayOfTokens.add(new TokenData(booleanLiteral.get(strObject.toUpperCase()), strObject));
            System.out.println(booleanLiteral.get(strObject.toUpperCase()) + " - " + strObject);

        } else if(opArithmetic.containsKey(strObject.toUpperCase())) {

            arrayOfTokens.add(new TokenData(opArithmetic.get(strObject.toUpperCase()), strObject));
            System.out.println(opArithmetic.get(strObject.toUpperCase()) + " - " + strObject);

        } else if(!strObject.contains(".")) {
            /*
                If the unrecognized string object DOES NOT contain a '.' separator, it is likely NEITHER a print nor casting statement.
                So the string object could be a class name, method name, variable identifier.
            */

            /* The string object is within a non main method */
            if(insideOfNonMainMethod && !insideOfMainMethod) {

                /*
                   If the string object is not 'f' that is separated by the stream tokenizer from
                   a number of float type, ex: 45.87f, recognize the string object as a variable identifier.

                   If the string object is 'f', skip over this object (by not using an else statement)
                   because Python already recognized the accompanying number as a float type.
                */
                if(!strObject.equals("f")) {

                    // check for long var = 0L
                    if(checkLongSpecificationStatement[0] == "T_LONG" && checkLongSpecificationStatement[1] == "VAR_IDENTIFIER" &&
                            checkLongSpecificationStatement[2] == "T_ASSIGN" && checkLongSpecificationStatement[3] == "NUMBER") {

                        arrayOfTokens.add(new TokenData("long specification", strObject));
                        System.out.println("long specification" + " - " + strObject);

                        for(int count = 0; count < checkLongSpecificationStatement.length; count++) {
                            checkLongSpecificationStatement[count] = "";
                        }

                    } else {

                        /*
                            Stream tokenizer does not separate variable identifier followed by two arithmetic '-'
                            operators. Ex: x-- is not separated from the entire thing being a var identifier even
                            though there are two '-' (for decrementing) after the var ident. So we have to treat
                            this as a special case and loop through strObject as a charArray and check the result
                            string '--' decrement and recognize them as operator arithmetic symbols from the hash map.

                            Otherwise, the variable identifier is normal and assume it has already been isolated
                            by the stream tokenizer.
                         */

                        String result = "";

                        char[] charArray = strObject.toCharArray();

                        for(int ct = 1; ct < charArray.length; ct++) {
                            result += charArray[ct];
                        }

                        if(result.equals("--")) {

                            arrayOfTokens.add(new TokenData("VAR_IDENTIFIER", strObject.substring(0, 1)));
                            System.out.println("VAR_IDENTIFIER" + " - " + strObject.substring(0, 1));

                            checkLongSpecificationStatement[1] = "VAR_IDENTIFIER";

                            if (!listOfVariables.contains(strObject.substring(0, 1)))
                                listOfVariables.add(strObject.substring(0, 1));

                            arrayOfTokens.add(new TokenData(opArithmetic.get(strObject.substring(1, 2)), strObject.substring(1, 2)));
                            System.out.println(opArithmetic.get(strObject.substring(1, 2)) + " - " + strObject.substring(1, 2));

                            arrayOfTokens.add(new TokenData(opArithmetic.get(strObject.substring(2, 3)), strObject.substring(2, 3)));
                            System.out.println(opArithmetic.get(strObject.substring(2, 3)) + " - " + strObject.substring(2, 3));

                        } else {

                            arrayOfTokens.add(new TokenData("VAR_IDENTIFIER", strObject));
                            System.out.println("VAR_IDENTIFIER" + " - " + strObject);

                            checkLongSpecificationStatement[1] = "VAR_IDENTIFIER";

                            if (!listOfVariables.contains(strObject))
                                listOfVariables.add(strObject);

                        }

                    }

                }

            } else if(classBool && publicBool) {

                // Public class [class name]...

                arrayOfTokens.add(new TokenData("CLASS_NAME", strObject));
                System.out.println("CLASS_NAME" + " - " + strObject);

                classBool = false;
                publicBool = false;

                insideOfClass = true;

            } else {

            	if(prevTokenTwo.equals("int") || prevTokenTwo.equals("short") || prevTokenTwo.equals("long") || prevTokenTwo.equals("double") || prevTokenTwo.equals("String")
            			|| prevTokenTwo.equals("float") || prevTokenTwo.equals("char")) {
            		arrayOfTokens.add(new TokenData("VAR_IDENTIFIER", strObject));
                    System.out.println("VAR_IDENTIFIER" + " - " + strObject);
            	}
            	else {
            		arrayOfTokens.add(new TokenData("METHOD_NAME", strObject));
                    System.out.println("METHOD_NAME" + " - " + strObject);
            	}
            		
                

            }

        } else {

            /* If the unrecognized string object DOES contain a '.' separator, it is likely EITHER a print or casting statement */

        	char[] temp = strObject.toCharArray();
        	
        	for(int i = 0; i < temp.length; i++)
        	{
        		
        		char c = temp[i];
        	
            //for(char c : strObject.toCharArray()) {

                /* If the char separator is likely '.', evaluate the previous token that comes before '.' that should belong to a cast
                    or print statement. Ex: System., out., Integer.
                 */
                if(separator.containsKey(String.valueOf(c))) {

                    if(prevToken.equals("System")) {
                        arrayOfTokens.add(new TokenData("System", prevToken));
                        System.out.println("System" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("out")) {
                        arrayOfTokens.add(new TokenData("out", prevToken));
                        System.out.println("out" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("String")) {
                        arrayOfTokens.add(new TokenData("String Cast", prevToken));
                        System.out.println("String Cast" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("Integer")) {
                        arrayOfTokens.add(new TokenData("Integer Cast", prevToken));
                        System.out.println("Integer Cast" + " - " + prevToken);
                        prevToken = "";
                    } else if(listOfVariables.contains(prevToken)){
                        arrayOfTokens.add(new TokenData("VAR_IDENTIFIER", prevToken));
                        System.out.println("VAR_IDENTIFIER - " + prevToken);
                        prevToken = "";
                    }//Check for math keyword before . separator
                    else if(prevToken.equals("Math")) {
                        arrayOfTokens.add(new TokenData("Math", prevToken));
                        System.out.println("Math" + "-" + prevToken);
                        prevToken = "";
                    }

                    arrayOfTokens.add(new TokenData(separator.get(String.valueOf(c)), String.valueOf(c)));
                    System.out.println(separator.get(String.valueOf(c)) + " - " + c);

                } else {

                    /*
                        Create a valid token from successive characters that either that belongs to a print
                        or cast statement to be including in the array of tokens
                     */
                	prevToken += c;
                    char nextChar = 0;
                    if((i + 1) < temp.length)
                    {
                    	nextChar = temp[i+1];
                    }
                	

                    // Whatever string occurs after the '.' separator (ex: .println, .toString, .format)
                    if(prevToken.equals("println")) {
                        arrayOfTokens.add(new TokenData("println", prevToken));
                        System.out.println("println" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("toString")) {
                        arrayOfTokens.add(new TokenData("toString method", prevToken));
                        System.out.println("toString method" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("valueOf")) {
                        arrayOfTokens.add(new TokenData("valueOf method", prevToken));
                        System.out.println("valueOf method" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("format")) {
                        arrayOfTokens.add(new TokenData("String Format", prevToken));
                        System.out.println("String Format" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("equals")){
                        arrayOfTokens.add(new TokenData("Equals Method", prevToken));
                        System.out.println("Equals method - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("length")) {
                        arrayOfTokens.add(new TokenData("length", prevToken));
                        System.out.println("length - " + prevToken);
                        prevToken = "";
                    }

                    //Check for math keywords after the . seperator
                    else if(prevToken.equals("abs")) {
                        arrayOfTokens.add(new TokenData("abs", prevToken));
                        System.out.println("abs" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("min")) {
                        arrayOfTokens.add(new TokenData("min", prevToken));
                        System.out.println("min" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("max")) {
                        arrayOfTokens.add(new TokenData("max", prevToken));
                        System.out.println("max" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("pow")) {
                        arrayOfTokens.add(new TokenData("pow", prevToken));
                        System.out.println("pow" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("acos")) {
                        arrayOfTokens.add(new TokenData("acos", prevToken));
                        System.out.println("acos" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("asin")) {
                        arrayOfTokens.add(new TokenData("asin", prevToken));
                        System.out.println("asin" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("atan") && nextChar != '2') {
                        arrayOfTokens.add(new TokenData("atan", prevToken));
                        System.out.println("atan" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("atan2")) {
                        arrayOfTokens.add(new TokenData("atan2", prevToken));
                        System.out.println("atan2" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("cos") && nextChar != 'h') {
                        arrayOfTokens.add(new TokenData("cos", prevToken));
                        System.out.println("cos" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("cosh")) {
                        arrayOfTokens.add(new TokenData("cosh", prevToken));
                        System.out.println("cosh" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("exp")) {
                        arrayOfTokens.add(new TokenData("exp", prevToken));
                        System.out.println("exp" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("log") && nextChar != '1') {
                        arrayOfTokens.add(new TokenData("log", prevToken));
                        System.out.println("log" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("log10")) {
                        arrayOfTokens.add(new TokenData("log10", prevToken));
                        System.out.println("log10" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("sin") && nextChar != 'h') {
                        arrayOfTokens.add(new TokenData("sin", prevToken));
                        System.out.println("sin" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("sinh")) {
                        arrayOfTokens.add(new TokenData("sinh", prevToken));
                        System.out.println("sinh" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("sqrt")) {
                        arrayOfTokens.add(new TokenData("sqrt", prevToken));
                        System.out.println("sqrt" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("tan") && nextChar != 'h') {
                        arrayOfTokens.add(new TokenData("tan", prevToken));
                        System.out.println("tan" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("tanh")) {
                        arrayOfTokens.add(new TokenData("tanh", prevToken));
                        System.out.println("tanh" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("toDegrees")) {
                        arrayOfTokens.add(new TokenData("toDegrees", prevToken));
                        System.out.println("toDegrees" + " - " + prevToken);
                        prevToken = "";
                    } else if(prevToken.equals("toRadians")) {
                        arrayOfTokens.add(new TokenData("toRadians", prevToken));
                        System.out.println("toRadians" + " - " + prevToken);
                        prevToken = "";
                    }

                }

            }
        }
        prevTokenTwo = strObject;
    }
    
}

