package com.vipshop.microscope.collector.analyzer;

import com.vipshop.microscope.thrift.Span;
import com.vipshop.microscope.client.exception.ExceptionData;
import com.vipshop.microscope.client.metric.MetricData;
import com.vipshop.microscope.client.system.SystemData;

/**
 * Message Analyze API.
 *
 * @author Xu Fei
 * @version 1.0
 */
public class MessageAnalyzer {

    private MessageAnalyzer() {
    }

    private static class MessageAnalyzerHolder {
        private static MessageAnalyzer messageAnalyzer = new MessageAnalyzer();
    }

    public static MessageAnalyzer getMessageAnalyzer() {
        return MessageAnalyzerHolder.messageAnalyzer;
    }

    public void analyze(Span span) {
    }

    public void analyze(MetricData metric) {
    }

    public void analyze(ExceptionData exception) {

    }

    public void analyze(SystemData system) {

    }

    public void analyzeAppLog(String logs){}

    public void analyzeGCLog(String logs){}
}
