package com.network.StatementHandlers;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.util.NumberedSet;
import soot.util.NumberedString;


public class HttpURLHandler extends AbstractHttpHandler {

    JimpleLocal outputStream;
    JimpleLocal inputStream;
    JimpleLocal inputStreamReader;
    JimpleLocal outputStreamWriter;

    public HttpURLHandler(Body body) {
        super(body);

    }

    @Override
    public void caseAssignStmt(AssignStmt stmt) {
        super.caseAssignStmt(stmt);
        //find input/output streams
        findStreams(stmt);
        findReads(stmt);
    }

    @Override
    public void caseInvokeStmt(InvokeStmt stmt) {
        super.caseInvokeStmt(stmt);
        findRWs(stmt);
        findBufferedRWs(stmt);
        findWrites(stmt);

    }


    public void findStreams(AssignStmt stmt){
        try {
            SootMethodRef methodRef = ((JVirtualInvokeExpr) stmt.getRightOp()).getMethodRef();
            //check for input/output of httpurlconnection
            if (methodRef.getDeclaringClass().toString().equals("java.net.HttpURLConnection")){
                NumberedString signature = methodRef.getSubSignature();
                //output stream identification
                if (signature.toString().equals( "java.io.OutputStream getOutputStream()")){
                    outputStream = (JimpleLocal) stmt.getLeftOp();
                }
                //input stream identification
                if (signature.toString().equals("java.io.InputStream getInputStream()")){
                    inputStream = (JimpleLocal) stmt.getLeftOp();
                }}}
        catch (ClassCastException e){
            // Will trigger in assignment of primtives/fields, rather than method calls
            // but we don't care about those
            return;
        }
    }

    public void findRWs(InvokeStmt stmt){
        if (stmt.getInvokeExpr().getMethodRef().toString().equals("<java.io.InputStreamReader: void <init>(java.io.InputStream,java.lang.String)>")){
            if (inputStream !=null && stmt.getInvokeExpr().getArgCount() >=1 && stmt.getInvokeExpr().getArgs().get(0).equals(inputStream)){
                inputStreamReader = (JimpleLocal) ((JSpecialInvokeExpr) stmt.getInvokeExpr()).getBase();
            }
        }

        if (stmt.getInvokeExpr().getMethodRef().toString().equals("<java.io.OutputStreamWriter: void <init>(java.io.OutputStream,java.lang.String)>")){
            if (outputStream !=null && stmt.getInvokeExpr().getArgCount() >=1 && stmt.getInvokeExpr().getArgs().get(0).equals(outputStream)) {
                outputStreamWriter = (JimpleLocal) ((JSpecialInvokeExpr) stmt.getInvokeExpr()).getBase();
            }
        }

    }

    public void findBufferedRWs(InvokeStmt stmt) {

        //Reader
        if (stmt.getInvokeExpr().getMethodRef().toString().equals("<java.io.BufferedReader: void <init>(java.io.Reader)>")){
            if (inputStreamReader !=null && stmt.getInvokeExpr().getArgCount() >=1 && stmt.getInvokeExpr().getArgs().get(0).equals(inputStreamReader)){
                inputStreamReader = (JimpleLocal) ((JSpecialInvokeExpr) stmt.getInvokeExpr()).getBase();
            }
        }

        //Writer
        if (stmt.getInvokeExpr().getMethodRef().toString().equals("<java.io.BufferedWriter: void <init>(java.io.Writer)>")){
            if (outputStreamWriter !=null && stmt.getInvokeExpr().getArgCount() >=1 && stmt.getInvokeExpr().getArgs().get(0).equals(outputStreamWriter)){
                outputStreamWriter = (JimpleLocal) ((JSpecialInvokeExpr) stmt.getInvokeExpr()).getBase();
            }
        }
    }

    public void findWrites(InvokeStmt stmt) {
        if (stmt.getInvokeExpr().getMethodRef().toString().equals("<java.io.BufferedWriter: void write(java.lang.String)>")) {
            if ((((JVirtualInvokeExpr) stmt.getInvokeExpr()).getBase()).equals(outputStreamWriter)) {
                JimpleLocal data = (JimpleLocal) stmt.getInvokeExpr().getArgs().get(0);
                instrumentCall(stmt, data);
            }
        }
    }

    public void findReads(AssignStmt stmt){
        try{
        JVirtualInvokeExpr invokeExpr = ((JVirtualInvokeExpr) stmt.getRightOp());
        if (invokeExpr.getMethodRef().toString().equals("<java.io.BufferedReader: java.lang.String readLine()>")){
            if (invokeExpr.getBase().equals(inputStreamReader) ){
                JimpleLocal data = (JimpleLocal) stmt.getLeftOp();
                instrumentCall(stmt,data);
            }
        }}
        catch (ClassCastException e) {
            return;
        }
    }


}
