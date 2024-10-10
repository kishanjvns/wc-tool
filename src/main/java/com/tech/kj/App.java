package com.tech.kj;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

public class App {
    public static void main(String[] args) {
      /*  if (args.length < 2 && args.length > 2) {
            throw new RuntimeException("invalid argument passed");
        }*/
        String operation = "-c";
        Path path = Paths.get("D:\\Intelij_WS\\john_cricket\\wc_tool_c1\\wc_tool\\test.txt");
        Optional<OperationType> operationType = OperationType.getOperationTypeByName(operation);

        operationType.ifPresentOrElse(value -> performByOperation(value,path),
                () -> System.out.println("No value present"));
    }

    public static void performByOperation(OperationType operation,Path path) {
        System.out.println("Value is present: " + operation.name());
        Function<Path,OperationResult> functionToApply = null;
        switch (operation){
            case c -> functionToApply = functionToCountByte(path);
            case d -> functionToApply = functionToCountLines(path);
        }
        OperationResult result = functionToApply.apply(path);
        if(result.ifSuccess()){
            System.out.println(result.result);
        }else {
            System.out.println(result.exceptionMessage);
        }
    }
    public record OperationResult<T>(T result,String exceptionMessage){
        //constructor for success
        public OperationResult(T result){
            this(result,null);
        }
        //constructor for failure message
        public OperationResult(String exceptionMessage){
            this(null,exceptionMessage);
        }

        // verify if OperationResult record hold success message
        public boolean ifSuccess(){
            return (result != null && exceptionMessage == null) ? true:false;
        }

        // verify if OperationResult record holds error message
        public boolean isFailure(){
            return (exceptionMessage != null && result == null) ? true : false;
        }
    }

    private static Function<Path,OperationResult> functionToCountByte(final Path path){
        return path1 -> {
            try {
                long countByte = Files.size(path);
                return new OperationResult(countByte);
            }catch (IOException exception){
                return new OperationResult("Invalid file path");
            }

        };
    }

    private static Function<Path,OperationResult> functionToCountLines(final Path path){
        return path1 -> {
            try {
                long lines = Files.lines(path).parallel().count();
                return new OperationResult(lines);
            }catch (IOException exception){
                return new OperationResult("Invalid file path");
            }

        };
    }
}
