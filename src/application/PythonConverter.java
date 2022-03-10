package application;
import java.util.*;

public class PythonConverter {

    // ------------------------------------------------------
    //                    VARIABLES
    // ------------------------------------------------------

    static ArrayList<String> methodCollection = new ArrayList<String>();

    static String[] statementArr = new String[3];
    static String[] equalOpStatement = new String[2];
    static String[] arrInitAndDeclaration = new String[5];
    static String[] arrDecAndAllocateMemory = new String[10];

    public static String resultPythonStr = "";
    static String pythonStr = "";
    static String currentMethodTkn = "";
    static String checkPrintStatement = "";
    static String intCastStr = "";
    static String strCastStr = "";
    static String strCastFormat = "";

    static int count = 0;

    static boolean checkEqualOperator = false;
    static boolean checkNotEqual = false;
    static boolean methodWithNoArgs = false;
    static boolean classContainsMain = false;
    static boolean classContainsOtherMethods = false;
    static boolean skipMain = false;
    static boolean classOnlyHasMain = false;
    static boolean casting = false;
    //static boolean ifStatement = false;
    //If file needs to import python math module
    static boolean needsMathImport = false;
    //Need this variable so math statements aren't identified as casting statements when also declaring a variable on the same line
    static boolean isAMathMethodDoNotCastIt = false;

    static boolean isArrayStatementAddRbracket = false;
    static boolean isArrDecMemAllocStatement = false;

    static int skipParenthesis = 0;
    static boolean endOfDo = false;
    static boolean doWhileLoop = false;

    private static ArrayList<String> tokenList = new ArrayList<String>();
    private static ArrayList<String> lexemeList = new ArrayList<String>();
    private static ArrayList<Integer> indices = new ArrayList<Integer>();

    public static ArrayList<String> listOfVariables = new ArrayList<>();
    public static ArrayList<TokenData> doWhileCondition = new ArrayList<>();



    // --------------------------------------------------------------------------------
    //          PRIMARY FUNCTIONS - GENERAL
    // --------------------------------------------------------------------------------

    public static void deletePyStr(ArrayList<TokenData> list) {

        list.clear();

        methodCollection.clear();

        for(int i1 = 0; i1 < statementArr.length; i1++) {
            statementArr[i1] = "";
        }

        for(int i2 = 0; i2 < equalOpStatement.length; i2++) {
            equalOpStatement[i2] = "";
        }

        for(int i3 = 0; i3 < arrInitAndDeclaration.length; i3++) {
            arrInitAndDeclaration[i3] = "";
        }

        for(int i4 = 0; i4 < arrDecAndAllocateMemory.length; i4++) {
            arrDecAndAllocateMemory[i4] = "";
        }

        resultPythonStr = "";
        pythonStr = "";
        currentMethodTkn = "";
        checkPrintStatement = "";
        intCastStr = "";
        strCastStr = "";
        strCastFormat = "";

        count = 0;

        checkEqualOperator = false;
        checkNotEqual = false;
        methodWithNoArgs = false;
        classContainsMain = false;
        classContainsOtherMethods = false;
        skipMain = false;
        classOnlyHasMain = false;
        casting = false;

        needsMathImport = false;
        isAMathMethodDoNotCastIt = false;

        isArrayStatementAddRbracket = false;
        isArrDecMemAllocStatement = false;

        skipParenthesis = 0;
        endOfDo = false;
        doWhileLoop = false;

        //ifStatement = false;

        tokenList.clear();
        lexemeList.clear();
        indices.clear();
        doWhileCondition.clear();

    }

    //Function called at the end to add to result string that outputs Python
    public static String onlyMainMethodOrOtherMethods(boolean x, boolean y) {

        String resultStr = "";

        resultStr += "\t";
        resultStr += "if __name__ == " + '"' + "__main__" + '"' + ":";
        resultStr += "\n\t\t";

        if(x && y) {

            for(int indx = 0; indx < methodCollection.size(); indx++) {
                if(methodCollection.get(indx) != "main") {
                    resultStr += "print(" + methodCollection.get(indx) + "(self))" + "\n\t\t";
                }

            }

        } else if(x && !y) {

            resultStr += methodCollection.get(0) + "(self)";

        }

        return resultStr;
    }

    //Look ahead to know if the class contains only a main methods or others in addition
    public static void lookAheadFunction(ArrayList<TokenData> list) {

        for(int idx = 0; idx < list.size(); idx++) {

            if(list.get(idx).token == "METHOD_NAME") {
                classContainsOtherMethods = true;
            }

            if(list.get(idx).token == "MAIN_IDENTIFIER") {
                classContainsMain = true;
            }
        }
    }

    //Method for determining if checkprintstatement is a math statement to avoid adding :
    public static boolean isMathStatement(String substring)
    {
        if (substring.equals("Math.abs"))
            return true;
        else if (substring.equals("Math.min"))
            return true;
        else if (substring.equals("Math.max"))
            return true;
        else if (substring.equals("Math.pow"))
            return true;
        else if (substring.equals("Math.acos"))
            return true;
        else if (substring.equals("Math.asin"))
            return true;
        else if (substring.equals("Math.atan"))
            return true;
        else if (substring.equals("Math.atan2"))
            return true;
        else if (substring.equals("Math.cos"))
            return true;
        else if (substring.equals("Math.cosh"))
            return true;
        else if (substring.equals("Math.exp"))
            return true;
        else if (substring.equals("Math.log"))
            return true;
        else if (substring.equals("Math.log10"))
            return true;
        else if (substring.equals("Math.sin"))
            return true;
        else if (substring.equals("Math.sinh"))
            return true;
        else if (substring.equals("Math.sqrt"))
            return true;
        else if (substring.equals("Math.tan"))
            return true;
        else if (substring.equals("Math.tanh"))
            return true;
        else if (substring.equals("Math.toRadians"))
            return true;
        else if (substring.equals("Math.Degrees"))
            return true;
        else
            return false;
    }

