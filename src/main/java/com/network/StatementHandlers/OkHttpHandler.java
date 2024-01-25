package com.network.StatementHandlers;

import com.network.Utils.BodyUtils;
import org.openjdk.jol.vm.VM;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;

import java.util.ArrayList;

public class OkHttpHandler extends AbstractHttpHandler{

    ArrayList<Local> responses;
    public OkHttpHandler(Body body) {
        super(body);
    }

    @Override
    public void caseAssignStmt(AssignStmt stmt) {
        super.caseAssignStmt(stmt);
        if (stmt.getLeftOp().getType().toString().equals("okhttp3.Response")){
            JimpleLocal responseString = addLocal();
            instrumentCall(stmt, responseString);
            JimpleLocal data  = getBody(stmt, (JimpleLocal) stmt.getLeftOp(), responseString);

        }
    }


    public JimpleLocal addLocal(){
        Local responseString = BodyUtils.addLocal(body, "responseString", "java.lang.String");
        return (JimpleLocal) responseString;

    }
    public JimpleLocal getBody(Stmt stmt, JimpleLocal data, Local responseString){
        Local tmpRef = Jimple.v().newLocal("body", RefType.v("okhttp3.ResponseBody"));

        Unit u = stmt;
        PatchingChain<Unit> units = body.getUnits();

        body.getLocals().add(tmpRef);


        // virtualinvoke data.<okhttp3.Response: okhttp3.ResponseBody body()>();
        SootClass declarer = Scene.v().getSootClass("okhttp3.Response");
        Type returnType = Scene.v().getSootClass("okhttp3.ResponseBody").getType();


        SootMethod bodyString = Scene.v().getSootClass("okhttp3.ResponseBody").getMethod("java.lang.String string()");
        units.insertAfter(Jimple.v().newAssignStmt(responseString, Jimple.v().newVirtualInvokeExpr(tmpRef, bodyString.makeRef())), u);

        units.insertAfter(Jimple.v().newAssignStmt(
                tmpRef, Jimple.v().newVirtualInvokeExpr((Local) data, Scene.v().makeMethodRef(declarer, "body", new ArrayList<>(), returnType, false))), u);

        return (JimpleLocal) responseString;
    }


}
