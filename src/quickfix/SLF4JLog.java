/*******************************************************************************
 * Copyright (c) quickfixengine.org  All rights reserved. 
 * 
 * This file is part of the QuickFIX FIX Engine 
 * 
 * This file may be distributed under the terms of the quickfixengine.org 
 * license as defined by quickfixengine.org and appearing in the file 
 * LICENSE included in the packaging of this file. 
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING 
 * THE WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 * 
 * See http://www.quickfixengine.org/LICENSE for licensing information. 
 * 
 * Contact ask@quickfixengine.org if any conditions of this licensing 
 * are not clear to you.
 ******************************************************************************/

package quickfix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SLF4JLog implements quickfix.Log {
    public static final String DEFAULT_EVENT_CATEGORY = "quickfixj.event";
    public static final String DEFAULT_INCOMING_MSG_CATEGORY = "quickfixj.msg.incoming";
    public static final String DEFAULT_OUTGOING_MSG_CATEGORY = "quickfixj.msg.outgoing";

    private final Logger eventLog;
    private final Logger incomingMsgLog;
    private final Logger outgoingMsgLog;
    private final String logPrefix;
    
    public SLF4JLog(SessionID sessionID, String eventCategory, String incomingMsgCategory,
            String outgoingMsgCategory, boolean prependSessionID) {
        logPrefix = prependSessionID ? (sessionID + ": ") : null;
        eventLog = getLogger(sessionID, eventCategory, DEFAULT_EVENT_CATEGORY);
        incomingMsgLog = getLogger(sessionID, incomingMsgCategory, DEFAULT_INCOMING_MSG_CATEGORY);
        outgoingMsgLog = getLogger(sessionID, outgoingMsgCategory, DEFAULT_OUTGOING_MSG_CATEGORY);
    }

    private Logger getLogger(SessionID sessionID, String category, String defaultCategory) {
        return LoggerFactory.getLogger(category != null
                ? substituteVariables(sessionID, category)
                : defaultCategory);
    }

    private static final String FIX_MAJOR_VERSION_VAR = "\\$\\{fixMajorVersion}";
    private static final String FIX_MINOR_VERSION_VAR = "\\$\\{fixMinorVersion}";
    private static final String SENDER_COMP_ID_VAR = "\\$\\{senderCompID}";
    private static final String TARGET_COMP_ID_VAR = "\\$\\{targetCompID}";
    private static final String QUALIFIER_VAR = "\\$\\{qualifier}";

    private String substituteVariables(SessionID sessionID, String category) {
        String[] beginStringFields = sessionID.getBeginString().split("\\.");
        String processedCategory = category;
        processedCategory = processedCategory.replaceAll(FIX_MAJOR_VERSION_VAR,
                beginStringFields[1]);
        processedCategory = processedCategory.replaceAll(FIX_MINOR_VERSION_VAR,
                beginStringFields[2]);
        processedCategory = processedCategory.replaceAll(SENDER_COMP_ID_VAR, sessionID
                .getSenderCompID());
        processedCategory = processedCategory.replaceAll(TARGET_COMP_ID_VAR, sessionID
                .getTargetCompID());
        processedCategory = processedCategory.replaceAll(QUALIFIER_VAR, sessionID
                .getSessionQualifier());
        return processedCategory;
    }

    public void onEvent(String text) {
        log(eventLog, text);
    }

    public void onIncoming(String message) {
        log(incomingMsgLog, message);
    }

    public void onOutgoing(String message) {
        log(outgoingMsgLog, message);
    }

    private void log(Logger log, String text) {
        log.info(logPrefix != null ? (logPrefix + text) : text);
    }

    public void clear() {
        onEvent("Log clear operation is not supported: " + getClass().getName());
    }

}