    public static void translateDriver(ArrayList<TokenData> list) {

        System.out.println();

        ArrayList<TokenData> tempData = new ArrayList<TokenData>();

        for(int i = 0; i < list.size(); i++) {

            switch(list.get(i).token) {

                case "T_PUBLIC":

                    continue;

                case "T_CLASS":

                    pythonStr += list.get(i).lexeme + " ";
                    break;

                case "CLASS_NAME":

                    pythonStr += list.get(i).lexeme + ":";
                    break;

                case "METHOD_NAME":

                    if(skipMain) {

                        currentMethodTkn = list.get(i).token;

                    } else {

                        pythonStr += "def " + list.get(i).lexeme;

                        currentMethodTkn = list.get(i).token;
                        methodCollection.add(list.get(i).lexeme);

                    }

                    break;

                case "T_LBRACE":

                    boolean isArrayStatement = false;

                    if((arrInitAndDeclaration[0] == "T_INT" || arrInitAndDeclaration[0] == "String Identifier") && arrInitAndDeclaration[1] == "T_LBRACKET"
                        && arrInitAndDeclaration[2] == "T_RBRACKET" && arrInitAndDeclaration[3] == "VAR_IDENTIFIER" && arrInitAndDeclaration[4] == "T_ASSIGN"
                            && ((list.get(i+1).token == "NUMBER" && list.get(i+2).token == "T_COMMA") || list.get(i+1).token == "T_RBRACE")) {

                        isArrayStatement = true;
                        isArrayStatementAddRbracket = true;

                        for(int clrArr = 0; clrArr < arrInitAndDeclaration.length; clrArr++) {
                            arrInitAndDeclaration[clrArr] = "";
                        }

                    } else {

                        isArrayStatement = false;
                    }

                    if(count >= 2 && isArrayStatement == false) {
                        pythonStr += ":\n";
                    } else if(count >= 2 && isArrayStatement == true) {
                        pythonStr += "[";
                        count++;
                        break;
                    } else {
                        pythonStr += "\n";
                    }

                    //determines how many times a statement will be indented based on its syntactic position in the code
                    count++;

                    //tabbing or indenting the statement how many # of times to get the correct python syntax
                    for(int j = 0; j < count; j++) {
                        pythonStr += "\t";
                    }

                    //After class declaration statement
                    if(count == 1) {
                        pythonStr += "def self(args):" + "\n";
                        pythonStr += "\t\t" + "pass\n\n\t";
                    }

                    break;

                case "T_RBRACE":

                    if(isArrayStatementAddRbracket) {
                        pythonStr += "]";
                        isArrayStatementAddRbracket = false;
                    }

                    pythonStr += "\n";

                    //We have reached the end of the current function. We now need less indentation for a new function declaration.
                    //Once we reach the left brace of the new function, make indentation based on reduced value of this 'count'.
                    count--;

                    for(int j = 0; j < count; j++) {
                        pythonStr += "\t";
                    }


                    methodWithNoArgs = false;
                    skipMain = false;

                    break;

                case "T_LBRACKET":

                    if(isArrDecMemAllocStatement) {

                        arrDecAndAllocateMemory[7] = list.get(i).token;
                        pythonStr += list.get(i).lexeme;
                        break;

                    }

                    arrInitAndDeclaration[1] = list.get(i).token;
                    arrDecAndAllocateMemory[1] = list.get(i).token;

                    break;

                case "T_RBRACKET":

                    if(isArrDecMemAllocStatement) {

                        // var = [""] * length of the array
                        if(arrDecAndAllocateMemory[6] == "String Identifier") {
                            pythonStr += "\"" + "\"" + "]*" + arrDecAndAllocateMemory[8];
                        }

                        // var = [0] * length of the array
                        if(arrDecAndAllocateMemory[6] == "T_INT") {
                            pythonStr += "0" + "]*" + arrDecAndAllocateMemory[8];
                        }

                        break;

                    }

                    arrInitAndDeclaration[2] = list.get(i).token;
                    arrDecAndAllocateMemory[2] = list.get(i).token;

                    break;

                case "T_STATIC":

                    continue;

                case "T_VOID":

                    continue;

                case "MAIN_IDENTIFIER":

                    currentMethodTkn = list.get(i).token;

                    //Execute if statement only if the class only contains a main method
                    if(!(classContainsMain && classContainsOtherMethods)){

                        pythonStr += "def " + list.get(i).lexeme;
                        methodCollection.add(list.get(i).lexeme);

                    }

                    break;

                case "T_LPARENTH":

                    // double var =
                    if(statementArr[0] == "T_DOUBLE" && statementArr[1] == "VAR_IDENTIFIER" && statementArr[2] == "T_ASSIGN" && !isAMathMethodDoNotCastIt) {

                        //doubles casted to float in python
                        pythonStr += "float(";

                        casting = true;

                        for(int a = 0; a < statementArr.length; a++) {
                            statementArr[a] = "";
                        }

                        break;
                    }

                    // int var =
                    if(statementArr[0] == "T_INT" && statementArr[1] == "VAR_IDENTIFIER" && statementArr[2] == "T_ASSIGN" && !isAMathMethodDoNotCastIt) {

                        pythonStr += "int(";

                        for(int b = 0; b < statementArr.length; b++) {
                            statementArr[b] = "";
                        }

                        casting = true;

                        break;

                    }

                    // float var =
                    if(statementArr[0] == "T_FLOAT" && statementArr[1] == "VAR_IDENTIFIER" && statementArr[2] == "T_ASSIGN" && !isAMathMethodDoNotCastIt) {


                        pythonStr += "";

                        for(int c = 0; c < statementArr.length; c++) {
                            statementArr[c] = "";
                        }

                        casting = true;

                        break;
                    }

                    // long var =
                    if(statementArr[0] == "T_LONG" && statementArr[1] == "VAR_IDENTIFIER" && statementArr[2] == "T_ASSIGN" && !isAMathMethodDoNotCastIt) {


                        pythonStr += "";

                        for(int d = 0; d < statementArr.length; d++) {
                            statementArr[d] = "";
                        }

                        casting = true;

                        break;
                    }

                    // byte var =
                    if(statementArr[0] == "T_BYTE" && statementArr[1] == "VAR_IDENTIFIER" && statementArr[2] == "T_ASSIGN" && !isAMathMethodDoNotCastIt) {

                        //treat a float in Python the same way in Java
                        pythonStr += "";

                        for(int e = 0; e < statementArr.length; e++) {
                            statementArr[e] = "";
                        }

                        casting = true;

                        break;

                    }

                    // String var = Integer.toString, or String var = String.valueOf, String var = String.format
                    if(statementArr[0] == "String Identifier" && statementArr[1] == "VAR_IDENTIFIER" && statementArr[2] == "T_ASSIGN"
                            && (intCastStr.equals("Integer.toString") || strCastStr.equals("String.valueOf") || strCastFormat.equals("String.format"))) {

                        pythonStr += "str(";

                        for(int d = 0; d < statementArr.length; d++) {
                            statementArr[d] = "";
                        }

                        casting = true;

                        intCastStr = "";
                        strCastStr = "";
                        strCastFormat = "";

                        break;

                    }

                    //If the class only contains a main method, we are directly converting the Java main method
                    //to a Python main method
                    if(currentMethodTkn == "MAIN_IDENTIFIER" && classContainsMain && !classContainsOtherMethods) {

                        classOnlyHasMain = true;

                    }

                    //Ignore Java main method conversion to a Python main method in a class with other methods
                    //because in Python, we are not calling other methods inside of the main method
                    if(currentMethodTkn == "MAIN_IDENTIFIER" && classContainsMain && classContainsOtherMethods) {

                        //so skip over or ignore the main method conversion in this class
                        skipMain = true;

                        break;

                    }

                    //In a class the has both main method and other methods, the other methods may contain
                    //empty parameters or filled parameters
                    if(currentMethodTkn == "METHOD_NAME" && classContainsMain && classContainsOtherMethods) {

                        // ex: funcName()
                        if(list.get(i+1).token == "T_RPARENTH") {

                            //if the class has both a main method and other methods, skip conversion of funcName
                            //method calls found in the main method of Java to Python, because we are also skipping
                            //the main method conversion in this class
                            if(skipMain) {

                                break;

                            } else {

                                //otherwise in a class with only a main method, this is just a function call
                                //not inside a main method invoking it. So we convert this funcName from
                                //Java to Python
                                methodWithNoArgs = true;

                            }

                        } else {

                            //If the parameter of the current method contains arguments
                            methodWithNoArgs = false;

                        }
                    }

                    if(list.get(i-1).lexeme.equals("if") || list.get(i-1).lexeme.equals("equals") || list.get(i-1).lexeme.equals("while")){
                        skipParenthesis += 1;
                        break;
                    }
                    //if(!ifStatement){
                    pythonStr += list.get(i).lexeme;

                    //}

                    //pythonStr += list.get(i).lexeme;

                    break;

                case "STRING_IDENTIFIER":

                    if(isArrDecMemAllocStatement) {

                        arrDecAndAllocateMemory[6] = "String Identifier";
                        break;
                    }

                    statementArr[0] = "String Identifier";
                    arrInitAndDeclaration[0] = "String Identifier";
                    arrDecAndAllocateMemory[0] = "String Identifier";

                    break;

                case "ARGS_IDENTIFIER":

                    if(skipMain) {
                        break;
                    } else if(classOnlyHasMain) {
                        pythonStr += "self";
                        break;
                    } else {
                        pythonStr += list.get(i).lexeme;
                        break;
                    }

                case "T_RPARENTH":

                    if(skipParenthesis!=0){
                        skipParenthesis -=1;
                        break;
                    }

                    if(casting) {

                        //finished casting the current statement
                        casting = false;
                        break;

                    } else if (isAMathMethodDoNotCastIt) {
                        //math statement is translated and no longer subject to being misinterpreted
                        isAMathMethodDoNotCastIt = false;
                        pythonStr += list.get(i).lexeme;
                        break;
                    } else if(methodWithNoArgs) {

                        //need 'self' parameter in Python
                        pythonStr += "self";
                        pythonStr += list.get(i).lexeme + ":";

                        currentMethodTkn = "";
                        methodWithNoArgs = false;

                        break;

                    } else if(skipMain) {

                        break;

                    } else if(classOnlyHasMain && !checkPrintStatement.equals("System.out.println")
                            && !isMathStatement(checkPrintStatement) && pythonStr.substring(pythonStr.length()-4).equals("self")) {

                        pythonStr += list.get(i).lexeme + ":";
                        break;

                    } else if(classOnlyHasMain && !checkPrintStatement.equals("System.out.println") && !isMathStatement(checkPrintStatement)
                            && !pythonStr.substring(pythonStr.length()-4).equals("self") || isMathStatement(checkPrintStatement)) {

                        pythonStr += list.get(i).lexeme;
                        break;

                    }

                    else {

                        pythonStr += list.get(i).lexeme;
                        currentMethodTkn = "";

                        break;

                    }

                case "System":

                    checkPrintStatement += list.get(i).lexeme;
                    break;

                case "T_DOT":

                    if(intCastStr.equals("Integer")) {
                        intCastStr += ".";
                    }

                    if(strCastStr.equals("String")) {
                        strCastStr += ".";
                        strCastFormat += ".";
                    }

                    checkPrintStatement += list.get(i).lexeme;

                    break;

                case "out":

                    checkPrintStatement += list.get(i).lexeme;

                    break;

                case "println":

                    checkPrintStatement += list.get(i).lexeme;

                    if(checkPrintStatement.equals("System.out.println")) {
                        pythonStr += "print";
                    }

                    //Properly reset this variable once println is added to the pythonStr
                    checkPrintStatement = "";

                    break;

                case "string literal":

                    String temp = list.get(i).lexeme;

                    // If the string literal is format specifier "%f"
                    if(casting && temp.length() == 4 && temp.charAt(1) == '%' && temp.charAt(2) == 'f') {
                        break;
                    } else {
                        pythonStr += list.get(i).lexeme;
                        break;
                    }

                case "T_SEMICOLON":


                    // Reset all statement evaluation arrays after every statement or semicolon to prevent array
                    // value carryover when analyzing the next statement in the function.
                    for(int clrStatementArr = 0; clrStatementArr < statementArr.length; clrStatementArr++) {
                        statementArr[clrStatementArr] = "";
                    }

                    for(int clrEqualOpSt = 0; clrEqualOpSt < equalOpStatement.length; clrEqualOpSt++) {
                        equalOpStatement[clrEqualOpSt] = "";
                    }

                    for(int clrArrInitAndDec = 0; clrArrInitAndDec < arrInitAndDeclaration.length; clrArrInitAndDec++) {
                        arrInitAndDeclaration[clrArrInitAndDec] = "";
                    }

                    for(int clrDecAllMem = 0; clrDecAllMem < arrDecAndAllocateMemory.length; clrDecAllMem++) {
                        arrDecAndAllocateMemory[clrDecAllMem] = "";
                    }

                    isArrDecMemAllocStatement = false;

                    /*---------------------------------------------------------------------------------------*/

                    pythonStr += "\n";

                    //Determine syntax of python output by tabbing new lines
                    for(int p = 0; p < count; p++) {
                        pythonStr += "\t";
                    }

                    checkPrintStatement = "";

                    break;

                case "T_INT":

                    if(isArrDecMemAllocStatement) {

                        arrDecAndAllocateMemory[6] = "T_INT";
                        break;
                    }

                    statementArr[0] = "T_INT";
                    arrInitAndDeclaration[0] = "T_INT";
                    arrDecAndAllocateMemory[0] = "T_INT";

                    break;

                case "T_DOUBLE":

                    statementArr[0] = "T_DOUBLE";
                    break;

                case "T_FLOAT":

                    statementArr[0] = "T_FLOAT";
                    break;

                case "T_SHORT":

                    statementArr[0] = "T_SHORT";
                    break;

                case "T_LONG":

                    statementArr[0] = "T_LONG";
                    break;

                case "T_BYTE":

                    statementArr[0] = "T_BYTE";
                    break;

                case "String Cast":

                    strCastStr += "String";
                    strCastFormat += "String";
                    break;

                case "Integer Cast":

                    intCastStr += "Integer";
                    break;

                case "toString method":

                    intCastStr += "toString";
                    break;

                case "valueOf method":

                    strCastStr += "valueOf";
                    break;

                case "String Format":

                    strCastFormat += "format";
                    break;

                case "VAR_IDENTIFIER":

                    /*
                        Analyze the last six characters or four characters in the collected python string "pythonStr"
                        to determine if the statement is casting the variable identifier.

                        If only 'print(' is occurring before the variable identifier, it is not casting and ensures
                        an additional enclosing ')' is not appended to pythonStr because a bug initially produced
                        'print(..))' instead of the correct 'print(..) output.
                    */

                    String lastSixChars = "";
                    String lastFourChars = "";

                    int lenOfLastSixChars = 6;
                    int lenOfLastFourChars = 4;

                    int pyStrIndexLen = pythonStr.length() - 1;

                    int counter1 = (pyStrIndexLen - lenOfLastSixChars) + 1;
                    int counter2 = (pyStrIndexLen - lenOfLastFourChars) + 1;

                    while(counter1 < pyStrIndexLen) {
                        lastSixChars += pythonStr.charAt(counter1);
                        counter1++;
                    }
                    lastSixChars += pythonStr.charAt(pyStrIndexLen);

                    while(counter2 < pyStrIndexLen) {
                        lastFourChars += pythonStr.charAt(counter2);
                        counter2++;
                    }
                    lastFourChars += pythonStr.charAt(pyStrIndexLen);

                    if(lastSixChars.equals("float(")) {
                        statementArr[1] = "VAR_IDENTIFIER";
                        pythonStr += list.get(i).lexeme + ")";
                        break;
                    } else if(lastFourChars.equals("int(") && lastSixChars.equals("print(")) { //prevents the 'print(..))' bug

                        if(list.get(i+1).token.equals("T_DOT") && list.get(i+2).token.equals("length")) {
                            pythonStr += "len(" + list.get(i).lexeme + ")";
                        } else {
                            statementArr[1] = "VAR_IDENTIFIER";
                            pythonStr += list.get(i).lexeme;
                        }

                        break;

                    } else if(lastFourChars.equals("int(") && !lastSixChars.equals("print(")) {
                        statementArr[1] = "VAR_IDENTIFIER";
                        pythonStr += list.get(i).lexeme + ")";
                        break;
                    } else if(lastFourChars.equals("str(")) {
                        statementArr[1] = "VAR_IDENTIFIER";
                        pythonStr += list.get(i).lexeme + ")";
                        break;
                    } else {
                        statementArr[1] = "VAR_IDENTIFIER";
                        equalOpStatement[0] = "VAR_IDENTIFIER";
                        arrInitAndDeclaration[3] = "VAR_IDENTIFIER";

                        arrDecAndAllocateMemory[3] = "VAR_IDENTIFIER";

                        pythonStr += list.get(i).lexeme;

                        break;
                    }

                case "T_COMMA":

                    if(casting) {
                        continue;
                    } else {
                        pythonStr += list.get(i).lexeme + " ";
                        break;
                    }

                case "T_ASSIGN":

                    /* Check for the equal operator */
                    if(list.get(i+1).lexeme.equals("=")) {

                        checkEqualOperator = true;
                        break;

                    } else if(checkEqualOperator) {

                        pythonStr += " == ";

                        equalOpStatement[1] = "Equal Operator";
                        checkEqualOperator = false;

                        break;

                    } else if(checkNotEqual) {

                        //pythonStr += " != ";
                        pythonStr += list.get(i).lexeme + " ";
                        checkNotEqual = false;

                        break;

                    } else {

                        pythonStr += " " + list.get(i).lexeme + " ";

                        statementArr[2] = "T_ASSIGN";
                        arrInitAndDeclaration[4] = "T_ASSIGN";
                        arrDecAndAllocateMemory[4]= "T_ASSIGN";

                        break;

                    }


                case "NUMBER":

                    if(isArrDecMemAllocStatement) {

                        String num = list.get(i).lexeme;

                        num = num.substring(0, num.length() -1);
                        num = num.substring(0, num.length() -1);

                        arrDecAndAllocateMemory[8] = num;

                        break;
                    }

                    if(isArrayStatementAddRbracket) {

                        String num = list.get(i).lexeme;

                        num = num.substring(0, num.length() -1);
                        num = num.substring(0, num.length() -1);

                        pythonStr += num;

                        break;

                    }

                    if(list.get(i - 2).token.equals("VAR_IDENTIFIER") && list.get(i - 1).lexeme.equals("[")
                        && list.get(i + 1).lexeme.equals("]") && list.get(i + 2).token.equals("T_ASSIGN")) {

                            pythonStr += "[";

                            String num = list.get(i).lexeme;

                            num = num.substring(0, num.length() -1);
                            num = num.substring(0, num.length() -1);

                            pythonStr += num;

                            pythonStr += "]";

                            break;
                    }

                    /*
                       The stream tokenizer seems to produce numbers ending in '.0' if they are whole (ex: 2045 -> 2045.0).
                       So we need a way to ensure an incoming whole number does not end in '.0' if it is assigned
                       to a variable of type integer of short in the statement.
                    */

                    //If 'int x =', 'short x =', or 'byte x ='
                    if((statementArr[0] == "T_INT" || statementArr[0] == "T_SHORT" || statementArr[0] == "T_BYTE") && statementArr[1] == "VAR_IDENTIFIER"
                            && statementArr[2] == "T_ASSIGN") {

                        String num = list.get(i).lexeme;

                        // Remove the last two characters in the number if its assigned variable is type short or int
                        if(num.charAt(num.length()-2) == '.' && num.charAt(num.length()-1) == '0') {

                            num = num.substring(0, num.length() -1);
                            num = num.substring(0, num.length() -1);

                            pythonStr += num + " ";

                            for(int q = 0; q < statementArr.length; q++) {
                                statementArr[q] = "";
                            }

                            break;

                        } else {

                            pythonStr += list.get(i).lexeme + " ";
                            break;

                        }


                    } else if(equalOpStatement[0] == "VAR_IDENTIFIER" && equalOpStatement[1] == "Equal Operator") {

                        String num = list.get(i).lexeme;

                        // Remove the last two characters in the number if its assigned variable is type short or int
                        if(num.charAt(num.length()-2) == '.' && num.charAt(num.length()-1) == '0') {

                            num = num.substring(0, num.length() -1);
                            num = num.substring(0, num.length() -1);

                            pythonStr += num;

                            for(int r = 0; r < equalOpStatement.length; r++) {
                                equalOpStatement[r] = "";
                            }

                            break;

                        }

                    } else if(statementArr[0] == "T_LONG" && statementArr[1] == "VAR_IDENTIFIER"
                            && statementArr[2] == "T_ASSIGN" && list.get(i+1).lexeme.equals("L")) {

                        String numStr = list.get(i).lexeme.replace(".", "");

                        char[] charDigits = new char[numStr.length()];

                        for(int count = 0; count < numStr.length(); count++) {
                            charDigits[count] = numStr.charAt(count);
                        }

                        int count1 = 0;
                        String resultNum = "";

                        while(charDigits[count1] != 'E') {

                            resultNum += charDigits[count1];
                            count1++;
                        }

                        pythonStr += resultNum + " ";

                        for(int s = 0; s < statementArr.length; s++) {
                            statementArr[s] = "";
                        }

                        break;

                    }

                    else {

                        //Remove the decimal from numbers whose decimal is 0 but is not caught in the above methods because it is not a variable declaration
                        try {
                            if(Double.valueOf(list.get(i).lexeme) - Double.valueOf(list.get(i).lexeme).intValue() < 0.000000000000001) {
                                pythonStr += Double.valueOf(list.get(i).lexeme).intValue();
                                break;
                            }
                        }
                        catch(Exception e){
                        }

                        pythonStr += list.get(i).lexeme + " ";
                        break;

                    }

                case "logical operator":

                    // Modified Logical Operator Functionality
                    list.set(i, handleLogicalOperator(list, list.get(i), i));
                    pythonStr += " " + list.get(i).lexeme;
                    break;

                case "unary operator":

                    // Modified Unary Operator Functionality
                    //handleUnaryOperator(list, list.get(i).lexeme, i);
                    break;

                case "relational operator":

                    if(list.get(i).lexeme.equals("<") || list.get(i).lexeme.equals(">")) {
                        pythonStr += " " + list.get(i).lexeme + " ";
                        break;
                    }

                case "arithmetic operator":

                    if(list.get(i).lexeme.equals("+") && list.get(i+1).lexeme.equals("+") && list.get(i+2).token.equals("T_SEMICOLON")) {
                        handleUnaryOperator(list, "++", i);
                        pythonStr += list.get(i).lexeme.substring(1);
                        break;
                    } else if(list.get(i).lexeme.equals("-") && list.get(i+1).lexeme.equals("-") && list.get(i+2).token.equals("T_SEMICOLON")) {
                        handleUnaryOperator(list, "--", i);
                        pythonStr += list.get(i).lexeme.substring(1);
                        break;

                        // Have not found the proper way to use handleUnaryOperator() for pre-increments and pre-decrements due to how the unary
                        // functions alter the original list of TokenData. This implementation should work for now.
                    } else if(list.get(i).lexeme.equals("+") && list.get(i+1).lexeme.equals("+") && list.get(i+2).token.equals("VAR_IDENTIFIER")) {
                        pythonStr += list.get(i+2).lexeme + "+=1";
                        list.remove(i+2);
                        break;
                    } else if(list.get(i).lexeme.equals("-") && list.get(i+1).lexeme.equals("-") && list.get(i+2).token.equals("VAR_IDENTIFIER")) {
                        pythonStr += list.get(i+2).lexeme + "-=1";
                        list.remove(i+2);
                        break;
                    } else {
                        break;
                    }

                case "long specification":

                    continue;

                case "boolean literal":

                    if(list.get(i).lexeme.equals("true")) {
                        pythonStr += "True";
                    } else if(list.get(i).lexeme.equals("false")) {
                        pythonStr += "False";
                    }

                    break;

                case "character literal":

                    pythonStr += list.get(i).lexeme;
                    break;

                case "T_IF":
                    //ifStatement = true;
                    pythonStr += "if ";
                    break;

                case "T_ELSE":
                    if(list.get(i+1).lexeme.equals("if")){
                        pythonStr += "elif ";
                        i+=1;
                        break;
                    }
                    pythonStr += "else";
                    break;

                case "Equals Method":
                    pythonStr += " == ";
                    break;

                //Math handling
                case "Math":
                    checkPrintStatement += list.get(i).lexeme;
                    isAMathMethodDoNotCastIt = true;
                    break;
                case "abs":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.abs"))
                        pythonStr += "abs";
                    break;
                case "min":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.min"))
                        pythonStr += "min";
                    break;
                case "max":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.max"))
                        pythonStr += "max";
                    break;
                case "pow":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.pow"))
                        pythonStr += "pow";
                    break;
                case "acos":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.acos"))
                        pythonStr += "math.acos";
                    needsMathImport = true;
                    break;
                case "asin":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.asin"))
                        pythonStr += "math.asin";
                    needsMathImport = true;
                    break;
                case "atan":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.atan"))
                        pythonStr += "math.atan";
                    needsMathImport = true;
                    break;
                case "atan2":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.atan2"))
                        pythonStr += "math.atan2";
                    needsMathImport = true;
                    break;
                case "cos":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.cos"))
                        pythonStr += "math.cos";
                    needsMathImport = true;
                    break;
                case "cosh":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.cosh"))
                        pythonStr += "math.cosh";
                    needsMathImport = true;
                    break;
                case "exp":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.exp"))
                        pythonStr += "math.exp";
                    needsMathImport = true;
                    break;
                case "log":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.log"))
                        pythonStr += "math.log";
                    needsMathImport = true;
                    break;
                case "log10":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.log10"))
                        pythonStr += "math.log10";
                    needsMathImport = true;
                    break;
                case "sin":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.sin"))
                        pythonStr += "math.sin";
                    needsMathImport = true;
                    break;
                case "sinh":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.sinh"))
                        pythonStr += "math.sinh";
                    break;
                case "sqrt":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.sqrt"))
                        pythonStr += "math.sqrt";
                    needsMathImport = true;
                    break;
                case "tan":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.tan"))
                        pythonStr += "math.tan";
                    break;
                case "tanh":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.tanh"))
                        pythonStr += "math.tanh";
                    needsMathImport = true;
                    break;
                case "toRadians":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.toRadians"))
                        pythonStr += "math.radians";
                    needsMathImport = true;
                    break;
                case "toDegrees":
                    checkPrintStatement += list.get(i).lexeme;
                    if(checkPrintStatement.equals("Math.toDegrees"))
                        pythonStr += "math.degrees";
                    needsMathImport = true;
                    break;

                case "T_FOR":
                    pythonStr += "for";
                    break;

                case "T_WHILE":
                    if(doWhileLoop && endOfDo){
                        //Have to add at the end of the loop if condition is true continue, else break to act as the 'do' portion of the loop
                        pythonStr += "\tif";
                        translateDriver(doWhileCondition);
                        pythonStr += ":\n";
                        for(int p = 0; p < count+2; p++) {
                            pythonStr += "\t";
                        }
                        pythonStr += "continue\n";

                        for(int p = 0; p < count+1; p++) {
                            pythonStr += "\t";
                        }
                        pythonStr += "else:\n";

                        for(int p = 0; p < count+2; p++) {
                            pythonStr += "\t";
                        }
                        pythonStr += "break\n";
                        break;
                    }

                    pythonStr += "while";
                    break;

                case "T_DO":
                    pythonStr += "while";

                    int braceCount=0;
                    doWhileLoop = true;
                    TokenData currObject;

                    //Have to find the while with the condition and add it to pythonStr and then skip it once it comes across
                    //later
                    for(int x=i+1; x<list.size(); x++){

                        if(list.get(x).lexeme.equals("{"))
                            braceCount+=1;

                        if(braceCount==0)
                            endOfDo=true;

                        if(braceCount==0 && list.get(x).lexeme.equals(";")){
                            break;
                        }

                        if(braceCount==0 && !list.get(x).lexeme.equals("while") && !list.get(x).lexeme.equals("(") && !list.get(x).lexeme.equals(")")){
                            currObject = list.get(x);
                            doWhileCondition.add(new TokenData(currObject.token, currObject.lexeme));
                            list.get(x).token = "T_SKIP";
                        }

                        if(list.get(x).lexeme.equals("}"))
                            braceCount-=1;

                    }
                    translateDriver(doWhileCondition);

                    break;

                case "T_SKIP":
                    break;

                case "T_NEW":

                    arrDecAndAllocateMemory[5] = "T_NEW";

                    // if String[] var = new or int[] var = new, it is an array declaration and memory allocation statement
                    if((arrDecAndAllocateMemory[0] == "String Identifier" || arrDecAndAllocateMemory[0] == "T_INT")
                        && arrDecAndAllocateMemory[1] == "T_LBRACKET" && arrDecAndAllocateMemory[2] == "T_RBRACKET"
                            && arrDecAndAllocateMemory[3] == "VAR_IDENTIFIER" && arrDecAndAllocateMemory[4] == "T_ASSIGN"
                                && arrDecAndAllocateMemory[5] == "T_NEW") {

                        isArrDecMemAllocStatement = true;

                    }
                    break;

            }

        }

        //Add math import statement if required
        if(needsMathImport)
            pythonStr = "import math \n\n" + pythonStr;


        /*
            Add the python result string to the function that will get the output string that will be different
            depending on if the Java class contains only a main method or contains other methods additionally.
         */

        resultPythonStr = pythonStr + onlyMainMethodOrOtherMethods(classContainsMain, classContainsOtherMethods) + "\n";

        System.out.println(pythonStr + onlyMainMethodOrOtherMethods(classContainsMain, classContainsOtherMethods));

        System.out.println();

        /* James B's logical operators code */
        //splitTokenList(tempData);
        //handleLogicalOperators();
        //handleUnaryOperators();

        //System.out.println(lexemeList);

    }

