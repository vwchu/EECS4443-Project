package ca.yorku.eecs.cse13261.eecs4443project;

/**
 * String Poly-fills for Java 7.
 *
 * @author Vincent Chu
 * @version 1.0
 */
public class StringUtils {

    /**
     * Returns a new String composed of copies of the
     * {@code CharSequence elements} joined together with a copy of
     * the specified {@code delimiter}.
     *
     * <blockquote>For example,
     * <pre>{@code
     *     String message = String.join("-", "Java", "is", "cool");
     *     // message returned is: "Java-is-cool"
     * }</pre></blockquote>
     *
     * Note that if an element is null, then {@code "null"} is added.
     *
     * @param  delimiter the delimiter that separates each element
     * @param  elements the elements to join together.
     *
     * @return a new {@code String} that is composed of the {@code elements}
     *         separated by the {@code delimiter}
     * @throws NullPointerException If {@code delimiter} or {@code elements}
     *         is {@code null}
     */
    public static String join(CharSequence delimiter, Object... elements) {
        String[] strings = new String[elements.length];
        for (int i = 0; i < strings.length; i += 1) {
            strings[i] = elements[i].toString();
        }
        return join(delimiter.toString(), strings);
    }
    public static String join(String delimiter, String... elements) {
        String dlm = "";
        StringBuilder sb = new StringBuilder();
        for (Object str : elements) {
            sb.append(dlm);
            if (dlm.isEmpty()) {
                dlm = delimiter.toString();
            }
            sb.append(str);
        }
        return sb.toString();
    }

} // StringUtils
