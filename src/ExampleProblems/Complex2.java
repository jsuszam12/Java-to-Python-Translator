package ExampleProblems;

/*
     Elements being captured in this example:
     - Comments
     - Variables
     - Casting
     - Boolean Logic
     - Operators
     - If/Else if/Else
     - While Loops
     - For Loops
     - Arrays
*/
public class Complex2 {
    // ----------------------------------------------------------------------------    
    //                          COMMENTS
    // ----------------------------------------------------------------------------
    
    
    public void commentSection() {
        // Make this comment a Python Comment

        /*
        Everything in this block area
        needs to be captured
        as a comment too
        */
        System.out.println("Reached the end of Comment Section");
    }

    // ----------------------------------------------------------------------------
    //                           VARIABLES
    // ----------------------------------------------------------------------------
    public void varSection() {
        byte testByte = 22; 
        short testShort = 2045; // Bigger than byte
        int testInt = 40341; // Bigger than short
        long testLong = 3685469852L; // Bigger than int
        float testFloat = 45.87f;
        double testDouble = 458.2247852682; // Handles more decimal places than float
        boolean testBoolean = true;
        char testChar = 'a';
        String testString = "text";

        System.out.println("Reached the end of Variable Section");
    }

    // ----------------------------------------------------------------------------
    //                            CASTING
    // ----------------------------------------------------------------------------
    // Implicit casting handled automatically in Python
    public void castSection() {
        short testShort = 2045;
        int testInt = 40341;
        float testFloat = 45.87f;
        double testDouble = 458.2247852682;
       
        // Explicit Casting: 

        // Digit Casting
        int castInt = (int)testFloat; // (int), (byte), (short), (long) will all cast to int(example) in Python
        float castFloat = (float)testDouble; // (float) and (double) will cast to float(example) in Python

        // String Casting
        String castString1 = Integer.toString(testInt);
        String castString2 = String.valueOf(testShort);
        // Available String Formats: %a , %b, %c, %d, %e, %f, %g, %h, %n, %o, %s,, %x 
        String castString3 = String.format("%f", testDouble);

        System.out.println("Reached the end of Casting Section");
    }

    // ----------------------------------------------------------------------------
    //                        BOOLEAN LOGIC
    // ----------------------------------------------------------------------------
    public void boolSection() {
        int x = 5;
        int y = 22;
        int z = 1;
        System.out.println(x > y && x != z);
        System.out.println(x > z || z == 4);

        System.out.println("Reached the end of Boolean Logic Section");
    }

    // ----------------------------------------------------------------------------
    //                          OPERATORS
    // ----------------------------------------------------------------------------
    public void operatorSection() {
        // Increment/Decrement Operators don't exist in Python
        int x = 1;
        x++;
        ++x;
        x--;
        --x;

        System.out.println("Reached the end of Operator Section");
    }
    
    // ----------------------------------------------------------------------------
    //                      CONDITIONAL STATEMENTS
    // ----------------------------------------------------------------------------
    public void conditionalSection() {
        int x = 1;
        int y = 2;
        String z = "testText";

        if(x > y) {
            x = 200;
        } else if(z.equals("text")) {
            z = "newText";
        } else {
            System.out.println("Random text");
        }

        System.out.println("Reached the end of Conditional Section");
    }

    // ----------------------------------------------------------------------------
    //                            ARRAYS
    // ----------------------------------------------------------------------------

    public void arraySection() {
        // Singular Arrays for now
        int[] smallInts = {1, 2, 3};
        System.out.println(smallInts);
        smallInts[0] = 4;
        System.out.println(smallInts);

        String[] testStringArray = new String[3];
        testStringArray[0] = "Change 1";
        testStringArray[1] = "Change 2";
        testStringArray[2] = "Change 3";
        System.out.println(testStringArray.length);

    }

    // ----------------------------------------------------------------------------
    //                            LOOPS
    // ----------------------------------------------------------------------------
    
    public void loopSection() {
        
        // While Loop
        boolean stopLoop = false;
        int count = 0;
        while(!stopLoop) {
            System.out.println("While loop running");
            count++;
            
            if(count == 3) {
                stopLoop = true;
            }
        }

        stopLoop = false;
        count = 0;

        // Do While Loop
        do {
            System.out.println("Do/While loop running");
            count++;
            if(count == 3) {
                stopLoop = true;
            }   
        } while (!stopLoop);

        // For Loop
        for(int index = 0; index < 3; index++) {
            System.out.println("For loop running");
        }

        // For Each Loop
        int[] testInts = {2, 4, 6};
        for(int i: testInts) {
            System.out.println("For Each loop running");
        }

        System.out.println("Reached end of Loop Section");
    }
}