    // --------------------------------------------------------------------------------
    //          SECONDARY FUNCTIONS - GENERAL
    // --------------------------------------------------------------------------------

    // Using the list of tokens to find where all logical operators are located in
    // lexeme list
    // private static void listAllOfType(String thisType) {

    //     // Clean slate
    //     indices.clear();

    //     for (int index = 0; index < tokenList.size(); index++) {
    //         if (tokenList.get(index).equals(thisType)) {
    //             indices.add(index);
    //         }
    //     }
    // }

    // // Makes the TokenData a little more managable by splitting into two
    // private static void splitTokenList(List<TokenData> allTokenData) {
    //     for (TokenData tokenData : allTokenData) {
    //         tokenList.add(tokenData.token);
    //         lexemeList.add(tokenData.lexeme);
    //     }
    // }

    // // Decreases each value in the indice list by one. Used when the primary list removes an item
    // // to keep the indice list accurate to the list it's representing
    // private static void decrementIndices() {
    //     for (int nums : indices) {
    //         indices.set(indices.indexOf(nums), nums - 1);
    //     }
    // }

    // --------------------------------------------------------------------------------
    //          PRIMARY FUNCTIONS - LOGICAL OPERATORS
    // --------------------------------------------------------------------------------

    private static TokenData handleLogicalOperator(ArrayList<TokenData> fullList, TokenData currentItem, int currentIndex) {
        String tempLexeme = currentItem.lexeme;

        tempLexeme = chooseLogicalOperatorCase(fullList, tempLexeme, currentIndex);

        currentItem.lexeme = tempLexeme;
        return currentItem;
    }

