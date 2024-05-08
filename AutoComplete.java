
/**
 * An implementation of the AutoCompleteInterface using a DLB Trie.
 */

import java.util.ArrayList;

public class AutoComplete implements AutoCompleteInterface {

  private DLBNode root;
  private StringBuilder currentPrefix;
  private DLBNode currentNode;
  private int level;  //checks the depth of currentNode
  // TODO: Add more instance variables as needed

  public AutoComplete() {
    root = null;
    currentPrefix = new StringBuilder();
    currentNode = null;
  }

  /**
   * Adds a word to the dictionary in O(alphabet size*word.length()) time
   * 
   * @param word the String to be added to the dictionary
   * @return true if add is successful, false if word already exists
   * @throws IllegalArgumentException if word is the empty string
   */
  public boolean add(String word) {  
    //Added word and if it was not a duplicate add returns true 
    //and if it was a duplicate add returns false
    //and decremented its size values for all the characters in the word

    if((word.equals(null)) || (word.equals(""))) {  //Checks for invalid input
      throw new IllegalArgumentException("Need to input a valid word");
    }
    DLBNode traverseNode; //Local variable to traverse the trie to add the word
    if (root == null) { //Checks if DLB is empty
      root = new DLBNode(word.charAt(0));  
      traverseNode = root;
      traverseNode.size++;
      //Loops through all the characters in the word and adds them to the trie
      //We start after the first character because we already set the root to the first character
      for (int i = 1; i < word.length(); i++) {
        DLBNode temp = traverseNode;
        traverseNode.child = new DLBNode(word.charAt(i));
        traverseNode = traverseNode.child;
        traverseNode.parent = temp;
        traverseNode.size++;
        //System.out.println("Tsize2: " + traverseNode.size);
        //System.out.println("Rsize2: " + root.size);
      }
      //Sets the last characters isWord flag to true to indicate that the new word is a valid word
      traverseNode.isWord = true; 
      // traverseNode.size++;
      //currentNode = root;
      return true;

    }
    //If it reaches here the trie is not empty
    traverseNode = root;
    //Loops through all the characters in the word and checks if they are in the trie
    //And if they are not in the trie then we add the character to the trie
    for (int i = 0; i < word.length(); i++) {
      //current character does not equal the character we are on in the trie
      if (traverseNode.data != word.charAt(i)) {
        //Need to attempt to find this character so we will check if the node in the trie has any siblings
        if (traverseNode.nextSibling != null) {
          traverseNode = traverseNode.nextSibling; //If it does we will go to its next sibling
          while (traverseNode.nextSibling != null) {
            if (traverseNode.data == word.charAt(i)) { 
              //if we find the character we need in the trie we no longer need to check any of its siblings
              break; //so we can break out of the loop
            }
            traverseNode = traverseNode.nextSibling;
          }
          if (traverseNode.data != word.charAt(i)) {
            // If the while loop completes and does not find the character in the siblings list
            //We add our current character as a new sibling to the current node in the trie
            DLBNode temp = traverseNode;
            traverseNode.nextSibling = new DLBNode(word.charAt(i));
            traverseNode = traverseNode.nextSibling;
            traverseNode.previousSibling = temp;
          }
          // If the if statement was satisfied don't do anything

        }else{ //If the current node in the trie had no siblings
          //We add our current character as a new sibling to the current node in the trie
          DLBNode temp = traverseNode;
          traverseNode.nextSibling = new DLBNode(word.charAt(i));
          traverseNode = traverseNode.nextSibling;
          traverseNode.previousSibling = temp;
        }
      }
      //If we are at the last character in our word we do not want to move any further down
      if(i < word.length() - 1){  
        if (traverseNode.child == null) {
          traverseNode.child = new DLBNode(word.charAt(i + 1));
        }
      }else{
        traverseNode.size++;
          break;
      }
      //Completed check for our current character so now we can move down the trie
      traverseNode.size++;
      DLBNode temp = traverseNode;
      traverseNode = traverseNode.child;
      //System.out.println("Node: " + traverseNode.data);
      traverseNode.parent = temp;
      // Need to add rest of the characters of the word to the end of the DLB
    }
    //If the word is already in the trie we need to decrement the size for each character
    if (traverseNode.isWord == true) {
      //Loop back through all the characters in our duplicate word
      for(int i=0; i<word.length()-1;i++) {
        //System.out.println("TSIZEB: " + traverseNode.size);
        //System.out.println("-");
        traverseNode.size--;
        while(traverseNode.parent == null){ //If it doesn't have a parent we have to
          traverseNode = traverseNode.previousSibling;      
        }
        //System.out.println("TSIZEA: " + traverseNode.size);
        traverseNode = traverseNode.parent;
      }
      //System.out.println("TN: " + traverseNode.data);
      return false;
    } else {
      traverseNode.isWord = true;
      // traverseNode.size++;
      //currentNode = root;
      return true;
    }
  }

