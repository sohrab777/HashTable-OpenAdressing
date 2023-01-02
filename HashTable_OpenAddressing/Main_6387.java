// Sohrab Namazi Nia, Last 4 digits of NJIT ID: 6387

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main_6387 
{
    public static void main(String[] args)
    {
      List<String> lines = read_file("file.txt");
      Lexicon L = new Lexicon();
      for (String line : lines)
      {
        String[] command = get_command(line);
        L.execute_command(command);
      }
    }

    public static List<String> read_file(String file_path_name)
    {
      List<String> lines = new ArrayList<String>();;
      try
      {
        File file = new File(file_path_name);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while((line=br.readLine())!=null)  
        {  
          lines.add(line);       
        }  
        fr.close();     
      }
      catch(IOException e)
      {
        System.out.println("The file does not exist");
      }
      return lines;
    }

    public static String[] get_command(String line)
    {
      return line.split(" ");
    }
}

class Lexicon
{
    T T;

    public Lexicon()
    {
    
    }
    
    public void execute_command(String[] command)
    {
      String first = command[0];
      // Insert
      if (first.equals("10"))
      {
        String word = command[1];
        this.Insert(word);
      }
      // Delete
      else if (first.equals("11"))
      {
        String word = command[1];
        this.Delete(word);
      }
      // Search
      else if (first.equals("12"))
      {
        String word = command[1];
        int slot_index = this.Search(word);
        if (slot_index != -1)
        {
          System.out.println(word + " found at slot " + slot_index);
        }
        else
        {
          System.out.println(word + " not found");
        }
      }
      // Print
      else if (first.equals("13"))
      {
        this.Print();
      }
      // Create
      else if (first.equals("14"))
      {
        int N = Integer.parseInt(command[1]);
        this.Create(N);
      }
      // Comment
      else if (first.equals("15"))
      {
        String[] comment_array = Arrays.copyOfRange(command, 1, command.length);
        this.Comment(Arrays.toString(comment_array));
        return;
      }
      else
      {
        System.out.println("Command is invalid");
      }
      return;
    }

    public int Insert(String word)
    { 
      int prev_insert_index = this.Search(word);   
      if (prev_insert_index != -1)
      {
        return prev_insert_index;
      }
      int t = this.T.compute_slot(word.toCharArray());
      if (t != -1)
      {
        this.T.Hash_Table[t] = this.T.A.cursor;
        this.T.A.Write(word.toCharArray());
      }
      else
      {
        this.Handle_Overflow();
      }
      return t;
    }

    public int Search(String word)
    {
      int slot_index = this.T.find_slot(word.toCharArray());
      return slot_index;
    }

    public int Delete(String word)
    {
      int word_index = this.Search(word);  
      if (word_index == -1) return -1;
      this.T.A.Write_Star(this.T.Hash_Table[word_index], word.length());
      this.T.Hash_Table[word_index] = -1;
      return word_index;
    }

    public String Print()
    {
      String result_T = "";
      String result_A = "";
      for (int i = 0; i < this.T.Hash_Table.length; i++)
      {
        String element;
        if (this.T.Hash_Table[i] < 0)
        {
          element = i + ":  " + "\n";
        } 
        else
        {
          element = i + ": " + this.T.Hash_Table[i] + "\n";
        }
        result_T += element;
      }
      for (int i = 0; i < this.T.A.cursor; i++)
      {
        char c = this.T.A.Array[i];
        if (c == A.NUL)
        {
          result_A += "\\";
        }
        else 
        {
          result_A += c;
        }
      }
      System.out.println(result_A + "\n" + result_T);
      return result_A + "\n" + result_T;
    }

    public T Create(int N)
    {
      this.T = new T(N);
      return T;
    }

    public String Comment(String comment)
    {
      return comment;
    }

    public void Handle_Overflow()
    {
      char[] old_A_array = this.T.A.Array;
      int New_N = this.T.N * 2;
      this.T = new T(New_N);
      List<String> previous_strings = this.T.A.get_all_strings(old_A_array);
      this.Insert_all(previous_strings);
    }

    // This function is only used when overflow occurs
    public void Insert_all(List<String> old_words)
    {
      for (String word : old_words)
      {
        this.Insert(word);
      }
    }

    
  }

class T
{
    public int N;
    public int[] Hash_Table;
    public A A;

    public T(int N) 
    {
      this.N = N;
      this.Hash_Table = new int[N];
      Arrays.fill(this.Hash_Table, -1);
      this.A = new A(N * 15);
    }

    public char[] get_char_array(String word)
    {
      return word.toCharArray();
    }

    public int get_Ascci(char c)
    {
      return (int) c;
    }

    public int h_prime(char[] word)
    {
      int sum = 0;
      int word_size = word.length;
      for (int i = 0; i < word_size; i++)
      {
        sum += get_Ascci(word[i]);
      }
      sum -= 2;
      sum = sum % N;
      return sum;
    }

    public int h(char[] k, int i)
    {
      int h = (h_prime(k) + i*i) % N;
      return h;
    }

    public int compute_slot(char[] word)
    {
      int t = -1;
      for (int i = 0; i < this.N; i++)
      {
        int candidate = this.h(word, i);
        if (this.Hash_Table[candidate] == -1)
        {
          t = candidate;
          break;
        }
      }
      return t;
    }

    public int find_slot(char[] word)
    {
      int candidate = -1;
      for (int i = 0; i < this.N; i++)
      {
        candidate = this.h(word, i);
        if ((this.Hash_Table[candidate] != -1))
        {
          int word_address = this.Hash_Table[candidate];
          for (int j = 0; j < word.length; j++)
          {
            if (word[j] != this.A.Array[word_address])
            {
              break;
            }
            else
            {
              word_address++;
              if (j == word.length-1 && this.A.Array[word_address] == '\0')
              {
                 return candidate;
              }
            }
          }
        }
      }
      return -1;
    }
}

class A
{
    static char NUL = '\0';
    public char[] Array;
    public int size;
    public int cursor;

    A(int size)
    {
      this.cursor = 0;
      this.size = size;
      Array = new char[size];
      Arrays.fill(Array, ' ');
    }

    public int Write(char[] word)
    {
      int word_address = this.cursor;
      for (int i = 0; i<word.length; i++)
      {
        this.Array[this.cursor] = word[i];
        this.cursor++;
      }
      this.Array[this.cursor] = '\0';
      this.cursor++;
      return word_address;
    }

    public int Write_Star(int start_point, int word_length)
    {
      for (int i = 0; i < word_length; i++)
      {
        this.Array[start_point + i] = '*';
      }
      return start_point + word_length + 1;
    }

    // This function is used only in case of overflow
    public List<String> get_all_strings(char[] old_A_array)
    {
        List<String> result = new ArrayList<String>();
        List<Character> current_string = new ArrayList<Character>();
        StringBuilder builder;
        for (int i = 0; i < old_A_array.length; i++)
        {
          char c = old_A_array[i];
          if (c == '\0')
          {
            if (current_string.size() != 0)
            {
              builder = new StringBuilder(current_string.size());
              for(Character ch: current_string)
              {
                  builder.append(ch);
              }
              result.add(builder.toString());
              current_string.clear();
            }
          }
          else if (c == ' ')
          {
            break;
          }
          else if (c != '*')
          {
            current_string.add(c);
          }
        }
        return result;
    }
} 