    // Beginning of logical operator logic
    // private static void handleLogicalOperators() {
    //     String logicalOperatorString = "logical operator";
    //     String tempLexeme;
    //     int tempLexemeInt;

    //     // Where are their specific locations
    //     listAllOfType(logicalOperatorString);

    //     // Loop until all logical operators are handled
    //     while (indices.size() > 0) {
    //         tempLexemeInt = indices.get(0);
    //         tempLexeme = lexemeList.get(tempLexemeInt);

    //         tempLexeme = chooseLogicalOperatorCase(tempLexeme, tempLexemeInt);


    //         // Replace symbol with word
    //         lexemeList.set(tempLexemeInt, tempLexeme);
    //         // Moves onto next logical operator
    //         indices.remove(indices.get(0));
    //     }
    // }

    // --------------------------------------------------------------------------------
    //          SECONDARY FUNCTIONS - LOGICAL OPERATORS
    // --------------------------------------------------------------------------------

    private static String chooseLogicalOperatorCase(ArrayList<TokenData> fullList, String caseToCheck, int caseIndex) {

        // Case: &
        if (caseToCheck.equals("&")) {
            caseToCheck = handleAmpersand(fullList, caseIndex);
        }
        // Case: |
        else if(caseToCheck.equals("|")) {
            caseToCheck = handleVertBar(fullList, caseIndex);
        }
        // Case: !
        else {

            if(fullList.get(caseIndex + 1).lexeme.equals("=")) {
                caseToCheck = "!";
                checkNotEqual = true;
            } else {
                // Must be a ! operator
                caseToCheck = handleExclamation();
            }

        }

        return caseToCheck;
    }

