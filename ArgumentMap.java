import java.util.HashMap;
import java.util.Map;

/**
 * Parses command-line arguments into flag/value pairs, and stores those pairs
 * in a map for easy access.
 */
public class ArgumentMap {

  private final Map<String, String> map;

  /**
   * Initializes the argument map.
   */
  public ArgumentMap() {
    map = new HashMap<>();
  }

  /**
   * Initializes the argument map and parses the specified arguments into
   * key/value pairs.
   *
   * @param args
   *            command line arguments
   *
   * @see #parse(String[])
   */
  public ArgumentMap(String[] args) {
    this();
    parse(args);
  }

  /**
   * Parses the specified arguments into key/value pairs and adds them to the
   * argument map.
   *
   * @param args
   *            command line arguments
   */
  public void parse(String[] args) {
    int len = args.length;
    for (int i = 0; i < len; i++) {
      if (isFlag(args[i])) {
        if (i == len - 1 || isFlag(args[i + 1])) {
          map.put(args[i], null);
        } else if (isValue(args[i + 1])) {
          map.put(args[i], args[i + 1]);
          i++;
        }
      }
    }
  }

  /**
   * Checks if the argument is a flag.
   * 
   * @param arg
   *            argument
   * @return true if the argument passed in is a flag, false otherwise
   */
  public static boolean isFlag(String arg) {
    if (arg == null) {
      return false;
    } else {
      arg = arg.trim();
      return arg.length() >= 2 && arg.startsWith("-");
    }
  }

  /**
   * Checks if the argument is a value.
   * 
   * @param arg
   *            argument
   * @return true if the argument is a value, false otherwise
   */
  public static boolean isValue(String arg) {
    arg = arg.trim();
    return ((arg != null) && (!arg.startsWith("-")) && (arg.length() > 0) && (!arg.isEmpty()));
  }

  /**
   * Returns the number of unique flags stored in the argument map.
   *
   * @return number of flags
   */
  public int numFlags() {
    return map.size();
  }

  /**
   * Determines whether the specified flag is stored in the argument map.
   *
   * @param flag
   *            flag to test
   *
   * @return true if the flag is in the argument map
   */
  public boolean hasFlag(String flag) {
    return map.containsKey(flag);
  }

  /**
   * Determines whether the specified flag is stored in the argument map and
   * has a non-null value stored with it.
   *
   * @param flag
   *            flag to test
   *
   * @return true if the flag is in the argument map and has a non-null value
   */
  public boolean hasValue(String flag) {
    return (map.get(flag) != null);
  }

  /**
   * Returns the value for the specified flag as a String object.
   *
   * @param flag
   *            flag to get value for
   *
   * @return value as a String or null if flag or value was not found
   */
  public String getString(String flag) {
    return map.get(flag);
  }

  /**
   * Returns the value for the specified flag as a String object. If the flag
   * is missing or the flag does not have a value, returns the specified
   * default value instead.
   *
   * @param flag
   *            flag to get value for
   * @param defaultValue
   *            value to return if flag or value is missing
   * @return value of flag as a String, or the default value if the flag or
   *         value is missing
   */
  public String getString(String flag, String defaultValue) {
    if (map.get(flag) != null) {
      return map.get(flag);
    } else {
      return defaultValue;
    }
  }

  /**
   * Returns the String value of the flag that is passed in.
   *
   * @param flag
   *            flag of the value we want returned
   *
   * @return the value of the flag passed in
   */
  public String getValue(String flag) {
    if (hasFlag(flag) && hasValue(flag)) {
      return map.get(flag);
    } else {
      return null;
    }
  }

  /**
   * Sets the value of the flag to the new value that is passed in.
   *
   * @param flag
   *            flag whose value will be reset
   * 
   * @param value
   *            new value of the flag passed in
   *
   */
  public void setValue(String flag, String value) {
    map.replace(flag, map.get(flag), value);
  }

  /**
   * Returns the value for the specified flag as an int value. If the flag is
   * missing or the flag does not have a value, returns the specified default
   * value instead.
   *
   * @param flag
   *            flag to get value for
   * @param defaultValue
   *            value to return if the flag or value is missing
   * @return value of flag as an int, or the default value if the flag or
   *         value is missing
   */
  public int getInteger(String flag, int defaultValue) {
    try {
      return Integer.parseInt(map.get(flag));
    } catch (NumberFormatException | NullPointerException e) {
      return defaultValue;
    }
  }

  @Override
  public String toString() {
    return map.toString();
  }
}