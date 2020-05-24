package com.Isles.BuildSystem.ApplicationCore;

/*
 * Interface only contains static methods that shall serve as a library to verifiers and interpreters.
 * Functionality that requires a state shall be placed in the core and managed by it.
 */

import com.Isles.BuildSystem.Container.IAttributeContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.System.exit;

public interface CoreFunctions {

    /* STRING operations */
    static String concat(String string1, String string2) {
        return string1.concat(string2);
    }

    static List<String> concat(List<String> string1, String string2) {
        List<String> l = new ArrayList<>();
        for(String s:string1) {
            l.add(s.concat(string2));
        }
        return l;
    }

    static List<String> split(String string, String delimiter) {
        String[] v = string.split(delimiter);
        List<String> l = new ArrayList<>();
        for(String s:v) {
            l.add(s);
        }
        return l;
    }

    static String replace(String string, String oldStr, String newStr) {
        return string.replace(oldStr,newStr);
    }

    static String substring(String string, int index1, int index2) {
        return string.substring(index1, index2);
    }

    static List<String> replace(List<String> string, String oldStr, String newStr) {
        List<String> l = new ArrayList<>();
        for(String s:string) {
            l.add(s.replace(oldStr,newStr));
        }
        return l;
    }

    static List<String> substring(List<String> string, int index1, int index2) {
        List<String> l = new ArrayList<>();
        for(String s:string) {
            l.add(s.substring(index1,index2));
        }
        return l;
    }

    static int indexof(String string, String indexString) {
        return string.indexOf(indexString);
    }

    /* SHELL operations */
    static int executeCommand(String command) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String os = System.getProperty("os.name");
        Process p = null;
        if(os.startsWith("Windows")) {
            processBuilder = processBuilder.command("cmd.exe",command);
        } else if(os.startsWith("Linux")) {
            processBuilder = processBuilder.command("bash",command);
        }
        try {
            p = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(p == null || p.exitValue() != 0) {
            System.out.println("Cannot execute the command:"+command);
            if(p == null)exit(-99);
            System.out.println("Exit code:"+p.exitValue());
            exit(p.exitValue());
        }
        return p.exitValue();

    }

    static int executeCommand(String program, String args) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        Process p = null;
        processBuilder = processBuilder.command(program,args);
        try {
            p = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(p == null || p.exitValue() != 0) {
            System.out.println("Cannot successfully execute "+program+" with the parameters "+args);
            if(p == null)exit(-99);
            System.out.println("Exit code:"+p.exitValue());
            exit(p.exitValue());
        }
        return p.exitValue();
    }