  /**
   * appends the character c to the current prefix in O(alphabet size) time.
   * This method doesn't modify the dictionary.
   * 
   * @param c: the character to append
   * @return true if the current prefix after appending c is a prefix to a word
   *         in the dictionary and false otherwise
   */
  public boolean advance(char c) { //This method appends the input character in for the currentPrefix
    //currentNode tracks where we are in the trie and if the currentPrefix is a valid prefix
    //When the currentPrefix is not valid advance returns false but the character is still appended to the currentPrefix
    if (c == ' ') { //Invalid input
      //System.out.println("F1");
      return false;
    }
    if(currentNode == null){  //First time calling advance
      currentNode = root;
      //System.out.println("CNSIZE: " + currentNode.size);
    }
    //System.out.println("CNSIZE: " + currentNode.size);
    currentPrefix.append(c);
    level++;
    DLBNode result = currentNode; //Holds original value of currentNode before traversal
    // Check level to make sure it maches up correctly to currentNode
    if (level != currentPrefix.length()) { 
      //System.out.println("FL");
      level--;
      return false;
    }
    if (currentPrefix.length() == 1) { //working on the first level of the trie 
      if (currentNode.data == c) { //Character was found
        return true;
      }
      //Character was not found 
      //Need to check the siblings list
      while (currentNode.nextSibling != null) { 
        if (currentNode.data == c) { //Character was found
          return true;
        }
        currentNode = currentNode.nextSibling;
      }
      if(currentNode.data == c){  //checking if character is the last sibling 
        return true;
      }
      //Character was not found
      currentNode = result; //reset currentNode back to where it was before looking for the character
      level--; 
      return false;
    }
    //currentPrefix length is greater than 1
    //advance is searching somewhere beyond the first level of the trie
    //and currentNode is one level above currentPrefix
    //currentNode needs to go down a level to check for the character
    if (currentNode.child == null) { //currentNode cannot go down a level so currentPrefix is not a prefix
      currentNode = result;
      //System.out.println("F3");
      level--;
      return false;
    }
    currentNode = currentNode.child; //currentNode can go down a level
    if (currentNode.data == c) { //Character was found
      return true;

    }
    //Character was not found 
    //Need to check the siblings list
    while (currentNode.nextSibling != null) {
      if (currentNode.data == c) { //Character was found
        return true;
      }
      currentNode = currentNode.nextSibling;
    }
    if (currentNode.data == c) { //checking if character is the last sibling 
      return true;
    }
    currentNode = result; //reset currentNode back to where it was before looking for the character
    //System.out.println("F4CN: " + currentNode.data);
    level--;
    return false;
  }

  public boolean containsPrefixOf(String s){
    if(root == null){
      return false;
    }
    DLBNode search = root;
    for(int i=0;i<s.length();i++){
       if(search.data != s.charAt(i)){
          while(search.nextSibling != null){
                if(search.data == s.charAt(i)){
                  break;
                }
                search = search.nextSibling;
          }
          if(search.isWord == true){ //isword should be replaced val and check if not null
            return true;
          }
          search = search.child;
       }
    }
    return false;
  }
  /**
   * removes the last character from the current prefix in O(alphabet size) time.
   * This
   * method doesn't modify the dictionary.
   * 
   * @throws IllegalStateException if the current prefix is the empty string
   */
  public void retreat() {
    //currentPrefix has no characters to retreat back from
    //You cannot go back if there is nothing to go back from
    if ((currentPrefix.toString().equals("")) || (currentPrefix.toString().equals(null))) {
      //throws exception because you cannot call retreat without advancing first
      throw new IllegalStateException("Cannot retreat");
    }
    //Checks to see if currentNode and currentPrefix are in different places
    if (level != currentPrefix.length()) { //currentPrefix is ahead of currentNode
      //Since currentPrefix is ahead of currentNode
      //then you just need to remove the last character in currentPrefix and not change currentNode
      currentPrefix = currentPrefix.deleteCharAt(currentPrefix.length() - 1);
    }
    // if(currentNode.parent == root){
    // currentPrefix = new StringBuilder("");
    // }
    else { //currentPrefix and currentNode are in the same place
      if (currentPrefix.length() == 1) { //Only one character left
        currentNode = root;
        level--;
        currentPrefix = new StringBuilder(""); 
      } else { //More than one character left
        if (currentNode.parent == null) {  //If you can't go up right away
          //Check for the sibling that can go up
          while (currentNode.previousSibling != null) {
            currentNode = currentNode.previousSibling;
          }
        }
        currentNode = currentNode.parent;
        level--;
        currentPrefix.deleteCharAt(currentPrefix.length() - 1);
      }
    }
    // TODO: implement this method
  }

