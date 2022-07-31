package de.heisluft.randomutils;

import java.util.Random;

/**
 * A fun utility for generating names consisting of latin alphabetic chars
 */
public final class NameGenerator {

  /**
   * All letters of the latin alphabet
   */
  private static final char[] letters = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
  /**
   * All vocals of the latin alphabet
   */
  private static final char[] vocals = new char[]{'a', 'e', 'i', 'o', 'u'};
  /**
   * All consonants of the latin alphabet
   */
  private static final char[] consonants = new char[]{'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z'};
  /**
   * All consonants that can be repeated
   */
  private static final char[] consonantsDouble = new char[]{'b', 'f', 'l', 'm', 'n', 'p', 'r', 's', 't', 'z'};

  private static final String[] sylStartingConsonants = new String[]{"t hrswy", "bgf rl", "v r", "cp hlry", "d ry", "hlmnr y", "k jlnr", "s chklnpty", "w hr"};
  private static final String[] sylEndingConsonants = new String[]{"t hyz", "c hksy", "d sy", "k sy", "f t", "gp hy", "y dlmnt", "h bcdklmnt", "l bcdfgnpstyz", "m bpsty", "r bcdfgklmnpsty", "s bdhckpty", "t hszy", "w lnsy"};

  /**
   * The generators random source
   */
  private final Random rand;

  /**
   * Constructs a new NameGenerator with a given random source
   *
   * @param rand the random to supply to the generator
   */
  public NameGenerator(Random rand) {
    this.rand = rand;
  }

  /**
   * Constructs a new NameGenerator without specifying a random source
   */
  public NameGenerator() {
    this(new Random());
  }

  /**
   * Tests if an event with an x in y chance tested positive.
   *
   * @param x the numerator
   * @param y the denominator
   * @return true if a random int was smaller than x
   */
  private boolean xinYChance(int x, int y) {
    return rand.nextInt(y) < x;
  }

  /**
   * Tests if a char array contains a certain char.
   * The array is searched in a linear fashion
   *
   * @param array the array to search in
   * @param search the char to search for
   * @return if the given array contains the char
   */
  private boolean contains(char[] array, char search) {
    for(char c : array) if(c == search) return true;
    return false;
  }

  /**
   * Gives a random char within the given array if it does not match the given last char.
   * @param last the char to compare against
   * @param array the array to choose from
   * @return a random char, not last
   */
  private char genDifferentChar(char last, char[] array) {
    char next = array[rand.nextInt(array.length)];
    while(next == last) next = array[rand.nextInt(array.length)];
    return next;
  }

  /**
   * Generates a continuation for the last char within builder based on the given string array
   * If the char is not found as a key nothing is appended
   *
   * @param builder the builder to maybe append to
   * @param strings the array containing all possible continuations
   */
  private void genContinuation(StringBuilder builder, String[] strings) {
    char last = builder.charAt(builder.length() - 1);
    String continuationString = null;
    STR:
    for(String str : strings)
      for(char c : str.toCharArray()) {
        if(c == last) {
          continuationString = str;
          break STR;
        } else if(c == ' ') continue STR;
      }

    if(continuationString != null) {
      String sel = "";
      for(int i = 0; i < continuationString.length(); i++) {
        char sc = continuationString.charAt(i);
        if(sc == ' ') {
          sel = continuationString.substring(i + 1);
          break;
        }
      }

      if(sel.length() > 0 && xinYChance((int) (((float) sel.length() / 26) * 100), 100))
        builder.append(sel.charAt(rand.nextInt(sel.length())));
    }
  }

  /**
   * Generates a random name based on a given number of execution steps.
   *
   * @param maxSteps the maximum number of steps to be executed. Steps should be about equivalent
   *                 with syllable in most cases, but due to the randomness more syllables might
   *                 sneak in, so calling steps syllable isn't really right
   * @param minLen the minimum length of the name
   *
   * @return the generated name
   */
  public String generateName(int maxSteps, int minLen) {
    StringBuilder builder = new StringBuilder();
    for(int steps = 0; steps < maxSteps; steps++) {
      if(steps > 0) {
        if(contains(consonantsDouble, builder.charAt(builder.length() - 1)) && contains(vocals, builder.charAt(builder.length() - 2)) && xinYChance(1, 2))
          builder.append(builder.charAt(builder.length() - 1));
        else if(contains(vocals, builder.charAt(builder.length() - 1)))
          builder.append(genDifferentChar(builder.charAt(builder.length() - 1), letters));
      } else builder.append(letters[rand.nextInt(letters.length)]);

      boolean hasVocal = contains(vocals, builder.charAt(builder.length() - 1));

      if(!hasVocal) {
        if(builder.charAt(builder.length() - 1) == 'q') builder.append('u');
        else if(xinYChance(2, 3)) {
          genContinuation(builder, sylStartingConsonants);

          if(xinYChance(1, 3)) genContinuation(builder, sylStartingConsonants);
        }

        builder.append(genDifferentChar(builder.charAt(builder.length() - 1), vocals));
      }

      if(hasVocal || xinYChance(1, 2)) {
        builder.append(genDifferentChar(builder.charAt(builder.length() - 1), consonants));
        if(xinYChance(1, 3)) genContinuation(builder, sylEndingConsonants);
      }

      if(builder.length() >= minLen && xinYChance(1 + steps, maxSteps * 3)) break;
    }

    if(builder.charAt(builder.length() - 1) == 'q') {
      builder.append('u');
      builder.append(genDifferentChar(builder.charAt(builder.length() - 1), vocals));
    }

    boolean hasVocal = contains(vocals, builder.charAt(builder.length() - 1));
    if(!hasVocal && xinYChance(1, 5)) {
      if(contains(consonantsDouble, builder.charAt(builder.length() - 1)) && contains(vocals, builder.charAt(builder.length() - 2)) && xinYChance(2, 3)) builder.append(builder.charAt(builder.length() - 1));
      else genContinuation(builder, sylEndingConsonants);
    } else if(hasVocal && contains(vocals, builder.charAt(builder.length() - 2)) && xinYChance(2, 3)) // h after two vocals
      builder.append('h');

    builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));

    return builder.toString();
  }
}