    /* DIRECTORY and FILE operations */
    static boolean createDirectory(String path) {
        Path p = null;
        try {
            p = Files.createDirectory(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(p != null) {
            return true;
        }
        return false;
    }

    static boolean deleteDirectory(String path) {
        File f = new File(path);
        if(f.isDirectory()) {
            for(String ff: f.list()) {
                (new File(ff)).delete();
            }
            return f.delete();
        }
        return false;
    }

    static boolean directoryExists(String path) {
        File f = new File(path);
        if(f.exists() && f.isDirectory()) {
            return true;
        }
        return false;
    }

    static boolean fileExists(String path) {
        File f = new File(path);
        if(f.exists() && f.isFile()) {
            return true;
        }
        return false;
    }

    static boolean copyFile(String oldPath, String newPath) {
        Path p = null;
        try {
            p = Files.copy(Paths.get(oldPath),Paths.get(newPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(p != null) {
            return true;
        }
        return false;
    }

    static boolean moveFile(String oldPath, String newPath) {
        Path p = null;
        try {
            p = Files.move(Paths.get(oldPath),Paths.get(newPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(p != null) {
            return true;
        }
        return false;
    }

    static boolean createFile(String path) {
        File file = new File(path);
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    static boolean deleteFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    static int size(IAttributeContainer<String> a) {
        return a.getValue().size();
    }

    /* TIME functions for the dependencies */

    static long getTimestamp(String filename) {
        File file = new File(filename);
        return file.lastModified();
    }

    static long getCurrentTime() {
        Date date = new Date();
        return date.getTime();
    }


    /* ERROR functions for the verifiers and interpreters */

    static void errorFunction(ICore core, String expected, String found) {
        System.out.print("Expected '");
        System.out.print(expected);
        System.out.print("' but found '");
        System.out.print(found);
        System.out.print("'\n");
        core.error();
    }

    static void errorFunctionForbiddenElement(ICore core, String found) {
        System.out.print("Found the keyword '");
        System.out.print(found);
        System.out.print("', but this keyword cannot be used here!\n");
        core.error();
    }

    static void errorFunctionUnexpectedEnd(ICore core) {
        System.out.println("Reached unexpectedly the end!");
        core.error();
    }

    static boolean isKeyword(String token) {
        /* realise with list of keywords and contains */
        if(token.equalsIgnoreCase("begin") ||
           token.equalsIgnoreCase("end") ||
           token.equalsIgnoreCase("module") ||
           token.equalsIgnoreCase("public") ||
           token.equalsIgnoreCase(":") ||
           token.equalsIgnoreCase("local") ||
           token.equalsIgnoreCase("function") ||
           token.equalsIgnoreCase("inherited") ||
           token.equalsIgnoreCase("variables") ||
           token.equalsIgnoreCase("rule") ||
           token.equalsIgnoreCase("dependency") ||
           token.equalsIgnoreCase("interface") ||
           token.equalsIgnoreCase("input") ||
           token.equalsIgnoreCase("update") ||
           token.equalsIgnoreCase("if") ||
           token.equalsIgnoreCase("then") ||
           token.equalsIgnoreCase("else") ||
           token.equalsIgnoreCase("for") ||
           token.equalsIgnoreCase("var") ||
           token.equalsIgnoreCase("in") ||
           token.equalsIgnoreCase("is") ||
           token.equalsIgnoreCase("==") ||
           token.equalsIgnoreCase("!=") ||
           token.equalsIgnoreCase("=") ||
           token.equalsIgnoreCase(":=") ||
           token.equalsIgnoreCase("+=") ||
           token.equalsIgnoreCase("<") ||
           token.equalsIgnoreCase(">") ||
           token.equalsIgnoreCase("<=") ||
           token.equalsIgnoreCase(">=") ||
           token.equalsIgnoreCase("&&") ||
           token.equalsIgnoreCase("||") ||
           token.equalsIgnoreCase("(") ||
           token.equalsIgnoreCase(")") ||
           token.equalsIgnoreCase("]") ||
           token.equalsIgnoreCase("[") ||
           token.equalsIgnoreCase(";") ||
           token.equalsIgnoreCase(",") ||
           token.equalsIgnoreCase("/") ||
           token.equalsIgnoreCase("{") ||
           token.equalsIgnoreCase("}") ||
           token.equalsIgnoreCase(".")
        ) {
            return true;
        }
        return false;
    }

    static boolean isBultinFunction(String t) {
        if(t.equalsIgnoreCase("concat") ||
                t.equalsIgnoreCase("split") ||
                t.equalsIgnoreCase("replace") ||
                t.equalsIgnoreCase("substring") ||
                t.equalsIgnoreCase("indexof") ||
                t.equalsIgnoreCase("timestamp") ||
                t.equalsIgnoreCase("size") ||
                t.equalsIgnoreCase("deletefile") ||
                t.equalsIgnoreCase("createfile") ||
                t.equalsIgnoreCase("deletedir") ||
                t.equalsIgnoreCase("createdir") ||
                t.equalsIgnoreCase("print") ||
                t.equalsIgnoreCase("execute") ||
                t.equalsIgnoreCase("path") ||
                t.equalsIgnoreCase("modulename") ||
                t.equalsIgnoreCase("movefile") ||
                t.equalsIgnoreCase("copyfile") ||
                t.equalsIgnoreCase("direxists") ||
                t.equalsIgnoreCase("fileexists") ||
                t.equalsIgnoreCase("MODULEINTERFACE")
        ) {
            return true;
        }
        return false;
    }

    static boolean isComparator(String s){
        if(s.equalsIgnoreCase("<") ||
           s.equalsIgnoreCase(">") ||
           s.equalsIgnoreCase("!=") ||
           s.equalsIgnoreCase("<=") ||
           s.equalsIgnoreCase(">=") ||
           s.equalsIgnoreCase("==")
        ){
            return true;
        }
        return false;
    }

    static void print(List<String> s) {
        s.forEach(System.out::println);

    }

    static String getmodulename(ICore core) {
        return core.getModuleName();
    }

    static String getInterface(ICore core, String iname) {
        return core.getInterface(iname);
    }

    static String path(ICore core) {
        return core.getPath();
    }


    static List<String> executeBuiltinFunction(ICore core, String functionName, String parameters){
        List<String> result = new ArrayList<>();
        String args[] = parameters.split(",");

        List<String> arg0 = null;
        List<String> arg1 = null;
        List<String> arg2 = null;

        if(args.length > 0)arg0 = core.getAttribute(args[0]).getValue();
        if(args.length > 1)arg1 = core.getAttribute(args[1]).getValue();
        if(args.length > 2)arg2 = core.getAttribute(args[2]).getValue();

        switch (functionName.toLowerCase()){
            case "concat":
                result = concat(arg0, arg1.get(0));
                break;
            case "split":
                result = split(arg0.get(0), arg1.get(0));
                break;
            case "replace":
                result = replace(arg0, arg1.get(0),arg2.get(0));
                break;
            case "substring":
                result = substring(arg0, Integer.getInteger(arg1.get(0)), Integer.getInteger(arg2.get(0)));
                break;
            case "indexof":
                result.add(Integer.toString(indexof(args[0],args[1])));
                break;
            case "timestamp":
                result.add(Long.toString(getTimestamp(arg0.get(0))));
                break;
            case "size":
                result.add(Integer.toString(size(core.getAttribute(args[0]))));
                break;
            case "deletefile":
                if(deleteFile(arg0.get(0)) == true){
                    result.add("true");
                }else{
                    result.add("false");
                }
                break;
            case "createfile":
                if(createFile(arg0.get(0)) == true){
                    result.add("true");
                }else{
                    result.add("false");
                }
                break;
            case "deletedir":
                if(deleteDirectory(arg0.get(0)) == true){
                    result.add("true");
                }else{
                    result.add("false");
                }
                break;
            case "createdir":
                if(createDirectory(arg0.get(0)) == true){
                    result.add("true");
                }else{
                    result.add("false");
                }
                break;
            case "print":
                print(arg0);
                break;
            case "execute":
                if(args.length == 1){
                    result.add(Integer.toString(executeCommand(arg0.get(0))));
                }else if(args.length == 2){
                    result.add(Integer.toString(executeCommand(arg0.get(0),arg1.get(0))));
                }
                break;
            case "path":
                result.add(path(core));
                break;
            case "modulename":
                result.add(getmodulename(core));
                break;
            case "movefile":
                if(moveFile(arg0.get(0),arg1.get(0)) == true){
                    result.add("true");
                }else{
                    result.add("false");
                }
                break;
            case "copyfile":
                if(copyFile(arg0.get(0),arg1.get(0)) == true){
                    result.add("true");
                }else{
                    result.add("false");
                }
                break;
            case "direxists":
                if(directoryExists(arg0.get(0)) == true){
                    result.add("true");
                }else{
                    result.add("false");
                }
                break;
            case "fileexists":
                if(fileExists(arg0.get(0)) == true){
                    result.add("true");
                }else{
                    result.add("false");
                }
                break;
            case "MODULEINTERFACE":
                //TODO maybe this has to be split in multiple strings
                result.add(core.getInterface(arg0.get(0)));
                break;
            default:
                System.out.println("Trying to execute a non-existing built-in function. Exiting...");
                exit(-1);
        }

        return result;
    }

    static boolean evaluateNumberOfArgsBuiltInFunctions(String functionName, int numArgs){
        switch(functionName){
            case "movefile":
            case "copyfile":
            case "indexof":
            case "concat":
            case "split":
            case "replace":
                return numArgs == 2;
            case "substring":
                return numArgs == 3;
            case "timestamp":
            case "size":
            case "deletefile":
            case "createfile":
            case "deletedir":
            case "createdir":
            case "print":
            case "modulename":
            case "direxists":
            case "fileexists":
            case "MODULEINTERFACE":
                return numArgs == 1;
            case "execute":
                return numArgs == 1 || numArgs == 2;
            case "path":
                return numArgs == 0;
        }
        return false;
    }
}
