package com.soriole.dht.kademlia.message;

import com.soriole.dht.kademlia.JKademliaStorageEntry;
import com.soriole.dht.kademlia.node.Node;
import com.soriole.dht.kademlia.util.serializer.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A Message used to send content between nodes
 *
 * @author Joshua Kissoon
 * @since 20140226
 */
public class ContentMessage implements Message {
    private static final Logger logger = LoggerFactory.getLogger(ContentMessage.class);

    public static final byte MSG_CODE = 0x04;

    private JKademliaStorageEntry content;
    private Node origin;

    /**
     * @param origin  Where the message came from
     * @param content The content to be stored
     */
    public ContentMessage(Node origin, JKademliaStorageEntry content) {
        this.content = content;
        this.origin = origin;
    }

    public ContentMessage(DataInputStream in) throws IOException {
        this.fromStream(in);
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException {
        this.origin.toStream(out);

        /* Serialize the KadContent, then send it to the stream */
        new JsonSerializer<JKademliaStorageEntry>().write(content, out);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException {
        this.origin = new Node(in);

        try {
            this.content = new JsonSerializer<JKademliaStorageEntry>().read(in);
        } catch (ClassNotFoundException e) {
            logger.error("ClassNotFoundException when reading StorageEntry;", e);
        }
    }

    public Node getOrigin() {
        return this.origin;
    }

    public JKademliaStorageEntry getContent() {
        return this.content;
    }

    @Override
    public byte code() {
        return MSG_CODE;
    }

    @Override
    public String toString() {
        return "ContentMessage[origin=" + origin + ",content=" + content + "]";
    }
}
