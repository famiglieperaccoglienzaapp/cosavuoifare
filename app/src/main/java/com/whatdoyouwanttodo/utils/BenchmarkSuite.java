package com.whatdoyouwanttodo.utils;

import android.util.Log;

public class BenchmarkSuite {
	public static long TIME_INIT = 0;

	public static int NO_ACTION = -1;
	public static int ACTION_START = 0;
	public static int ACTION_STOP = 1;
	
	// XXX ids here
	public static int NO_ID = -1;
	public static int OPEN_REDABLE = 0;
	public static final int ADD_ABRAKADABRA = 1;
	public static final int ADD_ACTIVE_LISTENING = 2;
	public static final int ADD_CELLS = 3;
	public static final int CLOSE = 4;
	public static final int CREATE_DBU = 5;
	public static final int CLEAR = 6;
	
	private static int MAX_ACTIONS = 1024;
	
	public static int[] ids;
	private long[] milliseconds;
	private int[] actions;
	
	private int currentIndex;
	
	public static String[] idNames;

	private String suiteName;
	private long suiteMilliseconds;
	
	public BenchmarkSuite(String name) {
		suiteName = name;
		// XXX register ids
		idNames = new String[7];
		idNames[OPEN_REDABLE] = "OPEN_REDABLE";
		idNames[ADD_ABRAKADABRA] = "ADD_ABRAKADABRA";
		idNames[ADD_ACTIVE_LISTENING] = "ADD_ACTIVE_LISTENING";
		idNames[ADD_CELLS] = "ADD_CELLS";
		idNames[CLOSE] = "CLOSE";
		idNames[CREATE_DBU] = "CREATE_DBU";
		idNames[CLEAR] = "CLEAR";
		ids = null;
		milliseconds = null;
		actions = null;
		currentIndex = 0;
	}
	
	public void start() {
		ids = new int[MAX_ACTIONS];
		milliseconds = new long[MAX_ACTIONS];
		actions = new int[MAX_ACTIONS];
		for(int i = 0; i < MAX_ACTIONS; i++) {
			ids[i] = NO_ID;
			milliseconds[i] = TIME_INIT;
			actions[i] = NO_ACTION;
		}
		suiteMilliseconds = System.currentTimeMillis();
	}
	
	public void stop() {
		long endSuiteMs = System.currentTimeMillis();
		long suiteTime = endSuiteMs - suiteMilliseconds;
		long lostSuiteTime = suiteTime;
		
		int size = 0;
		while(ids[size] != NO_ID) {
			size++;
		}
		
		Log.d("PROFILING", "START SUITE " + suiteName);
		for(int i = 0; i < size; i++) {
			int id = ids[i];
			long ms = milliseconds[i];
			int action = actions[i];
			
			if(action == ACTION_START) {
				for(int j = i + 1; j < size; j++) {
					if(actions[j] == ACTION_STOP && ids[i] == id) {
						long endms = milliseconds[j];
						
						long time = endms - ms;
						lostSuiteTime -= time;
						String overlapping = "";
						if(i + 1 != j) {
							overlapping = " overlapping";
						}
						Log.d("PROFILING", idNames[id] + " " + time + "ms" + overlapping);
						j = MAX_ACTIONS;
					}
				}
			}
		}
		Log.d("PROFILING", "END SUITE " + suiteName + " (" + suiteTime + "ms, " + lostSuiteTime + "ms lost)");
	}
	
	public void startMeasure(int id) {
		ids[currentIndex] = id;
		actions[currentIndex] = ACTION_START;
		milliseconds[currentIndex] = System.currentTimeMillis();
		currentIndex++;
	}
	
	public void stopMeasure(int id) {
		milliseconds[currentIndex] = System.currentTimeMillis();
		ids[currentIndex] = id;
		actions[currentIndex] = ACTION_STOP;
		currentIndex++;
	}
}
