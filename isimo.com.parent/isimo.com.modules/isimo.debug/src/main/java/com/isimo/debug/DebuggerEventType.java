package com.isimo.debug;

import com.isimo.core.event.EventType;

public class DebuggerEventType {
	public static EventType Problem = EventType.get("Problem");
	public static EventType SetBreakpoint = EventType.get("SetBreakpoint");
	public static EventType Suspend = EventType.get("Suspend");
	public static EventType Resume = EventType.get("Resume");
	public static EventType StepInto = EventType.get("StepInto");
	public static EventType Terminate = EventType.get("Terminate");
	public static EventType StepOver = EventType.get("StepOver");
	public static EventType StepReturn = EventType.get("StepReturn");
	
}
