package com.network.StatementHandlers;

import com.network.Utils.BodyUtils;
import soot.*;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.internal.JimpleLocal;

public abstract class AbstractHttpHandler extends AbstractStmtSwitch {

    Body body;


    public  AbstractHttpHandler(Body body){
        super();
        this.body = body;
    }


    public Local addMethodString(Stmt stmt){
        Local tmpRef = Jimple.v().newLocal("methodName", RefType.v("java.lang.String"));

        Unit u = stmt;
        PatchingChain<Unit> units = body.getUnits();
        String methodSig = body.getMethod().getSignature();
        body.getLocals().add(tmpRef);
        units.insertBefore(Jimple.v().newAssignStmt(
                tmpRef, StringConstant.v(methodSig)),u);
        return tmpRef;
    }

    public void instrumentCall(Stmt stmt, JimpleLocal data) {
        Local tag = BodyUtils.addTag(body);

        Unit u = stmt;
        PatchingChain<Unit> units = body.getUnits();
        // insert "tmpRef = java.lang.System.out;"
        units.insertBefore(Jimple.v().newAssignStmt(
                tag, StringConstant.v("AndroidHttpProfiler")),u);


        Local methodSig = addMethodString(stmt);

        // insert "Log.d(Tag, Method);
        SootMethod logD = Scene.v().getSootClass("android.util.Log").getMethod("int d(java.lang.String,java.lang.String)");

        SootMethod toString = Scene.v().getSootClass("java.lang.Object").getMethod("java.lang.String toString()");

        // insert String dataString = data.toString()
        Local dataString = BodyUtils.addLocal(body, "dataString", "java.lang.String");


        // insert "Log.d(Tag, dataString);"
        units.insertAfter(Jimple.v().newInvokeStmt(
                Jimple.v().newStaticInvokeExpr(logD.makeRef(), tag, dataString)), u);

        units.insertAfter(Jimple.v().newAssignStmt(
                dataString, Jimple.v().newVirtualInvokeExpr(data, toString.makeRef())), u);

        units.insertAfter(Jimple.v().newInvokeStmt(
                Jimple.v().newStaticInvokeExpr(logD.makeRef(), tag, methodSig)), u);



        //check that we did not mess up the Jimple
        body.validate();
        System.out.println("Added instrumention to " + body.getMethod().getDeclaringClass().toString());
    }
}