    // Ampersand logic
    private static String handleAmpersand(ArrayList<TokenData> fullList, int tempLexemeInt) {
        String ampersandSymbol = "&";

        // Check for double &&
        handleDoubles(fullList, tempLexemeInt, fullList.size(), ampersandSymbol);

        // If Single & in java, needs to be replaced with "and"
        // Single & in Python is a bitwise AND function, which is very different

        return "and ";
        //return "and";
    }

    // Vertical Bar logic
    private static String handleVertBar(ArrayList<TokenData> fullList,  int tempLexemeInt) {
        String vertBarSymbol = "|";

        // Check for double ||
        handleDoubles(fullList, tempLexemeInt, fullList.size(), vertBarSymbol);

        return "or ";
        //return "or";
    }

    // Exclamation logic
    private static String handleExclamation() {
        // String exclamationSymbol = "!";
        // No doubles for !, so if the symbol is ! it just needs to be replaced with "not".

        return "not ";
        //return "not";
        //return "!";
    }

    // Logic for double symbols && and ||
    private static void handleDoubles(ArrayList<TokenData> fullList, int tempLexemeInt, int listSize, String symbolToCheck) {
        if (tempLexemeInt + 1 != listSize && fullList.get(tempLexemeInt + 1).lexeme.equals(symbolToCheck)) {
            // && becomes 'and', both lists need doubled sign removed
            //lexemeList.remove(tempLexemeInt + 1);
            //tokenList.remove(tempLexemeInt + 1);
            //indices.remove(indices.get(0));


            //decrementIndices();

            // Modified for switch case implementation
            fullList.remove(tempLexemeInt + 1);
        }
    }

