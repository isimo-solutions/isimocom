package com.isimo.debug;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;



@PropertySource("file:test.properties")
@ConfigurationProperties
@Configuration
public class IsimoDebugProperties {

	public Isimo isimo = new Isimo();
	
	
	public Isimo getIsimo() {
		return isimo;
	}

	public void setIsimo(Isimo isimo) {
		this.isimo = isimo;
	}	
	
	
	public static class Isimo {
		public boolean suspendonfailure = false;
		public boolean suspendonerror = false;
		
		public Debug debug = new Debug();
				
		public Debug getDebug() {
			return debug;
		}

		public void setDebug(Debug debug) {
			this.debug = debug;
		}





		public static class Request {
			public int port = 0;

			public int getPort() {
				return port;
			}

			public void setPort(int port) {
				this.port = port;
			}
			
			
		}

		public static class Event {
			public int port = 0;

			public int getPort() {
				return port;
			}

			public void setPort(int port) {
				this.port = port;
			}
			
			
		}	

		public boolean isSuspendonfailure() {
			return suspendonfailure;
		}
		public void setSuspendonfailure(boolean suspendonfailure) {
			this.suspendonfailure = suspendonfailure;
		}
		public boolean isSuspendonerror() {
			return suspendonerror;
		}
		public void setSuspendonerror(boolean suspendonerror) {
			this.suspendonerror = suspendonerror;
		}
		
		public static class Debug {
			public boolean mode = false;
			public Request request = new Request();
			public Event event = new Event();

			public Request getRequest() {
				return request;
			}

			public void setRequest(Request request) {
				this.request = request;
			}

			public Event getEvent() {
				return event;
			}

			public void setEvent(Event event) {
				this.event = event;
			}

			
			public boolean isMode() {
				return mode;
			}

			public void setMode(boolean mode) {
				this.mode = mode;
			}
			
		}
	}
}
