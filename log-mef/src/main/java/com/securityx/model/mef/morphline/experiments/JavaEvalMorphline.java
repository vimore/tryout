package com.securityx.model.mef.morphline.experiments;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Configs;
import org.slf4j.Logger;

public interface JavaEvalMorphline {

    boolean process(Record record, Configs configs,
            Command parent, Command child,
            MorphlineContext context, Logger logger);
}
