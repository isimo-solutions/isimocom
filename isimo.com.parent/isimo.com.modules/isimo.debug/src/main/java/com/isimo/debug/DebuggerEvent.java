package com.isimo.debug;

import com.isimo.core.event.Event;

public class DebuggerEvent extends Event {
	private Breakpoint breakpoint;

	public Breakpoint getBreakpoint() {
		return breakpoint;
	}

	public void setBreakpoint(Breakpoint breakpoint) {
		this.breakpoint = breakpoint;
	}
	
	
}