    // --------------------------------------------------------------------------------
    //          PRIMARY FUNCTIONS - UNARY OPERATORS
    // --------------------------------------------------------------------------------

    private static void handleUnaryOperator(ArrayList<TokenData> fullList, String caseToCheck, int caseIndex) {
        String variableString = "VAR_IDENTIFIER";

        // True = ++, False = --
        boolean unaryType = false;
        // True = ++x/--x, False = x++/x--;
        boolean beforeAfter = false;

        if(caseToCheck.equals("++")) {
            unaryType = true;
        }

        // If index isn't 0 and previous index isn't a variable
        if(caseIndex != 0 && fullList.get(caseIndex-1).token.equals(variableString)) {
            beforeAfter = true;
        }

        caseToCheck = handleUnary(fullList, caseIndex, unaryType, beforeAfter);


        if(!beforeAfter) {caseIndex -= 1;}

        fullList.get(caseIndex).lexeme = caseToCheck;
    }

    // Beginning of unary operator logic
    // private static void handleUnaryOperators() {
    //     String unaryOperatorString = "unary operator";
    //     String variableString = "VAR_IDENTIFIER";
    //     String tempLexeme;

    //     int tempLexemeInt;

    //     // true = ++, false = --;
    //     boolean unaryType;
    //     // true = ++x/--x, false = x++/x--
    //     boolean beforeAfter;

