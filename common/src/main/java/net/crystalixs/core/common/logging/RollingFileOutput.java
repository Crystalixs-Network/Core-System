package net.crystalixs.core.common.logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public final class RollingFileOutput implements LogManager.LogOutput {

    private static final String FILE_PREFIX = "log";
    private static final DateTimeFormatter FILE_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String FILE_EXTENSION = ".log";

    private final ReentrantLock lock = new ReentrantLock();
    private final LogRenderer renderer = new LogRenderer();
    private final Path directory;
    private final long maxBytes;
    private final int maxFiles;

    private BufferedWriter writer;
    private LocalDate activeDate;
    private int activeIndex;
    private Path activeFile;

    public RollingFileOutput(Path directory, long maxBytes, int maxFiles) throws IOException {
        if (maxBytes <= 0) {
            throw new IllegalArgumentException("maxBytes must be positive");
        }
        if (maxFiles <= 0) {
            throw new IllegalArgumentException("maxFiles must be positive");
        }
        this.directory = directory;
        this.maxBytes = maxBytes;
        this.maxFiles = maxFiles;

        Files.createDirectories(directory);
        openWriter(LocalDate.now());
        pruneOldFiles();
    }

    @Override
    public void log(LogManager.Entry entry) {
        lock.lock();
        try {
            rotateIfRequired(LocalDate.from(entry.timestamp().atZone(java.time.ZoneId.systemDefault())));
            writer.write(renderer.format(entry));
            writer.newLine();
            writer.flush();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to write log file " + activeFile, exception);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        lock.lock();
        try {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to close log file " + activeFile, exception);
        } finally {
            lock.unlock();
        }
    }

    private void rotateIfRequired(LocalDate recordDate) throws IOException {
        if (!recordDate.equals(activeDate)) {
            reopen(recordDate, 0);
            pruneOldFiles();
            return;
        }

        if (activeFile != null && Files.exists(activeFile) && Files.size(activeFile) >= maxBytes) {
            reopen(activeDate, activeIndex + 1);
            pruneOldFiles();
        }
    }

    private void openWriter(LocalDate date) throws IOException {
        this.activeDate = date;
        this.activeIndex = findLatestIndex(date);
        this.activeFile = resolveFile(date, activeIndex);
        this.writer = Files.newBufferedWriter(activeFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private void reopen(LocalDate date, int index) throws IOException {
        if (writer != null) {
            writer.close();
        }
        this.activeDate = date;
        this.activeIndex = index;
        this.activeFile = resolveFile(date, index);
        this.writer = Files.newBufferedWriter(activeFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private int findLatestIndex(LocalDate date) throws IOException {
        String baseName = baseName(date);
        try (var stream = Files.list(directory)) {
            return stream
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.startsWith(baseName) && name.endsWith(FILE_EXTENSION))
                    .mapToInt(this::extractIndex)
                    .max()
                    .orElse(0);
        }
    }

    private void pruneOldFiles() throws IOException {
        try (var stream = Files.list(directory)) {
            List<Path> files = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith(FILE_PREFIX + "_"))
                    .filter(path -> path.getFileName().toString().endsWith(FILE_EXTENSION))
                    .sorted(Comparator.comparing(Path::getFileName).reversed())
                    .toList();

            for (int i = maxFiles; i < files.size(); i++) {
                Files.deleteIfExists(files.get(i));
            }
        }
    }

    private Path resolveFile(LocalDate date, int index) {
        String fileName = baseName(date) + "_" + index + FILE_EXTENSION;
        return directory.resolve(fileName);
    }

    private String baseName(LocalDate date) {
        return FILE_PREFIX + "_" + FILE_DATE.format(date);
    }

    private int extractIndex(String fileName) {
        int extensionIndex = fileName.lastIndexOf(FILE_EXTENSION);
        String withoutExtension = extensionIndex == -1 ? fileName : fileName.substring(0, extensionIndex);
        int separator = withoutExtension.lastIndexOf('_');
        if (separator <= FILE_PREFIX.length() + 10) {
            throw new IllegalStateException("Unexpected log file name: " + fileName);
        }
        return Integer.parseInt(withoutExtension.substring(separator + 1));
    }
}
