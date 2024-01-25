package com.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.network.StatementHandlers.HttpURLHandler;
import com.network.StatementHandlers.OkHttpHandler;
import com.network.StatementHandlers.VolleyHandler;
import com.google.devtools.common.options.OptionsParser;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.dava.toolkits.base.AST.transformations.SuperFirstStmtHandler;
import soot.jimple.*;
import soot.options.Options;


public class AndroidInstrumenter {

    public static void main(String[] args) {


        Options.v().set_src_prec(Options.src_prec_apk);

        OptionsParser parser = OptionsParser.newOptionsParser(InstrumenterOptions.class);
        parser.parseAndExitUponError(args);
        InstrumenterOptions instrumenterOptions = parser.getOptions(InstrumenterOptions.class);

        String apkPath = instrumenterOptions.apkPath;
        String packageName = instrumenterOptions.packageName;

        //output as APK, too//-f J
        Options.v().set_output_format(Options.output_format_dex);
        Options.v().set_prepend_classpath(true);
        Options.v().set_process_multiple_dex(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_include_all(true);

        String androidJar =  "C:\\Users\\jcall\\android-platforms";
        Options.v().set_android_jars(androidJar); // The path to Android Platforms
        Options.v().set_src_prec(Options.src_prec_apk); // Determine the input is an APK
        Options.v().set_process_dir(Collections.singletonList(apkPath));
        // resolve the PrintStream and System soot-classes

        Scene.v().loadNecessaryClasses();
        ArrayList<Body> instrumented = new ArrayList<>();
        PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", new BodyTransformer() {

            @Override
            protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
                if (instrumented.contains(b)) {
                    return;
                }
                instrumented.add(b);
                final PatchingChain<Unit> units = b.getUnits();
                if (b.getMethod().getDeclaringClass().getPackageName().contains(packageName)) {
                    HttpURLHandler httpURLHandler = new HttpURLHandler(b);
                    OkHttpHandler okHttpHandler = new OkHttpHandler(b);
                        VolleyHandler volleyHandler = new VolleyHandler(b);
                        //important to use snapshotIterator here
                        for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext(); ) {
                            final Unit u = iter.next();
                            u.apply(httpURLHandler);
                            u.apply(volleyHandler);
                            u.apply(okHttpHandler);

                        }
                    }

            }

        }));
        String[] sootArgs = new String[]{"-cp","\"C:\\Users\\jcall\\android-platforms\\android-29\""};
        soot.Main.main(sootArgs);
    }


}