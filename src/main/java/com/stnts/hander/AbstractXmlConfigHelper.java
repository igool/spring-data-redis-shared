package com.stnts.hander;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractXmlConfigHelper {

	private static final Logger LOGGER = Logger.getLogger(AbstractXmlConfigHelper.class);
	
	protected boolean domLevel3 = true;
	
    /**
     * Iterator for NodeList
     */
    public static class IterableNodeList implements Iterable<Node> {

        private final NodeList parent;
        private final int maximum;
        private final short nodeType;

        public IterableNodeList(final Node node) {
            this(node.getChildNodes());
        }

        public IterableNodeList(final NodeList list) {
            this(list, (short) 0);
        }

        public IterableNodeList(final Node node, short nodeType) {
            this(node.getChildNodes(), nodeType);
        }

        public IterableNodeList(final NodeList parent, short nodeType) {
            this.parent = parent;
            this.nodeType = nodeType;
            this.maximum = parent.getLength();
        }

        public Iterator<Node> iterator() {
            return new Iterator<Node>() {
                private int index;
                private Node next;

                public boolean hasNext() {
                    next = null;
                    for (; index < maximum; index++) {
                        final Node item = parent.item(index);
                        if (nodeType == 0 || item.getNodeType() == nodeType) {
                            next = item;
                            return true;
                        }
                    }
                    return false;
                }

                public Node next() {
                    if (hasNext()) {
                        index++;
                        return next;
                    }
                    throw new NoSuchElementException();
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
    
    protected String xmlToJavaName(final String name) {
        final StringBuilder builder = new StringBuilder();
        final char[] charArray = name.toCharArray();
        boolean dash = false;
        final StringBuilder token = new StringBuilder();
        for (char aCharArray : charArray) {
            if (aCharArray == '-') {
                appendToken(builder, token);
                dash = true;
                continue;
            }
            token.append(dash ? Character.toUpperCase(aCharArray) : aCharArray);
            dash = false;
        }
        appendToken(builder, token);
        return builder.toString();
    }

    protected void appendToken(final StringBuilder builder, final StringBuilder token) {
        String string = token.toString();
        if ("Jvm".equals(string)) {
            string = "JVM";
        }
        builder.append(string);
        token.setLength(0);
    }

    protected String getTextContent(final Node node) {
        if (node != null) {
            final String text;
            if (domLevel3) {
                text = node.getTextContent();
            } else {
                text = getTextContentOld(node);
            }
            return text != null ? text.trim() : "";
        }
        return "";
    }

    private String getTextContentOld(final Node node) {
        final Node child = node.getFirstChild();
        if (child != null) {
            final Node next = child.getNextSibling();
            if (next == null) {
                return hasTextContent(child) ? child.getNodeValue() : "";
            }
            final StringBuilder buf = new StringBuilder();
            appendTextContents(node, buf);
            return buf.toString();
        }
        return "";
    }

    private void appendTextContents(final Node node, final StringBuilder buf) {
        Node child = node.getFirstChild();
        while (child != null) {
            if (hasTextContent(child)) {
                buf.append(child.getNodeValue());
            }
            child = child.getNextSibling();
        }
    }

    protected final boolean hasTextContent(final Node node) {
        final short nodeType = node.getNodeType();
        return nodeType != Node.COMMENT_NODE && nodeType != Node.PROCESSING_INSTRUCTION_NODE;
    }

    public final String cleanNodeName(final Node node) {
        return cleanNodeName(node.getNodeName());
    }

    public static String cleanNodeName(final String nodeName) {
        String name = nodeName;
        if (name != null) {
            name = nodeName.replaceAll("\\w+:", "").toLowerCase();
        }
        return name;
    }

    protected boolean checkTrue(final String value) {
        return "true".equalsIgnoreCase(value)
                || "yes".equalsIgnoreCase(value)
                || "on".equalsIgnoreCase(value);
    }

    protected int getIntegerValue(final String parameterName, final String value, final int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (final Exception e) {
            LOGGER.info(parameterName + " parameter value, [" + value
                    + "], is not a proper integer. Default value, [" + defaultValue + "], will be used!");
            return defaultValue;
        }
    }

    protected long getLongValue(final String parameterName, final String value, final long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (final Exception e) {
            LOGGER.info(parameterName + " parameter value, [" + value
                    + "], is not a proper long. Default value, [" + defaultValue + "], will be used!");
            return defaultValue;
        }
    }

    protected String getAttribute(org.w3c.dom.Node node, String attName) {
        final Node attNode = node.getAttributes().getNamedItem(attName);
        if (attNode == null) {
            return null;
        }
        return getTextContent(attNode);
    }
}
