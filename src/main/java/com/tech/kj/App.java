package com.tech.kj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        if (args.length < 2 && args.length > 2) {
            throw new RuntimeException("invalid argument passed");
        }
        String operation = "";
        Path path = Paths.get("D:\\Intelij_WS\\john_cricket\\wc_tool_c1\\wc_tool\\test.txt");
        Optional<OperationType> operationType = OperationType.getOperationTypeByName(operation);

        operationType.ifPresentOrElse(value -> performByOperation(value, path),
                () -> System.out.println(funtionToDefaultOps(path).apply(path).result));
    }

    public static void performByOperation(OperationType operation, Path path) {
        Function<Path, OperationResult> functionToApply = null;
        switch (operation) {
            case c -> functionToApply = functionToCountByte(path);
            case l -> functionToApply = functionToCountLines(path);
            case w -> functionToApply = functionToTotalWords(path);
            case m -> functionToApply = functionToTotalCharacter(path);
            default -> functionToApply = funtionToDefaultOps(path);
        }
        OperationResult result = functionToApply.apply(path);
        if (result.ifSuccess()) {
            System.out.println(result.result);
        } else {
            System.out.println(result.exceptionMessage);
        }
    }

    private static Function<Path, OperationResult> funtionToDefaultOps(Path path) {
        return path1 -> {
            OperationResult totalLines = functionToCountLines(path).apply(path);
            OperationResult totalWords = functionToTotalWords(path).apply(path);
            OperationResult totalBytes = functionToCountByte(path).apply(path);
            String result = totalLines.result + "\t " + totalWords.result + "\t" + totalBytes.result + "\t" + path.getFileName();
            OperationResult<String> opResult = new OperationResult<String>(result, null);
            return opResult;
        };
    }

    public record OperationResult<T>(T result, String exceptionMessage) {
        //constructor for success
        public OperationResult(T result) {
            this(result, null);
        }

        //constructor for failure message
        public OperationResult(String exceptionMessage) {
            this(null, exceptionMessage);
        }


        // verify if OperationResult record hold success message
        public boolean ifSuccess() {
            return (result != null && exceptionMessage == null) ? true : false;
        }

        // verify if OperationResult record holds error message
        public boolean isFailure() {
            return (exceptionMessage != null && result == null) ? true : false;
        }
    }

    private static Function<Path, OperationResult> functionToCountByte(final Path path) {
        return path1 -> {
            try {
                long countByte = Files.size(path);
                return new OperationResult(countByte);
            } catch (IOException exception) {
                return new OperationResult("Invalid file path");
            }

        };
    }

    private static Function<Path, OperationResult> functionToCountLines(final Path path) {
        return path1 -> {
            try {
                long lines = Files.lines(path).parallel().count();
                return new OperationResult(lines);
            } catch (IOException exception) {
                return new OperationResult("Invalid file path");
            }

        };
    }

    private static Function<Path, OperationResult> functionToTotalWords(final Path path) {
        return path1 -> {
            try {
                long totalWords =
                        Files.lines(path).parallel()
                                .flatMap(line -> Arrays.stream(line.split("\\s+")))
                                .filter(word -> !word.isEmpty())
                                .count();
                return new OperationResult(totalWords);
            } catch (IOException exception) {
                return new OperationResult("Invalid file path");
            }

        };
    }

    private static Function<Path, OperationResult> functionToTotalCharacter(final Path path) {
        return path1 -> {
            try {
                long totalChars =
                        Files.lines(path).parallel()
                                .mapToLong(String::length)
                                .sum();
                long totalLines = Files.lines(path).count();  // Count the number of lines

                // Check if the system uses Windows-style line endings (\r\n)
                boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

                // Add line break characters (\n or \r\n)
                totalChars += isWindows ? 2 * totalLines : totalLines;  // 2 characters for Windows, 1 for Unix
                return new OperationResult(totalChars);
            } catch (IOException exception) {
                return new OperationResult("Invalid file path");
            }

        };
    }
}
