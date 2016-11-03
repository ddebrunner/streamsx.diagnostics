/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016
*/
package com.ibm.streamsx.diagnostics.ops;


import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.ibm.streams.operator.AbstractOperator;
import com.ibm.streams.operator.OperatorContext;
import com.ibm.streams.operator.ProcessingElement;
import com.ibm.streams.operator.logging.LoggerNames;
import com.ibm.streams.operator.model.PrimitiveOperator;
import com.ibm.streams.operator.version.Product;

/**
 */
@PrimitiveOperator(
description="Java Operator JavaEnvironment"
/*vmArgs={"-Xmx2G", "-Ddan.test=WatsonWest"}*/)
public class JavaEnvironment extends AbstractOperator {
	
	private static final Logger trace = Logger.getLogger(JavaEnvironment.class.getName());
	private static final Logger log = Logger.getLogger(LoggerNames.LOG_FACILITY);
	
	@Override
	public void initialize(OperatorContext context) throws Exception {
		super.initialize(context);
		
		streamsInfo();
		
		vmInfo();
		
		jobInfo();
		
		environment();
		
		systemProperties();
		
		logging();
	}
	
	private void streamsInfo() {
		section("IBM Streams information");
		
		log("STREAMS_INSTALL=" + System.getenv("STREAMS_INSTALL"));
		
		ProcessingElement pe = getOperatorContext().getPE();
		log("Domain=" + pe.getDomainId());
		log("Instance=" + pe.getInstanceId());
		
		log("Version=" + Product.getVersion());
	}
	
	private void vmInfo() {
		section("JVM information");
		
		Runtime rt = Runtime.getRuntime();
						
		log("Max   Memory=" + toMB(rt.maxMemory()));
		log("Total Memory=" + toMB(rt.totalMemory()));
		log("Free  Memory=" + toMB(rt.freeMemory()));
	}
	
	private static String toMB(long value) {
		return Long.toString(value / 1000 / 1000)+"MB";
	}
	
	private void jobInfo() {
		section("Job information");
		
		ProcessingElement pe = getOperatorContext().getPE();
		log("JobId=" + pe.getJobId());
		log("JobName=" + pe.getJobName());
		log("JobGroup=" + pe.getJobGroup());
		log("PEId=" + pe.getPEId());
		log("ApplicationName=" + pe.getApplicationName());
		log("ApplicationScope=" + pe.getApplicationScope());
	}
	
	private void environment() {
		section("Environment variables");
		
		Set<String> names = new TreeSet<>(System.getenv().keySet());
		
		for (String name : names)
			log(name + "=" + System.getenv(name));
	}
	
	private void systemProperties() {
		section("Java system properties");
		Set<Object> names = new TreeSet<>(System.getProperties().keySet());
		for (Object name : names)
			log(name.toString() + "=" + System.getProperty(name.toString()));
	}
	
	public void logging() {
		section("IBM Streams Log & Trace");
		
		String logLevel = log.getLevel() == null ? "unknown" : log.getLevel().getName();
		log("Log level:" + logLevel + " (Java name)");
		
		Logger root = Logger.getLogger("");
		String traceLevel = root.getLevel() == null ? "unknown" : root.getLevel().getName();
		log("Trace level:" + traceLevel + " (Java name)");
	}
	
	private void section(String section) {
		log("");
		log("---------------------------------------------------------");
		log(section);
		log("");
	}
	
	private void log(String message) {
		System.out.println(message);
		trace.info(message);
	}
}