  /**
   *
   * resets the current prefix to the empty string in O(1) time
   */
  public void reset() {
    //Resets all the values back to their original state
    currentPrefix = new StringBuilder("");
    currentNode = root;
    level = 0;
  }

  /**
   * @return true if the current prefix is a word in the dictionary and false
   *         otherwise. The running time is O(1).
   */
  public boolean isWord() {  //Checks if currentPrefix is a word
    //If currentNode is not in the same place as the currentPrefix than it cannot be a word
    if(level != currentPrefix.length()){ 
      return false;
    }
    //Checks the isWord flag of currentNode to check if the currentPrefix is a word
    if (currentNode.isWord == true) { 
      return true;
    }
  
    return false;
  }

  /**
   * adds the current prefix as a word to the dictionary (if not already a word)
   * The running time is O(alphabet size*length of the current prefix).
   */
  public void add() {
    //System.out.println("CPA: " + currentPrefix.toString());
    boolean check = add(currentPrefix.toString());
    if (check == true) { //currentPrefix is not a duplicate word
      //When the word is added as a word in the dictionary
      //Then we need to make sure that currentNode is aligned with currentPrefix
      if (level != currentPrefix.length()) { //currentNode and currentPrefix are not in the same place
        //Updates currentNode to be in the same place as currentPrefix
        while (level != currentPrefix.length()) {
          level++;
          currentNode = currentNode.child;
        }
      }
    }
  }

  /**
   * @return the number of words in the dictionary that start with the current
   *         prefix (including the current prefix if it is a word). The running
   *         time is
   *         O(1).
   */
  public int getNumberOfPredictions() {
    // TODO: implement this method
    if (root == null) {  //Trie is empty
      return 0; 
    }
    if (level != currentPrefix.length()) { //currentNode and currentPrefix are not aligned
      return 0;
    }
    //The size field of the currentNode tracks how many valid words past by that node in the trie
    //Therefore a node's size is how many valid words it is a prefix of
    //and therefore that amount is the correct amount of prediction based on that prefix
    return currentNode.size; 
  }

  /**
   * retrieves one word prediction for the current prefix. The running time is
   * O(prediction.length())
   * 
   * @return a String or null if no predictions exist for the current prefix
   */
  public String retrievePrediction() {  //Looks for a valid prediction based on the currentPrefix
    if (getNumberOfPredictions() == 0) { //There are no valid predictions
      return null;
    }
    DLBNode predNode = currentNode; //temporary variable so we don't modify currentNode 
    String predictor = ""; //use the string to find  a valid suffix to the currentPrefix
    while (predNode.isWord != true) {  //Looks for the first valid word
      //No valid word found yet
      predNode = predNode.child;  //Check the next node
      predictor += predNode.data; //add that character to the suffix
    }
    return currentPrefix.toString() + predictor; //Adds prefix and suffix for a valid prediction
  }

  /*
   * ==============================
   * Helper methods for debugging.
   * ==============================
   */

  // print the subtrie rooted at the node at the end of the start String
  public void printTrie(String start) {
    System.out.println("==================== START: DLB Trie Starting from \"" + start + "\" ====================");
    if (start.equals("")) {
      printTrie(root, 0);
    } else {
      DLBNode startNode = getNode(root, start, 0);
      if (startNode != null) {
        printTrie(startNode.child, 0);
      }
    }

    System.out.println("==================== END: DLB Trie Starting from \"" + start + "\" ====================");
  }

  // a helper method for printTrie
  private void printTrie(DLBNode node, int depth) {
    if (node != null) {
      for (int i = 0; i < depth; i++) {
        System.out.print(" ");
      }
      System.out.print(node.data);
      if (node.isWord) {
        System.out.print(" *");
      }
      System.out.println(" (" + node.size + ")");
      printTrie(node.child, depth + 1);
      printTrie(node.nextSibling, depth);
    }
  }

  // return a pointer to the node at the end of the start String
  // in O(start.length() - index)
  private DLBNode getNode(DLBNode node, String start, int index) {
    if (start.length() == 0) {
      return node;
    }
    DLBNode result = node;
    if (node != null) {
      if ((index < start.length() - 1) && (node.data == start.charAt(index))) {
        result = getNode(node.child, start, index + 1);
      } else if ((index == start.length() - 1) && (node.data == start.charAt(index))) {
        result = node;
      } else {
        result = getNode(node.nextSibling, start, index);
      }
    }
    return result;
  }

  // The DLB node class
  private class DLBNode {
    private char data;
    private int size;
    private boolean isWord;
    private DLBNode nextSibling;
    private DLBNode previousSibling;
    private DLBNode child;
    private DLBNode parent;

    private DLBNode(char data) {
      this.data = data;
      size = 0;
      isWord = false;
      nextSibling = previousSibling = child = parent = null;
    }
  }
}
