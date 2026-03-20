package net.crystalixs.core.common.command.help;

import net.crystalixs.core.common.command.help.NetworkHelpCatalog.Entry;
import net.crystalixs.core.common.command.help.NetworkHelpCatalog.SourceType;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;

public final class NetworkHelpCatalogCodec {

    private NetworkHelpCatalogCodec() {
    }

    public static byte[] encode(NetworkHelpCatalog catalog) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(outputStream);

            out.writeInt(NetworkHelpSyncProtocol.VERSION);
            out.writeUTF(catalog.sourceId());
            out.writeUTF(catalog.sourceType().name());
            out.writeLong(catalog.generatedAt().toEpochMilli());
            out.writeInt(catalog.entries().size());

            for (var entry : catalog.entries()) {
                out.writeUTF(entry.syntax());
                out.writeUTF(entry.description());

                out.writeBoolean(entry.permission() != null);
                if (entry.permission() != null) out.writeUTF(entry.permission());

                out.writeUTF(entry.command());

                out.writeInt(entry.arguments().size());
                for (var argument : entry.arguments()) {
                    out.writeUTF(argument.syntax());
                    out.writeBoolean(argument.optional());
                    out.writeUTF(argument.description());
                }
            }
            out.flush();
            return outputStream.toByteArray();

        } catch (IOException exception) {
            throw new IllegalStateException("Failed to encode help catalog", exception);
        }
    }

    public static NetworkHelpCatalog decode(byte[] data) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));

            int version = in.readInt();
            if (version != NetworkHelpSyncProtocol.VERSION) {
                throw new IllegalStateException("Unsupported help catalog version: " + version);
            }

            String sourceId = in.readUTF();
            SourceType sourceType = SourceType.valueOf(in.readUTF());
            Instant generatedAt = Instant.ofEpochMilli(in.readLong());

            int size = in.readInt();
            var entries = new ArrayList<Entry>(size);

            for(int i = 0; i < size; i++) {
                String syntax = in.readUTF();
                String description = in.readUTF();
                String permission = in.readBoolean() ? in.readUTF() : null;
                String command = in.readUTF();
                int argumentSize = in.readInt();
                var arguments = new ArrayList<NetworkHelpCatalog.Argument>(argumentSize);
                for (int j = 0; j < argumentSize; j++) {
                    String argumentSyntax = in.readUTF();
                    boolean optional = in.readBoolean();
                    String argumentDescription = in.readUTF();
                    arguments.add(new NetworkHelpCatalog.Argument(argumentSyntax, optional, argumentDescription));
                }

                entries.add(new Entry(syntax, description, permission, command, arguments));
            }
            return new NetworkHelpCatalog(sourceId, sourceType, generatedAt, entries);

        } catch (IOException exception) {
            throw new IllegalStateException("Failed to decode help catalog", exception);
        }
    }
}
