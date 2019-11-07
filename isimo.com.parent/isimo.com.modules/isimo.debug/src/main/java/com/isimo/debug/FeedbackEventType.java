package com.isimo.debug;

import com.isimo.core.event.EventType;

public class FeedbackEventType {
	public static EventType Resumed = EventType.get("Resumed");
	public static EventType Problem = EventType.get("Problem");
	public static EventType BreakpointHit = EventType.get("BreakpointHit");
	public static EventType StepInto = EventType.get("StepInto");
	public static EventType StepReturn = EventType.get("StepReturn");
	public static EventType StepOver = EventType.get("StepOver");
	public static EventType Suspended = EventType.get("Suspended");
}
