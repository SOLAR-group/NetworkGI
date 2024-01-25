package com.network;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

public class InstrumenterOptions extends OptionsBase {

    @Option(
            name="apkPath",
            abbrev = 'a',
            defaultValue = ""
    )
    public String apkPath;

    @Option(
            name="packageName",
            abbrev = 'p',
            defaultValue = ""
    )
    public String packageName;


}
