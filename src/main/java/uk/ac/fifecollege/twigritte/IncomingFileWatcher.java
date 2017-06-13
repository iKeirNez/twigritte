package uk.ac.fifecollege.twigritte;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class IncomingFileWatcher {
    private static final Log LOG = LogFactory.getLog(IncomingFileWatcher.class);

    private final File watchDirectory;
    private final Map<CompletableFuture<File>, FileFilter> jobs = new ConcurrentHashMap<>();

    private WatcherThread watcherThread = null;

    public IncomingFileWatcher(File watchDirectory) {
        this.watchDirectory = watchDirectory;
    }

    public CompletableFuture<File> watchFor(FileFilter filter) {
        CompletableFuture<File> future = new CompletableFuture<>();
        jobs.put(future, filter);
        return future;
    }

    public void start() {
        if (watcherThread == null) {
            try {
                Path watchPath = watchDirectory.toPath();

                WatchService watcher = watchPath.getFileSystem().newWatchService();
                watchPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
                watcherThread = new WatcherThread(watcher, Executors.newSingleThreadExecutor());
                watcherThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalStateException("Watcher already started.");
        }
    }

    public void stop() {
        if (watcherThread != null && watcherThread.running) {
            watcherThread.stop();
        } else {
            throw new IllegalStateException("Watcher thread not running.");
        }
    }

    public class WatcherThread implements Runnable {
        private final WatchService watcher;
        private final ExecutorService executor;
        private Future<?> future = null;

        protected boolean running;

        public WatcherThread(WatchService watcher, ExecutorService executor) {
            this.watcher = watcher;
            this.executor = executor;
        }

        public void start() {
            LOG.info("Starting incoming file watcher");
            running = true;
            future = executor.submit(this);
        }

        public void stop() {
            LOG.info("Stopping incoming file watcher");
            running = false;
            future.cancel(true);
        }

        @Override
        public void run() {
            try {
                WatchKey watchKey = watcher.take();

                while (running) {
                    List<WatchEvent<?>> events = watchKey.pollEvents();
                    for (WatchEvent<?> event : events) {
                        File file = new File(watchDirectory, event.context().toString());
                        WatchEvent.Kind<?> kind = event.kind();

                        if (kind == StandardWatchEventKinds.ENTRY_CREATE || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                            jobs.entrySet().stream()
                                    .filter(entry -> entry.getValue().accept(file))
                                    .map(Map.Entry::getKey)
                                    .forEach(future -> {
                                        future.complete(file);
                                        jobs.remove(future);
                                    });
                        }
                    }
                }
            } catch (InterruptedException e) {
                if (running) {
                    LOG.warn("Watcher interrupted", e);
                }
            }
        }
    }
}
