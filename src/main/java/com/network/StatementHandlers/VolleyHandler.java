package com.network.StatementHandlers;

import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;

public class VolleyHandler extends AbstractHttpHandler {
    public VolleyHandler(Body body) {
        super(body);
        boolean isListener = false;
        for (SootClass interf : body.getMethod().getDeclaringClass().getInterfaces()){
            if (interf.getName().equals("com.android.volley.Response$Listener")){
                isListener = true;
                break;
            }
        }

        if (isListener) {
            if (body.getMethod().getName().equals("onResponse"))
                instrumentBody(body);
        }
    }



    private void instrumentBody(Body body){
        Local response = body.getParameterLocals().get(0);
        Stmt statement = null;
        for (Unit u: body.getUnits()){
            if (u.toString().contains("onResponse")){
                statement = (Stmt) u;
            }
        }
        if (statement != null) {
            instrumentCall(statement, (JimpleLocal) response);
        }
    }


}
