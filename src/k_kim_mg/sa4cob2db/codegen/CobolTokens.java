package k_kim_mg.sa4cob2db.codegen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CobolTokens implements Iterator<String> {
  private List<String> list;
  private Iterator<String> iterator;

  /**
   * Constructor.
   * 
   * @param text
   *          logical row.
   */
  public CobolTokens(String text) {
    text2list(text);
    list2iterator();
  }

  /**
   * Generate list from text;
   * 
   * @param text
   *          row text.
   */
  private void text2list(String text) {
    list = new ArrayList<String>();
    String row = (text == null ? "" : text.trim());
    boolean sq = false;
    boolean dq = false;
    boolean escape = false;
    StringBuffer buff = new StringBuffer();
    for (int i = 0; i < row.length(); i++) {
      char chr = row.charAt(i);
      if (dq) {
        // "abc \"'defag"
        if (escape) {
          buff.append(chr);
          escape = false;
        } else {
          if (chr == '\\') {
            escape = true;
          } else if (chr == '"') {
            buff.append(chr);
            list.add(buff.toString());
            buff = new StringBuffer();
            dq = false;
          } else {
            buff.append(chr);
          }
        }
      } else if (sq) {
        // 'abc \'"defag'
        if (escape) {
          buff.append(chr);
          escape = false;
        } else {
          if (chr == '\\') {
            escape = true;
          } else if (chr == '\'') {
            buff.append(chr);
            list.add(buff.toString());
            buff = new StringBuffer();
            sq = false;
          } else {
            buff.append(chr);
          }
        }
      } else {
        // others
        if (chr == ' ' || chr == '\t' || chr == '\r' || chr == '\n' || chr == '\n') {
          if (buff.length() > 0) {
            list.add(buff.toString());
            buff = new StringBuffer();
          }
        } else if (chr == '"') {
          if (buff.length() > 0) {
            list.add(buff.toString());
            buff = new StringBuffer();
          }
          buff.append(chr);
          dq = true;
        } else if (chr == '\'') {
          if (buff.length() > 0) {
            list.add(buff.toString());
            buff = new StringBuffer();
          }
          buff.append(chr);
          sq = true;
        } else if (chr == '.' || chr == '+' || chr == '-' || chr == '*' || chr == '/' || chr == '=' || chr == '(' || chr == ')') {
          if (buff.length() > 0) {
            list.add(buff.toString());
            buff = new StringBuffer();
          }
          buff.append(chr);
          list.add(buff.toString());
          buff = new StringBuffer();
        } else {
          buff.append(chr);
        }
      }
    }
  }

  /**
   * Generator Iterator from List.
   */
  private void list2iterator() {
    iterator = list.iterator();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#hasNext()
   */
  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#next()
   */
  @Override
  public String next() {
    return iterator.next();
  }

}
