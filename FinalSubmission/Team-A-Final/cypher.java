public class cypher
{
    public static String shift(String plain, int amount)
    {
        String result = "";

        //for each letter in the string, shift by the value of the amount
        for(int i = 0; i < plain.length(); i++)
        {
            String temp = plain.substring(i).toLowerCase();
            if (temp.charAt(0) == ' ')   //ignore spaces
            {
            	result = result + " ";
            } 
            else
            {
            	result = result + (char)(((temp.toCharArray()[0]+amount-'a')%26)+'a');
            }
        }

        return result;
    }

    public static String poly(String plain, String key)
    {
        String result = "";
        String plainL = plain.toLowerCase();
        String keyL = key.toLowerCase();

        //for each letter in the string, shift by the value of the corresponding letter in key
        int l = plain.length();
        for(int i = 0; i < l; i++)
        {
            String temp = plainL.substring(i, i+1);
            char k = keyL.substring(i%key.length()).toCharArray()[0];
            result = result + (char)(((temp.toCharArray()[0]+k-'a'+1-'a')%26)+'a');
        }

        return result;
    }

    public static String analysis(String cypher)
    {
        int[] a = new int[26];
        String text = cypher.toLowerCase();
        String amounts = "";

        //search the string for the amounts of each letter
        for(int i = 0; i < cypher.length(); i++)
        {
            char temp = text.substring(i).toCharArray()[0];
            switch(temp) {
                case 'a':
                    a[0] += 1;
                    break;
                case 'b':
                    a[1] += 1;
                    break;
                case 'c':
                    a[2] += 1;
                    break;
                case 'd':
                    a[3] += 1;
                    break;
                case 'e':
                    a[4] += 1;
                    break;
                case 'f':
                    a[5] += 1;
                    break;
                case 'g':
                    a[6] += 1;
                    break;
                case 'h':
                    a[7] += 1;
                    break;
                case 'i':
                    a[8] += 1;
                    break;
                case 'j':
                    a[9] += 1;
                    break;
                case 'k':
                    a[10] += 1;
                    break;
                case 'l':
                    a[11] += 1;
                    break;
                case 'm':
                    a[12] += 1;
                    break;
                case 'n':
                    a[13] += 1;
                    break;
                case 'o':
                    a[14] += 1;
                    break;
                case 'p':
                    a[15] += 1;
                    break;
                case 'q':
                    a[16] += 1;
                    break;
                case 'r':
                    a[17] += 1;
                    break;
                case 's':
                    a[18] += 1;
                    break;
                case 't':
                    a[19] += 1;
                    break;
                case 'u':
                    a[20] += 1;
                    break;
                case 'v':
                    a[21] += 1;
                    break;
                case 'w':
                    a[22] += 1;
                    break;
                case 'x':
                    a[23] += 1;
                    break;
                case 'y':
                    a[24] += 1;
                    break;
                case 'z':
                    a[25] += 1;
                    break;
                default: break;
            }
        }

        for(int i = 0; i < 26; i++)
        {
            amounts = amounts + i + ":" + a[i] + "; ";
        }

        return amounts;
    }
}