    //     //listAllOfType(unaryOperatorString);

    //     // Loop until all unary operators are handled
    //     while(indices.size() > 0) {
    //         tempLexemeInt = indices.get(0);
    //         tempLexeme = lexemeList.get(tempLexemeInt);

    //         unaryType = false;
    //         beforeAfter = false;

    //         // Case: ++
    //         if(tempLexeme == "++") {
    //             unaryType = true;
    //         }
    //         // If not changed -> Case: --

    //         if(tempLexemeInt != 0 && tokenList.get(tempLexemeInt - 1).equals(variableString)) {
    //             beforeAfter = true;
    //         }

    //         // Gets correct replacement for unary operator case
    //         tempLexeme = handleUnary(tempLexemeInt, unaryType, beforeAfter);

    //         // If false, element behind tempLexemeInt is removed, so tempLexemeInt needs to shift as well
    //         if(!beforeAfter) {tempLexemeInt -= 1;}

    //         // Replace symbols with new expression
    //         lexemeList.set(tempLexemeInt, tempLexeme);
    //         // Moves onto the next unary operator
    //         indices.remove(indices.get(0));
    //     }
    // }

    // --------------------------------------------------------------------------------
    //          SECONDARY FUNCTIONS - UNARY OPERATORS
    // --------------------------------------------------------------------------------

    // Main logic for handling Unary Operator cases
    private static String handleUnary(ArrayList<TokenData> fullList, int lexemeInt, boolean type, boolean placement) {
        String unaryReplacement;
        // Case: True/True -> ++x
        if(type && placement) {
            // "x += 1", remove x
            unaryReplacement = fullList.get(lexemeInt+1).lexeme + "+=1";
            fullList.remove(lexemeInt + 1);
        }
        // Case: True/False -> x++
        else if(type && !placement) {
            // remove x, "x += 1"
            unaryReplacement = fullList.get(lexemeInt-1).lexeme + "+=1";
            fullList.remove(lexemeInt-1);
        }
        // Case: False/True -> --x
        else if(!type && placement) {
            // "x -= 1", remove x
            unaryReplacement = fullList.get(lexemeInt+1).lexeme + "-=1";
            fullList.remove(lexemeInt + 1);
        }
        // Case: False/False -> x--
        else {
            // remove x, "x -= 1"
            unaryReplacement = fullList.get(lexemeInt-1).lexeme + "-=1";
            fullList.remove(lexemeInt-1);
        }

        // Each case removes something, so the indices need to be decremented else
        //decrementIndices();

        return unaryReplacement;
    }

}

