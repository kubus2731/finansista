package pl.pb.finansista.common;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public class ExternalIdEncoder {

    public static String encode(String prefix, UUID uuid) {
        if (uuid == null) return null;
        
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(bb.array());
        return prefix != null && !prefix.isBlank() ? prefix + "_" + encoded : encoded;
    }

    public static UUID decode(String encodedId) {
        if (encodedId == null || encodedId.isBlank()) return null;
        
        String b64 = encodedId.contains("_") ? encodedId.substring(encodedId.indexOf('_') + 1) : encodedId;
        byte[] bytes = Base64.getUrlDecoder().decode(b64);
        
        if (bytes.length != 16) throw new IllegalArgumentException("Invalid UUID Base64 length");
        
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return new UUID(bb.getLong(), bb.getLong());
    }
}
